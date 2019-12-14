package l2.authserver.accounts;

import l2.authserver.database.L2DatabaseFactory;
import l2.commons.dbutils.DbUtils;
import l2.commons.net.utils.NetList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

@Slf4j
@Data
public class Account {
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
    } catch (Exception e) {
      log.error("restore: eMessage={}, eClass={}", e.getMessage(), e.getClass());
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
    } catch (Exception e) {
      log.error("save: eMessage={}, eClass={}", e.getMessage(), e.getClass());
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
    } catch (Exception e) {
      log.error("update: eMessage={}, eClass={}", e.getMessage(), e.getClass());
    } finally {
      DbUtils.closeQuietly(con, cstmt);
    }
  }

  public boolean isAllowedIP(String ip) {
    return this.allowedIpList.isEmpty() || this.allowedIpList.isInRange(ip);
  }
}
