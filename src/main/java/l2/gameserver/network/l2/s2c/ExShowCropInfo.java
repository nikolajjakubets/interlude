//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Manor;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowCropInfo extends L2GameServerPacket {
  private List<CropProcure> _crops;
  private int _manorId;

  public ExShowCropInfo(int manorId, List<CropProcure> crops) {
    this._manorId = manorId;
    this._crops = crops;
  }

  protected void writeImpl() {
    this.writeEx(29);
    this.writeC(0);
    this.writeD(this._manorId);
    this.writeD(0);
    this.writeD(this._crops.size());
    Iterator var1 = this._crops.iterator();

    while(var1.hasNext()) {
      CropProcure crop = (CropProcure)var1.next();
      this.writeD(crop.getId());
      this.writeD((int)crop.getAmount());
      this.writeD((int)crop.getStartAmount());
      this.writeD((int)crop.getPrice());
      this.writeC(crop.getReward());
      this.writeD(Manor.getInstance().getSeedLevelByCrop(crop.getId()));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItem(crop.getId(), 1));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItem(crop.getId(), 2));
    }

  }
}
