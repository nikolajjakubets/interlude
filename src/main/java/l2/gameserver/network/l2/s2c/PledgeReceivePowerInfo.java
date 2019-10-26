//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.RankPrivs;
import l2.gameserver.model.pledge.UnitMember;

public class PledgeReceivePowerInfo extends L2GameServerPacket {
  private int PowerGrade;
  private int privs;
  private String member_name;

  public PledgeReceivePowerInfo(UnitMember member) {
    this.PowerGrade = member.getPowerGrade();
    this.member_name = member.getName();
    if (member.isClanLeader()) {
      this.privs = 8388606;
    } else {
      RankPrivs temp = member.getClan().getRankPrivs(member.getPowerGrade());
      if (temp != null) {
        this.privs = temp.getPrivs();
      } else {
        this.privs = 0;
      }
    }

  }

  protected final void writeImpl() {
    this.writeEx(60);
    this.writeD(this.PowerGrade);
    this.writeS(this.member_name);
    this.writeD(this.privs);
  }
}
