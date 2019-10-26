//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

import java.util.Iterator;
import java.util.List;

public class DeleteHateOfMe extends Skill {
  public DeleteHateOfMe(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    Iterator var3 = targets.iterator();

    while(var3.hasNext()) {
      Creature target = (Creature)var3.next();
      if (target != null) {
        if (activeChar.isPlayer() && ((Player)activeChar).isGM()) {
          activeChar.sendMessage((new CustomMessage("l2p.gameserver.skills.Formulas.Chance", (Player)activeChar, new Object[0])).addString(this.getName()).addNumber((long)this.getActivateRate()));
        }

        if (target.isNpc() && Formulas.calcSkillSuccess(activeChar, target, this, this.getActivateRate())) {
          NpcInstance npc = (NpcInstance)target;
          npc.getAggroList().remove(activeChar, true);
          npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }

        this.getEffects(activeChar, target, true, false);
      }
    }

  }
}
