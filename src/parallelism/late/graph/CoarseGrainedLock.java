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
public class CoarseGrainedLock extends DependencyGraph{

    private Lock mutex = new ReentrantLock();
    
    
    
    public CoarseGrainedLock(int limit, CBASEScheduler scheduler) {
        super(limit,scheduler, null);
        System.out.println("Configured with coarseLock graph.");
    }

    
    @Override
    public int COSInsert(Object request) throws InterruptedException {
        int freeNode = 0;
        mutex.lock();
        try {
                   // use of monitor condition
            vNode newVnode = new vNode(request, Vertex.MESSAGE);
            vNode aux = head;
            while (aux.getNext().getVertex() != Vertex.TAIL) {
                if ((aux.getVertex() != Vertex.HEAD) && isDependent(newVnode.getAsRequest(), aux.getAsRequest())) {
                    newVnode.dependsMore();
                    aux.insert(newVnode);
                }
                aux = aux.getNext();
            }
            if ((aux.getVertex() != Vertex.HEAD) && isDependent(newVnode.getAsRequest(), aux.getAsRequest())) {
                newVnode.dependsMore();
                aux.insert(newVnode);
            }
            aux.setNext(newVnode);
            newVnode.setNext(tail);
            if (newVnode.getDepends() == 0) {
                freeNode = 1; //inserted element is ready to execute
            }
        } finally {
            mutex.unlock();
        }
        return freeNode;
    }

    
    @Override
    public Object COSGet() throws InterruptedException {
        mutex.lock();
        try {
            vNode aux = head;
            while (aux.getVertex() != Vertex.TAIL) {
                aux = aux.getNext();
                if (!aux.isreserved() && aux.getDepends() == 0 && (aux.getVertex() != Vertex.TAIL)) {
                    aux.reserved();
                    return aux;
                }
            }
        } finally {
            mutex.unlock();
        }
        return null; // this should never happen
    }

    @Override
    public int COSRemove(Object o) throws InterruptedException {
        vNode data = ((vNode) o);
        int freeNodes = 0;
        mutex.lock();
        try {
            vNode aux = head;
            while (aux.getVertex() != Vertex.TAIL) {
                if (aux.getNext() == data) {
                    vNode removed = aux.getNext();   // removed aponta para nodo a ser removido
                    aux.setNext(removed.getNext());
                    freeNodes = removed.destroyEdges(false); //diminui contadores de dependencia de todos os nodos da lista de dependencias do nodo removido
                } else {
                    aux = aux.getNext();
                }
            }
        } finally {
            mutex.unlock();
        }
        return freeNodes;
    }
}
