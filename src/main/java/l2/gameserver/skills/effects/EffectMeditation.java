//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectMeditation extends Effect {
  public EffectMeditation(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this._effected.startParalyzed();
    this._effected.setMeditated(true);
  }

  public void onExit() {
    super.onExit();
    this._effected.stopParalyzed();
    this._effected.setMeditated(false);
  }

  public boolean onActionTime() {
    return false;
  }
}
