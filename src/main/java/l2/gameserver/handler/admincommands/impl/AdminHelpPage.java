//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class AdminHelpPage implements IAdminCommandHandler {
  public AdminHelpPage() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminHelpPage.Commands command = (AdminHelpPage.Commands)comm;
    if (!activeChar.getPlayerAccess().Menu) {
      return false;
    } else {
      switch(command) {
        case admin_showhtml:
          if (wordList.length != 2) {
            activeChar.sendMessage("Usage: //showhtml <file>");
            return false;
          } else {
            activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/" + wordList[1]));
          }
        default:
          return true;
      }
    }
  }

  public static void showHelpHtml(Player targetChar, String content) {
    NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
    adminReply.setHtml(content);
    targetChar.sendPacket(adminReply);
  }

  public Enum[] getAdminCommandEnum() {
    return AdminHelpPage.Commands.values();
  }

  private static enum Commands {
    admin_showhtml;

    private Commands() {
    }
  }
}
