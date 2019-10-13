//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.door;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.instances.DoorInstance;

public interface OnOpenCloseListener extends CharListener {
  void onOpen(DoorInstance var1);

  void onClose(DoorInstance var1);
}
