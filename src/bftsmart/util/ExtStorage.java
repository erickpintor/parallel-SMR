/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.util;

import java.util.LinkedList;
import java.util.List;



public class ExtStorage {
    
    
    //private long[] values;
    //private int count = 0;
    
    private List<Long> val = new LinkedList<>();
    
    /** Creates a new instance of Storage */
    public ExtStorage() {
        //values = new long[size];
    }
    
   /* public int getCount(){
        return count;
    }*/

   /* public void reset(){
        count=0;
    }*/
    
    public void store(long value){
        val.add(value);
    }
    
    private long[] toArray(){
    
        long[] y = new long[val.size()];
        for(int i = 0; i < y.length; i++){
            y[i] = val.get(i);
        }
        return y;
    }
    
    public long getAverage(boolean limit){
        
        
        return computeAverage(toArray(), limit);
    }
    
    
    
    public long getPercentile(int percent){
      long[] values = toArray();
      java.util.Arrays.sort(values);
      int pos = (values.length-1)*percent/100;
      return values[pos];
   }
    
    
    
    
    public double getDP(boolean limit){
        return computeDP(toArray(), limit);
    }
    
    
    /*public long getMax(boolean limit){
        return computeMax(values,limit);
    }*/
    
    private long computeAverage(long[] values, boolean percent){
        java.util.Arrays.sort(values);
        //System.out.println("Val tam: "+values.length);
        int limit = 0;
        if(percent){
            limit = values.length/10;
        }
        long count = 0;
        for(int i = limit; i < values.length - limit;i++){
            count = count + values[i];
        }
        return count/(values.length - 2*limit);
    }
    
    /*private long computeMax(long[] values, boolean percent){
        java.util.Arrays.sort(values);
        int limit = 0;
        if(percent){
            limit = values.length/10;
        }
        long max = 0;
        for(int i = limit; i < values.length - limit;i++){
            if (values[i]>max){
                max = values[i];
            }
        }
        return max;
    }*/
    
    private double computeDP(long[] values, boolean percent){
        if(values.length <= 1){
            return 0;
        }
        java.util.Arrays.sort(values);
        int limit = 0;
        if(percent){
            limit = values.length/10;
        }
        long num = 0;
        long med = computeAverage(values,percent);
        long quad = 0;
        
        for(int i = limit; i < values.length - limit;i++){
            num++;
            quad = quad + values[i]*values[i]; //Math.pow(values[i],2);
        }
        long var = (quad - (num*(med*med)))/(num-1);
        ////br.ufsc.das.util.Logger.println("mim: "+values[limit]);
        ////br.ufsc.das.util.Logger.println("max: "+values[values.length-limit-1]);
        return Math.sqrt((double)var);
    }
    
    
}
