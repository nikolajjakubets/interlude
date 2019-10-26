//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

public class ExRegenMax extends L2GameServerPacket {
  private double _max;
  private int _count;
  private int _time;
  public static final int POTION_HEALING_GREATER = 16457;
  public static final int POTION_HEALING_MEDIUM = 16440;
  public static final int POTION_HEALING_LESSER = 16416;

  public ExRegenMax(double max, int count, int time) {
    this._max = max * 0.66D;
    this._count = count;
    this._time = time;
  }

  protected void writeImpl() {
    this.writeEx(1);
    this.writeD(1);
    this.writeD(this._count);
    this.writeD(this._time);
    this.writeF(this._max);
  }
}
