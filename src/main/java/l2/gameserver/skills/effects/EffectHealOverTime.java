//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.ExRegenMax;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectHealOverTime extends Effect {
  private final boolean _ignoreHpEff;

  public EffectHealOverTime(Env env, EffectTemplate template) {
    super(env, template);
    this._ignoreHpEff = template.getParam().getBool("ignoreHpEff", false);
  }

  public void onStart() {
    super.onStart();
    if (this.getEffected().isPlayer() && this.getCount() > 0 && this.getPeriod() > 0L) {
      this.getEffected().sendPacket(new ExRegenMax(this.calc(), (int)((long)this.getCount() * this.getPeriod() / 1000L), Math.round((float)(this.getPeriod() / 1000L))));
    }

  }

  public boolean onActionTime() {
    if (this._effected.isHealBlocked()) {
      return true;
    } else {
      double hp = this.calc();
      double newHp = hp * (!this._ignoreHpEff ? this._effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100.0D, this._effector, this.getSkill()) : 100.0D) / 100.0D;
      double addToHp = Math.max(0.0D, Math.min(newHp, this._effected.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)this._effected.getMaxHp() / 100.0D - this._effected.getCurrentHp()));
      if (addToHp > 0.0D) {
        this.getEffected().setCurrentHp(this._effected.getCurrentHp() + addToHp, false);
      }

      return true;
    }
  }
}
