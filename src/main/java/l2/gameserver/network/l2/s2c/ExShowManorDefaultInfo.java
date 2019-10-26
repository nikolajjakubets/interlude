//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Manor;

public class ExShowManorDefaultInfo extends L2GameServerPacket {
  private List<Integer> _crops = null;

  public ExShowManorDefaultInfo() {
    this._crops = Manor.getInstance().getAllCrops();
  }

  protected void writeImpl() {
    this.writeEx(30);
    this.writeC(0);
    this.writeD(this._crops.size());
    Iterator var1 = this._crops.iterator();

    while(var1.hasNext()) {
      int cropId = (Integer)var1.next();
      this.writeD(cropId);
      this.writeD(Manor.getInstance().getSeedLevelByCrop(cropId));
      this.writeD(Manor.getInstance().getSeedBasicPriceByCrop(cropId));
      this.writeD(Manor.getInstance().getCropBasicPrice(cropId));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItem(cropId, 1));
      this.writeC(1);
      this.writeD(Manor.getInstance().getRewardItem(cropId, 2));
    }

  }
}
