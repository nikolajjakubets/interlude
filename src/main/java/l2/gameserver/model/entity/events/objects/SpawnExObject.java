//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.NpcInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SpawnExObject implements SpawnableObject {
  private final List<Spawner> _spawns;
  private boolean _spawned;
  private String _name;

  public SpawnExObject(String name) {
    this._name = name;
    this._spawns = SpawnManager.getInstance().getSpawners(this._name);
    if (this._spawns.isEmpty() && Config.ALT_DEBUG_ENABLED) {
      log.info("SpawnExObject: not found spawn group: " + name);
    }

  }

  public void spawnObject(GlobalEvent event) {
    if (this._spawned) {
      log.info("SpawnExObject: can't spawn twice: " + this._name + "; event: " + event, new Exception());
    } else {

      for (Spawner spawn : this._spawns) {
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

      for (Spawner spawn : this._spawns) {
        spawn.removeEvent(event);
        spawn.deleteAll();
      }

    }
  }

  public void refreshObject(GlobalEvent event) {

    for (NpcInstance npc : this.getAllSpawned()) {
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
    List<NpcInstance> npcs = new ArrayList<>();

    for (Spawner spawn : this._spawns) {
      npcs.addAll(spawn.getAllSpawned());
    }

    return npcs.isEmpty() ? Collections.emptyList() : npcs;
  }

  public NpcInstance getFirstSpawned() {
    List<NpcInstance> npcs = this.getAllSpawned();
    return npcs.size() > 0 ? (NpcInstance) npcs.get(0) : null;
  }

  public boolean isSpawned() {
    return this._spawned;
  }
}
