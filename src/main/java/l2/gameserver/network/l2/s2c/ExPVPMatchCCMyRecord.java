//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPVPMatchCCMyRecord extends L2GameServerPacket {
  private int _points;

  public ExPVPMatchCCMyRecord(int points) {
    this._points = points;
  }

  public void writeImpl() {
    this.writeEx(138);
    this.writeD(this._points);
  }
}
