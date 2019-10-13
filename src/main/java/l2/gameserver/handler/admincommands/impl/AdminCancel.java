//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.util.Iterator;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminCancel implements IAdminCommandHandler {
  public AdminCancel() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminCancel.Commands command = (AdminCancel.Commands)comm;
    if (!activeChar.getPlayerAccess().CanEditChar) {
      return false;
    } else {
      switch(command) {
        case admin_cancel:
          this.handleCancel(activeChar, wordList.length > 1 ? wordList[1] : null);
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminCancel.Commands.values();
  }

  private void handleCancel(Player activeChar, String targetName) {
    GameObject obj = activeChar.getTarget();
    if (targetName != null) {
      Player plyr = World.getPlayer(targetName);
      if (plyr == null) {
        try {
          int radius = Math.max(Integer.parseInt(targetName), 100);
          Iterator var6 = activeChar.getAroundCharacters(radius, 200).iterator();

          while(var6.hasNext()) {
            Creature character = (Creature)var6.next();
            character.getEffectList().stopAllEffects();
          }

          activeChar.sendMessage("Apply Cancel within " + radius + " unit radius.");
          return;
        } catch (NumberFormatException var8) {
          activeChar.sendMessage("Enter valid player name or radius");
          return;
        }
      }

      obj = plyr;
    }

    if (obj == null) {
      obj = activeChar;
    }

    if (((GameObject)obj).isCreature()) {
      ((Creature)obj).getEffectList().stopAllEffects();
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    }

  }

  private static enum Commands {
    admin_cancel;

    private Commands() {
    }
  }
}
