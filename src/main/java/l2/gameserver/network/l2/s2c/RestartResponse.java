//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class RestartResponse extends L2GameServerPacket {
  public static final RestartResponse OK = new RestartResponse(1);
  public static final RestartResponse FAIL = new RestartResponse(0);
  private String _message = "bye";
  private int _param;

  public RestartResponse(int param) {
    this._param = param;
  }

  protected final void writeImpl() {
    this.writeC(95);
    this.writeD(this._param);
    this.writeS(this._message);
  }
}
