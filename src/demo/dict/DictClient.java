package demo.dict;

import bftsmart.tom.ParallelServiceProxy;
import bftsmart.util.MultiOperationRequest;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import parallelism.ParallelMapping;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.codahale.metrics.MetricRegistry.name;

final class DictClient extends Thread {

    private static final Logger LOGGER = Logger.getLogger(DictClient.class.getName());

    private final ParallelServiceProxy proxy;
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
                       MetricRegistry metrics) {
        super("DictClient-" + clientID);
        this.proxy = new ParallelServiceProxy(clientID);
        this.opsPerRequest = opsPerRequest;
        this.maxKey = maxKey;
        this.keySparseness = keySparseness;
        this.conflictPercentage = conflictPercentage;
        this.requestTimer = metrics.timer(name(DictClient.class, "requests"));
        ;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (; ; ) {
            MultiOperationRequest request = newRequest(random);
            try (Timer.Context ignored = requestTimer.time()) {
                proxy.invokeParallel(request.serialize(), ParallelMapping.SYNC_ALL);
            }
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
        if (args.length != 7) {
            System.out.println(
                    "Usage: DictClient " +
                            "<process id> " +
                            "<threads> " +
                            "<ops per request> " +
                            "<max key> " +
                            "<max duration sec> " +
                            "<key sparseness> " +
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
                    Float.parseFloat(args[5]),
                    Float.parseFloat(args[6]),
                    createMetricsDirectory()
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

    private static File createMetricsDirectory() {
        File dir = new File("./metrics");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("Can not create ./metrics directory.");
                System.exit(1);
            }
        } else if (!dir.isDirectory()) {
            System.out.println("./metrics must be a directory");
            System.exit(1);
        }
        return dir;
    }

    private static void runWorkload(int processID,
                                    int nThreads,
                                    int opsPerRequest,
                                    int maxKey,
                                    int maxDurationSec,
                                    float keySparseness,
                                    float conflictPercentage,
                                    File metricsPath)
            throws InterruptedException {

        MetricRegistry metrics = new MetricRegistry();


        for (int i = 0; i < nThreads; i++) {
            new DictClient(
                    processID + i,
                    opsPerRequest,
                    maxKey,
                    keySparseness,
                    conflictPercentage,
                    metrics
            ).start();
        }

        LOGGER.info("All clients started... running workload...");
        startReporting(metrics, metricsPath);
        Thread.sleep(maxDurationSec * 1000);
        LOGGER.info("Workload completed. Shutting down...");
        System.exit(0);
    }

    private static void startReporting(MetricRegistry metrics, File path) {
        CsvReporter csvReporter =
                CsvReporter
                        .forRegistry(metrics)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .build(path);
        csvReporter.start(1, TimeUnit.SECONDS);

        ConsoleReporter consoleReporter =
                ConsoleReporter
                        .forRegistry(metrics)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .build();
        consoleReporter.start(10, TimeUnit.SECONDS);
    }

}
