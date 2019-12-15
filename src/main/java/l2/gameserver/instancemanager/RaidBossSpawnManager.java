//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.instances.ReflectionBossInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.GmListTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.SqlBatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RaidBossSpawnManager {
  private static RaidBossSpawnManager _instance;
  protected static Map<Integer, Spawner> _spawntable = new ConcurrentHashMap<>();
  protected static Map<Integer, StatsSet> _storedInfo;
  protected static Map<Integer, Map<Integer, Integer>> _points;
  public static final Integer KEY_RANK = -1;
  public static final Integer KEY_TOTAL_POINTS = 0;
  private Lock pointsLock = new ReentrantLock();

  private RaidBossSpawnManager() {
    if (!Config.DONTLOADSPAWN) {
      this.reloadBosses();
    }

  }

  public void reloadBosses() {
    this.loadStatus();
    this.restorePointsTable();
    this.calculateRanking();
  }

  public void cleanUp() {
    this.updateAllStatusDb();
    this.updatePointsDb();
    _storedInfo.clear();
    _spawntable.clear();
    _points.clear();
  }

  public static RaidBossSpawnManager getInstance() {
    if (_instance == null) {
      _instance = new RaidBossSpawnManager();
    }

    return _instance;
  }

  private void loadStatus() {
    _storedInfo = new ConcurrentHashMap<>();
    Connection con = null;
    Statement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      rset = con.createStatement().executeQuery("SELECT * FROM `raidboss_status`");

      while(rset.next()) {
        int id = rset.getInt("id");
        StatsSet info = new StatsSet();
        info.set("current_hp", rset.getDouble("current_hp"));
        info.set("current_mp", rset.getDouble("current_mp"));
        info.set("respawn_delay", rset.getInt("respawn_delay"));
        _storedInfo.put(id, info);
      }
    } catch (Exception var9) {
      log.warn("RaidBossSpawnManager: Couldnt load raidboss statuses");
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    log.info("RaidBossSpawnManager: Loaded " + _storedInfo.size() + " Statuses");
  }

  public void updateAllStatusDb() {

    for (int id : _storedInfo.keySet()) {
      this.updateStatusDb(id);
    }

  }

  private static void addRespawnAnnounce(final int npcId, long respawnDelay) {
    if (Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY > 0 && respawnDelay > 0L && ArrayUtils.contains(Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS, npcId)) {
      long now = System.currentTimeMillis() / 1000L;
      if (respawnDelay - (long)Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY > now) {
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() {
            NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(npcId);
            if (npcTemplate != null) {
              Announcements.getInstance().announceByCustomMessage("l2.gameserver.instancemanager.RaidBossSpawnManager.AltAnnounceRaidbossSpawnSoon", new String[]{npcTemplate.getName()});
            }

          }
        }, (respawnDelay - (long)Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY - now) * 1000L);
      }
    }

  }

  private void updateStatusDb(int id) {
    Spawner spawner = _spawntable.get(id);
    if (spawner != null) {
      StatsSet info = _storedInfo.get(id);
      if (info == null) {
        _storedInfo.put(id, info = new StatsSet());
      }

      NpcInstance raidboss = spawner.getFirstSpawned();
      if (!(raidboss instanceof ReflectionBossInstance)) {
        int respawnDelay = 0;
        if (raidboss != null && !raidboss.isDead()) {
          info.set("current_hp", raidboss.getCurrentHp());
          info.set("current_mp", raidboss.getCurrentMp());
          info.set("respawn_delay", 0);
        } else {
          respawnDelay = spawner.getRespawnTime();
          info.set("current_hp", 0);
          info.set("current_mp", 0);
          info.set("respawn_delay", respawnDelay);
          addRespawnAnnounce(id, respawnDelay);
        }

        Log.add("updateStatusDb id=" + id + " current_hp=" + info.getDouble("current_hp") + " current_mp=" + info.getDouble("current_mp") + " respawn_delay=" + info.getInteger("respawn_delay", 0) + (raidboss != null ? " respawnTime=" + raidboss.getSpawn().getRespawnTime() : ""), "RaidBossSpawnManager");
        Connection con = null;
        PreparedStatement statement = null;

        try {
          con = DatabaseFactory.getInstance().getConnection();
          statement = con.prepareStatement("REPLACE INTO `raidboss_status` (id, current_hp, current_mp, respawn_delay) VALUES (?,?,?,?)");
          statement.setInt(1, id);
          statement.setInt(2, (int)info.getDouble("current_hp"));
          statement.setInt(3, (int)info.getDouble("current_mp"));
          statement.setInt(4, respawnDelay);
          statement.execute();
        } catch (SQLException var12) {
          log.warn("RaidBossSpawnManager: Couldnt update raidboss_status table");
        } finally {
          DbUtils.closeQuietly(con, statement);
        }

      }
    }
  }

  public void addNewSpawn(int npcId, Spawner spawnDat) {
    if (!_spawntable.containsKey(npcId)) {
      _spawntable.put(npcId, spawnDat);
      StatsSet info = _storedInfo.get(npcId);
      if (info != null) {
        long respawnTime = info.getLong("respawn_delay", 0L);
        spawnDat.setRespawnTime((int)respawnTime);
        Log.add("AddSpawn npc=" + npcId + " respawnDelay=" + spawnDat.getRespawnDelay() + " respawnDelayRandom=" + spawnDat.getRespawnDelayRandom() + " respawnCron=" + spawnDat.getRespawnCron() + " respawn_delay=" + respawnTime, "RaidBossSpawnManager");
        if (respawnTime > 0L) {
          addRespawnAnnounce(npcId, respawnTime);
        }
      }

    }
  }

  public void onBossSpawned(RaidBossInstance raidboss) {
    int bossId = raidboss.getNpcId();
    if (_spawntable.containsKey(bossId)) {
      StatsSet info = _storedInfo.get(bossId);
      if (info != null && info.getDouble("current_hp") > 1.0D) {
        raidboss.setCurrentHp(info.getDouble("current_hp"), false);
        raidboss.setCurrentMp(info.getDouble("current_mp"));
      }

      Log.add("onBossSpawned npc=" + bossId + " current_hp=" + raidboss.getCurrentHp() + " current_mp=" + raidboss.getCurrentMp(), "RaidBossSpawnManager");
      GmListTable.broadcastMessageToGMs("Spawning RaidBoss " + raidboss.getName());
      if (ArrayUtils.contains(Config.ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS, raidboss.getNpcId())) {
        Announcements.getInstance().announceByCustomMessage("l2.gameserver.instancemanager.RaidBossSpawnManager.AltAnnounceRaidbossSpawn", new String[]{raidboss.getName()});
      }

    }
  }

  public void onBossDespawned(RaidBossInstance raidboss) {
    this.updateStatusDb(raidboss.getNpcId());
  }

  public RaidBossSpawnManager.Status getRaidBossStatusId(int bossId) {
    Spawner spawner = _spawntable.get(bossId);
    if (spawner == null) {
      return RaidBossSpawnManager.Status.UNDEFINED;
    } else {
      NpcInstance npc = spawner.getFirstSpawned();
      return npc == null ? RaidBossSpawnManager.Status.DEAD : RaidBossSpawnManager.Status.ALIVE;
    }
  }

  public boolean isDefined(int bossId) {
    return _spawntable.containsKey(bossId);
  }

  public Map<Integer, Spawner> getSpawnTable() {
    return _spawntable;
  }

  private void restorePointsTable() {
    this.pointsLock.lock();
    _points = new ConcurrentHashMap<>();
    Connection con = null;
    Statement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.createStatement();
      rset = statement.executeQuery("SELECT owner_id, boss_id, points FROM `raidboss_points` ORDER BY owner_id ASC");
      int currentOwner = 0;
      HashMap score = null;

      while(rset.next()) {
        if (currentOwner != rset.getInt("owner_id")) {
          currentOwner = rset.getInt("owner_id");
          score = new HashMap<>();
          _points.put(currentOwner, score);
        }

        assert score != null;

        int bossId = rset.getInt("boss_id");
        NpcTemplate template = NpcHolder.getInstance().getTemplate(bossId);
        if (bossId != KEY_RANK && bossId != KEY_TOTAL_POINTS && template != null && template.rewardRp > 0) {
          score.put(bossId, rset.getInt("points"));
        }
      }
    } catch (Exception var11) {
      log.warn("RaidBossSpawnManager: Couldnt load raidboss points");
      log.error("", var11);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    this.pointsLock.unlock();
  }

  public void updatePointsDb() {
    this.pointsLock.lock();
    if (!mysql.set("TRUNCATE `raidboss_points`")) {
      log.warn("RaidBossSpawnManager: Couldnt empty raidboss_points table");
    }

    if (_points.isEmpty()) {
      this.pointsLock.unlock();
    } else {
      Connection con = null;
      Statement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.createStatement();
        SqlBatch b = new SqlBatch("INSERT INTO `raidboss_points` (owner_id, boss_id, points) VALUES");
        Iterator var5 = _points.entrySet().iterator();

        label117:
        while(true) {
          Entry pointEntry;
          Map tmpPoint;
          do {
            do {
              if (!var5.hasNext()) {
                if (!b.isEmpty()) {
                  statement.executeUpdate(b.close());
                }
                break label117;
              }

              pointEntry = (Entry)var5.next();
              tmpPoint = (Map)pointEntry.getValue();
            } while(tmpPoint == null);
          } while(tmpPoint.isEmpty());

          for (Object o : tmpPoint.entrySet()) {
            Entry<Integer, Integer> pointListEntry = (Entry) o;
            if (!KEY_RANK.equals(pointListEntry.getKey()) && !KEY_TOTAL_POINTS.equals(pointListEntry.getKey()) && pointListEntry.getValue() != null && pointListEntry.getValue() != 0) {
              String sb = "(" + pointEntry.getKey() + "," +
                pointListEntry.getKey() + "," +
                pointListEntry.getValue() + ")";
              b.write(sb);
            }
          }
        }
      } catch (SQLException var13) {
        log.warn("RaidBossSpawnManager: Couldnt update raidboss_points table");
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

      this.pointsLock.unlock();
    }
  }

  public void deletePoints(int ownerId) {
    if (ownerId > 0) {
      this.pointsLock.lock();

      try {
        _points.remove(ownerId);
      } finally {
        this.pointsLock.unlock();
      }

    }
  }

  public void addPoints(int ownerId, int bossId, int points) {
    if (points > 0 && ownerId > 0 && bossId > 0) {
      this.pointsLock.lock();
      Map<Integer, Integer> pointsTable = _points.get(ownerId);
      if (pointsTable == null) {
        pointsTable = new HashMap<>();
        _points.put(ownerId, pointsTable);
      }

      if (pointsTable.isEmpty()) {
        ((Map)pointsTable).put(bossId, points);
      } else {
        Integer currentPoins = (Integer)((Map)pointsTable).get(bossId);
        ((Map)pointsTable).put(bossId, currentPoins == null ? points : currentPoins + points);
      }

      this.pointsLock.unlock();
    }
  }

  public TreeMap<Integer, Integer> calculateRanking() {
    TreeMap<Integer, Integer> tmpRanking = new TreeMap<>();
    this.pointsLock.lock();

    for (Entry<Integer, Map<Integer, Integer>> integerMapEntry : _points.entrySet()) {
      Map<Integer, Integer> tmpPoint = integerMapEntry.getValue();
      tmpPoint.remove(KEY_RANK);
      tmpPoint.remove(KEY_TOTAL_POINTS);
      int totalPoints = 0;

      Entry e;
      for (Iterator var6 = tmpPoint.entrySet().iterator(); var6.hasNext(); totalPoints += (Integer) e.getValue()) {
        e = (Entry) var6.next();
      }

      if (totalPoints != 0) {
        tmpPoint.put(KEY_TOTAL_POINTS, totalPoints);
        tmpRanking.put(totalPoints, integerMapEntry.getKey());
      }
    }

    int ranking = 1;

    for(Iterator var9 = tmpRanking.descendingMap().entrySet().iterator(); var9.hasNext(); ++ranking) {
      Entry<Integer, Integer> entry = (Entry)var9.next();
      Map<Integer, Integer> tmpPoint = _points.get(entry.getValue());
      tmpPoint.put(KEY_RANK, ranking);
    }

    this.pointsLock.unlock();
    return tmpRanking;
  }

  public void distributeRewards() {
    this.pointsLock.lock();
    TreeMap<Integer, Integer> ranking = this.calculateRanking();
    Iterator<Integer> e = ranking.descendingMap().values().iterator();

    for(int counter = 1; e.hasNext() && counter <= 100; ++counter) {
      int reward = 0;
      int playerId = e.next();
      if (counter == 1) {
        reward = 2500;
      } else if (counter == 2) {
        reward = 1800;
      } else if (counter == 3) {
        reward = 1400;
      } else if (counter == 4) {
        reward = 1200;
      } else if (counter == 5) {
        reward = 900;
      } else if (counter == 6) {
        reward = 700;
      } else if (counter == 7) {
        reward = 600;
      } else if (counter == 8) {
        reward = 400;
      } else if (counter == 9) {
        reward = 300;
      } else if (counter == 10) {
        reward = 200;
      } else if (counter <= 50) {
        reward = 50;
      } else {
        reward = 25;
      }

      Player player = GameObjectsStorage.getPlayer(playerId);
      Clan clan;
      if (player != null) {
        clan = player.getClan();
      } else {
        clan = ClanTable.getInstance().getClan(mysql.simple_get_int("clanid", "characters", "obj_Id=" + playerId));
      }

      if (clan != null) {
        clan.incReputation(reward, true, "RaidPoints");
      }
    }

    _points.clear();
    this.updatePointsDb();
    this.pointsLock.unlock();
  }

  public Map<Integer, Map<Integer, Integer>> getPoints() {
    return _points;
  }

  public Map<Integer, Integer> getPointsForOwnerId(int ownerId) {
    return _points.get(ownerId);
  }

  public enum Status {
    ALIVE,
    DEAD,
    UNDEFINED;

    Status() {
    }
  }
}
