/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

import java.util.concurrent.Semaphore;
import parallelism.late.ConflictDefinition;
import parallelism.late.DefaultConflictDefinition;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;
import parallelism.late.CBASEScheduler;

/**
 *
 * @author eduardo
 */
public abstract class COS{

   
 

    private Semaphore space = null;                // counting semaphore for size of graph
    private Semaphore ready = new Semaphore(0);  // tells if there is ready to execute
    
    protected CBASEScheduler scheduler;
    
    
    public COS(int limit, CBASEScheduler scheduler) {
        this.space = new Semaphore(limit);
        this.scheduler = scheduler;
    } 
     
     
    
    protected boolean isDependent(MessageContextPair thisRequest, MessageContextPair otherRequest){
        return this.scheduler.isDependent(thisRequest, otherRequest);
    }
    
    public void insert(Object request) throws InterruptedException {
        space.acquire();
        int readyNum = COSInsert(request);
        this.ready.release(readyNum);
    }
    
    public void remove(Object requestNode) throws InterruptedException {
        int readyNum = COSRemove(requestNode);
        this.space.release();
        this.ready.release(readyNum);
    }

    public Object get() throws InterruptedException {
        this.ready.acquire();
        return COSGet();
    }
    
    protected abstract int COSInsert(Object request) throws InterruptedException;

    protected abstract Object COSGet() throws InterruptedException;

    protected abstract int COSRemove(Object request) throws InterruptedException;


}