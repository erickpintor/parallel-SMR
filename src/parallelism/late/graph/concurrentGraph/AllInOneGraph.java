/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph.concurrentGraph;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
//import parallelism.CBASE.graph.ConflictGraph;
import parallelism.late.graph.Parallelizer;
import parallelism.late.graph.Request;

public class AllInOneGraph{ // implements ConflictGraph {                   // Directed Graph

    private Parallelizer parallelizer;
    
    final private SyncType syT;                         // coarselock, hoh, hohGetWaitFree

    
    final private Lock mutex = new ReentrantLock();     // used to serialize only insertions
    // or all in case of COARSE LOCK

    final private Condition notFull = mutex.newCondition(); // only for COARSE LOCK

    private Semaphore semNotFull = null;                // counting semaphore for size of graph
    final private Semaphore semHasReady = new Semaphore(0);  // tells if there is ready to execute

    private vNode head;                                 // always exist
    private vNode tail;                                 // in the list: lower and highest
    private int limit;                                  // max size of the graph
    private int size;                                   // for COARSE LOCK

    //private float dependencyOdds;                       // dependency probability

    // ----------------------------------------------------------------------
    // --------- a vertex of the graph - graph kept as a linked list
    public class vNode {                                        // v_ertex Node

        private Vertex vertex;                          // kind of Vertex
        private Object data;                               // the item kept in the graph node
        private int depends;                            // number of nodes it depends of
        vNode next;                                     // next in the linked list
        private boolean reserved;                       // if reserved for execution
        public AtomicBoolean reservedAtomic;
        public AtomicBoolean removedAtomic;
        eNode head;
        eNode tail;

        final private ReentrantLock lock;
        Random r = new Random();

        // --------- eNodes are references to nodes that depend on this vNode
        class eNode {                                    // e_dge Node

            private Vertex vertex;                      // kind of Vertex
            vNode whoIAm;                               // ref to one vNode that depends of this vNode
            eNode next;

            public eNode(vNode whoYouAre, Vertex v) {
                whoIAm = whoYouAre;
                this.vertex = v;
                next = null;
            }

            // return type of node (head, tail, ...)
            public Vertex getVertex() {
                return vertex;
            }

            // refer to vNode this eNodes represents
            public vNode getDependentVNode() {
                return whoIAm;
            }

            public eNode getNext() {
                return next;
            }

            public void setNext(eNode next) {
                this.next = next;
            }
        }
        // --------- end of eNode class -------------------------------------

        public vNode(Object data, Vertex vertex) {
            this.data = data;                           // DATA and kind kept
            this.vertex = vertex;                       //
            this.next = null;                           // LINKING
            lock = new ReentrantLock();                 // LOCK
            depends = 0;                                // nodes THIS NODE DEPENDS OF
            head = new eNode(null, Vertex.HEAD);        // empty list of nodes that
            tail = new eNode(null, Vertex.TAIL);        // DEPEND FROM THIS NODE
            head.setNext(tail);                         //
            reserved = false;                          // not reserved for execution
            reservedAtomic = new AtomicBoolean(false);
            removedAtomic = new AtomicBoolean(false);
        }

        // DATA kept in node and kind of node (head, tail, message)
        public Request getAsRequest() {
            return (Request) data;
        }

        public Object getData() {
            return data;
        }
        
        
        public Vertex getVertex() {
            return vertex;
        }

        // LINKING info and setting
        public void setNext(vNode next) {
            this.next = next;
        }

        public vNode getNext() {
            return next;
        }

        // LOCK: returns the lock - but does not lock it
        public Lock getLock() {
            return lock;
        }

        // DEPENDENCIES: NODES THAT THIS NODE DEPENDS OF
        public void dependsMore() {
            depends++;
        }           // one more node

        public int getDepends() {
            return depends;
        }    // return number of nodes this depends

        public void dependsLess() {                          // one node this depends was removed
            depends--;
            if (depends == 0) {
                semHasReady.release();
            }       // if became free, signal!
        }

        // DEPENDENCIES: NODES THAT DEPEND FROM THIS ONE
        public eNode getDependents() {
            return head;
        }       // the list of dependent nodes

        // adds one more node that depends of this one
        public void insert(vNode newNode) {
            eNode neweNode = new eNode(newNode, Vertex.MESSAGE);
            eNode aux = head;
            while (aux.getNext().getVertex() != Vertex.TAIL) {
                aux = aux.getNext();
            }
            aux.setNext(neweNode);
            neweNode.setNext(tail);
        }

        public void destroyEdges(SyncType st) {
            if (st == SyncType.coarseLock) {
                coarseLockdestroyEdges();
            } else if (st == SyncType.fineLock) {
                hohDestroyEdges();
            } else if (st == SyncType.lockFree) {
                coarseLockdestroyEdges();
            } else {
                hohDestroyEdges();
            };
        }

        // FINE GRAINED DESTROY EDGES
        // when this node is removed, all edges pointing to nodes that depends from this
        // one are removed too
        public void hohDestroyEdges() {
            eNode aux = head;
            while (aux.getVertex() != Vertex.TAIL) {
                if (aux.getVertex() != Vertex.HEAD) {
                    aux.getDependentVNode().getLock().lock();
                    aux.getDependentVNode().dependsLess();
                    aux.getDependentVNode().getLock().unlock();
                }
                aux = aux.getNext();
            }
        }

        // COARSE LOCK DESTROY EDGES
        public void coarseLockdestroyEdges() {
            eNode aux = head;

            while (aux.getVertex() != Vertex.TAIL) {
                if (aux.getVertex() != Vertex.HEAD) {
                    aux.getDependentVNode().dependsLess();
                }
                aux = aux.getNext();
            }
        }

        // random conflict
      /*  public boolean isDependent(Object otherData) {

            if (otherData instanceof Message && this.data instanceof Message) {
                Message dataM = (Message) otherData;
                Message thisDataM = (Message) this.data;

                if (dataM.getType() == MessageType.READ && thisDataM.getType() != MessageType.WRITE) {
                    return false;
                } else {
                    return true;
                }
            }

            return false;

        }*/

        // RESERVE to execute:  consult and set
        public boolean isreserved() {
            return reserved;
        }

        public void reserved() {
            reserved = true;
        }

        // AUX methods
        public String printe() {
            String l = " ";
            eNode aux2 = head.getNext();
            while (aux2.getVertex() != Vertex.TAIL) {
                l = l + aux2.getDependentVNode().getData() + ",";
                aux2 = aux2.getNext();
            }
            return l;
        }

    }
    // --------- end of class vNode
    // ----------------------------------------

    public AllInOneGraph(int limit, Parallelizer parallelizer, String st) {

         this.parallelizer = parallelizer;
        
        this.limit = limit;
        this.size = 0;
        

        semNotFull = new Semaphore(limit, true);

        head = new vNode(null, Vertex.HEAD);
        tail = new vNode(null, Vertex.TAIL);

        head.setNext(tail);

        if (st.equals("coarselock")) {
            this.syT = SyncType.coarseLock;
        } else if (st.equals("fineLock")) {
            this.syT = SyncType.fineLock;
        } else if (st.equals("hohGetWaitFree")) {
            this.syT = SyncType.hohGetWaitFree;
        } else if (st.equals("hohHelpedRemove")) {
            this.syT = SyncType.hohHelpedRemove;
        } else if (st.equals("helpedRemoveGetWaitFree")) {
            this.syT = SyncType.helpedRemoveGetWaitFree;
        } else if (st.equals("lockFree")) {
            this.syT = SyncType.lockFree;
        } else {
            this.syT = SyncType.hohHelpedRemove;
        }
    }
    
    public int countRequests() {
        return size;
    }
    
    public void clear() {
        head = null;
        tail = null;
    }

    // INSERT METHOD -----------------------------------------------------------------------
    public void insert(Object data) throws InterruptedException {
        if (this.syT == SyncType.coarseLock) {
            coarseLockInsert(data);
        } else if (this.syT == SyncType.fineLock) {
            hohInsert(data);
        } else if (this.syT == SyncType.hohGetWaitFree) {
            hohInsert(data);
        } else if (this.syT == SyncType.hohHelpedRemove) {
            hohHelpedRemoveInsert(data);
        } else if (this.syT == SyncType.helpedRemoveGetWaitFree) {
            hohHelpedRemoveInsert(data);
        } else if (this.syT == SyncType.lockFree) {
            helpedRemoveLockFreeInsert(data);
        }
    }

    // INSERT for coarse lock
    private void coarseLockInsert(Object request) throws InterruptedException {
        mutex.lock();
        try {
            while (this.size() == limit) {
                notFull.await();
            }       // use of monitor condition
            vNode newVnode = new vNode(request, Vertex.MESSAGE);
            vNode aux = head;
            while (aux.getNext().getVertex() != Vertex.TAIL) {
                //if (this.parallelizer.isDependent(newVnode.getRequest(), aux.getRequest()) && (aux.getVertex() != Vertex.HEAD)) {
                if ((aux.getVertex() != Vertex.HEAD) && this.parallelizer.isDependent(newVnode.getAsRequest(), aux.getAsRequest())) {
                    newVnode.dependsMore();
                    aux.insert(newVnode);
                }
                aux = aux.getNext();
            }
            //if (this.parallelizer.isDependent(newVnode.getRequest(), aux.getRequest()) && (aux.getVertex() != Vertex.HEAD)) {// ultimo elemento
            if ((aux.getVertex() != Vertex.HEAD) && this.parallelizer.isDependent(newVnode.getAsRequest(), aux.getAsRequest())) {
                newVnode.dependsMore();
                aux.insert(newVnode);
            }
            aux.setNext(newVnode);
            newVnode.setNext(tail);
            if (newVnode.getDepends() == 0) {
                semHasReady.release();
            }
            this.size++;
        } finally {
            mutex.unlock();
        }
    }

    // INSERT for hoh
    private void hohInsert(Object request) throws InterruptedException {
        mutex.lock();                           // insertions are mutually exclusive among them
        try {
            semNotFull.acquire();
            vNode newvNode = new vNode(request, Vertex.MESSAGE);
            newvNode.getLock().lock();         // lock node being inserted
            vNode aux = head;                  // start HOH on ordered list of nodes
            aux.getLock().lock();
            while (aux.getNext().getVertex() != Vertex.TAIL) {
                if ((aux.getVertex() != Vertex.HEAD)
                        && this.parallelizer.isDependent(newvNode.getAsRequest(), aux.getAsRequest())) {      // if node conflicts
                    newvNode.dependsMore();                    // new node depends on one more
                    aux.insert(newvNode);  		             // add edge from older to newer
                }
                vNode temp = aux;                              // HOH steps
                aux = aux.getNext();
                aux.getLock().lock();
                temp.getLock().unlock();
            }
            if ((aux.getVertex() != Vertex.HEAD)
                    && this.parallelizer.isDependent(newvNode.getAsRequest(), aux.getAsRequest())){    //repeat for last node
                newvNode.dependsMore();
                aux.insert(newvNode);
            }                                                  // added all needed edges TO new node
            aux.setNext(newvNode);                             // insert new node
            newvNode.setNext(tail);                            // at the end of the list
            aux.getLock().unlock();                            // finalize HOH
            if (newvNode.getDepends() == 0) {                   // if it is free to execute, signal
                semHasReady.release();
            }
            newvNode.getLock().unlock();
        } finally {
            mutex.unlock();
        }      // release lock to next insertion
    }

    // INSERT for hoh helped remove insert
    private void hohHelpedRemoveInsert(Object request) throws InterruptedException {
        mutex.lock();                           // insertions are mutually exclusive among them
        try {
            semNotFull.acquire();
            vNode newvNode = new vNode(request, Vertex.MESSAGE);
            newvNode.getLock().lock();         // lock node being inserted

            vNode aux = head;                  // start HOH on ordered list of nodes
            aux.getLock().lock();
            aux.getNext().getLock().lock();
            vNode aux2 = aux.getNext();                 // aux2 is locked

            while (aux2.getVertex() != Vertex.TAIL) {
                while (aux2.removedAtomic.get()) {            // aux2 was removed, have to help
                    aux2.destroyEdges(this.syT);        // removes edges
                    aux.setNext(aux2.getNext());        // bypass it on the linked list
                    aux.getNext().getLock().lock();     // lock next one
                    aux2.getLock().unlock();            // unlock removed
                    aux2 = aux.getNext();               // proceed with aux2 to next node, locked
                }                                       // this helps removing several consecutive marked to remove
                // in the limit case, aux2 is tail

                if ((aux.getVertex() != Vertex.HEAD)
                        && this.parallelizer.isDependent(newvNode.getAsRequest(), aux.getAsRequest())){//if node conflicts
                    newvNode.dependsMore();                    // new node depends on one more
                    aux.insert(newvNode);  		               // add edge from older to newer
                }

                if (aux2.getVertex() != Vertex.TAIL) {
                    vNode temp = aux;
                    aux2.getNext().getLock().lock();
                    aux2 = aux2.getNext();
                    aux = aux.getNext();
                    temp.getLock().unlock();
                }
            }
            if ((aux.getVertex() != Vertex.HEAD)
                    && this.parallelizer.isDependent(newvNode.getAsRequest(), aux.getAsRequest())){ //if node conflicts
                newvNode.dependsMore();
                aux.insert(newvNode);
            }                                                  // added all needed edges TO new node
            aux.setNext(newvNode);                             // insert new node
            newvNode.setNext(tail);                            // at the end of the list
            aux.getLock().unlock();                            // finalize HOH
            aux2.getLock().unlock();                           // aux2 is tail here
            if (newvNode.getDepends() == 0) {                   // if it is free to execute, signal
                semHasReady.release();
            }
            newvNode.getLock().unlock();
        } finally {
            mutex.unlock();
        }      // release lock to next insertion
    }

    private void helpedRemoveLockFreeInsert(Object request) throws InterruptedException {
       // System.out.println("chamou aqui !");
        mutex.lock();                           // insertions are mutually exclusive among them
        try {
            semNotFull.acquire();
            vNode newvNode = new vNode(request, Vertex.MESSAGE);
            //newvNode.getLock().lock();         // lock node being inserted

            vNode aux = head;                  // start HOH on ordered list of nodes
            // aux.getLock().lock();
            // aux.getNext().getLock().lock();
            vNode aux2 = aux.getNext();                 // aux2 is locked

            while (aux2.getVertex() != Vertex.TAIL) {
                while (aux2.removedAtomic.get()) {            // aux2 was removed, have to help
                    aux2.destroyEdges(this.syT);        // removes edges
                    aux.setNext(aux2.getNext());        // bypass it on the linked list
                    // aux.getNext().getLock().lock();     // lock next one
                    // aux2.getLock().unlock();            // unlock removed
                    aux2 = aux.getNext();               // proceed with aux2 to next node, locked
                }                                       // this helps removing several consecutive marked to remove
                // in the limit case, aux2 is tail

                if ((aux.getVertex() != Vertex.HEAD)
                        && this.parallelizer.isDependent(newvNode.getAsRequest(), aux.getAsRequest())){//if node conflicts
                    newvNode.dependsMore();                    // new node depends on one more
                    aux.insert(newvNode);  		               // add edge from older to newer
                }

                if (aux2.getVertex() != Vertex.TAIL) {
                    vNode temp = aux;
                    //aux2.getNext().getLock().lock();
                    aux2 = aux2.getNext();
                    aux = aux.getNext();
                    //temp.getLock().unlock();
                }
            }
            if ((aux.getVertex() != Vertex.HEAD)
                    && this.parallelizer.isDependent(newvNode.getAsRequest(), aux.getAsRequest())){ //if node conflicts
                newvNode.dependsMore();
                aux.insert(newvNode);
            }                                                  // added all needed edges TO new node
            aux.setNext(newvNode);                             // insert new node
            newvNode.setNext(tail);                            // at the end of the list
            //aux.getLock().unlock();                            // finalize HOH
            //aux2.getLock().unlock();                           // aux2 is tail here
            if (newvNode.getDepends() == 0) {                   // if it is free to execute, signal
                semHasReady.release();
            }
            //newvNode.getLock().unlock();
        } finally {
            mutex.unlock();
        }      // release lock to next insertion
    }

    // GET METHOD -----------------------------------------------------------------------
    public Object get() throws InterruptedException {
        if (this.syT == SyncType.coarseLock) {
            return coarseLockGet();
        } else if (this.syT == SyncType.fineLock) {
            return hohGet();
        } else if (this.syT == SyncType.hohGetWaitFree) {
            return hohWaitFreeGet();
        } else if (this.syT == SyncType.hohHelpedRemove) {
            return hohGet();
        } else if (this.syT == SyncType.helpedRemoveGetWaitFree) {
            return hohWaitFreeGet();
        } else if (this.syT == SyncType.lockFree) {
            return hohWaitFreeGet();
        } else {
            return null;
        }
    }

    private Object coarseLockGet() throws InterruptedException {
        semHasReady.acquire();
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
        return null;                          // this should never happen
    }

    // searches for node to execute - HOH version
    private Object hohGet() throws InterruptedException {
        semHasReady.acquire();
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

    // searches for node to execute - lockFree version
    private Object hohWaitFreeGet() throws InterruptedException {
        semHasReady.acquire();              // wait for some ready - eventually exists
        vNode aux = head;
        boolean found = false;
        while (!found) {
            aux = aux.getNext();
            if (aux.getDepends() == 0) {
                found = aux.reservedAtomic.compareAndSet(false, true);  // atomically set to reserve for exec
            }
        }
        return aux;
    }

    // REMOVE METHOD -----------------------------------------------------------------------
    public void remove(Object data) throws InterruptedException {
        if (this.syT == SyncType.coarseLock) {
            coarseLockRemove(data);
        } else if (this.syT == SyncType.fineLock) {
            hohRemove(data);
        } else if (this.syT == SyncType.hohGetWaitFree) {
            hohRemove(data);
        } else if (this.syT == SyncType.hohHelpedRemove) {
            hohHelpedRemove(data);
        } else if (this.syT == SyncType.helpedRemoveGetWaitFree) {
            hohHelpedRemove(data);
        } else if (this.syT == SyncType.lockFree) {
            hohHelpedRemove(data);
        }
    }

    //a thread que processou um nodo chama esse metodo para procurar o nodo que ela processou
    //e remover esse nodo da lista
    private void coarseLockRemove(Object o) throws InterruptedException {
        vNode data = ((vNode) o);
        mutex.lock();
        try {
            vNode aux = head;
            while (aux.getVertex() != Vertex.TAIL) {
                if (aux.getNext() == data) {
                    vNode removed = aux.getNext();   // removed aponta para nodo a ser removido
                    removed.destroyEdges(this.syT); //diminui contadores de dependencia de todos os nodos da lista de dependencias do nodo removido
                    aux.setNext(removed.getNext());
                    this.size--;
                    notFull.signal();
                    break;
                } else {
                    aux = aux.getNext();
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    //a thread que processou um nodo chama esse metodo para procurar o nodo que ela processou
    //e remover esse nodo da lista
    private void hohRemove(Object o) throws InterruptedException {
        vNode data = ((vNode) o);
        vNode aux = head;
        vNode passed;
        aux.getLock().lock();                               // start HOH
        aux.getNext().getLock().lock();
        while (aux.getVertex() != Vertex.TAIL) {
            if (aux.getNext() == data) {                     // finds node to remove
                vNode removed = aux.getNext();              // removed points to it
                removed.destroyEdges(this.syT);             // removes edges starting from
                aux.setNext(removed.getNext());             // bypass it on the linked list
                semNotFull.release();                       // one more space
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
    }

    // hoh helped remove
    private void hohHelpedRemove(Object o) throws InterruptedException {
        vNode data = ((vNode) o);
        data.removedAtomic.compareAndSet(false, true);
        semNotFull.release();                       // one more space
    }

    // ---------------------------------------
    public int size() {
        return size;
    }

    // ------------ AUXILIARY METHODS ------------------------------------------------------
    public String printVL() {
        String list = "Head ";
        vNode aux = head.getNext();
        while (aux.getVertex() != Vertex.TAIL) {
            list = list + " " + aux.getData() + "<" + aux.getDepends() + ">";
//			list = list + "["+aux.printe()+"]";
            aux = aux.getNext();
        }
        list = list + " Tail";
        return list;
    }
}
