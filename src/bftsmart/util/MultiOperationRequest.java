/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eduardo
 */
public class MultiOperationRequest {

    public Operation[] operations;

    public MultiOperationRequest(int number) {
        this.operations = new Operation[number];
    }

    public void add(int index, byte[] data, int classId){
        this.operations[index] = new Operation(data, classId);
    }
    
    public MultiOperationRequest(byte[] buffer) {
        DataInputStream dis = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(buffer);
            dis = new DataInputStream(in);
            
            
            this.operations = new Operation[dis.readInt()];
            
            for(int i = 0; i < this.operations.length; i++){
                this.operations[i] = new Operation();
                this.operations[i].classId = dis.readInt();
                this.operations[i].data = new byte[dis.readInt()];
                dis.readFully(this.operations[i].data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                dis.close();
            } catch (IOException ex) {
                Logger.getLogger(MultiOperationRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		

    }

    public byte[] serialize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            
            oos.writeInt(operations.length);
            
            for(int i = 0; i < operations.length; i++){
                
                oos.writeInt(this.operations[i].classId);
                oos.writeInt(this.operations[i].data.length);
                oos.write(this.operations[i].data);
            }
            //oos.flush();
            //baos.flush();
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class Operation {

        public Operation() {
        }

        public Operation(byte[] data, int classId) {
            this.data = data;
            this.classId = classId;
        }

        
        
        public byte[] data;
        public int classId;
    }

}
