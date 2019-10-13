//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.zone;

import l2.commons.listener.Listener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener extends Listener<Zone> {
  void onZoneEnter(Zone var1, Creature var2);

  void onZoneLeave(Zone var1, Creature var2);
}
