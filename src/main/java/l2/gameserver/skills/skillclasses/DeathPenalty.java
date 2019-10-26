//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class DeathPenalty extends Skill {
  public DeathPenalty(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (activeChar.getKarma() > 0 && !Config.ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY) {
      activeChar.sendActionFailed();
      return false;
    } else {
      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null && target.isPlayer()) {
        ((Player)target).getDeathPenalty().reduceLevel();
      }
    }

  }
}
