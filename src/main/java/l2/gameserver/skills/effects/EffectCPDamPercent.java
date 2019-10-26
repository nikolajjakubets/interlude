//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectCPDamPercent extends Effect {
  public EffectCPDamPercent(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.isDead()) {
      double newCp = (100.0D - this.calc()) * (double)this._effected.getMaxCp() / 100.0D;
      newCp = Math.min(this._effected.getCurrentCp(), Math.max(0.0D, newCp));
      this._effected.setCurrentCp(newCp);
    }
  }

  public boolean onActionTime() {
    return false;
  }
}
