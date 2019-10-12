package com.lineage2.interlude.network.l2.s2c;

public final class LoginOk extends L2LoginServerPacket {
    private int _loginOk1;
    private int _loginOk2;

    public LoginOk(SessionKey sessionKey) {
        this._loginOk1 = sessionKey.loginOkID1;
        this._loginOk2 = sessionKey.loginOkID2;
    }

    protected void writeImpl() {
        this.writeC(3);
        this.writeD(this._loginOk1);
        this.writeD(this._loginOk2);
        this.writeD(0);
        this.writeD(0);
        this.writeD(1002);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeB(new byte[16]);
    }
}
