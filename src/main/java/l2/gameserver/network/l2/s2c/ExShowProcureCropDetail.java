//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowProcureCropDetail extends L2GameServerPacket {
  private int _cropId;
  private Map<Integer, CropProcure> _castleCrops;

  public ExShowProcureCropDetail(int cropId) {
    this._cropId = cropId;
    this._castleCrops = new TreeMap();
    List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
    Iterator var3 = castleList.iterator();

    while(var3.hasNext()) {
      Castle c = (Castle)var3.next();
      CropProcure cropItem = c.getCrop(this._cropId, 0);
      if (cropItem != null && cropItem.getAmount() > 0L) {
        this._castleCrops.put(c.getId(), cropItem);
      }
    }

  }

  public void writeImpl() {
    this.writeEx(34);
    this.writeD(this._cropId);
    this.writeD(this._castleCrops.size());
    Iterator var1 = this._castleCrops.keySet().iterator();

    while(var1.hasNext()) {
      int manorId = (Integer)var1.next();
      CropProcure crop = (CropProcure)this._castleCrops.get(manorId);
      this.writeD(manorId);
      this.writeD((int)crop.getAmount());
      this.writeD((int)crop.getPrice());
      this.writeC(crop.getReward());
    }

  }
}
