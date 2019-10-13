//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import java.util.Set;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2.gameserver.utils.Location;

public class SiegeToggleNpcObject implements SpawnableObject {
  private SiegeToggleNpcInstance _toggleNpc;
  private Location _location;

  public SiegeToggleNpcObject(int id, int fakeNpcId, Location loc, int hp, Set<String> set) {
    this._location = loc;
    this._toggleNpc = (SiegeToggleNpcInstance)NpcHolder.getInstance().getTemplate(id).getNewInstance();
    this._toggleNpc.initFake(fakeNpcId);
    this._toggleNpc.setMaxHp(hp);
    this._toggleNpc.setZoneList(set);
  }

  public void spawnObject(GlobalEvent event) {
    this._toggleNpc.decayFake();
    if (event.isInProgress()) {
      this._toggleNpc.addEvent(event);
    } else {
      this._toggleNpc.removeEvent(event);
    }

    this._toggleNpc.setCurrentHp((double)this._toggleNpc.getMaxHp(), true);
    this._toggleNpc.spawnMe(this._location);
  }

  public void despawnObject(GlobalEvent event) {
    this._toggleNpc.removeEvent(event);
    this._toggleNpc.decayFake();
    this._toggleNpc.decayMe();
  }

  public void refreshObject(GlobalEvent event) {
  }

  public SiegeToggleNpcInstance getToggleNpc() {
    return this._toggleNpc;
  }

  public boolean isAlive() {
    return this._toggleNpc.isVisible();
  }
}
