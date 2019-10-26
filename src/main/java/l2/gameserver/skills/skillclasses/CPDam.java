//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class CPDam extends Skill {
  public CPDam(StatsSet set) {
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
        target.doCounterAttack(this, activeChar, false);
        boolean reflected = target.checkReflectSkill(activeChar, this);
        Creature realTarget = reflected ? activeChar : target;
        if (!realTarget.isCurrentCpZero()) {
          double damage = this._power * realTarget.getCurrentCp();
          if (damage < 1.0D) {
            damage = 1.0D;
          }

          realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
          this.getEffects(activeChar, target, this.getActivateRate() > 0, false, reflected);
        }
      }
    }

  }
}
