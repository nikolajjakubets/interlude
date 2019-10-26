//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExBR_GamePoint extends L2GameServerPacket {
  private int _objectId;
  private long _points;

  public ExBR_GamePoint(Player player) {
    this._objectId = player.getObjectId();
    this._points = player.getPremiumPoints();
  }

  protected void writeImpl() {
    this.writeEx(213);
    this.writeD(this._objectId);
    this.writeQ(this._points);
    this.writeD(0);
  }
}
