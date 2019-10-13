//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.instances.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.ShortCutInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortCutList {
  private static final Logger _log = LoggerFactory.getLogger(ShortCutList.class);
  private final Player player;
  private Map<Integer, ShortCut> _shortCuts = new ConcurrentHashMap();

  public ShortCutList(Player owner) {
    this.player = owner;
  }

  public Collection<ShortCut> getAllShortCuts() {
    return this._shortCuts.values();
  }

  public void validate() {
    Iterator var1 = this._shortCuts.values().iterator();

    while(var1.hasNext()) {
      ShortCut sc = (ShortCut)var1.next();
      if (sc.getType() == 1 && this.player.getInventory().getItemByObjectId(sc.getId()) == null) {
        this.deleteShortCut(sc.getSlot(), sc.getPage());
      }
    }

  }

  public ShortCut getShortCut(int slot, int page) {
    ShortCut sc = (ShortCut)this._shortCuts.get(slot + page * 12);
    if (sc != null && sc.getType() == 1 && this.player.getInventory().getItemByObjectId(sc.getId()) == null) {
      this.player.sendPacket(Msg.THERE_ARE_NO_MORE_ITEMS_IN_THE_SHORTCUT);
      this.deleteShortCut(sc.getSlot(), sc.getPage());
      sc = null;
    }

    return sc;
  }

  public void registerShortCut(ShortCut shortcut) {
    ShortCut oldShortCut = (ShortCut)this._shortCuts.put(shortcut.getSlot() + 12 * shortcut.getPage(), shortcut);
    this.registerShortCutInDb(shortcut, oldShortCut);
  }

  private synchronized void registerShortCutInDb(ShortCut shortcut, ShortCut oldShortCut) {
    if (oldShortCut != null) {
      this.deleteShortCutFromDb(oldShortCut);
    }

    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("REPLACE INTO character_shortcuts SET object_id=?,slot=?,page=?,type=?,shortcut_id=?,level=?,character_type=?,class_index=?");
      statement.setInt(1, this.player.getObjectId());
      statement.setInt(2, shortcut.getSlot());
      statement.setInt(3, shortcut.getPage());
      statement.setInt(4, shortcut.getType());
      statement.setInt(5, shortcut.getId());
      statement.setInt(6, shortcut.getLevel());
      statement.setInt(7, shortcut.getCharacterType());
      statement.setInt(8, this.player.getActiveClassId());
      statement.execute();
    } catch (Exception var9) {
      _log.error("could not store shortcuts:", var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  private void deleteShortCutFromDb(ShortCut shortcut) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND slot=? AND page=? AND class_index=?");
      statement.setInt(1, this.player.getObjectId());
      statement.setInt(2, shortcut.getSlot());
      statement.setInt(3, shortcut.getPage());
      statement.setInt(4, this.player.getActiveClassId());
      statement.execute();
    } catch (Exception var8) {
      _log.error("could not delete shortcuts:", var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void deleteShortCut(int slot, int page) {
    ShortCut old = (ShortCut)this._shortCuts.remove(slot + page * 12);
    if (old != null) {
      this.deleteShortCutFromDb(old);
      if (old.getType() == 2) {
        this.player.sendPacket(new ShortCutInit(this.player));
        Iterator var4 = this.player.getAutoSoulShot().iterator();

        while(var4.hasNext()) {
          int shotId = (Integer)var4.next();
          this.player.sendPacket(new ExAutoSoulShot(shotId, true));
        }
      }

    }
  }

  public void deleteShortCutByObjectId(int objectId) {
    Iterator var2 = this._shortCuts.values().iterator();

    while(var2.hasNext()) {
      ShortCut shortcut = (ShortCut)var2.next();
      if (shortcut != null && shortcut.getType() == 1 && shortcut.getId() == objectId) {
        this.deleteShortCut(shortcut.getSlot(), shortcut.getPage());
      }
    }

  }

  public void deleteShortCutBySkillId(int skillId) {
    Iterator var2 = this._shortCuts.values().iterator();

    while(var2.hasNext()) {
      ShortCut shortcut = (ShortCut)var2.next();
      if (shortcut != null && shortcut.getType() == 2 && shortcut.getId() == skillId) {
        this.deleteShortCut(shortcut.getSlot(), shortcut.getPage());
      }
    }

  }

  public void restore() {
    this._shortCuts.clear();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT character_type, slot, page, type, shortcut_id, level FROM character_shortcuts WHERE object_id=? AND class_index=?");
      statement.setInt(1, this.player.getObjectId());
      statement.setInt(2, this.player.getActiveClassId());
      rset = statement.executeQuery();

      while(rset.next()) {
        int slot = rset.getInt("slot");
        int page = rset.getInt("page");
        int type = rset.getInt("type");
        int id = rset.getInt("shortcut_id");
        int level = rset.getInt("level");
        int character_type = rset.getInt("character_type");
        this._shortCuts.put(slot + page * 12, new ShortCut(slot, page, type, id, level, character_type));
      }
    } catch (Exception var13) {
      _log.error("could not store shortcuts:", var13);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }
}
