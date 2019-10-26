//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordAck;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordAck.Ex2ndPasswordAckResult;

public class RequestEx2ndPasswordReq extends L2GameClientPacket {
  private boolean _isChangePass;
  private String _password;
  private String _newPassword;

  public RequestEx2ndPasswordReq() {
  }

  protected void readImpl() {
    this._isChangePass = this.readC() == 2;
    this._password = this.readS(8);
    if (this._isChangePass) {
      this._newPassword = this.readS(8);
    }

  }

  protected void runImpl() {
    GameClient client = (GameClient)this.getClient();
    SecondPasswordAuth spa = client.getSecondPasswordAuth();
    if (spa == null) {
      client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.ERROR));
    } else {
      if (this._isChangePass) {
        if (spa.changePassword(this._password, this._newPassword)) {
          client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.SUCCESS_CREATE));
          return;
        }

        if (spa.isBlocked()) {
          client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.BLOCK_HOMEPAGE));
        } else {
          client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.FAIL_VERIFY, spa.getTrysCount()));
        }
      } else if (!spa.isSecondPasswordSet()) {
        spa.changePassword((String)null, this._password);
        client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.SUCCESS_CREATE));
      } else {
        if (spa.isValidSecondPassword(this._password)) {
          client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.SUCCESS_VERIFY));
          client.setSecondPasswordAuthed(true);
          return;
        }

        if (spa.isBlocked()) {
          client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.BLOCK_HOMEPAGE));
        } else {
          client.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAckResult.FAIL_VERIFY, spa.getTrysCount()));
        }
      }

    }
  }
}
