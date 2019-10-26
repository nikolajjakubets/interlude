//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExRedSky extends L2GameServerPacket {
  private int _duration;

  public ExRedSky(int duration) {
    this._duration = duration;
  }

  protected final void writeImpl() {
    this.writeEx(64);
    this.writeD(this._duration);
  }
}
