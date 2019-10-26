//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class CameraMode extends L2GameServerPacket {
  int _mode;

  public CameraMode(int mode) {
    this._mode = mode;
  }

  protected final void writeImpl() {
    this.writeC(241);
    this.writeD(this._mode);
  }
}
