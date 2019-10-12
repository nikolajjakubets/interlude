package l2.authserver.accounts;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import l2.authserver.database.L2DatabaseFactory;
import l2.commons.dbutils.DbUtils;
import l2.commons.net.utils.NetList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Account {
    private static final Logger _log = LoggerFactory.getLogger(Account.class);
    private static final String SQLP_ACCOUNT_LOAD = "{CALL `lip_AccountLoad`(?)}";
    private static final String SQLP_ACCOUNT_CREATE = "{CALL `lip_AccountCreate`(?, ?)}";
    private static final String SQLP_ACCOUNT_UPDATE = "{CALL `lip_AccountUpdate`(?, ?, ?, ?, ?, ?, ?)}";
    private final String login;
    private String passwordHash;
    private NetList allowedIpList = new NetList();
    private int accessLevel;
    private int banExpire;
    private String lastIP;
    private int lastAccess;
    private int lastServer;
    private String email;

    public Account(String login) {
        this.login = login;
    }

    public String getLogin() {
        return this.login;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String val) {
        this.email = val;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isAllowedIP(String ip) {
        return this.allowedIpList.isEmpty() || this.allowedIpList.isInRange(ip);
    }

    public int getAccessLevel() {
        return this.accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public int getBanExpire() {
        return this.banExpire;
    }

    public void setBanExpire(int banExpire) {
        this.banExpire = banExpire;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public String getLastIP() {
        return this.lastIP;
    }

    public int getLastAccess() {
        return this.lastAccess;
    }

    public void setLastAccess(int lastAccess) {
        this.lastAccess = lastAccess;
    }

    public int getLastServer() {
        return this.lastServer;
    }

    public void setLastServer(int lastServer) {
        this.lastServer = lastServer;
    }

    public String toString() {
        return this.login;
    }

    public void restore() {
        Connection con = null;
        CallableStatement cstmt = null;
        ResultSet rset = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            cstmt = con.prepareCall("{CALL `lip_AccountLoad`(?)}");
            cstmt.setString(1, this.login);
            rset = cstmt.executeQuery();
            if (rset.next()) {
                this.setPasswordHash(rset.getString("password").trim());
                this.setAccessLevel(rset.getInt("accessLevel"));
                this.setLastServer(rset.getInt("lastServerId"));
                this.setLastIP(rset.getString("lastIP"));
                this.setLastAccess(rset.getInt("lastactive"));
                this.setEmail(rset.getString("email"));
            }
        } catch (Exception var8) {
            _log.error("", var8);
        } finally {
            DbUtils.closeQuietly(con, cstmt, rset);
        }

    }

    public void save() {
        Connection con = null;
        CallableStatement cstmt = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            cstmt = con.prepareCall("{CALL `lip_AccountCreate`(?, ?)}");
            cstmt.setString(1, this.getLogin());
            cstmt.setString(2, this.getPasswordHash());
            cstmt.execute();
        } catch (Exception var7) {
            _log.error("", var7);
        } finally {
            DbUtils.closeQuietly(con, cstmt);
        }

    }

    public void update() {
        Connection con = null;
        CallableStatement cstmt = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            cstmt = con.prepareCall("{CALL `lip_AccountUpdate`(?, ?, ?, ?, ?, ?, ?)}");
            cstmt.setString(1, this.getLogin());
            cstmt.setString(2, this.getPasswordHash());
            cstmt.setInt(3, this.getAccessLevel());
            cstmt.setInt(4, this.getLastServer());
            cstmt.setString(5, this.getLastIP());
            cstmt.setInt(6, this.getLastAccess());
            cstmt.setString(7, this.getEmail());
            cstmt.execute();
        } catch (Exception var7) {
            _log.error("", var7);
        } finally {
            DbUtils.closeQuietly(con, cstmt);
        }

    }
}
