/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.util;

import bftsmart.tom.MessageContext;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import parallelism.FIFOQueue;
import parallelism.ParallelServiceReplica;

/**
 *
 * @author eduardo
 */
public class ThroughputStatistics {

    private int[][] counters;
    //private boolean[] restart;
    private int period = 1000; //millis

    private int interval = 120;

    private boolean started = false;
    private int now = 0;
    private PrintWriter pw;
    private String print;

    private int numT = 0;

    //private Timer timer = new Timer();
    public ThroughputStatistics(int numThreads, String filePath, String print) {
        this.print = print;
        numT = numThreads;
        counters = new int[numThreads][interval + 1];
        //restart = new boolean[numThreads];
        for (int i = 0; i < numThreads; i++) {
            for (int j = 0; j < interval + 1; j++) {
                counters[i][j] = 0;
            }
        }

        try {

            pw = new PrintWriter(new FileWriter(new File(filePath)));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void computeThroughput(long timeMillis) {

        for (int time = 0; time <= interval; time++) {
            int total = 0;
            for (int i = 0; i < numT; i++) {

                total = total + counters[i][time];

            }

            float tp = (float) (total * 1000 / (float) timeMillis);

            //System.out.println("Throughput at " + print + " = " + tp + " operations/sec in sec : " + now);
            pw.println(time + " " + tp);
        }
        pw.flush();

    }

    public void printTP(long timeMillis) {
        int total = 0;
        for (int i = 0; i < numT; i++) {
            total = total + counters[i][now];
            /* if (!restart[i]) {
                total = total + counters[i];
                counters[i] = 0;
                restart[i] = true;
            }*/

        }

        float tp = (float) (total * 1000 / (float) timeMillis);

        System.out.println("Throughput at " + print + " = " + tp + " operations/sec in sec : " + now);
        //System.out.println("valor de c: "+FIFOQueue.c);

    }

    boolean stoped = true;
    int fakenow = 0;
    public void start() {
        if (!started) {
            started = true;
            now = 0;
            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    fakenow++;
                    if(fakenow == 30){
                        stoped = false;
                        for(int i = 0; i < numT; i++){
                            counters[i][0] = 0;
                        }
                    }else if (!stoped) {
                        
                        if (now <= interval) {
                            printTP(period);
                            now++;
                        }

                        if (now == interval + 1) {
                            stoped = true;
                            computeThroughput(period);
                        }
                    }
                }
            }, period, period);

        }
    }


    /*public void start() {
        if (!started) {
            started = true;
            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    computeThroughput(period);
                }
            }, period, period);
            now = 0;
        }
    }*/
    public void computeStatistics(int threadId, int amount) {
        /*if (restart[threadId]) {
            counters[threadId] = amount;
            restart[threadId] = false;
        } else {
            counters[threadId] = counters[threadId] + amount;
        }*/
        if(!stoped){
            counters[threadId][now] = counters[threadId][now] + amount;
        }

    }

}
