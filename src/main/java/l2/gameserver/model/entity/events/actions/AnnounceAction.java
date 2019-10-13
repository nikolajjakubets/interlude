//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class AnnounceAction implements EventAction {
  private int _id;

  public AnnounceAction(int id) {
    this._id = id;
  }

  public void call(GlobalEvent event) {
    event.announce(this._id);
  }
}
