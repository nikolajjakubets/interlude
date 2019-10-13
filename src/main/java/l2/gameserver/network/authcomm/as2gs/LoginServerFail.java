//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServerFail extends ReceivablePacket {
  private static final Logger _log = LoggerFactory.getLogger(LoginServerFail.class);
  private static final String[] reasons = new String[]{"none", "IP banned", "IP reserved", "wrong hexid", "ID reserved", "no free ID", "not authed", "already logged in"};
  private int _reason;

  public LoginServerFail() {
  }

  public String getReason() {
    return reasons[this._reason];
  }

  protected void readImpl() {
    this._reason = this.readC();
  }

  protected void runImpl() {
    _log.warn("Authserver registration failed! Reason: " + this.getReason());
    AuthServerCommunication.getInstance().restart();
  }
}
