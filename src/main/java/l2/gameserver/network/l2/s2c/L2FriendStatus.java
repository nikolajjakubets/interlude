//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class L2FriendStatus extends L2GameServerPacket {
  private String _charName;
  private boolean _login;

  public L2FriendStatus(Player player, boolean login) {
    this._login = login;
    this._charName = player.getName();
  }

  protected final void writeImpl() {
    this.writeC(252);
    this.writeD(this._login ? 1 : 0);
    this.writeS(this._charName);
    this.writeD(0);
  }
}
