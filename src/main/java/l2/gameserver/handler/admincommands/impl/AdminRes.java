//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.*;
import l2.gameserver.model.instances.NpcInstance;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminRes implements IAdminCommandHandler {
  public AdminRes() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminRes.Commands command = (AdminRes.Commands) comm;
    if (!activeChar.getPlayerAccess().Res) {
      return false;
    } else {
      if (fullString.startsWith("admin_res ")) {
        this.handleRes(activeChar, wordList[1]);
      }

      if (fullString.equals("admin_res")) {
        this.handleRes(activeChar);
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminRes.Commands.values();
  }

  private void handleRes(Player activeChar) {
    this.handleRes(activeChar, (String) null);
  }

  private void handleRes(Player activeChar, String player) {
    GameObject obj = activeChar.getTarget();
    if (player != null) {
      Player plyr = World.getPlayer(player);
      if (plyr == null) {
        try {
          int radius = Math.max(Integer.parseInt(player), 100);

          for (Creature character : activeChar.getAroundCharacters(radius, radius)) {
            this.handleRes(character);
          }

          activeChar.sendMessage("Resurrected within " + radius + " unit radius.");
          return;
        } catch (NumberFormatException e) {
          log.error("handleRes: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
          activeChar.sendMessage("Enter valid player name or radius");
          return;
        }
      }

      obj = plyr;
    }

    if (obj == null) {
      obj = activeChar;
    }

    if (obj instanceof Creature) {
      this.handleRes((Creature) obj);
    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    }

  }

  private void handleRes(Creature target) {
    if (target.isDead()) {
      if (target.isPlayable()) {
        if (target.isPlayer()) {
          ((Player) target).doRevive(100.0D);
        } else {
          ((Playable) target).doRevive();
        }
      } else if (target.isNpc()) {
        ((NpcInstance) target).stopDecay();
      }

      target.setCurrentHpMp((double) target.getMaxHp(), (double) target.getMaxMp(), true);
      target.setCurrentCp((double) target.getMaxCp());
    }
  }

  private static enum Commands {
    admin_res;

    private Commands() {
    }
  }
}
