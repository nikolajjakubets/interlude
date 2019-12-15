package l2.authserver.network.l2.request;

import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.L2LoginClient.LoginClientState;
import l2.authserver.network.l2.s2c.GGAuth;
import l2.authserver.network.l2.s2c.LoginFail.LoginFailReason;

public class AuthGameGuard extends L2LoginClientPacket {
    private int sessionId;

    public AuthGameGuard() {
    }

    protected void readImpl() {
        this.sessionId = this.readD();
    }

    protected void runImpl() {
        L2LoginClient client = this.getClient();
        if (this.sessionId != 0 && this.sessionId != client.getSessionId()) {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
        } else {
            client.setState(LoginClientState.AUTHED_GG);
            client.sendPacket(new GGAuth(client.getSessionId()));
        }

    }
}
