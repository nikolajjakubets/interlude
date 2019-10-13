//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

public class CastleDamageZoneObject extends ZoneObject {
  private final long _price;

  public CastleDamageZoneObject(String name, long price) {
    super(name);
    this._price = price;
  }

  public long getPrice() {
    return this._price;
  }
}
