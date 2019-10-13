//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.usercommands.impl;

import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.NoblesController.NobleRecord;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;

public class OlympiadStat implements IUserCommandHandler {
  private static final int[] COMMAND_IDS = new int[]{109};

  public OlympiadStat() {
  }

  public boolean useUserCommand(int id, Player activeChar) {
    if (id != COMMAND_IDS[0]) {
      return false;
    } else if (!activeChar.isNoble()) {
      activeChar.sendPacket(SystemMsg.THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE);
      return true;
    } else {
      NobleRecord nr = NoblesController.getInstance().getNobleRecord(activeChar.getObjectId());
      CustomMessage sm = new CustomMessage("Olympiad.stat", activeChar, new Object[0]);
      sm = sm.addNumber((long)Math.max(0, nr.comp_done));
      sm = sm.addNumber((long)Math.max(0, nr.comp_win));
      sm = sm.addNumber((long)Math.max(0, nr.comp_loose));
      sm = sm.addNumber((long)Math.max(0, nr.points_current));
      activeChar.sendMessage(sm);
      return true;
    }
  }

  public int[] getUserCommandList() {
    return COMMAND_IDS;
  }
}
