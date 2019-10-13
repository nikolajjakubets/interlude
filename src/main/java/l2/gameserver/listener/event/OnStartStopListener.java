//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.event;

import l2.gameserver.listener.EventListener;
import l2.gameserver.model.entity.events.GlobalEvent;

public interface OnStartStopListener extends EventListener {
  void onStart(GlobalEvent var1);

  void onStop(GlobalEvent var1);
}
