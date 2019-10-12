package l2.authserver.network.l2.s2c;

public final class GGAuth extends L2LoginServerPacket {
    public static int SKIP_GG_AUTH_REQUEST = 11;
    private int _response;

    public GGAuth(int response) {
        this._response = response;
    }

    protected void writeImpl() {
        this.writeC(11);
        this.writeD(this._response);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
    }
}
