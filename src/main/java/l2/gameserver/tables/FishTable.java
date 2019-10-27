//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import gnu.trove.TIntObjectHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.templates.FishTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishTable {
  private static final Logger _log = LoggerFactory.getLogger(FishTable.class);
  private static final FishTable _instance = new FishTable();
  private TIntObjectHashMap<List<FishTemplate>> _fishes;
  private TIntObjectHashMap<List<RewardData>> _fishRewards;

  public static final FishTable getInstance() {
    return _instance;
  }

  private FishTable() {
    this.load();
  }

  public void reload() {
    this.load();
  }

  private void load() {
    this._fishes = new TIntObjectHashMap();
    this._fishRewards = new TIntObjectHashMap();
    int count = 0;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT id, level, name, hp, hpregen, fish_type, fish_group, fish_guts, guts_check_time, wait_time, combat_time FROM fish ORDER BY id");

      int rewardid;
      int mindrop;
      int maxdrop;
      int chance;
      for(resultSet = statement.executeQuery(); resultSet.next(); ++count) {
        int id = resultSet.getInt("id");
        int lvl = resultSet.getInt("level");
        String name = resultSet.getString("name");
        rewardid = resultSet.getInt("hp");
        mindrop = resultSet.getInt("hpregen");
        maxdrop = resultSet.getInt("fish_type");
        chance = resultSet.getInt("fish_group");
        int fish_guts = resultSet.getInt("fish_guts");
        int guts_check_time = resultSet.getInt("guts_check_time");
        int wait_time = resultSet.getInt("wait_time");
        int combat_time = resultSet.getInt("combat_time");
        FishTemplate fish = new FishTemplate(id, lvl, name, rewardid, mindrop, maxdrop, chance, fish_guts, guts_check_time, wait_time, combat_time);
        Object fishes;
        if ((fishes = this._fishes.get(chance)) == null) {
          this._fishes.put(chance, new ArrayList<>());
        }

        ((List)fishes).add(fish);
      }

      DbUtils.close(statement, resultSet);
      _log.info("FishTable: Loaded " + count + " fishes.");
      count = 0;
      statement = con.prepareStatement("SELECT fishid, rewardid, min, max, chance FROM fishreward ORDER BY fishid");

      for(resultSet = statement.executeQuery(); resultSet.next(); ++count) {
        int fishid = resultSet.getInt("fishid");
        rewardid = resultSet.getInt("rewardid");
        mindrop = resultSet.getInt("min");
        maxdrop = resultSet.getInt("max");
        chance = resultSet.getInt("chance");
        RewardData reward = new RewardData(rewardid, mindrop, maxdrop, (double)chance * 10000.0D);
        Object rewards;
        if ((rewards = this._fishRewards.get(fishid)) == null) {
          this._fishRewards.put(fishid, new ArrayList<>());
        }

        ((List)rewards).add(reward);
      }

      _log.info("FishTable: Loaded " + count + " fish rewards.");
    } catch (Exception var21) {
      _log.error("", var21);
    } finally {
      DbUtils.closeQuietly(con, statement, resultSet);
    }

  }

  public int[] getFishIds() {
    return this._fishRewards.keys();
  }

  public List<FishTemplate> getFish(int group, int type, int lvl) {
    List<FishTemplate> result = new ArrayList<>();
    List<FishTemplate> fishs = this._fishes.get(group);
    if (fishs == null) {
      _log.warn("No fishes defined for group : " + group + "!");
      return null;
    } else {
      Iterator var6 = fishs.iterator();

      while(var6.hasNext()) {
        FishTemplate f = (FishTemplate)var6.next();
        if (f.getType() == type && f.getLevel() == lvl) {
          result.add(f);
        }
      }

      if (result.isEmpty()) {
        _log.warn("No fishes for group : " + group + " type: " + type + " level: " + lvl + "!");
      }

      return result;
    }
  }

  public List<RewardData> getFishReward(int fishid) {
    List<RewardData> result = this._fishRewards.get(fishid);
    if (this._fishRewards == null) {
      _log.warn("No fish rewards defined for fish id: " + fishid + "!");
      return null;
    } else {
      if (result.isEmpty()) {
        _log.warn("No fish rewards for fish id: " + fishid + "!");
      }

      return result;
    }
  }
}
