//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;

public class EffectHate extends Effect {
  public EffectHate(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (this._effected.isNpc() && this._effected.isMonster()) {
      this._effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this._effector, this._template._value);
    }

  }

  public boolean isHidden() {
    return true;
  }

  public boolean onActionTime() {
    return false;
  }
}
