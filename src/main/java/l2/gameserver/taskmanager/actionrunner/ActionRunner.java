//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager.actionrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.logging.LoggerObject;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.taskmanager.actionrunner.tasks.AutomaticTask;
import l2.gameserver.taskmanager.actionrunner.tasks.DeleteExpiredMailTask;
import l2.gameserver.taskmanager.actionrunner.tasks.DeleteExpiredVarsTask;

public class ActionRunner extends LoggerObject {
  private static ActionRunner _instance = new ActionRunner();
  private Map<String, List<ActionWrapper>> _futures = new HashMap();
  private final Lock _lock = new ReentrantLock();

  public static ActionRunner getInstance() {
    return _instance;
  }

  private ActionRunner() {
    this.register(new DeleteExpiredVarsTask());
    this.register(new DeleteExpiredMailTask());
  }

  public void register(AutomaticTask task) {
    this.register(task.reCalcTime(true), task);
  }

  public void register(long time, ActionWrapper wrapper) {
    if (time == 0L) {
      this.info("Try register " + wrapper.getName() + " not defined time.");
    } else if (time <= System.currentTimeMillis()) {
      ThreadPoolManager.getInstance().execute(wrapper);
    } else {
      this.addScheduled(wrapper.getName(), wrapper, time - System.currentTimeMillis());
    }
  }

  protected void addScheduled(String name, ActionWrapper r, long diff) {
    this._lock.lock();

    try {
      String lower = name.toLowerCase();
      List<ActionWrapper> wrapperList = (List)this._futures.get(lower);
      if (wrapperList == null) {
        this._futures.put(lower, wrapperList = new ArrayList());
      }

      r.schedule(diff);
      ((List)wrapperList).add(r);
    } finally {
      this._lock.unlock();
    }

  }

  protected void remove(String name, ActionWrapper f) {
    this._lock.lock();

    try {
      String lower = name.toLowerCase();
      List<ActionWrapper> wrapperList = (List)this._futures.get(lower);
      if (wrapperList != null) {
        wrapperList.remove(f);
        if (wrapperList.isEmpty()) {
          this._futures.remove(lower);
        }

        return;
      }
    } finally {
      this._lock.unlock();
    }

  }

  public void clear(String name) {
    this._lock.lock();

    try {
      String lower = name.toLowerCase();
      List<ActionWrapper> wrapperList = (List)this._futures.remove(lower);
      if (wrapperList != null) {
        Iterator var4 = wrapperList.iterator();

        while(var4.hasNext()) {
          ActionWrapper f = (ActionWrapper)var4.next();
          f.cancel();
        }

        wrapperList.clear();
        return;
      }
    } finally {
      this._lock.unlock();
    }

  }

  public void info() {
    this._lock.lock();

    try {
      Iterator var1 = this._futures.entrySet().iterator();

      while(var1.hasNext()) {
        Entry<String, List<ActionWrapper>> entry = (Entry)var1.next();
        this.info("Name: " + (String)entry.getKey() + "; size: " + ((List)entry.getValue()).size());
      }
    } finally {
      this._lock.unlock();
    }

  }
}
