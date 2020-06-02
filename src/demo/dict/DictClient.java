package demo.dict;

import bftsmart.tom.ParallelServiceProxy;
import bftsmart.util.MultiOperationRequest;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import parallelism.ParallelMapping;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.codahale.metrics.MetricRegistry.name;

final class DictClient extends Thread {

    private static final Logger LOGGER = Logger.getLogger(DictClient.class.getName());

    private final ParallelServiceProxy proxy;
    private final AtomicInteger nRequests;
    private final CountDownLatch completed;
    private final Timer requestTimer;
    private final int opsPerRequest;
    private final int maxKey;
    private final float keySparseness;
    private final float conflictPercentage;

    private DictClient(int clientID,
                       int opsPerRequest,
                       int maxKey,
                       float keySparseness,
                       float conflictPercentage,
                       AtomicInteger nRequests,
                       CountDownLatch completed,
                       Timer requestTimer) {
        super("DictClient-" + clientID);
        this.proxy = new ParallelServiceProxy(clientID);
        this.opsPerRequest = opsPerRequest;
        this.maxKey = maxKey;
        this.keySparseness = keySparseness;
        this.conflictPercentage = conflictPercentage;
        this.nRequests = nRequests;
        this.completed = completed;
        this.requestTimer = requestTimer;
    }

    @Override
    public void run() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (nRequests.decrementAndGet() >= 0) {
            sendRequest(random);
        }
        completed.countDown();
    }

    private void sendRequest(Random random) {
        MultiOperationRequest request = newRequest(random);
        try (Timer.Context ignored = requestTimer.time()) {
            proxy.invokeParallel(request.serialize(), ParallelMapping.SYNC_ALL);
        }
    }

    private MultiOperationRequest newRequest(Random random) {
        MultiOperationRequest request = new MultiOperationRequest(opsPerRequest);
        for (int i = 0; i < opsPerRequest; i++) {
            Command cmd = Command.random(
                    random,
                    maxKey,
                    keySparseness,
                    conflictPercentage
            );
            request.add(i, cmd.encode(), ParallelMapping.CONC_ALL);
        }
        return request;
    }

    public static void main(String[] args) {
        if (args.length != 8) {
            System.out.println(
                    "Usage: DictClient " +
                            "<process id> " +
                            "<threads> " +
                            "<ops per request> " +
                            "<requests> " +
                            "<max key> " +
                            "<max duration sec> " +
                            "<key sparseness>" +
                            "<conflict percentage>"
            );
            System.exit(1);
        }

        try {
            runWorkload(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]),
                    Integer.parseInt(args[5]),
                    Float.parseFloat(args[6]),
                    Float.parseFloat(args[7])
            );
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid arguments", e);
            System.exit(1);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Experiment interrupted", e);
            System.exit(1);
        }

        LOGGER.info("Experiment completed.");
        System.exit(0);
    }

    private static void runWorkload(int processID,
                                    int nThreads,
                                    int opsPerRequest,
                                    int nRequests,
                                    int maxKey,
                                    int maxDurationSec,
                                    float keySparseness,
                                    float conflictPercentage)
            throws InterruptedException {

        MetricRegistry metrics = new MetricRegistry();
        CountDownLatch completed = new CountDownLatch(nThreads);
        startClients(
                processID,
                nThreads,
                opsPerRequest,
                nRequests,
                maxKey,
                keySparseness,
                conflictPercentage,
                completed,
                metrics
        );
        LOGGER.info("All clients started... running workload...");
        startReporting(metrics);
        completed.await(maxDurationSec, TimeUnit.SECONDS);
    }

    private static void startReporting(MetricRegistry metrics) {
        ConsoleReporter consoleReporter =
                ConsoleReporter
                        .forRegistry(metrics)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .build();
        consoleReporter.start(10, TimeUnit.SECONDS);
    }

    private static void startClients(int processID,
                                     int nThreads,
                                     int opsPerRequest,
                                     int nRequests,
                                     int maxKey,
                                     float conflictSD,
                                     float conflictPercentage,
                                     CountDownLatch completed,
                                     MetricRegistry metrics) {

        AtomicInteger requests = new AtomicInteger(nRequests);
        Timer requestTimer = metrics.timer(name(DictClient.class, "requests"));

        for (int i = 0; i < nThreads; i++) {
            new DictClient(
                    processID + i,
                    opsPerRequest,
                    maxKey,
                    conflictSD,
                    conflictPercentage,
                    requests,
                    completed,
                    requestTimer
            ).start();
        }
    }
}
