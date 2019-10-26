//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.RankPrivs;

public class PledgePowerGradeList extends L2GameServerPacket {
  private RankPrivs[] _privs;

  public PledgePowerGradeList(RankPrivs[] privs) {
    this._privs = privs;
  }

  protected final void writeImpl() {
    this.writeEx(59);
    this.writeD(this._privs.length);
    RankPrivs[] var1 = this._privs;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      RankPrivs element = var1[var3];
      this.writeD(element.getRank());
      this.writeD(element.getParty());
    }

  }
}
