package demo.dict;

import com.codahale.metrics.MetricRegistry;

import java.util.function.Supplier;

class SyntacticDelay {

    private static final int BURN_CYCLES = 1_000;
    private final int minCostPerOperation;
    // NB. Volatile updates to a public var prevents the JVM
    // from eliding the code used to produce busy wait.
    public volatile int dummyVar;

    SyntacticDelay(int minCostPerOperation) {
        this.minCostPerOperation = minCostPerOperation;
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    <T> T ensureMinCost(Supplier<T> fn) {
        long start = System.currentTimeMillis();
        T result = fn.get();
        // Burn CPU to reduce contention on system calls.
        do for (int i = 0; i < BURN_CYCLES; i++) dummyVar++;
        while (System.currentTimeMillis() - start < minCostPerOperation);
        return result;
    }
}
