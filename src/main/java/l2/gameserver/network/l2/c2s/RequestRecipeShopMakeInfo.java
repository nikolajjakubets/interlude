//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.RecipeShopItemInfo;

public class RequestRecipeShopMakeInfo extends L2GameClientPacket {
  private int _manufacturerId;
  private int _recipeId;

  public RequestRecipeShopMakeInfo() {
  }

  protected void readImpl() {
    this._manufacturerId = this.readD();
    this._recipeId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.isActionsDisabled()) {
        activeChar.sendActionFailed();
      } else {
        Player manufacturer = (Player)activeChar.getVisibleObject(this._manufacturerId);
        if (manufacturer != null && manufacturer.getPrivateStoreType() == 5 && manufacturer.isInActingRange(activeChar)) {
          long price = -1L;
          Iterator var5 = manufacturer.getCreateList().iterator();

          while(var5.hasNext()) {
            ManufactureItem i = (ManufactureItem)var5.next();
            if (i.getRecipeId() == this._recipeId) {
              price = i.getCost();
              break;
            }
          }

          if (price == -1L) {
            activeChar.sendActionFailed();
          } else {
            activeChar.sendPacket(new RecipeShopItemInfo(activeChar, manufacturer, this._recipeId, price, -1));
          }
        } else {
          activeChar.sendActionFailed();
        }
      }
    }
  }
}
