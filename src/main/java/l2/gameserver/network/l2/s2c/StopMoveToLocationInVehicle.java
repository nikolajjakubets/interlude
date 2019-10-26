//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.utils.Location;

public class StopMoveToLocationInVehicle extends L2GameServerPacket {
  private int _boatObjectId;
  private int _playerObjectId;
  private int _heading;
  private Location _loc;

  public StopMoveToLocationInVehicle(Player player) {
    this._boatObjectId = player.getBoat().getObjectId();
    this._playerObjectId = player.getObjectId();
    this._loc = player.getInBoatPosition();
    this._heading = player.getHeading();
  }

  protected final void writeImpl() {
    this.writeC(114);
    this.writeD(this._playerObjectId);
    this.writeD(this._boatObjectId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._heading);
  }
}
