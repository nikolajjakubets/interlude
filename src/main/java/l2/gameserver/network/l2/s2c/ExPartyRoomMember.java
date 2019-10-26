//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;

public class ExPartyRoomMember extends L2GameServerPacket {
  private int _type;
  private List<ExPartyRoomMember.PartyRoomMemberInfo> _members = Collections.emptyList();

  public ExPartyRoomMember(MatchingRoom room, Player activeChar) {
    this._type = room.getMemberType(activeChar);
    this._members = new ArrayList(room.getPlayers().size());
    Iterator var3 = room.getPlayers().iterator();

    while(var3.hasNext()) {
      Player $member = (Player)var3.next();
      this._members.add(new ExPartyRoomMember.PartyRoomMemberInfo($member, room.getMemberType($member)));
    }

  }

  protected final void writeImpl() {
    this.writeEx(14);
    this.writeD(this._type);
    this.writeD(this._members.size());
    Iterator var1 = this._members.iterator();

    while(var1.hasNext()) {
      ExPartyRoomMember.PartyRoomMemberInfo member_info = (ExPartyRoomMember.PartyRoomMemberInfo)var1.next();
      this.writeD(member_info.objectId);
      this.writeS(member_info.name);
      this.writeD(member_info.classId);
      this.writeD(member_info.level);
      this.writeD(member_info.location);
      this.writeD(member_info.memberType);
    }

  }

  static class PartyRoomMemberInfo {
    public final int objectId;
    public final int classId;
    public final int level;
    public final int location;
    public final int memberType;
    public final String name;
    public final int[] instanceReuses;

    public PartyRoomMemberInfo(Player member, int type) {
      this.objectId = member.getObjectId();
      this.name = member.getName();
      this.classId = member.getClassId().ordinal();
      this.level = member.getLevel();
      this.location = MatchingRoomManager.getInstance().getLocation(member);
      this.memberType = type;
      this.instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
    }
  }
}
