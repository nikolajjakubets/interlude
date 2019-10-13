//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import l2.gameserver.network.authcomm.gs2as.OnlineStatus;
import l2.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthResponse extends ReceivablePacket {
  private static final Logger _log = LoggerFactory.getLogger(AuthResponse.class);
  private int _serverId;
  private String _serverName;

  public AuthResponse() {
  }

  protected void readImpl() {
    this._serverId = this.readC();
    this._serverName = this.readS();
  }

  protected void runImpl() {
    _log.info("Registered on authserver as " + this._serverId + " [" + this._serverName + "]");
    this.sendPacket(new OnlineStatus(true));
    String[] accounts = AuthServerCommunication.getInstance().getAccounts();
    String[] var2 = accounts;
    int var3 = accounts.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      String account = var2[var4];
      this.sendPacket(new PlayerInGame(account));
    }

  }
}
