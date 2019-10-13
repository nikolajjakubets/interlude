//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.util.Iterator;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;

public class AdminInstance implements IAdminCommandHandler {
  public AdminInstance() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminInstance.Commands command = (AdminInstance.Commands)comm;
    if (!activeChar.getPlayerAccess().CanTeleport) {
      return false;
    } else {
      Player p;
      switch(command) {
        case admin_instance:
          this.listOfInstances(activeChar);
          break;
        case admin_instance_id:
          if (wordList.length > 1) {
            this.listOfCharsForInstance(activeChar, wordList[1]);
          }
          break;
        case admin_collapse:
          if (!activeChar.getReflection().isDefault()) {
            activeChar.getReflection().collapse();
          } else {
            activeChar.sendMessage("Cannot collapse default reflection!");
          }
          break;
        case admin_reset_reuse:
          if (wordList.length > 1 && activeChar.getTarget() != null && activeChar.getTarget().isPlayer()) {
            p = activeChar.getTarget().getPlayer();
            p.removeInstanceReuse(Integer.parseInt(wordList[1]));
            Functions.sendDebugMessage(activeChar, "Instance reuse has been removed");
          }
          break;
        case admin_reset_reuse_all:
          if (activeChar.getTarget() != null && activeChar.getTarget().isPlayer()) {
            p = activeChar.getTarget().getPlayer();
            p.removeAllInstanceReuses();
            Functions.sendDebugMessage(activeChar, "All instance reuses has been removed");
          }
          break;
        case admin_set_reuse:
          if (activeChar.getReflection() != null) {
            activeChar.getReflection().setReenterTime(System.currentTimeMillis());
          }
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminInstance.Commands.values();
  }

  private void listOfInstances(Player activeChar) {
    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body>");
    replyMSG.append("<table width=260><tr>");
    replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("<td width=180><center>List of Instances</center></td>");
    replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
    replyMSG.append("</tr></table><br><br>");
    Reflection[] var4 = ReflectionManager.getInstance().getAll();
    int var5 = var4.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      Reflection reflection = var4[var6];
      if (reflection != null && !reflection.isDefault() && !reflection.isCollapseStarted()) {
        int countPlayers = 0;
        if (reflection.getPlayers() != null) {
          countPlayers = reflection.getPlayers().size();
        }

        replyMSG.append("<a action=\"bypass -h admin_instance_id ").append(reflection.getId()).append(" \">").append(reflection.getName()).append("(").append(countPlayers).append(" players). Id: ").append(reflection.getId()).append("</a><br>");
      }
    }

    replyMSG.append("<button value=\"Refresh\" action=\"bypass -h admin_instance\" width=50 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\">");
    replyMSG.append("</body></html>");
    adminReply.setHtml(replyMSG.toString());
    activeChar.sendPacket(adminReply);
  }

  private void listOfCharsForInstance(Player activeChar, String sid) {
    Reflection reflection = ReflectionManager.getInstance().get(Integer.parseInt(sid));
    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body><br>");
    if (reflection != null) {
      replyMSG.append("<table width=260><tr>");
      replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("<td width=180><center>List of players in ").append(reflection.getName()).append("</center></td>");
      replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_instance\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      replyMSG.append("</tr></table><br><br>");
      Iterator var6 = reflection.getPlayers().iterator();

      while(var6.hasNext()) {
        Player player = (Player)var6.next();
        replyMSG.append("<a action=\"bypass -h admin_teleportto ").append(player.getName()).append(" \">").append(player.getName()).append("</a><br>");
      }
    } else {
      replyMSG.append("Instance not active.<br>");
      replyMSG.append("<a action=\"bypass -h admin_instance\">Back to list.</a><br>");
    }

    replyMSG.append("</body></html>");
    adminReply.setHtml(replyMSG.toString());
    activeChar.sendPacket(adminReply);
  }

  private static enum Commands {
    admin_instance,
    admin_instance_id,
    admin_collapse,
    admin_reset_reuse,
    admin_reset_reuse_all,
    admin_set_reuse,
    admin_addtiatkill;

    private Commands() {
    }
  }
}
