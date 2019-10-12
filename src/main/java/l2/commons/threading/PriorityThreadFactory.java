//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory implements ThreadFactory {
    private int _prio;
    private String _name;
    private AtomicInteger _threadNumber = new AtomicInteger(1);
    private ThreadGroup _group;

    public PriorityThreadFactory(String name, int prio) {
        this._prio = prio;
        this._name = name;
        this._group = new ThreadGroup(this._name);
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(this._group, r);
        t.setName(this._name + "-" + this._threadNumber.getAndIncrement());
        t.setPriority(this._prio);
        return t;
    }

    public ThreadGroup getGroup() {
        return this._group;
    }
}
