//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectBuffImmunity extends Effect {
  public EffectBuffImmunity(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this.getEffected().startBuffImmunity();
  }

  public void onExit() {
    super.onExit();
    this.getEffected().stopBuffImmunity();
  }

  public boolean onActionTime() {
    if (this._effected.isDead()) {
      return false;
    } else {
      return this.getSkill().isToggle();
    }
  }
}
