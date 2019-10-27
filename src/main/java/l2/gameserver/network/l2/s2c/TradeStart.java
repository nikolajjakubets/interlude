//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;

public class TradeStart extends L2GameServerPacket {
  private List<ItemInfo> _tradelist = new ArrayList<>();
  private int targetId;

  public TradeStart(Player player, Player target) {
    this.targetId = target.getObjectId();
    ItemInstance[] items = player.getInventory().getItems();
    ItemInstance[] var4 = items;
    int var5 = items.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      ItemInstance item = var4[var6];
      if (item.canBeTraded(player)) {
        this._tradelist.add(new ItemInfo(item));
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(30);
    this.writeD(this.targetId);
    this.writeH(this._tradelist.size());
    Iterator var1 = this._tradelist.iterator();

    while(var1.hasNext()) {
      ItemInfo item = (ItemInfo)var1.next();
      this.writeH(item.getItem().getType1());
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD((int)item.getCount());
      this.writeH(item.getItem().getType2ForPackets());
      this.writeH(0);
      this.writeD(item.getItem().getBodyPart());
      this.writeH(item.getEnchantLevel());
      this.writeH(0);
      this.writeH(0);
    }

  }
}
