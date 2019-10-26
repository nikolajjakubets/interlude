//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class EffectsFromSkills extends Skill {
  public EffectsFromSkills(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(true) {
      Creature target;
      do {
        if (!var3.hasNext()) {
          return;
        }

        target = (Creature)var3.next();
      } while(target == null);

      AddedSkill[] var5 = this.getAddedSkills();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        AddedSkill as = var5[var7];
        as.getSkill().getEffects(activeChar, target, false, false);
      }
    }
  }
}
