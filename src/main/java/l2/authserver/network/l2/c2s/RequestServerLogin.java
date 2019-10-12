package l2.authserver.network.l2.c2s;

import l2.authserver.GameServerManager;
import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.SessionKey;
import l2.authserver.network.l2.s2c.PlayOk;
import l2.authserver.network.l2.s2c.LoginFail.LoginFailReason;

public class RequestServerLogin extends L2LoginClientPacket {
    private int _loginOkID1;
    private int _loginOkID2;
    private int _serverId;

    public RequestServerLogin() {
    }

    protected void readImpl() {
        this._loginOkID1 = this.readD();
        this._loginOkID2 = this.readD();
        this._serverId = this.readC();
    }

    protected void runImpl() {
        L2LoginClient client = (L2LoginClient)this.getClient();
        SessionKey skey = client.getSessionKey();
        if (skey != null && skey.checkLoginPair(this._loginOkID1, this._loginOkID2)) {
            Account account = client.getAccount();
            GameServer gs = GameServerManager.getInstance().getGameServerById(this._serverId);
            if (gs == null) {
                ProxyServer ps = GameServerManager.getInstance().getProxyServerById(this._serverId);
                if (ps != null) {
                    gs = GameServerManager.getInstance().getGameServerById(ps.getOrigServerId());
                }
            }

            if (gs == null || !gs.isAuthed() || gs.getOnline() >= gs.getMaxPlayers() && account.getAccessLevel() < 50) {
                client.close(LoginFailReason.REASON_ACCESS_FAILED);
            } else if (gs.isGmOnly() && account.getAccessLevel() < 100) {
                client.close(LoginFailReason.REASON_SERVER_MAINTENANCE);
            } else {
                account.setLastServer(this._serverId);
                account.update();
                client.close(new PlayOk(skey));
            }
        } else {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
        }
    }
}
