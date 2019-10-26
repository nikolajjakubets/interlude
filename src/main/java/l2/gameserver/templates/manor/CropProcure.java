//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.manor;

public class CropProcure {
  int _rewardType;
  int _cropId;
  long _buyResidual;
  long _buy;
  long _price;

  public CropProcure(int id) {
    this._cropId = id;
    this._buyResidual = 0L;
    this._rewardType = 0;
    this._buy = 0L;
    this._price = 0L;
  }

  public CropProcure(int id, long amount, int type, long buy, long price) {
    this._cropId = id;
    this._buyResidual = amount;
    this._rewardType = type;
    this._buy = buy;
    this._price = price;
    if (this._price < 0L) {
      this._price = 0L;
    }

  }

  public int getReward() {
    return this._rewardType;
  }

  public int getId() {
    return this._cropId;
  }

  public long getAmount() {
    return this._buyResidual;
  }

  public long getStartAmount() {
    return this._buy;
  }

  public long getPrice() {
    return this._price;
  }

  public void setAmount(long amount) {
    this._buyResidual = amount;
  }
}
