//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.FeedableBeastInstance;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class BeastFeed extends Skill {
  public BeastFeed(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target instanceof FeedableBeastInstance) {
        ((FeedableBeastInstance)target).onSkillUse((Player)activeChar, this._id);
      }
    }

  }
}
