//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.HennaUnequipList;

public class RequestHennaUnequipList extends L2GameClientPacket {
  private int _symbolId;

  public RequestHennaUnequipList() {
  }

  protected void readImpl() {
    this._symbolId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      HennaUnequipList he = new HennaUnequipList(activeChar);
      activeChar.sendPacket(he);
    }
  }
}
