//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events;

import l2.gameserver.taskmanager.actionrunner.ActionWrapper;

public class EventWrapper extends ActionWrapper {
  private final GlobalEvent _event;
  private final int _time;

  public EventWrapper(String name, GlobalEvent event, int time) {
    super(name);
    this._event = event;
    this._time = time;
  }

  public void runImpl0() throws Exception {
    this._event.timeActions(this._time);
  }
}
