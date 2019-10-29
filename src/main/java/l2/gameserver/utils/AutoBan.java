//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.utils;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class AutoBan {
  private static final Logger _log = LoggerFactory.getLogger(AutoBan.class);

  public AutoBan() {
  }

  public static boolean isBanned(int ObjectId) {
    boolean res = false;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT MAX(endban) AS endban FROM bans WHERE obj_Id=? AND endban IS NOT NULL");
      statement.setInt(1, ObjectId);
      rset = statement.executeQuery();
      if (rset.next()) {
        long endban = rset.getLong("endban") * 1000L;
        res = endban > System.currentTimeMillis();
      }
    } catch (Exception var9) {
      _log.warn("Could not restore ban data: " + var9);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return res;
  }

  public static void Banned(Player actor, int period, String msg, String GM) {
    int endban;
    if (period == -1) {
      endban = 2147483647;
    } else {
      if (period <= 0) {
        _log.warn("Negative ban period: " + period);
        return;
      }

      Calendar end = Calendar.getInstance();
      end.add(5, period);
      endban = (int)(end.getTimeInMillis() / 1000L);
    }

    String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
    String enddate = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date((long)endban * 1000L));
    if ((long)endban * 1000L <= Calendar.getInstance().getTimeInMillis()) {
      _log.warn("Negative ban period | From " + date + " to " + enddate);
    } else {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?,?)");
        statement.setString(1, actor.getAccountName());
        statement.setInt(2, actor.getObjectId());
        statement.setString(3, date);
        statement.setString(4, enddate);
        statement.setString(5, msg);
        statement.setString(6, GM);
        statement.setLong(7, (long)endban);
        statement.execute();
      } catch (Exception var13) {
        _log.warn("could not store bans data:" + var13);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }

  public static boolean Banned(String actor, int acc_level, int period, String msg, String GM) {
    int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);
    boolean res = obj_id > 0;
    if (!res) {
      return false;
    } else {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE obj_Id=?");
        statement.setInt(1, acc_level);
        statement.setInt(2, obj_id);
        statement.executeUpdate();
        DbUtils.close(statement);
        if (acc_level < 0) {
          int endban;
          if (period == -1) {
            endban = 2147483647;
          } else {
            if (period <= 0) {
              _log.warn("Negative ban period: " + period);
              boolean var20 = false;
              return var20;
            }

            Calendar end = Calendar.getInstance();
            end.add(5, period);
            endban = (int)(end.getTimeInMillis() / 1000L);
          }

          String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
          String enddate = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date((long)endban * 1000L));
          if ((long)endban * 1000L <= Calendar.getInstance().getTimeInMillis()) {
            _log.warn("Negative ban period | From " + date + " to " + enddate);
            boolean var12 = false;
            return var12;
          }

          statement = con.prepareStatement("INSERT INTO bans (obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?)");
          statement.setInt(1, obj_id);
          statement.setString(2, date);
          statement.setString(3, enddate);
          statement.setString(4, msg);
          statement.setString(5, GM);
          statement.setLong(6, (long)endban);
          statement.execute();
        } else {
          statement = con.prepareStatement("DELETE FROM bans WHERE obj_id=?");
          statement.setInt(1, obj_id);
          statement.execute();
        }
      } catch (Exception var16) {
        var16.printStackTrace();
        _log.warn("could not store bans data:" + var16);
        res = false;
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

      return res;
    }
  }

  public static void Karma(Player actor, int karma, String msg, String GM) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
      msg = "Add karma(" + karma + ") " + msg;
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, reason, GM) VALUES(?,?,?,?,?)");
      statement.setString(1, actor.getAccountName());
      statement.setInt(2, actor.getObjectId());
      statement.setString(3, date);
      statement.setString(4, msg);
      statement.setString(5, GM);
      statement.execute();
    } catch (Exception var10) {
      _log.warn("could not store bans data:" + var10);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public static void Banned(Player actor, int period, String msg) {
    Banned(actor, period, msg, "AutoBan");
  }

  public static boolean ChatBan(String actor, int period, String msg, String GM) {
    boolean res = true;
    long NoChannel = (long)(period * '\uea60');
    int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);
    if (obj_id == 0) {
      return false;
    } else {
      Player plyr = World.getPlayer(actor);
      Connection con = null;
      PreparedStatement statement = null;
      if (plyr != null) {
        plyr.sendMessage((new CustomMessage("l2p.Util.AutoBan.ChatBan", plyr, new Object[0])).addString(GM).addNumber((long)period));
        plyr.updateNoChannel(NoChannel);
      } else {
        try {
          con = DatabaseFactory.getInstance().getConnection();
          statement = con.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
          statement.setLong(1, NoChannel > 0L ? NoChannel / 1000L : NoChannel);
          statement.setInt(2, obj_id);
          statement.executeUpdate();
        } catch (Exception var15) {
          res = false;
          _log.warn("Could not activate nochannel:" + var15);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }
      }

      return res;
    }
  }

  public static boolean ChatUnBan(String actor, String GM) {
    boolean res = true;
    Player plyr = World.getPlayer(actor);
    int obj_id = CharacterDAO.getInstance().getObjectIdByName(actor);
    if (obj_id == 0) {
      return false;
    } else {
      Connection con = null;
      PreparedStatement statement = null;
      if (plyr != null) {
        plyr.sendMessage((new CustomMessage("l2p.Util.AutoBan.ChatUnBan", plyr, new Object[0])).addString(GM));
        plyr.updateNoChannel(0L);
      } else {
        try {
          con = DatabaseFactory.getInstance().getConnection();
          statement = con.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
          statement.setLong(1, 0L);
          statement.setInt(2, obj_id);
          statement.executeUpdate();
        } catch (Exception var11) {
          res = false;
          _log.warn("Could not activate nochannel:" + var11);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }
      }

      return res;
    }
  }
}
