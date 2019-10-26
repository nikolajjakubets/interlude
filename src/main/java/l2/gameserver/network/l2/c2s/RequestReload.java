//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.GameClient;

public class RequestReload extends L2GameClientPacket {
  public RequestReload() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    Player player = client.getActiveChar();
    if (player != null) {
      long now = System.currentTimeMillis();
      if (now - client.getLastIncomePacketTimeStamp(RequestReload.class) < (long)Config.RELOAD_PACKET_DELAY) {
        player.sendActionFailed();
      } else {
        client.setLastIncomePacketTimeStamp(RequestReload.class, now);
        player.sendUserInfo(true);
        World.showObjectsToPlayer(player);
      }
    }
  }
}
