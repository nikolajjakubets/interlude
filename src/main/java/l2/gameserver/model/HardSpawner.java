//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.collections.MultiValueSet;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.spawn.SpawnNpcInfo;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.templates.spawn.SpawnTemplate;
import lombok.extern.slf4j.Slf4j;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class HardSpawner extends Spawner {
  private IntObjectMap<Queue<NpcInstance>> _cache = new CHashIntObjectMap<>();
  private final SpawnTemplate _template;
  private int _pointIndex;
  private int _npcIndex;
  private List<NpcInstance> _reSpawned = new CopyOnWriteArrayList<>();

  public HardSpawner(SpawnTemplate template) {
    this._template = template;
    this._spawned = new CopyOnWriteArrayList<>();
  }

  public void decreaseCount(NpcInstance oldNpc) {
    this.addToCache(oldNpc);
    this._spawned.remove(oldNpc);
    SpawnNpcInfo npcInfo = this.getNextNpcInfo();
    NpcInstance npc = this.getCachedNpc(npcInfo.getTemplate().getNpcId());
    if (npc == null) {
      npc = npcInfo.getTemplate().getNewInstance();
    } else {
      npc.refreshID();
    }

    npc.setSpawn(this);
    this._reSpawned.add(npc);
    this.decreaseCount0(npcInfo.getTemplate(), npc, oldNpc.getDeadTime());
  }

  public NpcInstance doSpawn(boolean spawn) {
    SpawnNpcInfo npcInfo = this.getNextNpcInfo();
    return this.doSpawn0(npcInfo.getTemplate(), spawn, npcInfo.getParameters());
  }

  protected NpcInstance initNpc(NpcInstance mob, boolean spawn, MultiValueSet<String> set) {
    this._reSpawned.remove(mob);
    SpawnRange range = this._template.getSpawnRange(this.getNextRangeId());
    mob.setSpawnRange(range);
    return this.initNpc0(mob, range.getRandomLoc(this.getReflection().getGeoIndex()), spawn, set);
  }

  public int getCurrentNpcId() {
    SpawnNpcInfo npcInfo = this._template.getNpcId(this._npcIndex);
    return npcInfo.getTemplate().npcId;
  }

  public SpawnRange getCurrentSpawnRange() {
    return this._template.getSpawnRange(this._pointIndex);
  }

  public void respawnNpc(NpcInstance oldNpc) {
    this.initNpc(oldNpc, true, StatsSet.EMPTY);
  }

  public void deleteAll() {
    super.deleteAll();
    Iterator var1 = this._reSpawned.iterator();

    while(var1.hasNext()) {
      NpcInstance npc = (NpcInstance)var1.next();
      this.addToCache(npc);
    }

    this._reSpawned.clear();
    var1 = this._cache.values().iterator();

    while(var1.hasNext()) {
      Collection c = (Collection)var1.next();
      c.clear();
    }

    this._cache.clear();
  }

  private synchronized SpawnNpcInfo getNextNpcInfo() {
    SpawnNpcInfo npcInfo = null;
    int attempts = 0;
    int old = this._npcIndex++;
    if (this._npcIndex >= this._template.getNpcSize()) {
      this._npcIndex = 0;
    }

    npcInfo = this._template.getNpcId(old);
    if (npcInfo.getMax() > 0) {
      int count = 0;

      for (NpcInstance npc : this._spawned) {
        if (npc.getNpcId() == npcInfo.getTemplate().getNpcId()) {
          ++count;
        }
      }

      if (count >= npcInfo.getMax()) {
        if (attempts > this._template.getNpcSize() * 2) {
          throw new IllegalStateException("getNextNpcInfo failed (" + count + ", " + npcInfo.getMax() + ", " + npcInfo.getNpcId() + ")");
        }
      }
    }

    return npcInfo;
  }

  private synchronized int getNextRangeId() {
    int old = this._pointIndex++;
    if (this._pointIndex >= this._template.getSpawnRangeSize()) {
      this._pointIndex = 0;
    }

    return old;
  }

  public HardSpawner clone() {
    HardSpawner spawnDat = new HardSpawner(this._template);
    spawnDat.setAmount(this._maximumCount);
    spawnDat.setRespawnDelay(this._respawnDelay, this._respawnDelayRandom);
    spawnDat.setRespawnTime(0);
    spawnDat.setRespawnCron(this.getRespawnCron());
    return spawnDat;
  }

  private void addToCache(NpcInstance npc) {
    npc.setSpawn(null);
    npc.decayMe();
    Queue<NpcInstance> queue = this._cache.get(npc.getNpcId());
    if (queue == null) {
      this._cache.put(npc.getNpcId(), queue = new ArrayDeque<>());
    }

    queue.add(npc);
  }

  private NpcInstance getCachedNpc(int id) {
    Queue<NpcInstance> queue = this._cache.get(id);
    if (queue == null) {
      return null;
    } else {
      NpcInstance npc = queue.poll();
      if (npc != null && npc.isDeleted()) {
        log.info("Npc: " + id + " is deleted, cant used by cache.");
        return this.getCachedNpc(id);
      } else {
        return npc;
      }
    }
  }

  public SpawnTemplate getTemplate() {
    return this._template;
  }
}
