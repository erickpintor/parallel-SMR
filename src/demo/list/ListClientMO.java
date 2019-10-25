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

//import bftsmart.tom.parallelism.ParallelMapping;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import bftsmart.util.ExtStorage;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Example client that updates a BFT replicated service (a counter).
 *
 */
public class ListClientMO {

    public static int initId = 0;

    //public static boolean mix = true;
    public static int p = 0;

    public static boolean stop = false;

    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException {
        if (args.length < 8) {
            System.out.println("Usage: ... ListClient <num. threads> <process id> <number of requests> <interval> <maxIndex> <parallel?> <operations per request> <conflict percent>");
            System.exit(-1);
        }

        int numThreads = Integer.parseInt(args[0]);
        initId = Integer.parseInt(args[1]);

        int numberOfReqs = Integer.parseInt(args[2]);
        //int requestSize = Integer.parseInt(args[3]);
        int interval = Integer.parseInt(args[3]);
        int max = Integer.parseInt(args[4]);
        boolean verbose = false;
        boolean parallel = Boolean.parseBoolean(args[5]);
        

        int numberOfOps = Integer.parseInt(args[6]);

        p = Integer.parseInt(args[7]);
        System.out.println("percent : " + p);

        Client[] c = new Client[numThreads];

        for (int i = 0; i < numThreads; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ListClientMO.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Launching client " + (initId + i));
            c[i] = new ListClientMO.Client(initId + i, numberOfReqs, numberOfOps, interval, max, verbose, parallel);
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(ListClientMO.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < numThreads; i++) {
            c[i].start();
        }

        (new Timer()).scheduleAtFixedRate(new TimerTask() {
            public void run() {
                //change();
            }
        }, 60000, 60000); //a cada 1 minuto

        (new Timer()).schedule(new TimerTask() {
            public void run() {
                stop();
            }
        }, 5 * 60000); //depois de 5 minutos

        for (int i = 0; i < numThreads; i++) {

            try {
                c[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        }

        //System.exit(0);
    }

    public static void stop() {
        stop = true;
    }

    public static void change() {
        /*if (op == BFTList.CONTAINS) {
            op = BFTList.ADD;
        } else {
            op = BFTList.CONTAINS;
        }*/
    }

    static class Client extends Thread {

        int id;
        int numberOfReqs;
        int interval;

        int countNumOp = 0;

        boolean verbose;
        //boolean dos;
        //ServiceProxy proxy;
        //byte[] request;
        BFTListMO<Integer> store;

        int op = BFTList.CONTAINS;

        int maxIndex;
        //int percent;

        int opPerReq = 1;

        public Client(int id, int numberOfRqs, int opPerReq, int interval, int maxIndex, boolean verbose, boolean parallel) {
            super("Client " + id);

            this.id = id;
            this.numberOfReqs = numberOfRqs;
            this.opPerReq = opPerReq;
            // this.percent = percent;

            this.interval = interval;

            this.verbose = verbose;
            //this.proxy = new ServiceProxy(id);
            //this.request = new byte[this.requestSize];
            this.maxIndex = maxIndex;

            store = new BFTListMO<Integer>(id, parallel);
            //this.dos = dos;
        }

        /*  private boolean insertValue(int index) {

            return store.add(index);

        }*/
        public void run() {

            int req = 0;

            ExtStorage sRead = new ExtStorage();
            ExtStorage sWrite = new ExtStorage();

            System.out.println("Executing experiment for " + numberOfReqs + " ops");

            Random rand = new Random();

            Random indexRand = new Random();

            //for (int i = 0; i < numberOfReqs && !stop; i++, req++) {
            for (int i = 0; i < numberOfReqs; i++, req++) {

                if (i == 1) {
                    try {
                        //Thread.currentThread().sleep(20000);
                        Thread.currentThread().sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ListClientMO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                int r = rand.nextInt(100);
                if (r < p) {
                    op = BFTList.ADD;
                } else {
                    op = BFTList.CONTAINS;
                }
                if (op == BFTList.ADD) {
                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }

                    long last_send_instant = System.nanoTime();
                    store.add(reqs);
                    sWrite.store(System.nanoTime() - last_send_instant);
                } else if (op == BFTList.CONTAINS) {
                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }

                    long last_send_instant = System.nanoTime();
                    store.contains(reqs);
                    sRead.store(System.nanoTime() - last_send_instant);

                }
                /*else if (op == BFTList.GET) {

                    //int index = rand.nextInt(maxIndex);
                    int index = maxIndex - 1;
                    long last_send_instant = System.nanoTime();
                    store.get(index);
                    st.store(System.nanoTime() - last_send_instant);

                } else if (op == BFTList.REMOVE) {

                    //int index = rand.nextInt(maxIndex);
                    int index = maxIndex - 1;
                    long last_send_instant = System.nanoTime();
                    store.remove(new Integer(index));
                    st.store(System.nanoTime() - last_send_instant);

                } else {//SIZE
                    long last_send_instant = System.nanoTime();
                    store.size();
                    st.store(System.nanoTime() - last_send_instant);
                }*/

                if (verbose) {
                    System.out.println(this.id + " // sent!");
                }

                if (interval > 0 && i % 50 == 100) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {
                    }
                }

                if (verbose && (req % 1000 == 0)) {
                    System.out.println(this.id + " // " + req + " operations sent!");
                }

            }

            if (id == initId) {
                System.out.println(this.id + " //READ Average time for " + numberOfReqs + " executions (-10%) = " + sRead.getAverage(true) / 1000 + " us ");
                System.out.println(this.id + " //READ Standard desviation for " + numberOfReqs + " executions (-10%) = " + sRead.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " // READ 90th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(90) / 1000 + " us ");
                System.out.println(this.id + " // READ 95th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(95) / 1000 + " us ");
                System.out.println(this.id + " // READ 99th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(99) / 1000 + " us ");

                System.out.println(this.id + " //WRITE Average time for " + numberOfReqs + " executions (-10%) = " + sWrite.getAverage(true) / 1000 + " us ");
                System.out.println(this.id + " //WRITE Standard desviation for " + numberOfReqs + " executions (-10%) = " + sWrite.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 90th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(90) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 95th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(95) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 99th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(99) / 1000 + " us ");
            }

            System.out.println(this.id + " FINISHED!!!");

        }

    }
}
