//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.DoorTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class DoorHolder extends AbstractHolder {
  private static final DoorHolder _instance = new DoorHolder();
  private IntObjectMap<DoorTemplate> _doors = new HashIntObjectMap();

  public DoorHolder() {
  }

  public static DoorHolder getInstance() {
    return _instance;
  }

  public void addTemplate(DoorTemplate door) {
    this._doors.put(door.getNpcId(), door);
  }

  public DoorTemplate getTemplate(int doorId) {
    return (DoorTemplate)this._doors.get(doorId);
  }

  public IntObjectMap<DoorTemplate> getDoors() {
    return this._doors;
  }

  public int size() {
    return this._doors.size();
  }

  public void clear() {
    this._doors.clear();
  }
}
