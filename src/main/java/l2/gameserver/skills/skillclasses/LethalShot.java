//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Formulas.AttackInfo;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class LethalShot extends Skill {
  public LethalShot(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    boolean ss = activeChar.getChargedSoulShot() && this.isSSPossible();
    if (ss) {
      activeChar.unChargeShots(false);
    }

    Iterator var6 = targets.iterator();

    while(var6.hasNext()) {
      Creature target = (Creature)var6.next();
      if (target != null && !target.isDead()) {
        boolean reflected = target.checkReflectSkill(activeChar, this);
        Creature realTarget = reflected ? activeChar : target;
        if (this.getPower() > 0.0D) {
          AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, ss, false);
          if (info.lethal_dmg > 0.0D) {
            realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
          }

          realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true);
          if (!reflected) {
            realTarget.doCounterAttack(this, activeChar, false);
          }
        }

        this.getEffects(activeChar, target, this.getActivateRate() > 0, false, reflected);
      }
    }

  }
}
