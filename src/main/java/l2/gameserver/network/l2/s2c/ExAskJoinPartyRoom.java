//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExAskJoinPartyRoom extends L2GameServerPacket {
  private String _charName;
  private String _roomName;

  public ExAskJoinPartyRoom(String charName, String roomName) {
    this._charName = charName;
    this._roomName = roomName;
  }

  protected final void writeImpl() {
    this.writeEx(52);
    this.writeS(this._charName);
  }
}
