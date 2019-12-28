//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import l2.commons.collections.LazyArrayList;
import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AggroList {
  private final NpcInstance npc;
  private final TIntObjectHashMap<AggroList.AggroInfo> hateList = new TIntObjectHashMap();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Lock readLock;
  private final Lock writeLock;

  public AggroList(NpcInstance npc) {
    this.readLock = this.lock.readLock();
    this.writeLock = this.lock.writeLock();
    this.npc = npc;
  }

  public void addDamageHate(Creature attacker, int damage, int aggro) {
    damage = Math.max(damage, 0);
    if (damage != 0 || aggro != 0) {
      this.writeLock.lock();

      try {
        AggroList.AggroInfo ai;
        if ((ai = this.hateList.get(attacker.getObjectId())) == null) {
          this.hateList.put(attacker.getObjectId(), ai = new AggroList.AggroInfo(attacker));
        }

        ai.damage += damage;
        ai.hate += aggro;
        ai.damage = Math.max(ai.damage, 0);
        ai.hate = Math.max(ai.hate, 0);
      } finally {
        this.writeLock.unlock();
      }

    }
  }

  public AggroList.AggroInfo get(Creature attacker) {
    this.readLock.lock();

    AggroList.AggroInfo var2;
    try {
      var2 = this.hateList.get(attacker.getObjectId());
    } finally {
      this.readLock.unlock();
    }

    return var2;
  }

  public void remove(Creature attacker, boolean onlyHate) {
    this.writeLock.lock();

    try {
      if (!onlyHate) {
        this.hateList.remove(attacker.getObjectId());
        return;
      }

      AggroList.AggroInfo ai = this.hateList.get(attacker.getObjectId());
      if (ai != null) {
        ai.hate = 0;
      }
    } finally {
      this.writeLock.unlock();
    }

  }

  public void clear() {
    this.clear(false);
  }

  public void clear(boolean onlyHate) {
    this.writeLock.lock();

    try {
      if (this.hateList.isEmpty()) {
        return;
      }

      if (!onlyHate) {
        this.hateList.clear();
        return;
      }

      TIntObjectIterator itr = this.hateList.iterator();

      while(itr.hasNext()) {
        itr.advance();
        AggroList.AggroInfo ai = (AggroList.AggroInfo)itr.value();
        ai.hate = 0;
        if (ai.damage == 0) {
          itr.remove();
        }
      }
    } finally {
      this.writeLock.unlock();
    }

  }

  public boolean isEmpty() {
    this.readLock.lock();

    boolean var1;
    try {
      var1 = this.hateList.isEmpty();
    } finally {
      this.readLock.unlock();
    }

    return var1;
  }

  public List<Creature> getHateList(int radius) {
    this.readLock.lock();

    AggroList.AggroInfo[] hated;
    try {
      if (this.hateList.isEmpty()) {
        List var3 = Collections.emptyList();
        return var3;
      }

      hated = this.hateList.getValues(new AggroInfo[this.hateList.size()]);
    } finally {
      this.readLock.unlock();
    }

    Arrays.sort(hated, AggroList.HateComparator.getInstance());
    if (hated[0].hate == 0) {
      return Collections.emptyList();
    } else {
      List<Creature> hateList = new LazyArrayList();
      List<Creature> chars = World.getAroundCharacters(this.npc, radius, radius);

      for(int i = 0; i < hated.length; ++i) {
        AggroList.AggroInfo ai = hated[i];
        if (ai.hate != 0) {
          Iterator var7 = chars.iterator();

          while(var7.hasNext()) {
            Creature cha = (Creature)var7.next();
            if (cha.getObjectId() == ai.attackerId) {
              hateList.add(cha);
              break;
            }
          }
        }
      }

      return hateList;
    }
  }

  public Creature getMostHated() {
    this.readLock.lock();

    AggroList.AggroInfo[] hated;
    List chars;
    try {
      if (this.hateList.isEmpty()) {
//        chars = null;
        return null;
      }

      hated = this.hateList.getValues(new AggroInfo[this.hateList.size()]);
    } finally {
      this.readLock.unlock();
    }

    Arrays.sort(hated, AggroList.HateComparator.getInstance());
    if (hated[0].hate == 0) {
      return null;
    } else {
      chars = World.getAroundCharacters(this.npc);

      for (AggroInfo ai : hated) {
        if (ai.hate != 0) {

          for (Object aChar : chars) {
            Creature cha = (Creature) aChar;
            if (cha.getObjectId() == ai.attackerId) {
              if (!cha.isDead()) {
                return cha;
              }
              break;
            }
          }
        }
      }

      return null;
    }
  }

  public Creature getRandomHated() {
    this.readLock.lock();

    AggroList.AggroInfo[] hated;
    List chars;
    label112: {
      try {
        if (!this.hateList.isEmpty()) {
          hated = this.hateList.getValues(new AggroList.AggroInfo[this.hateList.size()]);
          break label112;
        }

//        chars = null;
      } finally {
        this.readLock.unlock();
      }

      return null;
    }

    Arrays.sort(hated, AggroList.HateComparator.getInstance());
    if (hated[0].hate == 0) {
      return null;
    } else {
      chars = World.getAroundCharacters(this.npc);
      LazyArrayList<Creature> randomHated = LazyArrayList.newInstance();

      for(int i = 0; i < hated.length; ++i) {
        AggroList.AggroInfo ai = hated[i];
        if (ai.hate != 0) {
          Iterator var7 = chars.iterator();

          while(var7.hasNext()) {
            Creature cha = (Creature)var7.next();
            if (cha.getObjectId() == ai.attackerId) {
              if (!cha.isDead()) {
                randomHated.add(cha);
              }
              break;
            }
          }
        }
      }

      Creature mostHated;
      if (randomHated.isEmpty()) {
        mostHated = null;
      } else {
        mostHated = randomHated.get(Rnd.get(randomHated.size()));
      }

      LazyArrayList.recycle(randomHated);
      return mostHated;
    }
  }

  public Creature getTopDamager() {
    this.readLock.lock();

    AggroList.AggroInfo[] hated;
    Object var2;
    try {
      if (this.hateList.isEmpty()) {
        var2 = null;
        return (Creature)var2;
      }

      hated = this.hateList.getValues(new AggroInfo[this.hateList.size()]);
    } finally {
      this.readLock.unlock();
    }

    var2 = null;
    Arrays.sort(hated, AggroList.DamageComparator.getInstance());
    if (hated[0].damage == 0) {
      return null;
    } else {
      List<Creature> chars = World.getAroundCharacters(this.npc);

      for(int i = 0; i < hated.length; ++i) {
        AggroList.AggroInfo ai = hated[i];
        if (ai.damage != 0) {
          Iterator var6 = chars.iterator();

          while(var6.hasNext()) {
            Creature cha = (Creature)var6.next();
            if (cha.getObjectId() == ai.attackerId) {
              return cha;
            }
          }
        }
      }

      return null;
    }
  }

  public Map<Creature, AggroList.HateInfo> getCharMap() {
    if (this.isEmpty()) {
      return Collections.emptyMap();
    } else {
      Map<Creature, AggroList.HateInfo> aggroMap = new HashMap<>();
      List<Creature> chars = World.getAroundCharacters(this.npc);
      this.readLock.lock();

      try {
        TIntObjectIterator itr = this.hateList.iterator();

        while(true) {
          while(true) {
            AggroList.AggroInfo ai;
            do {
              if (!itr.hasNext()) {
                return aggroMap;
              }

              itr.advance();
              ai = (AggroList.AggroInfo)itr.value();
            } while(ai.damage == 0 && ai.hate == 0);

            Iterator var5 = chars.iterator();

            while(var5.hasNext()) {
              Creature attacker = (Creature)var5.next();
              if (attacker.getObjectId() == ai.attackerId) {
                aggroMap.put(attacker, new AggroList.HateInfo(attacker, ai));
                break;
              }
            }
          }
        }
      } finally {
        this.readLock.unlock();
      }
    }
  }

  public Map<Playable, AggroList.HateInfo> getPlayableMap() {
    if (this.isEmpty()) {
      return Collections.emptyMap();
    } else {
      Map<Playable, AggroList.HateInfo> aggroMap = new HashMap<>();
      List<Playable> chars = World.getAroundPlayables(this.npc);
      this.readLock.lock();

      try {
        TIntObjectIterator itr = this.hateList.iterator();

        while(true) {
          while(true) {
            AggroList.AggroInfo ai;
            do {
              if (!itr.hasNext()) {
                return aggroMap;
              }

              itr.advance();
              ai = (AggroList.AggroInfo)itr.value();
            } while(ai.damage == 0 && ai.hate == 0);

            Iterator var5 = chars.iterator();

            while(var5.hasNext()) {
              Playable attacker = (Playable)var5.next();
              if (attacker.getObjectId() == ai.attackerId) {
                aggroMap.put(attacker, new AggroList.HateInfo(attacker, ai));
                break;
              }
            }
          }
        }
      } finally {
        this.readLock.unlock();
      }
    }
  }

  public static class HateComparator implements Comparator<AggroList.DamageHate> {
    private static Comparator<AggroList.DamageHate> instance = new AggroList.HateComparator();

    public static Comparator<AggroList.DamageHate> getInstance() {
      return instance;
    }

    HateComparator() {
    }

    public int compare(AggroList.DamageHate o1, AggroList.DamageHate o2) {
      int diff = o2.hate - o1.hate;
      return diff == 0 ? o2.damage - o1.damage : diff;
    }
  }

  public static class DamageComparator implements Comparator<AggroList.DamageHate> {
    private static Comparator<AggroList.DamageHate> instance = new AggroList.DamageComparator();

    public static Comparator<AggroList.DamageHate> getInstance() {
      return instance;
    }

    DamageComparator() {
    }

    public int compare(AggroList.DamageHate o1, AggroList.DamageHate o2) {
      return o2.damage - o1.damage;
    }
  }

  public class AggroInfo extends AggroList.DamageHate {
    public final int attackerId;

    AggroInfo(Creature attacker) {
      super();
      this.attackerId = attacker.getObjectId();
    }
  }

  public class HateInfo extends AggroList.DamageHate {
    public final Creature attacker;

    HateInfo(Creature attacker, AggroList.AggroInfo ai) {
      super();
      this.attacker = attacker;
      this.hate = ai.hate;
      this.damage = ai.damage;
    }
  }

  private abstract class DamageHate {
    public int hate;
    public int damage;

    private DamageHate() {
    }
  }
}
