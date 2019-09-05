/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph.concurrentGraph;



import parallelism.late.graph.Request;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import parallelism.late.graph.ConflictGraph;
import parallelism.late.graph.Parallelizer;

public class ConcGraph implements ConflictGraph {

    //final private Lock mutex = new ReentrantLock();
    final private Semaphore semHasReady = new Semaphore(0);
    private vnode head;
    private vnode tail;
    
    private Semaphore sem = null;
    //private float dependencyOdds;
    private int size;
    
    private Parallelizer parallelizer;

    public ConcGraph(int limit, Parallelizer parallelizer) {

        this.parallelizer = parallelizer;
    

        sem = new Semaphore(limit, true);

        head = new vnode(null, Vertex.HEAD);
        tail = new vnode(null, Vertex.TAIL);

        head.setNext(tail);
    }

    
    //adiciona um novo dado na lista para ser processado
    public void insert(Request request) throws InterruptedException {

        //mutex.lock();
        //try {

            sem.acquire();
            vnode newVnode = new vnode(request, Vertex.MESSAGE);

            newVnode.getLock().lock();
            vnode aux = head;

            aux.getLock().lock();
            while (aux.getNext().getVertex() != Vertex.TAIL) {


                //if ((aux.getVertex() != Vertex.HEAD) && (aux.getVertex() != Vertex.TAIL) && (newVnode.isDependent(aux.getData()))) { //checar se nodo que sera inserido depende do que esta sendo olhado
                if ((aux.getVertex() != Vertex.HEAD) && (aux.getVertex() != Vertex.TAIL) && 
                        this.parallelizer.isDependent(newVnode.getRequest(), aux.getRequest())) { //checar se nodo que sera inserido depende do que esta sendo olhado  
                    newVnode.dependsMore();
                    aux.insert(newVnode);
                }

                vnode temp = aux;
                aux = aux.getNext();
                aux.getLock().lock();
                temp.getLock().unlock();
            }

            // aux.getLock().lock();

            if ((aux.getVertex() != Vertex.HEAD) && (aux.getVertex() != Vertex.TAIL) &&
                    this.parallelizer.isDependent(newVnode.getRequest(), aux.getRequest())) { //checar se nodo que sera inserido depende do que esta sendo olhado  { //checar se nodo que sera inserido depende do que esta sendo olhado
                newVnode.dependsMore();
                aux.insert(newVnode);
            }

            aux.setNext(newVnode);
            newVnode.setNext(tail);

            aux.getLock().unlock();

            if (newVnode.getDepends() == 0) {
                semHasReady.release();
            }

            newVnode.getLock().unlock();
            size++;

       /* } finally {
            mutex.unlock();
        }*/
    }

    //procura na lista por algum dado para processar
    
    public Request nextRequest() throws InterruptedException {

        semHasReady.acquire();

        vnode aux = head;
        vnode passed;

        aux.getLock().lock();
        while (aux.getVertex() != Vertex.TAIL) {
                
                passed = aux;
                aux = aux.getNext();
                aux.getLock().lock();
                passed.getLock().unlock();
                
                //try {
                if (!aux.isRemoved() && aux.getDepends() == 0 && (aux.getVertex() != Vertex.TAIL)) {
                    aux.removed();
                    aux.getLock().unlock();
                    
                    return aux.getRequest();
                }
                
                //} finally {
                //}
        }
        
        aux.getLock().unlock();
        return null;

    }

    public int countRequests() {
        return size;
    }

    public void clear() {
        head = null;
        tail = null;
    }

    //a thread que processou um nodo chama esse metodo para procurar o nodo que ela processou
    //e remover esse nodo da lista
    @Override
    public void remove(Request request) {
        //mutex.lock();
        //try{
        
        vnode aux = head;
        vnode passed;

        aux.getLock().lock();
        aux.getNext().getLock().lock();

        while (aux.getVertex() != Vertex.TAIL) {
            if (aux.getNext().getRequestId() == request.getRequestId()) {

                vnode removed = aux.getNext();   // removed aponta para nodo a ser removido

                // aux.getLock().lock();
                // removed.getLock().lock();
                //removed.getNext().getLock().lock();

                removed.destroyEdges(); //diminui contadores de dependencia de todos os nodos da lista de dependencias do nodo removido

                //try {

                aux.setNext(removed.getNext());

                //    } finally {
                sem.release();
                aux.getLock().unlock();
                removed.getLock().unlock();

                size--;

                //aux.getNext().getLock().unlock();
                // }

                break;

            } else {
                passed = aux;
                aux = aux.getNext();
                aux.getNext().getLock().lock();
                passed.getLock().unlock();
            }


        }
        //} finally {
        // mutex.unlock();
        //}
    }

    
    public String printVL() {

        String list = "Head ";
        vnode aux = head.getNext();
        while (aux.getVertex() != Vertex.TAIL) {
            list = list + " " + aux.getRequestId() + "<" + aux.getDepends() + ">";
//			list = list + "["+aux.printe()+"]";
            aux = aux.getNext();

        }
        list = list + " Tail";

        return list;
    }

    class vnode {

        // final private Lock lock;
        final private ReentrantLock lock;
        vnode next;
        boolean removed;
        enode head;
        enode tail;
        //Random r = new Random();
        private Vertex vertex;
        private Request request;
        private int depends;

        public vnode(Request request, Vertex vertex) {

            this.request = request;
            this.vertex = vertex;
            depends = 0;
            head = new enode(null, Vertex.HEAD);
            tail = new enode(null, Vertex.TAIL);
            head.setNext(tail);

            removed = false;
            lock = new ReentrantLock();
        }

        //aumenta a dependencia desse vnode, precisa esperar retirar mais nodos da lista para poder ser processado
        public void dependsMore() {
            depends++;
        }

        //diminui a dependencia desse vnode, algum nodo que ele dependia foi removido da lista
        public void dependsLess() {
            depends--;
            if (depends == 0) {
                semHasReady.release();
            }
        }

        //retorna o inicio da lista de dependentes (quem depende desse vnode)
        public enode getDependents() {
            return head;
        }

        //retorna a quantidade de nodos que esse nodo depende
        public int getDepends() {
            return depends;
        }

        //retorna o enumerador que diz se esse vnode ee Head, Tail ou Message da lista
        public Vertex getVertex() {
            return vertex;
        }

        /*public boolean isDependent(int otherData) {
            float random = r.nextFloat();
            return random <= dependencyOdds;

        }*/

        public Lock getLock() {
            return lock;
        }

        //dado que sera processado, que o nodo guarda
        public int getRequestId() {
            return request.getRequestId();
        }

        public Request getRequest(){
            return request;
        }

        public vnode getNext() {
            return next;
        }

        public void setNext(vnode next) {
            this.next = next;
        }

        //boolean que diz se esse nodo ja foi processado
        public boolean isRemoved() {
            return removed;
        }

        //quando esse nodo ee pego para ser processado ee chamado esse metodo
        public void removed() {
            removed = true;
        }

        //quando esse nodo ee removido da lista de vertices destroyedges ee chamado
        //diminui 1 da quantidade de dependencias de todos os dependentes desse nodo
        public void destroyEdges() {
            enode aux = head;

            while (aux.getVertex() != Vertex.TAIL) {
                if (aux.getVertex() != Vertex.HEAD) {
                    aux.getMe().getLock().lock();
                    try {
                        aux.getMe().dependsLess();
                    } finally {
                        aux.getMe().getLock().unlock();
                    }
                }
                aux = aux.getNext();
            }
        }

        //adiciona mais um enode para a lista de arestas desse nodo
        public void insert(vnode newNode) {
            enode newEnode = new enode(newNode, Vertex.MESSAGE);

            enode aux = head;

            while (aux.getNext().getVertex() != Vertex.TAIL) {
                aux = aux.getNext();
            }

            aux.setNext(newEnode);
            newEnode.setNext(tail);
        }

        public String printe() {
            String l = " ";
            enode aux2 = head.getNext();
            while (aux2.getVertex() != Vertex.TAIL) {
                l = l + aux2.getMe().getRequestId() + ",";
                aux2 = aux2.getNext();
            }
            return l;
        }

        class enode {
            vnode whoIAm;
            enode next;
            private Vertex vertex;

            public enode(vnode whoYouAre, Vertex vertex) {
                whoIAm = whoYouAre;
                this.vertex = vertex;
                next = null;
            }

            //retorna o enumerador que diz se o nodo ee Head, Tail ou esta no meio da lista
            public Vertex getVertex() {
                return vertex;
            }

            //referencia para o vnode que esse enode representa
            public vnode getMe() {
                return whoIAm;
            }

            public enode getNext() {
                return next;
            }

            public void setNext(enode next) {
                this.next = next;
            }

        }

    }

}