//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExPCCafePointInfo extends L2GameServerPacket {
  private int _mAddPoint;
  private int _mPeriodType;
  private int _pointType;
  private int _pcBangPoints;
  private int _remainTime;

  public ExPCCafePointInfo(Player player, int mAddPoint, int mPeriodType, int pointType, int remainTime) {
    this._pcBangPoints = player.getPcBangPoints();
    this._mAddPoint = mAddPoint;
    this._mPeriodType = mPeriodType;
    this._pointType = pointType;
    this._remainTime = remainTime;
  }

  protected final void writeImpl() {
    this.writeEx(49);
    this.writeD(this._pcBangPoints);
    this.writeD(this._mAddPoint);
    this.writeC(this._mPeriodType);
    this.writeD(this._remainTime);
    this.writeC(this._pointType);
  }
}
