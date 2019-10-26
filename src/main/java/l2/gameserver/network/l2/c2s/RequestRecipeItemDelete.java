//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.Recipe.ERecipeType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.RecipeBookItemList;

public class RequestRecipeItemDelete extends L2GameClientPacket {
  private int _recipeId;

  public RequestRecipeItemDelete() {
  }

  protected void readImpl() {
    this._recipeId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.getPrivateStoreType() == 5) {
        activeChar.sendActionFailed();
      } else {
        Recipe recipe = RecipeHolder.getInstance().getRecipeById(this._recipeId);
        if (recipe == null) {
          activeChar.sendActionFailed();
        } else {
          activeChar.unregisterRecipe(this._recipeId);
          activeChar.sendPacket(new RecipeBookItemList(activeChar, recipe.getType() == ERecipeType.ERT_DWARF));
        }
      }
    }
  }
}
