/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;


/**
 *
 * @author alchieri
 */
public class ParallelMapping {

    public static int CONC_ALL = -1;
    public static int SYNC_ALL = -2;
    public static int CONFLICT_RECONFIGURATION = -3;

    private ClassToThreads[] classes;
    //private Map<Integer, CyclicBarrier> barriers = new HashMap<Integer, CyclicBarrier>();
    //private Map<Integer, Integer> executorThread = new HashMap<Integer, Integer>();

    private BlockingQueue[] queues;
    private CyclicBarrier reconfBarrier;

   // private CyclicBarrier reconfThreadBarrier;

    //private int numberOfthreadsAC = 0;

    //alex INICIO
    //private int maxNumberOfthreads = 0;
   // private int minNumberOfThreads = 0;
   // public static int THREADS_RECONFIGURATION = -4;
    //alex FIM

    
    
    public ParallelMapping(int numberOfThreads, ClassToThreads[] cToT) {
        //alex
        //this.maxNumberOfthreads = maxNumberOfThreads;
        //this.minNumberOfThreads = minNumberOfThreads;
        //this.numberOfthreadsAC = numberOfThreads;
        
        queues = new BlockingQueue[numberOfThreads];
        
        for (int i = 0; i < queues.length; i++) {
            //queues[i] = new LinkedBlockingQueue();
            queues[i] = new FIFOQueue();
        } 
        this.classes = cToT;
        
        for(int i = 0; i < this.classes.length;i++){
            BlockingQueue[] q = new BlockingQueue[this.classes[i].tIds.length];
            for(int j = 0; j < q.length; j++){
                q[j] = queues[this.classes[i].tIds[j]];
            }
            this.classes[i].setQueues(q);            
        }
        

        //this.barriers.put(CONFLICT_ALL, new CyclicBarrier(getNumThreadsAC()));

        //this.executorThread.put(CONFLICT_ALL, 0);
        reconfBarrier = new CyclicBarrier(numberOfThreads + 1);
        //reconfThreadBarrier = new CyclicBarrier(maxNumberOfthreads);
    }
    
    public int getNumWorkers(){
        return this.queues.length;
    }
    
    public ClassToThreads getClass(int id){
        for(int i = 0; i < this.classes.length; i++){
            if(this.classes[i].classId == id){
                return this.classes[i];
            }
        }
        return null;
    }

    public CyclicBarrier getBarrier(int classId) {
        //return barriers.get(groupID);
        return this.getClass(classId).barrier;
    }

    public int getExecutorThread(int classId) {
        //return executorThread.get(groupId);
        return this.getClass(classId).tIds[0];
    }

    public CyclicBarrier getReconfBarrier() {
        return reconfBarrier;
    }
    
    public BlockingQueue[] getAllQueues() {
        return queues;
    }

//Alex inicio 
   /* public CyclicBarrier getReconfThreadBarrier() {
        return reconfThreadBarrier;
    }*/

   /* public int checkNumReconfigurationThreads(int nt) {
        //menor ou igual a minimo retorna minimo
        int numTResult = this.numberOfthreadsAC + nt;
        if (numTResult >= this.minNumberOfThreads && numTResult <= this.maxNumberOfthreads){
            return nt;
        }else{
            if (nt < 0) {
                while (numTResult < this.minNumberOfThreads) {
                    nt++;
                    numTResult = this.numberOfthreadsAC + nt;
                }
            } else if (nt > 0) {
                while (numTResult > this.maxNumberOfthreads) {
                    nt--;
                    numTResult = this.numberOfthreadsAC + nt;
                }
            }
            return nt;
        }
    }*/

    /*public void setNumThreadsAC(int x) {
        this.numberOfthreadsAC = x;
    }

    public int getNumMaxOfThreads() {
        return this.maxNumberOfthreads;
    }

    public int getNumMinOfThreads() {
        return this.minNumberOfThreads;
    }*/

    /*public BlockingQueue[] getQueuesActive() {
        BlockingQueue[] qAtivas = new BlockingQueue[getNumThreadsAC()];
        for (int i = 0; i < qAtivas.length; i++) {
            qAtivas[i] = queues[i];
        }
        return qAtivas;
    }*/

    //Alex fim
   /* public void reconfigureBarrier(int nextN) {
        //TODO: implement!!!
       // this.barriers.remove(CONFLICT_ALL);
       // this.barriers.put(CONFLICT_ALL, new CyclicBarrier(nextN));
        reconfBarrier = new CyclicBarrier(nextN + 1);
    }*/

  /*  public boolean addMultiGroup(int groupId, int[] groupsId) {
       if (groupId >= getNumThreadsAC()) {

            BlockingQueue[] q = new BlockingQueue[groupsId.length];
            for (int i = 0; i < q.length; i++) {
                q[i] = queues[groupsId[i]];
                //System.out.println("GID: " + groupId + " m:" + groupsId[i]);
            }
            groups.put(groupId, q);
            this.barriers.put(groupId, new CyclicBarrier(groupsId.length));
            this.executorThread.put(groupId, groupsId[0]);
            return true;
        }
       
       
        return false;
    }*/

    /*public BlockingQueue[] getMultiGroup(int groupId) {
        return groups.get(groupId).queues;
    }*/

    /*public BlockingQueue[] getAllQueues() {
        return queues;
    }*/

  /*  public int getNumThreadsAC() {
        return this.numberOfthreadsAC;
    }*/

   /* public BlockingQueue getThreadQueue(int threadID) {
        return queues[threadID];
    }*/

   /* public BlockingQueue[] getQueues(int groupID) {
        if (groupID == CONFLICT_NONE) {
            BlockingQueue[] r = {queues[0]};
            return r;
        } else if (groupID == CONFLICT_ALL) {
            return queues;
        } else if (groupID < getNumThreadsAC()) {
            BlockingQueue[] r = {queues[groupID]};
            return r;
        } else {
            return getMultiGroup(groupID);
        }
    }*/

}
