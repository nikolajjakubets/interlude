//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Effect.EEffectSlot;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class StealBuff extends Skill {
  private final int _stealCount;
  private final int _chanceMod;

  public StealBuff(StatsSet set) {
    super(set);
    this._stealCount = set.getInteger("StealCount", 1);
    this._chanceMod = set.getInteger("ChanceMod", 0);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (target != null && target.isPlayer()) {
      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    } else {
      activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    label102:
    while(true) {
      Creature target;
      do {
        if (!var3.hasNext()) {
          if (this.isSSPossible()) {
            activeChar.unChargeShots(this.isMagic());
          }

          return;
        }

        target = (Creature)var3.next();
      } while(target == null);

      double res_mul = 1.0D - target.calcStat(Stats.CANCEL_RESIST, 0.0D, (Creature)null, (Skill)null) * 0.01D;
      Effect[] effects = target.getEffectList().getAllFirstEffects();
      LinkedList<Effect> eset = new LinkedList();
      EEffectSlot[] var9 = EEffectSlot.VALUES;
      int var10 = var9.length;

      int var11;
      Effect leff;
      for(var11 = 0; var11 < var10; ++var11) {
        EEffectSlot ees = var9[var11];
        Effect[] var13 = effects;
        int var14 = effects.length;

        for(int var15 = 0; var15 < var14; ++var15) {
          leff = var13[var15];
          if (leff != null && !leff.getTemplate()._applyOnCaster && leff.getEffectSlot() == ees) {
            Skill skill = leff.getSkill();
            if (skill.isCancelable() && skill.isActive() && !skill.isOffensive() && !skill.isToggle() && !skill.isTrigger()) {
              eset.add(leff);
            }
          }
        }
      }

      boolean update = false;
      Iterator<Effect> it = eset.descendingIterator();
      var11 = 0;

      while(true) {
        Effect effect;
        do {
          if (!it.hasNext() || var11++ >= this._stealCount) {
            if (update) {
              target.sendChanges();
              target.updateEffectIcons();
              activeChar.sendChanges();
              activeChar.updateEffectIcons();
            }

            this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
            continue label102;
          }

          effect = (Effect)it.next();
        } while(!calcSkillCancel(this, effect, this._chanceMod, res_mul, true));

        Skill skill = effect.getSkill();
        Iterator var22 = target.getEffectList().getEffectsBySkill(skill).iterator();

        while(var22.hasNext()) {
          Effect ceff = (Effect)var22.next();
          if (ceff != null) {
            leff = ceff.getTemplate().getEffect(new Env(activeChar, activeChar, skill));
            leff.setCount(ceff.getCount());
            if (ceff.getCount() == 1) {
              leff.setPeriod(ceff.getPeriod() - ceff.getTime());
            } else {
              leff.setPeriod(ceff.getPeriod());
            }

            update = true;
            ceff.exit();
            activeChar.getEffectList().addEffect(leff);
          }
        }

        target.sendPacket((new SystemMessage(92)).addSkillName(skill.getId(), skill.getLevel()));
      }
    }
  }

  public static boolean calcSkillCancel(Skill cancel, Effect effect, int chance_mod, double res_mul, boolean chance_restrict) {
    int dml = Math.max(0, cancel.getMagicLevel() - effect.getSkill().getMagicLevel());
    int chance = (int)((double)((long)(2 * dml + chance_mod) + effect.getPeriod() * (long)effect.getCount() / 120000L) * res_mul);
    return Rnd.chance(Math.max(Config.SKILLS_DISPEL_MOD_MIN, Math.min(Config.SKILLS_DISPEL_MOD_MAX, chance)));
  }
}
