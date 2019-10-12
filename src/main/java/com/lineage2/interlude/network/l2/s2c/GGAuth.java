package com.lineage2.interlude.network.l2.s2c;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GGAuth extends L2LoginServerPacket {
    static Logger _log = LoggerFactory.getLogger(GGAuth.class);
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
