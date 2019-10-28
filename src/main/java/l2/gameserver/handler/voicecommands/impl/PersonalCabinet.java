//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.gs2as.IGPwdCng;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PersonalCabinet extends Functions implements IVoicedCommandHandler {
  private static final Pattern PASSWORD_BYPASS_PATTERN = Pattern.compile("^([\\w\\d_-]{4,18})\\s+([\\w\\d_-]{4,16})$");
  private static final long PASSWORD_CHANGE_INTERVAL = 3600000L;
  private static String[] _voicedCommands = new String[]{"my", "cfg", "pc", "personal_cabinet", "repair", "password"};

  public PersonalCabinet() {
  }

  public boolean useVoicedCommand(String command, Player activeChar, String target) {
    if (activeChar == null) {
      return false;
    } else {
      command = command.intern();
      if (!command.equalsIgnoreCase(_voicedCommands[0]) && !command.equalsIgnoreCase(_voicedCommands[1]) && !command.equalsIgnoreCase(_voicedCommands[2]) && !command.equalsIgnoreCase(_voicedCommands[3])) {
        if (command.equalsIgnoreCase(_voicedCommands[5])) {
          String lastChanged = activeChar.getVar("LastPwdChng");
          if (lastChanged != null && !lastChanged.isEmpty()) {
            long lastChange = Long.parseLong(lastChanged) * 1000L;
            if (lastChange + 3600000L > System.currentTimeMillis()) {
              activeChar.sendMessage(new CustomMessage("usercommandhandlers.CantChangePasswordSoFast", activeChar));
              return false;
            }
          }

          if (!target.isEmpty()) {
            Matcher m = PASSWORD_BYPASS_PATTERN.matcher(target);
            if (m.find() && m.groupCount() == 2) {
              String oldpassword = m.group(1);
              String newpassword = m.group(2);
              AuthServerCommunication.getInstance().sendPacket(new IGPwdCng(activeChar, oldpassword, newpassword));
              return true;
            }

            activeChar.sendMessage(new CustomMessage("usercommandhandlers.PasswordChangeNotMet", activeChar));
          } else {
            activeChar.sendMessage(new CustomMessage("usercommandhandlers.PasswordCantBeEmpty", activeChar));
          }

          return true;
        } else if (!command.equalsIgnoreCase(_voicedCommands[4])) {
          return false;
        } else {
          PreparedStatement fpstmt = null;
          if (target.isEmpty()) {
            NpcHtmlMessage msg = new NpcHtmlMessage(activeChar, null);
            msg.setFile("mods/pc/repair.html");
            StringBuilder cl = new StringBuilder();
            Connection con = null;
            ResultSet rset = null;

            try {
              con = DatabaseFactory.getInstance().getConnection();
              fpstmt = con.prepareStatement("SELECT `obj_Id`, `char_name` FROM `characters` WHERE `account_name` = ? AND `online` = 0");
              fpstmt.setString(1, activeChar.getAccountName());
              rset = fpstmt.executeQuery();

              while(rset.next()) {
                String charName = rset.getString("char_name");
                int charId = rset.getInt("obj_Id");
                cl.append("<a action=\"bypass -h user_repair ").append(charId).append("\">").append(charName).append("</a><br1>");
              }
            } catch (Exception e) {
              log.error("useVoicedCommand: eMessage={}, eClause={}", e.getMessage(), e.getClass());
            } finally {
              DbUtils.closeQuietly(con, fpstmt, rset);
            }

            msg.replace("%repair%", cl.toString());
            activeChar.sendPacket(msg);
            return true;
          } else {
            Connection con = null;
            fpstmt = null;
            ResultSet rset = null;

            try {
              int charId = Integer.parseInt(target);
              con = DatabaseFactory.getInstance().getConnection();
              fpstmt = con.prepareStatement("SELECT * FROM `characters` WHERE `account_name` = ? AND `obj_Id` = ? AND `online` = 0");
              fpstmt.setString(1, activeChar.getAccountName());
              fpstmt.setInt(2, charId);
              rset = fpstmt.executeQuery();
              boolean var8;
              if (!rset.next()) {
                activeChar.sendMessage(new CustomMessage("usercommandhandlers.CharNotFound", activeChar));
                return true;
              }

              if (World.getPlayer(charId) != null) {
                activeChar.sendMessage(new CustomMessage("usercommandhandlers.CharacterOnline", activeChar));
                return true;
              }

              DbUtils.close(fpstmt, rset);
              fpstmt = con.prepareStatement("UPDATE `characters` SET `x` = 17867, `y` = 170259, `z` = -3503 WHERE `obj_Id` = ?");
              fpstmt.setInt(1, charId);
              fpstmt.executeUpdate();
              DbUtils.close(fpstmt);
              fpstmt = con.prepareStatement("DELETE FROM `character_shortcuts` WHERE `char_obj_id` = ?");
              fpstmt.setInt(1, charId);
              fpstmt.executeUpdate();
              DbUtils.close(fpstmt);
              fpstmt = con.prepareStatement("UPDATE `items` SET `loc` = \"INVENTORY\" WHERE `loc` = \"PAPERDOLL\" AND `owner_id` = ? AND `item_id` NOT IN (13530, 13531, 13532, 13533, 13534, 13535, 13536, 13537, 13538, 10281, 10283, 10282, 10286, 10284, 10285, 10287, 10289, 10290, 10288, 10294, 10292, 10291, 10293, 10280, 10612)");
              fpstmt.setInt(1, charId);
              fpstmt.executeUpdate();
              DbUtils.close(fpstmt);
              fpstmt = con.prepareStatement("UPDATE `items` SET `loc` = \"WAREHOUSE\" WHERE `loc` = \"INVENTORY\" AND `owner_id` = ? AND `item_id` NOT IN (13530, 13531, 13532, 13533, 13534, 13535, 13536, 13537, 13538, 10281, 10283, 10282, 10286, 10284, 10285, 10287, 10289, 10290, 10288, 10294, 10292, 10291, 10293, 10280, 10612)");
              fpstmt.setInt(1, charId);
              fpstmt.executeUpdate();
              DbUtils.close(fpstmt);
              activeChar.sendMessage(new CustomMessage("usercommandhandlers.CharacterRepaired", activeChar));
            } catch (Exception e) {
              log.error("useVoicedCommand: eMessage={}, eClause={}", e.getMessage(), e.getClass());
            } finally {
              DbUtils.closeQuietly(con, fpstmt, rset);
            }

            return true;
          }
        }
      } else {
        if (!target.isEmpty()) {
          if (target.startsWith("news")) {
            this.showPage(activeChar, "news.html");
          }

          if (target.startsWith("rules")) {
            this.showPage(activeChar, "rules.html");
          } else if (target.startsWith("shop")) {
            this.showPage(activeChar, "shop.html");
          } else if (target.startsWith("pass")) {
            this.showPage(activeChar, "passcng.html");
          } else if (target.startsWith("faq")) {
            this.showPage(activeChar, "faq.html");
          } else if (target.startsWith("event")) {
            this.showPage(activeChar, "event.html");
          }
        } else {
          this.showPage(activeChar, "news.html");
        }

        return true;
      }
    }
  }

  private void showPage(Player activeChar, String page) {
    this.show("mods/pc/".concat(page), activeChar);
  }

  public String[] getVoicedCommandList() {
    return _voicedCommands;
  }
}
