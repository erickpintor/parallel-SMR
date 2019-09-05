/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph.syncGraph;


import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import parallelism.late.graph.ConflictGraph;
import parallelism.late.graph.ExecState;
import parallelism.late.graph.Parallelizer;
import parallelism.late.graph.Request;

/**
 * Created by ruda on 01/09/16.
 */

public class SyncGraph implements ConflictGraph{

    private ArrayList<Request> pendingRequests;
    private int limit;
    final private Parallelizer parallelizer;
    final private Lock mutex = new ReentrantLock();
    final private Condition notFull = mutex.newCondition();
    final private Condition hasReady = mutex.newCondition();

    public SyncGraph(int max_elements, Parallelizer parallelizer) {
        limit = max_elements;
        pendingRequests = new ArrayList<Request>(limit);
        this.parallelizer = parallelizer;
    }

    public void insert(Request request) throws InterruptedException {
       /* mutex.lock();
        try {
            while (pendingRequests.size() == limit) {
                notFull.await();
            }
            for (Request cur : pendingRequests) {
                if (parallelizer.isDependent(request, cur)) {
                    request.addDependency(cur);
                    request.setState(ExecState.blocked);
                    cur.addDependent(request);
                }
            }
            pendingRequests.add(request);
            //System.out.println("Tamanho do grafo: "+pendingRequests.size());
            hasReady.signal();
        } finally {
            mutex.unlock();
        }*/
    }

    public void remove(Request request) throws InterruptedException {
        /*mutex.lock();
        try {
            
            if (request.countDependents() != 0) {
                for (Request cur : request.getDependents()) {
                    if (cur == null) {
                        continue;
                    }
                    cur.removeDependency(request);
                    if (cur.countDependencies() == 0) {
                        assert cur.getState() == ExecState.blocked : "Request is not in blocked state";
                        cur.setState(ExecState.ready);
                        hasReady.signal();
                    }
                }
            }
            pendingRequests.remove(request);
            notFull.signal();
        } finally {
            mutex.unlock();
        }*/
    }

    public void clear() {
       /* mutex.lock();
        pendingRequests.clear();
        mutex.unlock();*/
    }

    public Request nextRequest() throws InterruptedException {
       /* mutex.lock();
        try {
            while (true) {
                for (Request cur : pendingRequests) {
                    if (cur.getState() == ExecState.ready) {
                        cur.setState(ExecState.running);
                        return cur;
                    }
                }
                hasReady.await();
            }
        } finally {
            mutex.unlock();
        }*/
       return null;
    }

    public int countRequests() {
        return pendingRequests.size();
    }
}
