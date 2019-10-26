//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class Balance extends Skill {
  public Balance(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    double summaryCurrentHp = 0.0D;
    int summaryMaximumHp = 0;
    Iterator var6 = targets.iterator();

    while(var6.hasNext()) {
      Creature target = (Creature)var6.next();
      if (target != null && !target.isAlikeDead()) {
        summaryCurrentHp += target.getCurrentHp();
        summaryMaximumHp += target.getMaxHp();
      }
    }

    double percent = summaryCurrentHp / (double)summaryMaximumHp;
    Iterator var8 = targets.iterator();

    while(var8.hasNext()) {
      Creature target = (Creature)var8.next();
      if (target != null && !target.isAlikeDead()) {
        double hp = (double)target.getMaxHp() * percent;
        if (hp > target.getCurrentHp()) {
          double limit = target.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)target.getMaxHp() / 100.0D;
          if (target.getCurrentHp() < limit) {
            target.setCurrentHp(Math.min(hp, limit), false);
          }
        } else {
          target.setCurrentHp(Math.max(1.01D, hp), false);
        }

        this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
