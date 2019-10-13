//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.data.xml.holder;

import java.util.Iterator;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.GlobalEvent;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

public final class EventHolder extends AbstractHolder {
  private static final EventHolder _instance = new EventHolder();
  private final IntObjectMap<GlobalEvent> _events = new TreeIntObjectMap();

  public EventHolder() {
  }

  public static EventHolder getInstance() {
    return _instance;
  }

  public void addEvent(EventType type, GlobalEvent event) {
    this._events.put(type.step() + event.getId(), event);
  }

  public <E extends GlobalEvent> E getEvent(EventType type, int id) {
    return (GlobalEvent)this._events.get(type.step() + id);
  }

  public void findEvent(Player player) {
    Iterator var2 = this._events.values().iterator();

    while(var2.hasNext()) {
      GlobalEvent event = (GlobalEvent)var2.next();
      if (event.isParticle(player)) {
        player.addEvent(event);
      }
    }

  }

  public void callInit() {
    Iterator var1 = this._events.values().iterator();

    while(var1.hasNext()) {
      GlobalEvent event = (GlobalEvent)var1.next();
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
