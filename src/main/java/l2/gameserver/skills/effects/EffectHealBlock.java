//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectHealBlock extends Effect {
  public EffectHealBlock(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return this._effected.isHealBlocked() ? false : super.checkCondition();
  }

  public void onStart() {
    super.onStart();
    this._effected.startHealBlocked();
  }

  public void onExit() {
    super.onExit();
    this._effected.stopHealBlocked();
  }

  public boolean onActionTime() {
    return false;
  }
}
