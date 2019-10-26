//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance.ItemLocation;

public class PcWarehouse extends Warehouse {
  public PcWarehouse(Player owner) {
    super(owner.getObjectId());
  }

  public PcWarehouse(int ownerId) {
    super(ownerId);
  }

  public ItemLocation getItemLocation() {
    return ItemLocation.WAREHOUSE;
  }
}
