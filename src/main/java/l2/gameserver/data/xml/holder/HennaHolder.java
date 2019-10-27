//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.ArrayList;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.templates.Henna;

public final class HennaHolder extends AbstractHolder {
  private static final HennaHolder _instance = new HennaHolder();
  private TIntObjectHashMap<Henna> _hennas = new TIntObjectHashMap();

  public HennaHolder() {
  }

  public static HennaHolder getInstance() {
    return _instance;
  }

  public void addHenna(Henna h) {
    this._hennas.put(h.getSymbolId(), h);
  }

  public Henna getHenna(int symbolId) {
    return (Henna)this._hennas.get(symbolId);
  }

  public List<Henna> generateList(Player player) {
    List<Henna> list = new ArrayList<>();
    TIntObjectIterator iterator = this._hennas.iterator();

    while(iterator.hasNext()) {
      iterator.advance();
      Henna h = (Henna)iterator.value();
      if (h.isForThisClass(player)) {
        list.add(h);
      }
    }

    return list;
  }

  public int size() {
    return this._hennas.size();
  }

  public void clear() {
    this._hennas.clear();
  }
}
