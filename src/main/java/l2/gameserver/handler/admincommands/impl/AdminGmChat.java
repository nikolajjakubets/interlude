//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.tables.GmListTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminGmChat implements IAdminCommandHandler {
  public AdminGmChat() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminGmChat.Commands command = (AdminGmChat.Commands)comm;
    if (!activeChar.getPlayerAccess().CanAnnounce) {
      return false;
    } else {
      switch(command) {
        case admin_gmchat:
          try {
            String text = fullString.replaceFirst(AdminGmChat.Commands.admin_gmchat.name(), "");
            Say2 cs = new Say2(0, ChatType.ALLIANCE, activeChar.getName(), text);
            GmListTable.broadcastToGMs(cs);
          } catch (StringIndexOutOfBoundsException e) {
            log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), this.getClass().getSimpleName(), e.getCause());
          }
        case admin_snoop:
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminGmChat.Commands.values();
  }

  private static enum Commands {
    admin_gmchat,
    admin_snoop;

    private Commands() {
    }
  }
}
