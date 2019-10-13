//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.CubicTemplate;

public final class CubicHolder extends AbstractHolder {
  private static CubicHolder _instance = new CubicHolder();
  private final TIntObjectHashMap<CubicTemplate> _cubics = new TIntObjectHashMap(10);

  public static CubicHolder getInstance() {
    return _instance;
  }

  private CubicHolder() {
  }

  public void addCubicTemplate(CubicTemplate template) {
    this._cubics.put(this.hash(template.getId(), template.getLevel()), template);
  }

  public CubicTemplate getTemplate(int id, int level) {
    return (CubicTemplate)this._cubics.get(this.hash(id, level));
  }

  public int hash(int id, int level) {
    return id * 10000 + level;
  }

  public int size() {
    return this._cubics.size();
  }

  public void clear() {
    this._cubics.clear();
  }
}
