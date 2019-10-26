//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.utils.Location;

public class ValidateLocation extends L2GameServerPacket {
  private int _chaObjId;
  private Location _loc;

  public ValidateLocation(Creature cha) {
    this._chaObjId = cha.getObjectId();
    this._loc = cha.getLoc();
  }

  protected final void writeImpl() {
    this.writeC(97);
    this.writeD(this._chaObjId);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._loc.h);
  }
}
