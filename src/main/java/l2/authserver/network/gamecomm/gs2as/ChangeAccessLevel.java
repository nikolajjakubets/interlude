package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.ReceivablePacket;

public class ChangeAccessLevel extends ReceivablePacket {
    private String account;
    private int level;
    private int banExpire;

    public ChangeAccessLevel() {
    }

    protected void readImpl() {
        this.account = this.readS();
        this.level = this.readD();
        this.banExpire = this.readD();
    }

    protected void runImpl() {
        Account acc = new Account(this.account);
        acc.restore();
        acc.setAccessLevel(this.level);
        acc.setBanExpire(this.banExpire);
        acc.update();
    }
}
