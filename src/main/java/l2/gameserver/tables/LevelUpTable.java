//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Creature;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

@Slf4j
public class LevelUpTable {
  private static LevelUpTable _instance;
  private int _maxLvl;
  private int _maxClassID;
  private double[] _hp_table;
  private double[] _cp_table;
  private double[] _mp_table;

  public static LevelUpTable getInstance() {
    if (_instance == null) {
      _instance = new LevelUpTable();
    }

    return _instance;
  }

  private LevelUpTable() {
    this.loadData();
  }

  private void loadData() {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rset = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      stmt = conn.createStatement();
      rset = stmt.executeQuery("SELECT MAX(`lvl`) FROM `lvlupgain`");
      if (rset.next()) {
        this._maxLvl = rset.getInt(1);
      }

      DbUtils.closeQuietly(stmt, rset);
      stmt = conn.createStatement();
      rset = stmt.executeQuery("SELECT MAX(`class_id`) FROM `lvlupgain`");
      if (rset.next()) {
        this._maxClassID = rset.getInt(1);
      }

      DbUtils.closeQuietly(stmt, rset);
      int max_idx = this.getIdx(this._maxLvl, this._maxClassID) + 1;
      this._hp_table = new double[max_idx];
      this._cp_table = new double[max_idx];
      this._mp_table = new double[max_idx];
      stmt = conn.createStatement();

      int idx;
      for (rset = stmt.executeQuery("SELECT `class_id`,`lvl`,`hp`,`cp`,`mp` FROM `lvlupgain`"); rset.next(); this._mp_table[idx] = rset.getDouble("mp")) {
        idx = this.getIdx(rset.getInt("lvl"), rset.getInt("class_id"));
        this._hp_table[idx] = rset.getDouble("hp");
        this._cp_table[idx] = rset.getDouble("cp");
      }
    } catch (SQLException e) {
      log.error("Can't load lvlupgain table " + Level.SEVERE);
      log.error("closeQuietly: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    } finally {
      DbUtils.closeQuietly(conn, stmt, rset);
    }

  }

  public double getMaxHP(Creature character) {
    return character.isPlayer() ? this._hp_table[this.getIdx(character.getLevel(), character.getPlayer().getClassId().getId())] : character.getTemplate().baseHpMax;
  }

  public double getMaxCP(Creature character) {
    return character.isPlayer() ? this._cp_table[this.getIdx(character.getLevel(), character.getPlayer().getClassId().getId())] : character.getTemplate().baseCpMax;
  }

  public double getMaxMP(Creature character) {
    return character.isPlayer() ? this._mp_table[this.getIdx(character.getLevel(), character.getPlayer().getClassId().getId())] : character.getTemplate().baseMpMax;
  }

  private int getIdx(int lvl, int class_id) {
    return lvl << 8 | class_id & 255;
  }
}
