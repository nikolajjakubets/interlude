//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;

public class RecipeItemMakeInfo extends L2GameServerPacket {
  private int _id;
  private int _typeOrd;
  private int _status;
  private int _curMP;
  private int _maxMP;

  public RecipeItemMakeInfo(Player player, Recipe recipe, int status) {
    this._id = recipe.getId();
    this._typeOrd = recipe.getType().ordinal();
    this._status = status;
    this._curMP = (int)player.getCurrentMp();
    this._maxMP = player.getMaxMp();
  }

  protected final void writeImpl() {
    this.writeC(215);
    this.writeD(this._id);
    this.writeD(this._typeOrd);
    this.writeD(this._curMP);
    this.writeD(this._maxMP);
    this.writeD(this._status);
  }
}
