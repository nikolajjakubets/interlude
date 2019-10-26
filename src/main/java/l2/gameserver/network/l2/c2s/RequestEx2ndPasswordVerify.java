//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordCheck;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordVerify;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordCheck.Ex2ndPasswordCheckResult;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult;

public class RequestEx2ndPasswordVerify extends L2GameClientPacket {
  private String _password;

  public RequestEx2ndPasswordVerify() {
  }

  protected void readImpl() {
    this._password = this.readS(8);
  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    SecondPasswordAuth spa = client.getSecondPasswordAuth();
    if (spa == null) {
      client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerifyResult.ERROR));
    } else if (!spa.isSecondPasswordSet()) {
      client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheckResult.CREATE));
    } else {
      if (spa.isValidSecondPassword(this._password)) {
        client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerifyResult.SUCCESS));
        client.setSecondPasswordAuthed(true);
      } else if (spa.isBlocked()) {
        client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerifyResult.BLOCK_HOMEPAGE));
      } else {
        client.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerifyResult.FAILED, spa.getTrysCount()));
      }

    }
  }
}
