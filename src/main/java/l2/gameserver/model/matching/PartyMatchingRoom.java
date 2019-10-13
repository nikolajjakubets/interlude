//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.matching;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExClosePartyRoom;
import l2.gameserver.network.l2.s2c.ExPartyRoomMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PartyRoomInfo;

public class PartyMatchingRoom extends MatchingRoom {
  public PartyMatchingRoom(Player leader, int minLevel, int maxLevel, int maxMemberSize, int lootType, String topic) {
    super(leader, minLevel, maxLevel, maxMemberSize, lootType, topic);
    leader.broadcastCharInfo();
  }

  public SystemMsg notValidMessage() {
    return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM;
  }

  public SystemMsg enterMessage() {
    return SystemMsg.C1_HAS_ENTERED_THE_PARTY_ROOM;
  }

  public SystemMsg exitMessage(boolean toOthers, boolean kick) {
    if (toOthers) {
      return kick ? SystemMsg.C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM : SystemMsg.C1_HAS_LEFT_THE_PARTY_ROOM;
    } else {
      return kick ? SystemMsg.YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM : SystemMsg.YOU_HAVE_EXITED_THE_PARTY_ROOM;
    }
  }

  public SystemMsg closeRoomMessage() {
    return SystemMsg.THE_PARTY_ROOM_HAS_BEEN_DISBANDED;
  }

  public L2GameServerPacket closeRoomPacket() {
    return ExClosePartyRoom.STATIC;
  }

  public L2GameServerPacket infoRoomPacket() {
    return new PartyRoomInfo(this);
  }

  public L2GameServerPacket addMemberPacket(Player $member, Player active) {
    return this.membersPacket($member);
  }

  public L2GameServerPacket removeMemberPacket(Player $member, Player active) {
    return this.membersPacket($member);
  }

  public L2GameServerPacket updateMemberPacket(Player $member, Player active) {
    return this.membersPacket($member);
  }

  public L2GameServerPacket membersPacket(Player active) {
    return new ExPartyRoomMember(this, active);
  }

  public int getType() {
    return PARTY_MATCHING;
  }

  public int getMemberType(Player member) {
    return member.equals(this._leader) ? ROOM_MASTER : (member.getParty() != null && this._leader.getParty() == member.getParty() ? PARTY_MEMBER : WAIT_PLAYER);
  }
}
