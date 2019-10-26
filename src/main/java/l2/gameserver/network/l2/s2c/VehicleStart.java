//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.boat.Boat;

public class VehicleStart extends L2GameServerPacket {
  private int _objectId;
  private int _state;

  public VehicleStart(Boat boat) {
    this._objectId = boat.getObjectId();
    this._state = boat.getRunState();
  }

  protected void writeImpl() {
    this.writeC(186);
    this.writeD(this._objectId);
    this.writeD(this._state);
  }
}
