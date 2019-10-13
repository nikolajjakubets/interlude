//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class EventOwner implements Serializable {
  private Set<GlobalEvent> _events = new HashSet(2);

  public EventOwner() {
  }

  public <E extends GlobalEvent> E getEvent(Class<E> eventClass) {
    Iterator var2 = this._events.iterator();

    GlobalEvent e;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      e = (GlobalEvent)var2.next();
      if (e.getClass() == eventClass) {
        return e;
      }
    } while(!eventClass.isAssignableFrom(e.getClass()));

    return e;
  }

  public void addEvent(GlobalEvent event) {
    this._events.add(event);
  }

  public void removeEvent(GlobalEvent event) {
    this._events.remove(event);
  }

  public void removeEventsByClass(Class<? extends GlobalEvent> eventClass) {
    Iterator var2 = this._events.iterator();

    while(var2.hasNext()) {
      GlobalEvent e = (GlobalEvent)var2.next();
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
