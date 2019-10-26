//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;

public class RequestPartyMatchDetail extends L2GameClientPacket {
  private int _roomId;
  private int _locations;
  private int _level;

  public RequestPartyMatchDetail() {
  }

  protected void readImpl() {
    this._roomId = this.readD();
    this._locations = this.readD();
    this._level = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      if (player.getMatchingRoom() == null) {
        if (this._roomId > 0) {
          MatchingRoom room = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.PARTY_MATCHING, this._roomId);
          if (room == null) {
            return;
          }

          room.addMember(player);
        } else {
          Iterator var4 = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, this._locations, this._level == 1, player).iterator();

          while(var4.hasNext()) {
            MatchingRoom room = (MatchingRoom)var4.next();
            if (room.addMember(player)) {
              break;
            }
          }
        }

      }
    }
  }
}
