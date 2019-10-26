//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

import java.util.Map;
import l2.commons.collections.MultiValueSet;

public class LureTemplate {
  private final int _itemId;
  private final int _lengthBonus;
  private final double _revisionNumber;
  private final double _rateBonus;
  private final LureType _lureType;
  private final Map<FishGroup, Integer> _chances;

  public LureTemplate(MultiValueSet<String> set) {
    this._itemId = set.getInteger("item_id");
    this._lengthBonus = set.getInteger("length_bonus");
    this._revisionNumber = set.getDouble("revision_number");
    this._rateBonus = set.getDouble("rate_bonus");
    this._lureType = (LureType)set.getEnum("type", LureType.class);
    this._chances = (Map)set.get("chances");
  }

  public int getItemId() {
    return this._itemId;
  }

  public int getLengthBonus() {
    return this._lengthBonus;
  }

  public double getRevisionNumber() {
    return this._revisionNumber;
  }

  public double getRateBonus() {
    return this._rateBonus;
  }

  public LureType getLureType() {
    return this._lureType;
  }

  public Map<FishGroup, Integer> getChances() {
    return this._chances;
  }
}
