//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordCheck;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordCheck.Ex2ndPasswordCheckResult;

public class RequestEx2ndPasswordCheck extends L2GameClientPacket {
  public RequestEx2ndPasswordCheck() {
  }

  protected void readImpl() {
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    if (!Config.USE_SECOND_PASSWORD_AUTH) {
      client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheckResult.SUCCESS));
    } else {
      SecondPasswordAuth spa = client.getSecondPasswordAuth();
      if (spa == null) {
        client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheckResult.ERROR));
      } else if (!spa.isSecondPasswordSet()) {
        client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheckResult.CREATE));
      } else if (spa.isBlocked()) {
        client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheckResult.BLOCK_TIME, spa.getBlockTimeLeft()));
      } else if (client.isSecondPasswordAuthed()) {
        client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheckResult.SUCCESS));
      } else {
        client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheckResult.CHECK));
      }
    }
  }
}
