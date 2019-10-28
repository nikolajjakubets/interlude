//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class EventOwner implements Serializable {
  private Set<GlobalEvent> _events = new HashSet<>(2);

  public EventOwner() {
  }

  @SuppressWarnings("unchecked")
  public <E extends GlobalEvent> E getEvent(Class<E> eventClass) {
    for (GlobalEvent e : _events) {
      if (e.getClass() == eventClass) // fast hack
      {
        return (E) e;
      }
      if (eventClass.isAssignableFrom(e.getClass())) // FIXME [VISTALL]
      // какойто другой
      // способ определить
      {
        return (E) e;
      }
    }

    return null;
  }

  public void addEvent(GlobalEvent event) {
    this._events.add(event);
  }

  public void removeEvent(GlobalEvent event) {
    this._events.remove(event);
  }

  public void removeEventsByClass(Class<? extends GlobalEvent> eventClass) {

    for (GlobalEvent e : this._events) {
      if (e.getClass() == eventClass) {
        this._events.remove(e);
      } else if (eventClass.isAssignableFrom(e.getClass())) {
        this._events.remove(e);
      }
    }

  }

  public Set<GlobalEvent> getEvents() {
    return this._events;
  }
}
