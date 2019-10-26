//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.SubUnit;

public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket {
  private int type;
  private String _name;
  private String leader_name;

  public PledgeReceiveSubPledgeCreated(SubUnit subPledge) {
    this.type = subPledge.getType();
    this._name = subPledge.getName();
    this.leader_name = subPledge.getLeaderName();
  }

  protected final void writeImpl() {
    this.writeEx(63);
    this.writeD(1);
    this.writeD(this.type);
    this.writeS(this._name);
    this.writeS(this.leader_name);
  }
}
