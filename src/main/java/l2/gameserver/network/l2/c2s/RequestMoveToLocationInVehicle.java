//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.BoatHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.utils.Location;

public class RequestMoveToLocationInVehicle extends L2GameClientPacket {
  private Location _pos = new Location();
  private Location _originPos = new Location();
  private int _boatObjectId;

  public RequestMoveToLocationInVehicle() {
  }

  protected void readImpl() {
    this._boatObjectId = this.readD();
    this._pos.x = this.readD();
    this._pos.y = this.readD();
    this._pos.z = this.readD();
    this._originPos.x = this.readD();
    this._originPos.y = this.readD();
    this._originPos.z = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Boat boat = BoatHolder.getInstance().getBoat(this._boatObjectId);
      if (boat == null) {
        player.sendActionFailed();
      } else {
        boat.moveInBoat(player, this._originPos, this._pos);
      }
    }
  }
}
