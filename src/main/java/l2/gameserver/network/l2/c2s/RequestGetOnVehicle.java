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

public class RequestGetOnVehicle extends L2GameClientPacket {
  private int _objectId;
  private Location _loc = new Location();

  public RequestGetOnVehicle() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this._loc.x = this.readD();
    this._loc.y = this.readD();
    this._loc.z = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Boat boat = BoatHolder.getInstance().getBoat(this._objectId);
      if (boat != null) {
        player._stablePoint = boat.getCurrentWay().getReturnLoc();
        boat.addPlayer(player, this._loc);
      }
    }
  }
}
