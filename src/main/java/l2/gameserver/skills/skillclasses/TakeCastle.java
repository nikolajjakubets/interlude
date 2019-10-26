//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;

public class TakeCastle extends Skill {
  public TakeCastle(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (!super.checkCondition(activeChar, target, forceUse, dontMove, first)) {
      return false;
    } else if (activeChar != null && activeChar.isPlayer()) {
      Player player = (Player)activeChar;
      if (player.getClan() != null && player.isClanLeader()) {
        CastleSiegeEvent siegeEvent = (CastleSiegeEvent)player.getEvent(CastleSiegeEvent.class);
        if (siegeEvent == null) {
          activeChar.sendPacket((new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
          return false;
        } else if (siegeEvent.getSiegeClan("attackers", player.getClan()) == null) {
          activeChar.sendPacket((new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
          return false;
        } else if (player.isMounted()) {
          activeChar.sendPacket((new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
          return false;
        } else if (!player.isInRangeZ(target, 185L)) {
          player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
          return false;
        } else if (((Castle)siegeEvent.getResidence()).getZone().checkIfInZone(activeChar) && ((Castle)siegeEvent.getResidence()).getZone().checkIfInZone(target)) {
          if (first) {
            siegeEvent.broadcastTo(SystemMsg.THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT, new String[]{"defenders"});
          }

          return true;
        } else {
          activeChar.sendPacket((new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
          return false;
        }
      } else {
        activeChar.sendPacket((new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
        return false;
      }
    } else {
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null && target.isArtefact()) {
        Player player = (Player)activeChar;
        CastleSiegeEvent siegeEvent = (CastleSiegeEvent)player.getEvent(CastleSiegeEvent.class);
        if (siegeEvent != null) {
          siegeEvent.broadcastTo((new SystemMessage2(SystemMsg.CLAN_S1_HAS_SUCCESSFULLY_ENGRAVED_THE_HOLY_ARTIFACT)).addString(player.getClan().getName()), new String[]{"attackers", "defenders"});
          siegeEvent.processStep(player.getClan());
        }
      }
    }

    this.getEffects(activeChar, activeChar, this.getActivateRate() > 0, false);
  }
}
