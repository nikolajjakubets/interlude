//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class KeyPacket extends L2GameServerPacket {
  private byte[] _key;

  public KeyPacket(byte[] key) {
    this._key = key;
  }

  public void writeImpl() {
    this.writeC(0);
    if (this._key != null && this._key.length != 0) {
      this.writeC(1);
      this.writeB(this._key);
      this.writeD(1);
      this.writeD(0);
      this.writeC(0);
      this.writeD(0);
    } else {
      this.writeC(0);
    }
  }
}
