//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.utils.Location;

public class FlyToLocation extends L2GameServerPacket {
  private int _chaObjId;
  private final FlyToLocation.FlyType _type;
  private Location _loc;
  private Location _destLoc;

  public FlyToLocation(Creature cha, Location destLoc, FlyToLocation.FlyType type) {
    this._destLoc = destLoc;
    this._type = type;
    this._chaObjId = cha.getObjectId();
    this._loc = cha.getLoc();
  }

  protected void writeImpl() {
    this.writeC(212);
    this.writeD(this._chaObjId);
    this.writeD(this._destLoc.x);
    this.writeD(this._destLoc.y);
    this.writeD(this._destLoc.z);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._type.ordinal());
  }

  public static enum FlyType {
    THROW_UP,
    THROW_HORIZONTAL,
    DUMMY,
    CHARGE,
    NONE;

    private FlyType() {
    }
  }
}
