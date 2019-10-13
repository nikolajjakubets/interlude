//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.lang.reflect.Constructor;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.templates.CharTemplate;

public final class BoatHolder extends AbstractHolder {
  public static final CharTemplate TEMPLATE = new CharTemplate(CharTemplate.getEmptyStatsSet());
  private static BoatHolder _instance = new BoatHolder();
  private final TIntObjectHashMap<Boat> _boats = new TIntObjectHashMap();

  public BoatHolder() {
  }

  public static BoatHolder getInstance() {
    return _instance;
  }

  public void spawnAll() {
    this.log();
    TIntObjectIterator iterator = this._boats.iterator();

    while(iterator.hasNext()) {
      iterator.advance();
      ((Boat)iterator.value()).spawnMe();
      this.info("Spawning: " + ((Boat)iterator.value()).getName());
    }

  }

  public Boat initBoat(String name, String clazz) {
    try {
      Class<?> cl = Class.forName("l2.gameserver.model.entity.boat." + clazz);
      Constructor constructor = cl.getConstructor(Integer.TYPE, CharTemplate.class);
      Boat boat = (Boat)constructor.newInstance(IdFactory.getInstance().getNextId(), TEMPLATE);
      boat.setName(name);
      this.addBoat(boat);
      return boat;
    } catch (Exception var6) {
      this.error("Fail to init boat: " + clazz, var6);
      return null;
    }
  }

  public Boat getBoat(String name) {
    TIntObjectIterator iterator = this._boats.iterator();

    do {
      if (!iterator.hasNext()) {
        return null;
      }

      iterator.advance();
    } while(!((Boat)iterator.value()).getName().equals(name));

    return (Boat)iterator.value();
  }

  public Boat getBoat(int objectId) {
    return (Boat)this._boats.get(objectId);
  }

  public void addBoat(Boat boat) {
    this._boats.put(boat.getObjectId(), boat);
  }

  public void removeBoat(Boat boat) {
    this._boats.remove(boat.getObjectId());
  }

  public int size() {
    return this._boats.size();
  }

  public void clear() {
    this._boats.clear();
  }
}
