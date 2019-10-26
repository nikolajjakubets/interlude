//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.mapregion;

import java.util.Map;
import l2.gameserver.model.Territory;
import l2.gameserver.model.base.Race;

public class RestartArea implements RegionData {
  private final Territory _territory;
  private final Map<Race, RestartPoint> _restarts;

  public RestartArea(Territory territory, Map<Race, RestartPoint> restarts) {
    this._territory = territory;
    this._restarts = restarts;
  }

  public Territory getTerritory() {
    return this._territory;
  }

  public Map<Race, RestartPoint> getRestartPoint() {
    return this._restarts;
  }
}
