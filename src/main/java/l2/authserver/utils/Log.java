package l2.authserver.utils;

import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.database.L2DatabaseFactory;
import l2.commons.dbutils.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Slf4j
public class Log {

  public Log() {
  }

  public static void LogAccount(Account account) {
    if (Config.LOGIN_LOG) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("INSERT INTO account_log (time, login, ip) VALUES(?,?,?)");
        statement.setInt(1, account.getLastAccess());
        statement.setString(2, account.getLogin());
        statement.setString(3, account.getLastIP());
        statement.execute();
      } catch (Exception e) {
        log.error("LogAccount: eMessage={}, eClause={}", e.getMessage(), e.getClass());
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }
}
