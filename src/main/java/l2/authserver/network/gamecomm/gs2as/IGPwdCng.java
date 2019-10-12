package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.crypt.PasswordHash;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.as2gs.NotifyPwdCngResult;

public class IGPwdCng extends ReceivablePacket {
    private int _requestor_oid;
    private String _account;
    private String _old_pass;
    private String _new_pass;

    public IGPwdCng() {
    }

    protected void readImpl() {
        this._requestor_oid = this.readD();
        this._account = this.readS();
        this._old_pass = this.readS();
        this._new_pass = this.readS();
    }

    protected void runImpl() {
        try {
            Account acc = new Account(this._account);
            acc.restore();
            if (acc.getPasswordHash() == null) {
                this.sendPacket(new NotifyPwdCngResult(this._requestor_oid, 4));
                return;
            }

            if (!this._new_pass.matches(Config.APASSWD_TEMPLATE)) {
                this.sendPacket(new NotifyPwdCngResult(this._requestor_oid, 3));
                return;
            }

            boolean passwordCorrect = Config.DEFAULT_CRYPT.compare(this._old_pass, acc.getPasswordHash());
            if (!passwordCorrect) {
                PasswordHash[] var3 = Config.LEGACY_CRYPT;
                int var4 = var3.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    PasswordHash c = var3[var5];
                    if (c.compare(this._old_pass, acc.getPasswordHash())) {
                        passwordCorrect = true;
                        break;
                    }
                }
            }

            if (!passwordCorrect) {
                this.sendPacket(new NotifyPwdCngResult(this._requestor_oid, 2));
                return;
            }

            acc.setPasswordHash(Config.DEFAULT_CRYPT.encrypt(this._new_pass));
            acc.update();
            this.sendPacket(new NotifyPwdCngResult(this._requestor_oid, 1));
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }
}
