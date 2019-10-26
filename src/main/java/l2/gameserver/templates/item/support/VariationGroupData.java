//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

public class VariationGroupData {
  private final long _cancelPrice;
  private final int _mineralItemId;
  private final int _gemstoneItemId;
  private final long _gemstoneItemCnt;

  public VariationGroupData(int mineralItemId, int gemstoneItemId, long gemstoneItemCnt, long cancelPrice) {
    this._mineralItemId = mineralItemId;
    this._gemstoneItemId = gemstoneItemId;
    this._gemstoneItemCnt = gemstoneItemCnt;
    this._cancelPrice = cancelPrice;
  }

  public long getCancelPrice() {
    return this._cancelPrice;
  }

  public int getMineralItemId() {
    return this._mineralItemId;
  }

  public int getGemstoneItemId() {
    return this._gemstoneItemId;
  }

  public long getGemstoneItemCnt() {
    return this._gemstoneItemCnt;
  }
}
