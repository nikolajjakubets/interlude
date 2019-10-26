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

public class EffectHeal extends Effect {
  private final boolean _ignoreHpEff;

  public EffectHeal(Env env, EffectTemplate template) {
    super(env, template);
    this._ignoreHpEff = template.getParam().getBool("ignoreHpEff", false);
  }

  public boolean checkCondition() {
    return this._effected.isHealBlocked() ? false : super.checkCondition();
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.isHealBlocked()) {
      double hp = this.calc();
      double newHp = hp * (!this._ignoreHpEff ? this._effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100.0D, this._effector, this.getSkill()) : 100.0D) / 100.0D;
      double addToHp = Math.max(0.0D, Math.min(newHp, this._effected.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)this._effected.getMaxHp() / 100.0D - this._effected.getCurrentHp()));
      if (addToHp > 0.0D) {
        this._effected.sendPacket((new SystemMessage(1066)).addNumber(Math.round(addToHp)));
        this._effected.setCurrentHp(addToHp + this._effected.getCurrentHp(), false);
      }

    }
  }

  public boolean onActionTime() {
    return false;
  }
}
