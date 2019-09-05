/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

import parallelism.late.ConflictDefinition;
import parallelism.late.DefaultConflictDefinition;
import parallelism.late.graph.concurrentGraph.AllInOneGraph;
import parallelism.late.graph.concurrentGraph.ConcGraph;
import parallelism.late.graph.syncGraph.SyncGraph;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;

/**
 *
 * @author eduardo
 */
public class Parallelizer {

    private ConflictDefinition conflictDef;
   // private ConflictGraph graph;
     private AllInOneGraph graph;
    
    private int counter = 0;
    
    public Parallelizer(int limit, String graphType){
        this(limit, graphType, null);
    }
    
    public Parallelizer(int limit, String graphType, ConflictDefinition conflictDef) {
        if(conflictDef == null){
            this.conflictDef = new DefaultConflictDefinition();
        }else{
            this.conflictDef = conflictDef;
        }
        
        //if(concGraph){
            //graph = new ConcGraph(limit, this);
           // String type = "hoh";
            graph = new AllInOneGraph(limit, this,graphType);
            System.out.println("Configured with "+graphType+" graph.");
        /*}else{
            graph = new SyncGraph(limit, this);
            System.out.println("Configured with synch graph.");
        }*/
        //graph = new VList(limit, this);
        
    } 
     
     
    
    public boolean isDependent(Request thisRequest, Request otherRequest){
       
        
        if(thisRequest.getRequest().classId == ParallelMapping.CONFLICT_RECONFIGURATION || 
                otherRequest.getRequest().classId == ParallelMapping.CONFLICT_RECONFIGURATION){
            return true;
        }
        
        return this.conflictDef.isDependent(thisRequest.getRequest(), otherRequest.getRequest());
        
    }
    
    public void insert(MessageContextPair request) throws InterruptedException {
        //Request req = new Request(ExecState.ready, request, counter);
        Request req = new Request(request, counter);
        graph.insert(req);
        counter++;
    }
    
     
    
    public boolean remove(Object requestNode) throws InterruptedException {
        //System.out.println(String.format("removing %d", request.getRequestId()));
        graph.remove(requestNode);
        return true;
    }
    


    public Object nextRequest() throws InterruptedException {
        //System.out.println("processing next request");
        return graph.get();
    }



    public void clear(){
        this.graph.clear();
    }

    public int countRequests(){
        return this.graph.countRequests();
    }
}