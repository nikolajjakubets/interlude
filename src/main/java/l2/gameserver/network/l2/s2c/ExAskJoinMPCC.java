//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExAskJoinMPCC extends L2GameServerPacket {
  private String _requestorName;

  public ExAskJoinMPCC(String requestorName) {
    this._requestorName = requestorName;
  }

  protected void writeImpl() {
    this.writeEx(39);
    this.writeS(this._requestorName);
  }
}
