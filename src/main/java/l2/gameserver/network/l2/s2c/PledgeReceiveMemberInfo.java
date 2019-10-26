//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.UnitMember;

public class PledgeReceiveMemberInfo extends L2GameServerPacket {
  private UnitMember _member;

  public PledgeReceiveMemberInfo(UnitMember member) {
    this._member = member;
  }

  protected final void writeImpl() {
    this.writeEx(61);
    this.writeD(this._member.getPledgeType());
    this.writeS(this._member.getName());
    this.writeS(this._member.getTitle());
    this.writeD(this._member.getPowerGrade());
    this.writeS(this._member.getSubUnit().getName());
    this.writeS(this._member.getRelatedName());
  }
}

