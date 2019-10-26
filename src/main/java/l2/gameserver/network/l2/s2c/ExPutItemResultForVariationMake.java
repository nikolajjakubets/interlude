//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExPutItemResultForVariationMake extends L2GameServerPacket {
  public static final ExPutItemResultForVariationMake FAIL_PACKET = new ExPutItemResultForVariationMake(0, false);
  private int _itemObjId;
  private int _serverId;
  private int _result;

  public ExPutItemResultForVariationMake(int itemObjId, boolean isSuccess) {
    this._itemObjId = itemObjId;
    this._serverId = 0;
    this._result = isSuccess ? 1 : 0;
  }

  protected void writeImpl() {
    this.writeEx(82);
    this.writeD(this._itemObjId);
    this.writeD(this._serverId);
    this.writeD(this._result);
  }
}
