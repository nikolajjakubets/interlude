//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.database;

//import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import l2.commons.db.BaseDataConnectionFactory;
import l2.gameserver.Config;

import javax.sql.ConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseFactory extends BaseDataConnectionFactory {
  private static DatabaseFactory _instance;
  private static final String ENCODING_STR = "UTF-8";

  public static DatabaseFactory getInstance() {
    if (_instance == null) {
      _instance = new DatabaseFactory(makeConnectionPoolDataSource(), Config.DATABASE_MAX_CONN, Config.DATABASE_TIMEOUT);
    }

    return _instance;
  }

  private DatabaseFactory(ConnectionPoolDataSource connectionPoolDataSource, int maxConnections, int timeout) {
    super(connectionPoolDataSource, maxConnections, timeout);
  }

  private static ConnectionPoolDataSource makeConnectionPoolDataSource() {
    MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
    ds.setServerName(Config.DATABASE_HOST);
    ds.setPort(Config.DATABASE_PORT);
    ds.setDatabaseName(Config.DATABASE_NAME);
    ds.setUser(Config.DATABASE_USER);
    ds.setPassword(Config.DATABASE_PASS);
    try {
      ds.setPasswordCharacterEncoding("UTF-8");
      ds.setServerTimezone("UTC");
      ds.setAutoReconnect(true);
//    ds.setAutoReconnectForConnectionPools(true);
      ds.setAutoReconnectForPools(true);
//    ds.setUseUnicode(true);
//    ds.setEncoding("UTF-8");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ds;
  }

  protected void testDB() throws SQLException {
    Connection conn = this.getConnection();
    Statement stmt = conn.createStatement();
    stmt.executeQuery("SELECT * FROM `characters` LIMIT 1");
    stmt.clearBatch();
    stmt.close();
    conn.close();
  }
}
