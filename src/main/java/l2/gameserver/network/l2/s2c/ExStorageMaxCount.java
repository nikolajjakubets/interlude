//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.Player;

public class ExStorageMaxCount extends L2GameServerPacket {
  private int _inventory;
  private int _warehouse;
  private int _clan;
  private int _privateSell;
  private int _privateBuy;
  private int _recipeDwarven;
  private int _recipeCommon;

  public ExStorageMaxCount(Player player) {
    this._inventory = player.getInventoryLimit();
    this._warehouse = player.getWarehouseLimit();
    this._clan = Config.WAREHOUSE_SLOTS_CLAN;
    this._privateBuy = this._privateSell = player.getTradeLimit();
    this._recipeDwarven = player.getDwarvenRecipeLimit();
    this._recipeCommon = player.getCommonRecipeLimit();
  }

  protected final void writeImpl() {
    this.writeEx(46);
    this.writeD(this._inventory);
    this.writeD(this._warehouse);
    this.writeD(this._clan);
    this.writeD(this._privateSell);
    this.writeD(this._privateBuy);
    this.writeD(this._recipeDwarven);
    this.writeD(this._recipeCommon);
  }
}
