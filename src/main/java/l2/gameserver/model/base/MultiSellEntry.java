//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiSellEntry {
  private int _entryId;
  private List<MultiSellIngredient> _ingredients = new ArrayList<>();
  private List<MultiSellIngredient> _production = new ArrayList<>();
  private long _tax;

  public MultiSellEntry() {
  }

  public MultiSellEntry(int id) {
    this._entryId = id;
  }

  public MultiSellEntry(int id, int product, int prod_count, int enchant) {
    this._entryId = id;
    this.addProduct(new MultiSellIngredient(product, (long)prod_count, enchant));
  }

  public void setEntryId(int entryId) {
    this._entryId = entryId;
  }

  public int getEntryId() {
    return this._entryId;
  }

  public void addIngredient(MultiSellIngredient ingredient) {
    if (ingredient.getItemCount() > 0L) {
      this._ingredients.add(ingredient);
    }

  }

  public List<MultiSellIngredient> getIngredients() {
    return this._ingredients;
  }

  public void addProduct(MultiSellIngredient ingredient) {
    this._production.add(ingredient);
  }

  public List<MultiSellIngredient> getProduction() {
    return this._production;
  }

  public long getTax() {
    return this._tax;
  }

  public void setTax(long tax) {
    this._tax = tax;
  }

  public int hashCode() {
    return this._entryId;
  }

  public MultiSellEntry clone() {
    MultiSellEntry ret = new MultiSellEntry(this._entryId);
    Iterator var2 = this._ingredients.iterator();

    MultiSellIngredient i;
    while(var2.hasNext()) {
      i = (MultiSellIngredient)var2.next();
      ret.addIngredient(i.clone());
    }

    var2 = this._production.iterator();

    while(var2.hasNext()) {
      i = (MultiSellIngredient)var2.next();
      ret.addProduct(i.clone());
    }

    return ret;
  }
}
