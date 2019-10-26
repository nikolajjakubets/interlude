//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowSellCropList extends L2GameServerPacket {
  private int _manorId = 1;
  private Map<Integer, ItemInstance> _cropsItems;
  private Map<Integer, CropProcure> _castleCrops;

  public ExShowSellCropList(Player player, int manorId, List<CropProcure> crops) {
    this._manorId = manorId;
    this._castleCrops = new TreeMap();
    this._cropsItems = new TreeMap();
    List<Integer> allCrops = Manor.getInstance().getAllCrops();
    Iterator var5 = allCrops.iterator();

    while(var5.hasNext()) {
      int cropId = (Integer)var5.next();
      ItemInstance item = player.getInventory().getItemByItemId(cropId);
      if (item != null) {
        this._cropsItems.put(cropId, item);
      }
    }

    var5 = crops.iterator();

    while(var5.hasNext()) {
      CropProcure crop = (CropProcure)var5.next();
      if (this._cropsItems.containsKey(crop.getId()) && crop.getAmount() > 0L) {
        this._castleCrops.put(crop.getId(), crop);
      }
    }

  }

  public void writeImpl() {
    this.writeEx(33);
    this.writeD(this._manorId);
    this.writeD(this._cropsItems.size());

    ItemInstance item;
    for(Iterator var1 = this._cropsItems.values().iterator(); var1.hasNext(); this.writeD((int)item.getCount())) {
      item = (ItemInstance)var1.next();
      this.writeD(item.getObjectId());
      this.writeD(item.getItemId());
      this.writeD(Manor.getInstance().getSeedLevelByCrop(item.getItemId()));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItem(item.getItemId(), 1));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItem(item.getItemId(), 2));
      if (this._castleCrops.containsKey(item.getItemId())) {
        CropProcure crop = (CropProcure)this._castleCrops.get(item.getItemId());
        this.writeD(this._manorId);
        this.writeD((int)crop.getAmount());
        this.writeD((int)crop.getPrice());
        this.writeC(crop.getReward());
      } else {
        this.writeD(-1);
        this.writeD(0);
        this.writeD(0);
        this.writeC(0);
      }
    }

  }
}
