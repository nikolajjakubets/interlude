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

public class ManaHealPercent extends Skill {
  private final boolean _ignoreMpEff;

  public ManaHealPercent(StatsSet set) {
    super(set);
    this._ignoreMpEff = set.getBool("ignoreMpEff", true);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null && !target.isDead() && !target.isHealBlocked()) {
        this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        double mp = this._power * (double)target.getMaxMp() / 100.0D;
        double newMp = mp * (!this._ignoreMpEff ? target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0D, activeChar, this) : 100.0D) / 100.0D;
        double addToMp = Math.max(0.0D, Math.min(newMp, target.calcStat(Stats.MP_LIMIT, (Creature)null, (Skill)null) * (double)target.getMaxMp() / 100.0D - target.getCurrentMp()));
        if (addToMp > 0.0D) {
          target.setCurrentMp(target.getCurrentMp() + addToMp);
        }

        if (target.isPlayer()) {
          if (activeChar != target) {
            target.sendPacket((new SystemMessage(1069)).addString(activeChar.getName()).addNumber(Math.round(addToMp)));
          } else {
            activeChar.sendPacket((new SystemMessage(1068)).addNumber(Math.round(addToMp)));
          }
        }
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
