/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

import com.codahale.metrics.*;
import parallelism.MessageContextPair;
import parallelism.late.CBASEScheduler;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.LongAdder;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * @author eduardo
 */
public abstract class COS {

    private static final class Stats {
        final Histogram size;
        final Histogram ready;

        Stats(MetricRegistry metrics) {
            size = metrics.histogram(name(COS.class, "size"));
            ready = metrics.histogram(name(COS.class, "ready"));
        }
    }

    private final Semaphore ready = new Semaphore(0);  // tells if there is ready to execute
    private final Semaphore space;                     // counting semaphore for size of graph
    private final int limit;
    private Stats stats;

    protected CBASEScheduler scheduler;

    public COS(int limit, CBASEScheduler scheduler, MetricRegistry metrics) {
        this.limit = limit;
        this.space = new Semaphore(limit);
        this.scheduler = scheduler;
        if (metrics != null) {
            this.stats = new Stats(metrics);
        }
    }

    protected boolean isDependent(MessageContextPair thisRequest, MessageContextPair otherRequest) {
        return this.scheduler.isDependent(thisRequest, otherRequest);
    }

    public void insert(Object request) throws InterruptedException {
        space.acquire();
        ready.release(COSInsert(request));
        if (stats != null) {
            stats.size.update(limit - space.availablePermits());
            stats.ready.update(ready.availablePermits());
        }
    }

    public void remove(Object requestNode) throws InterruptedException {
        int nReady = COSRemove(requestNode);
        space.release();
        ready.release(nReady);
    }

    public Object get() throws InterruptedException {
        this.ready.acquire();
        return COSGet();
    }

    protected abstract int COSInsert(Object request) throws InterruptedException;

    protected abstract Object COSGet() throws InterruptedException;

    protected abstract int COSRemove(Object request) throws InterruptedException;


}