//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.Env;

public final class ConditionSlotItemId extends ConditionInventory {
  private final int _itemId;
  private final int _enchantLevel;

  public ConditionSlotItemId(int slot, int itemId, int enchantLevel) {
    super(slot);
    this._itemId = itemId;
    this._enchantLevel = enchantLevel;
  }

  protected boolean testImpl(Env env) {
    if (!env.character.isPlayer()) {
      return false;
    } else {
      Inventory inv = ((Player)env.character).getInventory();
      ItemInstance item = inv.getPaperdollItem(this._slot);
      if (item == null) {
        return this._itemId == 0;
      } else {
        return item.getItemId() == this._itemId && item.getEnchantLevel() >= this._enchantLevel;
      }
    }
  }
}
