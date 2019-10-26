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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Default extends Skill {
  private static final Logger _log = LoggerFactory.getLogger(Default.class);

  public Default(StatsSet set) {
    super(set);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayer()) {
      activeChar.sendMessage((new CustomMessage("l2p.gameserver.skills.skillclasses.Default.NotImplemented", (Player)activeChar, new Object[0])).addNumber((long)this.getId()).addString("" + this.getSkillType()));
    }

    _log.warn("NOTDONE skill: " + this.getId() + ", used by" + activeChar);
    activeChar.sendActionFailed();
  }
}
