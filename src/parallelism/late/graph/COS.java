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

    private final class Stats {
        private final Histogram size;
        private final Histogram free;

        Stats(MetricRegistry metrics) {
            size = metrics.histogram(name(COS.class, "size"));
            free = metrics.histogram(name(COS.class, "free"));
            metrics.register(name(COS.class, "conflict"), new RatioGauge() {
                @Override
                protected Ratio getRatio() {
                    return Ratio.of(
                            free.getSnapshot().getMean(),
                            size.getSnapshot().getMean());
                }
                @Override
                public Double getValue() {
                    return Math.max(1 - super.getValue(), 0);
                }
            });
        }

        void update() {
            size.update(COS.this.size.intValue());
            free.update(COS.this.free.intValue());
        }
    }

    private final LongAdder size = new LongAdder();
    private final LongAdder free = new LongAdder();
    private final Semaphore ready = new Semaphore(0);  // tells if there is ready to execute
    private final Semaphore space;                     // counting semaphore for size of graph
    private Stats stats;

    protected CBASEScheduler scheduler;

    public COS(int limit, CBASEScheduler scheduler, MetricRegistry metrics) {
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
        int nReady = COSInsert(request);
        ready.release(nReady);
        if (stats != null) {
            size.increment();
            free.add(nReady);
            stats.update();
        }
    }

    public void remove(Object requestNode) throws InterruptedException {
        int nReady = COSRemove(requestNode);
        space.release();
        ready.release(nReady);
        if (stats != null) {
            free.add(-1 + nReady);
            size.decrement();
            stats.update();
        }
    }

    public Object get() throws InterruptedException {
        this.ready.acquire();
        return COSGet();
    }

    protected abstract int COSInsert(Object request) throws InterruptedException;

    protected abstract Object COSGet() throws InterruptedException;

    protected abstract int COSRemove(Object request) throws InterruptedException;


}