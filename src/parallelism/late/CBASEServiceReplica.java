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
import bftsmart.util.ThroughputStatistics;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.late.graph.Request;
import parallelism.late.graph.concurrentGraph.AllInOneGraph;
import parallelism.MessageContextPair;
import parallelism.MultiOperationCtx;
import parallelism.ParallelMapping;
import parallelism.ParallelServiceReplica;

/**
 *
 * @author eduardo
 */
public class CBASEServiceReplica extends ParallelServiceReplica {

    private CyclicBarrier recBarrier = new CyclicBarrier(2);

    public CBASEServiceReplica(int id, Executable executor, Recoverable recoverer, int numWorkers, ConflictDefinition cf, String graphType) {
        super(id, executor, recoverer, new CBASEScheduler(cf, numWorkers, graphType));

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

        statistics = new ThroughputStatistics(id, n, "results_" + id + ".txt", "");

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

        public void run() {
            //System.out.println("rum: " + thread_id);
            Request msg = null;
            while (true) {
                Object node = s.nextRequest();
                
                msg = ((AllInOneGraph.vNode) node).getAsRequest();
                if (msg.getRequest().classId == ParallelMapping.CONFLICT_RECONFIGURATION) {
                    try {
                        getReconfBarrier().await();
                        getReconfBarrier().await();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    msg.getRequest().resp = ((SingleExecutable) executor).executeOrdered(msg.getRequest().operation, null);
                    MultiOperationCtx ctx = ctxs.get(msg.getRequest().request.toString());
                    ctx.add(msg.getRequest().index, msg.getRequest().resp);
                    if (ctx.response.isComplete() && !ctx.finished && (ctx.interger.getAndIncrement() == 0)) {
                        ctx.finished = true;
                        ctx.request.reply = new TOMMessage(id, ctx.request.getSession(),
                                ctx.request.getSequence(), ctx.response.serialize(), SVController.getCurrentViewId());
                        //bftsmart.tom.util.Logger.println("(ParallelServiceReplica.receiveMessages) sending reply to "
                        //      + msg.message.getSender());
                        replier.manageReply(ctx.request, null);
                    }
                    statistics.computeStatistics(thread_id, 1);
                }
                
                //s.removeRequest(msg);
                s.removeRequest(node);
            }
        }

    }
}
