//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.SessionKey;

public final class LoginOk extends L2LoginServerPacket {
    private int loginOk1;
    private int loginOk2;

    public LoginOk(SessionKey sessionKey) {
        this.loginOk1 = sessionKey.loginOkID1;
        this.loginOk2 = sessionKey.loginOkID2;
    }

    protected void writeImpl() {
        this.writeC(3);
        this.writeD(this.loginOk1);
        this.writeD(this.loginOk2);
        this.writeD(0);
        this.writeD(0);
        this.writeD(1002);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeB(new byte[16]);
    }
}
