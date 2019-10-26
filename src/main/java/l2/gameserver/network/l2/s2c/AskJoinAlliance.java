//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class AskJoinAlliance extends L2GameServerPacket {
  private String _requestorName;
  private String _requestorAllyName;
  private int _requestorId;

  public AskJoinAlliance(int requestorId, String requestorName, String requestorAllyName) {
    this._requestorName = requestorName;
    this._requestorAllyName = requestorAllyName;
    this._requestorId = requestorId;
  }

  protected final void writeImpl() {
    this.writeC(168);
    this.writeD(this._requestorId);
    this.writeS(this._requestorName);
    this.writeS("");
    this.writeS(this._requestorAllyName);
  }
}
