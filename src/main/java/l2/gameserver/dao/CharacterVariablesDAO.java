//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.dao;

import l2.commons.collections.MultiValueSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.utils.Strings;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public class CharacterVariablesDAO {
  private static final CharacterVariablesDAO _instance = new CharacterVariablesDAO();
  private static final String GET_VAR = "SELECT value FROM character_variables WHERE obj_id=? AND type=? AND name=?";
  private static final String SET_VAR = "REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,?,?,?,?)";
  private static final String DELETE_VAR = "DELETE FROM character_variables WHERE obj_id=? AND type=? AND name=? LIMIT 1";
  private static final String DELETE_VARS = "DELETE FROM character_variables WHERE obj_id=?";
  private static final String LOAD_VARS = "SELECT name,value FROM character_variables WHERE obj_id=?";

  public CharacterVariablesDAO() {
  }

  public static CharacterVariablesDAO getInstance() {
    return _instance;
  }

  public String getVar(int objectId, String name) {
    return this.getVar(objectId, name, "user-var");
  }

  public String getVar(int objectId, String name, String type) {
    String value = null;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(GET_VAR);
      statement.setInt(1, objectId);
      statement.setString(2, type);
      statement.setString(3, name);
      rs = statement.executeQuery();
      if (rs.next()) {
        value = Strings.stripSlashes(rs.getString("value"));
      }
    } catch (Exception var12) {
      log.error("CharacterVariablesDAO.getVar(int,String,String): " + var12, var12);
    } finally {
      DbUtils.closeQuietly(con, statement, rs);
    }

    return value;
  }

  public void setVar(int objectId, String name, String value, long expiration) {
    this.setVar(objectId, name, "user-var", value, expiration);
  }

  public void setVar(int objectId, String name, String type, String value, long expiration) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(SET_VAR);
      statement.setInt(1, objectId);
      statement.setString(2, type);
      statement.setString(3, name);
      statement.setString(4, value);
      statement.setLong(5, expiration);
      statement.execute();
    } catch (Exception var13) {
      log.error("CharacterVariablesDAO.setVar(int,String,String,String,long): " + var13, var13);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void deleteVar(int objectId, String name) {
    this.deleteVar(objectId, name, "user-var");
  }

  public void deleteVar(int objectId, String name, String type) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(DELETE_VAR);
      statement.setInt(1, objectId);
      statement.setString(2, type);
      statement.setString(3, name);
      statement.execute();
    } catch (Exception var10) {
      log.error("CharacterVariablesDAO.deleteVar(int,String,String): " + var10, var10);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void deleteVars(int objectId) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(DELETE_VARS);
      statement.setInt(1, objectId);
      statement.execute();
    } catch (Exception var8) {
      log.error("CharacterVariablesDAO.deleteVar(int): " + var8, var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  protected void deleteVars0(Connection con, int objectId) {
    PreparedStatement statement = null;

    try {
      statement = con.prepareStatement(DELETE_VARS);
      statement.setInt(1, objectId);
      statement.execute();
    } catch (Exception var8) {
      log.error("CharacterVariablesDAO.deleteVar(int): " + var8, var8);
    } finally {
      DbUtils.closeQuietly(statement);
    }

  }

  public void loadVariables(int objectId, MultiValueSet<String> vars) {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(LOAD_VARS);
      statement.setInt(1, objectId);
      rs = statement.executeQuery();

      while(rs.next()) {
        String name = rs.getString(1);
        String value = Strings.stripSlashes(rs.getString(2));
        vars.put(name, value);
      }
    } catch (Exception var11) {
      log.error("CharacterVariablesDAO.loadVariables(int,MultiValueSet<String>): " + var11, var11);
    } finally {
      DbUtils.closeQuietly(con, statement, rs);
    }

  }
}
