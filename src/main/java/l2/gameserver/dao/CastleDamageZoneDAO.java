//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.entity.residence.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CastleDamageZoneDAO {
  private static final CastleDamageZoneDAO _instance = new CastleDamageZoneDAO();
  private static final Logger _log = LoggerFactory.getLogger(CastleDoorUpgradeDAO.class);
  public static final String SELECT_SQL_QUERY = "SELECT zone FROM castle_damage_zones WHERE residence_id=?";
  public static final String INSERT_SQL_QUERY = "INSERT INTO castle_damage_zones (residence_id, zone) VALUES (?,?)";
  public static final String DELETE_SQL_QUERY = "DELETE FROM castle_damage_zones WHERE residence_id=?";

  public CastleDamageZoneDAO() {
  }

  public static CastleDamageZoneDAO getInstance() {
    return _instance;
  }

  public List<String> load(Residence r) {
    List<String> set = Collections.emptyList();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT zone FROM castle_damage_zones WHERE residence_id=?");
      statement.setInt(1, r.getId());
      rset = statement.executeQuery();
      set = new ArrayList<>();

      while(rset.next()) {
        ((List)set).add(rset.getString("zone"));
      }
    } catch (Exception var10) {
      _log.error("CastleDamageZoneDAO:load(Residence): " + var10, var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return (List)set;
  }

  public void insert(Residence residence, String name) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("INSERT INTO castle_damage_zones (residence_id, zone) VALUES (?,?)");
      statement.setInt(1, residence.getId());
      statement.setString(2, name);
      statement.execute();
    } catch (Exception var9) {
      _log.error("CastleDamageZoneDAO:insert(Residence, String): " + var9, var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void delete(Residence residence) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM castle_damage_zones WHERE residence_id=?");
      statement.setInt(1, residence.getId());
      statement.execute();
    } catch (Exception var8) {
      _log.error("CastleDamageZoneDAO:delete(Residence): " + var8, var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }
}
