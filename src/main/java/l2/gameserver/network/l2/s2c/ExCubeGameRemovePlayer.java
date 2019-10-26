//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExCubeGameRemovePlayer extends L2GameServerPacket {
  private int _objectId;
  private boolean _isRedTeam;

  public ExCubeGameRemovePlayer(Player player, boolean isRedTeam) {
    this._objectId = player.getObjectId();
    this._isRedTeam = isRedTeam;
  }

  protected void writeImpl() {
    this.writeEx(151);
    this.writeD(2);
    this.writeD(-1);
    this.writeD(this._isRedTeam ? 1 : 0);
    this.writeD(this._objectId);
  }
}
