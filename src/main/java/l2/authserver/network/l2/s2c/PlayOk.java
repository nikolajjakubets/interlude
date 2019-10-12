package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.SessionKey;

public final class PlayOk extends L2LoginServerPacket {
    private int playOk1;
    private int playOk2;

    public PlayOk(SessionKey sessionKey) {
        this.playOk1 = sessionKey.playOkID1;
        this.playOk2 = sessionKey.playOkID2;
    }

    protected void writeImpl() {
        this.writeC(7);
        this.writeD(this.playOk1);
        this.writeD(this.playOk2);
    }
}