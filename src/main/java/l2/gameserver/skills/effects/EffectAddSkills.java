//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill.AddedSkill;
import l2.gameserver.stats.Env;

public class EffectAddSkills extends Effect {
  public EffectAddSkills(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    AddedSkill[] var1 = this.getSkill().getAddedSkills();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      AddedSkill as = var1[var3];
      this.getEffected().addSkill(as.getSkill());
    }

  }

  public void onExit() {
    super.onExit();
    AddedSkill[] var1 = this.getSkill().getAddedSkills();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      AddedSkill as = var1[var3];
      this.getEffected().removeSkill(as.getSkill());
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
