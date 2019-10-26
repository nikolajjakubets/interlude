//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class CombatPointHeal extends Skill {
  private final boolean _ignoreCpEff;

  public CombatPointHeal(StatsSet set) {
    super(set);
    this._ignoreCpEff = set.getBool("ignoreCpEff", false);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null && !target.isDead() && !target.isHealBlocked()) {
        double maxNewCp = this._power * (!this._ignoreCpEff ? target.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100.0D, activeChar, this) : 100.0D) / 100.0D;
        double addToCp = Math.max(0.0D, Math.min(maxNewCp, target.calcStat(Stats.CP_LIMIT, (Creature)null, (Skill)null) * (double)target.getMaxCp() / 100.0D - target.getCurrentCp()));
        if (addToCp > 0.0D) {
          target.setCurrentCp(addToCp + target.getCurrentCp());
        }

        target.sendPacket((new SystemMessage(1405)).addNumber((long)addToCp));
        this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
