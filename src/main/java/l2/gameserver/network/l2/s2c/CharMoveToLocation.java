//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;

public class CharMoveToLocation extends L2GameServerPacket {
  private int _objectId;
  private int _client_z_shift;
  private Location _current;
  private Location _destination;

  public CharMoveToLocation(Creature cha) {
    this(cha, cha.getLoc(), cha.getDestination());
  }

  public CharMoveToLocation(Creature cha, Location from, Location to) {
    this._objectId = cha.getObjectId();
    this._current = from;
    this._destination = to;
    if (!cha.isFlying()) {
      this._client_z_shift = Config.CLIENT_Z_SHIFT;
    }

    if (cha.isInWater()) {
      this._client_z_shift += Config.CLIENT_Z_SHIFT;
    }

    if (this._destination == null) {
      Log.debug("CharMoveToLocation: desc is null, but moving. L2Character: " + cha.getObjectId() + ":" + cha.getName() + "; Loc: " + this._current);
      this._destination = this._current;
    }

  }

  public CharMoveToLocation(int objectId, Location from, Location to) {
    this._objectId = objectId;
    this._current = from;
    this._destination = to;
  }

  protected final void writeImpl() {
    this.writeC(1);
    this.writeD(this._objectId);
    this.writeD(this._destination.x);
    this.writeD(this._destination.y);
    this.writeD(this._destination.z + this._client_z_shift);
    this.writeD(this._current.x);
    this.writeD(this._current.y);
    this.writeD(this._current.z + this._client_z_shift);
  }
}
