//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import l2.gameserver.network.authcomm.SessionKey;
import l2.gameserver.network.authcomm.gs2as.PlayerInGame;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.GameClient.GameClientState;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.LoginFail;
import l2.gameserver.network.l2.s2c.ServerClose;

public class PlayerAuthResponse extends ReceivablePacket {
  private String account;
  private boolean authed;
  private int playOkId1;
  private int playOkId2;
  private int loginOkId1;
  private int loginOkId2;
  private int lastServerId;

  public PlayerAuthResponse() {
    this.lastServerId = Config.REQUEST_ID;
  }

  public void readImpl() {
    this.account = this.readS();
    this.authed = this.readC() == 1;
    if (this.authed) {
      this.playOkId1 = this.readD();
      this.playOkId2 = this.readD();
      this.loginOkId1 = this.readD();
      this.loginOkId2 = this.readD();
      if (this.getAvaliableBytes() > 0) {
        this.lastServerId = this.readD();
      }
    }

  }

  protected void runImpl() {
    SessionKey skey = new SessionKey(this.loginOkId1, this.loginOkId2, this.playOkId1, this.playOkId2);
    GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(this.account);
    if (client != null) {
      if (this.authed && client.getSessionKey().equals(skey)) {
        client.setAuthed(true);
        client.setState(GameClientState.AUTHED);
        client.setServerId(this.lastServerId);
        GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
        if (!Config.ALLOW_MULILOGIN && oldClient != null) {
          oldClient.setAuthed(false);
          Player activeChar = oldClient.getActiveChar();
          if (activeChar != null) {
            activeChar.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
            activeChar.logout();
          } else {
            oldClient.close(ServerClose.STATIC);
          }
        }

        this.sendPacket(new PlayerInGame(client.getLogin()));
        CharacterSelectionInfo csi = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
        client.sendPacket(csi);
        client.setCharSelection(csi.getCharInfo());
      } else {
        client.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
      }

    }
  }
}
