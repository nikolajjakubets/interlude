//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;

public class AdminMove implements IAdminCommandHandler {
  public AdminMove() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminMove.Commands command = (AdminMove.Commands)comm;
    if (!activeChar.getPlayerAccess().CanReload) {
      return false;
    } else {
      switch(command) {
        case admin_move_debug:
          if (wordList.length > 1) {
            int dbgMode = Integer.parseInt(wordList[1]);
            if (dbgMode > 0) {
              activeChar.setVar("debugMove", Integer.parseInt(wordList[1]), -1L);
              activeChar.sendMessage("Move debug mode " + dbgMode);
            } else {
              activeChar.unsetVar("debugMove");
              activeChar.sendMessage("Move debug disabled");
            }
          } else {
            activeChar.setVar("debugMove", activeChar.getVarInt("debugMove", 0) > 0 ? 0 : 1, -1L);
            activeChar.sendMessage("Move debug mode " + activeChar.getVarInt("debugMove", 0));
          }
        default:
          return false;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminMove.Commands.values();
  }

  private static enum Commands {
    admin_move_debug;

    private Commands() {
    }
  }
}
