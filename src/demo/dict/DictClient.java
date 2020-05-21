package demo.dict;

import bftsmart.tom.ParallelServiceProxy;
import bftsmart.util.MultiOperationRequest;
import parallelism.ParallelMapping;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class DictClient extends Thread {

    private static final Logger LOGGER = Logger.getLogger(DictClient.class.getName());

    private final ParallelServiceProxy proxy;
    private final CountDownLatch completed;
    private final int opsPerRequest;
    private final int nRequests;
    private final int maxKey;

    private DictClient(int clientID,
                       int opsPerRequest,
                       int nRequests,
                       int maxKey,
                       CountDownLatch completed) {
        super("DictClient-" + clientID);
        this.proxy = new ParallelServiceProxy(clientID);
        this.opsPerRequest = opsPerRequest;
        this.nRequests = nRequests;
        this.maxKey = maxKey;
        this.completed = completed;
    }

    @Override
    public void run() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < nRequests; i++) {
            sendRequest(random);
        }
        completed.countDown();
    }

    private void sendRequest(ThreadLocalRandom random) {
        MultiOperationRequest request = newRequest(random);
        proxy.invokeParallel(request.serialize(), ParallelMapping.SYNC_ALL);
    }

    private MultiOperationRequest newRequest(ThreadLocalRandom random) {
        MultiOperationRequest request = new MultiOperationRequest(opsPerRequest);
        for (int i = 0; i < opsPerRequest; i++) {
            Command cmd = Command.random(random, maxKey);
            request.add(i, cmd.encode(), ParallelMapping.CONC_ALL);
        }
        return request;
    }

    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println(
                    "Usage: DictClient " +
                            "<process id> " +
                            "<threads> " +
                            "<ops per request> " +
                            "<requests> " +
                            "<max key>" +
                            "<max duration sec>"
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
                    Integer.parseInt(args[5])
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
                                    int maxDurationSec) throws InterruptedException {

        CountDownLatch completed = new CountDownLatch(nThreads);
        startClients(
                processID,
                nThreads,
                opsPerRequest,
                nRequests,
                maxKey,
                completed
        );
        LOGGER.info("All clients started... running workload...");
        completed.await(maxDurationSec, TimeUnit.SECONDS);
    }

    private static void startClients(int processID,
                                     int nThreads,
                                     int opsPerRequest,
                                     int nRequests,
                                     int maxKey,
                                     CountDownLatch completed) {

        int requestsPerThread = nRequests / nThreads;
        int lastThreadRequests = requestsPerThread + (nRequests % nThreads);

        for (int i = 0; i < nThreads - 1; i++) {
            new DictClient(
                    processID + i,
                    opsPerRequest,
                    requestsPerThread,
                    maxKey,
                    completed
            ).start();
        }

        new DictClient(
                processID + nThreads - 1,
                opsPerRequest,
                lastThreadRequests,
                maxKey,
                completed
        ).start();
    }
}
