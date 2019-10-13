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
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import org.apache.commons.lang3.math.NumberUtils;

public class AdminKill implements IAdminCommandHandler {
  public AdminKill() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminKill.Commands command = (AdminKill.Commands)comm;
    if (!activeChar.getPlayerAccess().CanEditNPC) {
      return false;
    } else {
      switch(command) {
        case admin_kill:
          if (wordList.length == 1) {
            this.handleKill(activeChar);
          } else {
            this.handleKill(activeChar, wordList[1]);
          }
          break;
        case admin_damage:
          this.handleDamage(activeChar, NumberUtils.toInt(wordList[1], 1));
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminKill.Commands.values();
  }

  private void handleKill(Player activeChar) {
    this.handleKill(activeChar, (String)null);
  }

  private void handleKill(Player activeChar, String player) {
    GameObject obj = activeChar.getTarget();
    if (player != null) {
      Player plyr = World.getPlayer(player);
      if (plyr == null) {
        int radius = Math.max(Integer.parseInt(player), 100);
        Iterator var6 = activeChar.getAroundCharacters(radius, 200).iterator();

        while(var6.hasNext()) {
          Creature character = (Creature)var6.next();
          if (!character.isDoor()) {
            character.doDie(activeChar);
          }
        }

        activeChar.sendMessage("Killed within " + radius + " unit radius.");
        return;
      }

      obj = plyr;
    }

    if (obj != null && ((GameObject)obj).isCreature()) {
      Creature target = (Creature)obj;
      target.doDie(activeChar);
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    }

  }

  private void handleDamage(Player activeChar, int damage) {
    GameObject obj = activeChar.getTarget();
    if (obj == null) {
      activeChar.sendPacket(Msg.SELECT_TARGET);
    } else if (!obj.isCreature()) {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    } else {
      Creature cha = (Creature)obj;
      cha.reduceCurrentHp((double)damage, activeChar, (Skill)null, true, true, false, false, false, false, true);
      activeChar.sendMessage("You gave " + damage + " damage to " + cha.getName() + ".");
    }
  }

  private static enum Commands {
    admin_kill,
    admin_damage;

    private Commands() {
    }
  }
}
