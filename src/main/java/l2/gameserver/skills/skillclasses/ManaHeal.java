//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class ManaHeal extends Skill {
  private final boolean _ignoreMpEff;

  public ManaHeal(StatsSet set) {
    super(set);
    this._ignoreMpEff = set.getBool("ignoreMpEff", false);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    double mp = this._power;
    int sps = this.isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
    if (sps > 0 && Config.MANAHEAL_SPS_BONUS) {
      mp *= sps == 2 ? 1.5D : 1.3D;
    }

    Iterator var6 = targets.iterator();

    while(var6.hasNext()) {
      Creature target = (Creature)var6.next();
      if (!target.isHealBlocked()) {
        double newMp = activeChar == target ? mp : Math.min(mp * 1.7D, mp * (!this._ignoreMpEff ? target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0D, activeChar, this) : 100.0D) / 100.0D);
        if (this.getMagicLevel() > 0 && activeChar != target) {
          int diff = target.getLevel() - this.getMagicLevel();
          if (diff > 5) {
            if (diff < 20) {
              newMp = newMp / 100.0D * (double)(100 - diff * 5);
            } else {
              newMp = 0.0D;
            }
          }
        }

        if (newMp == 0.0D) {
          activeChar.sendPacket((new SystemMessage(1597)).addSkillName(this._id, this.getDisplayLevel()));
          this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        } else {
          double addToMp = Math.max(0.0D, Math.min(newMp, target.calcStat(Stats.MP_LIMIT, (Creature)null, (Skill)null) * (double)target.getMaxMp() / 100.0D - target.getCurrentMp()));
          if (addToMp > 0.0D) {
            target.setCurrentMp(addToMp + target.getCurrentMp());
          }

          if (target.isPlayer()) {
            if (activeChar != target) {
              target.sendPacket((new SystemMessage(1069)).addString(activeChar.getName()).addNumber(Math.round(addToMp)));
            } else {
              activeChar.sendPacket((new SystemMessage(1068)).addNumber(Math.round(addToMp)));
            }
          }

          this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        }
      }
    }

    if (this.isSSPossible()) {
      activeChar.unChargeShots(this.isMagic());
    }

  }
}
