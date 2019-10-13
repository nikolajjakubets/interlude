//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class RefreshAction implements EventAction {
  private final String _name;

  public RefreshAction(String name) {
    this._name = name;
  }

  public void call(GlobalEvent event) {
    event.refreshAction(this._name);
  }
}
