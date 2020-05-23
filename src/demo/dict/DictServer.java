package demo.dict;

import bftsmart.tom.MessageContext;
import bftsmart.tom.server.SingleExecutable;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import parallelism.MessageContextPair;
import parallelism.late.CBASEServiceReplica;
import parallelism.late.COSType;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class DictServer implements SingleExecutable {

    private static final Logger LOGGER = Logger.getLogger(DictServer.class.getName());

    private final Map<Integer, Integer> dict;

    private DictServer(int processID, int nThreads, int nKeys, File metricsPath) {
        dict = new HashMap<>(nKeys);
        for (int i = 0; i < nKeys; i++)
            dict.put(i, 0);

        MetricRegistry metrics = new MetricRegistry();
        startCBASEService(processID, nThreads, metrics);
        startMetricsReporter(metrics, metricsPath);
    }

    private void startCBASEService(int processID,
                                   int nThreads,
                                   MetricRegistry metrics) {
        new CBASEServiceReplica(
                processID,
                this,
                null,
                nThreads,
                DictServer::isConflicting,
                COSType.lockFreeGraph,
                metrics
        );
    }

    private static void startMetricsReporter(MetricRegistry metrics, File path) {
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

    private static boolean isConflicting(MessageContextPair a,
                                         MessageContextPair b) {
        Command cmdA = Command.wrap(a.operation);
        Command cmdB = Command.wrap(b.operation);
        return cmdA.conflictWith(cmdB);
    }

    @Override
    public byte[] executeOrdered(byte[] bytes, MessageContext ctx) {
        return execute(bytes);
    }

    @Override
    public byte[] executeUnordered(byte[] bytes, MessageContext ctx) {
        return execute(bytes);
    }

    private byte[] execute(byte[] bytes) {
        Command cmd = Command.wrap(bytes);
        ByteBuffer resp = ByteBuffer.allocate(4);
        resp.putInt(cmd.execute(dict));
        return resp.array();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: DictServer <processID> <threads> <keys>");
            System.exit(1);
        }

        try {
            int processID = Integer.parseInt(args[0]);
            int nThreads = Integer.parseInt(args[1]);
            int nKeys = Integer.parseInt(args[2]);
            File metricsPath = createMetricsDirectory();
            new DictServer(processID, nThreads, nKeys, metricsPath);
            LOGGER.info("Server initialization completed.");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid arguments.", e);
            System.exit(1);
        }
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
}
