//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import java.util.Queue;

public abstract class FIFORunnableQueue<T extends Runnable> implements Runnable {
    private static final int NONE = 0;
    private static final int QUEUED = 1;
    private static final int RUNNING = 2;
    private int _state = 0;
    private final Queue<T> _queue;

    public FIFORunnableQueue(Queue<T> queue) {
        this._queue = queue;
    }

    public void execute(T t) {
        this._queue.add(t);
        synchronized(this) {
            if (this._state != 0) {
                return;
            }

            this._state = 1;
        }

        this.execute();
    }

    protected abstract void execute();

    public void clear() {
        this._queue.clear();
    }

    public void run() {
        synchronized(this) {
            if (this._state == 2) {
                return;
            }

            this._state = 2;
        }

        while(true) {
            boolean var11 = false;

            try {
                var11 = true;
                Runnable t = (Runnable)this._queue.poll();
                if (t == null) {
                    var11 = false;
                    break;
                }

                t.run();
            } finally {
                if (var11) {
                    synchronized(this) {
                        this._state = 0;
                    }
                }
            }
        }

        synchronized(this) {
            this._state = 0;
        }
    }
}
