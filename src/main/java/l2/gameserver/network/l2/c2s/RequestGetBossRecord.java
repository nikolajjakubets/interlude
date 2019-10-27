//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExGetBossRecord;
import l2.gameserver.network.l2.s2c.ExGetBossRecord.BossRecordInfo;

public class RequestGetBossRecord extends L2GameClientPacket {
  private int _bossID;

  public RequestGetBossRecord() {
  }

  protected void readImpl() {
    this._bossID = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    int totalPoints = 0;
    int ranking = 0;
    if (activeChar != null) {
      List<BossRecordInfo> list = new ArrayList<>();
      Map<Integer, Integer> points = RaidBossSpawnManager.getInstance().getPointsForOwnerId(activeChar.getObjectId());
      if (points != null && !points.isEmpty()) {
        Iterator var6 = points.entrySet().iterator();

        while(var6.hasNext()) {
          Entry<Integer, Integer> e = (Entry)var6.next();
          switch((Integer)e.getKey()) {
            case -1:
              ranking = (Integer)e.getValue();
              break;
            case 0:
              totalPoints = (Integer)e.getValue();
              break;
            default:
              list.add(new BossRecordInfo((Integer)e.getKey(), (Integer)e.getValue(), 0));
          }
        }
      }

      activeChar.sendPacket(new ExGetBossRecord(ranking, totalPoints, list));
    }
  }
}
