//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.RecipeShopSellList;

public class RequestRecipeShopSellList extends L2GameClientPacket {
  int _manufacturerId;

  public RequestRecipeShopSellList() {
  }

  protected void readImpl() {
    this._manufacturerId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else {
        Player manufacturer = (Player)activeChar.getVisibleObject(this._manufacturerId);
        if (manufacturer != null && manufacturer.getPrivateStoreType() == 5 && manufacturer.isInActingRange(activeChar)) {
          activeChar.sendPacket(new RecipeShopSellList(activeChar, manufacturer));
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
