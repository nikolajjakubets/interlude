//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.GlobalEvent;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

public final class EventHolder extends AbstractHolder {
  private static final EventHolder _instance = new EventHolder();
  private final IntObjectMap<GlobalEvent> _events = new TreeIntObjectMap<>();

  public EventHolder() {
  }

  public static EventHolder getInstance() {
    return _instance;
  }

  public void addEvent(EventType type, GlobalEvent event) {
    this._events.put(type.step() + event.getId(), event);
  }

  public GlobalEvent getEvent(EventType type, int id) {
    return this._events.get(type.step() + id);
  }

  public void findEvent(Player player) {

    for (GlobalEvent event : this._events.values()) {
      if (event.isParticle(player)) {
        player.addEvent(event);
      }
    }

  }

  public void callInit() {

    for (GlobalEvent event : this._events.values()) {
      event.initEvent();
    }

  }

  public int size() {
    return this._events.size();
  }

  public void clear() {
    this._events.clear();
  }
}
