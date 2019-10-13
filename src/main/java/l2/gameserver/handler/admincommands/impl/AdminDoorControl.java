//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.DoorInstance;

public class AdminDoorControl implements IAdminCommandHandler {
  public AdminDoorControl() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminDoorControl.Commands command = (AdminDoorControl.Commands)comm;
    if (!activeChar.getPlayerAccess().Door) {
      return false;
    } else {
      GameObject target;
      switch(command) {
        case admin_open:
          if (wordList.length > 1) {
            target = World.getAroundObjectById(activeChar, Integer.parseInt(wordList[1]));
          } else {
            target = activeChar.getTarget();
          }

          if (target != null && target.isDoor()) {
            ((DoorInstance)target).openMe();
          } else {
            activeChar.sendPacket(Msg.INVALID_TARGET);
          }
          break;
        case admin_close:
          if (wordList.length > 1) {
            target = World.getAroundObjectById(activeChar, Integer.parseInt(wordList[1]));
          } else {
            target = activeChar.getTarget();
          }

          if (target != null && target.isDoor()) {
            ((DoorInstance)target).closeMe();
          } else {
            activeChar.sendPacket(Msg.INVALID_TARGET);
          }
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminDoorControl.Commands.values();
  }

  private static enum Commands {
    admin_open,
    admin_close;

    private Commands() {
    }
  }
}
