package l2.authserver.database;


//import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import l2.authserver.Config;
import l2.authserver.ThreadPoolManager;
import l2.commons.db.BaseDataConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import javax.sql.ConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static l2.authserver.Config.*;

@Slf4j
public class L2DatabaseFactory extends BaseDataConnectionFactory {
  private static L2DatabaseFactory l2DatabaseFactory;

  public static L2DatabaseFactory getInstance() {
    if (l2DatabaseFactory == null) {
      l2DatabaseFactory = new L2DatabaseFactory(makeConnectionPoolDataSource(), Config.DATABASE_MAX_CONN, Config.DATABASE_TIMEOUT);
    }
    return l2DatabaseFactory;
  }

  private L2DatabaseFactory(ConnectionPoolDataSource connectionPoolDataSource, int maxConnections, int timeout) {
    super(connectionPoolDataSource, maxConnections, timeout);
    this.addTestTask();
  }

  private static ConnectionPoolDataSource makeConnectionPoolDataSource() {
    var mysqlDB = new MysqlConnectionPoolDataSource();
    mysqlDB.setServerName(DATABASE_HOST);
    mysqlDB.setPort(DATABASE_PORT);
    mysqlDB.setDatabaseName(DATABASE_NAME);
    mysqlDB.setUser(DATABASE_USER);
    try {
      mysqlDB.setPasswordCharacterEncoding("UTF-8");
      mysqlDB.setPassword(DATABASE_PASS);
      mysqlDB.setAutoReconnect(true);
      mysqlDB.setServerTimezone("UTC");
//        ds.setAutoReconnectForConnectionPools(true);
      mysqlDB.setAutoReconnectForPools(true);
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("makeConnectionPoolDataSource: eMessage={}, eCause={}", e.getMessage(), e.getCause());
    }
    return mysqlDB;
  }

  protected void testDB() throws SQLException {
    Connection conn = this.getConnection();
    Statement stmt = conn.createStatement();
    stmt.executeQuery("SELECT * FROM `accounts` LIMIT 1");
    stmt.clearBatch();
    stmt.close();
    conn.close();
  }

  private void addTestTask() {
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
      public void run() {
        try {
          L2DatabaseFactory.this.testDB();
        } catch (SQLException e) {
          log.error("addTestTask: eMessage={}, eClass={}, eCause={}", e.getMessage(), e.getClass(), this.getClass().getSimpleName());
        }
      }
    }, 240000L, 240000L);
  }
}
