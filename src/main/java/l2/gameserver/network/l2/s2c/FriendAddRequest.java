//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class FriendAddRequest extends L2GameServerPacket {
  private String _requestorName;

  public FriendAddRequest(String requestorName) {
    this._requestorName = requestorName;
  }

  protected final void writeImpl() {
    this.writeC(125);
    this.writeS(this._requestorName);
    this.writeD(0);
  }
}
