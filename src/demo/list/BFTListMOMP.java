/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.list;

import bftsmart.tom.ParallelAsynchServiceProxy;
import bftsmart.tom.ParallelServiceProxy;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.util.MultiOperationRequest;
import bftsmart.util.MultiOperationResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.ParallelMapping;

/**
 *
 * @author alchieri
 */
public class BFTListMOMP<V> extends BFTList<V> {

    
    
    public BFTListMOMP(int id, boolean parallelExecution) {
        super(id, parallelExecution, false);
    }

    public boolean addP1(V[] e) {
        return  addFinal(e, MultipartitionMapping.W1);
    }
    
    public boolean addP2(V[] e) {
        return  addFinal(e, MultipartitionMapping.W2);
    }
    
    public boolean addP3(V[] e) {
        return  addFinal(e, MultipartitionMapping.W3);
    }
    
    public boolean addP4(V[] e) {
        return  addFinal(e, MultipartitionMapping.W4);
    }
    
    public boolean addP5(V[] e) {
        return  addFinal(e, MultipartitionMapping.W5);
    }
    public boolean addP6(V[] e) {
        return  addFinal(e, MultipartitionMapping.W6);
    }
    public boolean addP7(V[] e) {
        return  addFinal(e, MultipartitionMapping.W7);
    }
    public boolean addP8(V[] e) {
        return  addFinal(e, MultipartitionMapping.W8);
    }
    
    public boolean addFinal(V[] e, int pId) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {
                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(pId);
                ObjectOutputStream out1 = new ObjectOutputStream(out);
                out1.writeObject(e[i]);
                out1.close();
                mo.add(i, out.toByteArray(), pId);
            }
            byte[] rep = proxy.invokeParallel(mo.serialize(), -100);
            
            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean addAll(V[] e) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {
                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(MultipartitionMapping.GW);
                ObjectOutputStream out1 = new ObjectOutputStream(out);
                out1.writeObject(e[i]);
                out1.close();
                mo.add(i, out.toByteArray(), MultipartitionMapping.GW);
            }
            byte[] rep = proxy.invokeParallel(mo.serialize(), -100);
            
            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    
    public boolean containsP1(V[] e) {
        return containsFinal(e, MultipartitionMapping.R1);
    }
    
    public boolean containsP2(V[] e) {
        return containsFinal(e, MultipartitionMapping.R2);
    }
    
    public boolean containsP3(V[] e) {
        return containsFinal(e, MultipartitionMapping.R3);
    }
    
    public boolean containsP4(V[] e) {
        return containsFinal(e, MultipartitionMapping.R4);
    }
    
    public boolean containsP5(V[] e) {
        return containsFinal(e, MultipartitionMapping.R5);
    }
    public boolean containsP6(V[] e) {
        return containsFinal(e, MultipartitionMapping.R6);
    }
    public boolean containsP7(V[] e) {
        return containsFinal(e, MultipartitionMapping.R7);
    }
    public boolean containsP8(V[] e) {
        return containsFinal(e, MultipartitionMapping.R8);
    }
    
    public boolean containsFinal(V[] e, int pId) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {

                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(pId);
                ObjectOutputStream out1 = new ObjectOutputStream(out);
                out1.writeObject(e[i]);
                out1.close();
                mo.add(i, out.toByteArray(), pId);

            }
            byte[] rep =  proxy.invokeParallel(mo.serialize(), -100);
                        
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    
    public boolean containsAll(V[] e) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {

                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(MultipartitionMapping.GR);
                ObjectOutputStream out1 = new ObjectOutputStream(out);
                out1.writeObject(e[i]);
                out1.close();
                mo.add(i, out.toByteArray(), MultipartitionMapping.GR);

            }
            byte[] rep =  proxy.invokeParallel(mo.serialize(), -100);
                        
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
