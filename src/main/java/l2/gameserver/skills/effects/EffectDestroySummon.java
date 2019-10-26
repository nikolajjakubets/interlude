//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Summon;
import l2.gameserver.stats.Env;

public final class EffectDestroySummon extends Effect {
  public EffectDestroySummon(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return !this._effected.isSummon() ? false : super.checkCondition();
  }

  public void onStart() {
    super.onStart();
    ((Summon)this._effected).unSummon();
  }

  public boolean onActionTime() {
    return false;
  }
}
