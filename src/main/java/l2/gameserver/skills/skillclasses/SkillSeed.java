//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.skills.effects.EffectSkillSeed;
import l2.gameserver.templates.StatsSet;

public class SkillSeed extends Skill {
  public SkillSeed(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (!activeChar.isAlikeDead()) {
      Iterator var3 = targets.iterator();

      while(true) {
        Creature target;
        do {
          if (!var3.hasNext()) {
            return;
          }

          target = (Creature)var3.next();
        } while(target.isAlikeDead() && this.getTargetType() != SkillTargetType.TARGET_CORPSE);

        List<Effect> effects = target.getEffectList().getEffectsBySkill(this);
        boolean haveEffect = false;
        if (effects != null && !effects.isEmpty()) {
          Iterator var7 = effects.iterator();

          while(var7.hasNext()) {
            Effect effect = (Effect)var7.next();
            if (effect instanceof EffectSkillSeed) {
              EffectSkillSeed effectSeed = (EffectSkillSeed)effect;
              effectSeed.incSeeds();
              haveEffect = true;
            }
          }
        }

        if (!haveEffect) {
          this.getEffects(activeChar, target, false, false);
        }
      }
    }
  }
}
