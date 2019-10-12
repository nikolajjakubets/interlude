package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;

public class PlayerLogout extends ReceivablePacket {
    private String account;

    public PlayerLogout() {
    }

    protected void readImpl() {
        this.account = this.readS();
    }

    protected void runImpl() {
        GameServer gs = this.getGameServer();
        if (gs.isAuthed()) {
            gs.removeAccount(this.account);
        }
    }
}
