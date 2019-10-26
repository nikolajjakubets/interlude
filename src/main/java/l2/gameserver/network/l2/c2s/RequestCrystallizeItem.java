//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class RequestCrystallizeItem extends L2GameClientPacket {
  private int _objectId;
  private long unk;

  public RequestCrystallizeItem() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this.unk = (long)this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isInStoreMode()) {
        activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
      } else if (activeChar.isInTrade()) {
        activeChar.sendActionFailed();
      } else {
        ItemInstance item = activeChar.getInventory().getItemByObjectId(this._objectId);
        if (item == null) {
          activeChar.sendActionFailed();
        } else if (item.isHeroWeapon()) {
          activeChar.sendPacket(Msg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
        } else if (!item.canBeCrystallized(activeChar)) {
          activeChar.sendActionFailed();
        } else if (activeChar.isInStoreMode()) {
          activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
        } else if (activeChar.isFishing()) {
          activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
        } else if (activeChar.isInTrade()) {
          activeChar.sendActionFailed();
        } else {
          int crystalAmount = item.getTemplate().getCrystalCount();
          int crystalId = item.getTemplate().getCrystalType().cry;
          int level = activeChar.getSkillLevel(248);
          if (level >= 1 && crystalId - 1458 + 1 <= level) {
            Log.LogItem(activeChar, ItemLog.Crystalize, item);
            if (!activeChar.getInventory().destroyItemByObjectId(this._objectId, 1L)) {
              activeChar.sendActionFailed();
            } else {
              activeChar.sendPacket(Msg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED);
              ItemFunctions.addItem(activeChar, crystalId, (long)crystalAmount, true);
              activeChar.sendChanges();
            }
          } else {
            activeChar.sendPacket(Msg.CANNOT_CRYSTALLIZE_CRYSTALLIZATION_SKILL_LEVEL_TOO_LOW);
            activeChar.sendActionFailed();
          }
        }
      }
    }
  }
}
