//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class ActiveDeactiveAction implements EventAction {
  private final boolean _active;
  private final String _name;

  public ActiveDeactiveAction(boolean active, String name) {
    this._active = active;
    this._name = name;
  }

  public void call(GlobalEvent event) {
    event.zoneAction(this._name, this._active);
  }
}
