//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class OpenCloseAction implements EventAction {
  private final boolean _open;
  private final String _name;

  public OpenCloseAction(boolean open, String name) {
    this._open = open;
    this._name = name;
  }

  public void call(GlobalEvent event) {
    event.doorAction(this._name, this._open);
  }
}
