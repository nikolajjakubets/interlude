//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import java.util.ArrayDeque;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StadiumPool {
  private static final Logger _log = LoggerFactory.getLogger(StadiumPool.class);
  private static StadiumPool _instance;
  public static final int REFLECTION_COUNT = 22;
  private static final StadiumPool.StadiumTemplate[] OLY_STADIA_TEMPLATES = new StadiumPool.StadiumTemplate[]{new StadiumPool.StadiumTemplate(147, new Location(-20814, -21189, -3030))};
  private ArrayDeque<Stadium> _freeStadiums = new ArrayDeque();
  private Stadium[] _allStadiums = new Stadium[22];

  public static final StadiumPool getInstance() {
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
      StadiumPool.StadiumTemplate[] var3 = OLY_STADIA_TEMPLATES;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        StadiumPool.StadiumTemplate st = var3[var5];
        Stadium stadium = new Stadium(cnt, st.zid, st.oloc);
        this._allStadiums[cnt] = stadium;
        this._freeStadiums.addLast(stadium);
        ++cnt;
      }
    }

    _log.info("OlyStadiumPool: allocated " + cnt + " stadiums.");
  }

  public void FreeStadiums() {
    for(int i = 0; i < this._allStadiums.length; ++i) {
      if (this._allStadiums[i] != null) {
        this._allStadiums[i].collapse();
        this._allStadiums[i] = null;
      }
    }

    this._freeStadiums.clear();
    _log.info("OlyStadiumPool: stadiums cleared.");
  }

  public boolean isStadiumAvailable() {
    return this._freeStadiums.size() > 0;
  }

  public synchronized Stadium pollStadium() {
    Stadium stadium = (Stadium)this._freeStadiums.pollFirst();
    if (!stadium.isFree()) {
      _log.warn("Poll used stadium");
      Thread.dumpStack();
      stadium = (Stadium)this._freeStadiums.pollFirst();
    }

    stadium.setFree(false);
    return stadium;
  }

  public synchronized void putStadium(Stadium stadium) {
    if (stadium.isFree()) {
      _log.warn("Put free stadium");
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
