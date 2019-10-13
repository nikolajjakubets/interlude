//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.Iterator;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.templates.StaticObjectTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class StaticObjectHolder extends AbstractHolder {
  private static final StaticObjectHolder _instance = new StaticObjectHolder();
  private IntObjectMap<StaticObjectTemplate> _templates = new HashIntObjectMap();
  private IntObjectMap<StaticObjectInstance> _spawned = new HashIntObjectMap();

  public StaticObjectHolder() {
  }

  public static StaticObjectHolder getInstance() {
    return _instance;
  }

  public void addTemplate(StaticObjectTemplate template) {
    this._templates.put(template.getUId(), template);
  }

  public StaticObjectTemplate getTemplate(int id) {
    return (StaticObjectTemplate)this._templates.get(id);
  }

  public void spawnAll() {
    Iterator var1 = this._templates.values().iterator();

    while(var1.hasNext()) {
      StaticObjectTemplate template = (StaticObjectTemplate)var1.next();
      if (template.isSpawn()) {
        StaticObjectInstance obj = template.newInstance();
        this._spawned.put(template.getUId(), obj);
      }
    }

    this.info("spawned: " + this._spawned.size() + " static object(s).");
  }

  public StaticObjectInstance getObject(int id) {
    return (StaticObjectInstance)this._spawned.get(id);
  }

  public int size() {
    return this._templates.size();
  }

  public void clear() {
    this._templates.clear();
  }
}
