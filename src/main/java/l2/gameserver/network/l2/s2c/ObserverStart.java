//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.utils.Location;

public class ObserverStart extends L2GameServerPacket {
  private Location _loc;

  public ObserverStart(Location loc) {
    this._loc = loc;
  }

  protected final void writeImpl() {
    this.writeC(223);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeC(0);
    this.writeC(192);
    this.writeC(0);
  }
}
