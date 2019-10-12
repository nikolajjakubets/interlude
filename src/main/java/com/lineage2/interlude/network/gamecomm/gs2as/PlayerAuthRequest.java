package com.lineage2.interlude.network.gamecomm.gs2as;

import l2.authserver.accounts.SessionManager;
import l2.authserver.accounts.SessionManager.Session;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.as2gs.PlayerAuthResponse;
import l2.authserver.network.l2.SessionKey;

public class PlayerAuthRequest extends ReceivablePacket {
    private String account;
    private int playOkId1;
    private int playOkId2;
    private int loginOkId1;
    private int loginOkId2;

    public PlayerAuthRequest() {
    }

    protected void readImpl() {
        this.account = this.readS();
        this.playOkId1 = this.readD();
        this.playOkId2 = this.readD();
        this.loginOkId1 = this.readD();
        this.loginOkId2 = this.readD();
    }

    protected void runImpl() {
        SessionKey skey = new SessionKey(this.loginOkId1, this.loginOkId2, this.playOkId1, this.playOkId2);
        Session session = SessionManager.getInstance().closeSession(skey);
        if (session != null && session.getAccount().getLogin().equals(this.account)) {
            this.sendPacket(new PlayerAuthResponse(session, session.getSessionKey().equals(skey), session.getAccount().getLastServer()));
        } else {
            this.sendPacket(new PlayerAuthResponse(this.account));
        }
    }
}
