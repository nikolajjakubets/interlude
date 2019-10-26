//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;

public class RequestOustFromPartyRoom extends L2GameClientPacket {
  private int _objectId;

  public RequestOustFromPartyRoom() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    MatchingRoom room = player.getMatchingRoom();
    if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING) {
      if (room.getLeader() == player) {
        Player member = GameObjectsStorage.getPlayer(this._objectId);
        if (member != null) {
          if (member != room.getLeader()) {
            room.removeMember(member, true);
          }
        }
      }
    }
  }
}
