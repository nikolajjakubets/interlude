//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ManufactureItem;

public class RecipeShopSellList extends L2GameServerPacket {
  private int objId;
  private int curMp;
  private int maxMp;
  private long adena;
  private List<ManufactureItem> createList;

  public RecipeShopSellList(Player buyer, Player manufacturer) {
    this.objId = manufacturer.getObjectId();
    this.curMp = (int)manufacturer.getCurrentMp();
    this.maxMp = manufacturer.getMaxMp();
    this.adena = buyer.getAdena();
    this.createList = manufacturer.getCreateList();
  }

  protected final void writeImpl() {
    this.writeC(217);
    this.writeD(this.objId);
    this.writeD(this.curMp);
    this.writeD(this.maxMp);
    this.writeD((int)this.adena);
    this.writeD(this.createList.size());
    Iterator var1 = this.createList.iterator();

    while(var1.hasNext()) {
      ManufactureItem mi = (ManufactureItem)var1.next();
      this.writeD(mi.getRecipeId());
      this.writeD(0);
      this.writeD((int)mi.getCost());
    }

  }
}
