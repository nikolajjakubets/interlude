//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExSetPartyLooting extends L2GameServerPacket {
  private int _result;
  private int _mode;

  public ExSetPartyLooting(int result, int mode) {
    this._result = result;
    this._mode = mode;
  }

  protected void writeImpl() {
    this.writeEx(192);
    this.writeD(this._result);
    this.writeD(this._mode);
  }
}
