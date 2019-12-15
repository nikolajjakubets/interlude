//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.dao;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public class CastleDoorUpgradeDAO {
  private static final CastleDoorUpgradeDAO _instance = new CastleDoorUpgradeDAO();
  public static final String SELECT_SQL_QUERY = "SELECT hp FROM castle_door_upgrade WHERE door_id=?";
  public static final String REPLACE_SQL_QUERY = "REPLACE INTO castle_door_upgrade (door_id, hp) VALUES (?,?)";
  public static final String DELETE_SQL_QUERY = "DELETE FROM castle_door_upgrade WHERE door_id=?";

  public CastleDoorUpgradeDAO() {
  }

  public static CastleDoorUpgradeDAO getInstance() {
    return _instance;
  }

  public int load(int doorId) {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    int var5;
    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(SELECT_SQL_QUERY);
      statement.setInt(1, doorId);
      rset = statement.executeQuery();
      if (!rset.next()) {
        return 0;
      }

      var5 = rset.getInt("hp");
    } catch (Exception var9) {
      log.error("CastleDoorUpgradeDAO:load(int): " + var9, var9);
      return 0;
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return var5;
  }

  public void insert(int uId, int val) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(REPLACE_SQL_QUERY);
      statement.setInt(1, uId);
      statement.setInt(2, val);
      statement.execute();
    } catch (Exception var9) {
      log.error("CastleDoorUpgradeDAO:insert(int, int): " + var9, var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void delete(int uId) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(DELETE_SQL_QUERY);
      statement.setInt(1, uId);
      statement.execute();
    } catch (Exception var8) {
      log.error("CastleDoorUpgradeDAO:delete(int): " + var8, var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }
}
