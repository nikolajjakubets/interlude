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

public class AdminHeal implements IAdminCommandHandler {
  public AdminHeal() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminHeal.Commands command = (AdminHeal.Commands)comm;
    if (!activeChar.getPlayerAccess().Heal) {
      return false;
    } else {
      switch(command) {
        case admin_heal:
          if (wordList.length == 1) {
            this.handleRes(activeChar);
          } else {
            this.handleRes(activeChar, wordList[1]);
          }
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminHeal.Commands.values();
  }

  private void handleRes(Player activeChar) {
    this.handleRes(activeChar, (String)null);
  }

  private void handleRes(Player activeChar, String player) {
    GameObject obj = activeChar.getTarget();
    if (player != null) {
      Player plyr = World.getPlayer(player);
      if (plyr == null) {
        int radius = Math.max(Integer.parseInt(player), 100);
        Iterator var6 = activeChar.getAroundCharacters(radius, 200).iterator();

        while(var6.hasNext()) {
          Creature character = (Creature)var6.next();
          character.setCurrentHpMp((double)character.getMaxHp(), (double)character.getMaxMp());
          if (character.isPlayer()) {
            character.setCurrentCp((double)character.getMaxCp());
          }
        }

        activeChar.sendMessage("Healed within " + radius + " unit radius.");
        return;
      }

      obj = plyr;
    }

    if (obj == null) {
      obj = activeChar;
    }

    if (obj instanceof Creature) {
      Creature target = (Creature)obj;
      target.setCurrentHpMp((double)target.getMaxHp(), (double)target.getMaxMp());
      if (target.isPlayer()) {
        target.setCurrentCp((double)target.getMaxCp());
      }
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    }

  }

  private static enum Commands {
    admin_heal;

    private Commands() {
    }
  }
}
