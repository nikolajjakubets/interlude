//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;

import java.util.List;

public class ClanGate extends Skill {
  public ClanGate(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (!activeChar.isPlayer()) {
      return false;
    } else {
      Player player = (Player)activeChar;
      Clan clan = player.getClan();
      if (clan != null && player.isClanLeader() && clan.getCastle() != 0) {
        SystemMessage msg = Call.canSummonHere(player);
        if (msg != null) {
          activeChar.sendPacket(msg);
          return false;
        } else {
          return super.checkCondition(activeChar, target, forceUse, dontMove, first);
        }
      } else {
        player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
        return false;
      }
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      Player player = (Player)activeChar;
      Clan clan = player.getClan();
      clan.broadcastToOtherOnlineMembers(Msg.COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED, player);
      this.getEffects(activeChar, activeChar, this.getActivateRate() > 0, true);
    }
  }
}
