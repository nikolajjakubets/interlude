//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectDebuffImmunity extends Effect {
  public EffectDebuffImmunity(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this.getEffected().startDebuffImmunity();
  }

  public void onExit() {
    super.onExit();
    this.getEffected().stopDebuffImmunity();
  }

  public boolean onActionTime() {
    return false;
  }
}
