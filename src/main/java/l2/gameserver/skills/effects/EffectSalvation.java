//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectSalvation extends Effect {
  public EffectSalvation(Env env, EffectTemplate template) {
    super(env, template);
  }

  public boolean checkCondition() {
    return this.getEffected().isPlayer() && super.checkCondition();
  }

  public void onStart() {
    this.getEffected().setIsSalvation(true);
  }

  public void onExit() {
    super.onExit();
    this.getEffected().setIsSalvation(false);
  }

  public boolean onActionTime() {
    return false;
  }
}
