//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class PledgeCrest extends L2GameServerPacket {
  private int _crestId;
  private int _crestSize;
  private byte[] _data;

  public PledgeCrest(int crestId, byte[] data) {
    this._crestId = crestId;
    this._data = data;
    this._crestSize = this._data.length;
  }

  protected final void writeImpl() {
    this.writeC(108);
    this.writeD(this._crestId);
    this.writeD(this._crestSize);
    this.writeB(this._data);
  }
}
