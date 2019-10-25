/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

import java.util.concurrent.atomic.AtomicBoolean;
import parallelism.late.CBASEScheduler;

/**
 *
 * @author eduardo
 */
public class LockFreeGraph extends DependencyGraph {

    public LockFreeGraph(int limit, CBASEScheduler scheduler) {
        super(limit, scheduler);

        head = new LockFreeNode(null, Vertex.HEAD);
        tail = new LockFreeNode(null, Vertex.TAIL);
        head.setNext(tail);

        System.out.println("Configured with lockFree graph.");
    }

    @Override
    public int COSInsert(Object request) throws InterruptedException {

        LockFreeNode newvNode = new LockFreeNode(request, Vertex.MESSAGE);
        LockFreeNode aux = (LockFreeNode) head;
        LockFreeNode aux2 = (LockFreeNode) aux.getNext();

        while (aux2.getVertex() != Vertex.TAIL) {
            //HELPED REMOVE
            while (aux2.removedAtomic.get()) {            // aux2 was removed, have to help
                //Se quiser da pra remover aux2 das listas depOn de quem depende dele -- poderia melhorar o testReady
                aux.setNext(aux2.getNext());        // bypass it on the linked list
                aux2 = (LockFreeNode) aux.getNext();               // proceed with aux2 to next node
            }                                       // this helps removing several consecutive marked to remove
            // in the limit case, aux2 is tail
            if ((aux.getVertex() != Vertex.HEAD)
                    && isDependent(newvNode.getAsRequest(), aux.getAsRequest())) {//if node conflicts
                //newvNode.dependsMore();                    // new node depends on one more
                newvNode.insertDepOn(aux);
                aux.insert(newvNode);  		               // add edge from older to newer

            }
            if (aux2.getVertex() != Vertex.TAIL) {
                aux2 = (LockFreeNode) aux2.getNext();
                aux = (LockFreeNode) aux.getNext();
            }
        }
        if ((aux.getVertex() != Vertex.HEAD)
                && isDependent(newvNode.getAsRequest(), aux.getAsRequest())) { //if node conflicts
            //newvNode.dependsMore(); // new node depends on one more
            newvNode.insertDepOn(aux);
            aux.insert(newvNode);

        }                                                  // added all needed edges TO new node
        newvNode.setNext(tail);                            // at the end of the list
        aux.setNext(newvNode);                             // insert new node
        
        
        newvNode.inserted = true;
        return newvNode.testReady();
    }

    @Override
    public Object COSGet() throws InterruptedException {
        LockFreeNode aux = (LockFreeNode) head;
        boolean found = false;
        while (!found) {
            if(aux.getVertex() == Vertex.TAIL){
                //System.out.println(Thread.currentThread().getId()+" TAIL,TAIL,TAIL,TAIL,TAIL,TAIL,TAIL,TAIL,TAIL,TAIL,TAIL,TAIL");
                aux = (LockFreeNode) head;
            }
            
            aux = (LockFreeNode) aux.getNext();
            /*if(aux == null){
                aux = (LockFreeNode) head;
            }else*/ if (aux.readyAtomic.get()) { //was marked as ready
                found = aux.reservedAtomic.compareAndSet(false, true);  // atomically set to reserve for exec
            }
        }
        return aux;
    }

    @Override
    public int COSRemove(Object o) throws InterruptedException {
        LockFreeNode data = ((LockFreeNode) o);
        //DUVIDA: acredito que não precisa ser atomico!
        data.removedAtomic.compareAndSet(false, true);
        return data.testDepMeReady();
    }

    public class LockFreeNode extends vNode {

        public AtomicBoolean reservedAtomic;
        public AtomicBoolean removedAtomic;
        public AtomicBoolean readyAtomic;

        public boolean inserted = false;
        
        eNode headDepOn;
        eNode tailDepOn;

        public LockFreeNode(Object data, Vertex vertex) {
            super(data, vertex);
            reservedAtomic = new AtomicBoolean(false);
            removedAtomic = new AtomicBoolean(false);
            readyAtomic = new AtomicBoolean(false);

            headDepOn = new eNode(null, Vertex.HEAD);
            tailDepOn = new eNode(null, Vertex.TAIL);
            headDepOn.setNext(tailDepOn);

        }

        public void insertDepOn(LockFreeNode newNode) {
            eNode neweNode = new eNode(newNode, Vertex.MESSAGE);
            eNode aux = headDepOn;
            while (aux.getNext().getVertex() != Vertex.TAIL) {
                aux = aux.getNext();
            }
            
            neweNode.setNext(tailDepOn);
            aux.setNext(neweNode);
            
        }

        public int testDepMeReady() throws InterruptedException {
            int freeNodes = 0;
            eNode ni = head.getNext();
            while (ni.getVertex() != Vertex.TAIL) {
                freeNodes = freeNodes + ((LockFreeNode)ni.getDependentVNode()).testReady();
                ni = ni.getNext();
            }
            return freeNodes;
        }

        public int testReady() {
            if(!inserted){
                return 0;
            }
            
            eNode ni = headDepOn.getNext();
            while (ni.getVertex() != Vertex.TAIL) {
                if (!((LockFreeNode) ni.getDependentVNode()).removedAtomic.get()) {//not removed
                    return 0;
                }
                ni = ni.getNext();
            }
            if (readyAtomic.compareAndSet(false, true)) { //it is necessary to return true only once
                return 1;
            }
            return 0;
        }

    }

}
