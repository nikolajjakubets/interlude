//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class SendTradeDone extends L2GameServerPacket {
  public static final L2GameServerPacket SUCCESS = new SendTradeDone(1);
  public static final L2GameServerPacket FAIL = new SendTradeDone(0);
  private int _response;

  private SendTradeDone(int num) {
    this._response = num;
  }

  protected final void writeImpl() {
    this.writeC(34);
    this.writeD(this._response);
  }
}
