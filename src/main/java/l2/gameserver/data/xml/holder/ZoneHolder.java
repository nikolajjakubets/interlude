//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.ZoneTemplate;

public class ZoneHolder extends AbstractHolder {
  private static final ZoneHolder _instance = new ZoneHolder();
  private final Map<String, ZoneTemplate> _zones = new HashMap<>();

  public ZoneHolder() {
  }

  public static ZoneHolder getInstance() {
    return _instance;
  }

  public void addTemplate(ZoneTemplate zone) {
    this._zones.put(zone.getName(), zone);
  }

  public ZoneTemplate getTemplate(String name) {
    return (ZoneTemplate)this._zones.get(name);
  }

  public Map<String, ZoneTemplate> getZones() {
    return this._zones;
  }

  public int size() {
    return this._zones.size();
  }

  public void clear() {
    this._zones.clear();
  }
}
