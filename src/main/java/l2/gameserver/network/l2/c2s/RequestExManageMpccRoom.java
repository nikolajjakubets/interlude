//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;

public class RequestExManageMpccRoom extends L2GameClientPacket {
  private int _id;
  private int _memberSize;
  private int _minLevel;
  private int _maxLevel;
  private String _topic;

  public RequestExManageMpccRoom() {
  }

  protected void readImpl() {
    this._id = this.readD();
    this._memberSize = this.readD();
    this._minLevel = this.readD();
    this._maxLevel = this.readD();
    this.readD();
    this._topic = this.readS();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      MatchingRoom room = player.getMatchingRoom();
      if (room != null && room.getId() == this._id && room.getType() == MatchingRoom.CC_MATCHING) {
        if (room.getLeader() == player) {
          room.setTopic(this._topic);
          room.setMaxMemberSize(this._memberSize);
          room.setMinLevel(this._minLevel);
          room.setMaxLevel(this._maxLevel);
          room.broadCast(new IStaticPacket[]{room.infoRoomPacket()});
          player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
        }
      }
    }
  }
}
