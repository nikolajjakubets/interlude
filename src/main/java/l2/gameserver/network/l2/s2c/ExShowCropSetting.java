//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Manor;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.templates.manor.CropProcure;

public class ExShowCropSetting extends L2GameServerPacket {
  private int _manorId;
  private int _count;
  private long[] _cropData;

  public ExShowCropSetting(int manorId) {
    this._manorId = manorId;
    Castle c = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
    List<Integer> crops = Manor.getInstance().getCropsForCastle(this._manorId);
    this._count = crops.size();
    this._cropData = new long[this._count * 14];
    int i = 0;

    for(Iterator var5 = crops.iterator(); var5.hasNext(); ++i) {
      int cr = (Integer)var5.next();
      this._cropData[i * 14 + 0] = (long)cr;
      this._cropData[i * 14 + 1] = (long)Manor.getInstance().getSeedLevelByCrop(cr);
      this._cropData[i * 14 + 2] = (long)Manor.getInstance().getRewardItem(cr, 1);
      this._cropData[i * 14 + 3] = (long)Manor.getInstance().getRewardItem(cr, 2);
      this._cropData[i * 14 + 4] = Manor.getInstance().getCropPuchaseLimit(cr);
      this._cropData[i * 14 + 5] = 0L;
      this._cropData[i * 14 + 6] = (long)(Manor.getInstance().getCropBasicPrice(cr) * 60 / 100);
      this._cropData[i * 14 + 7] = (long)(Manor.getInstance().getCropBasicPrice(cr) * 10);
      CropProcure cropPr = c.getCrop(cr, 0);
      if (cropPr != null) {
        this._cropData[i * 14 + 8] = cropPr.getStartAmount();
        this._cropData[i * 14 + 9] = cropPr.getPrice();
        this._cropData[i * 14 + 10] = (long)cropPr.getReward();
      } else {
        this._cropData[i * 14 + 8] = 0L;
        this._cropData[i * 14 + 9] = 0L;
        this._cropData[i * 14 + 10] = 0L;
      }

      cropPr = c.getCrop(cr, 1);
      if (cropPr != null) {
        this._cropData[i * 14 + 11] = cropPr.getStartAmount();
        this._cropData[i * 14 + 12] = cropPr.getPrice();
        this._cropData[i * 14 + 13] = (long)cropPr.getReward();
      } else {
        this._cropData[i * 14 + 11] = 0L;
        this._cropData[i * 14 + 12] = 0L;
        this._cropData[i * 14 + 13] = 0L;
      }
    }

  }

  public void writeImpl() {
    this.writeEx(32);
    this.writeD(this._manorId);
    this.writeD(this._count);

    for(int i = 0; i < this._count; ++i) {
      this.writeD((int)this._cropData[i * 14 + 0]);
      this.writeD((int)this._cropData[i * 14 + 1]);
      this.writeC(1);
      this.writeD((int)this._cropData[i * 14 + 2]);
      this.writeC(1);
      this.writeD((int)this._cropData[i * 14 + 3]);
      this.writeD((int)this._cropData[i * 14 + 4]);
      this.writeD((int)this._cropData[i * 14 + 5]);
      this.writeD((int)this._cropData[i * 14 + 6]);
      this.writeD((int)this._cropData[i * 14 + 7]);
      this.writeD((int)this._cropData[i * 14 + 8]);
      this.writeD((int)this._cropData[i * 14 + 9]);
      this.writeC((int)this._cropData[i * 14 + 10]);
      this.writeD((int)this._cropData[i * 14 + 11]);
      this.writeD((int)this._cropData[i * 14 + 12]);
      this.writeC((int)this._cropData[i * 14 + 13]);
    }

  }
}
