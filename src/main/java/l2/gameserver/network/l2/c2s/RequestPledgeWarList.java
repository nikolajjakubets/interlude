//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PledgeReceiveWarList;

public class RequestPledgeWarList extends L2GameClientPacket {
  private int _type;
  private int _page;

  public RequestPledgeWarList() {
  }

  protected void readImpl() {
    this._page = this.readD();
    this._type = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan != null) {
        activeChar.sendPacket(new PledgeReceiveWarList(clan, this._type, this._page));
      }

    }
  }
}
