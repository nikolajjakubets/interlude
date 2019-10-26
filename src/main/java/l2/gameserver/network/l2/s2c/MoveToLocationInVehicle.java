//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.utils.Location;

public class MoveToLocationInVehicle extends L2GameServerPacket {
  private int _playerObjectId;
  private int _boatObjectId;
  private Location _origin;
  private Location _destination;

  public MoveToLocationInVehicle(Player cha, Boat boat, Location origin, Location destination) {
    this._playerObjectId = cha.getObjectId();
    this._boatObjectId = boat.getObjectId();
    this._origin = origin;
    this._destination = destination;
  }

  protected final void writeImpl() {
    this.writeC(113);
    this.writeD(this._playerObjectId);
    this.writeD(this._boatObjectId);
    this.writeD(this._destination.x);
    this.writeD(this._destination.y);
    this.writeD(this._destination.z);
    this.writeD(this._origin.x);
    this.writeD(this._origin.y);
    this.writeD(this._origin.z);
  }
}
