//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Env;

public class EffectMutePhisycal extends Effect {
  public EffectMutePhisycal(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    if (!this._effected.startPMuted()) {
      Skill castingSkill = this._effected.getCastingSkill();
      if (castingSkill != null && !castingSkill.isMagic()) {
        this._effected.abortCast(true, true);
      }
    }

  }

  public void onExit() {
    super.onExit();
    this._effected.stopPMuted();
  }

  public boolean onActionTime() {
    return false;
  }
}
