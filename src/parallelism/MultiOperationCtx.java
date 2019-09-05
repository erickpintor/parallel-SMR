/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.util.MultiOperationResponse;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author eduardo
 */
public class MultiOperationCtx {
    
    public TOMMessage request;
    public MultiOperationResponse response;
    
    public AtomicInteger interger = new AtomicInteger();
    
    public boolean finished = false;
    
    public MultiOperationCtx(int size, TOMMessage request){
        response = new MultiOperationResponse(size);
        this.request = request;
    }
    
    public void add(int index, byte[] resp){
        this.response.add(index, resp);
        //return this.response.isComplete();
    }
    
    
}
