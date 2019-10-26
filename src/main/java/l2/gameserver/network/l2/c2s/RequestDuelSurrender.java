//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.network.l2.GameClient;

public class RequestDuelSurrender extends L2GameClientPacket {
  public RequestDuelSurrender() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      DuelEvent duelEvent = (DuelEvent)player.getEvent(DuelEvent.class);
      if (duelEvent != null) {
        duelEvent.packetSurrender(player);
      }
    }
  }
}
