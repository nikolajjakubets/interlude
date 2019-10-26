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
import l2.gameserver.templates.manor.SeedProduction;

public class ExShowSeedSetting extends L2GameServerPacket {
  private int _manorId;
  private int _count;
  private long[] _seedData;

  public ExShowSeedSetting(int manorId) {
    this._manorId = manorId;
    Castle c = (Castle)ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
    List<Integer> seeds = Manor.getInstance().getSeedsForCastle(this._manorId);
    this._count = seeds.size();
    this._seedData = new long[this._count * 12];
    int i = 0;

    for(Iterator var5 = seeds.iterator(); var5.hasNext(); ++i) {
      int s = (Integer)var5.next();
      this._seedData[i * 12 + 0] = (long)s;
      this._seedData[i * 12 + 1] = (long)Manor.getInstance().getSeedLevel(s);
      this._seedData[i * 12 + 2] = (long)Manor.getInstance().getRewardItemBySeed(s, 1);
      this._seedData[i * 12 + 3] = (long)Manor.getInstance().getRewardItemBySeed(s, 2);
      this._seedData[i * 12 + 4] = Manor.getInstance().getSeedSaleLimit(s);
      this._seedData[i * 12 + 5] = Manor.getInstance().getSeedBuyPrice(s);
      this._seedData[i * 12 + 6] = (long)(Manor.getInstance().getSeedBasicPrice(s) * 60 / 100);
      this._seedData[i * 12 + 7] = (long)(Manor.getInstance().getSeedBasicPrice(s) * 10);
      SeedProduction seedPr = c.getSeed(s, 0);
      if (seedPr != null) {
        this._seedData[i * 12 + 8] = seedPr.getStartProduce();
        this._seedData[i * 12 + 9] = seedPr.getPrice();
      } else {
        this._seedData[i * 12 + 8] = 0L;
        this._seedData[i * 12 + 9] = 0L;
      }

      seedPr = c.getSeed(s, 1);
      if (seedPr != null) {
        this._seedData[i * 12 + 10] = seedPr.getStartProduce();
        this._seedData[i * 12 + 11] = seedPr.getPrice();
      } else {
        this._seedData[i * 12 + 10] = 0L;
        this._seedData[i * 12 + 11] = 0L;
      }
    }

  }

  public void writeImpl() {
    this.writeEx(31);
    this.writeD(this._manorId);
    this.writeD(this._count);

    for(int i = 0; i < this._count; ++i) {
      this.writeD((int)this._seedData[i * 12 + 0]);
      this.writeD((int)this._seedData[i * 12 + 1]);
      this.writeC(1);
      this.writeD((int)this._seedData[i * 12 + 2]);
      this.writeC(1);
      this.writeD((int)this._seedData[i * 12 + 3]);
      this.writeD((int)this._seedData[i * 12 + 4]);
      this.writeD((int)this._seedData[i * 12 + 5]);
      this.writeD((int)this._seedData[i * 12 + 6]);
      this.writeD((int)this._seedData[i * 12 + 7]);
      this.writeD((int)this._seedData[i * 12 + 8]);
      this.writeD((int)this._seedData[i * 12 + 9]);
      this.writeD((int)this._seedData[i * 12 + 10]);
      this.writeD((int)this._seedData[i * 12 + 11]);
    }

  }
}
