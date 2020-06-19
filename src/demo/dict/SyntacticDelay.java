package demo.dict;

import java.util.function.Supplier;

class SyntacticDelay {

    private static final int BURN_CYCLES = 1000;
    private final int minCostNS;
    // NB. Volatile updates to a public var prevents the JVM
    // from eliding the code used to produce busy wait.
    public volatile int dummyVar;

    SyntacticDelay(int minCostNS) {
        this.minCostNS = minCostNS;
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    <T> T ensureMinCost(Supplier<T> fn) {
        long start = System.nanoTime();
        T result = fn.get();
        // Burn CPU to reduce contention on system calls.
        while (System.nanoTime() < start + minCostNS)
            for (int i = 0; i < BURN_CYCLES; i++)
                dummyVar++;
        return result;
    }
}
