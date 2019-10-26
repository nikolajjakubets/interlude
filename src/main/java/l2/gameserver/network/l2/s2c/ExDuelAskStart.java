//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExDuelAskStart extends L2GameServerPacket {
  String _requestor;
  int _isPartyDuel;

  public ExDuelAskStart(String requestor, int isPartyDuel) {
    this._requestor = requestor;
    this._isPartyDuel = isPartyDuel;
  }

  protected final void writeImpl() {
    this.writeEx(75);
    this.writeS(this._requestor);
    this.writeD(this._isPartyDuel);
  }
}
