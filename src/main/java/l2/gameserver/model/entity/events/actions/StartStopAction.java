//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class StartStopAction implements EventAction {
  public static final String EVENT = "event";
  private final String _name;
  private final boolean _start;

  public StartStopAction(String name, boolean start) {
    this._name = name;
    this._start = start;
  }

  public void call(GlobalEvent event) {
    event.action(this._name, this._start);
  }
}
