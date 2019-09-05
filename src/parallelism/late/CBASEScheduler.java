/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import parallelism.late.graph.Parallelizer;
import parallelism.late.graph.Request;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;
import parallelism.scheduler.Scheduler;

/**
 *
 * @author eduardo
 */
public class CBASEScheduler implements Scheduler{

    private Parallelizer parallelizer;
    
    private int numWorkers;
    
    //private ThroughputStatistics statistics;
    
    public CBASEScheduler(int numWorkers, String graphType) {
        this(null, numWorkers, graphType);
    }

    public CBASEScheduler(ConflictDefinition cd, int numWorkers, String graphType) {
        
        parallelizer = new Parallelizer(150,graphType, cd);
        this.numWorkers = numWorkers;
        
        // statistics = new ThroughputStatistics(1, "results_scheduler_"+id+".txt","SCHEDULER");
    }

    
    public int getNumWorkers() {
        return this.numWorkers;
    }
    
    
    @Override
    public void schedule(MessageContextPair request) {
        
        //statistics.start();
        //statistics.computeStatistics(0, 1);
        
        try {
            parallelizer.insert(request);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
    }

    public Object nextRequest(){
        
        try {
            return parallelizer.nextRequest();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void removeRequest(Object requestRequest){
        try {
            parallelizer.remove(requestRequest);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void scheduleReplicaReconfiguration() {
        
        TOMMessage reconf = new TOMMessage(0, 0, 0,0, null, 0, TOMMessageType.ORDERED_REQUEST, ParallelMapping.CONFLICT_RECONFIGURATION);
        MessageContextPair m = new MessageContextPair(reconf, ParallelMapping.CONFLICT_RECONFIGURATION, -1, null);
        
        schedule(m);
        
        
    }

    @Override
    public ParallelMapping getMapping() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
