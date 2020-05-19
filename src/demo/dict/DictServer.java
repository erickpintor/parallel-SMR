package demo.dict;

import bftsmart.tom.MessageContext;
import parallelism.MessageContextPair;
import parallelism.late.CBASEServiceReplica;
import parallelism.late.COSType;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

final class DictServer {

    private static final Logger LOGGER = Logger.getLogger(DictServer.class.getName());

    private final Map<Integer, Integer> dict;

    private DictServer(int processID, int nThreads, int nKeys) {
        dict = new HashMap<>(nKeys);
        for (int i = 0; i < nKeys; i++)
            dict.put(i, 0);

        new CBASEServiceReplica(
                processID,
                this::execute,
                null,
                nThreads,
                DictServer::isConflicting,
                COSType.lockFreeGraph);
    }

    private static boolean isConflicting(MessageContextPair a,
                                         MessageContextPair b) {
        Command.Encoded cmdA = Command.wrap(a.operation);
        Command.Encoded cmdB = Command.wrap(b.operation);
        return cmdA.conflictWith(cmdB);
    }

    private byte[] execute(byte[] bytes, MessageContext ctx) {
        ByteBuffer resp = ByteBuffer.allocate(4);
        Command.Encoded command = Command.wrap(bytes);
        resp.putInt(command.execute(dict));
        return resp.flip().array();
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: DictServer <processID> <threads> <keys>");
            System.exit(1);
        }

        try {
            int processID = Integer.parseInt(args[0]);
            int nThreads = Integer.parseInt(args[1]);
            int nKeys = Integer.parseInt(args[2]);
            new DictServer(processID, nThreads, nKeys);
            LOGGER.info("Server initialization completed.");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid arguments.", e);
            System.exit(1);
        }
    }
}
