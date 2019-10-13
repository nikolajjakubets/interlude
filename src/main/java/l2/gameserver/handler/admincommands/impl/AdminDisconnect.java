//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.CustomMessage;

public class AdminDisconnect implements IAdminCommandHandler {
  public AdminDisconnect() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminDisconnect.Commands command = (AdminDisconnect.Commands)comm;
    if (!activeChar.getPlayerAccess().CanKick) {
      return false;
    } else {
      switch(command) {
        case admin_disconnect:
        case admin_kick:
          final Player player;
          if (wordList.length == 1) {
            GameObject target = activeChar.getTarget();
            if (target == null) {
              activeChar.sendMessage("Select character or specify player name.");
              return true;
            }

            if (!target.isPlayer()) {
              activeChar.sendPacket(Msg.INVALID_TARGET);
              return true;
            }

            player = (Player)target;
          } else {
            player = World.getPlayer(wordList[1]);
            if (player == null) {
              activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
              return true;
            }
          }

          if (player.getObjectId() == activeChar.getObjectId()) {
            activeChar.sendMessage("You can't logout your character.");
          } else {
            activeChar.sendMessage("Character " + player.getName() + " disconnected from server.");
            if (player.isInOfflineMode()) {
              player.setOfflineMode(false);
              player.kick();
              return true;
            } else {
              player.sendMessage(new CustomMessage("admincommandhandlers.AdminDisconnect.YoureKickedByGM", player, new Object[0]));
              player.sendPacket(Msg.YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_LOGIN_AGAIN);
              ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                public void runImpl() throws Exception {
                  player.kick();
                }
              }, 500L);
            }
          }
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminDisconnect.Commands.values();
  }

  private static enum Commands {
    admin_disconnect,
    admin_kick;

    private Commands() {
    }
  }
}
