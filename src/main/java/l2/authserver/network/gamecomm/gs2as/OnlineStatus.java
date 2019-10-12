//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;

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
