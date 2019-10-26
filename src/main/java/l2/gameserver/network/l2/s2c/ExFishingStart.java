//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.utils.Location;

public class ExFishingStart extends L2GameServerPacket {
  private int _charObjId;
  private Location _loc;
  private int _fishType;
  private boolean _isNightLure;

  public ExFishingStart(Creature character, int fishType, Location loc, boolean isNightLure) {
    this._charObjId = character.getObjectId();
    this._fishType = fishType;
    this._loc = loc;
    this._isNightLure = isNightLure;
  }

  protected final void writeImpl() {
    this.writeEx(19);
    this.writeD(this._charObjId);
    this.writeD(this._fishType);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeC(this._isNightLure ? 1 : 0);
    this.writeC(1);
  }
}
