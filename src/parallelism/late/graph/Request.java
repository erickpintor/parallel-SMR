/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

import java.util.ArrayList;
import parallelism.MessageContextPair;

/**
 * Created by ruda on 01/09/16.
 */


public class Request {

    private int rid;
    private MessageContextPair request;
    //private ExecState state;
    private ArrayList<Request> dlist;
    private ArrayList<Request> olist;

    //public Request(ExecState state, MessageContextPair request, int rid) {
    public Request(MessageContextPair request, int rid) {
      
        //this.state = state;
        this.request = request;
        this.rid = rid;
        dlist = new ArrayList<Request>();
        olist = new ArrayList<Request>();
    }

    public int getRequestId() {
        return rid;
    }

    public MessageContextPair getRequest() {
        return this.request;
    }

    /*public ExecState getState() {
        return state;
    }

    public void setState(ExecState state) {
        this.state = state;
    }
*/
    public void addDependency(Request req) {
        dlist.add(req);
    }

    public void removeDependency(Request req) {
        dlist.remove(req);
    }

    public int countDependencies() {
        return dlist.size();
    }

    public void addDependent(Request req) {
        olist.add(req);
    }

    public void removeDependent(Request req) {
        olist.remove(req);
    }

    public int countDependents() {
        return olist.size();
    }

    public ArrayList<Request> getDependents() {
        return olist;
    }

}
