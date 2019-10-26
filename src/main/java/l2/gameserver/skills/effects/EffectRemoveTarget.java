//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.stats.Env;

public final class EffectRemoveTarget extends Effect {
  private boolean _doStopTarget;

  public EffectRemoveTarget(Env env, EffectTemplate template) {
    super(env, template);
    this._doStopTarget = template.getParam().getBool("doStopTarget", false);
  }

  public boolean checkCondition() {
    return Rnd.chance(this._template.chance(100));
  }

  public void onStart() {
    if (this.getEffected().getAI() instanceof DefaultAI) {
      ((DefaultAI)this.getEffected().getAI()).setGlobalAggro(System.currentTimeMillis() + 3000L);
    }

    this.getEffected().setTarget((GameObject)null);
    if (this._doStopTarget) {
      this.getEffected().stopMove();
    }

    this.getEffected().abortAttack(true, true);
    this.getEffected().abortCast(true, true);
    this.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, this.getEffector());
  }

  public boolean isHidden() {
    return true;
  }

  public boolean onActionTime() {
    return false;
  }
}
