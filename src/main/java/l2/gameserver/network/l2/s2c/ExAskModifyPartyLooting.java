//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExAskModifyPartyLooting extends L2GameServerPacket {
  private String _requestor;
  private int _mode;

  public ExAskModifyPartyLooting(String name, int mode) {
    this._requestor = name;
    this._mode = mode;
  }

  protected void writeImpl() {
    this.writeEx(191);
    this.writeS(this._requestor);
    this.writeD(this._mode);
  }
}
