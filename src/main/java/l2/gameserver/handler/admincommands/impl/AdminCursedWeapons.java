//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.CursedWeapon;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.utils.ItemFunctions;

public class AdminCursedWeapons implements IAdminCommandHandler {
  public AdminCursedWeapons() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminCursedWeapons.Commands command = (AdminCursedWeapons.Commands)comm;
    if (!activeChar.getPlayerAccess().Menu) {
      return false;
    } else {
      CursedWeaponsManager cwm = CursedWeaponsManager.getInstance();
      CursedWeapon cw = null;
      CursedWeapon[] var8;
      int var9;
      int var10;
      CursedWeapon c;
      switch(command) {
        case admin_cw_remove:
        case admin_cw_goto:
        case admin_cw_add:
        case admin_cw_drop:
          if (wordList.length < 2) {
            activeChar.sendMessage("You did not specify id");
            return false;
          } else {
            var8 = CursedWeaponsManager.getInstance().getCursedWeapons();
            var9 = var8.length;

            for(var10 = 0; var10 < var9; ++var10) {
              c = var8[var10];
              if (c.getName().toLowerCase().contains(wordList[1].toLowerCase())) {
                cw = c;
              }
            }

            if (cw == null) {
              activeChar.sendMessage("Unknown id");
              return false;
            }
          }
        default:
          GameObject target;
          Player player;
          switch(command) {
            case admin_cw_remove:
              if (cw == null) {
                return false;
              }

              CursedWeaponsManager.getInstance().endOfLife(cw);
              break;
            case admin_cw_goto:
              if (cw == null) {
                return false;
              }

              activeChar.teleToLocation(cw.getLoc());
              break;
            case admin_cw_add:
              if (cw == null) {
                return false;
              }

              if (cw.isActive()) {
                activeChar.sendMessage("This cursed weapon is already active.");
              } else {
                target = activeChar.getTarget();
                if (target != null && target.isPlayer() && !((Player)target).isOlyParticipant()) {
                  player = (Player)target;
                  ItemInstance item = ItemFunctions.createItem(cw.getItemId());
                  cwm.activate(player, player.getInventory().addItem(item));
                  cwm.showUsageTime(player, cw);
                }
              }
              break;
            case admin_cw_drop:
              if (cw == null) {
                return false;
              }

              if (cw.isActive()) {
                activeChar.sendMessage("This cursed weapon is already active.");
              } else {
                target = activeChar.getTarget();
                if (target != null && target.isPlayer() && !((Player)target).isOlyParticipant()) {
                  player = (Player)target;
                  cw.create((NpcInstance)null, player);
                }
              }
              break;
            case admin_cw_info:
              activeChar.sendMessage("======= Cursed Weapons: =======");
              var8 = cwm.getCursedWeapons();
              var9 = var8.length;

              for(var10 = 0; var10 < var9; ++var10) {
                c = var8[var10];
                activeChar.sendMessage("> " + c.getName() + " (" + c.getItemId() + ")");
                if (c.isActivated()) {
                  Player pl = c.getPlayer();
                  activeChar.sendMessage("  Player holding: " + pl.getName());
                  activeChar.sendMessage("  Player karma: " + c.getPlayerKarma());
                  activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000L + " min.");
                  activeChar.sendMessage("  Kills : " + c.getNbKills());
                } else if (c.isDropped()) {
                  activeChar.sendMessage("  Lying on the ground.");
                  activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000L + " min.");
                  activeChar.sendMessage("  Kills : " + c.getNbKills());
                } else {
                  activeChar.sendMessage("  Don't exist in the world.");
                }
              }

              return true;
            case admin_cw_reload:
              activeChar.sendMessage("Cursed weapons can't be reloaded.");
              break;
            case admin_cw_check:
              CursedWeaponsManager.getInstance().checkConditions();
          }

          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminCursedWeapons.Commands.values();
  }

  private static enum Commands {
    admin_cw_info,
    admin_cw_remove,
    admin_cw_goto,
    admin_cw_reload,
    admin_cw_add,
    admin_cw_drop,
    admin_cw_check;

    private Commands() {
    }
  }
}
