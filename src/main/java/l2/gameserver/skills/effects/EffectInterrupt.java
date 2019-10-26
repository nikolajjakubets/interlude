//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectInterrupt extends Effect {
  public EffectInterrupt(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (!this.getEffected().isRaid()) {
      this.getEffected().abortCast(false, true);
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
