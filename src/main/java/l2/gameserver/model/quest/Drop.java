//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.quest;

import org.apache.commons.lang3.ArrayUtils;

public class Drop {
  public final int condition;
  public final int maxcount;
  public final int chance;
  public int[] itemList;

  public Drop(int condition, int maxcount, int chance) {
    this.itemList = ArrayUtils.EMPTY_INT_ARRAY;
    this.condition = condition;
    this.maxcount = maxcount;
    this.chance = chance;
  }

  public Drop addItem(int item) {
    this.itemList = ArrayUtils.add(this.itemList, item);
    return this;
  }
}
