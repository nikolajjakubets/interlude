//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.utils.Location;

public class GetOffVehicle extends L2GameServerPacket {
  private int _playerObjectId;
  private int _boatObjectId;
  private Location _loc;

  public GetOffVehicle(Player cha, Boat boat, Location loc) {
    this._playerObjectId = cha.getObjectId();
    this._boatObjectId = boat.getObjectId();
    this._loc = loc;
  }

  protected final void writeImpl() {
    this.writeC(93);
    this.writeD(this._playerObjectId);
    this.writeD(this._boatObjectId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
