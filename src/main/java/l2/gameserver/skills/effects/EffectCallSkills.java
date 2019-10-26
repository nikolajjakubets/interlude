//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.effects;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;

import java.util.Iterator;

public class EffectCallSkills extends Effect {
  public EffectCallSkills(Env env, EffectTemplate template) {
    super(env, template);
  }

  public void onStart() {
    super.onStart();
    int[] skillIds = this.getTemplate().getParam().getIntegerArray("skillIds");
    int[] skillLevels = this.getTemplate().getParam().getIntegerArray("skillLevels");

    for(int i = 0; i < skillIds.length; ++i) {
      Skill skill = SkillTable.getInstance().getInfo(skillIds[i], skillLevels[i]);
      Iterator var5 = skill.getTargets(this.getEffector(), this.getEffected(), false).iterator();

      while(var5.hasNext()) {
        Creature cha = (Creature)var5.next();
        this.getEffector().broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(this.getEffector(), cha, skillIds[i], skillLevels[i], 0, 0L)});
      }

      this.getEffector().callSkill(skill, skill.getTargets(this.getEffector(), this.getEffected(), false), false);
    }

  }

  public boolean onActionTime() {
    return false;
  }
}
