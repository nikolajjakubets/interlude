//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExCubeGameChangeTimeToStart extends L2GameServerPacket {
  int _seconds;

  public ExCubeGameChangeTimeToStart(int seconds) {
    this._seconds = seconds;
  }

  protected void writeImpl() {
    this.writeEx(151);
    this.writeD(3);
    this.writeD(this._seconds);
  }
}
