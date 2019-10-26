//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExNeedToChangeName extends L2GameServerPacket {
  private int _type;
  private int _reason;
  private String _origName;

  public ExNeedToChangeName(int type, int reason, String origName) {
    this._type = type;
    this._reason = reason;
    this._origName = origName;
  }

  protected final void writeImpl() {
    this.writeEx(105);
    this.writeD(this._type);
    this.writeD(this._reason);
    this.writeS(this._origName);
  }
}
