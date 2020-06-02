/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import parallelism.late.CBASEScheduler;

/**
 *
 * @author eduardo
 */
public class FineGrainedLock extends DependencyGraph{

   // private Lock mutex = new ReentrantLock();     // used to serialize only insertions
    
    
    public FineGrainedLock(int limit, CBASEScheduler scheduler) {
        super(limit, scheduler, null);
        System.out.println("Configured with fineLock graph.");
    }

    
    
    @Override
    public int COSInsert(Object request) throws InterruptedException {
        int freeNode = 0;
        //mutex.lock();                           // insertions are mutually exclusive among them
       // try {
            vNode newvNode = new vNode(request, Vertex.MESSAGE);
            newvNode.getLock().lock();         // lock node being inserted
            vNode aux = head;                  // start HOH on ordered list of nodes
            aux.getLock().lock();
            while (aux.getNext().getVertex() != Vertex.TAIL) {
                if ((aux.getVertex() != Vertex.HEAD)
                        && isDependent(newvNode.getAsRequest(), aux.getAsRequest())) {      // if node conflicts
                    newvNode.dependsMore();                    // new node depends on one more
                    aux.insert(newvNode);  		             // add edge from older to newer
                }
                vNode temp = aux;                              // HOH steps
                aux = aux.getNext();
                aux.getLock().lock();
                temp.getLock().unlock();
                //O codigo de baixo faz a mesma coisa???
                //aux.getNext().getLock().lock();
                //aux.getLock().unlock();
                //aux = aux.getNext();
                
                
            }
            if ((aux.getVertex() != Vertex.HEAD)
                    && isDependent(newvNode.getAsRequest(), aux.getAsRequest())){    //repeat for last node
                newvNode.dependsMore();
                aux.insert(newvNode);
            }                                                  // added all needed edges TO new node
            aux.setNext(newvNode);                             // insert new node
            newvNode.setNext(tail);                            // at the end of the list
            aux.getLock().unlock();                            // finalize HOH
            newvNode.getLock().unlock();
            if (newvNode.getDepends() == 0) {                   // if it is free to execute, signal
                freeNode = 1; //inserted element is ready to execute
            }
        //} finally {
       //     mutex.unlock();
       // }      // release lock to next insertion
        return freeNode;
    }

    @Override
    public Object COSGet() throws InterruptedException {
        vNode aux = head;
        vNode passed;
        aux.getLock().lock();
        while (aux.getVertex() != Vertex.TAIL) {
            passed = aux;
            aux = aux.getNext();
            aux.getLock().lock();
            passed.getLock().unlock();
            if (!aux.isreserved() && aux.getDepends() == 0
                    && (aux.getVertex() != Vertex.TAIL)) {
                aux.reserved();
                aux.getLock().unlock();
                return aux;
            }
        }
        aux.getLock().unlock();
        return null;                           // this should never happen
    }

    @Override
    public int COSRemove(Object o) throws InterruptedException {
        int freeNodes = 0;
        vNode data = ((vNode) o);
        vNode aux = head;
        vNode passed;
        aux.getLock().lock();                               // start HOH
        aux.getNext().getLock().lock();
        while (aux.getVertex() != Vertex.TAIL) {
            if (aux.getNext() == data) {                     // finds node to remove
                vNode removed = aux.getNext();              // removed points to it
                aux.setNext(removed.getNext());             // bypass it on the linked list
                freeNodes = removed.destroyEdges(true);             // removes edges starting from
                aux.getLock().unlock();                     // finalize HOH
                removed.getLock().unlock();                 //
                break;
            } else {                                        // not font
                passed = aux;                               // HOH advances
                aux = aux.getNext();                        //
                aux.getNext().getLock().lock();             //
                passed.getLock().unlock();                  //
            }
        }
        return freeNodes;
    }
    
    
    
}
