//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import gnu.trove.TIntObjectHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.gameserver.data.xml.holder.DoorHolder;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.utils.Location;

public class ReflectionManager {
  public static final Reflection DEFAULT = Reflection.createReflection(0);
  public static final Reflection GIRAN_HARBOR = Reflection.createReflection(-1);
  public static final Reflection JAIL = Reflection.createReflection(-2);
  private static final ReflectionManager _instance = new ReflectionManager();
  private final TIntObjectHashMap<Reflection> _reflections = new TIntObjectHashMap();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Lock readLock;
  private final Lock writeLock;

  public static ReflectionManager getInstance() {
    return _instance;
  }

  private ReflectionManager() {
    this.readLock = this.lock.readLock();
    this.writeLock = this.lock.writeLock();
    this.add(DEFAULT);
    this.add(GIRAN_HARBOR);
    this.add(JAIL);
    DEFAULT.init(DoorHolder.getInstance().getDoors(), ZoneHolder.getInstance().getZones());
    GIRAN_HARBOR.fillSpawns(InstantZoneHolder.getInstance().getInstantZone(10).getSpawnsInfo());
    JAIL.setCoreLoc(new Location(-114648, -249384, -2984));
  }

  public Reflection get(int id) {
    this.readLock.lock();

    Reflection var2;
    try {
      var2 = (Reflection)this._reflections.get(id);
    } finally {
      this.readLock.unlock();
    }

    return var2;
  }

  public Reflection add(Reflection ref) {
    this.writeLock.lock();

    Reflection var2;
    try {
      var2 = (Reflection)this._reflections.put(ref.getId(), ref);
    } finally {
      this.writeLock.unlock();
    }

    return var2;
  }

  public Reflection remove(Reflection ref) {
    this.writeLock.lock();

    Reflection var2;
    try {
      var2 = (Reflection)this._reflections.remove(ref.getId());
    } finally {
      this.writeLock.unlock();
    }

    return var2;
  }

  public Reflection[] getAll() {
    this.readLock.lock();

    Reflection[] var1;
    try {
      var1 = (Reflection[])this._reflections.getValues(new Reflection[this._reflections.size()]);
    } finally {
      this.readLock.unlock();
    }

    return var1;
  }

  public int getCountByIzId(int izId) {
    this.readLock.lock();

    int var10;
    try {
      int i = 0;
      Reflection[] var3 = this.getAll();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        Reflection r = var3[var5];
        if (r.getInstancedZoneId() == izId) {
          ++i;
        }
      }

      var10 = i;
    } finally {
      this.readLock.unlock();
    }

    return var10;
  }

  public int size() {
    return this._reflections.size();
  }
}
