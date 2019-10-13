//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class AdminEvents implements IAdminCommandHandler {
  public AdminEvents() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminEvents.Commands command = (AdminEvents.Commands)comm;
    if (!activeChar.getPlayerAccess().IsEventGm) {
      return false;
    } else {
      switch(command) {
        case admin_events:
          if (wordList.length == 1) {
            activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/events/events.htm"));
          } else {
            activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/events/" + wordList[1].trim()));
          }
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminEvents.Commands.values();
  }

  private static enum Commands {
    admin_events;

    private Commands() {
    }
  }
}
