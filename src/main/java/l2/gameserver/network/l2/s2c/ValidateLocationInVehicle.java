//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.utils.Location;

public class ValidateLocationInVehicle extends L2GameServerPacket {
  private int _playerObjectId;
  private int _boatObjectId;
  private Location _loc;

  public ValidateLocationInVehicle(Player player) {
    this._playerObjectId = player.getObjectId();
    this._boatObjectId = player.getBoat().getObjectId();
    this._loc = player.getInBoatPosition();
  }

  protected final void writeImpl() {
    this.writeC(115);
    this.writeD(this._playerObjectId);
    this.writeD(this._boatObjectId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._loc.h);
  }
}
