//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.matching.PartyMatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;

public class RequestPartyMatchList extends L2GameClientPacket {
  private int _lootDist;
  private int _maxMembers;
  private int _minLevel;
  private int _maxLevel;
  private int _roomId;
  private String _roomTitle;

  public RequestPartyMatchList() {
  }

  protected void readImpl() {
    this._roomId = this.readD();
    this._maxMembers = this.readD();
    this._minLevel = this.readD();
    this._maxLevel = this.readD();
    this._lootDist = this.readD();
    this._roomTitle = this.readS(64);
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      MatchingRoom room = player.getMatchingRoom();
      if (room == null) {
        new PartyMatchingRoom(player, this._minLevel, this._maxLevel, this._maxMembers, this._lootDist, this._roomTitle);
      } else if (room.getId() == this._roomId && room.getType() == MatchingRoom.PARTY_MATCHING && room.getLeader() == player) {
        room.setMinLevel(this._minLevel);
        room.setMaxLevel(this._maxLevel);
        room.setMaxMemberSize(this._maxMembers);
        room.setTopic(this._roomTitle);
        room.setLootType(this._lootDist);
        room.broadCast(new IStaticPacket[]{room.infoRoomPacket()});
      }

    }
  }
}
