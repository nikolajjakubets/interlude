package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.SessionKey;

public final class PlayOk extends L2LoginServerPacket {
    private int _playOk1;
    private int _playOk2;

    public PlayOk(SessionKey sessionKey) {
        this._playOk1 = sessionKey.playOkID1;
        this._playOk2 = sessionKey.playOkID2;
    }

    protected void writeImpl() {
        this.writeC(7);
        this.writeD(this._playOk1);
        this.writeD(this._playOk2);
    }
}