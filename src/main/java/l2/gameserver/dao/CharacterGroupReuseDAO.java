//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.dao;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.utils.SqlBatch;
import lombok.extern.slf4j.Slf4j;
import org.napile.primitive.maps.IntObjectMap.Entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class CharacterGroupReuseDAO {
  private static CharacterGroupReuseDAO _instance = new CharacterGroupReuseDAO();
  public static final String DELETE_SQL_QUERY = "DELETE FROM character_group_reuse WHERE object_id=?";
  public static final String SELECT_SQL_QUERY = "SELECT * FROM character_group_reuse WHERE object_id=?";
  public static final String INSERT_SQL_QUERY = "REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES";

  public CharacterGroupReuseDAO() {
  }

  public static CharacterGroupReuseDAO getInstance() {
    return _instance;
  }

  public void select(Player player) {
    long curTime = System.currentTimeMillis();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT * FROM character_group_reuse WHERE object_id=?");
      statement.setInt(1, player.getObjectId());
      rset = statement.executeQuery();

      while (rset.next()) {
        int group = rset.getInt("reuse_group");
        int item_id = rset.getInt("item_id");
        long endTime = rset.getLong("end_time");
        long reuse = rset.getLong("reuse");
        if (endTime - curTime > 500L) {
          TimeStamp stamp = new TimeStamp(item_id, endTime, reuse);
          player.addSharedGroupReuse(group, stamp);
        }
      }

      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM character_group_reuse WHERE object_id=?");
      statement.setInt(1, player.getObjectId());
      statement.execute();
    } catch (Exception var17) {
      log.error("CharacterGroupReuseDAO.select(L2Player):", var17);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public void insert(Player player) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM character_group_reuse WHERE object_id=?");
      statement.setInt(1, player.getObjectId());
      statement.execute();
      Collection<Entry<TimeStamp>> reuses = player.getSharedGroupReuses();
      if (!reuses.isEmpty()) {
        SqlBatch b = new SqlBatch("REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES");
        synchronized (reuses) {
          Iterator var7 = reuses.iterator();

          while (true) {
            if (!var7.hasNext()) {
              break;
            }

            Entry<TimeStamp> entry = (Entry) var7.next();
            int group = entry.getKey();
            TimeStamp timeStamp = (TimeStamp) entry.getValue();
            if (timeStamp.hasNotPassed()) {
              StringBuilder sb = new StringBuilder("(");
              sb.append(player.getObjectId()).append(",");
              sb.append(group).append(",");
              sb.append(timeStamp.getId()).append(",");
              sb.append(timeStamp.getEndTime()).append(",");
              sb.append(timeStamp.getReuseBasic()).append(")");
              b.write(sb.toString());
            }
          }
        }

        if (!b.isEmpty()) {
          statement.executeUpdate(b.close());
        }

      }
    } catch (Exception var18) {
      log.error("CharacterGroupReuseDAO.insert(L2Player):", var18);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }
}
