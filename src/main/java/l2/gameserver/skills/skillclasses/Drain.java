//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Formulas.AttackInfo;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class Drain extends Skill {
  private double _absorbAbs;

  public Drain(StatsSet set) {
    super(set);
    this._absorbAbs = set.getDouble("absorbAbs", 0.0D);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    int sps = this.isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
    boolean ss = this.isSSPossible() && activeChar.getChargedSoulShot();
    boolean corpseSkill = this._targetType == SkillTargetType.TARGET_CORPSE;
    Iterator var8 = targets.iterator();

    while(true) {
      boolean reflected;
      Creature target;
      while(true) {
        do {
          if (!var8.hasNext()) {
            if (this.isMagic()) {
              if (sps == 0) {
                return;
              }
            } else if (!ss) {
              return;
            }

            activeChar.unChargeShots(this.isMagic());
            return;
          }

          target = (Creature)var8.next();
        } while(target == null);

        reflected = !corpseSkill && target.checkReflectSkill(activeChar, this);
        Creature realTarget = reflected ? activeChar : target;
        if (this.getPower() <= 0.0D && this._absorbAbs <= 0.0D) {
          break;
        }

        if (!realTarget.isDead() || corpseSkill) {
          double hp = 0.0D;
          double targetHp = realTarget.getCurrentHp();
          double addToHp;
          if (!corpseSkill) {
            if (this.isMagic()) {
              addToHp = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
            } else {
              AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, ss, false);
              addToHp = info.damage;
              if (info.lethal_dmg > 0.0D) {
                realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
              }
            }

            double targetCP = realTarget.getCurrentCp();
            if (addToHp > targetCP || !realTarget.isPlayer()) {
              hp = (addToHp - targetCP) * this._absorbPart;
            }

            realTarget.reduceCurrentHp(addToHp, activeChar, this, true, true, false, true, false, false, true);
            if (!reflected) {
              realTarget.doCounterAttack(this, activeChar, false);
            }
          }

          if (this._absorbAbs != 0.0D || this._absorbPart != 0.0D) {
            hp += this._absorbAbs;
            if (hp > targetHp && !corpseSkill) {
              hp = targetHp;
            }

            addToHp = Math.max(0.0D, Math.min(hp, activeChar.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)activeChar.getMaxHp() / 100.0D - activeChar.getCurrentHp()));
            if (addToHp > 0.0D && !activeChar.isHealBlocked()) {
              activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);
            }

            if (realTarget.isDead() && corpseSkill && realTarget.isNpc()) {
              activeChar.getAI().setAttackTarget((Creature)null);
              ((NpcInstance)realTarget).endDecayTask();
            }
            break;
          }
        }
      }

      this.getEffects(activeChar, target, this.getActivateRate() > 0, false, reflected);
    }
  }
}
