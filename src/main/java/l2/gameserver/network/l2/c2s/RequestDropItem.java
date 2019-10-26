//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.Location;

public class RequestDropItem extends L2GameClientPacket {
  private int _objectId;
  private long _count;
  private Location _loc;

  public RequestDropItem() {
  }

  protected void readImpl() {
    this._objectId = this.readD();
    this._count = (long)this.readD();
    this._loc = new Location(this.readD(), this.readD(), this.readD());
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._count >= 1L && !this._loc.isNull()) {
        if (activeChar.isActionsDisabled()) {
          activeChar.sendActionFailed();
        } else if (Config.ALLOW_DISCARDITEM && !activeChar.getPlayerAccess().BlockInventory) {
          if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
          } else if (!activeChar.isSitting() && !activeChar.isDropDisabled()) {
            if (activeChar.isInTrade()) {
              activeChar.sendActionFailed();
            } else if (activeChar.isFishing()) {
              activeChar.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            } else if (activeChar.isActionBlocked("drop_item")) {
              activeChar.sendPacket(Msg.YOU_CANNOT_DISCARD_THOSE_ITEMS_HERE);
            } else if (activeChar.isInRangeSq(this._loc, 22500L) && Math.abs(this._loc.z - activeChar.getZ()) <= 50) {
              ItemInstance item = activeChar.getInventory().getItemByObjectId(this._objectId);
              if (item == null) {
                activeChar.sendActionFailed();
              } else if (!item.canBeDropped(activeChar, false)) {
                activeChar.sendPacket(Msg.THAT_ITEM_CANNOT_BE_DISCARDED);
              } else {
                item.getTemplate().getHandler().dropItem(activeChar, item, this._count, this._loc);
              }
            } else {
              activeChar.sendPacket(Msg.THAT_IS_TOO_FAR_FROM_YOU_TO_DISCARD);
            }
          } else {
            activeChar.sendActionFailed();
          }
        } else {
          activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestDropItem.Disallowed", activeChar, new Object[0]));
        }
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
