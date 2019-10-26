//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;

public class RequestDismissPartyRoom extends L2GameClientPacket {
  private int _roomId;

  public RequestDismissPartyRoom() {
  }

  protected void readImpl() {
    this._roomId = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      MatchingRoom room = player.getMatchingRoom();
      if (room.getId() == this._roomId && room.getType() == MatchingRoom.PARTY_MATCHING) {
        if (room.getLeader() == player) {
          room.disband();
        }
      }
    }
  }
}
