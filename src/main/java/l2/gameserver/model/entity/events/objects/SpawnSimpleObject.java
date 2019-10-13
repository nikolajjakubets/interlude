//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;

public class SpawnSimpleObject implements SpawnableObject {
  private int _npcId;
  private Location _loc;
  private NpcInstance _npc;

  public SpawnSimpleObject(int npcId, Location loc) {
    this._npcId = npcId;
    this._loc = loc;
  }

  public void spawnObject(GlobalEvent event) {
    this._npc = NpcUtils.spawnSingle(this._npcId, this._loc, event.getReflection());
    this._npc.addEvent(event);
  }

  public void despawnObject(GlobalEvent event) {
    this._npc.removeEvent(event);
    this._npc.deleteMe();
  }

  public void refreshObject(GlobalEvent event) {
  }
}
