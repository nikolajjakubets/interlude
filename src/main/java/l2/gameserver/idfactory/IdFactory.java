//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.idfactory;

import gnu.trove.TIntArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IdFactory {
  private static final Logger _log = LoggerFactory.getLogger(IdFactory.class);
  public static final String[][] EXTRACT_OBJ_ID_TABLES = new String[][]{{"characters", "obj_id"}, {"items", "item_id"}, {"clan_data", "clan_id"}, {"ally_data", "ally_id"}, {"pets", "objId"}, {"couples", "id"}};
  public static final int FIRST_OID = 268435456;
  public static final int LAST_OID = 2147483647;
  public static final int FREE_OBJECT_ID_SIZE = 1879048191;
  protected static final IdFactory _instance = new BitSetIDFactory();
  protected boolean initialized;
  protected long releasedCount = 0L;

  public static final IdFactory getInstance() {
    return _instance;
  }

  protected IdFactory() {
    this.resetOnlineStatus();
  }

  private void resetOnlineStatus() {
    Connection con = null;
    Statement st = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      st = con.createStatement();
      st.executeUpdate("UPDATE characters SET online = 0");
      _log.info("IdFactory: Clear characters online status.");
    } catch (SQLException var7) {
      _log.error("", var7);
    } finally {
      DbUtils.closeQuietly(con, st);
    }

  }

  protected int[] extractUsedObjectIDTable() throws SQLException {
    TIntArrayList objectIds = new TIntArrayList();
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      st = con.createStatement();
      String[][] var5 = EXTRACT_OBJ_ID_TABLES;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        String[] table = var5[var7];
        rs = st.executeQuery("SELECT " + table[1] + " FROM " + table[0]);
        int size = objectIds.size();

        while(rs.next()) {
          objectIds.add(rs.getInt(1));
        }

        DbUtils.close(rs);
        size = objectIds.size() - size;
        if (size > 0) {
          _log.info("IdFactory: Extracted " + size + " used id's from " + table[0]);
        }
      }
    } finally {
      DbUtils.closeQuietly(con, st, rs);
    }

    int[] extracted = objectIds.toNativeArray();
    Arrays.sort(extracted);
    _log.info("IdFactory: Extracted total " + extracted.length + " used id's.");
    return extracted;
  }

  public boolean isInitialized() {
    return this.initialized;
  }

  public abstract int getNextId();

  public void releaseId(int id) {
    ++this.releasedCount;
  }

  public long getReleasedCount() {
    return this.releasedCount;
  }

  public abstract int size();
}
