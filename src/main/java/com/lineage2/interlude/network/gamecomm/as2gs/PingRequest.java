package com.lineage2.interlude.network.gamecomm.as2gs;

import l2.authserver.network.gamecomm.SendablePacket;

public class PingRequest extends SendablePacket {
    public PingRequest() {
    }

    protected void writeImpl() {
        this.writeC(255);
    }
}
