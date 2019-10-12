package l2.authserver.network.l2.c2s;

import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.L2LoginClient.LoginClientState;
import l2.authserver.network.l2.s2c.GGAuth;
import l2.authserver.network.l2.s2c.LoginFail.LoginFailReason;

public class AuthGameGuard extends L2LoginClientPacket {
    private int _sessionId;

    public AuthGameGuard() {
    }

    protected void readImpl() {
        this._sessionId = this.readD();
    }

    protected void runImpl() {
        L2LoginClient client = (L2LoginClient)this.getClient();
        if (this._sessionId != 0 && this._sessionId != client.getSessionId()) {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
        } else {
            client.setState(LoginClientState.AUTHED_GG);
            client.sendPacket(new GGAuth(client.getSessionId()));
        }

    }
}
