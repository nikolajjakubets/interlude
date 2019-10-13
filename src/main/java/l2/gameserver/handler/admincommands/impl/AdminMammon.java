//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class AdminMammon implements IAdminCommandHandler {
  List<Integer> npcIds = new ArrayList();

  public AdminMammon() {
  }

  public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
    AdminMammon.Commands command = (AdminMammon.Commands)comm;
    this.npcIds.clear();
    if (!activeChar.getPlayerAccess().Menu) {
      return false;
    } else {
      int npcId;
      if (fullString.startsWith("admin_find_mammon")) {
        this.npcIds.add(31113);
        this.npcIds.add(31126);
        this.npcIds.add(31092);
        npcId = -1;

        try {
          if (fullString.length() > 16) {
            npcId = Integer.parseInt(fullString.substring(18));
          }
        } catch (Exception var9) {
        }

        this.findAdminNPCs(activeChar, this.npcIds, npcId, -1);
      } else if (fullString.equals("admin_show_mammon")) {
        this.npcIds.add(31113);
        this.npcIds.add(31126);
        this.findAdminNPCs(activeChar, this.npcIds, -1, 1);
      } else if (fullString.equals("admin_hide_mammon")) {
        this.npcIds.add(31113);
        this.npcIds.add(31126);
        this.findAdminNPCs(activeChar, this.npcIds, -1, 0);
      } else if (fullString.startsWith("admin_list_spawns")) {
        npcId = 0;

        try {
          npcId = Integer.parseInt(fullString.substring(18).trim());
        } catch (Exception var8) {
          activeChar.sendMessage("Command format is //list_spawns <NPC_ID>");
        }

        this.npcIds.add(npcId);
        this.findAdminNPCs(activeChar, this.npcIds, -1, -1);
      } else if (fullString.startsWith("admin_msg")) {
        activeChar.sendPacket(new SystemMessage(Integer.parseInt(fullString.substring(10).trim())));
      }

      return true;
    }
  }

  public Enum[] getAdminCommandEnum() {
    return AdminMammon.Commands.values();
  }

  public void findAdminNPCs(Player activeChar, List<Integer> npcIdList, int teleportIndex, int makeVisible) {
    int index = 0;
    Iterator var6 = GameObjectsStorage.getAllNpcsForIterate().iterator();

    while(var6.hasNext()) {
      NpcInstance npcInst = (NpcInstance)var6.next();
      int npcId = npcInst.getNpcId();
      if (npcIdList.contains(npcId)) {
        if (makeVisible == 1) {
          npcInst.spawnMe();
        } else if (makeVisible == 0) {
          npcInst.decayMe();
        }

        if (npcInst.isVisible()) {
          ++index;
          if (teleportIndex > -1) {
            if (teleportIndex == index) {
              activeChar.teleToLocation(npcInst.getLoc());
            }
          } else {
            activeChar.sendMessage(index + " - " + npcInst.getName() + " (" + npcInst.getObjectId() + "): " + npcInst.getX() + " " + npcInst.getY() + " " + npcInst.getZ());
          }
        }
      }
    }

  }

  private static enum Commands {
    admin_find_mammon,
    admin_show_mammon,
    admin_hide_mammon,
    admin_list_spawns;

    private Commands() {
    }
  }
}
