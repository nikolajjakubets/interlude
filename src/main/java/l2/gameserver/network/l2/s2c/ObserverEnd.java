//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.utils.Location;

public class ObserverEnd extends L2GameServerPacket {
  private Location _loc;

  public ObserverEnd(Location loc) {
    this._loc = loc;
  }

  protected final void writeImpl() {
    this.writeC(224);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
  }
}
