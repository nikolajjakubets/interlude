//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.EnchantItem;
import l2.gameserver.templates.item.support.EnchantScroll;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public class EnchantItemHolder extends AbstractHolder {
  private static EnchantItemHolder _instance = new EnchantItemHolder();
  private Map<Integer, EnchantItem> _items = new HashMap<>();

  public static EnchantItemHolder getInstance() {
    return _instance;
  }

  private EnchantItemHolder() {
  }

  public void log() {
    this.info("load " + this._items.size() + " enchant item(s).");
  }

  public EnchantItem getEnchantItem(int item_id) {
    return (EnchantItem)this._items.get(item_id);
  }

  public EnchantScroll getEnchantScroll(int item_id) {
    EnchantItem ei = this.getEnchantItem(item_id);
    return ei != null && ei instanceof EnchantScroll ? (EnchantScroll)ei : null;
  }

  public void addEnchantItem(EnchantItem ei) {
    this._items.put(ei.getItemId(), ei);
  }

  public int[] getScrollIds() {
    IntSet is = new HashIntSet();
    Iterator var2 = this._items.values().iterator();

    while(var2.hasNext()) {
      EnchantItem ei = (EnchantItem)var2.next();
      if (ei instanceof EnchantScroll) {
        is.add(ei.getItemId());
      }
    }

    return is.toArray(new int[is.size()]);
  }

  public int size() {
    return this._items.size();
  }

  public void clear() {
    this._items.clear();
  }
}
