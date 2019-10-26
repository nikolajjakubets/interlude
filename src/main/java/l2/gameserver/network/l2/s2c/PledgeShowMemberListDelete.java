//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class PledgeShowMemberListDelete extends L2GameServerPacket {
  private String _player;

  public PledgeShowMemberListDelete(String playerName) {
    this._player = playerName;
  }

  protected final void writeImpl() {
    this.writeC(86);
    this.writeS(this._player);
  }
}
