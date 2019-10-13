//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.util.StringTokenizer;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.AdminFunctions;
import l2.gameserver.utils.Location;

public class AdminMenu implements IAdminCommandHandler {
  public AdminMenu() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminMenu.Commands command = (AdminMenu.Commands)comm;
    if (!activeChar.getPlayerAccess().Menu) {
      return false;
    } else {
      String player;
      if (fullString.startsWith("admin_teleport_character_to_menu")) {
        String[] data = fullString.split(" ");
        if (data.length == 5) {
          player = data[1];
          Player player = World.getPlayer(player);
          if (player != null) {
            this.teleportCharacter(player, new Location(Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])), activeChar);
          }
        }
      } else {
        String targetName;
        Player player;
        if (fullString.startsWith("admin_recall_char_menu")) {
          try {
            targetName = fullString.substring(23);
            player = World.getPlayer(targetName);
            this.teleportCharacter(player, activeChar.getLoc(), activeChar);
          } catch (StringIndexOutOfBoundsException var11) {
          }
        } else if (fullString.startsWith("admin_goto_char_menu")) {
          try {
            targetName = fullString.substring(21);
            player = World.getPlayer(targetName);
            this.teleportToCharacter(activeChar, player);
          } catch (StringIndexOutOfBoundsException var10) {
          }
        } else if (fullString.equals("admin_kill_menu")) {
          GameObject obj = activeChar.getTarget();
          StringTokenizer st = new StringTokenizer(fullString);
          if (st.countTokens() > 1) {
            st.nextToken();
            String player = st.nextToken();
            Player plyr = World.getPlayer(player);
            if (plyr == null) {
              activeChar.sendMessage("Player " + player + " not found in game.");
            }

            obj = plyr;
          }

          if (obj != null && ((GameObject)obj).isCreature()) {
            Creature target = (Creature)obj;
            target.reduceCurrentHp((double)(target.getMaxHp() + 1), activeChar, (Skill)null, true, true, true, false, false, false, true);
          } else {
            activeChar.sendPacket(Msg.INVALID_TARGET);
          }
        } else if (fullString.startsWith("admin_kick_menu")) {
          StringTokenizer st = new StringTokenizer(fullString);
          if (st.countTokens() > 1) {
            st.nextToken();
            player = st.nextToken();
            if (AdminFunctions.kick(player, "kick")) {
              activeChar.sendMessage("Player kicked.");
            }
          }
        }
      }

      activeChar.sendPacket((new NpcHtmlMessage(5)).setFile("admin/charmanage.htm"));
      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminMenu.Commands.values();
  }

  private void teleportCharacter(Player player, Location loc, Player activeChar) {
    if (player != null) {
      player.sendMessage("Admin is teleporting you.");
      player.teleToLocation(loc);
    }

  }

  private void teleportToCharacter(Player activeChar, GameObject target) {
    if (target != null && target.isPlayer()) {
      Player player = (Player)target;
      if (player.getObjectId() == activeChar.getObjectId()) {
        activeChar.sendMessage("You cannot self teleport.");
      } else {
        activeChar.teleToLocation(player.getLoc());
        activeChar.sendMessage("You have teleported to character " + player.getName() + ".");
      }

    } else {
      activeChar.sendPacket(Msg.INVALID_TARGET);
    }
  }

  private static enum Commands {
    admin_char_manage,
    admin_teleport_character_to_menu,
    admin_recall_char_menu,
    admin_goto_char_menu,
    admin_kick_menu,
    admin_kill_menu,
    admin_ban_menu,
    admin_unban_menu;

    private Commands() {
    }
  }
}
