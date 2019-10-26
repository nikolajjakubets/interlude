//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.RecipeShopMsg;
import l2.gameserver.utils.TradeHelper;

public class RequestRecipeShopListSet extends L2GameClientPacket {
  private int[] _recipes;
  private long[] _prices;
  private int _count;

  public RequestRecipeShopListSet() {
  }

  protected void readImpl() {
    this._count = this.readD();
    if (this._count * 8 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._recipes = new int[this._count];
      this._prices = new long[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._recipes[i] = this.readD();
        this._prices[i] = (long)this.readD();
        if (this._prices[i] < 0L) {
          this._count = 0;
          return;
        }
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player manufacturer = ((GameClient)this.getClient()).getActiveChar();
    if (manufacturer != null && this._count != 0) {
      if (!TradeHelper.checksIfCanOpenStore(manufacturer, 5)) {
        manufacturer.sendActionFailed();
      } else if (this._count > Config.MAX_PVTCRAFT_SLOTS) {
        this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
      } else {
        List<ManufactureItem> createList = new CopyOnWriteArrayList();

        for(int i = 0; i < this._count; ++i) {
          int recipeId = this._recipes[i];
          long price = this._prices[i];
          if (manufacturer.findRecipe(recipeId)) {
            ManufactureItem mi = new ManufactureItem(recipeId, price);
            createList.add(mi);
          }
        }

        if (!createList.isEmpty()) {
          manufacturer.setCreateList(createList);
          manufacturer.saveTradeList();
          manufacturer.setPrivateStoreType(5);
          manufacturer.broadcastPacket(new L2GameServerPacket[]{new RecipeShopMsg(manufacturer)});
        }

        manufacturer.sendActionFailed();
      }
    }
  }
}
