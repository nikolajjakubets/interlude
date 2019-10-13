//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.VariationGroupData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class VariationGroupHolder extends AbstractHolder {
  private static final VariationGroupHolder _instance = new VariationGroupHolder();
  private List<Pair<int[], VariationGroupData>> _variationGroupData = new ArrayList();

  public static VariationGroupHolder getInstance() {
    return _instance;
  }

  private VariationGroupHolder() {
  }

  public int size() {
    return this._variationGroupData.size();
  }

  public void clear() {
    this._variationGroupData.clear();
  }

  public void add(int[] itemIds, VariationGroupData vgd) {
    int[] sortedIds = (int[])itemIds.clone();
    Arrays.sort(sortedIds);
    this._variationGroupData.add(new ImmutablePair(sortedIds, vgd));
  }

  public void addSorted(int[] sortedIds, VariationGroupData vgd) {
    this._variationGroupData.add(new ImmutablePair(sortedIds, vgd));
  }

  public List<VariationGroupData> getDataForItemId(int itemId) {
    List<VariationGroupData> resultList = new ArrayList();
    Iterator var3 = this._variationGroupData.iterator();

    while(var3.hasNext()) {
      Pair<int[], VariationGroupData> e = (Pair)var3.next();
      int[] ids = (int[])e.getLeft();
      if (Arrays.binarySearch(ids, itemId) >= 0) {
        resultList.add(e.getRight());
      }
    }

    return resultList;
  }
}
