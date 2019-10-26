//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Env;

public class EffectMute extends Effect {
  public EffectMute(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.startMuted()) {
      Skill castingSkill = this._effected.getCastingSkill();
      if (castingSkill != null && castingSkill.isMagic()) {
        this._effected.abortCast(true, true);
      }
    }

  }

  public boolean onActionTime() {
    return false;
  }

  public void onExit() {
    super.onExit();
    this._effected.stopMuted();
  }
}
