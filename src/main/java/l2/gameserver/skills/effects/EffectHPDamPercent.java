//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectHPDamPercent extends Effect {
  public EffectHPDamPercent(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.isDead()) {
      double newHp = (100.0D - this.calc()) * (double)this._effected.getMaxHp() / 100.0D;
      newHp = Math.min(this._effected.getCurrentHp(), Math.max(0.0D, newHp));
      this._effected.setCurrentHp(newHp, false);
    }
  }

  public boolean onActionTime() {
    return false;
  }
}
