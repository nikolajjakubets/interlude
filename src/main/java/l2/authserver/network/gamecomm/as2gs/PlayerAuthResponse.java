package l2.authserver.network.gamecomm.as2gs;

import l2.authserver.accounts.Account;
import l2.authserver.accounts.SessionManager.Session;
import l2.authserver.network.gamecomm.SendablePacket;
import l2.authserver.network.l2.SessionKey;

public class PlayerAuthResponse extends SendablePacket {
    private String login;
    private boolean authed;
    private int playOkID1;
    private int playOkID2;
    private int loginOkID1;
    private int loginOkID2;
    private double bonus;
    private int bonusExpire;
    private int lastServerId;

    public PlayerAuthResponse(Session session, boolean authed, int lastServer) {
        Account account = session.getAccount();
        this.login = account.getLogin();
        this.authed = authed;
        if (authed) {
            SessionKey skey = session.getSessionKey();
            this.playOkID1 = skey.playOkID1;
            this.playOkID2 = skey.playOkID2;
            this.loginOkID1 = skey.loginOkID1;
            this.loginOkID2 = skey.loginOkID2;
            this.lastServerId = lastServer;
        }

    }

    public PlayerAuthResponse(String account) {
        this.login = account;
        this.authed = false;
    }

    protected void writeImpl() {
        this.writeC(2);
        this.writeS(this.login);
        this.writeC(this.authed ? 1 : 0);
        if (this.authed) {
            this.writeD(this.playOkID1);
            this.writeD(this.playOkID2);
            this.writeD(this.loginOkID1);
            this.writeD(this.loginOkID2);
            this.writeD(this.lastServerId);
        }

    }
}
