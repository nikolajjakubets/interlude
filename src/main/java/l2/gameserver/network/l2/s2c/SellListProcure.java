//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.manor.CropProcure;

public class SellListProcure extends L2GameServerPacket {
  private long _money;
  private Map<ItemInstance, Long> _sellList = new HashMap<>();
  private List<CropProcure> _procureList = new ArrayList<>();
  private int _castle;

  public SellListProcure(Player player, int castleId) {
    this._money = player.getAdena();
    this._castle = castleId;
    this._procureList = ((Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._castle)).getCropProcure(0);
    Iterator var3 = this._procureList.iterator();

    while(var3.hasNext()) {
      CropProcure c = (CropProcure)var3.next();
      ItemInstance item = player.getInventory().getItemByItemId(c.getId());
      if (item != null && c.getAmount() > 0L) {
        this._sellList.put(item, c.getAmount());
      }
    }

  }

  protected final void writeImpl() {
    this.writeC(233);
    this.writeD((int)this._money);
    this.writeD(0);
    this.writeH(this._sellList.size());
    Iterator var1 = this._sellList.keySet().iterator();

    while(var1.hasNext()) {
      ItemInstance item = (ItemInstance)var1.next();
      this.writeH(0);
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD(((Long)this._sellList.get(item)).intValue());
      this.writeH(item.getTemplate().getType2ForPackets());
      this.writeH(0);
      this.writeD(0);
    }

  }
}
