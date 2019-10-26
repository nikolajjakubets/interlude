//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExCubeGameExtendedChangePoints extends L2GameServerPacket {
  private int _timeLeft;
  private int _bluePoints;
  private int _redPoints;
  private boolean _isRedTeam;
  private int _objectId;
  private int _playerPoints;

  public ExCubeGameExtendedChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, Player player, int playerPoints) {
    this._timeLeft = timeLeft;
    this._bluePoints = bluePoints;
    this._redPoints = redPoints;
    this._isRedTeam = isRedTeam;
    this._objectId = player.getObjectId();
    this._playerPoints = playerPoints;
  }

  protected void writeImpl() {
    this.writeEx(152);
    this.writeD(0);
    this.writeD(this._timeLeft);
    this.writeD(this._bluePoints);
    this.writeD(this._redPoints);
    this.writeD(this._isRedTeam ? 1 : 0);
    this.writeD(this._objectId);
    this.writeD(this._playerPoints);
  }
}
