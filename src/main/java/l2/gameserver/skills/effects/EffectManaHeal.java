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

public class EffectManaHeal extends Effect {
  private final boolean _ignoreMpEff;

  public EffectManaHeal(Env env, EffectTemplate template) {
    super(env, template);
    this._ignoreMpEff = template.getParam().getBool("ignoreMpEff", false);
  }

  public boolean checkCondition() {
    return this._effected.isHealBlocked() ? false : super.checkCondition();
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.isHealBlocked()) {
      double mp = this.calc();
      double newMp = Math.min(mp * 1.7D, mp * (!this._ignoreMpEff ? this._effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0D, this._effector, this.getSkill()) : 100.0D) / 100.0D);
      double addToMp = Math.max(0.0D, Math.min(newMp, this._effected.calcStat(Stats.MP_LIMIT, (Creature)null, (Skill)null) * (double)this._effected.getMaxMp() / 100.0D - this._effected.getCurrentMp()));
      this._effected.sendPacket((new SystemMessage(1068)).addNumber(Math.round(addToMp)));
      if (addToMp > 0.0D) {
        this._effected.setCurrentMp(addToMp + this._effected.getCurrentMp());
      }

    }
  }

  public boolean onActionTime() {
    return false;
  }
}
