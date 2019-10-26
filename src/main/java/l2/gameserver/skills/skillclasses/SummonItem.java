//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ItemFunctions;

public class SummonItem extends Skill {
  private final int _itemId;
  private final int _minId;
  private final int _maxId;
  private final long _minCount;
  private final long _maxCount;

  public SummonItem(StatsSet set) {
    super(set);
    this._itemId = set.getInteger("SummonItemId", 0);
    this._minId = set.getInteger("SummonMinId", 0);
    this._maxId = set.getInteger("SummonMaxId", this._minId);
    this._minCount = set.getLong("SummonMinCount");
    this._maxCount = set.getLong("SummonMaxCount", this._minCount);
  }

  public void useSkill(Creature activeChar, List<Creature> targets) {
    if (activeChar.isPlayable()) {
      Iterator var3 = targets.iterator();

      while(var3.hasNext()) {
        Creature target = (Creature)var3.next();
        if (target != null) {
          int itemId = this._minId > 0 ? Rnd.get(this._minId, this._maxId) : this._itemId;
          long count = Rnd.get(this._minCount, this._maxCount);
          ItemFunctions.addItem((Playable)activeChar, itemId, count, true);
          this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
        }
      }

    }
  }
}
