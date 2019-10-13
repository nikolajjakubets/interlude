//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.model.pledge.Clan;

public class AuctionSiegeClanObject extends SiegeClanObject {
  private long _bid;

  public AuctionSiegeClanObject(String type, Clan clan, long param) {
    this(type, clan, param, System.currentTimeMillis());
  }

  public AuctionSiegeClanObject(String type, Clan clan, long param, long date) {
    super(type, clan, param, date);
    this._bid = param;
  }

  public long getParam() {
    return this._bid;
  }

  public void setParam(long param) {
    this._bid = param;
  }
}
