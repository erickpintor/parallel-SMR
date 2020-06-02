/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import parallelism.MessageContextPair;
import parallelism.late.CBASEScheduler;

import java.util.concurrent.Semaphore;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * @author eduardo
 */
public abstract class COS {

    private static final class Stats {
        final Counter size;
        final Counter free;

        Stats(MetricRegistry metrics) {
            size = metrics.counter(name(COS.class, "size"));
            free = metrics.counter(name(COS.class, "free"));
            metrics.register(name(COS.class, "conflict"), new RatioGauge() {
                @Override
                protected Ratio getRatio() {
                    return Ratio.of(free.getCount(), size.getCount());
                }
                @Override
                public Double getValue() {
                    return 1 - super.getValue();
                }
            });
        }
    }

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
        int free = COSInsert(request);
        ready.release(free);
        if (stats != null) {
            stats.size.inc();
            stats.free.inc(free);
        }
    }

    public void remove(Object requestNode) throws InterruptedException {
        int free = COSRemove(requestNode);
        space.release();
        ready.release(free);
        if (stats != null) {
            stats.size.dec();
            stats.free.dec(1 - free);
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