//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class AskJoinParty extends L2GameServerPacket {
  private String _requestorName;
  private int _itemDistribution;

  public AskJoinParty(String requestorName, int itemDistribution) {
    this._requestorName = requestorName;
    this._itemDistribution = itemDistribution;
  }

  protected final void writeImpl() {
    this.writeC(57);
    this.writeS(this._requestorName);
    this.writeD(this._itemDistribution);
  }
}
