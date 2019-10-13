//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.spawn.SpawnTemplate;

public final class SpawnHolder extends AbstractHolder {
  private static final SpawnHolder _instance = new SpawnHolder();
  private Map<String, List<SpawnTemplate>> _spawns = new HashMap();

  public SpawnHolder() {
  }

  public static SpawnHolder getInstance() {
    return _instance;
  }

  public void addSpawn(String group, SpawnTemplate spawn) {
    List<SpawnTemplate> spawns = (List)this._spawns.get(group);
    if (spawns == null) {
      this._spawns.put(group, spawns = new ArrayList());
    }

    ((List)spawns).add(spawn);
  }

  public List<SpawnTemplate> getSpawn(String name) {
    List<SpawnTemplate> template = (List)this._spawns.get(name);
    return template == null ? Collections.emptyList() : template;
  }

  public int size() {
    int i = 0;

    List l;
    for(Iterator var2 = this._spawns.values().iterator(); var2.hasNext(); i += l.size()) {
      l = (List)var2.next();
    }

    return i;
  }

  public void clear() {
    this._spawns.clear();
  }

  public Map<String, List<SpawnTemplate>> getSpawns() {
    return this._spawns;
  }
}
