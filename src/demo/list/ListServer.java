/**
 * Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and
 * the authors indicated in the @author tags
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package demo.list;

import bftsmart.reconfiguration.ServerViewController;
import bftsmart.statemanagement.ApplicationState;
import bftsmart.statemanagement.StateManager;
import bftsmart.statemanagement.strategy.StandardStateManager;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.leaderchange.CertifiedDecision;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;
import bftsmart.tom.server.defaultservices.DefaultApplicationState;
import bftsmart.tom.util.Storage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.late.CBASEServiceReplica;
import parallelism.late.ConflictDefinition;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;
import parallelism.ParallelServiceReplica;
import parallelism.ParallelServiceReplica;
import parallelism.SequentialServiceReplica;


public final class ListServer implements SingleExecutable {

    private List<Integer> l = new LinkedList<Integer>();

    
    public ListServer(int id, int initThreads, int entries, boolean late, String gType) {

        if (initThreads <= 0) {
            System.out.println("Replica in sequential execution model.");

            //new ServiceReplica(id, this, null);
            new SequentialServiceReplica(id, this, null);
            
        } else if (late) {
            System.out.println("Replica in parallel execution model (late scheduling).");
            ConflictDefinition cd = new ConflictDefinition() {
                @Override
                public boolean isDependent(MessageContextPair r1, MessageContextPair r2) {
                    if(r1.classId == ParallelMapping.SYNC_ALL ||
                            r2.classId == ParallelMapping.SYNC_ALL){
                        return true;
                    }
                    return false;
                }
            };
            
            new CBASEServiceReplica(id, this, null, initThreads, cd, gType );
        } else {
            System.out.println("Replica in parallel execution model.");

            new ParallelServiceReplica(id, this, null, initThreads);
            //replica = new ParallelServiceReplica(id, this, null, minThreads, initThreads, maxThreads, new LazyPolicy());
            //replica = new ParallelServiceReplica(id, this,this, minThreads, initThreads, maxThreads, new AgressivePolicy());

        }
       for (int i = 0; i < entries; i++) {
            l.add(i);
        }

        System.out.println("Server initialization complete!");
    }

    public byte[] executeOrdered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    public byte[] executeUnordered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    public byte[] execute(byte[] command, MessageContext msgCtx) {

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(command);
            ByteArrayOutputStream out = null;
            byte[] reply = null;
            int cmd = new DataInputStream(in).readInt();

            switch (cmd) {
                case BFTList.ADD:
                    Integer value = (Integer) new ObjectInputStream(in).readObject();
                    boolean ret = false;
                    if (!l.contains(value)) {
                        ret = l.add(value);
                    }
                    out = new ByteArrayOutputStream();
                    ObjectOutputStream out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case BFTList.REMOVE:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = l.remove(value);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case BFTList.SIZE:
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeInt(l.size());
                    reply = out.toByteArray();
                    break;
                case BFTList.CONTAINS:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case BFTList.GET:
                    int index = new DataInputStream(in).readInt();
                    Integer r = null;
                    if (index > l.size()) {
                        r = new Integer(-1);
                    } else {
                        r = l.get(index);
                    }
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeObject(r);
                    reply = out.toByteArray();
                    break;
            }
            return reply;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ListServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

   
    public static void main(String[] args) {
        if (args.length < 7) {
            System.out.println("Usage: ... ListServer <processId> <num threads> <initial entries> <late scheduling?> <graph type>");
            System.exit(-1);
        }
        int processId = Integer.parseInt(args[0]);
        int initialNT = Integer.parseInt(args[1]);
        int entries = Integer.parseInt(args[2]);
        boolean late = Boolean.parseBoolean(args[3]);
        String gType = args[4];

        new ListServer(processId, initialNT, entries, late, gType);
    }

}
