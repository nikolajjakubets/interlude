//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class RequestShortCutDel extends L2GameClientPacket {
  private int _slot;
  private int _page;

  public RequestShortCutDel() {
  }

  protected void readImpl() {
    int id = this.readD();
    this._slot = id % 12;
    this._page = id / 12;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.deleteShortCut(this._slot, this._page);
    }
  }
}
