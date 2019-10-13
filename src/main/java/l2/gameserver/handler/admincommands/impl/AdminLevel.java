//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.tables.PetDataTable;

public class AdminLevel implements IAdminCommandHandler {
  public AdminLevel() {
  }

  private void setLevel(Player activeChar, GameObject target, int level) {
    if (target != null && (target.isPlayer() || target.isPet())) {
      if (level >= 1 && level <= Experience.getMaxLevel()) {
        Long exp_add;
        if (target.isPlayer()) {
          exp_add = Experience.LEVEL[level] - ((Player)target).getExp();
          ((Player)target).addExpAndSp(exp_add, 0L);
        } else {
          if (target.isPet()) {
            exp_add = PetDataTable.getInstance().getInfo(((PetInstance)target).getNpcId(), level).getExp() - ((PetInstance)target).getExp();
            ((PetInstance)target).addExpAndSp(exp_add, 0L);
          }

        }
      } else {
        activeChar.sendMessage("You must specify level 1 - " + Experience.getMaxLevel());
      }
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    }
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminLevel.Commands command = (AdminLevel.Commands)comm;
    if (!activeChar.getPlayerAccess().CanEditChar) {
      return false;
    } else {
      GameObject target = activeChar.getTarget();
      if (target != null && (target.isPlayer() || target.isPet())) {
        int level;
        switch(command) {
          case admin_add_level:
          case admin_addLevel:
            if (wordList.length < 2) {
              activeChar.sendMessage("USAGE: //addLevel level");
              return false;
            }

            try {
              level = Integer.parseInt(wordList[1]);
            } catch (NumberFormatException var10) {
              activeChar.sendMessage("You must specify level");
              return false;
            }

            this.setLevel(activeChar, target, level + ((Creature)target).getLevel());
            break;
          case admin_set_level:
          case admin_setLevel:
            if (wordList.length < 2) {
              activeChar.sendMessage("USAGE: //setLevel level");
              return false;
            }

            try {
              level = Integer.parseInt(wordList[1]);
            } catch (NumberFormatException var9) {
              activeChar.sendMessage("You must specify level");
              return false;
            }

            this.setLevel(activeChar, target, level);
        }

        return true;
      } else {
        activeChar.sendPacket(Msg.INVALID_TARGET);
        return false;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminLevel.Commands.values();
  }

  private static enum Commands {
    admin_add_level,
    admin_addLevel,
    admin_set_level,
    admin_setLevel;

    private Commands() {
    }
  }
}
