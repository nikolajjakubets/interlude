//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExEventMatchMessage extends L2GameServerPacket {
  public static final ExEventMatchMessage FINISH = new ExEventMatchMessage(1);
  public static final ExEventMatchMessage START = new ExEventMatchMessage(2);
  public static final ExEventMatchMessage GAMEOVER = new ExEventMatchMessage(3);
  public static final ExEventMatchMessage COUNT1 = new ExEventMatchMessage(4);
  public static final ExEventMatchMessage COUNT2 = new ExEventMatchMessage(5);
  public static final ExEventMatchMessage COUNT3 = new ExEventMatchMessage(6);
  public static final ExEventMatchMessage COUNT4 = new ExEventMatchMessage(7);
  public static final ExEventMatchMessage COUNT5 = new ExEventMatchMessage(8);
  private int _type;
  private String _message;

  public ExEventMatchMessage(int type) {
    this._type = type;
    this._message = "";
  }

  public ExEventMatchMessage(String message) {
    this._type = 0;
    this._message = message;
  }

  protected void writeImpl() {
    this.writeEx(4);
    this.writeC(this._type);
    this.writeS(this._message);
  }
}
