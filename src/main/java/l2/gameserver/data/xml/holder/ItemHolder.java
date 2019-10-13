//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemTemplate.Grade;

public final class ItemHolder extends AbstractHolder {
  private static final ItemHolder _instance = new ItemHolder();
  private final TIntObjectHashMap<ItemTemplate> _items = new TIntObjectHashMap();
  private ItemTemplate[] _allTemplates;

  public static ItemHolder getInstance() {
    return _instance;
  }

  private ItemHolder() {
  }

  public void addItem(ItemTemplate template) {
    this._items.put(template.getItemId(), template);
  }

  private void buildFastLookupTable() {
    int highestId = 0;
    int[] var2 = this._items.keys();
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      int id = var2[var4];
      if (id > highestId) {
        highestId = id;
      }
    }

    this._allTemplates = new ItemTemplate[highestId + 1];

    for(TIntObjectIterator iterator = this._items.iterator(); iterator.hasNext(); this._allTemplates[iterator.key()] = (ItemTemplate)iterator.value()) {
      iterator.advance();
    }

  }

  public ItemTemplate getTemplate(int id) {
    ItemTemplate item = (ItemTemplate)ArrayUtils.valid(this._allTemplates, id);
    if (item == null) {
      this.warn("Not defined item id : " + id + ", or out of range!", new Exception());
      return null;
    } else {
      return this._allTemplates[id];
    }
  }

  public ItemTemplate[] getAllTemplates() {
    return this._allTemplates;
  }

  private void itemBreakCrystalPrice() {
    Map<Grade, Long> refGradeCrystalPrices = new HashMap();
    Grade[] var2 = Grade.values();
    int var3 = var2.length;

    int crystalCount;
    for(crystalCount = 0; crystalCount < var3; ++crystalCount) {
      Grade grade = var2[crystalCount];
      if (grade.cry > 0) {
        ItemTemplate crystalItem = this.getTemplate(grade.cry);
        refGradeCrystalPrices.put(grade, (long)crystalItem.getReferencePrice());
      }
    }

    TIntObjectIterator iterator = this._items.iterator();

    while(iterator.hasNext()) {
      iterator.advance();
      ItemTemplate itemTemplate = (ItemTemplate)iterator.value();
      if (itemTemplate != null) {
        crystalCount = itemTemplate.getCrystalCount();
        long refPrice = (long)itemTemplate.getReferencePrice();
        Grade grade = itemTemplate.getCrystalType();
        Long crystalPrice = (Long)refGradeCrystalPrices.get(grade);
        if (crystalPrice != null && grade.cry != itemTemplate.getItemId() && crystalCount != 0 && refPrice != 0L) {
          long crystalizedPrice = (long)crystalCount * crystalPrice;
          if (crystalPrice > refPrice) {
            this.warn("Reference price (" + refPrice + ") of item \"" + itemTemplate.getItemId() + "\" lower than crystal price (" + crystalizedPrice + ")");
          }
        }
      }
    }

  }

  private void processAdditionalChecks() {
    this.itemBreakCrystalPrice();
  }

  protected void process() {
    this.buildFastLookupTable();
    this.processAdditionalChecks();
  }

  public int size() {
    return this._items.size();
  }

  public void clear() {
    this._items.clear();
  }
}
