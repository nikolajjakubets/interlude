//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.utils.Location;

public class RadarControl extends L2GameServerPacket {
  private int _x;
  private int _y;
  private int _z;
  private int _type;
  private int _showRadar;

  public RadarControl(int showRadar, int type, Location loc) {
    this(showRadar, type, loc.x, loc.y, loc.z);
  }

  public RadarControl(int showRadar, int type, int x, int y, int z) {
    this._showRadar = showRadar;
    this._type = type;
    this._x = x;
    this._y = y;
    this._z = z;
  }

  protected final void writeImpl() {
    this.writeC(235);
    this.writeD(this._showRadar);
    this.writeD(this._type);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
  }
}
