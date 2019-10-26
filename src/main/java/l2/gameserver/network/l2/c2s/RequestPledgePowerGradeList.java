//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.RankPrivs;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.PledgePowerGradeList;

public class RequestPledgePowerGradeList extends L2GameClientPacket {
  public RequestPledgePowerGradeList() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan != null) {
        RankPrivs[] privs = clan.getAllRankPrivs();
        activeChar.sendPacket(new PledgePowerGradeList(privs));
      }

    }
  }
}
