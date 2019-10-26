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

public class EffectHealPercent extends Effect {
  public EffectHealPercent(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return this._effected.isHealBlocked() ? false : super.checkCondition();
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.isHealBlocked()) {
      double hp = this.calc() * (double)this._effected.getMaxHp() / 100.0D;
      double addToHp = Math.max(0.0D, Math.min(hp, this._effected.calcStat(Stats.HP_LIMIT, (Creature)null, (Skill)null) * (double)this._effected.getMaxHp() / 100.0D - this._effected.getCurrentHp()));
      this._effected.sendPacket((new SystemMessage(1066)).addNumber(Math.round(addToHp)));
      if (addToHp > 0.0D) {
        this._effected.setCurrentHp(addToHp + this._effected.getCurrentHp(), false);
      }

    }
  }

  public boolean onActionTime() {
    return false;
  }
}
