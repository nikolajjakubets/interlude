//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.model.pledge.Clan;

public final class ClanWarehouse extends Warehouse {
  public ClanWarehouse(Clan clan) {
    super(clan.getClanId());
  }

  public ItemLocation getItemLocation() {
    return ItemLocation.CLANWH;
  }
}
