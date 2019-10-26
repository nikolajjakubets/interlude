//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPutIntensiveResultForVariationMake extends L2GameServerPacket {
  public static final ExPutIntensiveResultForVariationMake FAIL_PACKET = new ExPutIntensiveResultForVariationMake(0, 0, 0, 0L, false);
  private int _refinerItemObjId;
  private int _lifestoneItemId;
  private int _gemstoneItemId;
  private int _result;
  private long _gemstoneCount;

  public ExPutIntensiveResultForVariationMake(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, long gemstoneCount, boolean isSuccess) {
    this._refinerItemObjId = refinerItemObjId;
    this._lifestoneItemId = lifeStoneId;
    this._gemstoneItemId = gemstoneItemId;
    this._gemstoneCount = gemstoneCount;
    this._result = isSuccess ? 1 : 0;
  }

  protected void writeImpl() {
    this.writeEx(83);
    this.writeD(this._refinerItemObjId);
    this.writeD(this._lifestoneItemId);
    this.writeD(this._gemstoneItemId);
    this.writeD((int)this._gemstoneCount);
    this.writeD(this._result);
  }
}
