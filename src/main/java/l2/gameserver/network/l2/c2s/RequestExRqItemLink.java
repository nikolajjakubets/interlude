//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.ItemInfoCache;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.ExRpItemLink;

public class RequestExRqItemLink extends L2GameClientPacket {
  private int _objectId;

  public RequestExRqItemLink() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
  }

  protected void runImpl() {
    ItemInfo item;
    if ((item = ItemInfoCache.getInstance().get(this._objectId)) == null) {
      this.sendPacket(ActionFail.getStatic());
    } else {
      this.sendPacket(new ExRpItemLink(item));
    }

  }
}
