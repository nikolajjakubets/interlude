package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.L2LoginClient;

public final class Init extends L2LoginServerPacket {
    private int _sessionId;
    private byte[] _publicKey;
    private byte[] _blowfishKey;

    public Init(L2LoginClient client) {
        this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
    }

    public Init(byte[] publickey, byte[] blowfishkey, int sessionId) {
        this._sessionId = sessionId;
        this._publicKey = publickey;
        this._blowfishKey = blowfishkey;
    }

    protected void writeImpl() {
        this.writeC(0);
        this.writeD(this._sessionId);
        this.writeD(50721);
        this.writeB(this._publicKey);
        this.writeD(702387534);
        this.writeD(2009308412);
        this.writeD(-1750223328);
        this.writeD(129884407);
        this.writeB(this._blowfishKey);
        this.writeC(0);
    }
}
