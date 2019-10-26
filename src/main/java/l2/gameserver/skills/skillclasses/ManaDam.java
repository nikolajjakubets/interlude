//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class ManaDam extends Skill {
  public ManaDam(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    int sps = 0;
    if (this.isSSPossible()) {
      sps = activeChar.getChargedSpiritShot();
    }

    Iterator var4 = targets.iterator();

    while(var4.hasNext()) {
      Creature target = (Creature)var4.next();
      if (target != null && !target.isDead()) {
        int magicLevel = this.getMagicLevel() == 0 ? activeChar.getLevel() : this.getMagicLevel();
        int landRate = Rnd.get(30, 100);
        landRate *= target.getLevel();
        landRate /= magicLevel;
        if (Rnd.chance(landRate)) {
          double mAtk = (double)activeChar.getMAtk(target, this);
          if (sps == 2) {
            mAtk *= 4.0D;
          } else if (sps == 1) {
            mAtk *= 2.0D;
          }

          double mDef = (double)target.getMDef(activeChar, this);
          if (mDef < 1.0D) {
            mDef = 1.0D;
          }

          double damage = Math.sqrt(mAtk) * this.getPower() * (double)(target.getMaxMp() / 97) / mDef;
          if (Config.MDAM_CRIT_POSSIBLE) {
            boolean crit = Formulas.calcMCrit(activeChar, target, activeChar.getMagicCriticalRate(target, this));
            if (crit) {
              activeChar.sendPacket(Msg.MAGIC_CRITICAL_HIT);
              damage *= activeChar.calcStat(Stats.MCRITICAL_DAMAGE, 4.0D, target, this);
            }
          }

          target.reduceCurrentMp(damage, activeChar, true);
        } else {
          SystemMessage msg = (new SystemMessage(159)).addName(target);
          activeChar.sendPacket(msg);
          target.sendPacket(msg);
          target.reduceCurrentHp(1.0D, activeChar, this, true, true, false, true, false, false, true);
        }

        this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
