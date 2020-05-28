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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eduardo
 */
public class MultiOperationResponse {

    public Response[] operations;

    public MultiOperationResponse(int number) {
        operations = new Response[number];
        for (int i = 0; i < operations.length; i++) {
            operations[i] = new Response();
        }
    }

    
    public boolean isComplete(){
        for (Response operation : operations) {
            if (operation.data == null) {
                return false;
            }
        }
        return true;
    }
    
    public void add(int index, byte[] data){
        this.operations[index].data = data;
    }
    
    public MultiOperationResponse(byte[] buffer) {
        DataInputStream dis = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(buffer);
            dis = new DataInputStream(in);
            
            
            this.operations = new Response[dis.readInt()];
            
            for(int i = 0; i < this.operations.length; i++){
                this.operations[i] = new Response();
                this.operations[i].data = new byte[dis.readInt()];
                dis.readFully(this.operations[i].data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                dis.close();
            } catch (IOException ex) {
                Logger.getLogger(MultiOperationResponse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		

    }

    public byte[] serialize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            
            oos.writeInt(operations.length);
            
            for(int i = 0; i < operations.length; i++){
                
                oos.writeInt(this.operations[i].data.length);
                oos.write(this.operations[i].data);
            }
            
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class Response {

        public Response() {
        }

        public Response(byte[] data) {
            this.data = data;
        }

        
        
        public volatile byte[] data;
        }

}
