//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.GlobalEvent;

public class ZoneObject implements InitableObject {
  private String _name;
  private Zone _zone;

  public ZoneObject(String name) {
    this._name = name;
  }

  public void initObject(GlobalEvent e) {
    Reflection r = e.getReflection();
    this._zone = r.getZone(this._name);
  }

  public void setActive(boolean a) {
    this._zone.setActive(a);
  }

  public void setActive(boolean a, GlobalEvent event) {
    this.setActive(a);
  }

  public Zone getZone() {
    return this._zone;
  }

  public List<Player> getInsidePlayers() {
    return this._zone.getInsidePlayers();
  }

  public boolean checkIfInZone(Creature c) {
    return this._zone.checkIfInZone(c);
  }
}
