//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.network.l2.s2c.CameraMode;
import l2.gameserver.network.l2.s2c.SpecialCamera;

public class AdminCamera implements IAdminCommandHandler {
  public AdminCamera() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminCamera.Commands command = (AdminCamera.Commands)comm;
    if (!activeChar.getPlayerAccess().Menu) {
      return false;
    } else {
      int mode;
      switch(command) {
        case admin_freelook:
          if (fullString.length() <= 15) {
            activeChar.sendMessage("Usage: //freelook 1 or //freelook 0");
            return false;
          }

          fullString = fullString.substring(15);
          mode = Integer.parseInt(fullString);
          if (mode == 1) {
            activeChar.setInvisibleType(InvisibleType.NORMAL);
            activeChar.setIsInvul(true);
            activeChar.setNoChannel(-1L);
            activeChar.setFlying(true);
          } else {
            activeChar.setInvisibleType(InvisibleType.NONE);
            activeChar.setIsInvul(false);
            activeChar.setNoChannel(0L);
            activeChar.setFlying(false);
          }

          activeChar.sendPacket(new CameraMode(mode));
          break;
        case admin_cinematic:
          mode = Integer.parseInt(wordList[1]);
          int dist = Integer.parseInt(wordList[2]);
          int yaw = Integer.parseInt(wordList[3]);
          int pitch = Integer.parseInt(wordList[4]);
          int time = Integer.parseInt(wordList[5]);
          int duration = Integer.parseInt(wordList[6]);
          activeChar.sendPacket(new SpecialCamera(mode, dist, yaw, pitch, time, duration));
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminCamera.Commands.values();
  }

  private static enum Commands {
    admin_freelook,
    admin_cinematic;

    private Commands() {
    }
  }
}
