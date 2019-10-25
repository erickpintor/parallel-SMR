/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.list;


import bftsmart.util.MultiOperationRequest;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import parallelism.ParallelMapping;

/**
 *
 * @author alchieri
 */
public class BFTListMO<V> extends BFTList<V> {

    public BFTListMO(int id, boolean parallelExecution) {
        super(id, parallelExecution);
    }

    public boolean add(V[] e) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {
                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(ADD);
                //dos.writeInt(key.intValue());

                ObjectOutputStream out1 = new ObjectOutputStream(out);
                out1.writeObject(e[i]);
                out1.close();

                mo.add(i, out.toByteArray(), ParallelMapping.SYNC_ALL);

            }
            byte[] rep = null;
            if (parallel) {
                    rep = proxy.invokeParallel(mo.serialize(), ParallelMapping.SYNC_ALL);
            } else {
                rep = proxy.invokeOrdered(mo.serialize());
            }
            
            
            /*
            MultiOperationResponse resp = new MultiOperationResponse(rep);
            
            for(int i = 0; i < resp.operations.length;i++){
                ByteArrayInputStream bis = new ByteArrayInputStream(resp.operations[i].data);
                ObjectInputStream in = new ObjectInputStream(bis);
                boolean ret = in.readBoolean();
                
                in.close();
                if(ret != true){
                    return false;
                }
                
            }
            */
            return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean contains(V[] e) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {

                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(CONTAINS);
                //dos.writeInt(key.intValue());

                ObjectOutputStream out1 = new ObjectOutputStream(out);
                out1.writeObject(e[i]);
                out1.close();
                mo.add(i, out.toByteArray(), ParallelMapping.CONC_ALL);

            }
            //byte[] rep = null;
            if (parallel) {
                    proxy.invokeParallel(mo.serialize(), ParallelMapping.CONC_ALL);
            } else {
                proxy.invokeOrdered(mo.serialize());
            }
            /*
            MultiOperationResponse resp = new MultiOperationResponse(rep);
            
            for(int i = 0; i < resp.operations.length;i++){
                ByteArrayInputStream bis = new ByteArrayInputStream(resp.operations[i].data);
                ObjectInputStream in = new ObjectInputStream(bis);
                boolean ret = in.readBoolean();
                
                in.close();
                if(ret != true){
                    return false;
                }
                
            }
            */
            
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
