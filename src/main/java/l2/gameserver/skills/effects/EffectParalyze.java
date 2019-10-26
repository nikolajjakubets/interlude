//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectParalyze extends Effect {
  public EffectParalyze(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return this._effected.isParalyzeImmune() ? false : super.checkCondition();
  }

  public void onStart() {
    super.onStart();
    this._effected.startParalyzed();
    this._effected.abortAttack(true, true);
    this._effected.abortCast(true, true);
  }

  public void onExit() {
    super.onExit();
    this._effected.stopParalyzed();
  }

  public boolean onActionTime() {
    return false;
  }
}
