//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.items;

import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import org.apache.commons.lang3.ArrayUtils;

public interface IItemHandler {
  IItemHandler NULL = new IItemHandler() {
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
      return false;
    }

    public void dropItem(Player player, ItemInstance item, long count, Location loc) {
      if (item.isEquipped()) {
        player.getInventory().unEquipItem(item);
        player.sendUserInfo(true);
      }

      item = player.getInventory().removeItemByObjectId(item.getObjectId(), count);
      if (item == null) {
        player.sendActionFailed();
      } else {
        Log.LogItem(player, ItemLog.Drop, item);
        item.dropToTheGround(player, loc);
        player.disableDrop(1000);
        player.sendChanges();
      }
    }

    public boolean pickupItem(Playable playable, ItemInstance item) {
      return true;
    }

    public int[] getItemIds() {
      return ArrayUtils.EMPTY_INT_ARRAY;
    }
  };

  boolean useItem(Playable var1, ItemInstance var2, boolean var3);

  void dropItem(Player var1, ItemInstance var2, long var3, Location var5);

  boolean pickupItem(Playable var1, ItemInstance var2);

  int[] getItemIds();
}
