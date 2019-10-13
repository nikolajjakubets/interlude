//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnExObject implements SpawnableObject {
  private static final Logger _log = LoggerFactory.getLogger(SpawnExObject.class);
  private final List<Spawner> _spawns;
  private boolean _spawned;
  private String _name;

  public SpawnExObject(String name) {
    this._name = name;
    this._spawns = SpawnManager.getInstance().getSpawners(this._name);
    if (this._spawns.isEmpty() && Config.ALT_DEBUG_ENABLED) {
      _log.info("SpawnExObject: not found spawn group: " + name);
    }

  }

  public void spawnObject(GlobalEvent event) {
    if (this._spawned) {
      _log.info("SpawnExObject: can't spawn twice: " + this._name + "; event: " + event, new Exception());
    } else {
      Iterator var2 = this._spawns.iterator();

      while(var2.hasNext()) {
        Spawner spawn = (Spawner)var2.next();
        if (event.isInProgress()) {
          spawn.addEvent(event);
        } else {
          spawn.removeEvent(event);
        }

        spawn.setReflection(event.getReflection());
        spawn.init();
      }

      this._spawned = true;
    }

  }

  public void despawnObject(GlobalEvent event) {
    if (this._spawned) {
      this._spawned = false;
      Iterator var2 = this._spawns.iterator();

      while(var2.hasNext()) {
        Spawner spawn = (Spawner)var2.next();
        spawn.removeEvent(event);
        spawn.deleteAll();
      }

    }
  }

  public void refreshObject(GlobalEvent event) {
    Iterator var2 = this.getAllSpawned().iterator();

    while(var2.hasNext()) {
      NpcInstance npc = (NpcInstance)var2.next();
      if (event.isInProgress()) {
        npc.addEvent(event);
      } else {
        npc.removeEvent(event);
      }
    }

  }

  public List<Spawner> getSpawns() {
    return this._spawns;
  }

  public List<NpcInstance> getAllSpawned() {
    List<NpcInstance> npcs = new ArrayList();
    Iterator var2 = this._spawns.iterator();

    while(var2.hasNext()) {
      Spawner spawn = (Spawner)var2.next();
      npcs.addAll(spawn.getAllSpawned());
    }

    return (List)(npcs.isEmpty() ? Collections.emptyList() : npcs);
  }

  public NpcInstance getFirstSpawned() {
    List<NpcInstance> npcs = this.getAllSpawned();
    return npcs.size() > 0 ? (NpcInstance)npcs.get(0) : null;
  }

  public boolean isSpawned() {
    return this._spawned;
  }
}
