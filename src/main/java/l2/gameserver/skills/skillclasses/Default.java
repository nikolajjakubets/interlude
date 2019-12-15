//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.StatsSet;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Default extends Skill {

  public Default(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      activeChar.sendMessage((new CustomMessage("l2p.gameserver.skills.skillclasses.Default.NotImplemented", (Player) activeChar, new Object[0])).addNumber((long) this.getId()).addString("" + this.getSkillType()));
    }

    log.warn("NOTDONE skill: " + this.getId() + ", used by" + activeChar);
    activeChar.sendActionFailed();
  }
}
