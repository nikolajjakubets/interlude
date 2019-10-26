//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.utils.Location;

public class PlaySound extends L2GameServerPacket {
  public static final L2GameServerPacket SIEGE_VICTORY = new PlaySound("Siege_Victory");
  public static final L2GameServerPacket B04_S01 = new PlaySound("B04_S01");
  public static final L2GameServerPacket HB01;
  private PlaySound.Type _type;
  private String _soundFile;
  private int _hasCenterObject;
  private int _objectId;
  private int _x;
  private int _y;
  private int _z;

  public PlaySound(String soundFile) {
    this(PlaySound.Type.SOUND, soundFile, 0, 0, 0, 0, 0);
  }

  public PlaySound(PlaySound.Type type, String soundFile, int c, int objectId, Location loc) {
    this(type, soundFile, c, objectId, loc == null ? 0 : loc.x, loc == null ? 0 : loc.y, loc == null ? 0 : loc.z);
  }

  public PlaySound(PlaySound.Type type, String soundFile, int c, int objectId, int x, int y, int z) {
    this._type = type;
    this._soundFile = soundFile;
    this._hasCenterObject = c;
    this._objectId = objectId;
    this._x = x;
    this._y = y;
    this._z = z;
  }

  protected final void writeImpl() {
    this.writeC(152);
    this.writeD(this._type.ordinal());
    this.writeS(this._soundFile);
    this.writeD(this._hasCenterObject);
    this.writeD(this._objectId);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
  }

  static {
    HB01 = new PlaySound(PlaySound.Type.MUSIC, "HB01", 0, 0, 0, 0, 0);
  }

  public static enum Type {
    SOUND,
    MUSIC,
    VOICE;

    private Type() {
    }
  }
}
