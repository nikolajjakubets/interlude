//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.commons.util.Rnd;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminTest implements IAdminCommandHandler {
  public AdminTest() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminTest.Commands command = (AdminTest.Commands)comm;
    Player targetPlayer;
    switch(command) {
      case admin_collapse_this:
        if (activeChar.getReflection() != null) {
          activeChar.getReflection().startCollapseTimer(1000L);
        } else {
          activeChar.sendMessage("No reflection");
        }
        break;
      case admin_collapse_this2:
        if (activeChar.getReflection() != null) {
          activeChar.getReflection().collapse();
        } else {
          activeChar.sendMessage("No reflection");
        }
        break;
      case admin_alt_set_target_hwid:
        targetPlayer = activeChar.getTarget() != null ? activeChar.getTarget().getPlayer() : null;
        if (targetPlayer != null) {
          targetPlayer.getNetConnection().setHwid(wordList[1]);
        }
        break;
      case admin_alt_move_000:
        targetPlayer = activeChar.getTarget() != null ? activeChar.getTarget().getPlayer() : null;
        if (targetPlayer == null) {
          targetPlayer = activeChar;
        }

        targetPlayer.moveToLocation(0, 0, 0, 0, true);
        break;
      case admin_alt_move_rnd:
        targetPlayer = activeChar.getTarget() != null ? activeChar.getTarget().getPlayer() : null;
        if (targetPlayer == null) {
          targetPlayer = activeChar;
        }

        targetPlayer.moveToLocation(Rnd.get(World.MAP_MIN_X, World.MAP_MAX_X), Rnd.get(World.MAP_MIN_Y, World.MAP_MAX_Y), Rnd.get(World.MAP_MIN_Z, World.MAP_MAX_Z), 0, true);
    }

    return false;
  }

  public Enum[] getAdminCommandEnum() {
    return AdminTest.Commands.values();
  }

  private static enum Commands {
    admin_collapse_this,
    admin_collapse_this2,
    admin_alt_set_target_hwid,
    admin_alt_move_000,
    admin_alt_move_rnd;

    private Commands() {
    }
  }
}
