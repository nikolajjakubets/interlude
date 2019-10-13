//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Couple;
import l2.gameserver.network.l2.components.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoupleManager {
  private static final Logger _log = LoggerFactory.getLogger(CoupleManager.class);
  private static CoupleManager _instance;
  private List<Couple> _couples;
  private List<Couple> _deletedCouples;

  public static CoupleManager getInstance() {
    if (_instance == null) {
      new CoupleManager();
    }

    return _instance;
  }

  public CoupleManager() {
    _instance = this;
    _log.info("Initializing CoupleManager");
    _instance.load();
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new CoupleManager.StoreTask(), 600000L, 600000L);
  }

  private void load() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT * FROM couples ORDER BY id");
      rs = statement.executeQuery();

      while(rs.next()) {
        Couple c = new Couple(rs.getInt("id"));
        c.setPlayer1Id(rs.getInt("player1Id"));
        c.setPlayer2Id(rs.getInt("player2Id"));
        c.setMaried(rs.getBoolean("maried"));
        c.setAffiancedDate(rs.getLong("affiancedDate"));
        c.setWeddingDate(rs.getLong("weddingDate"));
        this.getCouples().add(c);
      }

      _log.info("Loaded: " + this.getCouples().size() + " couples(s)");
    } catch (Exception var8) {
      _log.error("", var8);
    } finally {
      DbUtils.closeQuietly(con, statement, rs);
    }

  }

  public final Couple getCouple(int coupleId) {
    Iterator var2 = this.getCouples().iterator();

    Couple c;
    do {
      if (!var2.hasNext()) {
        return null;
      }

      c = (Couple)var2.next();
    } while(c == null || c.getId() != coupleId);

    return c;
  }

  public void engage(Player cha) {
    int chaId = cha.getObjectId();
    Iterator var3 = this.getCouples().iterator();

    while(true) {
      Couple cl;
      do {
        do {
          if (!var3.hasNext()) {
            return;
          }

          cl = (Couple)var3.next();
        } while(cl == null);
      } while(cl.getPlayer1Id() != chaId && cl.getPlayer2Id() != chaId);

      if (cl.getMaried()) {
        cha.setMaried(true);
      }

      cha.setCoupleId(cl.getId());
      if (cl.getPlayer1Id() == chaId) {
        cha.setPartnerId(cl.getPlayer2Id());
      } else {
        cha.setPartnerId(cl.getPlayer1Id());
      }
    }
  }

  public void notifyPartner(Player cha) {
    if (cha.getPartnerId() != 0) {
      Player partner = GameObjectsStorage.getPlayer(cha.getPartnerId());
      if (partner != null) {
        partner.sendMessage(new CustomMessage("l2p.gameserver.instancemanager.CoupleManager.PartnerEntered", partner, new Object[0]));
      }
    }

  }

  public void createCouple(Player player1, Player player2) {
    if (player1 != null && player2 != null && player1.getPartnerId() == 0 && player2.getPartnerId() == 0) {
      this.getCouples().add(new Couple(player1, player2));
    }

  }

  public final List<Couple> getCouples() {
    if (this._couples == null) {
      this._couples = new CopyOnWriteArrayList();
    }

    return this._couples;
  }

  public List<Couple> getDeletedCouples() {
    if (this._deletedCouples == null) {
      this._deletedCouples = new CopyOnWriteArrayList();
    }

    return this._deletedCouples;
  }

  public void store() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      Iterator var3;
      Couple c;
      if (this._deletedCouples != null && !this._deletedCouples.isEmpty()) {
        statement = con.prepareStatement("DELETE FROM couples WHERE id = ?");
        var3 = this._deletedCouples.iterator();

        while(var3.hasNext()) {
          c = (Couple)var3.next();
          statement.setInt(1, c.getId());
          statement.execute();
        }

        this._deletedCouples.clear();
      }

      if (this._couples != null && !this._couples.isEmpty()) {
        var3 = this._couples.iterator();

        while(var3.hasNext()) {
          c = (Couple)var3.next();
          if (c != null && c.isChanged()) {
            c.store(con);
            c.setChanged(false);
          }
        }
      }
    } catch (Exception var8) {
      _log.error("", var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private class StoreTask extends RunnableImpl {
    private StoreTask() {
    }

    public void runImpl() throws Exception {
      CoupleManager.this.store();
    }
  }
}
