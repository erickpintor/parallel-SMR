/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.list;

import parallelism.ClassToThreads;

/**
 *
 * @author eduardo
 */
public class MultipartitionMapping {
    
    public static final int R1 = 11;
    public static final int R2 = 12;
    public static final int R3 = 13;
    public static final int R4 = 14;
    public static final int R5 = 15;
    public static final int R6 = 16;
    public static final int R7 = 17;
    public static final int R8 = 18;
    
    public static final int W1 = 21;
    public static final int W2 = 22;
    public static final int W3 = 23;
    public static final int W4 = 24;
    public static final int W5 = 25;
    public static final int W6 = 26;
    public static final int W7 = 27;
    public static final int W8 = 28;
    
    public static final int GR = 31;
    public static final int GW = 41;
    
    
    public static ClassToThreads[] getM2P4T2(){
        ClassToThreads[] cts = new ClassToThreads[10];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 0;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 0;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[1];
        ids[0] = 1;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[1];
        ids[0] = 1;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        
        
        
        //W1
        ids = new int[1];
        ids[0] = 0;
        cts[6] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 0;
        cts[7] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        
        //W3
        ids = new int[1];
        ids[0] = 1;
        cts[8] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[1];
        ids[0] = 1;
        cts[9] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        return cts;
         
     }
     
    
    
    public static ClassToThreads[] getM2P4T4(){
        ClassToThreads[] cts = new ClassToThreads[10];
        
        //GR
        int[] ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 0;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 2;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[1];
        ids[0] = 3;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[1];
        ids[0] = 1;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        
        
        
        //W1
        ids = new int[1];
        ids[0] = 0;
        cts[6] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 2;
        cts[7] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        
        //W3
        ids = new int[1];
        ids[0] = 3;
        cts[8] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[1];
        ids[0] = 1;
        cts[9] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        return cts;
         
     }
     
     public static ClassToThreads[] getM2P4T8(){
        ClassToThreads[] cts = new ClassToThreads[10];
        
        //GR
        int[] ids = new int[4];
        ids[0] = 0;
        ids[1] = 4;
        ids[2] = 6;
        ids[3] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 4;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 6;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 3;
        ids[1] = 5;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 7;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        
        
        
        //W1
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 4;
        cts[6] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 6;
        cts[7] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        
        //W3
        ids = new int[2];
        ids[0] = 3;
        ids[1] = 5;
        cts[8] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 7;
        cts[9] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        return cts;
     }
     
     public static ClassToThreads[] getM2P4T12(){
         ClassToThreads[] cts = new ClassToThreads[10];
        
        //GR
        int[] ids = new int[4];
        ids[0] = 0;
        ids[1] = 3;
        ids[2] = 6;
        ids[3] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[12];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        ids[10] = 10;
        ids[11] = 11;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[3];
        ids[0] = 1;
        ids[1] = 6;
        ids[2] = 10;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[3];
        ids[0] = 4;
        ids[1] = 5;
        ids[2] = 7;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 8;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[3];
        ids[0] = 3;
        ids[1] = 9;
        ids[2] = 11;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        
        //W1
        ids = new int[3];
        ids[0] = 1;
        ids[1] = 6;
        ids[2] = 10;
        cts[6] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //W2
        ids = new int[3];
        ids[0] = 4;
        ids[1] = 5;
        ids[2] = 7;
        cts[7] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        //W3
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 8;
        cts[8] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[3];
        ids[0] = 3;
        ids[1] = 9;
        ids[2] = 11;
        cts[9] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        return cts;
     }
    
     
     
    public static ClassToThreads[] getM2P2T2(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 0;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 1;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[1];
        ids[0] = 0;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 1;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
     
    public static ClassToThreads[] getM2P2T4(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 3;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 2;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 3;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 2;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 3;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
    public static ClassToThreads[] getM2P2T8(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 3;
        ids[1] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[4];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[4];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
    public static ClassToThreads[] getM2P2T12(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 1;
        ids[1] = 9;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[12];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        ids[10] = 10;
        ids[11] = 11;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[6];
        ids[0] = 2;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 8;
        ids[5] = 9;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[6];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 10;
        ids[5] = 11;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[6];
        ids[0] = 2;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 8;
        ids[5] = 9;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[6];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 10;
        ids[5] = 11;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
        
        
    }
    
    
    public static ClassToThreads[] getM2P2T12RW(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[1];
        ids[0] = 0;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[12];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        ids[10] = 10;
        ids[11] = 11;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[11];
        ids[0] = 1;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 5;
        ids[5] = 6;
        ids[6] = 7;
        ids[7] = 8;
        ids[8] = 9;
        ids[9] = 10;
        ids[10] = 11;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 0;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[11];
        ids[0] = 1;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 5;
        ids[5] = 6;
        ids[6] = 7;
        ids[7] = 8;
        ids[8] = 9;
        ids[9] = 10;
        ids[10] = 11;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 0;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
        
        
    }
    
    public static ClassToThreads[] getM2P2T4TunnedR1(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 3;
        ids[0] = 2;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 3;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 2;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 3;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 2;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
    public static ClassToThreads[] getM2P2T8TunnedR1(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 4;
        ids[1] = 6;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[5];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 5;
        ids[4] = 6;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[3];
        ids[0] = 3;
        ids[1] = 4;
        ids[2] = 7;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[5];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 5;
        ids[4] = 6;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[3];
        ids[0] = 3;
        ids[1] = 4;
        ids[2] = 7;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
    public static ClassToThreads[] getM2P2T12TunnedR1(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 3;
        ids[1] = 8;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[12];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        ids[10] = 10;
        ids[11] = 11;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 5;
        ids[5] = 6;
        ids[6] = 7;
        ids[7] = 9;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[4];
        ids[0] = 1;
        ids[1] = 8;
        ids[2] = 10;
        ids[3] = 11;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 5;
        ids[5] = 6;
        ids[6] = 7;
        ids[7] = 9;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[4];
        ids[0] = 1;
        ids[1] = 8;
        ids[2] = 10;
        ids[3] = 11;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
        
        
    }
    
    public static ClassToThreads[] getM2P2T4TunnedW1(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 2;
        ids[0] = 3;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 3;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 2;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[1];
        ids[0] = 3;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 2;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
    
    public static ClassToThreads[] getM2P2T8TunnedW1(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 7;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 0;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[1];
        ids[0] = 7;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 0;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
    
    public static ClassToThreads[] getM2P2T12TunnedW1(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 3;
        ids[1] = 8;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[12];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        ids[10] = 10;
        ids[11] = 11;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 3;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 8;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[1];
        ids[0] = 3;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 8;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
        
        
    }
    
    public static ClassToThreads[] getP8T8(){
        ClassToThreads[] cts = new ClassToThreads[18];
        
        //GR
        int[] ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 1;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 2;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[1];
        ids[0] = 3;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[1];
        ids[0] = 4;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        

        //R5
        ids = new int[1];
        ids[0] = 5;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);
        
        //R6
        ids = new int[1];
        ids[0] = 6;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);
        
        //R7
        ids = new int[1];
        ids[0] = 7;
        cts[8] = new ClassToThreads(R7, ClassToThreads.CONC, ids);

        //R8
        ids = new int[1];
        ids[0] = 0;
        cts[9] = new ClassToThreads(R8, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[1];
        ids[0] = 1;
        cts[10] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 2;
        cts[11] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        
        //W3
        ids = new int[1];
        ids[0] = 3;
        cts[12] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[1];
        ids[0] = 4;
        cts[13] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        //W5
        ids = new int[1];
        ids[0] = 5;
        cts[14] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //W6
        ids = new int[1];
        ids[0] = 6;
        cts[15] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        
        //W7
        ids = new int[1];
        ids[0] = 7;
        cts[16] = new ClassToThreads(W7, ClassToThreads.SYNC, ids);
        
        //W8
        ids = new int[1];
        ids[0] = 0;
        cts[17] = new ClassToThreads(W8, ClassToThreads.SYNC, ids);
        
        return cts;
     }
    
    public static ClassToThreads[] getP8T16(){
        ClassToThreads[] cts = new ClassToThreads[18];
        
        //GR
        int[] ids = new int[8];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 4;
        ids[3] = 6;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 12;
        ids[7] = 14;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[16];
        for(int i = 0; i < 16;i++){
            ids[i] = i;
        }
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        //W1
        cts[10] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        //W2
        cts[11] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 5;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        //W3
        cts[12] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 7;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        //W4
        cts[13] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
    
        //R5
        ids = new int[2];
        ids[0] = 8;
        ids[1] = 9;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);
        //W5
        cts[14] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //R6
        ids = new int[2];
        ids[0] = 10;
        ids[1] = 11;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);
        //W6
        cts[15] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        
        //R7
        ids = new int[2];
        ids[0] = 12;
        ids[1] = 13;
        cts[8] = new ClassToThreads(R7, ClassToThreads.CONC, ids);
        //W7
        cts[16] = new ClassToThreads(W7, ClassToThreads.SYNC, ids);
    
        //R8
        ids = new int[2];
        ids[0] = 14;
        ids[1] = 15;
        cts[9] = new ClassToThreads(R8, ClassToThreads.CONC, ids);
        //W8
        cts[17] = new ClassToThreads(W8, ClassToThreads.SYNC, ids);
        
        return cts;
     }
    
    
    public static ClassToThreads[] getP6T6(){
        ClassToThreads[] cts = new ClassToThreads[14];
        
        //GR
        int[] ids = new int[6];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[6];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[1];
        ids[0] = 1;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[1];
        ids[0] = 2;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[1];
        ids[0] = 3;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[1];
        ids[0] = 4;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        

        //R5
        ids = new int[1];
        ids[0] = 5;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);
        
        //R6
        ids = new int[1];
        ids[0] = 0;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);
        
        //W1
        ids = new int[1];
        ids[0] = 1;
        cts[8] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[1];
        ids[0] = 2;
        cts[9] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        
        //W3
        ids = new int[1];
        ids[0] = 3;
        cts[10] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[1];
        ids[0] = 4;
        cts[11] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        //W5
        ids = new int[1];
        ids[0] = 5;
        cts[12] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //W6
        ids = new int[1];
        ids[0] = 0;
        cts[13] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        
        return cts;
     }
    
    
    public static ClassToThreads[] getP6T12(){
        ClassToThreads[] cts = new ClassToThreads[14];
        
        //GR
        int[] ids = new int[6];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 4;
        ids[3] = 6;
        ids[4] = 8;
        ids[5] = 10;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[12];
        for(int i = 0; i < 12;i++){
            ids[i] = i;
        }
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        //W1
        cts[8] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        //W2
        cts[9] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 5;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        //W3
        cts[10] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 7;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        //W4
        cts[11] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
    
        //R5
        ids = new int[2];
        ids[0] = 8;
        ids[1] = 9;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);
        //W5
        cts[12] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //R6
        ids = new int[2];
        ids[0] = 10;
        ids[1] = 11;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);
        //W6
        cts[13] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        
        
        return cts;
     }
    
    
    public static ClassToThreads[] getNaiveP2T4(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[0] = new ClassToThreads(GR, ClassToThreads.CONC, ids);
        
        //GW
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 2;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 3;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //W1
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 1;
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //W2
        ids = new int[3];
        ids[0] = 1;
        ids[1] = 3;
        ids[2] = 0;
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
    
    public static ClassToThreads[] getNaiveP4T8(){
        ClassToThreads[] cts = new ClassToThreads[10];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[0] = new ClassToThreads(GR, ClassToThreads.CONC, ids);
        
        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 4;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 6;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 3;
        ids[1] = 5;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 7;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        
        //W1
        ids = new int[4];
        ids[0] = 2;
        ids[1] = 4;
        ids[2] = 0;
        ids[3] = 1;
        cts[6] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //W2
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 6;
        ids[2] = 1;
        cts[7] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        //W3
        ids = new int[4];
        ids[0] = 3;
        ids[1] = 5;
        ids[2] = 0;
        ids[3] = 1;
        cts[8] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[3];
        ids[0] = 1;
        ids[1] = 7;
        ids[2] = 0;
        cts[9] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        return cts;
     }
    
    
    public static ClassToThreads[] getNaiveP6T12(){
        ClassToThreads[] cts = new ClassToThreads[14];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[0] = new ClassToThreads(GR, ClassToThreads.CONC, ids);
        
        
        //GW
        ids = new int[12];
        for(int i = 0; i < 12;i++){
            ids[i] = i;
        }
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        //W1
        cts[8] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);

        //W2
        ids = new int[4];
        ids[0] = 2;
        ids[1] = 3;
        ids[2] = 0;
        ids[3] = 1;
        cts[9] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 5;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //W3
        ids = new int[4];
        ids[0] = 4;
        ids[1] = 5;
        ids[2] = 0;
        ids[3] = 1;
        cts[10] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 7;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        
        //W4
        ids = new int[4];
        ids[0] = 6;
        ids[1] = 7;
        ids[2] = 0;
        ids[3] = 1;
        cts[11] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
    
        //R5
        ids = new int[2];
        ids[0] = 8;
        ids[1] = 9;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);

        //W5
        ids = new int[4];
        ids[0] = 8;
        ids[1] = 9;
        ids[2] = 0;
        ids[3] = 1;
        cts[12] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //R6
        ids = new int[2];
        ids[0] = 10;
        ids[1] = 11;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);

        //W6
        ids = new int[4];
        ids[0] = 10;
        ids[1] = 11;
        ids[2] = 0;
        ids[3] = 1;
        cts[13] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        return cts;
     }
    
    public static ClassToThreads[] getNaiveP8T16(){
        ClassToThreads[] cts = new ClassToThreads[18];
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[0] = new ClassToThreads(GR, ClassToThreads.CONC, ids);
        
        //GW
        ids = new int[16];
        for(int i = 0; i < 16;i++){
            ids[i] = i;
        }
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        //W1
        cts[10] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);

        //W2
        ids = new int[4];
        ids[0] = 2;
        ids[1] = 3;
        ids[2] = 0;
        ids[3] = 1;
        cts[11] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 5;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //W3
        ids = new int[4];
        ids[0] = 4;
        ids[1] = 5;
        ids[2] = 0;
        ids[3] = 1;
        cts[12] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 7;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);

        //W4
        ids = new int[4];
        ids[0] = 6;
        ids[1] = 7;
        ids[2] = 0;
        ids[3] = 1;
        cts[13] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
    
        //R5
        ids = new int[2];
        ids[0] = 8;
        ids[1] = 9;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);

        //W5
        ids = new int[4];
        ids[0] = 8;
        ids[1] = 9;
        ids[2] = 0;
        ids[3] = 1;
        cts[14] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //R6
        ids = new int[2];
        ids[0] = 10;
        ids[1] = 11;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);

        //W6
        ids = new int[4];
        ids[0] = 10;
        ids[1] = 11;
        ids[2] = 0;
        ids[3] = 1;
        cts[15] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        
        //R7
        ids = new int[2];
        ids[0] = 12;
        ids[1] = 13;
        cts[8] = new ClassToThreads(R7, ClassToThreads.CONC, ids);

        //W7
        ids = new int[4];
        ids[0] = 12;
        ids[1] = 13;
        ids[2] = 0;
        ids[3] = 1;
        cts[16] = new ClassToThreads(W7, ClassToThreads.SYNC, ids);
    
        //R8
        ids = new int[2];
        ids[0] = 14;
        ids[1] = 15;
        cts[9] = new ClassToThreads(R8, ClassToThreads.CONC, ids);
        //W8
        ids = new int[4];
        ids[0] = 14;
        ids[1] = 15;
        ids[2] = 0;
        ids[3] = 1;
        cts[17] = new ClassToThreads(W8, ClassToThreads.SYNC, ids);
        return cts;
     }
    

    public static ClassToThreads[] getP2T10(){
        ClassToThreads[] cts = new ClassToThreads[6];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 3;
        ids[1] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[10];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[5];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        //W1
        cts[4] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //R2
        ids = new int[5];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        //W2
        cts[5] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        return cts;
    }
    
   public static ClassToThreads[] getP4T10(){
        ClassToThreads[] cts = new ClassToThreads[10];
        
        //GR
        int[] ids = new int[4];
        ids[0] = 0;
        ids[1] = 4;
        ids[2] = 6;
        ids[3] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[10];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[3];
        ids[0] = 2;
        ids[1] = 4;
        ids[2] = 8;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        //W1
        cts[6] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //R2
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 6;
        ids[2] = 8;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        //W2
        cts[7] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        
        //R3
        ids = new int[3];
        ids[0] = 3;
        ids[1] = 5;
        ids[2] = 9;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        //W3
        cts[8] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        
        //R4
        ids = new int[3];
        ids[0] = 1;
        ids[1] = 7;
        ids[2] = 9;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        //W4
        cts[9] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        return cts;
     }
     
    
    public static ClassToThreads[] getP6T10(){
        ClassToThreads[] cts = new ClassToThreads[14];
        
        //GR
        int[] ids = new int[5];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 4;
        ids[3] = 6;
        ids[4] = 8;
        //ids[5] = 10;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[10];
        for(int i = 0; i < 10;i++){
            ids[i] = i;
        }
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        //W1
        cts[8] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        //W2
        cts[9] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 5;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        //W3
        cts[10] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 7;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        //W4
        cts[11] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
    
        //R5
        ids = new int[2];
        ids[0] = 8;
        ids[1] = 9;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);
        //W5
        cts[12] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //R6
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 8;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);
        //W6
        cts[13] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        
        
        return cts;
     }
    
    
    public static ClassToThreads[] getP8T10(){
        ClassToThreads[] cts = new ClassToThreads[18];
        
        //GR
        int[] ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        cts[0] = new ClassToThreads(GR, ClassToThreads.SYNC, ids);
        
        
        //GW
        ids = new int[10];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        cts[1] = new ClassToThreads(GW, ClassToThreads.SYNC, ids);
        
        
        //R1
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 8;
        cts[2] = new ClassToThreads(R1, ClassToThreads.CONC, ids);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 8;
        cts[3] = new ClassToThreads(R2, ClassToThreads.CONC, ids);
        
        //R3
        ids = new int[2];
        ids[0] = 3;
        ids[1] = 8;
        cts[4] = new ClassToThreads(R3, ClassToThreads.CONC, ids);
        
        //R4
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 8;
        cts[5] = new ClassToThreads(R4, ClassToThreads.CONC, ids);
        

        //R5
        ids = new int[2];
        ids[0] = 5;
        ids[1] = 9;
        cts[6] = new ClassToThreads(R5, ClassToThreads.CONC, ids);
        
        //R6
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 9;
        cts[7] = new ClassToThreads(R6, ClassToThreads.CONC, ids);
        
        //R7
        ids = new int[2];
        ids[0] = 7;
        ids[1] = 9;
        cts[8] = new ClassToThreads(R7, ClassToThreads.CONC, ids);

        //R8
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 9;
        cts[9] = new ClassToThreads(R8, ClassToThreads.CONC, ids);
        
        
        //W1
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 8;
        cts[10] = new ClassToThreads(W1, ClassToThreads.SYNC, ids);
        
        
        //W2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 8;
        cts[11] = new ClassToThreads(W2, ClassToThreads.SYNC, ids);
        
        
        //W3
        ids = new int[2];
        ids[0] = 3;
        ids[1] = 8;
        cts[12] = new ClassToThreads(W3, ClassToThreads.SYNC, ids);
        
        //W4
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 8;
        cts[13] = new ClassToThreads(W4, ClassToThreads.SYNC, ids);
        
        //W5
        ids = new int[2];
        ids[0] = 5;
        ids[1] = 9;
        cts[14] = new ClassToThreads(W5, ClassToThreads.SYNC, ids);
        
        //W6
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 9;
        cts[15] = new ClassToThreads(W6, ClassToThreads.SYNC, ids);
        
        //W7
        ids = new int[2];
        ids[0] = 7;
        ids[1] = 9;
        cts[16] = new ClassToThreads(W7, ClassToThreads.SYNC, ids);
        
        //W8
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 9;
        cts[17] = new ClassToThreads(W8, ClassToThreads.SYNC, ids);
        
        return cts;
     }
    
    
}

    
    
    
    