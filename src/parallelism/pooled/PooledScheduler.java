package parallelism.pooled;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;
import parallelism.late.ConflictDefinition;
import parallelism.scheduler.Scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.codahale.metrics.MetricRegistry.name;

final class PooledScheduler implements Scheduler {

    private static final int MAX_SIZE = 150;

    private static final class Task {
        private final MessageContextPair request;
        private final CompletableFuture<Void> future;

        Task(MessageContextPair request) {
            this.request = request;
            this.future = new CompletableFuture<>();
        }
    }

    private static class Stats {
        final Counter size;
        final Counter ready;

        Stats(MetricRegistry metrics) {
            size = metrics.counter(name(PooledScheduler.class, "size"));
            ready = metrics.counter(name(PooledScheduler.class, "ready"));
        }
    }

    private final int nThreads;
    private final ConflictDefinition conflict;
    private final ExecutorService pool;
    private final Semaphore space;
    private final List<Task> scheduled;
    private final Stats stats;

    private Consumer<MessageContextPair> executor;

    PooledScheduler(int nThreads,
                    ConflictDefinition conflict,
                    MetricRegistry metrics) {
        this.nThreads = nThreads;
        this.conflict = conflict;
        this.space = new Semaphore(MAX_SIZE);
        this.scheduled = new LinkedList<>();
        this.stats = new Stats(metrics);
        this.pool = new ForkJoinPool(
                nThreads, ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null, true, nThreads, nThreads, 0, null, 60, TimeUnit.SECONDS);
    }

    // Breaks cyclic dependency with PooledServiceReplica
    void setExecutor(Consumer<MessageContextPair> executor) {
        this.executor = executor;
    }

    @Override
    public int getNumWorkers() {
        return nThreads;
    }

    @Override
    public void schedule(MessageContextPair request) {
        try {
            space.acquire();
            stats.size.inc();
            doSchedule(request);
        } catch (InterruptedException e) {
            // Ignored.
        }
    }

    private void doSchedule(MessageContextPair request) {
        Task newTask = new Task(request);
        submit(newTask, addTask(newTask));
    }

    private List<CompletableFuture<Void>> addTask(Task newTask) {
        List<CompletableFuture<Void>> dependencies = new LinkedList<>();
        ListIterator<Task> iterator = scheduled.listIterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.future.isDone()) {
                iterator.remove();
                continue;
            }
            if (conflict.isDependent(task.request, newTask.request)) {
                dependencies.add(task.future);
            }
        }

        scheduled.add(newTask);
        return dependencies;
    }

    private void submit(Task newTask, List<CompletableFuture<Void>> dependencies) {
        if (dependencies.isEmpty()) {
            stats.ready.inc();
            pool.execute(() -> execute(newTask));
        } else {
            after(dependencies).thenRun(() -> {
                stats.ready.inc();
                execute(newTask);
            });
        }
    }

    private static CompletableFuture<Void> after(List<CompletableFuture<Void>> fs) {
        if (fs.size() == 1) return fs.get(0); // fast path
        return CompletableFuture.allOf(fs.toArray(new CompletableFuture[0]));
    }

    private void execute(Task task) {
        executor.accept(task.request);
        space.release();
        stats.ready.dec();
        stats.size.dec();
        task.future.complete(null);
    }

    @Override
    public ParallelMapping getMapping() {
        return null;
    }

    @Override
    public void scheduleReplicaReconfiguration() {
    }
}
