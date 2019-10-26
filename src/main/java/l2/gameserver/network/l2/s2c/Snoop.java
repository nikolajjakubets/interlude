//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class Snoop extends L2GameServerPacket {
  private int _convoID;
  private String _name;
  private int _type;
  private int _fStringId;
  private String _speaker;
  private String[] _params;

  public Snoop(int id, String name, int type, String speaker, String msg, int fStringId, String... params) {
    this._convoID = id;
    this._name = name;
    this._type = type;
    this._speaker = speaker;
    this._fStringId = fStringId;
    this._params = params;
  }

  protected final void writeImpl() {
    this.writeC(213);
    this.writeD(this._convoID);
    this.writeS(this._name);
    this.writeD(0);
    this.writeD(this._type);
    this.writeS(this._speaker);
    String[] var1 = this._params;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      String param = var1[var3];
      this.writeS(param);
    }

  }
}
