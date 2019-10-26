//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class JoinPledge extends L2GameServerPacket {
  private int _pledgeId;

  public JoinPledge(int pledgeId) {
    this._pledgeId = pledgeId;
  }

  protected final void writeImpl() {
    this.writeC(51);
    this.writeD(this._pledgeId);
  }
}
