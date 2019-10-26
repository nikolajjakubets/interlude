//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PledgeInfo;
import l2.gameserver.tables.ClanTable;

public class RequestPledgeInfo extends L2GameClientPacket {
  private int _clanId;

  public RequestPledgeInfo() {
  }

  protected void readImpl() {
    this._clanId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._clanId < 10000000) {
        activeChar.sendActionFailed();
      } else {
        Clan clan = ClanTable.getInstance().getClan(this._clanId);
        if (clan == null) {
          activeChar.sendActionFailed();
        } else {
          activeChar.sendPacket(new PledgeInfo(clan));
        }
      }
    }
  }
}
