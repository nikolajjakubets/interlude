//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectMuteAttack extends Effect {
  public EffectMuteAttack(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.startAMuted()) {
      this._effected.abortCast(true, true);
      this._effected.abortAttack(true, true);
    }

  }

  public void onExit() {
    super.onExit();
    this._effected.stopAMuted();
  }

  public boolean onActionTime() {
    return false;
  }
}
