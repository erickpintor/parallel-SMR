/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

/**
 *
 * @author eduardo
 */
public interface ConflictGraph {
    
    public void insert(Request request) throws InterruptedException;

    public Request nextRequest() throws InterruptedException;

    public void remove(Request request) throws InterruptedException;

    public void clear();
    
    public int countRequests();
    
}
