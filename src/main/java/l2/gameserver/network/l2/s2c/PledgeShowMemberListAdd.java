//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListAdd extends L2GameServerPacket {
  private PledgeShowMemberListAdd.PledgePacketMember _member;

  public PledgeShowMemberListAdd(UnitMember member) {
    this._member = new PledgeShowMemberListAdd.PledgePacketMember(member);
  }

  protected final void writeImpl() {
    this.writeC(85);
    this.writeS(this._member._name);
    this.writeD(this._member._level);
    this.writeD(this._member._classId);
    this.writeD(this._member._sex);
    this.writeD(this._member._race);
    this.writeD(this._member._online);
    this.writeD(this._member._pledgeType);
  }

  private class PledgePacketMember {
    private String _name;
    private int _level;
    private int _classId;
    private int _sex;
    private int _race;
    private int _online;
    private int _pledgeType;

    public PledgePacketMember(UnitMember m) {
      this._name = m.getName();
      this._level = m.getLevel();
      this._classId = m.getClassId();
      this._sex = m.getSex();
      this._race = 0;
      this._online = m.isOnline() ? m.getObjectId() : 0;
      this._pledgeType = m.getPledgeType();
    }
  }
}
