//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class RecipeShopMsg extends L2GameServerPacket {
  private int _objectId;
  private String _storeName;

  public RecipeShopMsg(Player player) {
    this._objectId = player.getObjectId();
    this._storeName = player.getManufactureName();
  }

  protected final void writeImpl() {
    this.writeC(219);
    this.writeD(this._objectId);
    this.writeS(this._storeName);
  }
}
