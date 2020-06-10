package parallelism.pooled;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.server.Executable;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import parallelism.MessageContextPair;
import parallelism.MultiOperationCtx;
import parallelism.ParallelServiceReplica;
import parallelism.late.ConflictDefinition;

import static com.codahale.metrics.MetricRegistry.name;

public final class PooledServiceReplica extends ParallelServiceReplica {

    private static final class Stats {
        final Meter commands;
        final Meter requests;

        Stats(MetricRegistry metrics) {
            commands = metrics.meter(name(PooledScheduler.class, "commands"));
            requests = metrics.meter(name(PooledScheduler.class, "requests"));
        }

    }

    private final Stats stats;

    public PooledServiceReplica(int processID,
                                int nThreads,
                                Executable executor,
                                Recoverable recover,
                                ConflictDefinition cf,
                                MetricRegistry metrics) {
        super(processID, executor, recover, new PooledScheduler(nThreads, cf, metrics));
        stats = new Stats(metrics);
    }

    @Override
    protected void initWorkers(int nThreads, int processID) {
        PooledScheduler scheduler = (PooledScheduler) this.scheduler;
        scheduler.setExecutor(this::execute);
    }

    private void execute(MessageContextPair msg) {
        msg.resp = ((SingleExecutable) executor).executeOrdered(msg.operation, null);
        stats.commands.mark();

        MultiOperationCtx ctx = ctxs.get(msg.request.toString());
        ctx.add(msg.index, msg.resp);

        if (ctx.response.isComplete()
                && !ctx.finished
                && ctx.interger.getAndIncrement() == 0) {
            ctx.finished = true;
            ctx.request.reply = new TOMMessage(
                    id,
                    ctx.request.getSession(),
                    ctx.request.getSequence(),
                    ctx.response.serialize(),
                    SVController.getCurrentViewId()
            );
            replier.manageReply(ctx.request, null);
            stats.requests.mark();
        }
    }
}
