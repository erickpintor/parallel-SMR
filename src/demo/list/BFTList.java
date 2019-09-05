/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.list;



import bftsmart.tom.ParallelAsynchServiceProxy;
import bftsmart.tom.ParallelServiceProxy;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.ParallelMapping;

/**
 *
 * @author alchieri
 */
public class BFTList<V> implements List<V> {
    
    static final int CONTAINS = 1;
    static final int ADD = 2;
    static final int GET = 3;
    static final int REMOVE = 4;
    static final int SIZE = 5;

    protected ParallelServiceProxy proxy = null;
    protected ByteArrayOutputStream out = null;

    protected boolean parallel = false;

    protected boolean async = false;
    
    protected ParallelAsynchServiceProxy asyncProxy = null;
    
    
    public BFTList(int id, boolean parallelExecution, boolean async) {
       
        this.parallel = parallelExecution;
        this.async = async;
        if(async){
            asyncProxy = new ParallelAsynchServiceProxy(id);
        }else{
             proxy = new ParallelServiceProxy(id);     
        }
    }

    @Override
    public int size() {
        try {
            out = new ByteArrayOutputStream();
            new DataOutputStream(out).writeInt(SIZE);
            byte[] rep;
            
            
            
            if (parallel) {
                if(async){
                    int id = asyncProxy.invokeParallelAsynchRequest(out.toByteArray(), null, TOMMessageType.ORDERED_REQUEST, ParallelMapping.CONC_ALL);
                    asyncProxy.cleanAsynchRequest(id);
                    
                    return -1;
                }else{
                    rep = proxy.invokeParallel(out.toByteArray(), ParallelMapping.CONC_ALL);
                }
            } else {
                rep = proxy.invokeOrdered(out.toByteArray());
            }

            ByteArrayInputStream in = new ByteArrayInputStream(rep);
            int size = new DataInputStream(in).readInt();
            return size;
        } catch (IOException ex) {
            return -1;
        }
    }

    @Override
    public boolean add(V e) {
        try {
            out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(ADD);
            //dos.writeInt(key.intValue());

            ObjectOutputStream out1 = new ObjectOutputStream(out);
            out1.writeObject(e);
            out1.close();
            byte[] rep = null;
            if (parallel) {
                if(async){
                    int id = asyncProxy.invokeParallelAsynchRequest(out.toByteArray(), null, TOMMessageType.ORDERED_REQUEST, ParallelMapping.SYNC_ALL);
                    asyncProxy.cleanAsynchRequest(id);
                    return true;
                }else{
                    rep = proxy.invokeParallel(out.toByteArray(), ParallelMapping.SYNC_ALL);
                }
            } else {
                rep = proxy.invokeOrdered(out.toByteArray());
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(rep);
            ObjectInputStream in = new ObjectInputStream(bis);
            boolean ret = in.readBoolean();
            in.close();
            return ret;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean contains(Object o) {
        try {
            out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(CONTAINS);
            //dos.writeInt(key.intValue());

            ObjectOutputStream out1 = new ObjectOutputStream(out);
            out1.writeObject(o);
            out1.close();
            byte[] rep = null;
            if (parallel) {
                if(async){
                    int id = asyncProxy.invokeParallelAsynchRequest(out.toByteArray(), null, TOMMessageType.ORDERED_REQUEST, ParallelMapping.CONC_ALL);
                    asyncProxy.cleanAsynchRequest(id);
                    return true;
                }else{
                    rep = proxy.invokeParallel(out.toByteArray(), ParallelMapping.CONC_ALL);
                }
            } else {
                rep = proxy.invokeOrdered(out.toByteArray());
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(rep);
            ObjectInputStream in = new ObjectInputStream(bis);
            boolean ret = in.readBoolean();
            in.close();
            return ret;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(REMOVE);
            //dos.writeInt(key.intValue());

            ObjectOutputStream out1 = new ObjectOutputStream(out);
            out1.writeObject(o);
            out1.close();
            byte[] rep = null;
            if (parallel) {
                if(async){
                    int id = asyncProxy.invokeParallelAsynchRequest(out.toByteArray(), null, TOMMessageType.ORDERED_REQUEST, ParallelMapping.SYNC_ALL);
                    asyncProxy.cleanAsynchRequest(id);   
                    return true;
                }else{
                    rep = proxy.invokeParallel(out.toByteArray(), ParallelMapping.SYNC_ALL);
                }
            } else {
                rep = proxy.invokeOrdered(out.toByteArray());
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(rep);
            ObjectInputStream in = new ObjectInputStream(bis);
            boolean ret = in.readBoolean();
            in.close();
            return ret;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public V get(int index) {
        try {
            out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeInt(GET);
            dos.writeInt(index);

            byte[] rep = null;
            if (parallel) {
                if(async){
                    int id = asyncProxy.invokeParallelAsynchRequest(out.toByteArray(), null, TOMMessageType.ORDERED_REQUEST, ParallelMapping.CONC_ALL);
                    asyncProxy.cleanAsynchRequest(id);
                    return null;
                }else{
                    rep = proxy.invokeParallel(out.toByteArray(), ParallelMapping.CONC_ALL);
                }
            } else {
                rep = proxy.invokeOrdered(out.toByteArray());
            }
            if(rep == null){
                return null;
            }
            
            ByteArrayInputStream bis = new ByteArrayInputStream(rep);
            ObjectInputStream in = new ObjectInputStream(bis);
            V resp = (V) in.readObject();
            in.close();
            return resp;

        } catch (IOException ex) {
            return null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BFTList.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }


@Override
        public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public Iterator<V> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public boolean addAll(Collection<? extends V> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public boolean addAll(int index, Collection<? extends V> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public V set(int index, V element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public void add(int index, V element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public V remove(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public ListIterator<V> listIterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public ListIterator<V> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
        public List<V> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
