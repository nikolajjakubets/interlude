//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;

public class RequestPledgeMemberList extends L2GameClientPacket {
  public RequestPledgeMemberList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan != null) {
        activeChar.sendPacket(clan.listAll());
      }

    }
  }
}
