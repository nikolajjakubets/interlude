//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ExEnchantSkillInfo extends L2GameServerPacket {
  private final int _skillId;
  private final int _skillLevel;
  private final int _sp;
  private final long _exp;
  private final int _chance;
  private final List<Pair<Integer, Long>> _itemsNeeded;

  public ExEnchantSkillInfo(int skillId, int skillLvl, int sp, long exp, int chance) {
    this._skillId = skillId;
    this._skillLevel = skillLvl;
    this._sp = sp;
    this._exp = exp;
    this._chance = chance;
    this._itemsNeeded = new LinkedList();
  }

  public void addNeededItem(int itemId, long itemCount) {
    this._itemsNeeded.add(ImmutablePair.of(itemId, itemCount));
  }

  protected void writeImpl() {
    this.writeEx(24);
    this.writeD(this._skillId);
    this.writeD(this._skillLevel);
    this.writeD(this._sp);
    this.writeQ(this._exp);
    this.writeD(this._chance);
    if (this._itemsNeeded.isEmpty()) {
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
      this.writeD(0);
    } else {
      this.writeD(this._itemsNeeded.size());
      Iterator var1 = this._itemsNeeded.iterator();

      while(var1.hasNext()) {
        Pair<Integer, Long> itemNeeded = (Pair)var1.next();
        this.writeD(4);
        this.writeD((Integer)itemNeeded.getKey());
        this.writeD(((Long)itemNeeded.getValue()).intValue());
        this.writeD(0);
      }
    }

  }
}
