//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;

public class SetPrivateStoreWholeMsg extends L2GameClientPacket {
  private String _storename;

  public SetPrivateStoreWholeMsg() {
  }

  protected void readImpl() {
    this._storename = this.readS(32);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      activeChar.setSellStoreName(this._storename);
    }
  }
}
