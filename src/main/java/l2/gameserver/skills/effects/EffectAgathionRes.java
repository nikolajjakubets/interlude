//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public final class EffectAgathionRes extends Effect {
  public EffectAgathionRes(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    this.getEffected().setIsBlessedByNoblesse(true);
  }

  public void onExit() {
    super.onExit();
    this.getEffected().setIsBlessedByNoblesse(false);
  }

  public boolean onActionTime() {
    return false;
  }
}
