//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.mapregion;

import l2.gameserver.model.Territory;

public class DomainArea implements RegionData {
  private final int _id;
  private final Territory _territory;

  public DomainArea(int id, Territory territory) {
    this._id = id;
    this._territory = territory;
  }

  public int getId() {
    return this._id;
  }

  public Territory getTerritory() {
    return this._territory;
  }
}
