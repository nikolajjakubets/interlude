//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;

/** @deprecated */
@Deprecated
public class RequestUnEquipItem extends L2GameClientPacket {
  private int _slot;

  public RequestUnEquipItem() {
  }

  protected void readImpl() {
    this._slot = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else if (activeChar.isFishing()) {
        activeChar.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
      } else if (this._slot != 128 && this._slot != 256 && this._slot != 16384 || !activeChar.isCursedWeaponEquipped() && activeChar.getActiveWeaponFlagAttachment() == null) {
        if (this._slot == 128) {
          ItemInstance weapon = activeChar.getActiveWeaponInstance();
          if (weapon == null) {
            return;
          }

          activeChar.abortAttack(true, true);
          activeChar.abortCast(true, true);
          activeChar.sendDisarmMessage(weapon);
        }

        activeChar.getInventory().unEquipItemInBodySlot(this._slot);
      }
    }
  }
}
