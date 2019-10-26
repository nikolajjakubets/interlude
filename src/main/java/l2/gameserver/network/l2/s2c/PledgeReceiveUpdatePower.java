//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class PledgeReceiveUpdatePower extends L2GameServerPacket {
  private int _privs;

  public PledgeReceiveUpdatePower(int privs) {
    this._privs = privs;
  }

  protected final void writeImpl() {
    this.writeEx(66);
    this.writeD(this._privs);
  }
}
