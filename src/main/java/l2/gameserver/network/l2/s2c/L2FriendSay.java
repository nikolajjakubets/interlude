//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class L2FriendSay extends L2GameServerPacket {
  private String _sender;
  private String _receiver;
  private String _message;

  public L2FriendSay(String sender, String reciever, String message) {
    this._sender = sender;
    this._receiver = reciever;
    this._message = message;
  }

  protected final void writeImpl() {
    this.writeC(253);
    this.writeD(0);
    this.writeS(this._receiver);
    this.writeS(this._sender);
    this.writeS(this._message);
  }
}
