//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPutCommissionResultForVariationMake extends L2GameServerPacket {
  public static final ExPutCommissionResultForVariationMake FAIL_PACKET = new ExPutCommissionResultForVariationMake();
  private int _gemstoneObjId;
  private int _serverId;
  private int _result;
  private long _gemstoneCount;
  private long _requiredGemstoneCount;

  private ExPutCommissionResultForVariationMake() {
    this._gemstoneObjId = 0;
    this._serverId = 1;
    this._gemstoneCount = 0L;
    this._requiredGemstoneCount = 0L;
    this._result = 0;
  }

  public ExPutCommissionResultForVariationMake(int gemstoneObjId, long count) {
    this._gemstoneObjId = gemstoneObjId;
    this._serverId = 1;
    this._gemstoneCount = count;
    this._requiredGemstoneCount = count;
    this._result = 1;
  }

  protected void writeImpl() {
    this.writeEx(84);
    this.writeD(this._gemstoneObjId);
    this.writeD(this._serverId);
    this.writeD((int)this._gemstoneCount);
    this.writeD((int)this._requiredGemstoneCount);
    this.writeD(this._result);
  }
}
