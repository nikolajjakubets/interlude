//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.usercommands.impl;

import java.util.Iterator;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class InstanceZone implements IUserCommandHandler {
  private static final int[] COMMAND_IDS = new int[]{114};

  public InstanceZone() {
  }

  public boolean useUserCommand(int id, Player activeChar) {
    if (COMMAND_IDS[0] != id) {
      return false;
    } else {
      if (activeChar.getActiveReflection() != null) {
        activeChar.sendPacket((new SystemMessage2(SystemMsg.INSTANT_ZONE_CURRENTLY_IN_USE_S1)).addInstanceName(activeChar.getActiveReflection().getInstancedZoneId()));
      }

      boolean noLimit = true;
      boolean showMsg = false;
      Iterator var6 = activeChar.getInstanceReuses().keySet().iterator();

      while(var6.hasNext()) {
        int i = (Integer)var6.next();
        int limit = InstantZoneHolder.getInstance().getMinutesToNextEntrance(i, activeChar);
        if (limit > 0) {
          noLimit = false;
          if (!showMsg) {
            activeChar.sendPacket(SystemMsg.INSTANCE_ZONE_TIME_LIMIT);
            showMsg = true;
          }

          activeChar.sendPacket(((SystemMessage2)((SystemMessage2)(new SystemMessage2(SystemMsg.S1_WILL_BE_AVAILABLE_FOR_REUSE_AFTER_S2_HOURS_S3_MINUTES)).addInstanceName(i)).addInteger((double)(limit / 60))).addInteger((double)(limit % 60)));
        }
      }

      if (noLimit) {
        activeChar.sendPacket(SystemMsg.THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT);
      }

      return true;
    }
  }

  public final int[] getUserCommandList() {
    return COMMAND_IDS;
  }
}
