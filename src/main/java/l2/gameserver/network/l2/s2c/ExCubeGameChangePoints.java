//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExCubeGameChangePoints extends L2GameServerPacket {
  int _timeLeft;
  int _bluePoints;
  int _redPoints;

  public ExCubeGameChangePoints(int timeLeft, int bluePoints, int redPoints) {
    this._timeLeft = timeLeft;
    this._bluePoints = bluePoints;
    this._redPoints = redPoints;
  }

  protected void writeImpl() {
    this.writeEx(152);
    this.writeD(2);
    this.writeD(this._timeLeft);
    this.writeD(this._bluePoints);
    this.writeD(this._redPoints);
  }
}
