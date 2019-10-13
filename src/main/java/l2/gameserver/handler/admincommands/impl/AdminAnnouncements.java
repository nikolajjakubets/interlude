//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.Announcements;
import l2.gameserver.Announcements.Announce;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

public class AdminAnnouncements implements IAdminCommandHandler {
  public AdminAnnouncements() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminAnnouncements.Commands command = (AdminAnnouncements.Commands)comm;
    if (!activeChar.getPlayerAccess().CanAnnounce) {
      return false;
    } else {
      int time;
      int i;
      switch(command) {
        case admin_list_announcements:
          this.listAnnouncements(activeChar);
          break;
        case admin_announce_menu:
          Announcements.getInstance().announceToAll(fullString.substring(20));
          this.listAnnouncements(activeChar);
          break;
        case admin_announce_announcements:
          Iterator var13 = GameObjectsStorage.getAllPlayersForIterate().iterator();

          while(var13.hasNext()) {
            Player player = (Player)var13.next();
            Announcements.getInstance().showAnnouncements(player);
          }

          this.listAnnouncements(activeChar);
          break;
        case admin_add_announcement:
          if (wordList.length < 3) {
            return false;
          }

          try {
            time = Integer.parseInt(wordList[1]);
            StringBuilder builder = new StringBuilder();

            for(i = 2; i < wordList.length; ++i) {
              builder.append(" ").append(wordList[i]);
            }

            Announcements.getInstance().addAnnouncement(time, builder.toString(), true);
            this.listAnnouncements(activeChar);
          } catch (Exception var12) {
          }
          break;
        case admin_del_announcement:
          if (wordList.length != 2) {
            return false;
          }

          time = Integer.parseInt(wordList[1]);
          Announcements.getInstance().delAnnouncement(time);
          this.listAnnouncements(activeChar);
          break;
        case admin_announce:
          Announcements.getInstance().announceToAll(fullString.substring(15));
          break;
        case admin_a:
          Announcements.getInstance().announceToAll(fullString.substring(8));
          break;
        case admin_crit_announce:
        case admin_c:
          if (wordList.length < 2) {
            return false;
          }

          Announcements.getInstance().announceToAll(activeChar.getName() + ": " + fullString.replaceFirst("admin_crit_announce ", "").replaceFirst("admin_c ", ""), ChatType.CRITICAL_ANNOUNCE);
          break;
        case admin_toscreen:
        case admin_s:
          if (wordList.length < 2) {
            return false;
          }

          String text = activeChar.getName() + ": " + fullString.replaceFirst("admin_toscreen ", "").replaceFirst("admin_s ", "");
          i = 3000 + text.length() * 100;
          ExShowScreenMessage sm = new ExShowScreenMessage(text, i, ScreenMessageAlign.TOP_CENTER, text.length() < 64);
          Iterator var10 = GameObjectsStorage.getAllPlayersForIterate().iterator();

          while(var10.hasNext()) {
            Player player = (Player)var10.next();
            player.sendPacket(sm);
          }

          return true;
        case admin_reload_announcements:
          Announcements.getInstance().loadAnnouncements();
          this.listAnnouncements(activeChar);
          activeChar.sendMessage("Announcements reloaded.");
      }

      return true;
    }
  }

  public void listAnnouncements(Player activeChar) {
    List<Announce> announcements = Announcements.getInstance().getAnnouncements();
    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    StringBuilder replyMSG = new StringBuilder("<html><body>");
    replyMSG.append("<table width=260><tr>");
    replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td width=180><center>Announcement Menu</center></td>");
    replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("</tr></table>");
    replyMSG.append("<br><br>");
    replyMSG.append("<center>Add or announce a new announcement:</center>");
    replyMSG.append("<center><multiedit var=\"new_announcement\" width=240 height=30></center><br>");
    replyMSG.append("<center>Time(in seconds, 0 - only for start)<edit var=\"time\" width=40 height=20></center><br>");
    replyMSG.append("<center><table><tr><td>");
    replyMSG.append("<button value=\"Add\" action=\"bypass -h admin_add_announcement $time $new_announcement\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
    replyMSG.append("<button value=\"Announce\" action=\"bypass -h admin_announce_menu $new_announcement\" width=64 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
    replyMSG.append("<button value=\"Reload\" action=\"bypass -h admin_reload_announcements\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
    replyMSG.append("<button value=\"Broadcast\" action=\"bypass -h admin_announce_announcements\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">");
    replyMSG.append("</td></tr></table></center>");
    replyMSG.append("<br>");

    for(int i = 0; i < announcements.size(); ++i) {
      Announce announce = (Announce)announcements.get(i);
      replyMSG.append("<table width=260><tr><td width=180>" + announce.getAnnounce() + "</td><td width=40>" + announce.getTime() + "</td><<td width=40>");
      replyMSG.append("<button value=\"Delete\" action=\"bypass -h admin_del_announcement " + i + "\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table>");
    }

    replyMSG.append("</body></html>");
    adminReply.setHtml(replyMSG.toString());
    activeChar.sendPacket(adminReply);
  }

  public Enum[] getAdminCommandEnum() {
    return AdminAnnouncements.Commands.values();
  }

  private static enum Commands {
    admin_list_announcements,
    admin_announce_announcements,
    admin_add_announcement,
    admin_del_announcement,
    admin_announce,
    admin_a,
    admin_announce_menu,
    admin_crit_announce,
    admin_c,
    admin_toscreen,
    admin_s,
    admin_reload_announcements;

    private Commands() {
    }
  }
}
