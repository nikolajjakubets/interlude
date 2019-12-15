//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager.tasks;

import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.skills.AbnormalEffect;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public class RestoreOfflineTraders extends RunnableImpl {

  public RestoreOfflineTraders() {
  }

  public void runImpl() throws Exception {
    int count = 0;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      int objectId;
      if (Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0L) {
        objectId = (int) (System.currentTimeMillis() / 1000L - Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK);
        statement = con.prepareStatement("DELETE FROM character_variables WHERE name = 'offline' AND value < ?");
        statement.setLong(1, (long) objectId);
        statement.executeUpdate();
        DbUtils.close(statement);
      }

      statement = con.prepareStatement("DELETE FROM character_variables WHERE name = 'offline' AND obj_id IN (SELECT obj_id FROM characters WHERE accessLevel < 0)");
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("SELECT obj_id, value FROM character_variables WHERE name = 'offline'");
      rset = statement.executeQuery();

      label145:
      while (true) {
        while (true) {
          int expireTimeSecs;
          Player p;
          do {
            if (!rset.next()) {
              break label145;
            }

            objectId = rset.getInt("obj_id");
            expireTimeSecs = rset.getInt("value");
            p = Player.restore(objectId);
          } while (p == null);

          if (p.isDead()) {
            p.kick();
          } else {
            if (Config.SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE) {
              p.setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
            }

            if (Config.SERVICES_OFFLINE_TRADE_ABNORMAL != AbnormalEffect.NULL) {
              p.startAbnormalEffect(Config.SERVICES_OFFLINE_TRADE_ABNORMAL);
            }

            p.setOfflineMode(true);
            p.setIsOnline(true);
            p.spawnMe();
            if (p.getClan() != null && p.getClan().getAnyMember(p.getObjectId()) != null) {
              p.getClan().getAnyMember(p.getObjectId()).setPlayerInstance(p, false);
            }

            if (Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0L) {
              p.startKickTask((Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK + (long) expireTimeSecs - System.currentTimeMillis() / 1000L) * 1000L);
            }

            if (Config.SERVICES_TRADE_ONLY_FAR) {

              for (Player player : World.getAroundPlayers(p, Config.SERVICES_TRADE_RADIUS, 200)) {
                if (player.isInStoreMode()) {
                  if (player.isInOfflineMode()) {
                    player.setOfflineMode(false);
                    player.kick();
                    log.warn("Offline trader: " + player + " kicked.");
                  } else {
                    player.setPrivateStoreType(0);
                  }
                }
              }
            }

            ++count;
          }
        }
      }
    } catch (Exception var13) {
      log.error("Error while restoring offline traders!", var13);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    log.info("Restored " + count + " offline traders");
  }
}
