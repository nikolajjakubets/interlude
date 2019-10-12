package com.lineage2.interlude.network.l2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;
import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.crypt.LoginCrypt;
import l2.authserver.crypt.ScrambledKeyPair;
import l2.authserver.network.l2.s2c.AccountKicked;
import l2.authserver.network.l2.s2c.L2LoginServerPacket;
import l2.authserver.network.l2.s2c.LoginFail;
import l2.authserver.network.l2.s2c.AccountKicked.AccountKickedReason;
import l2.authserver.network.l2.s2c.LoginFail.LoginFailReason;
import l2.commons.net.nio.impl.MMOClient;
import l2.commons.net.nio.impl.MMOConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class L2LoginClient extends MMOClient<MMOConnection<L2LoginClient>> {
    private static final Logger _log = LoggerFactory.getLogger(L2LoginClient.class);
    private L2LoginClient.LoginClientState _state;
    private LoginCrypt _loginCrypt;
    private ScrambledKeyPair _scrambledPair;
    private byte[] _blowfishKey;
    private String _login;
    private SessionKey _skey;
    private Account _account;
    private String _ipAddr;
    private int _sessionId;

    public L2LoginClient(MMOConnection<L2LoginClient> con) {
        super(con);
        this._state = L2LoginClient.LoginClientState.CONNECTED;
        this._scrambledPair = Config.getScrambledRSAKeyPair();
        this._blowfishKey = Config.getBlowfishKey();
        this._loginCrypt = new LoginCrypt();
        this._loginCrypt.setKey(this._blowfishKey);
        this._sessionId = con.hashCode();
        this._ipAddr = this.getConnection().getSocket().getInetAddress().getHostAddress();
    }

    public boolean decrypt(ByteBuffer buf, int size) {
        boolean ret;
        try {
            ret = this._loginCrypt.decrypt(buf.array(), buf.position(), size);
        } catch (IOException var5) {
            _log.error("", var5);
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
            size = this._loginCrypt.encrypt(buf.array(), offset, size);
        } catch (IOException var5) {
            _log.error("", var5);
            return false;
        }

        buf.position(offset + size);
        return true;
    }

    public L2LoginClient.LoginClientState getState() {
        return this._state;
    }

    public void setState(L2LoginClient.LoginClientState state) {
        this._state = state;
    }

    public byte[] getBlowfishKey() {
        return this._blowfishKey;
    }

    public byte[] getScrambledModulus() {
        return this._scrambledPair.getScrambledModulus();
    }

    public RSAPrivateKey getRSAPrivateKey() {
        return (RSAPrivateKey)this._scrambledPair.getKeyPair().getPrivate();
    }

    public String getLogin() {
        return this._login;
    }

    public void setLogin(String login) {
        this._login = login;
    }

    public Account getAccount() {
        return this._account;
    }

    public void setAccount(Account account) {
        this._account = account;
    }

    public SessionKey getSessionKey() {
        return this._skey;
    }

    public void setSessionKey(SessionKey skey) {
        this._skey = skey;
    }

    public void setSessionId(int val) {
        this._sessionId = val;
    }

    public int getSessionId() {
        return this._sessionId;
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
        this._state = L2LoginClient.LoginClientState.DISCONNECTED;
        this._skey = null;
        this._loginCrypt = null;
        this._scrambledPair = null;
        this._blowfishKey = null;
    }

    public String toString() {
        switch(this._state) {
            case AUTHED:
                return "[ Account : " + this.getLogin() + " IP: " + this.getIpAddress() + "]";
            default:
                return "[ State : " + this.getState() + " IP: " + this.getIpAddress() + "]";
        }
    }

    public String getIpAddress() {
        return this._ipAddr;
    }

    protected void onForcedDisconnection() {
    }

    public static enum LoginClientState {
        CONNECTED,
        AUTHED_GG,
        AUTHED,
        DISCONNECTED;

        private LoginClientState() {
        }
    }
}
