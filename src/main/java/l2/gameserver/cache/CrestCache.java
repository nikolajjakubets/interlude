//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.cache;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrestCache {
  public static final int ALLY_CREST_SIZE = 192;
  public static final int CREST_SIZE = 256;
  public static final int LARGE_CREST_SIZE = 2176;
  private static final Logger _log = LoggerFactory.getLogger(CrestCache.class);
  private static final CrestCache _instance = new CrestCache();
  private final TIntIntHashMap _pledgeCrestId = new TIntIntHashMap();
  private final TIntIntHashMap _pledgeCrestLargeId = new TIntIntHashMap();
  private final TIntIntHashMap _allyCrestId = new TIntIntHashMap();
  private final TIntObjectHashMap<byte[]> _pledgeCrest = new TIntObjectHashMap();
  private final TIntObjectHashMap<byte[]> _pledgeCrestLarge = new TIntObjectHashMap();
  private final TIntObjectHashMap<byte[]> _allyCrest = new TIntObjectHashMap();
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final Lock readLock;
  private final Lock writeLock;

  public static final CrestCache getInstance() {
    return _instance;
  }

  private CrestCache() {
    this.readLock = this.lock.readLock();
    this.writeLock = this.lock.writeLock();
    this.load();
  }

  public void load() {
    int count = 0;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT clan_id, crest FROM clan_data WHERE crest IS NOT NULL");
      rset = statement.executeQuery();

      int pledgeId;
      int crestId;
      byte[] crest;
      while(rset.next()) {
        ++count;
        pledgeId = rset.getInt("clan_id");
        crest = rset.getBytes("crest");
        crestId = getCrestId(pledgeId, crest);
        this._pledgeCrestId.put(pledgeId, crestId);
        this._pledgeCrest.put(crestId, crest);
      }

      DbUtils.close(statement, rset);
      statement = con.prepareStatement("SELECT clan_id, largecrest FROM clan_data WHERE largecrest IS NOT NULL");
      rset = statement.executeQuery();

      while(rset.next()) {
        ++count;
        pledgeId = rset.getInt("clan_id");
        crest = rset.getBytes("largecrest");
        crestId = getCrestId(pledgeId, crest);
        this._pledgeCrestLargeId.put(pledgeId, crestId);
        this._pledgeCrestLarge.put(crestId, crest);
      }

      DbUtils.close(statement, rset);
      statement = con.prepareStatement("SELECT ally_id, crest FROM ally_data WHERE crest IS NOT NULL");
      rset = statement.executeQuery();

      while(rset.next()) {
        ++count;
        pledgeId = rset.getInt("ally_id");
        crest = rset.getBytes("crest");
        crestId = getCrestId(pledgeId, crest);
        this._allyCrestId.put(pledgeId, crestId);
        this._allyCrest.put(crestId, crest);
      }
    } catch (Exception var12) {
      _log.error("", var12);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    _log.info("CrestCache: Loaded " + count + " crests");
  }

  private static int getCrestId(int pledgeId, byte[] crest) {
    return Math.abs((new HashCodeBuilder(15, 87)).append(pledgeId).append(crest).toHashCode());
  }

  public byte[] getPledgeCrest(int crestId) {
    byte[] crest = null;
    this.readLock.lock();

    byte[] crest;
    try {
      crest = (byte[])this._pledgeCrest.get(crestId);
    } finally {
      this.readLock.unlock();
    }

    return crest;
  }

  public byte[] getPledgeCrestLarge(int crestId) {
    byte[] crest = null;
    this.readLock.lock();

    byte[] crest;
    try {
      crest = (byte[])this._pledgeCrestLarge.get(crestId);
    } finally {
      this.readLock.unlock();
    }

    return crest;
  }

  public byte[] getAllyCrest(int crestId) {
    byte[] crest = null;
    this.readLock.lock();

    byte[] crest;
    try {
      crest = (byte[])this._allyCrest.get(crestId);
    } finally {
      this.readLock.unlock();
    }

    return crest;
  }

  public int getPledgeCrestId(int pledgeId) {
    int crestId = false;
    this.readLock.lock();

    int crestId;
    try {
      crestId = this._pledgeCrestId.get(pledgeId);
    } finally {
      this.readLock.unlock();
    }

    return crestId;
  }

  public int getPledgeCrestLargeId(int pledgeId) {
    int crestId = false;
    this.readLock.lock();

    int crestId;
    try {
      crestId = this._pledgeCrestLargeId.get(pledgeId);
    } finally {
      this.readLock.unlock();
    }

    return crestId;
  }

  public int getAllyCrestId(int pledgeId) {
    int crestId = false;
    this.readLock.lock();

    int crestId;
    try {
      crestId = this._allyCrestId.get(pledgeId);
    } finally {
      this.readLock.unlock();
    }

    return crestId;
  }

  public void removePledgeCrest(int pledgeId) {
    this.writeLock.lock();

    try {
      this._pledgeCrest.remove(this._pledgeCrestId.remove(pledgeId));
    } finally {
      this.writeLock.unlock();
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?");
      statement.setNull(1, -3);
      statement.setInt(2, pledgeId);
      statement.execute();
    } catch (Exception var12) {
      _log.error("", var12);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void removePledgeCrestLarge(int pledgeId) {
    this.writeLock.lock();

    try {
      this._pledgeCrestLarge.remove(this._pledgeCrestLargeId.remove(pledgeId));
    } finally {
      this.writeLock.unlock();
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?");
      statement.setNull(1, -3);
      statement.setInt(2, pledgeId);
      statement.execute();
    } catch (Exception var12) {
      _log.error("", var12);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void removeAllyCrest(int pledgeId) {
    this.writeLock.lock();

    try {
      this._allyCrest.remove(this._allyCrestId.remove(pledgeId));
    } finally {
      this.writeLock.unlock();
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?");
      statement.setNull(1, -3);
      statement.setInt(2, pledgeId);
      statement.execute();
    } catch (Exception var12) {
      _log.error("", var12);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public int savePledgeCrest(int pledgeId, byte[] crest) {
    int crestId = getCrestId(pledgeId, crest);
    this.writeLock.lock();

    try {
      this._pledgeCrestId.put(pledgeId, crestId);
      this._pledgeCrest.put(crestId, crest);
    } finally {
      this.writeLock.unlock();
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?");
      statement.setBytes(1, crest);
      statement.setInt(2, pledgeId);
      statement.execute();
    } catch (Exception var14) {
      _log.error("", var14);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    return crestId;
  }

  public int savePledgeCrestLarge(int pledgeId, byte[] crest) {
    int crestId = getCrestId(pledgeId, crest);
    this.writeLock.lock();

    try {
      this._pledgeCrestLargeId.put(pledgeId, crestId);
      this._pledgeCrestLarge.put(crestId, crest);
    } finally {
      this.writeLock.unlock();
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?");
      statement.setBytes(1, crest);
      statement.setInt(2, pledgeId);
      statement.execute();
    } catch (Exception var14) {
      _log.error("", var14);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    return crestId;
  }

  public int saveAllyCrest(int pledgeId, byte[] crest) {
    int crestId = getCrestId(pledgeId, crest);
    this.writeLock.lock();

    try {
      this._allyCrestId.put(pledgeId, crestId);
      this._allyCrest.put(crestId, crest);
    } finally {
      this.writeLock.unlock();
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?");
      statement.setBytes(1, crest);
      statement.setInt(2, pledgeId);
      statement.execute();
    } catch (Exception var14) {
      _log.error("", var14);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    return crestId;
  }

  public static boolean isValidCrestData(byte[] crestData) {
    switch(crestData.length) {
      case 192:
      case 256:
      case 2176:
        if (crestData[0] == 68 && crestData[1] == 68 && crestData[2] == 83 && crestData[3] == 32 && crestData[84] == 68 && crestData[85] == 88 && crestData[86] == 84 && crestData[87] == 49) {
          switch(crestData.length) {
            case 192:
              if (crestData[12] != 16 || crestData[16] != 8) {
                return false;
              }
              break;
            case 256:
              if (crestData[12] != 16 || crestData[16] != 16) {
                return false;
              }
              break;
            case 2176:
              if (crestData[12] != 64 || crestData[16] != 64) {
                return false;
              }
          }

          return true;
        } else {
          return false;
        }
      default:
        return false;
    }
  }
}
