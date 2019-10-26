//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExCubeGameChangeTeam extends L2GameServerPacket {
  private int _objectId;
  private boolean _fromRedTeam;

  public ExCubeGameChangeTeam(Player player, boolean fromRedTeam) {
    this._objectId = player.getObjectId();
    this._fromRedTeam = fromRedTeam;
  }

  protected void writeImpl() {
    this.writeEx(151);
    this.writeD(5);
    this.writeD(this._objectId);
    this.writeD(this._fromRedTeam ? 1 : 0);
    this.writeD(this._fromRedTeam ? 0 : 1);
  }
}
