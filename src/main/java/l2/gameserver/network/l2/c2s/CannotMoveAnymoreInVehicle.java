//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class CannotMoveAnymoreInVehicle extends L2GameClientPacket {
  private Location _loc = new Location();
  private int _boatid;

  public CannotMoveAnymoreInVehicle() {
  }

  protected void readImpl() {
    this._boatid = this.readD();
    this._loc.x = this.readD();
    this._loc.y = this.readD();
    this._loc.z = this.readD();
    this._loc.h = this.readD();
  }

  protected void runImpl() {
    Player player = ((GameClient)this.getClient()).getActiveChar();
    if (player != null) {
      Boat boat = player.getBoat();
      if (boat != null && boat.getObjectId() == this._boatid) {
        player.setInBoatPosition(this._loc);
        player.setHeading(this._loc.h);
        player.broadcastPacket(new L2GameServerPacket[]{boat.inStopMovePacket(player)});
      }

    }
  }
}
