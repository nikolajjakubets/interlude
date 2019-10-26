//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

import l2.gameserver.model.entity.SevenSigns;
import org.napile.primitive.sets.IntSet;

public class MerchantGuard {
  private int _itemId;
  private int _npcId;
  private int _max;
  private IntSet _ssq;

  public MerchantGuard(int itemId, int npcId, int max, IntSet ssq) {
    this._itemId = itemId;
    this._npcId = npcId;
    this._max = max;
    this._ssq = ssq;
  }

  public int getItemId() {
    return this._itemId;
  }

  public int getNpcId() {
    return this._npcId;
  }

  public int getMax() {
    return this._max;
  }

  public boolean isValidSSQPeriod() {
    return SevenSigns.getInstance().getCurrentPeriod() == 3 && this._ssq.contains(SevenSigns.getInstance().getSealOwner(3));
  }
}
