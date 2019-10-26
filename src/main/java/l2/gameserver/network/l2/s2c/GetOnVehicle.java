//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.utils.Location;

public class GetOnVehicle extends L2GameServerPacket {
  private int _playerObjectId;
  private int _boatObjectId;
  private Location _loc;

  public GetOnVehicle(Player activeChar, Boat boat, Location loc) {
    this._loc = loc;
    this._playerObjectId = activeChar.getObjectId();
    this._boatObjectId = boat.getObjectId();
  }

  protected final void writeImpl() {
    this.writeC(92);
    this.writeD(this._playerObjectId);
    this.writeD(this._boatObjectId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
