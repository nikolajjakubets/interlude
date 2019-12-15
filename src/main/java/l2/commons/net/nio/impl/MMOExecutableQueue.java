//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MMOExecutableQueue<T extends MMOClient> implements Queue<ReceivablePacket<T>>, Runnable {
    private static final int NONE = 0;
    private static final int QUEUED = 1;
    private static final int RUNNING = 2;
    private final IMMOExecutor<T> _executor;
    private final Queue<ReceivablePacket<T>> _queue;
    private AtomicInteger _state = new AtomicInteger(0);

    public MMOExecutableQueue(IMMOExecutor<T> executor) {
        this._executor = executor;
      this._queue = new ArrayDeque<>();
    }

    public void run() {
        label38:
        while(true) {
            if (this._state.compareAndSet(1, 2)) {
                try {
                    while(true) {
                        Runnable t = this.poll();
                        if (t == null) {
                            continue label38;
                        }

                        t.run();
                    }
                } finally {
                    this._state.compareAndSet(2, 0);
                }
            }

            return;
        }
    }

    public int size() {
        return this._queue.size();
    }

    public boolean isEmpty() {
        return this._queue.isEmpty();
    }

    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    public Iterator<ReceivablePacket<T>> iterator() {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public <E> E[] toArray(E[] a) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends ReceivablePacket<T>> c) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        synchronized(this._queue) {
            this._queue.clear();
        }
    }

    public boolean add(ReceivablePacket<T> e) {
        synchronized(this._queue) {
            if (!this._queue.add(e)) {
                return false;
            }
        }

        if (this._state.getAndSet(1) == 0) {
            this._executor.execute(this);
        }

        return true;
    }

    public boolean offer(ReceivablePacket<T> e) {
        synchronized(this._queue) {
            return this._queue.offer(e);
        }
    }

    public ReceivablePacket<T> remove() {
        synchronized(this._queue) {
          return this._queue.remove();
        }
    }

    public ReceivablePacket<T> poll() {
        synchronized(this._queue) {
          return this._queue.poll();
        }
    }

    public ReceivablePacket<T> element() {
        synchronized(this._queue) {
          return this._queue.element();
        }
    }

    public ReceivablePacket<T> peek() {
        synchronized(this._queue) {
          return this._queue.peek();
        }
    }
}
