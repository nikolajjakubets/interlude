//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

public final class TradeItem extends ItemInfo {
  private long _price;
  private long _referencePrice;
  private long _currentValue;
  private int _lastRechargeTime;
  private int _rechargeTime;

  public TradeItem() {
  }

  public TradeItem(ItemInstance item) {
    super(item);
    this.setReferencePrice((long)item.getReferencePrice());
  }

  public void setOwnersPrice(long price) {
    this._price = price;
  }

  public long getOwnersPrice() {
    return this._price;
  }

  public void setReferencePrice(long price) {
    this._referencePrice = price;
  }

  public long getReferencePrice() {
    return this._referencePrice;
  }

  public long getStorePrice() {
    return this.getReferencePrice() / 2L;
  }

  public void setCurrentValue(long value) {
    this._currentValue = value;
  }

  public long getCurrentValue() {
    return this._currentValue;
  }

  public void setRechargeTime(int rechargeTime) {
    this._rechargeTime = rechargeTime;
  }

  public int getRechargeTime() {
    return this._rechargeTime;
  }

  public boolean isCountLimited() {
    return this.getCount() > 0L;
  }

  public void setLastRechargeTime(int lastRechargeTime) {
    this._lastRechargeTime = lastRechargeTime;
  }

  public int getLastRechargeTime() {
    return this._lastRechargeTime;
  }
}
