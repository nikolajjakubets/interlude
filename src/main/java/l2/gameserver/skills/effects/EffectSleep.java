//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectStun extends Effect {
  public EffectStun(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return Rnd.chance(this._template.chance(100));
  }

  public void onStart() {
    super.onStart();
    this._effected.startStunning();
    this._effected.abortAttack(true, true);
    this._effected.abortCast(true, true);
    this._effected.stopMove();
  }

  public void onExit() {
    super.onExit();
    this._effected.stopStunning();
  }

  public boolean onActionTime() {
    return false;
  }
}
