//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.matching;

import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExDissmissMpccRoom;
import l2.gameserver.network.l2.s2c.ExManageMpccRoomMember;
import l2.gameserver.network.l2.s2c.ExMpccRoomMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PartyRoomInfo;

public class CCMatchingRoom extends MatchingRoom {
  public CCMatchingRoom(Player leader, int minLevel, int maxLevel, int maxMemberSize, int lootType, String topic) {
    super(leader, minLevel, maxLevel, maxMemberSize, lootType, topic);
    leader.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED);
  }

  public SystemMsg notValidMessage() {
    return SystemMsg.YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
  }

  public SystemMsg enterMessage() {
    return SystemMsg.C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM;
  }

  public SystemMsg exitMessage(boolean toOthers, boolean kick) {
    if (!toOthers) {
      return kick ? SystemMsg.YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM : SystemMsg.YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM;
    } else {
      return null;
    }
  }

  public SystemMsg closeRoomMessage() {
    return SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED;
  }

  public L2GameServerPacket closeRoomPacket() {
    return ExDissmissMpccRoom.STATIC;
  }

  public L2GameServerPacket infoRoomPacket() {
    return new PartyRoomInfo(this);
  }

  public L2GameServerPacket addMemberPacket(Player $member, Player active) {
    return new ExManageMpccRoomMember(ExManageMpccRoomMember.ADD_MEMBER, this, active);
  }

  public L2GameServerPacket removeMemberPacket(Player $member, Player active) {
    return new ExManageMpccRoomMember(ExManageMpccRoomMember.REMOVE_MEMBER, this, active);
  }

  public L2GameServerPacket updateMemberPacket(Player $member, Player active) {
    return new ExManageMpccRoomMember(ExManageMpccRoomMember.UPDATE_MEMBER, this, active);
  }

  public L2GameServerPacket membersPacket(Player active) {
    return new ExMpccRoomMember(this, active);
  }

  public int getType() {
    return CC_MATCHING;
  }

  public void disband() {
    Party party = this._leader.getParty();
    if (party != null) {
      CommandChannel commandChannel = party.getCommandChannel();
      if (commandChannel != null) {
        commandChannel.setMatchingRoom((MatchingRoom)null);
      }
    }

    super.disband();
  }

  public int getMemberType(Player member) {
    Party party = this._leader.getParty();
    CommandChannel commandChannel = party.getCommandChannel();
    if (member == this._leader) {
      return MatchingRoom.UNION_LEADER;
    } else if (member.getParty() == null) {
      return MatchingRoom.WAIT_NORMAL;
    } else if (member.getParty() != party && !commandChannel.getParties().contains(member.getParty())) {
      return member.getParty() != null ? MatchingRoom.WAIT_PARTY : MatchingRoom.WAIT_NORMAL;
    } else {
      return MatchingRoom.UNION_PARTY;
    }
  }
}
