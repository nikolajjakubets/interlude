//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance.ItemLocation;

public class PcFreight extends Warehouse {
  public PcFreight(Player player) {
    super(player.getObjectId());
  }

  public PcFreight(int objectId) {
    super(objectId);
  }

  public ItemLocation getItemLocation() {
    return ItemLocation.FREIGHT;
  }
}
