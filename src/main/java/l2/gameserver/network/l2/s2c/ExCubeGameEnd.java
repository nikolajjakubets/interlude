//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExCubeGameEnd extends L2GameServerPacket {
  boolean _isRedTeamWin;

  public ExCubeGameEnd(boolean isRedTeamWin) {
    this._isRedTeamWin = isRedTeamWin;
  }

  protected void writeImpl() {
    this.writeEx(152);
    this.writeD(1);
    this.writeD(this._isRedTeamWin ? 1 : 0);
  }
}
