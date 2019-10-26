//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.RecipeBookItemList;
import l2.gameserver.templates.StatsSet;

import java.util.List;

public class Craft extends Skill {
  private final boolean _dwarven;

  public Craft(StatsSet set) {
    super(set);
    this._dwarven = set.getBool("isDwarven");
  }

  public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
    Player p = (Player)activeChar;
    return !p.isInStoreMode() && !p.isProcessingRequest() && !p.isInDuel() ? super.checkCondition(activeChar, target, forceUse, dontMove, first) : false;
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    activeChar.sendPacket(new RecipeBookItemList((Player)activeChar, this._dwarven));
  }
}
