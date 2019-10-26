//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.manor;

public class SeedProduction {
  int _seedId;
  long _residual;
  long _price;
  long _sales;

  public SeedProduction(int id) {
    this._seedId = id;
    this._sales = 0L;
    this._price = 0L;
    this._sales = 0L;
  }

  public SeedProduction(int id, long amount, long price, long sales) {
    this._seedId = id;
    this._residual = amount;
    this._price = price;
    this._sales = sales;
  }

  public int getId() {
    return this._seedId;
  }

  public long getCanProduce() {
    return this._residual;
  }

  public long getPrice() {
    return this._price;
  }

  public long getStartProduce() {
    return this._sales;
  }

  public void setCanProduce(long amount) {
    this._residual = amount;
  }
}
