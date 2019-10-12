package com.lineage2.interlude.network.gamecomm.gs2as;

import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeAccessLevel extends ReceivablePacket {
    public static final Logger _log = LoggerFactory.getLogger(ChangeAccessLevel.class);
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
