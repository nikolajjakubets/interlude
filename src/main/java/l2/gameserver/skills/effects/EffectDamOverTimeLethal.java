//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectDamOverTimeLethal extends Effect {
  public EffectDamOverTimeLethal(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean onActionTime() {
    if (this._effected.isDead()) {
      return false;
    } else {
      double damage = this.calc();
      if (this.getSkill().isOffensive()) {
        damage *= 2.0D;
      }

      damage = this._effector.calcStat(this.getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, this._effected, this.getSkill());
      this._effected.reduceCurrentHp(damage, this._effector, this.getSkill(), !this._effected.isNpc() && this._effected != this._effector, this._effected != this._effector, this._effector.isNpc() || this._effected == this._effector, false, false, true, false);
      return true;
    }
  }
}
