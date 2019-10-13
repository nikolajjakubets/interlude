//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.events.objects;

import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.DoorInstance;

public class DoorObject implements SpawnableObject, InitableObject {
  private int _id;
  private DoorInstance _door;
  private boolean _weak;

  public DoorObject(int id) {
    this._id = id;
  }

  public void initObject(GlobalEvent e) {
    this._door = e.getReflection().getDoor(this._id);
  }

  public void spawnObject(GlobalEvent event) {
    this.refreshObject(event);
  }

  public void despawnObject(GlobalEvent event) {
    Reflection ref = event.getReflection();
    if (ref == ReflectionManager.DEFAULT) {
      this.refreshObject(event);
    }

  }

  public void refreshObject(GlobalEvent event) {
    if (!event.isInProgress()) {
      this._door.removeEvent(event);
    } else {
      this._door.addEvent(event);
    }

    if (this._door.getCurrentHp() <= 0.0D) {
      this._door.decayMe();
      this._door.spawnMe();
    }

    this._door.setCurrentHp((double)this._door.getMaxHp() * (this.isWeak() ? 0.5D : 1.0D), true);
    this.close(event);
  }

  public int getUId() {
    return this._door.getDoorId();
  }

  public int getUpgradeValue() {
    return this._door.getUpgradeHp();
  }

  public void setUpgradeValue(GlobalEvent event, int val) {
    this._door.setUpgradeHp(val);
    this.refreshObject(event);
  }

  public void open(GlobalEvent e) {
    this._door.openMe((Player)null, !e.isInProgress());
  }

  public void close(GlobalEvent e) {
    this._door.closeMe((Player)null, !e.isInProgress());
  }

  public DoorInstance getDoor() {
    return this._door;
  }

  public boolean isWeak() {
    return this._weak;
  }

  public void setWeak(boolean weak) {
    this._weak = weak;
  }
}
