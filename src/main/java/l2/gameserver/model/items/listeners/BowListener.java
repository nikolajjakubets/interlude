//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class BowListener implements OnEquipListener {
  private static final BowListener _instance = new BowListener();

  public BowListener() {
  }

  public static BowListener getInstance() {
    return _instance;
  }

  public void onUnequip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable() && slot == 7) {
      Player player = (Player)actor;
      if (item.getItemType() == WeaponType.BOW || item.getItemType() == WeaponType.ROD) {
        player.getInventory().setPaperdollItem(8, (ItemInstance)null);
      }

    }
  }

  public void onEquip(int slot, ItemInstance item, Playable actor) {
    if (item.isEquipable() && slot == 7) {
      Player player = (Player)actor;
      ItemInstance bait;
      if (item.getItemType() == WeaponType.BOW) {
        bait = player.getInventory().findArrowForBow(item.getTemplate());
        if (bait != null) {
          player.getInventory().setPaperdollItem(8, bait);
        }
      }

      if (item.getItemType() == WeaponType.ROD) {
        bait = player.getInventory().findEquippedLure();
        if (bait != null) {
          player.getInventory().setPaperdollItem(8, bait);
        }
      }

    }
  }
}
