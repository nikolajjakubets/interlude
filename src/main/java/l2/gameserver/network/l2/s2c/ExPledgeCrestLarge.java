//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPledgeCrestLarge extends L2GameServerPacket {
  private int _crestId;
  private byte[] _data;

  public ExPledgeCrestLarge(int crestId, byte[] data) {
    this._crestId = crestId;
    this._data = data;
  }

  protected final void writeImpl() {
    this.writeEx(40);
    this.writeD(0);
    this.writeD(this._crestId);
    this.writeD(this._data.length);
    this.writeB(this._data);
  }
}
