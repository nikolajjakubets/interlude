//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.residence;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.tables.SkillTable;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class ResidenceFunction {
  public static final int TELEPORT = 1;
  public static final int ITEM_CREATE = 2;
  public static final int RESTORE_HP = 3;
  public static final int RESTORE_MP = 4;
  public static final int RESTORE_EXP = 5;
  public static final int SUPPORT = 6;
  public static final int CURTAIN = 7;
  public static final int PLATFORM = 8;
  private int _id;
  private int _type;
  private int _level;
  private Calendar _endDate;
  private boolean _inDebt;
  private boolean _active;
  private Map<Integer, Integer> _leases = new ConcurrentSkipListMap<>();
  private Map<Integer, TeleportLocation[]> _teleports = new ConcurrentSkipListMap<>();
  private Map<Integer, int[]> _buylists = new ConcurrentSkipListMap<>();
  private Map<Integer, Object[][]> _buffs = new ConcurrentSkipListMap<>();
  public static final String A = "";
  public static final String W = "W";
  public static final String M = "M";
  private static final Object[][][] buffs_template = new Object[][][]{new Object[0][], {{SkillTable.getInstance().getInfo(4342, 1), ""}, {SkillTable.getInstance().getInfo(4343, 1), ""}, {SkillTable.getInstance().getInfo(4344, 1), ""}, {SkillTable.getInstance().getInfo(4346, 1), ""}, {SkillTable.getInstance().getInfo(4345, 1), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 2), ""}, {SkillTable.getInstance().getInfo(4349, 1), ""}, {SkillTable.getInstance().getInfo(4350, 1), "W"}, {SkillTable.getInstance().getInfo(4348, 2), ""}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 2), ""}, {SkillTable.getInstance().getInfo(4349, 1), ""}, {SkillTable.getInstance().getInfo(4350, 1), "W"}, {SkillTable.getInstance().getInfo(4348, 2), ""}, {SkillTable.getInstance().getInfo(4351, 2), "M"}, {SkillTable.getInstance().getInfo(4352, 1), ""}, {SkillTable.getInstance().getInfo(4353, 2), "W"}, {SkillTable.getInstance().getInfo(4358, 1), "W"}, {SkillTable.getInstance().getInfo(4354, 1), "W"}}, new Object[0][], {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 6), ""}, {SkillTable.getInstance().getInfo(4349, 2), ""}, {SkillTable.getInstance().getInfo(4350, 4), "W"}, {SkillTable.getInstance().getInfo(4348, 6), ""}, {SkillTable.getInstance().getInfo(4351, 6), "M"}, {SkillTable.getInstance().getInfo(4352, 2), ""}, {SkillTable.getInstance().getInfo(4353, 6), "W"}, {SkillTable.getInstance().getInfo(4358, 3), "W"}, {SkillTable.getInstance().getInfo(4354, 4), "W"}}, {{SkillTable.getInstance().getInfo(4342, 2), ""}, {SkillTable.getInstance().getInfo(4343, 3), ""}, {SkillTable.getInstance().getInfo(4344, 3), ""}, {SkillTable.getInstance().getInfo(4346, 4), ""}, {SkillTable.getInstance().getInfo(4345, 3), "W"}, {SkillTable.getInstance().getInfo(4347, 6), ""}, {SkillTable.getInstance().getInfo(4349, 2), ""}, {SkillTable.getInstance().getInfo(4350, 4), "W"}, {SkillTable.getInstance().getInfo(4348, 6), ""}, {SkillTable.getInstance().getInfo(4351, 6), "M"}, {SkillTable.getInstance().getInfo(4352, 2), ""}, {SkillTable.getInstance().getInfo(4353, 6), "W"}, {SkillTable.getInstance().getInfo(4358, 3), "W"}, {SkillTable.getInstance().getInfo(4354, 4), "W"}, {SkillTable.getInstance().getInfo(4355, 1), "M"}, {SkillTable.getInstance().getInfo(4356, 1), "M"}, {SkillTable.getInstance().getInfo(4357, 1), "W"}, {SkillTable.getInstance().getInfo(4359, 1), "W"}, {SkillTable.getInstance().getInfo(4360, 1), "W"}}, new Object[0][], new Object[0][], {{SkillTable.getInstance().getInfo(4342, 3), ""}, {SkillTable.getInstance().getInfo(4343, 4), ""}, {SkillTable.getInstance().getInfo(4344, 4), ""}, {SkillTable.getInstance().getInfo(4346, 5), ""}, {SkillTable.getInstance().getInfo(4345, 4), "W"}}, {{SkillTable.getInstance().getInfo(4342, 4), ""}, {SkillTable.getInstance().getInfo(4343, 6), ""}, {SkillTable.getInstance().getInfo(4344, 6), ""}, {SkillTable.getInstance().getInfo(4346, 8), ""}, {SkillTable.getInstance().getInfo(4345, 6), "W"}}, {{SkillTable.getInstance().getInfo(4342, 4), ""}, {SkillTable.getInstance().getInfo(4343, 6), ""}, {SkillTable.getInstance().getInfo(4344, 6), ""}, {SkillTable.getInstance().getInfo(4346, 8), ""}, {SkillTable.getInstance().getInfo(4345, 6), "W"}}, {{SkillTable.getInstance().getInfo(4342, 4), ""}, {SkillTable.getInstance().getInfo(4343, 6), ""}, {SkillTable.getInstance().getInfo(4344, 6), ""}, {SkillTable.getInstance().getInfo(4346, 8), ""}, {SkillTable.getInstance().getInfo(4345, 6), "W"}, {SkillTable.getInstance().getInfo(4347, 8), ""}, {SkillTable.getInstance().getInfo(4349, 3), ""}, {SkillTable.getInstance().getInfo(4350, 5), "W"}, {SkillTable.getInstance().getInfo(4348, 8), ""}}, {{SkillTable.getInstance().getInfo(4342, 4), ""}, {SkillTable.getInstance().getInfo(4343, 6), ""}, {SkillTable.getInstance().getInfo(4344, 6), ""}, {SkillTable.getInstance().getInfo(4346, 8), ""}, {SkillTable.getInstance().getInfo(4345, 6), "W"}, {SkillTable.getInstance().getInfo(4347, 8), ""}, {SkillTable.getInstance().getInfo(4349, 3), ""}, {SkillTable.getInstance().getInfo(4350, 5), "W"}, {SkillTable.getInstance().getInfo(4348, 8), ""}, {SkillTable.getInstance().getInfo(4351, 8), "M"}, {SkillTable.getInstance().getInfo(4352, 3), ""}, {SkillTable.getInstance().getInfo(4353, 8), "W"}, {SkillTable.getInstance().getInfo(4358, 4), "W"}, {SkillTable.getInstance().getInfo(4354, 5), "W"}}, new Object[0][], {{SkillTable.getInstance().getInfo(4342, 4), ""}, {SkillTable.getInstance().getInfo(4343, 6), ""}, {SkillTable.getInstance().getInfo(4344, 6), ""}, {SkillTable.getInstance().getInfo(4346, 8), ""}, {SkillTable.getInstance().getInfo(4345, 6), "W"}, {SkillTable.getInstance().getInfo(4347, 12), ""}, {SkillTable.getInstance().getInfo(4349, 4), ""}, {SkillTable.getInstance().getInfo(4350, 8), "W"}, {SkillTable.getInstance().getInfo(4348, 12), ""}, {SkillTable.getInstance().getInfo(4351, 12), "M"}, {SkillTable.getInstance().getInfo(4352, 4), ""}, {SkillTable.getInstance().getInfo(4353, 12), "W"}, {SkillTable.getInstance().getInfo(4358, 6), "W"}, {SkillTable.getInstance().getInfo(4354, 8), "W"}}, {{SkillTable.getInstance().getInfo(4342, 4), ""}, {SkillTable.getInstance().getInfo(4343, 6), ""}, {SkillTable.getInstance().getInfo(4344, 6), ""}, {SkillTable.getInstance().getInfo(4346, 8), ""}, {SkillTable.getInstance().getInfo(4345, 6), "W"}, {SkillTable.getInstance().getInfo(4347, 12), ""}, {SkillTable.getInstance().getInfo(4349, 4), ""}, {SkillTable.getInstance().getInfo(4350, 8), "W"}, {SkillTable.getInstance().getInfo(4348, 12), ""}, {SkillTable.getInstance().getInfo(4351, 12), "M"}, {SkillTable.getInstance().getInfo(4352, 4), ""}, {SkillTable.getInstance().getInfo(4353, 12), "W"}, {SkillTable.getInstance().getInfo(4358, 6), "W"}, {SkillTable.getInstance().getInfo(4354, 8), "W"}, {SkillTable.getInstance().getInfo(4355, 4), "M"}, {SkillTable.getInstance().getInfo(4356, 4), "M"}, {SkillTable.getInstance().getInfo(4357, 3), "W"}, {SkillTable.getInstance().getInfo(4359, 4), "W"}, {SkillTable.getInstance().getInfo(4360, 4), "W"}}};

  public ResidenceFunction(int id, int type) {
    this._id = id;
    this._type = type;
    this._endDate = Calendar.getInstance();
  }

  public int getResidenceId() {
    return this._id;
  }

  public int getType() {
    return this._type;
  }

  public int getLevel() {
    return this._level;
  }

  public void setLvl(int lvl) {
    this._level = lvl;
  }

  public long getEndTimeInMillis() {
    return this._endDate.getTimeInMillis();
  }

  public void setEndTimeInMillis(long time) {
    this._endDate.setTimeInMillis(time);
  }

  public void setInDebt(boolean inDebt) {
    this._inDebt = inDebt;
  }

  public boolean isInDebt() {
    return this._inDebt;
  }

  public void setActive(boolean active) {
    this._active = active;
  }

  public boolean isActive() {
    return this._active;
  }

  public void updateRentTime(boolean inDebt) {
    this.setEndTimeInMillis(System.currentTimeMillis() + 86400000L);
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE residence_functions SET endTime=?, inDebt=? WHERE type=? AND id=?");
      statement.setInt(1, (int) (this.getEndTimeInMillis() / 1000L));
      statement.setInt(2, inDebt ? 1 : 0);
      statement.setInt(3, this.getType());
      statement.setInt(4, this.getResidenceId());
      statement.executeUpdate();
    } catch (Exception e) {
      log.error("updateRentTime: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public TeleportLocation[] getTeleports() {
    return this.getTeleports(this._level);
  }

  public TeleportLocation[] getTeleports(int level) {
    return this._teleports.get(level);
  }

  public void addTeleports(int level, TeleportLocation[] teleports) {
    this._teleports.put(level, teleports);
  }

  public int getLease() {
    return this._level == 0 ? 0 : this.getLease(this._level);
  }

  public int getLease(int level) {
    return this._leases.get(level);
  }

  public void addLease(int level, int lease) {
    this._leases.put(level, lease);
  }

  public int[] getBuylist() {
    return this.getBuylist(this._level);
  }

  public int[] getBuylist(int level) {
    return this._buylists.get(level);
  }

  public void addBuylist(int level, int[] buylist) {
    this._buylists.put(level, buylist);
  }

  public Object[][] getBuffs() {
    return this.getBuffs(this._level);
  }

  public Object[][] getBuffs(int level) {
    return this._buffs.get(level);
  }

  public void addBuffs(int level) {
    this._buffs.put(level, buffs_template[level]);
  }

  public Set<Integer> getLevels() {
    return this._leases.keySet();
  }
}
