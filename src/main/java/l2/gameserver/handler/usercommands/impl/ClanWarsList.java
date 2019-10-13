//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.usercommands.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.SystemMessage;
import org.apache.commons.lang3.ArrayUtils;

public class ClanWarsList implements IUserCommandHandler {
  private static final int ATTACKLIST_COMMAND_ID = 88;
  private static final int UNDERATTACKLIST_COMMAND_ID = 89;
  private static final int WAR_COMMAND_ID = 90;
  private static final int[] COMMAND_IDS = new int[]{88, 89, 90};

  public ClanWarsList() {
  }

  private static void sendAttackList(Player player, Clan clan) {
    player.sendPacket(Msg._ATTACK_LIST_);
    sendClans(player, clan.getEnemyClans());
  }

  private static void sendUnderAttackList(Player player, Clan clan) {
    player.sendPacket(Msg._UNDER_ATTACK_LIST_);
    sendClans(player, clan.getAttackerClans());
  }

  private static void sendWarList(Player player, Clan clan) {
    Set<Clan> clans = new LinkedHashSet(clan.getEnemyClans());
    clans.retainAll(clan.getAttackerClans());
    player.sendPacket(Msg._WAR_LIST_);
    sendClans(player, clans);
  }

  private static void sendClans(Player player, Collection<Clan> clans) {
    Iterator var2 = clans.iterator();

    while(var2.hasNext()) {
      Clan c = (Clan)var2.next();
      Alliance alliance = c.getAlliance();
      player.sendPacket(alliance != null ? (new SystemMessage(1200)).addString(c.getName()).addString(alliance.getAllyName()) : (new SystemMessage(1202)).addString(c.getName()));
    }

    player.sendPacket(Msg.__EQUALS__);
  }

  public boolean useUserCommand(int id, Player activeChar) {
    if (!ArrayUtils.contains(COMMAND_IDS, id)) {
      return false;
    } else {
      Clan clan = activeChar.getClan();
      if (clan == null) {
        activeChar.sendPacket(Msg.NOT_JOINED_IN_ANY_CLAN);
        return false;
      } else {
        switch(id) {
          case 88:
            sendAttackList(activeChar, clan);
            return true;
          case 89:
            sendUnderAttackList(activeChar, clan);
            return true;
          case 90:
            sendWarList(activeChar, clan);
            return true;
          default:
            return false;
        }
      }
    }
  }

  public int[] getUserCommandList() {
    return COMMAND_IDS;
  }
}
