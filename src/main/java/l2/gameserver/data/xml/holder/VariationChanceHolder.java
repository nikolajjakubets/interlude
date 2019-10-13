//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.VariationChanceData;
import org.apache.commons.lang3.tuple.Pair;

public class VariationChanceHolder extends AbstractHolder {
  private static final VariationChanceHolder _instance = new VariationChanceHolder();
  private HashMap<Integer, Pair<VariationChanceData, VariationChanceData>> _minerallChances = new HashMap();

  public static VariationChanceHolder getInstance() {
    return _instance;
  }

  private VariationChanceHolder() {
  }

  public int size() {
    return this._minerallChances.size();
  }

  public void clear() {
    this._minerallChances.clear();
  }

  public void add(Pair<VariationChanceData, VariationChanceData> vcdp) {
    if (vcdp.getLeft() != null && vcdp.getRight() != null && ((VariationChanceData)vcdp.getLeft()).getMineralItemId() == ((VariationChanceData)vcdp.getRight()).getMineralItemId()) {
      this._minerallChances.put(((VariationChanceData)vcdp.getLeft()).getMineralItemId(), vcdp);
    } else if (vcdp.getLeft() != null) {
      this._minerallChances.put(((VariationChanceData)vcdp.getLeft()).getMineralItemId(), vcdp);
    } else {
      if (vcdp.getRight() == null) {
        throw new RuntimeException("Empty mineral");
      }

      this._minerallChances.put(((VariationChanceData)vcdp.getRight()).getMineralItemId(), vcdp);
    }

  }

  public Pair<VariationChanceData, VariationChanceData> getVariationChanceDataForMineral(int mineralItemId) {
    return (Pair)this._minerallChances.get(mineralItemId);
  }
}
