//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import l2.gameserver.network.authcomm.gs2as.OnlineStatus;
import l2.gameserver.network.authcomm.gs2as.PlayerInGame;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class AuthResponse extends ReceivablePacket {
  private int _serverId;
  private String _serverName;

  public AuthResponse() {
  }

  protected void readImpl() {
    this._serverId = this.readC();
    this._serverName = this.readS();
  }

  protected void runImpl() {
    log.info("Registered on authserver as " + this._serverId + " [" + this._serverName + "]");
    this.sendPacket(new OnlineStatus(true));
    String[] accounts = AuthServerCommunication.getInstance().getAccounts();

    for (String account : accounts) {
      this.sendPacket(new PlayerInGame(account));
    }

  }
}
