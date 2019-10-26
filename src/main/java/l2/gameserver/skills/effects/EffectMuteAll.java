//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectMuteAll extends Effect {
  public EffectMuteAll(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this._effected.startMuted();
    this._effected.startPMuted();
    this._effected.abortCast(true, true);
  }

  public void onExit() {
    super.onExit();
    this._effected.stopMuted();
    this._effected.stopPMuted();
  }

  public boolean onActionTime() {
    return false;
  }
}
