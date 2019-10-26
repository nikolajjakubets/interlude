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

public class EffectCombatPointHealOverTime extends Effect {
  public EffectCombatPointHealOverTime(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean onActionTime() {
    if (this._effected.isHealBlocked()) {
      return true;
    } else {
      double addToCp = Math.max(0.0D, Math.min(this.calc(), this._effected.calcStat(Stats.CP_LIMIT, (Creature)null, (Skill)null) * (double)this._effected.getMaxCp() / 100.0D - this._effected.getCurrentCp()));
      if (addToCp > 0.0D) {
        this._effected.setCurrentCp(this._effected.getCurrentCp() + addToCp);
      }

      return true;
    }
  }
}
