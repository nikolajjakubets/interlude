package l2.commons.collections;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class LazyArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    private static final long serialVersionUID = 8683452581122892189L;
    private static final int POOL_SIZE = Integer.parseInt(System.getProperty("lazyarraylist.poolsize", "-1"));
    private static final ObjectPool POOL;
    private static final int L = 8;
    private static final int H = 1024;
    protected transient Object[] elementData;
    protected transient int size;
    protected transient int capacity;

    public static <E> LazyArrayList<E> newInstance() {
        try {
            return (LazyArrayList)POOL.borrowObject();
        } catch (Exception var1) {
            var1.printStackTrace();
            return new LazyArrayList();
        }
    }

    public static <E> void recycle(LazyArrayList<E> obj) {
        try {
            POOL.returnObject(obj);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public LazyArrayList(int initialCapacity) {
        this.size = 0;
        this.capacity = 8;
        if (initialCapacity < 1024) {
            while(this.capacity < initialCapacity) {
                this.capacity <<= 1;
            }
        } else {
            this.capacity = initialCapacity;
        }

    }

    public LazyArrayList() {
        this(8);
    }

    public boolean add(E element) {
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = element;
        return true;
    }

    public E set(int index, E element) {
        E e = null;
        if (index >= 0 && index < this.size) {
            //TODO: i add cast
            e = (E) this.elementData[index];
            this.elementData[index] = element;
        }

        return e;
    }

    public void add(int index, E element) {
        if (index >= 0 && index < this.size) {
            this.ensureCapacity(this.size + 1);
            System.arraycopy(this.elementData, index, this.elementData, index + 1, this.size - index);
            this.elementData[index] = element;
            ++this.size;
        }

    }

    public boolean addAll(int index, Collection<? extends E> c) {
        if (c != null && !c.isEmpty()) {
            if (index >= 0 && index < this.size) {
                Object[] a = c.toArray();
                int numNew = a.length;
                this.ensureCapacity(this.size + numNew);
                int numMoved = this.size - index;
                if (numMoved > 0) {
                    System.arraycopy(this.elementData, index, this.elementData, index + numNew, numMoved);
                }

                System.arraycopy(a, 0, this.elementData, index, numNew);
                this.size += numNew;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void ensureCapacity(int newSize) {
        if (newSize > this.capacity) {
            if (newSize < 1024) {
                while(this.capacity < newSize) {
                    this.capacity <<= 1;
                }
            } else {
                while(this.capacity < newSize) {
                    this.capacity = this.capacity * 3 / 2;
                }
            }

            Object[] elementDataResized = new Object[this.capacity];
            if (this.elementData != null) {
                System.arraycopy(this.elementData, 0, elementDataResized, 0, this.size);
            }

            this.elementData = elementDataResized;
        } else if (this.elementData == null) {
            this.elementData = new Object[this.capacity];
        }

    }

    public E remove(int index) {
        E e = null;
        if (index >= 0 && index < this.size) {
            --this.size;
            e = this.elementData[index];
            this.elementData[index] = this.elementData[this.size];
            this.elementData[this.size] = null;
            this.trim();
        }

        return e;
    }

    public boolean remove(Object o) {
        if (this.size == 0) {
            return false;
        } else {
            int index = -1;

            for(int i = 0; i < this.size; ++i) {
                if (this.elementData[i] == o) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                return false;
            } else {
                --this.size;
                this.elementData[index] = this.elementData[this.size];
                this.elementData[this.size] = null;
                this.trim();
                return true;
            }
        }
    }

    public boolean contains(Object o) {
        if (this.size == 0) {
            return false;
        } else {
            for(int i = 0; i < this.size; ++i) {
                if (this.elementData[i] == o) {
                    return true;
                }
            }

            return false;
        }
    }

    public int indexOf(Object o) {
        if (this.size == 0) {
            return -1;
        } else {
            int index = -1;

            for(int i = 0; i < this.size; ++i) {
                if (this.elementData[i] == o) {
                    index = i;
                    break;
                }
            }

            return index;
        }
    }

    public int lastIndexOf(Object o) {
        if (this.size == 0) {
            return -1;
        } else {
            int index = -1;

            for(int i = 0; i < this.size; ++i) {
                if (this.elementData[i] == o) {
                    index = i;
                }
            }

            return index;
        }
    }

    protected void trim() {
    }

    public E get(int index) {
        return this.size > 0 && index >= 0 && index < this.size ? this.elementData[index] : null;
    }

    public Object clone() {
        LazyArrayList<E> clone = new LazyArrayList();
        if (this.size > 0) {
            clone.capacity = this.capacity;
            clone.elementData = new Object[this.elementData.length];
            System.arraycopy(this.elementData, 0, clone.elementData, 0, this.size);
        }

        return clone;
    }

    public void clear() {
        if (this.size != 0) {
            for(int i = 0; i < this.size; ++i) {
                this.elementData[i] = null;
            }

            this.size = 0;
            this.trim();
        }
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int capacity() {
        return this.capacity;
    }

    public boolean addAll(Collection<? extends E> c) {
        if (c != null && !c.isEmpty()) {
            Object[] a = c.toArray();
            int numNew = a.length;
            this.ensureCapacity(this.size + numNew);
            System.arraycopy(a, 0, this.elementData, this.size, numNew);
            this.size += numNew;
            return true;
        } else {
            return false;
        }
    }

    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            return false;
        } else if (c.isEmpty()) {
            return true;
        } else {
            Iterator e = c.iterator();

            do {
                if (!e.hasNext()) {
                    return true;
                }
            } while(this.contains(e.next()));

            return false;
        }
    }

    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            return false;
        } else {
            boolean modified = false;
            Iterator e = this.iterator();

            while(e.hasNext()) {
                if (!c.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }

            return modified;
        }
    }

    public boolean removeAll(Collection<?> c) {
        if (c != null && !c.isEmpty()) {
            boolean modified = false;
            Iterator e = this.iterator();

            while(e.hasNext()) {
                if (c.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }

            return modified;
        } else {
            return false;
        }
    }

    public Object[] toArray() {
        Object[] r = new Object[this.size];
        if (this.size > 0) {
            System.arraycopy(this.elementData, 0, r, 0, this.size);
        }

        return r;
    }

    public <T> T[] toArray(T[] a) {
        T[] r = a.length >= this.size ? a : (Object[])((Object[])Array.newInstance(a.getClass().getComponentType(), this.size));
        if (this.size > 0) {
            System.arraycopy(this.elementData, 0, r, 0, this.size);
        }

        if (r.length > this.size) {
            r[this.size] = null;
        }

        return r;
    }

    public Iterator<E> iterator() {
        return new LazyArrayList.LazyItr();
    }

    public ListIterator<E> listIterator() {
        return new LazyArrayList.LazyListItr(0);
    }

    public ListIterator<E> listIterator(int index) {
        return new LazyArrayList.LazyListItr(index);
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');

            for(int i = 0; i < this.size; ++i) {
                Object e = this.elementData[i];
                sb.append(e == this ? "this" : e);
                if (i == this.size - 1) {
                    sb.append(']').toString();
                } else {
                    sb.append(", ");
                }
            }

            return sb.toString();
        }
    }

    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    static {
        POOL = new GenericObjectPool(new LazyArrayList.PoolableLazyArrayListFactory(), POOL_SIZE, (byte)2, 0L, -1);
    }

    private class LazyListItr extends LazyArrayList<E>.LazyItr implements ListIterator<E> {
        LazyListItr(int index) {
            super(null);
            this.cursor = index;
        }

        public boolean hasPrevious() {
            return this.cursor > 0;
        }

        public E previous() {
            int i = this.cursor - 1;
            E previous = LazyArrayList.this.get(i);
            this.lastRet = this.cursor = i;
            return previous;
        }

        public int nextIndex() {
            return this.cursor;
        }

        public int previousIndex() {
            return this.cursor - 1;
        }

        public void set(E e) {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            } else {
                LazyArrayList.this.set(this.lastRet, e);
            }
        }

        public void add(E e) {
            LazyArrayList.this.add(this.cursor++, e);
            this.lastRet = -1;
        }
    }

    private class LazyItr implements Iterator<E> {
        int cursor;
        int lastRet;

        private LazyItr() {
            this.cursor = 0;
            this.lastRet = -1;
        }

        public boolean hasNext() {
            return this.cursor < LazyArrayList.this.size();
        }

        public E next() {
            E next = LazyArrayList.this.get(this.cursor);
            this.lastRet = this.cursor++;
            return next;
        }

        public void remove() {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            } else {
                LazyArrayList.this.remove(this.lastRet);
                if (this.lastRet < this.cursor) {
                    --this.cursor;
                }

                this.lastRet = -1;
            }
        }
    }

    private static class PoolableLazyArrayListFactory implements PoolableObjectFactory {
        private PoolableLazyArrayListFactory() {
        }

        public Object makeObject() throws Exception {
            return new LazyArrayList();
        }

        public void destroyObject(Object obj) throws Exception {
            ((LazyArrayList)obj).clear();
        }

        public boolean validateObject(Object obj) {
            return true;
        }

        public void activateObject(Object obj) throws Exception {
        }

        public void passivateObject(Object obj) throws Exception {
            ((LazyArrayList)obj).clear();
        }
    }
}
