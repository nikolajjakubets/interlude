//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectHealCPPercent extends Effect {
  private final boolean _ignoreCpEff;

  public EffectHealCPPercent(Env env, EffectTemplate template) {
    super(env, template);
    this._ignoreCpEff = template.getParam().getBool("ignoreCpEff", true);
  }

  public boolean checkCondition() {
    return this._effected.isHealBlocked() ? false : super.checkCondition();
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.isHealBlocked()) {
      double cp = this.calc() * (double)this._effected.getMaxCp() / 100.0D;
      double newCp = cp * (!this._ignoreCpEff ? this._effected.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100.0D, this._effector, this.getSkill()) : 100.0D) / 100.0D;
      double addToCp = Math.max(0.0D, Math.min(newCp, this._effected.calcStat(Stats.CP_LIMIT, (Creature)null, (Skill)null) * (double)this._effected.getMaxCp() / 100.0D - this._effected.getCurrentCp()));
      if (this._effected == this._effector) {
        this._effected.sendPacket((new SystemMessage(1405)).addNumber((long)addToCp));
      } else {
        this._effected.sendPacket((new SystemMessage(1406)).addName(this._effector).addNumber((long)addToCp));
      }

      if (addToCp > 0.0D) {
        this._effected.setCurrentCp(addToCp + this._effected.getCurrentCp());
      }

    }
  }

  public boolean onActionTime() {
    return false;
  }
}
