/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.util;


/**
 *
 * @author eduardo
 */

public class Storage {
    
    
    private long[] values;
    private int count = 0;
    
    
    /** Creates a new instance of Storage */
    public Storage(int size) {
        values = new long[size];
    }
    
    public int getCount(){
        return count;
    }
    
    public void store(long value){
        if(count < values.length){
            values[count++] = value;
        }
    }
    
    public long getAverage(boolean limit){
        return computeAverage(values,limit);
    }
    
    public double getDP(boolean limit){
        return computeDP(values,limit);
    }
    
    public long[] getValues(){
        return values;
    }
    
   public long getPercentile(int percent){
      java.util.Arrays.sort(values);
      int pos = (values.length-1)*percent/100;
      return values[pos];
   }
    
    private long computeAverage(long[] values, boolean percent){
        java.util.Arrays.sort(values);
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
        //System.out.println("mim: "+values[limit]);
        //System.out.println("max: "+values[values.length-limit-1]);
        return Math.sqrt((double)var);
    }
    
    
}