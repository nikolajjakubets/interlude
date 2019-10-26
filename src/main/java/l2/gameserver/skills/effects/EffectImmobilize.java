//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectImmobilize extends Effect {
  public EffectImmobilize(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this._effected.startImmobilized();
    this._effected.stopMove();
  }

  public void onExit() {
    super.onExit();
    this._effected.stopImmobilized();
  }

  public boolean onActionTime() {
    return false;
  }
}
