/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.scheduler;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import java.util.concurrent.BlockingQueue;

import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.ClassToThreads;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;

/**
 *
 * @author eduardo
 */
public class DefaultScheduler implements Scheduler {

    protected ParallelMapping mapping;
        
    public DefaultScheduler(int numberWorkers, ClassToThreads[] cToT) {
        this.mapping = new ParallelMapping(numberWorkers, cToT);
    }

    @Override
    public int getNumWorkers() {
        return this.mapping.getNumWorkers();
    }

    @Override
    public ParallelMapping getMapping() {
        return mapping;
    }

    @Override
    public void scheduleReplicaReconfiguration() {
        TOMMessage reconf = new TOMMessage(0, 0, 0, 0, null, 0, TOMMessageType.ORDERED_REQUEST, ParallelMapping.CONFLICT_RECONFIGURATION);
        MessageContextPair m = new MessageContextPair(reconf, ParallelMapping.CONFLICT_RECONFIGURATION,-1,null);
        BlockingQueue[] q = this.getMapping().getAllQueues();
        try {
            for (BlockingQueue q1 : q) {
                q1.put(m);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void schedule(MessageContextPair request) {
        try {
            
            ClassToThreads ct = this.mapping.getClass(request.classId);
            if(ct == null){
                //TRATAR COMO CONFLICT ALL
                //criar uma classe que sincroniza tudo
                System.err.println("CLASStoTHREADs MAPPING NOT FOUND");
            }
            
            if(ct.type == ClassToThreads.CONC){//conc
                ct.queues[ct.executorIndex].put(request);
                ct.executorIndex = (ct.executorIndex+1)% ct.queues.length;
                
            }else{ //sync
                for (BlockingQueue q : ct.queues) {
                    q.put(request);
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Logger.getLogger(DefaultScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
