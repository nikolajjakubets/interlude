//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager.actionrunner.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Strings;
import org.apache.log4j.Logger;

public class DeleteExpiredVarsTask extends AutomaticTask {
  private static final Logger _log = Logger.getLogger(DeleteExpiredVarsTask.class);

  public DeleteExpiredVarsTask() {
  }

  public void doTask() throws Exception {
    long t = System.currentTimeMillis();
    Connection con = null;
    PreparedStatement query = null;
    Map<Integer, String> varMap = new HashMap<>();
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      query = con.prepareStatement("SELECT obj_id, name FROM character_variables WHERE expire_time > 0 AND expire_time < ?");
      query.setLong(1, System.currentTimeMillis());
      rs = query.executeQuery();

      while(rs.next()) {
        String name = rs.getString("name");
        String obj_id = Strings.stripSlashes(rs.getString("obj_id"));
        varMap.put(Integer.parseInt(obj_id), name);
      }
    } catch (Exception var12) {
      _log.error("", var12);
    } finally {
      DbUtils.closeQuietly(con, query, rs);
    }

    if (!varMap.isEmpty()) {
      Iterator var14 = varMap.entrySet().iterator();

      while(true) {
        while(var14.hasNext()) {
          Entry<Integer, String> entry = (Entry)var14.next();
          Player player = GameObjectsStorage.getPlayer((Integer)entry.getKey());
          if (player != null && player.isOnline()) {
            player.unsetVar((String)entry.getValue());
          } else {
            mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", new Object[]{entry.getKey(), entry.getValue()});
          }
        }

        return;
      }
    }
  }

  public long reCalcTime(boolean start) {
    return System.currentTimeMillis() + 600000L;
  }
}
