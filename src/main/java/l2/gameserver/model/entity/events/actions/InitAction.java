//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class InitAction implements EventAction {
  private String _name;

  public InitAction(String name) {
    this._name = name;
  }

  public void call(GlobalEvent event) {
    event.initAction(this._name);
  }
}
