//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.tables.ClanTable;

public class RequestStopPledgeWar extends L2GameClientPacket {
  private String _pledgeName;

  public RequestStopPledgeWar() {
  }

  protected void readImpl() {
    this._pledgeName = this.readS(32);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Clan playerClan = activeChar.getClan();
      if (playerClan != null) {
        if ((activeChar.getClanPrivileges() & 32) != 32) {
          activeChar.sendPacket(new IStaticPacket[]{Msg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT, ActionFail.STATIC});
        } else {
          Clan clan = ClanTable.getInstance().getClanByName(this._pledgeName);
          if (clan == null) {
            activeChar.sendPacket(new IStaticPacket[]{SystemMsg.CLAN_NAME_IS_INVALID, ActionFail.STATIC});
          } else if (!playerClan.isAtWarWith(clan.getClanId())) {
            activeChar.sendPacket(new IStaticPacket[]{Msg.YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_TO_S1_CLAN, ActionFail.STATIC});
          } else {
            Iterator var4 = playerClan.iterator();

            UnitMember mbr;
            do {
              if (!var4.hasNext()) {
                ClanTable.getInstance().stopClanWar(playerClan, clan);
                return;
              }

              mbr = (UnitMember)var4.next();
            } while(!mbr.isOnline() || !mbr.getPlayer().isInCombat());

            activeChar.sendPacket(new IStaticPacket[]{Msg.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE, ActionFail.STATIC});
          }
        }
      }
    }
  }
}
