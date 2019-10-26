//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectManaHealOverTime extends Effect {
  private final boolean _ignoreMpEff;

  public EffectManaHealOverTime(Env env, EffectTemplate template) {
    super(env, template);
    this._ignoreMpEff = template.getParam().getBool("ignoreMpEff", false);
  }

  public boolean onActionTime() {
    if (this._effected.isHealBlocked()) {
      return true;
    } else {
      double mp = this.calc();
      double newMp = mp * (!this._ignoreMpEff ? this._effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0D, this._effector, this.getSkill()) : 100.0D) / 100.0D;
      double addToMp = Math.max(0.0D, Math.min(newMp, this._effected.calcStat(Stats.MP_LIMIT, (Creature)null, (Skill)null) * (double)this._effected.getMaxMp() / 100.0D - this._effected.getCurrentMp()));
      if (addToMp > 0.0D) {
        this._effected.setCurrentMp(this._effected.getCurrentMp() + addToMp);
      }

      return true;
    }
  }
}
