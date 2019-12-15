//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.collections.MultiValueSet;
import l2.commons.time.cron.NextTime;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.EventOwner;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.taskmanager.SpawnTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class Spawner extends EventOwner implements Cloneable {
  private static final long serialVersionUID = 1L;
  protected static final int MIN_RESPAWN_DELAY = 300;
  protected int _maximumCount;
  protected int _referenceCount;
  protected AtomicInteger _currentCount = new AtomicInteger(0);
  protected AtomicInteger _scheduledCount = new AtomicInteger(0);
  protected long _respawnDelay;
  protected long _respawnDelayRandom;
  protected NextTime _respawnCron;
  protected int _respawnTime;
  protected boolean _doRespawn;
  protected NpcInstance _lastSpawn;
  protected List<NpcInstance> _spawned;
  protected Reflection _reflection;

  public Spawner() {
    this._reflection = ReflectionManager.DEFAULT;
  }

  public void decreaseScheduledCount() {
    while (true) {
      int scheduledCount = this._scheduledCount.get();
      if (scheduledCount > 0) {
        if (!this._scheduledCount.compareAndSet(scheduledCount, scheduledCount - 1)) {
          continue;
        }

        return;
      }

      return;
    }
  }

  public boolean isDoRespawn() {
    return this._doRespawn;
  }

  public Reflection getReflection() {
    return this._reflection;
  }

  public void setReflection(Reflection reflection) {
    this._reflection = reflection;
  }

  public long getRespawnDelay() {
    return this._respawnDelay;
  }

  public long getRespawnDelayRandom() {
    return this._respawnDelayRandom;
  }

  public long getRespawnDelayWithRnd() {
    return this._respawnDelayRandom == 0L ? this._respawnDelay : Rnd.get(this._respawnDelay - this._respawnDelayRandom, this._respawnDelay);
  }

  public int getRespawnTime() {
    return this._respawnTime;
  }

  public NpcInstance getLastSpawn() {
    return this._lastSpawn;
  }

  public void setAmount(int amount) {
    if (this._referenceCount == 0) {
      this._referenceCount = amount;
    }

    this._maximumCount = amount;
  }

  public void deleteAll() {
    this.stopRespawn();

    for (NpcInstance npc : this._spawned) {
      npc.deleteMe();
    }

    this._spawned.clear();
    this._respawnTime = 0;
    this._scheduledCount.set(0);
    this._currentCount.set(0);
  }

  public abstract void decreaseCount(NpcInstance var1);

  public abstract NpcInstance doSpawn(boolean var1);

  public abstract void respawnNpc(NpcInstance var1);

  protected abstract NpcInstance initNpc(NpcInstance var1, boolean var2, MultiValueSet<String> var3);

  public abstract int getCurrentNpcId();

  public abstract SpawnRange getCurrentSpawnRange();

  public int init() {
    while (this._currentCount.get() + this._scheduledCount.get() < this._maximumCount) {
      this.doSpawn(false);
    }

    this._doRespawn = true;
    return this._currentCount.get();
  }

  public NpcInstance spawnOne() {
    return this.doSpawn(false);
  }

  public void stopRespawn() {
    this._doRespawn = false;
  }

  public void startRespawn() {
    this._doRespawn = true;
  }

  public List<NpcInstance> getAllSpawned() {
    return this._spawned;
  }

  public NpcInstance getFirstSpawned() {
    List<NpcInstance> npcs = this.getAllSpawned();
    return npcs.size() > 0 ? (NpcInstance) npcs.get(0) : null;
  }

  public void setRespawnDelay(long respawnDelay, long respawnDelayRandom) {
    if (respawnDelay < 0L) {
      log.warn("respawn delay is negative");
    }

    this._respawnDelay = respawnDelay;
    this._respawnDelayRandom = respawnDelayRandom;
  }

  public void setRespawnDelay(int respawnDelay) {
    this.setRespawnDelay((long) respawnDelay, 0L);
  }

  public void setRespawnTime(int respawnTime) {
    this._respawnTime = respawnTime;
  }

  public NextTime getRespawnCron() {
    return this._respawnCron;
  }

  public void setRespawnCron(NextTime respawnCron) {
    this._respawnCron = respawnCron;
  }

  protected NpcInstance doSpawn0(NpcTemplate template, boolean spawn, MultiValueSet<String> set) {
    if (!template.isInstanceOf(PetInstance.class) && !template.isInstanceOf(MinionInstance.class)) {
      NpcInstance tmp = template.getNewInstance();
      if (tmp == null) {
        return null;
      } else {
        if (!spawn) {
          spawn = (long) this._respawnTime <= System.currentTimeMillis() / 1000L + 300L;
        }

        return this.initNpc(tmp, spawn, set);
      }
    } else {
      this._currentCount.incrementAndGet();
      return null;
    }
  }

  protected NpcInstance initNpc0(NpcInstance mob, Location newLoc, boolean spawn, MultiValueSet<String> set) {
    mob.setParameters(set);
    mob.setCurrentHpMp((double) mob.getMaxHp(), (double) mob.getMaxMp(), true);
    mob.setSpawn(this);
    mob.setSpawnedLoc(newLoc);
    mob.setUnderground(GeoEngine.getHeight(newLoc, this.getReflection().getGeoIndex()) < GeoEngine.getHeight(newLoc.clone().changeZ(5000), this.getReflection().getGeoIndex()));

    for (GlobalEvent e : this.getEvents()) {
      mob.addEvent(e);
    }

    if (spawn) {
      mob.setReflection(this.getReflection());
      if (mob.isMonster()) {
        ((MonsterInstance) mob).setChampion();
      }

      mob.spawnMe(newLoc);
      this._currentCount.incrementAndGet();
    } else {
      mob.setLoc(newLoc);
      this._scheduledCount.incrementAndGet();
      SpawnTaskManager.getInstance().addSpawnTask(mob, (long) this._respawnTime * 1000L - System.currentTimeMillis());
    }

    this._spawned.add(mob);
    this._lastSpawn = mob;
    return mob;
  }

  public void decreaseCount0(NpcTemplate template, NpcInstance spawnedNpc, long deadTime) {
    int currentCount;
    do {
      currentCount = this._currentCount.get();
    } while (currentCount > 0 && !this._currentCount.compareAndSet(currentCount, currentCount - 1));

    if (this.getRespawnDelay() != 0L || this.getRespawnCron() != null) {
      if (this._doRespawn && this._scheduledCount.get() + this._currentCount.get() < this._maximumCount) {
        long now = System.currentTimeMillis();
        long delay;
        if (this.getRespawnCron() == null) {
          if (template.isRaid) {
            delay = (long) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (double) this.getRespawnDelayWithRnd()) * 1000L;
          } else {
            delay = this.getRespawnDelayWithRnd() * 1000L;
          }
        } else {
          delay = this.getRespawnCron().next(now) - now;
        }

        delay = Math.max(1000L, delay - deadTime);
        this._respawnTime = (int) ((now + delay) / 1000L);
        this._scheduledCount.incrementAndGet();
        SpawnTaskManager.getInstance().addSpawnTask(spawnedNpc, delay);
      }

    }
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (NpcInstance spawnedNpc : this._spawned) {
      sb.append(spawnedNpc.getNpcId());
    }

    return "Spawner{_currentCount=" + this._currentCount + ", _maximumCount=" + this._maximumCount + ", _referenceCount=" + this._referenceCount + ", _scheduledCount=" + this._scheduledCount + ", _respawnDelay=" + this._respawnDelay + ", _respawnCron=" + this._respawnCron + ", _respawnDelayRandom=" + this._respawnDelayRandom + ", _respawnTime=" + this._respawnTime + ", _doRespawn=" + this._doRespawn + ", _lastSpawn=" + this._lastSpawn + ", _spawned=" + this._spawned + ", _reflection=" + this._reflection + '}';
  }
}
