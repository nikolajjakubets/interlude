//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;

public class RecipeBookItemList extends L2GameServerPacket {
  private Collection<Recipe> _recipes;
  private final boolean _isDwarvenCraft;
  private final int _currentMp;

  public RecipeBookItemList(Player player, boolean isDwarvenCraft) {
    this._isDwarvenCraft = isDwarvenCraft;
    this._currentMp = (int)player.getCurrentMp();
    if (isDwarvenCraft) {
      this._recipes = player.getDwarvenRecipeBook();
    } else {
      this._recipes = player.getCommonRecipeBook();
    }

  }

  protected final void writeImpl() {
    this.writeC(214);
    this.writeD(this._isDwarvenCraft ? 0 : 1);
    this.writeD(this._currentMp);
    this.writeD(this._recipes.size());
    Iterator var1 = this._recipes.iterator();

    while(var1.hasNext()) {
      Recipe recipe = (Recipe)var1.next();
      this.writeD(recipe.getId());
      this.writeD(1);
    }

  }
}
