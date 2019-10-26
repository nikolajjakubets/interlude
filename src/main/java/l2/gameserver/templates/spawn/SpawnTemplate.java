//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.spawn;

import java.util.ArrayList;
import java.util.List;
import l2.commons.time.cron.NextTime;

public class SpawnTemplate {
  private final String _makerName;
  private final String _eventName;
  private final PeriodOfDay _periodOfDay;
  private final int _count;
  private final long _respawn;
  private final long _respawnRandom;
  private final NextTime _respawnCron;
  private final List<SpawnNpcInfo> _npcList = new ArrayList(1);
  private final List<SpawnRange> _spawnRangeList = new ArrayList(1);

  public SpawnTemplate(String makerName, String eventName, PeriodOfDay periodOfDay, int count, long respawn, long respawnRandom, NextTime respawnCron) {
    this._makerName = makerName;
    this._eventName = eventName;
    this._periodOfDay = periodOfDay;
    this._count = count;
    this._respawn = respawn;
    this._respawnRandom = respawnRandom;
    this._respawnCron = respawnCron;
  }

  public void addSpawnRange(SpawnRange range) {
    this._spawnRangeList.add(range);
  }

  public SpawnRange getSpawnRange(int index) {
    return (SpawnRange)this._spawnRangeList.get(index);
  }

  public void addNpc(SpawnNpcInfo info) {
    this._npcList.add(info);
  }

  public SpawnNpcInfo getNpcId(int index) {
    return (SpawnNpcInfo)this._npcList.get(index);
  }

  public String getMakerName() {
    return this._makerName;
  }

  public int getNpcSize() {
    return this._npcList.size();
  }

  public int getSpawnRangeSize() {
    return this._spawnRangeList.size();
  }

  public int getCount() {
    return this._count;
  }

  public long getRespawn() {
    return this._respawn;
  }

  public long getRespawnRandom() {
    return this._respawnRandom;
  }

  public NextTime getRespawnCron() {
    return this._respawnCron;
  }

  public PeriodOfDay getPeriodOfDay() {
    return this._periodOfDay;
  }

  public String getEventName() {
    return this._eventName;
  }
}
