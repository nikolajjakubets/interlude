//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.lang.ArrayUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class WorldRegion implements Iterable<GameObject> {
  public static final WorldRegion[] EMPTY_L2WORLDREGION_ARRAY = new WorldRegion[0];
  private static final Logger _log = LoggerFactory.getLogger(WorldRegion.class);
  private final int tileX;
  private final int tileY;
  private final int tileZ;
  private volatile GameObject[] _objects;
  private int _objectsCount;
  private volatile Zone[] _zones;
  private int _playersCount;
  private final AtomicBoolean _isActive;
  private Future<?> _activateTask;
  private final Lock lock;

  WorldRegion(int x, int y, int z) {
    this._objects = GameObject.EMPTY_L2OBJECT_ARRAY;
    this._objectsCount = 0;
    this._zones = Zone.EMPTY_L2ZONE_ARRAY;
    this._playersCount = 0;
    this._isActive = new AtomicBoolean();
    this.lock = new ReentrantLock();
    this.tileX = x;
    this.tileY = y;
    this.tileZ = z;
  }

  int getX() {
    return this.tileX;
  }

  int getY() {
    return this.tileY;
  }

  int getZ() {
    return this.tileZ;
  }

  void setActive(boolean activate) {
    if (this._isActive.compareAndSet(!activate, activate)) {
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
        GameObject obj = (GameObject)var3.next();
        if (obj.isNpc()) {
          NpcInstance npc = (NpcInstance)obj;
          if (npc.getAI().isActive() != this.isActive()) {
            if (this.isActive()) {
              npc.getAI().startAITask();
              npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
              npc.startRandomAnimation();
            } else if (!npc.getAI().isGlobalAI()) {
              npc.getAI().stopAITask();
              npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
              npc.stopRandomAnimation();
            }
          }
        }
      }

    }
  }

  void addToPlayers(GameObject object, Creature dropper) {
    if (object != null) {
      Player player = null;
      if (object.isPlayer()) {
        player = (Player)object;
      }

      int oid = object.getObjectId();
      int rid = object.getReflectionId();
      Iterator var7 = this.iterator();

      while(var7.hasNext()) {
        GameObject obj = (GameObject)var7.next();
        if (obj.getObjectId() != oid && obj.getReflectionId() == rid) {
          if (player != null) {
            player.sendPacket(player.addVisibleObject(obj, (Creature)null));
          }

          if (obj.isPlayer()) {
            Player p = (Player)obj;
            p.sendPacket(p.addVisibleObject(object, dropper));
          }
        }
      }

    }
  }

  void removeFromPlayers(GameObject object) {
    if (object != null) {
      Player player = null;
      if (object.isPlayer()) {
        player = (Player)object;
      }

      int oid = object.getObjectId();
      Reflection rid = object.getReflection();
      List<L2GameServerPacket> d = null;

      for (GameObject obj : this) {
        if (obj.getObjectId() != oid && obj.getReflection() == rid) {
          if (player != null) {
            player.sendPacket(player.removeVisibleObject(obj, (List) null));
          }

          if (obj.isPlayer()) {
            Player p = (Player) obj;
            p.sendPacket(p.removeVisibleObject(object, d == null ? (d = object.deletePacketList()) : d));
          }
        }
      }

    }
  }

  public void addObject(GameObject obj) {
    if (obj != null) {
      this.lock.lock();

      try {
        GameObject[] objects = this._objects;
        GameObject[] resizedObjects = new GameObject[this._objectsCount + 1];
        System.arraycopy(objects, 0, resizedObjects, 0, this._objectsCount);
        resizedObjects[this._objectsCount++] = obj;
        this._objects = resizedObjects;
        if (obj.isPlayer() && this._playersCount++ == 0) {
          if (this._activateTask != null) {
            this._activateTask.cancel(false);
          }

          this._activateTask = ThreadPoolManager.getInstance().schedule(new WorldRegion.ActivateTask(true), 1000L);
        }
      } finally {
        this.lock.unlock();
      }

    }
  }

  public void removeObject(GameObject obj) {
    if (obj != null) {
      this.lock.lock();

      try {
        GameObject[] objects = this._objects;
        int index = -1;

        for(int i = 0; i < this._objectsCount; ++i) {
          if (objects[i] == obj) {
            index = i;
            break;
          }
        }

        if (index == -1) {
          return;
        }

        --this._objectsCount;
        GameObject[] resizedObjects = new GameObject[this._objectsCount];
        objects[index] = objects[this._objectsCount];
        System.arraycopy(objects, 0, resizedObjects, 0, this._objectsCount);
        this._objects = resizedObjects;
        if (obj.isPlayer() && --this._playersCount == 0) {
          if (this._activateTask != null) {
            this._activateTask.cancel(false);
          }

          this._activateTask = ThreadPoolManager.getInstance().schedule(new WorldRegion.ActivateTask(false), 60000L);
        }
      } finally {
        this.lock.unlock();
      }

    }
  }

  public int getObjectsSize() {
    return this._objectsCount;
  }

  public int getPlayersCount() {
    return this._playersCount;
  }

  public boolean isEmpty() {
    return this._playersCount == 0;
  }

  public boolean isActive() {
    return this._isActive.get();
  }

  void addZone(Zone zone) {
    this.lock.lock();

    try {
      this._zones = (Zone[])ArrayUtils.add(this._zones, zone);
    } finally {
      this.lock.unlock();
    }

  }

  void removeZone(Zone zone) {
    this.lock.lock();

    try {
      this._zones = (Zone[])ArrayUtils.remove(this._zones, zone);
    } finally {
      this.lock.unlock();
    }

  }

  Zone[] getZones() {
    return this._zones;
  }

  public String toString() {
    return "[" + this.tileX + ", " + this.tileY + ", " + this.tileZ + "]";
  }

  public Iterator<GameObject> iterator() {
    return new WorldRegion.InternalIterator(this._objects);
  }

  private class InternalIterator implements Iterator<GameObject> {
    final GameObject[] objects;
    int cursor = 0;

    public InternalIterator(GameObject[] objects) {
      this.objects = objects;
    }

    public boolean hasNext() {
      if (this.cursor < this.objects.length) {
        return this.objects[this.cursor] != null;
      } else {
        return false;
      }
    }

    public GameObject next() {
      return this.objects[this.cursor++];
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public class ActivateTask extends RunnableImpl {
    private boolean _isActivating;

    public ActivateTask(boolean isActivating) {
      this._isActivating = isActivating;
    }

    public void runImpl() throws Exception {
      if (this._isActivating) {
        World.activate(WorldRegion.this);
      } else {
        World.deactivate(WorldRegion.this);
      }

    }
  }
}
