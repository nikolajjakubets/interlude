//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.FishGroup;
import l2.gameserver.templates.item.support.FishTemplate;
import l2.gameserver.templates.item.support.LureTemplate;
import l2.gameserver.templates.item.support.LureType;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public class FishDataHolder extends AbstractHolder {
  private static final FishDataHolder _instance = new FishDataHolder();
  private List<FishTemplate> _fishes = new ArrayList();
  private IntObjectMap<LureTemplate> _lures = new HashIntObjectMap();
  private IntObjectMap<Map<LureType, Map<FishGroup, Integer>>> _distributionsForZones = new HashIntObjectMap();

  public FishDataHolder() {
  }

  public static FishDataHolder getInstance() {
    return _instance;
  }

  public void addFish(FishTemplate fishTemplate) {
    this._fishes.add(fishTemplate);
  }

  public void addLure(LureTemplate template) {
    this._lures.put(template.getItemId(), template);
  }

  public void addDistribution(int id, LureType lureType, Map<FishGroup, Integer> map) {
    Map<LureType, Map<FishGroup, Integer>> byLureType = (Map)this._distributionsForZones.get(id);
    if (byLureType == null) {
      this._distributionsForZones.put(id, byLureType = new HashMap());
    }

    ((Map)byLureType).put(lureType, map);
  }

  public void log() {
    this.info("load " + this._fishes.size() + " fish(es).");
    this.info("load " + this._lures.size() + " lure(s).");
    this.info("load " + this._distributionsForZones.size() + " distribution(s).");
  }

  /** @deprecated */
  @Deprecated
  public int size() {
    return 0;
  }

  public void clear() {
    this._fishes.clear();
    this._lures.clear();
    this._distributionsForZones.clear();
  }
}
