//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminTarget implements IAdminCommandHandler {
  public AdminTarget() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminTarget.Commands command = (AdminTarget.Commands)comm;
    if (!activeChar.getPlayerAccess().CanViewChar) {
      return false;
    } else {
      try {
        String targetName = wordList[1];
        GameObject obj = World.getPlayer(targetName);
        if (obj != null && obj.isPlayer()) {
          obj.onAction(activeChar, false);
        } else {
          activeChar.sendMessage("Player " + targetName + " not found");
        }
      } catch (IndexOutOfBoundsException var8) {
        activeChar.sendMessage("Please specify correct name.");
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminTarget.Commands.values();
  }

  private static enum Commands {
    admin_target;

    private Commands() {
    }
  }
}
