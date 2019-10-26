//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Manor;
import l2.gameserver.templates.manor.SeedProduction;

public class ExShowSeedInfo extends L2GameServerPacket {
  private List<SeedProduction> _seeds;
  private int _manorId;

  public ExShowSeedInfo(int manorId, List<SeedProduction> seeds) {
    this._manorId = manorId;
    this._seeds = seeds;
  }

  protected void writeImpl() {
    this.writeEx(28);
    this.writeC(0);
    this.writeD(this._manorId);
    this.writeD(0);
    this.writeD(this._seeds.size());
    Iterator var1 = this._seeds.iterator();

    while(var1.hasNext()) {
      SeedProduction seed = (SeedProduction)var1.next();
      this.writeD(seed.getId());
      this.writeD((int)seed.getCanProduce());
      this.writeD((int)seed.getStartProduce());
      this.writeD((int)seed.getPrice());
      this.writeD(Manor.getInstance().getSeedLevel(seed.getId()));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 1));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItemBySeed(seed.getId(), 2));
    }

  }
}
