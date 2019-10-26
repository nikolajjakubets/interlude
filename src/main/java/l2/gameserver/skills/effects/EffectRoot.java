//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectRoot extends Effect {
  public EffectRoot(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this._effected.startRooted();
    this._effected.stopMove();
  }

  public void onExit() {
    super.onExit();
    this._effected.stopRooted();
  }

  public boolean onActionTime() {
    return false;
  }
}
