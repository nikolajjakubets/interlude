//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2.gameserver.model.base.MultiSellEntry;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.templates.item.ItemTemplate;

public class MultiSellList extends L2GameServerPacket {
  private final int _page;
  private final int _finished;
  private final int _listId;
  private final List<MultiSellEntry> _list;

  public MultiSellList(MultiSellListContainer list, int page, int finished) {
    this._list = list.getEntries();
    this._listId = list.getListId();
    this._page = page;
    this._finished = finished;
  }

  protected final void writeImpl() {
    this.writeC(208);
    this.writeD(this._listId);
    this.writeD(this._page);
    this.writeD(this._finished);
    this.writeD(Config.MULTISELL_SIZE);
    this.writeD(this._list.size());
    Iterator var2 = this._list.iterator();

    while(var2.hasNext()) {
      MultiSellEntry ent = (MultiSellEntry)var2.next();
      List<MultiSellIngredient> ingredients = fixIngredients(ent.getIngredients());
      this.writeD(ent.getEntryId());
      this.writeD(0);
      this.writeD(0);
      this.writeC(1);
      this.writeH(ent.getProduction().size());
      this.writeH(ingredients.size());
      Iterator var4 = ent.getProduction().iterator();

      MultiSellIngredient i;
      int itemId;
      ItemTemplate item;
      while(var4.hasNext()) {
        i = (MultiSellIngredient)var4.next();
        itemId = i.getItemId();
        item = itemId > 0 ? ItemHolder.getInstance().getTemplate(i.getItemId()) : null;
        this.writeH(itemId);
        this.writeD(itemId > 0 ? item.getBodyPart() : 0);
        this.writeH(itemId > 0 ? item.getType2ForPackets() : 0);
        this.writeD((int)i.getItemCount());
        this.writeH(i.getItemEnchant());
        this.writeD(0);
        this.writeD(0);
      }

      var4 = ingredients.iterator();

      while(var4.hasNext()) {
        i = (MultiSellIngredient)var4.next();
        itemId = i.getItemId();
        item = itemId > 0 ? ItemHolder.getInstance().getTemplate(i.getItemId()) : null;
        this.writeH(itemId);
        this.writeH(itemId > 0 ? item.getType2() : '\uffff');
        this.writeD((int)i.getItemCount());
        this.writeH(i.getItemEnchant());
        this.writeD(0);
        this.writeD(0);
      }
    }

  }

  private static List<MultiSellIngredient> fixIngredients(List<MultiSellIngredient> ingredients) {
    int needFix = 0;
    Iterator var2 = ingredients.iterator();

    while(var2.hasNext()) {
      MultiSellIngredient ingredient = (MultiSellIngredient)var2.next();
      if (ingredient.getItemCount() > 2147483647L) {
        ++needFix;
      }
    }

    if (needFix == 0) {
      return ingredients;
    } else {
      List<MultiSellIngredient> result = new ArrayList(ingredients.size() + needFix);
      Iterator var4 = ingredients.iterator();

      while(var4.hasNext()) {
        MultiSellIngredient ingredient = (MultiSellIngredient)var4.next();
        ingredient = ingredient.clone();

        while(ingredient.getItemCount() > 2147483647L) {
          MultiSellIngredient temp = ingredient.clone();
          temp.setItemCount(2000000000L);
          result.add(temp);
          ingredient.setItemCount(ingredient.getItemCount() - 2000000000L);
        }

        if (ingredient.getItemCount() > 0L) {
          result.add(ingredient);
        }
      }

      return result;
    }
  }
}
