//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.residences.SiegeFlagInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class Heal extends Skill {
  private final boolean _ignoreHpEff;
  private final boolean _staticPower;

  public Heal(StatsSet set) {
    super(set);
    this._ignoreHpEff = set.getBool("ignoreHpEff", false);
    this._staticPower = set.getBool("staticPower", this.isHandler());
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    return target != null && !target.isDoor() && !(target instanceof SiegeFlagInstance) ? super.checkCondition(activeChar, target, forceUse, dontMove, first) : false;
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    double hp = this.getPower();
    if (!this._staticPower) {
      int mAtk = activeChar.getMAtk((Creature)null, this);
      int mAtkMod = 1;
      int staticBonus = 0;
      if (this.isSSPossible()) {
        switch(activeChar.getChargedSpiritShot()) {
          case 1:
            mAtkMod = 2;
            staticBonus = this.getStaticBonus(mAtk) / 2;
            break;
          case 2:
            mAtkMod = 4;
            staticBonus = this.getStaticBonus(mAtk);
        }
      }

      hp += Math.sqrt((double)(mAtkMod * mAtk)) + (double)staticBonus;
      if (Config.HEAL_CRIT_POSSIBLE && Formulas.calcMCrit(activeChar, (Creature)null, 4.5D)) {
        hp *= 3.0D;
      }
    }

    Iterator var9 = targets.iterator();

    while(true) {
      Creature target;
      do {
        do {
          do {
            if (!var9.hasNext()) {
              if (this.isSSPossible() && this.isMagic()) {
                activeChar.unChargeShots(this.isMagic());
              }

              return;
            }

            target = (Creature)var9.next();
          } while(target == null);
        } while(target.isHealBlocked());
      } while(target != activeChar && (target.isPlayer() && target.isCursedWeaponEquipped() || activeChar.isPlayer() && activeChar.isCursedWeaponEquipped()));

      double addToHp;
      if (this._staticPower) {
        addToHp = this._power;
      } else {
        addToHp = hp;
        if (!this.isHandler()) {
          addToHp = hp + activeChar.calcStat(Stats.HEAL_POWER, activeChar, this);
          addToHp *= (!this._ignoreHpEff ? target.calcStat(Stats.HEAL_EFFECTIVNESS, 100.0D, activeChar, this) : 100.0D) / 100.0D;
        }
      }

      addToHp = Math.max(0.0D, Math.min(addToHp, target.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)target.getMaxHp() / 100.0D - target.getCurrentHp()));
      if (addToHp > 0.0D) {
        target.setCurrentHp(addToHp + target.getCurrentHp(), false);
      }

      if (target.isPlayer()) {
        if (this.getId() == 4051) {
          target.sendPacket(Msg.REJUVENATING_HP);
        } else if (activeChar == target) {
          activeChar.sendPacket((new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED)).addNumber(Math.round(addToHp)));
        } else {
          target.sendPacket((new SystemMessage(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1)).addName(activeChar).addNumber(Math.round(addToHp)));
        }
      }

      this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
    }
  }

  private final int getStaticBonus(int mAtk) {
    double power = this.getPower();
    double bottom = this.getPower() / 4.0D;
    if ((double)mAtk < bottom) {
      return 0;
    } else {
      double top = this.getPower() / 3.1D;
      if ((double)mAtk > this.getPower()) {
        return (int)top;
      } else {
        mAtk = (int)((double)mAtk - bottom);
        return (int)(top * ((double)mAtk / (power - bottom)));
      }
    }
  }
}
