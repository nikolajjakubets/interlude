//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExOlympiadMode extends L2GameServerPacket {
  private int _mode;

  public ExOlympiadMode(int mode) {
    this._mode = mode;
  }

  protected final void writeImpl() {
    this.writeEx(43);
    this.writeC(this._mode);
  }
}
