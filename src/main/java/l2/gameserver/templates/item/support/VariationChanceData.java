//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item.support;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class VariationChanceData {
  private final int _mineralItemId;
  private final List<Pair<List<Pair<Integer, Double>>, Double>> _variation1;
  private final List<Pair<List<Pair<Integer, Double>>, Double>> _variation2;

  public VariationChanceData(int mineralItemId, List<Pair<List<Pair<Integer, Double>>, Double>> variation1, List<Pair<List<Pair<Integer, Double>>, Double>> variation2) {
    this._mineralItemId = mineralItemId;
    this._variation1 = variation1;
    this._variation2 = variation2;
  }

  public int getMineralItemId() {
    return this._mineralItemId;
  }

  public List<Pair<List<Pair<Integer, Double>>, Double>> getVariation1() {
    return this._variation1;
  }

  public List<Pair<List<Pair<Integer, Double>>, Double>> getVariation2() {
    return this._variation2;
  }
}
