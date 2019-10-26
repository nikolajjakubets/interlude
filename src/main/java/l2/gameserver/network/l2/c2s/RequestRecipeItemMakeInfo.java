//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.RecipeItemMakeInfo;

public class RequestRecipeItemMakeInfo extends L2GameClientPacket {
  private int _id;

  public RequestRecipeItemMakeInfo() {
  }

  protected void readImpl() {
    this._id = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Recipe recipe = RecipeHolder.getInstance().getRecipeById(this._id);
      if (recipe == null) {
        activeChar.sendActionFailed();
      } else {
        this.sendPacket(new RecipeItemMakeInfo(activeChar, recipe, -1));
      }
    }
  }
}
