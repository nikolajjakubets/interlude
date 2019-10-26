//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class ExSetCompassZoneCode extends L2GameServerPacket {
  public static final int ZONE_ALTERED = 8;
  public static final int ZONE_ALTERED2 = 9;
  public static final int ZONE_REMINDER = 10;
  public static final int ZONE_SIEGE = 11;
  public static final int ZONE_PEACE = 12;
  public static final int ZONE_SSQ = 13;
  public static final int ZONE_PVP = 14;
  public static final int ZONE_GENERAL_FIELD = 15;
  public static final int ZONE_PVP_FLAG = 16384;
  public static final int ZONE_ALTERED_FLAG = 256;
  public static final int ZONE_SIEGE_FLAG = 2048;
  public static final int ZONE_PEACE_FLAG = 4096;
  public static final int ZONE_SSQ_FLAG = 8192;
  private final int _zone;

  public ExSetCompassZoneCode(Player player) {
    this(player.getZoneMask());
  }

  public ExSetCompassZoneCode(int zoneMask) {
    if ((zoneMask & 256) == 256) {
      this._zone = 8;
    } else if ((zoneMask & 2048) == 2048) {
      this._zone = 11;
    } else if ((zoneMask & 16384) == 16384) {
      this._zone = 14;
    } else if ((zoneMask & 4096) == 4096) {
      this._zone = 12;
    } else if ((zoneMask & 8192) == 8192) {
      this._zone = 13;
    } else {
      this._zone = 15;
    }

  }

  protected final void writeImpl() {
    this.writeEx(50);
    this.writeD(this._zone);
  }
}
