//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class RecipeShopItemInfo extends L2GameServerPacket {
  private int _recipeId;
  private int _shopId;
  private int _curMp;
  private int _maxMp;
  private int _success = -1;
  private long _price;

  public RecipeShopItemInfo(Player activeChar, Player manufacturer, int recipeId, long price, int success) {
    this._recipeId = recipeId;
    this._shopId = manufacturer.getObjectId();
    this._price = price;
    this._success = success;
    this._curMp = (int)manufacturer.getCurrentMp();
    this._maxMp = manufacturer.getMaxMp();
  }

  protected final void writeImpl() {
    this.writeC(218);
    this.writeD(this._shopId);
    this.writeD(this._recipeId);
    this.writeD(this._curMp);
    this.writeD(this._maxMp);
    this.writeD(this._success);
    this.writeD((int)this._price);
  }
}
