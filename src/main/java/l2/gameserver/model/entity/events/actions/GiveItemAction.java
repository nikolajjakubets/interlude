//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.actions;

import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;

public class GiveItemAction implements EventAction {
  private int _itemId;
  private long _count;

  public GiveItemAction(int itemId, long count) {
    this._itemId = itemId;
    this._count = count;
  }

  public void call(GlobalEvent event) {
    Iterator var2 = event.itemObtainPlayers().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      event.giveItem(player, this._itemId, this._count);
    }

  }
}
