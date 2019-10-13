//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.base;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.items.ItemAttributes;

public class MultiSellIngredient implements Cloneable {
  private int _itemId;
  private long _itemCount;
  private int _itemEnchant;
  private ItemAttributes _itemAttributes;
  private boolean _mantainIngredient;

  public MultiSellIngredient(int itemId, long itemCount) {
    this(itemId, itemCount, 0);
  }

  public MultiSellIngredient(int itemId, long itemCount, int enchant) {
    this._itemId = itemId;
    this._itemCount = itemCount;
    this._itemEnchant = enchant;
    this._mantainIngredient = false;
    this._itemAttributes = new ItemAttributes();
  }

  public MultiSellIngredient clone() {
    MultiSellIngredient mi = new MultiSellIngredient(this._itemId, this._itemCount, this._itemEnchant);
    mi.setMantainIngredient(this._mantainIngredient);
    mi.setItemAttributes(this._itemAttributes.clone());
    return mi;
  }

  public void setItemId(int itemId) {
    this._itemId = itemId;
  }

  public int getItemId() {
    return this._itemId;
  }

  public void setItemCount(long itemCount) {
    this._itemCount = itemCount;
  }

  public long getItemCount() {
    return this._itemCount;
  }

  public boolean isStackable() {
    return this._itemId <= 0 || ItemHolder.getInstance().getTemplate(this._itemId).isStackable();
  }

  public void setItemEnchant(int itemEnchant) {
    this._itemEnchant = itemEnchant;
  }

  public int getItemEnchant() {
    return this._itemEnchant;
  }

  public ItemAttributes getItemAttributes() {
    return this._itemAttributes;
  }

  public void setItemAttributes(ItemAttributes attr) {
    this._itemAttributes = attr;
  }

  public int hashCode() {
    int prime = true;
    int result = 1;
    int result = 31 * result + (int)(this._itemCount ^ this._itemCount >>> 32);
    Element[] var3 = Element.VALUES;
    int var4 = var3.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      Element e = var3[var5];
      result = 31 * result + this._itemAttributes.getValue(e);
    }

    result = 31 * result + this._itemEnchant;
    result = 31 * result + this._itemId;
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (this.getClass() != obj.getClass()) {
      return false;
    } else {
      MultiSellIngredient other = (MultiSellIngredient)obj;
      if (this._itemId != other._itemId) {
        return false;
      } else if (this._itemCount != other._itemCount) {
        return false;
      } else if (this._itemEnchant != other._itemEnchant) {
        return false;
      } else {
        Element[] var3 = Element.VALUES;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
          Element e = var3[var5];
          if (this._itemAttributes.getValue(e) != other._itemAttributes.getValue(e)) {
            return false;
          }
        }

        return true;
      }
    }
  }

  public boolean getMantainIngredient() {
    return this._mantainIngredient;
  }

  public void setMantainIngredient(boolean mantainIngredient) {
    this._mantainIngredient = mantainIngredient;
  }
}
