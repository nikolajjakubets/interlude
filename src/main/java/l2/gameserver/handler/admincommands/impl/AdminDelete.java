//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import org.apache.commons.lang3.math.NumberUtils;

public class AdminDelete implements IAdminCommandHandler {
  public AdminDelete() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminDelete.Commands command = (AdminDelete.Commands)comm;
    if (!activeChar.getPlayerAccess().CanEditNPC) {
      return false;
    } else {
      switch(command) {
        case admin_delete:
          GameObject obj = wordList.length == 1 ? activeChar.getTarget() : GameObjectsStorage.getNpc(NumberUtils.toInt(wordList[1]));
          if (obj != null && ((GameObject)obj).isNpc()) {
            NpcInstance target = (NpcInstance)obj;
            target.deleteMe();
            Spawner spawn = target.getSpawn();
            if (spawn != null) {
              spawn.stopRespawn();
            }
          } else {
            activeChar.sendPacket(Msg.INVALID_TARGET);
          }
        default:
          return true;
      }
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminDelete.Commands.values();
  }

  private static enum Commands {
    admin_delete;

    private Commands() {
    }
  }
}
