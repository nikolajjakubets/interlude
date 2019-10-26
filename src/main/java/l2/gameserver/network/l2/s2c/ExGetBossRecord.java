//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import java.util.List;

public class ExGetBossRecord extends L2GameServerPacket {
  private List<ExGetBossRecord.BossRecordInfo> _bossRecordInfo;
  private int _ranking;
  private int _totalPoints;

  public ExGetBossRecord(int ranking, int totalScore, List<ExGetBossRecord.BossRecordInfo> bossRecordInfo) {
    this._ranking = ranking;
    this._totalPoints = totalScore;
    this._bossRecordInfo = bossRecordInfo;
  }

  protected final void writeImpl() {
    this.writeEx(51);
    this.writeD(this._ranking);
    this.writeD(this._totalPoints);
    this.writeD(this._bossRecordInfo.size());
    Iterator var1 = this._bossRecordInfo.iterator();

    while(var1.hasNext()) {
      ExGetBossRecord.BossRecordInfo w = (ExGetBossRecord.BossRecordInfo)var1.next();
      this.writeD(w._bossId);
      this.writeD(w._points);
      this.writeD(w._unk1);
    }

  }

  public static class BossRecordInfo {
    public int _bossId;
    public int _points;
    public int _unk1;

    public BossRecordInfo(int bossId, int points, int unk1) {
      this._bossId = bossId;
      this._points = points;
      this._unk1 = unk1;
    }
  }
}
