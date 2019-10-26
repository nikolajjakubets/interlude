//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class EnchantResult extends L2GameServerPacket {
  private final int _resultId;
  private final int _crystalId;
  private final long _count;
  public static final EnchantResult SUCESS = new EnchantResult(0, 0, 0L);
  public static final EnchantResult CANCEL = new EnchantResult(2, 0, 0L);
  public static final EnchantResult BLESSED_FAILED = new EnchantResult(3, 0, 0L);
  public static final EnchantResult FAILED_NO_CRYSTALS = new EnchantResult(4, 0, 0L);
  public static final EnchantResult ANCIENT_FAILED = new EnchantResult(5, 0, 0L);

  public EnchantResult(int resultId, int crystalId, long count) {
    this._resultId = resultId;
    this._crystalId = crystalId;
    this._count = count;
  }

  protected final void writeImpl() {
    this.writeC(129);
    this.writeD(this._resultId);
    this.writeD(this._crystalId);
    this.writeQ(this._count);
  }
}
