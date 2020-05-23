/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.server.Executable;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;
import java.util.concurrent.CyclicBarrier;

import bftsmart.util.ThroughputStatistics;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import parallelism.MessageContextPair;
import parallelism.MultiOperationCtx;
import parallelism.ParallelMapping;
import parallelism.ParallelServiceReplica;
import parallelism.late.graph.DependencyGraph;

/**
 *
 * @author eduardo
 */
public class CBASEServiceReplica extends ParallelServiceReplica {

    private final CyclicBarrier recBarrier = new CyclicBarrier(2);
    private Meter rps;

    public CBASEServiceReplica(int id,
                               Executable executor,
                               Recoverable recoverer,
                               int numWorkers,
                               ConflictDefinition cf,
                               COSType graphType) {
        this(id, executor, recoverer, numWorkers, cf, graphType, null);
    }

    public CBASEServiceReplica(int id,
                               Executable executor,
                               Recoverable recoverer,
                               int numWorkers,
                               ConflictDefinition cf,
                               COSType graphType,
                               MetricRegistry metrics) {
        super(id, executor, recoverer, new CBASEScheduler(cf, numWorkers, graphType));
        if (metrics != null) {
            this.rps = metrics.meter("requests");
        }
    }

    public CyclicBarrier getReconfBarrier() {
        return recBarrier;
    }

    @Override
    public int getNumActiveThreads() {
        return this.scheduler.getNumWorkers();
    }

    @Override
    protected void initWorkers(int n, int id) {
        int tid = 0;
        for (int i = 0; i < n; i++) {
            new CBASEServiceReplicaWorker((CBASEScheduler) this.scheduler, tid).start();
            tid++;
        }
    }

    private class CBASEServiceReplicaWorker extends Thread {

        private CBASEScheduler s;
        private int thread_id;

        public CBASEServiceReplicaWorker(CBASEScheduler s, int id) {
            this.thread_id = id;
            this.s = s;

            //System.out.println("Criou um thread: " + id);
        }
        //int exec = 0;

        public void run() {
            //System.out.println("rum: " + thread_id);
            MessageContextPair msg = null;
            while (true) {
                // System.out.println("vai pegar...");
                Object node = s.get();
                //System.out.println("Pegou req...");
                
                msg = ((DependencyGraph.vNode) node).getAsRequest();
                //System.out.println("Pegou req..."+ msg.toString());
                if (msg.classId == ParallelMapping.CONFLICT_RECONFIGURATION) {
                    try {
                        getReconfBarrier().await();
                        getReconfBarrier().await();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    msg.resp = ((SingleExecutable) executor).executeOrdered(msg.operation, null);
                    //exec++;
                    
                    //System.out.println(thread_id+" Executadas: "+exec);
                    
                    MultiOperationCtx ctx = ctxs.get(msg.request.toString());
                    ctx.add(msg.index, msg.resp);
                    if (ctx.response.isComplete() && !ctx.finished && (ctx.interger.getAndIncrement() == 0)) {
                        ctx.finished = true;
                        ctx.request.reply = new TOMMessage(id, ctx.request.getSession(),
                                ctx.request.getSequence(), ctx.response.serialize(), SVController.getCurrentViewId());
                        //bftsmart.tom.util.Logger.println("(ParallelServiceReplica.receiveMessages) sending reply to "
                        //      + msg.message.getSender());
                        replier.manageReply(ctx.request, null);
                    }
                    if (rps != null) {
                        rps.mark();
                    }
                }
                
                //s.removeRequest(msg);
                s.remove(node);
            }
        }

    }
}
