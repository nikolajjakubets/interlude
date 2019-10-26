//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;

public class RequestExWithdrawMpccRoom extends L2GameClientPacket {
  public RequestExWithdrawMpccRoom() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      MatchingRoom room = player.getMatchingRoom();
      if (room != null && room.getType() == MatchingRoom.CC_MATCHING) {
        if (room.getLeader() != player) {
          room.removeMember(player, false);
        }
      }
    }
  }
}
