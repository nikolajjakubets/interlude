//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.MinionData;

public class MinionList {
  private final Set<MinionData> _minionData;
  private final Set<MinionInstance> _minions;
  private final Lock lock;
  private final MonsterInstance _master;

  public MinionList(MonsterInstance master) {
    this._master = master;
    this._minions = new HashSet();
    this._minionData = new HashSet();
    this._minionData.addAll(this._master.getTemplate().getMinionData());
    this.lock = new ReentrantLock();
  }

  public boolean addMinion(MinionData m) {
    this.lock.lock();

    boolean var2;
    try {
      var2 = this._minionData.add(m);
    } finally {
      this.lock.unlock();
    }

    return var2;
  }

  public boolean addMinion(MinionInstance m) {
    this.lock.lock();

    boolean var2;
    try {
      var2 = this._minions.add(m);
    } finally {
      this.lock.unlock();
    }

    return var2;
  }

  public boolean hasAliveMinions() {
    this.lock.lock();

    try {
      Iterator var1 = this._minions.iterator();

      while(var1.hasNext()) {
        MinionInstance m = (MinionInstance)var1.next();
        if (m.isVisible() && !m.isDead()) {
          boolean var3 = true;
          return var3;
        }
      }
    } finally {
      this.lock.unlock();
    }

    return false;
  }

  public boolean hasMinions() {
    return this._minionData.size() > 0;
  }

  public List<MinionInstance> getAliveMinions() {
    List<MinionInstance> result = new ArrayList(this._minions.size());
    this.lock.lock();

    try {
      Iterator var2 = this._minions.iterator();

      while(var2.hasNext()) {
        MinionInstance m = (MinionInstance)var2.next();
        if (m.isVisible() && !m.isDead()) {
          result.add(m);
        }
      }
    } finally {
      this.lock.unlock();
    }

    return result;
  }

  public void spawnMinions() {
    this.lock.lock();

    try {
      Iterator var3 = this._minionData.iterator();

      while(var3.hasNext()) {
        MinionData minion = (MinionData)var3.next();
        int minionId = minion.getMinionId();
        int minionCount = minion.getAmount();
        Iterator var5 = this._minions.iterator();

        MinionInstance m;
        while(var5.hasNext()) {
          m = (MinionInstance)var5.next();
          if (m.getNpcId() == minionId) {
            --minionCount;
          }

          if (m.isDead() || !m.isVisible()) {
            m.refreshID();
            m.stopDecay();
            this._master.spawnMinion(m);
          }
        }

        for(int i = 0; i < minionCount; ++i) {
          m = new MinionInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(minionId));
          m.setLeader(this._master);
          this._master.spawnMinion(m);
          this._minions.add(m);
        }
      }
    } finally {
      this.lock.unlock();
    }

  }

  public void unspawnMinions() {
    this.lock.lock();

    try {
      Iterator var1 = this._minions.iterator();

      while(var1.hasNext()) {
        MinionInstance m = (MinionInstance)var1.next();
        m.decayMe();
      }
    } finally {
      this.lock.unlock();
    }

  }

  public void deleteMinions() {
    this.lock.lock();

    try {
      Iterator var1 = this._minions.iterator();

      while(var1.hasNext()) {
        MinionInstance m = (MinionInstance)var1.next();
        m.deleteMe();
      }

      this._minions.clear();
    } finally {
      this.lock.unlock();
    }
  }
}
