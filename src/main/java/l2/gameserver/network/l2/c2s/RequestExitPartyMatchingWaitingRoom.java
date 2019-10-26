//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestExitPartyMatchingWaitingRoom extends L2GameClientPacket {
  public RequestExitPartyMatchingWaitingRoom() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      MatchingRoomManager.getInstance().removeFromWaitingList(player);
    }
  }
}
