//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class GmListTable {
  public GmListTable() {
  }

  public static List<Player> getAllGMs() {
    List<Player> gmList = new ArrayList<>();
    Iterator var1 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var1.hasNext()) {
      Player player = (Player)var1.next();
      if (player.isGM()) {
        gmList.add(player);
      }
    }

    return gmList;
  }

  public static List<Player> getAllVisibleGMs() {
    List<Player> gmList = new ArrayList<>();
    Iterator var1 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var1.hasNext()) {
      Player player = (Player)var1.next();
      if (player.isGM() && !player.isInvisible()) {
        gmList.add(player);
      }
    }

    return gmList;
  }

  public static void sendListToPlayer(Player player) {
    List<Player> gmList = Config.HIDE_GM_STATUS ? getAllVisibleGMs() : getAllGMs();
    if (gmList.isEmpty()) {
      player.sendPacket(Msg.THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY);
    } else {
      player.sendPacket(Msg._GM_LIST_);
      Iterator var2 = gmList.iterator();

      while(var2.hasNext()) {
        Player gm = (Player)var2.next();
        player.sendPacket((new SystemMessage(704)).addString(gm.getName()));
      }

    }
  }

  public static void broadcastToGMs(L2GameServerPacket packet) {
    Iterator var1 = getAllGMs().iterator();

    while(var1.hasNext()) {
      Player gm = (Player)var1.next();
      gm.sendPacket(packet);
    }

  }

  public static void broadcastMessageToGMs(String message) {
    Iterator var1 = getAllGMs().iterator();

    while(var1.hasNext()) {
      Player gm = (Player)var1.next();
      gm.sendMessage(message);
    }

  }
}
