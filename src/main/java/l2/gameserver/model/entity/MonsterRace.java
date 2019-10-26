//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity;

import java.lang.reflect.Constructor;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonsterRace {
  private static final Logger _log = LoggerFactory.getLogger(MonsterRace.class);
  private NpcInstance[] monsters = new NpcInstance[8];
  private static MonsterRace _instance;
  private Constructor<?> _constructor;
  private int[][] speeds = new int[8][20];
  private int[] first = new int[2];
  private int[] second = new int[2];

  private MonsterRace() {
  }

  public static MonsterRace getInstance() {
    if (_instance == null) {
      _instance = new MonsterRace();
    }

    return _instance;
  }

  public void newRace() {
//    int random = false;

    for(int i = 0; i < 8; ++i) {
      int id = 31003;
      int random = Rnd.get(24);

      for(int j = i - 1; j >= 0; --j) {
        if (this.monsters[j].getTemplate().npcId == id + random) {
          random = Rnd.get(24);
        }
      }

      try {
        NpcTemplate template = NpcHolder.getInstance().getTemplate(id + random);
        this._constructor = template.getInstanceConstructor();
        int objectId = IdFactory.getInstance().getNextId();
        this.monsters[i] = (NpcInstance)this._constructor.newInstance(objectId, template);
      } catch (Exception var6) {
        _log.error("", var6);
      }
    }

    this.newSpeeds();
  }

  public void newSpeeds() {
    this.speeds = new int[8][20];
//    int total = false;
    this.first[1] = 0;
    this.second[1] = 0;

    for(int i = 0; i < 8; ++i) {
      int total = 0;

      for(int j = 0; j < 20; ++j) {
        if (j == 19) {
          this.speeds[i][j] = 100;
        } else {
          this.speeds[i][j] = Rnd.get(65, 124);
        }

        total += this.speeds[i][j];
      }

      if (total >= this.first[1]) {
        this.second[0] = this.first[0];
        this.second[1] = this.first[1];
        this.first[0] = 8 - i;
        this.first[1] = total;
      } else if (total >= this.second[1]) {
        this.second[0] = 8 - i;
        this.second[1] = total;
      }
    }

  }

  public NpcInstance[] getMonsters() {
    return this.monsters;
  }

  public int[][] getSpeeds() {
    return this.speeds;
  }

  public int getFirstPlace() {
    return 8 - this.first[0];
  }

  public int getSecondPlace() {
    return 8 - this.second[0];
  }
}
