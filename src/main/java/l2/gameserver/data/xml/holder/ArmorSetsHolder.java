//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.ArmorSet;

public final class ArmorSetsHolder extends AbstractHolder {
  private static final ArmorSetsHolder _instance = new ArmorSetsHolder();
  private List<ArmorSet> _armorSets = new ArrayList();

  public ArmorSetsHolder() {
  }

  public static ArmorSetsHolder getInstance() {
    return _instance;
  }

  public void addArmorSet(ArmorSet armorset) {
    this._armorSets.add(armorset);
  }

  public ArmorSet getArmorSet(int chestItemId) {
    Iterator var2 = this._armorSets.iterator();

    ArmorSet as;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      as = (ArmorSet)var2.next();
    } while(!as.getChestItemIds().contains(chestItemId));

    return as;
  }

  public int size() {
    return this._armorSets.size();
  }

  public void clear() {
    this._armorSets.clear();
  }
}
