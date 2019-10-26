//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class AskJoinPledge extends L2GameServerPacket {
  private int _requestorId;
  private String _pledgeName;

  public AskJoinPledge(int requestorId, String pledgeName) {
    this._requestorId = requestorId;
    this._pledgeName = pledgeName;
  }

  protected final void writeImpl() {
    this.writeC(50);
    this.writeD(this._requestorId);
    this.writeS(this._pledgeName);
  }
}
