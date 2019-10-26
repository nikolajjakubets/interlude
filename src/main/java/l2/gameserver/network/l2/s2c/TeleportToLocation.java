//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.GameObject;
import l2.gameserver.utils.Location;

public class TeleportToLocation extends L2GameServerPacket {
  private int _targetId;
  private Location _loc;

  public TeleportToLocation(GameObject cha, Location loc) {
    this._targetId = cha.getObjectId();
    this._loc = loc;
  }

  public TeleportToLocation(GameObject cha, int x, int y, int z) {
    this._targetId = cha.getObjectId();
    this._loc = new Location(x, y, z, cha.getHeading());
  }

  protected final void writeImpl() {
    this.writeC(40);
    this.writeD(this._targetId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
  }
}
