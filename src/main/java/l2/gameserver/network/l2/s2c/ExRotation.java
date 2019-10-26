//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExRotation extends L2GameServerPacket {
  private int _charObjId;
  private int _degree;

  public ExRotation(int charId, int degree) {
    this._charObjId = charId;
    this._degree = degree;
  }

  protected void writeImpl() {
    this.writeEx(193);
    this.writeD(this._charObjId);
    this.writeD(this._degree);
  }
}
