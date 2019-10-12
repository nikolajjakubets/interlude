package com.lineage2.interlude.network.gamecomm.gs2as;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;

public class PlayerInGame extends ReceivablePacket {
    private String account;

    public PlayerInGame() {
    }

    protected void readImpl() {
        this.account = this.readS();
    }

    protected void runImpl() {
        GameServer gs = this.getGameServer();
        if (gs.isAuthed()) {
            gs.addAccount(this.account);
        }
    }
}