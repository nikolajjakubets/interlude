//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.data.xml.holder.StaticObjectHolder;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.StaticObjectInstance;

public class StaticObjectObject implements SpawnableObject {
  private int _uid;
  private StaticObjectInstance _instance;

  public StaticObjectObject(int id) {
    this._uid = id;
  }

  public void spawnObject(GlobalEvent event) {
    this._instance = StaticObjectHolder.getInstance().getObject(this._uid);
  }

  public void despawnObject(GlobalEvent event) {
  }

  public void refreshObject(GlobalEvent event) {
    if (!event.isInProgress()) {
      this._instance.removeEvent(event);
    } else {
      this._instance.addEvent(event);
    }

  }

  public void setMeshIndex(int id) {
    this._instance.setMeshIndex(id);
    this._instance.broadcastInfo(false);
  }

  public int getUId() {
    return this._uid;
  }
}
