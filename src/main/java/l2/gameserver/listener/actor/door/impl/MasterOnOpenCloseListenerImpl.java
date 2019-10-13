//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.door.impl;

import l2.gameserver.listener.actor.door.OnOpenCloseListener;
import l2.gameserver.model.instances.DoorInstance;

public class MasterOnOpenCloseListenerImpl implements OnOpenCloseListener {
  private DoorInstance _door;

  public MasterOnOpenCloseListenerImpl(DoorInstance door) {
    this._door = door;
  }

  public void onOpen(DoorInstance doorInstance) {
    this._door.openMe();
  }

  public void onClose(DoorInstance doorInstance) {
    this._door.closeMe();
  }
}
