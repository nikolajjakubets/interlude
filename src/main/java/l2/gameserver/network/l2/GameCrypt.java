//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2;

public class GameCrypt {
  private final byte[] _inKey = new byte[16];
  private final byte[] _outKey = new byte[16];
  private boolean _isEnabled = false;

  public GameCrypt() {
  }

  public void setKey(byte[] key) {
    System.arraycopy(key, 0, this._inKey, 0, 16);
    System.arraycopy(key, 0, this._outKey, 0, 16);
  }

  public void setKey(byte[] key, boolean value) {
    this.setKey(key);
  }

  public boolean decrypt(byte[] raw, int offset, int size) {
    if (!this._isEnabled) {
      return true;
    } else {
      int temp = 0;

      int old;
      for(old = 0; old < size; ++old) {
        int temp2 = raw[offset + old] & 255;
        raw[offset + old] = (byte)(temp2 ^ this._inKey[old & 15] ^ temp);
        temp = temp2;
      }

      old = this._inKey[8] & 255;
      old |= this._inKey[9] << 8 & '\uff00';
      old |= this._inKey[10] << 16 & 16711680;
      old |= this._inKey[11] << 24 & -16777216;
      old += size;
      this._inKey[8] = (byte)(old & 255);
      this._inKey[9] = (byte)(old >> 8 & 255);
      this._inKey[10] = (byte)(old >> 16 & 255);
      this._inKey[11] = (byte)(old >> 24 & 255);
      return true;
    }
  }

  public void encrypt(byte[] raw, int offset, int size) {
    if (!this._isEnabled) {
      this._isEnabled = true;
    } else {
      int temp = 0;

      int old;
      for(old = 0; old < size; ++old) {
        int temp2 = raw[offset + old] & 255;
        temp ^= temp2 ^ this._outKey[old & 15];
        raw[offset + old] = (byte)temp;
      }

      old = this._outKey[8] & 255;
      old |= this._outKey[9] << 8 & '\uff00';
      old |= this._outKey[10] << 16 & 16711680;
      old |= this._outKey[11] << 24 & -16777216;
      old += size;
      this._outKey[8] = (byte)(old & 255);
      this._outKey[9] = (byte)(old >> 8 & 255);
      this._outKey[10] = (byte)(old >> 16 & 255);
      this._outKey[11] = (byte)(old >> 24 & 255);
    }
  }
}
