//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.voicecommands.impl;

import java.util.Iterator;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class InstanceZone extends Functions implements IVoicedCommandHandler {
  private final String[] _commandList = new String[]{"instancezone"};

  public InstanceZone() {
  }

  public boolean useVoicedCommand(String command, Player activeChar, String args) {
    if (activeChar == null) {
      return false;
    } else {
      if (activeChar.getActiveReflection() != null) {
        activeChar.sendMessage((new CustomMessage("INSTANT_ZONE_CURRENTLY_IN_USE_S1", activeChar, new Object[0])).addString(activeChar.getActiveReflection().getName()));
      }

      boolean noLimit = true;
      boolean showMsg = false;
      Iterator var7 = activeChar.getInstanceReuses().keySet().iterator();

      while(var7.hasNext()) {
        int i = (Integer)var7.next();
        int limit = InstantZoneHolder.getInstance().getMinutesToNextEntrance(i, activeChar);
        if (limit > 0) {
          noLimit = false;
          if (!showMsg) {
            activeChar.sendMessage(new CustomMessage("INSTANCE_ZONE_TIME_LIMIT", activeChar, new Object[0]));
            showMsg = true;
          }

          activeChar.sendMessage((new CustomMessage("S1_WILL_BE_AVAILABLE_FOR_REUSE_AFTER_S2_HOURS_S3_MINUTES", activeChar, new Object[0])).addNumber((long)i).addNumber((long)(limit / 60)).addNumber((long)(limit % 60)));
        }
      }

      if (noLimit) {
        activeChar.sendMessage(new CustomMessage("THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT", activeChar, new Object[0]));
      }

      return true;
    }
  }

  public String[] getVoicedCommandList() {
    return this._commandList;
  }
}
