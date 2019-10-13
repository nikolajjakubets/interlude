//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.ArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @deprecated */
@Deprecated
public class SimpleSpawner extends Spawner implements Cloneable {
  private static final Logger _log = LoggerFactory.getLogger(SimpleSpawner.class);
  private NpcTemplate _npcTemplate;
  private int _locx;
  private int _locy;
  private int _locz;
  private int _heading;
  private Territory _territory;

  public SimpleSpawner(NpcTemplate mobTemplate) {
    if (mobTemplate == null) {
      throw new NullPointerException();
    } else {
      this._npcTemplate = mobTemplate;
      this._spawned = new ArrayList(1);
    }
  }

  public SimpleSpawner(int npcId) {
    NpcTemplate mobTemplate = NpcHolder.getInstance().getTemplate(npcId);
    if (mobTemplate == null) {
      throw new NullPointerException("Not find npc: " + npcId);
    } else {
      this._npcTemplate = mobTemplate;
      this._spawned = new ArrayList(1);
    }
  }

  public int getAmount() {
    return this._maximumCount;
  }

  public int getSpawnedCount() {
    return this._currentCount.get();
  }

  public int getSheduledCount() {
    return this._scheduledCount.get();
  }

  public Territory getTerritory() {
    return this._territory;
  }

  public Location getLoc() {
    return new Location(this._locx, this._locy, this._locz);
  }

  public int getLocx() {
    return this._locx;
  }

  public int getLocy() {
    return this._locy;
  }

  public int getLocz() {
    return this._locz;
  }

  public int getCurrentNpcId() {
    return this._npcTemplate.getNpcId();
  }

  public SpawnRange getCurrentSpawnRange() {
    return (SpawnRange)(this._locx == 0 && this._locz == 0 ? this._territory : this.getLoc());
  }

  public int getHeading() {
    return this._heading;
  }

  public void restoreAmount() {
    this._maximumCount = this._referenceCount;
  }

  public void setTerritory(Territory territory) {
    this._territory = territory;
  }

  public void setLoc(Location loc) {
    this._locx = loc.x;
    this._locy = loc.y;
    this._locz = loc.z;
    this._heading = loc.h;
  }

  public void setLocx(int locx) {
    this._locx = locx;
  }

  public void setLocy(int locy) {
    this._locy = locy;
  }

  public void setLocz(int locz) {
    this._locz = locz;
  }

  public void setHeading(int heading) {
    this._heading = heading;
  }

  public void decreaseCount(NpcInstance oldNpc) {
    this.decreaseCount0(this._npcTemplate, oldNpc, oldNpc.getDeadTime());
  }

  public NpcInstance doSpawn(boolean spawn) {
    return this.doSpawn0(this._npcTemplate, spawn, StatsSet.EMPTY);
  }

  protected NpcInstance initNpc(NpcInstance mob, boolean spawn, MultiValueSet<String> set) {
    Location newLoc;
    if (this._territory != null) {
      newLoc = this._territory.getRandomLoc(this._reflection.getGeoIndex());
      newLoc.setH(Rnd.get(65535));
    } else {
      newLoc = this.getLoc();
      newLoc.h = this.getHeading() == -1 ? Rnd.get(65535) : this.getHeading();
    }

    return this.initNpc0(mob, newLoc, spawn, set);
  }

  public void respawnNpc(NpcInstance oldNpc) {
    oldNpc.refreshID();
    this.initNpc(oldNpc, true, StatsSet.EMPTY);
  }

  public SimpleSpawner clone() {
    SimpleSpawner spawnDat = new SimpleSpawner(this._npcTemplate);
    spawnDat.setTerritory(this._territory);
    spawnDat.setLocx(this._locx);
    spawnDat.setLocy(this._locy);
    spawnDat.setLocz(this._locz);
    spawnDat.setHeading(this._heading);
    spawnDat.setAmount(this._maximumCount);
    spawnDat.setRespawnDelay(this._respawnDelay, this._respawnDelayRandom);
    spawnDat.setRespawnCron(this.getRespawnCron());
    return spawnDat;
  }
}
