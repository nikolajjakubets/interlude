//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.SoulCrystal;

public final class SoulCrystalHolder extends AbstractHolder {
  private static final SoulCrystalHolder _instance = new SoulCrystalHolder();
  private final TIntObjectHashMap<SoulCrystal> _crystals = new TIntObjectHashMap();

  public SoulCrystalHolder() {
  }

  public static SoulCrystalHolder getInstance() {
    return _instance;
  }

  public void addCrystal(SoulCrystal crystal) {
    this._crystals.put(crystal.getItemId(), crystal);
  }

  public SoulCrystal getCrystal(int item) {
    return (SoulCrystal)this._crystals.get(item);
  }

  public SoulCrystal[] getCrystals() {
    return (SoulCrystal[])this._crystals.getValues(new SoulCrystal[this._crystals.size()]);
  }

  public int size() {
    return this._crystals.size();
  }

  public void clear() {
    this._crystals.clear();
  }
}
