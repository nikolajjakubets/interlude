//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;

public class ExMpccRoomMember extends L2GameServerPacket {
  private int _type;
  private List<ExMpccRoomMember.MpccRoomMemberInfo> _members = Collections.emptyList();

  public ExMpccRoomMember(MatchingRoom room, Player player) {
    this._type = room.getMemberType(player);
    this._members = new ArrayList(room.getPlayers().size());
    Iterator var3 = room.getPlayers().iterator();

    while(var3.hasNext()) {
      Player member = (Player)var3.next();
      this._members.add(new ExMpccRoomMember.MpccRoomMemberInfo(member, room.getMemberType(member)));
    }

  }

  public void writeImpl() {
    this.writeEx(14);
    this.writeD(this._type);
    this.writeD(this._members.size());
    Iterator var1 = this._members.iterator();

    while(var1.hasNext()) {
      ExMpccRoomMember.MpccRoomMemberInfo member = (ExMpccRoomMember.MpccRoomMemberInfo)var1.next();
      this.writeD(member.objectId);
      this.writeS(member.name);
      this.writeD(member.level);
      this.writeD(member.classId);
      this.writeD(member.location);
      this.writeD(member.memberType);
    }

  }

  static class MpccRoomMemberInfo {
    public final int objectId;
    public final int classId;
    public final int level;
    public final int location;
    public final int memberType;
    public final String name;

    public MpccRoomMemberInfo(Player member, int type) {
      this.objectId = member.getObjectId();
      this.name = member.getName();
      this.classId = member.getClassId().ordinal();
      this.level = member.getLevel();
      this.location = MatchingRoomManager.getInstance().getLocation(member);
      this.memberType = type;
    }
  }
}
