/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.list;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author alchieri
 */
public class WorkloadGenerator {

    private int percent;
    private int[] operations;

    public WorkloadGenerator(int percent, int size) {
        this.percent = percent;
        this.operations = new int[size];
        generate();
    }

    //Alex apenas para experimento
    public WorkloadGenerator(int size) {
        this.percent = -1;
        this.operations = new int[size];
        generate2();
    }
    //Fim ALex

    public int[] getOperations() {
        return operations;
    }

    private void generate() {
        String sep = System.getProperty("file.separator");
        String path = "config" + sep + "workloadP_BFT_SMART";
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        path = path + sep + "workload_lista" + percent + "_" + operations.length + ".txt";

        f = new File(path);
        if (f.exists()) {
            load(path);
        } else {

            try {
                FileWriter fw = new FileWriter(f);
                PrintWriter pw = new PrintWriter(fw);

                Random rand = new Random();
                int op = 0;
                int num = 0;
                int cnf = 0;
                int ncnf = 0;

                int ncnfT = ((100 - this.percent) * this.operations.length) / 100;
                int cnfT = (this.percent * this.operations.length) / 100;

                while (num < this.operations.length) {

                    int r = rand.nextInt(100);
                    if ((cnf == cnfT) || (r >= percent && ncnf < ncnfT)) {
                        ncnf++;
                        //nao conflitantes
                        r = rand.nextInt(2);
                        //if(r >= 2){
                        //CONTAINS
                        //op = BFTList.CONTAINS;
                        //}else
                        if (r >= 1) {
                            //SIZE
                            //op = BFTList.SIZE;
                            op = BFTList.CONTAINS;

                        } else {
                            //GET
                            op = BFTList.GET;
                        }
                    } else {
                        cnf++;
                        //conflitante
                        r = rand.nextInt(2);
                        if (r >= 1) {
                            //ADD
                            op = BFTList.ADD;
                        } else {
                            //REMOVE
                            op = BFTList.REMOVE;
                        }
                    }

                    pw.println(op);
                    this.operations[num] = op;
                    num++;

                }

                pw.flush();
                fw.flush();
                pw.close();
                fw.close();

                System.out.println("Conflitantes: " + cnf);
                System.out.println("Não Conflitantes: " + ncnf);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }

    //Alex apenas para Experimento
    private void generate2() {
        String sep = System.getProperty("file.separator");
        String path = "config" + sep + "workloadP_BFT_SMART";
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        path = path + sep + "workload_lista" + percent + "_" + operations.length + ".txt";

        f = new File(path);
        if (f.exists()) {
            load(path);
        } else {

            try {
                FileWriter fw = new FileWriter(f);
                PrintWriter pw = new PrintWriter(fw);

                Random rand = new Random();
                int op = 0;
                int num = 0;
                int cnf = 0;
                int ncnf = 0;

                int ncnfT = 0;
                int cnfT = 0;

                while (num < this.operations.length) {

                    //0% conflitantes
                    while (num < this.operations.length * 0.1) {
                        op = BFTList.CONTAINS;
                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //10% conflitantes
                    while (num < this.operations.length * 0.2) {

                        if (num < (this.operations.length * 0.2) * 0.1) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //20% conflitantes
                    while (num < this.operations.length * 0.3) {
                        if (num < (this.operations.length * 0.3) * 0.2) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //30% conflitantes
                    while (num < this.operations.length * 0.4) {
                        if (num < (this.operations.length * 0.4) * 0.3) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //40% conflitantes
                    while (num < this.operations.length * 0.5) {
                        if (num < (this.operations.length * 0.5) * 0.4) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //50% conflitantes
                    while (num < this.operations.length * 0.6) {
                        if (num < (this.operations.length * 0.6) * 0.5) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //60% conflitantes
                    while (num < this.operations.length * 0.7) {
                        if (num < (this.operations.length * 0.7) * 0.6) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //70% conflitantes
                    while (num < this.operations.length * 0.8) {
                        if (num < (this.operations.length * 0.8) * 0.7) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //80% conflitantes
                    while (num < this.operations.length * 0.9) {
                        if (num < (this.operations.length * 0.9) * 0.8) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //100%conflitantes
                    while (num < this.operations.length * 1) {
                        op = BFTList.ADD;
                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    /*
                    //50%conflitantes
                    ncnfT = ((100 - 50) * this.operations.length) / 100;
                    cnfT = (50 * this.operations.length) / 100;

                    while (num < this.operations.length * 0.6) {
                        int r = rand.nextInt(100);
                        if ((cnf == cnfT) || (r >= percent && ncnf < ncnfT)) {
                            ncnf++;
                            //nao conflitantes
                            r = rand.nextInt(2);
                            //if(r >= 2){
                            //CONTAINS
                            //op = BFTList.CONTAINS;
                            //}else
                            if (r >= 1) {
                                //SIZE
                                //op = BFTList.SIZE;
                                op = BFTList.CONTAINS;

                            } else {
                                //GET
                                op = BFTList.GET;
                            }
                        } else {
                            cnf++;
                            //conflitante
                            r = rand.nextInt(2);
                            if (r >= 1) {
                                //ADD
                                op = BFTList.ADD;
                            } else {
                                //REMOVE
                                op = BFTList.REMOVE;
                            }
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //75%conflitantes
                    ncnfT = ((100 - 75) * this.operations.length) / 100;
                    cnfT = (75 * this.operations.length) / 100;

                    while (num < this.operations.length * 0.8) {
                        int r = rand.nextInt(100);
                        if ((cnf == cnfT) || (r >= percent && ncnf < ncnfT)) {
                            ncnf++;
                            //nao conflitantes
                            r = rand.nextInt(2);
                            //if(r >= 2){
                            //CONTAINS
                            //op = BFTList.CONTAINS;
                            //}else
                            if (r >= 1) {
                                //SIZE
                                //op = BFTList.SIZE;
                                op = BFTList.CONTAINS;

                            } else {
                                //GET
                                op = BFTList.GET;
                            }
                        } else {
                            cnf++;
                            //conflitante
                            r = rand.nextInt(2);
                            if (r >= 1) {
                                //ADD
                                op = BFTList.ADD;
                            } else {
                                //REMOVE
                                op = BFTList.REMOVE;
                            }
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }

                    //25%conflitantes
                    ncnfT = ((100 - 25) * this.operations.length) / 100;
                    cnfT = (25 * this.operations.length) / 100;
                    while (num < this.operations.length) {
                        int r = rand.nextInt(100);
                        if ((cnf == cnfT) || (r >= percent && ncnf < ncnfT)) {
                            ncnf++;
                            //nao conflitantes
                            r = rand.nextInt(2);
                            //if(r >= 2){
                            //CONTAINS
                            //op = BFTList.CONTAINS;
                            //}else
                            if (r >= 1) {
                                //SIZE
                                //op = BFTList.SIZE;
                                op = BFTList.CONTAINS;

                            } else {
                                //GET
                                op = BFTList.GET;
                            }
                        } else {
                            cnf++;
                            //conflitante
                            r = rand.nextInt(2);
                            if (r >= 1) {
                                //ADD
                                op = BFTList.ADD;
                            } else {
                                //REMOVE
                                op = BFTList.REMOVE;
                            }
                        }

                        pw.println(op);
                        this.operations[num] = op;
                        num++;
                    }
                     */
                }

                pw.flush();
                fw.flush();
                pw.close();
                fw.close();

                System.out.println("Conflitantes: " + cnf);
                System.out.println("Não Conflitantes: " + ncnf);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }

    private void load(String path) {
        //System.out.println("Vai ler!!!");
        try {

            FileReader fr = new FileReader(path);

            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            int j = 0;
            while (((line = rd.readLine()) != null) && (j < operations.length)) {
                operations[j] = Integer.valueOf(line);
                //System.out.println("Leu:" + operations[j]);
                j++;
            }
            fr.close();
            rd.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        for (int i = 0; i <= 600; i++) {
            System.out.print(i + " ");
            double j = rand.nextInt(2);
            System.out.println(j + 10);
        }

        new WorkloadGenerator(100000);
//        
//        new WorkloadGenerator(25, 1000);
//        
//        new WorkloadGenerator(50, 1000);
//        
//        new WorkloadGenerator(75, 1000);
//        
//        new WorkloadGenerator(100, 1000);
    }

}
