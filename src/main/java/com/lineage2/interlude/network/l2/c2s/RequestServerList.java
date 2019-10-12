package com.lineage2.interlude.network.l2.c2s;

import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.SessionKey;
import l2.authserver.network.l2.s2c.ServerList;
import l2.authserver.network.l2.s2c.LoginFail.LoginFailReason;

public class RequestServerList extends L2LoginClientPacket {
    private int _loginOkID1;
    private int _loginOkID2;

    public RequestServerList() {
    }

    protected void readImpl() {
        this._loginOkID1 = this.readD();
        this._loginOkID2 = this.readD();
        this.readC();
    }

    protected void runImpl() {
        L2LoginClient client = (L2LoginClient)this.getClient();
        SessionKey skey = client.getSessionKey();
        if (skey != null && skey.checkLoginPair(this._loginOkID1, this._loginOkID2)) {
            client.sendPacket(new ServerList(client.getAccount()));
        } else {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
        }
    }
}
