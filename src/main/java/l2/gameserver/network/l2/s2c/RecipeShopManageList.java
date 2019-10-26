//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ManufactureItem;

public class RecipeShopManageList extends L2GameServerPacket {
  private List<ManufactureItem> createList;
  private Collection<Recipe> recipes;
  private int sellerId;
  private long adena;
  private boolean isDwarven;

  public RecipeShopManageList(Player seller, boolean isDwarvenCraft) {
    this.sellerId = seller.getObjectId();
    this.adena = seller.getAdena();
    this.isDwarven = isDwarvenCraft;
    if (this.isDwarven) {
      this.recipes = seller.getDwarvenRecipeBook();
    } else {
      this.recipes = seller.getCommonRecipeBook();
    }

    this.createList = seller.getCreateList();
    Iterator var3 = this.createList.iterator();

    while(var3.hasNext()) {
      ManufactureItem mi = (ManufactureItem)var3.next();
      if (!seller.findRecipe(mi.getRecipeId())) {
        this.createList.remove(mi);
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(216);
    this.writeD(this.sellerId);
    this.writeD((int)Math.min(this.adena, 2147483647L));
    this.writeD(this.isDwarven ? 0 : 1);
    this.writeD(this.recipes.size());
    int i = 1;
    Iterator var2 = this.recipes.iterator();

    while(var2.hasNext()) {
      Recipe recipe = (Recipe)var2.next();
      this.writeD(recipe.getId());
      this.writeD(i++);
    }

    this.writeD(this.createList.size());
    var2 = this.createList.iterator();

    while(var2.hasNext()) {
      ManufactureItem mi = (ManufactureItem)var2.next();
      this.writeD(mi.getRecipeId());
      this.writeD(0);
      this.writeD((int)mi.getCost());
    }

  }
}
