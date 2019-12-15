//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;

@Slf4j
public class StadiumPool {
  private static StadiumPool _instance;
  public static final int REFLECTION_COUNT = 22;
  private static final StadiumPool.StadiumTemplate[] OLY_STADIA_TEMPLATES = new StadiumPool.StadiumTemplate[]{new StadiumPool.StadiumTemplate(147, new Location(-20814, -21189, -3030))};
  private ArrayDeque<Stadium> _freeStadiums = new ArrayDeque<>();
  private Stadium[] _allStadiums = new Stadium[22];

  public static StadiumPool getInstance() {
    if (_instance == null) {
      _instance = new StadiumPool();
    }

    return _instance;
  }

  private StadiumPool() {
  }

  public Stadium[] getAllStadiums() {
    return this._allStadiums;
  }

  public void AllocateStadiums() {
    int cnt = 0;

    for(int i = 0; i < 22 / OLY_STADIA_TEMPLATES.length; ++i) {
      for (StadiumTemplate st : OLY_STADIA_TEMPLATES) {
        Stadium stadium = new Stadium(cnt, st.zid, st.oloc);
        this._allStadiums[cnt] = stadium;
        this._freeStadiums.addLast(stadium);
        ++cnt;
      }
    }

    log.info("OlyStadiumPool: allocated " + cnt + " stadiums.");
  }

  public void FreeStadiums() {
    for(int i = 0; i < this._allStadiums.length; ++i) {
      if (this._allStadiums[i] != null) {
        this._allStadiums[i].collapse();
        this._allStadiums[i] = null;
      }
    }

    this._freeStadiums.clear();
    log.info("OlyStadiumPool: stadiums cleared.");
  }

  public boolean isStadiumAvailable() {
    return this._freeStadiums.size() > 0;
  }

  public synchronized Stadium pollStadium() {
    Stadium stadium = (Stadium)this._freeStadiums.pollFirst();
    if (!stadium.isFree()) {
      log.warn("Poll used stadium");
      Thread.dumpStack();
      stadium = (Stadium)this._freeStadiums.pollFirst();
    }

    stadium.setFree(false);
    return stadium;
  }

  public synchronized void putStadium(Stadium stadium) {
    if (stadium.isFree()) {
      log.warn("Put free stadium");
      Thread.dumpStack();
    }

    stadium.clear();
    stadium.setFree(true);
    this._freeStadiums.addFirst(stadium);
  }

  public Stadium getStadium(int id) {
    return this._allStadiums[id];
  }

  private static class StadiumTemplate {
    public Location[] plocs;
    public Location[] blocs;
    public Location oloc;
    public int zid;

    public StadiumTemplate(int _zid, Location ol) {
      this.oloc = ol;
      this.zid = _zid;
    }
  }
}
