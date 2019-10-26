//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;

public class RequestExJoinMpccRoom extends L2GameClientPacket {
  private int _roomId;

  public RequestExJoinMpccRoom() {
  }

  protected void readImpl() throws Exception {
    this._roomId = this.readD();
  }

  protected void runImpl() throws Exception {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      if (player.getMatchingRoom() == null) {
        MatchingRoom room = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.CC_MATCHING, this._roomId);
        if (room != null) {
          room.addMember(player);
        }
      }
    }
  }
}
