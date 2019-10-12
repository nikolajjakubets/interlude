package com.lineage2.interlude.network.gamecomm.gs2as;


public class OnlineStatus extends ReceivablePacket {
    private boolean _online;

    public OnlineStatus() {
    }

    protected void readImpl() {
        this._online = this.readC() == 1;
    }

    protected void runImpl() {
        GameServer gameServer = this.getGameServer();
        if (gameServer.isAuthed()) {
            gameServer.setOnline(this._online);
        }
    }
}
