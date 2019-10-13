//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;

public class AdminGm implements IAdminCommandHandler {
  public AdminGm() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminGm.Commands command = (AdminGm.Commands)comm;
    if (Boolean.TRUE) {
      return false;
    } else if (!activeChar.getPlayerAccess().CanEditChar) {
      return false;
    } else {
      switch(command) {
        case admin_gm:
          this.handleGm(activeChar);
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminGm.Commands.values();
  }

  private void handleGm(Player activeChar) {
    if (activeChar.isGM()) {
      activeChar.getPlayerAccess().IsGM = false;
      activeChar.sendMessage("You no longer have GM status.");
    } else {
      activeChar.getPlayerAccess().IsGM = true;
      activeChar.sendMessage("You have GM status now.");
    }

  }

  private static enum Commands {
    admin_gm;

    private Commands() {
    }
  }
}
