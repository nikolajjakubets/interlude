package l2.authserver.network.l2;

import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.crypt.LoginCrypt;
import l2.authserver.crypt.ScrambledKeyPair;
import l2.authserver.network.l2.s2c.AccountKicked;
import l2.authserver.network.l2.s2c.AccountKicked.AccountKickedReason;
import l2.authserver.network.l2.s2c.L2LoginServerPacket;
import l2.authserver.network.l2.s2c.LoginFail;
import l2.authserver.network.l2.s2c.LoginFail.LoginFailReason;
import l2.commons.net.nio.impl.MMOClient;
import l2.commons.net.nio.impl.MMOConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;

@Slf4j
public final class L2LoginClient extends MMOClient<MMOConnection<L2LoginClient>> {
    private LoginClientState state;
    private LoginCrypt loginCrypt;
    private ScrambledKeyPair scrambledKeyPair;
    private byte[] blowfishKey;
    private String login;
    private SessionKey skey;
    private Account account;
    private String ipAddr;
    private int sessionId;

    public L2LoginClient(MMOConnection<L2LoginClient> con) {
        super(con);
        this.state = L2LoginClient.LoginClientState.CONNECTED;
        this.scrambledKeyPair = Config.getScrambledRSAKeyPair();
        this.blowfishKey = Config.getBlowfishKey();
        this.loginCrypt = new LoginCrypt();
        this.loginCrypt.setKey(this.blowfishKey);
        this.sessionId = con.hashCode();
        this.ipAddr = this.getConnection().getSocket().getInetAddress().getHostAddress();
    }

    public boolean decrypt(ByteBuffer buf, int size) {
        boolean ret;
        try {
            ret = this.loginCrypt.decrypt(buf.array(), buf.position(), size);
        } catch (IOException e) {
            log.error("decrypt: eMessage={}, eClass={}", e.getMessage(), e.getClass());
            this.closeNow(true);
            return false;
        }

        if (!ret) {
            this.closeNow(true);
        }

        return ret;
    }

    public boolean encrypt(ByteBuffer buf, int size) {
        int offset = buf.position();

        try {
            size = this.loginCrypt.encrypt(buf.array(), offset, size);
        } catch (IOException e) {
            log.error("encrypt: eMessage={}, eClass={}", e.getMessage(), e.getClass());
            return false;
        }

        buf.position(offset + size);
        return true;
    }

    public L2LoginClient.LoginClientState getState() {
        return this.state;
    }

    public void setState(L2LoginClient.LoginClientState state) {
        this.state = state;
    }

    public byte[] getBlowfishKey() {
        return this.blowfishKey;
    }

    public byte[] getScrambledModulus() {
        return this.scrambledKeyPair.getScrambledModulus();
    }

    public RSAPrivateKey getRSAPrivateKey() {
        return (RSAPrivateKey) this.scrambledKeyPair.getKeyPair().getPrivate();
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public SessionKey getSessionKey() {
        return this.skey;
    }

    public void setSessionKey(SessionKey skey) {
        this.skey = skey;
    }

    public void setSessionId(int val) {
        this.sessionId = val;
    }

    public int getSessionId() {
        return this.sessionId;
    }

    public void sendPacket(L2LoginServerPacket lsp) {
        if (this.isConnected()) {
            this.getConnection().sendPacket(lsp);
        }

    }

    public void close(LoginFailReason reason) {
        if (this.isConnected()) {
            this.getConnection().close(new LoginFail(reason));
        }

    }

    public void close(AccountKickedReason reason) {
        if (this.isConnected()) {
            this.getConnection().close(new AccountKicked(reason));
        }

    }

    public void close(L2LoginServerPacket lsp) {
        if (this.isConnected()) {
            this.getConnection().close(lsp);
        }

    }

    public void onDisconnection() {
        this.state = L2LoginClient.LoginClientState.DISCONNECTED;
        this.skey = null;
        this.loginCrypt = null;
        this.scrambledKeyPair = null;
        this.blowfishKey = null;
    }

    public String toString() {
        if (this.state == LoginClientState.AUTHED) {
            return "[ Account : " + this.getLogin() + " IP: " + this.getIpAddress() + "]";
        }
        return "[ State : " + this.getState() + " IP: " + this.getIpAddress() + "]";
    }

    public String getIpAddress() {
        return this.ipAddr;
    }

    protected void onForcedDisconnection() {
    }

    public enum LoginClientState {
        CONNECTED,
        AUTHED_GG,
        AUTHED,
        DISCONNECTED;

        private LoginClientState() {
        }
    }
}
