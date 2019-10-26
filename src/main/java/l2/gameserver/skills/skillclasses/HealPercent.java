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

public class HealPercent extends Skill {
  public HealPercent(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null && !target.isHealBlocked()) {
        this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        double hp = this._power * (double)target.getMaxHp() / 100.0D;
        double addToHp = Math.max(0.0D, Math.min(hp, target.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)target.getMaxHp() / 100.0D - target.getCurrentHp()));
        if (addToHp > 0.0D) {
          target.setCurrentHp(addToHp + target.getCurrentHp(), false);
        }

        if (target.isPlayer()) {
          if (activeChar != target) {
            target.sendPacket((new SystemMessage(1067)).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
          } else {
            activeChar.sendPacket((new SystemMessage(1066)).addNumber(Math.round(addToHp)));
          }
        }
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
