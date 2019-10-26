//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.utils.Location;

public class Earthquake extends L2GameServerPacket {
  private Location _loc;
  private int _intensity;
  private int _duration;

  public Earthquake(Location loc, int intensity, int duration) {
    this._loc = loc;
    this._intensity = intensity;
    this._duration = duration;
  }

  protected final void writeImpl() {
    this.writeC(196);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._intensity);
    this.writeD(this._duration);
    this.writeD(0);
  }
}
