//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedItemsManager extends RunnableImpl {
  private static final Logger _log = LoggerFactory.getLogger(DelayedItemsManager.class);
  private static DelayedItemsManager _instance;
  private static final Object _lock = new Object();
  private int last_payment_id = 0;

  public static DelayedItemsManager getInstance() {
    if (_instance == null) {
      _instance = new DelayedItemsManager();
    }

    return _instance;
  }

  public DelayedItemsManager() {
    Connection con = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      this.last_payment_id = this.get_last_payment_id(con);
    } catch (Exception var6) {
      _log.error("", var6);
    } finally {
      DbUtils.closeQuietly(con);
    }

    ThreadPoolManager.getInstance().schedule(this, 10000L);
  }

  private int get_last_payment_id(Connection con) {
    PreparedStatement st = null;
    ResultSet rset = null;
    int result = this.last_payment_id;

    try {
      st = con.prepareStatement("SELECT MAX(payment_id) AS last FROM items_delayed");
      rset = st.executeQuery();
      if (rset.next()) {
        result = rset.getInt("last");
      }
    } catch (Exception var9) {
      _log.error("", var9);
    } finally {
      DbUtils.closeQuietly(st, rset);
    }

    return result;
  }

  public void runImpl() throws Exception {
    Player player = null;
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      int last_payment_id_temp = this.get_last_payment_id(con);
      if (last_payment_id_temp != this.last_payment_id) {
        synchronized(_lock) {
          st = con.prepareStatement("SELECT DISTINCT owner_id FROM items_delayed WHERE payment_status=0 AND payment_id > ?");
          st.setInt(1, this.last_payment_id);
          rset = st.executeQuery();

          while(rset.next()) {
            if ((player = GameObjectsStorage.getPlayer(rset.getInt("owner_id"))) != null) {
              this.loadDelayed(player, true);
            }
          }

          this.last_payment_id = last_payment_id_temp;
        }
      }
    } catch (Exception var13) {
      _log.error("", var13);
    } finally {
      DbUtils.closeQuietly(con, st, rset);
    }

    ThreadPoolManager.getInstance().schedule(this, 10000L);
  }

  public int loadDelayed(Player player, boolean notify) {
    if (player == null) {
      return 0;
    } else {
      int player_id = player.getObjectId();
      PcInventory inv = player.getInventory();
      if (inv == null) {
        return 0;
      } else {
        int restored_counter = 0;
        Connection con = null;
        PreparedStatement st = null;
        PreparedStatement st_delete = null;
        ResultSet rset = null;
        synchronized(_lock) {
          try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("SELECT * FROM items_delayed WHERE owner_id=? AND payment_status=0");
            st.setInt(1, player_id);
            rset = st.executeQuery();
            st_delete = con.prepareStatement("UPDATE items_delayed SET payment_status=1 WHERE payment_id=?");

            while(rset.next()) {
              int ITEM_ID = rset.getInt("item_id");
              long ITEM_COUNT = rset.getLong("count");
              int ITEM_ENCHANT = rset.getInt("enchant_level");
              int PAYMENT_ID = rset.getInt("payment_id");
              int FLAGS = rset.getInt("flags");
              boolean stackable = ItemHolder.getInstance().getTemplate(ITEM_ID).isStackable();
              boolean success = false;

              for(int i = 0; (long)i < (stackable ? 1L : ITEM_COUNT); ++i) {
                ItemInstance item = ItemFunctions.createItem(ITEM_ID);
                if (item.isStackable()) {
                  item.setCount(ITEM_COUNT);
                } else {
                  item.setEnchantLevel(ITEM_ENCHANT);
                }

                item.setLocation(ItemLocation.INVENTORY);
                item.setCustomFlags(FLAGS);
                if (ITEM_COUNT > 0L) {
                  ItemInstance newItem = inv.addItem(item);
                  if (newItem == null) {
                    _log.warn("Unable to delayed create item " + ITEM_ID + " request " + PAYMENT_ID);
                    continue;
                  }
                }

                success = true;
                ++restored_counter;
                if (notify && ITEM_COUNT > 0L) {
                  player.sendPacket(SystemMessage2.obtainItems(ITEM_ID, stackable ? ITEM_COUNT : 1L, ITEM_ENCHANT));
                }

                player.sendMessage(new CustomMessage("l2.gameserver.taskmanager.DelayedItemsManager.ItemSendMessage", player, new Object[0]));
              }

              if (success) {
                Log.add("<add owner_id=" + player_id + " item_id=" + ITEM_ID + " count=" + ITEM_COUNT + " enchant_level=" + ITEM_ENCHANT + " payment_id=" + PAYMENT_ID + "/>", "delayed_add");
                st_delete.setInt(1, PAYMENT_ID);
                st_delete.execute();
              }
            }
          } catch (Exception var27) {
            _log.error("Could not load delayed items for player " + player + "!", var27);
          } finally {
            DbUtils.closeQuietly(st_delete);
            DbUtils.closeQuietly(con, st, rset);
          }

          return restored_counter;
        }
      }
    }
  }

  public void addDelayed(int ownerObjId, int itemTypeId, int amount, int enchant, String desc) {
    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      pstmt = con.prepareStatement("INSERT INTO \t`items_delayed`\t(\t`payment_id`, \t\t`owner_id`, \t\t`item_id`, \t\t`count`, \t\t`enchant_level`, \t\t`flags`, \t\t`payment_status`, \t\t`description`\t) SELECT \tMAX(`payment_id`) + 1, \t?, ?, ?, ?, 0, 0, ? \tFROM `items_delayed`");
      pstmt.setInt(1, ownerObjId);
      pstmt.setInt(2, itemTypeId);
      pstmt.setInt(3, amount);
      pstmt.setInt(4, enchant);
      pstmt.setString(5, desc);
      pstmt.executeUpdate();
    } catch (SQLException var12) {
      _log.error("Could not add delayed items " + itemTypeId + " " + amount + "(+" + enchant + ") for objId " + ownerObjId + " desc \"" + desc + "\" !", var12);
    } finally {
      DbUtils.closeQuietly(con, pstmt);
    }

  }
}
