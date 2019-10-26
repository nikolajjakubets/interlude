//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectMPDamPercent extends Effect {
  public EffectMPDamPercent(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.isDead()) {
      double newMp = (100.0D - this.calc()) * (double)this._effected.getMaxMp() / 100.0D;
      newMp = Math.min(this._effected.getCurrentMp(), Math.max(0.0D, newMp));
      this._effected.setCurrentMp(newMp);
    }
  }

  public boolean onActionTime() {
    return false;
  }
}
