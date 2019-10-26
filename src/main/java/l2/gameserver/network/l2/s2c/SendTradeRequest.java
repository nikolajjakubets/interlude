//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class SendTradeRequest extends L2GameServerPacket {
  private int _senderId;

  public SendTradeRequest(int senderId) {
    this._senderId = senderId;
  }

  protected final void writeImpl() {
    this.writeC(94);
    this.writeD(this._senderId);
  }
}
