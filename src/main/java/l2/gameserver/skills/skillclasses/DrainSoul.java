//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class DrainSoul extends Skill {
  public DrainSoul(StatsSet set) {
    super(set);
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    if (target != null && target.isMonster() && !target.isDead()) {
      return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    } else {
      activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
      return false;
    }
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer() && !activeChar.isDead()) {
      Iterator var3 = targets.iterator();

      while(var3.hasNext()) {
        Creature c = (Creature)var3.next();
        if (c == null || c.isDead() || !c.isMonster()) {
          return;
        }

        MonsterInstance monster = (MonsterInstance)c;
        if (!monster.getTemplate().getAbsorbInfo().isEmpty()) {
          monster.addAbsorber(activeChar.getPlayer());
        }
      }

    }
  }
}
