//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.tables.ClanTable;

public class RequestDismissAlly extends L2GameClientPacket {
  public RequestDismissAlly() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan clan = activeChar.getClan();
      if (clan == null) {
        activeChar.sendActionFailed();
      } else {
        Alliance alliance = clan.getAlliance();
        if (alliance == null) {
          activeChar.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
        } else if (!activeChar.isAllyLeader()) {
          activeChar.sendPacket(Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
        } else if (alliance.getMembersCount() > 1) {
          activeChar.sendPacket(Msg.YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE);
        } else {
          ClanTable.getInstance().dissolveAlly(activeChar);
        }
      }
    }
  }
}
