package com.lineage2.interlude.network.gamecomm.as2gs;

import l2.authserver.network.gamecomm.SendablePacket;

public class KickPlayer extends SendablePacket {
    private String account;

    public KickPlayer(String login) {
        this.account = login;
    }

    protected void writeImpl() {
        this.writeC(3);
        this.writeS(this.account);
    }
}
