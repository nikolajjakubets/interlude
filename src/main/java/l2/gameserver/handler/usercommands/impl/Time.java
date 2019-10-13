//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.handler.usercommands.impl;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class Time implements IUserCommandHandler {
  private static final int[] COMMAND_IDS = new int[]{77};
  private static final NumberFormat df;
  private static final SimpleDateFormat sf;

  public Time() {
  }

  public boolean useUserCommand(int id, Player activeChar) {
    if (COMMAND_IDS[0] != id) {
      return false;
    } else {
      int h = GameTimeController.getInstance().getGameHour();
      int m = GameTimeController.getInstance().getGameMin();
      SystemMessage sm;
      if (GameTimeController.getInstance().isNowNight()) {
        sm = new SystemMessage(928);
      } else {
        sm = new SystemMessage(927);
      }

      sm.addString(df.format((long)h)).addString(df.format((long)m));
      activeChar.sendPacket(sm);
      if (Config.ALT_SHOW_SERVER_TIME) {
        activeChar.sendMessage(new CustomMessage("usercommandhandlers.Time.ServerTime", activeChar, new Object[]{sf.format(new Date(System.currentTimeMillis()))}));
      }

      return true;
    }
  }

  public final int[] getUserCommandList() {
    return COMMAND_IDS;
  }

  static {
    df = NumberFormat.getInstance(Locale.ENGLISH);
    sf = new SimpleDateFormat("H:mm");
    df.setMinimumIntegerDigits(2);
  }
}
