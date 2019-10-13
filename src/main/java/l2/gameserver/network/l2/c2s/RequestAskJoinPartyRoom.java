//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExAskJoinPartyRoom;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestAskJoinPartyRoom extends L2GameClientPacket {
  private String _name;

  public RequestAskJoinPartyRoom() {
  }

  protected void readImpl() {
    this._name = this.readS(16);
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Player targetPlayer = World.getPlayer(this._name);
      if (targetPlayer != null && targetPlayer != player) {
        if (player.isProcessingRequest()) {
          player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
        } else if (targetPlayer.isProcessingRequest()) {
          player.sendPacket((new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK)).addName(targetPlayer));
        } else if (targetPlayer.getMatchingRoom() == null) {
          MatchingRoom room = player.getMatchingRoom();
          if (room != null && room.getType() == MatchingRoom.PARTY_MATCHING) {
            if (room.getLeader() != player) {
              player.sendPacket(SystemMsg.ONLY_A_ROOM_LEADER_MAY_INVITE_OTHERS_TO_A_PARTY_ROOM);
            } else if (room.getPlayers().size() >= room.getMaxMembersSize()) {
              player.sendPacket(SystemMsg.THE_PARTY_ROOM_IS_FULL);
            } else {
              (new Request(L2RequestType.PARTY_ROOM, player, targetPlayer)).setTimeout(10000L);
              targetPlayer.sendPacket(new ExAskJoinPartyRoom(player.getName(), room.getTopic()));
              player.sendPacket(((SystemMessage2)(new SystemMessage2(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2)).addName(player)).addString(room.getTopic()));
              targetPlayer.sendPacket(((SystemMessage2)(new SystemMessage2(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2)).addName(player)).addString(room.getTopic()));
            }
          }
        }
      } else {
        player.sendActionFailed();
      }
    }
  }
}
