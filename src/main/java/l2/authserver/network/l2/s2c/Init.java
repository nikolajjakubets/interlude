package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.L2LoginClient;

public final class Init extends L2LoginServerPacket {
    private int sessionId;
    private byte[] publicKey;
    private byte[] blowfishKey;

    public Init(L2LoginClient client) {
        this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
    }

    private Init(byte[] publickey, byte[] blowfishkey, int sessionId) {
        this.sessionId = sessionId;
        this.publicKey = publickey;
        this.blowfishKey = blowfishkey;
    }

    protected void writeImpl() {
        this.writeC(0);
        this.writeD(this.sessionId);
        this.writeD(50721);
        this.writeB(this.publicKey);
        this.writeD(702387534);
        this.writeD(2009308412);
        this.writeD(-1750223328);
        this.writeD(129884407);
        this.writeB(this.blowfishKey);
        this.writeC(0);
    }
}
