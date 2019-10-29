//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.dao;

import l2.commons.dao.JdbcDAO;
import l2.commons.dao.JdbcEntityState;
import l2.commons.dao.JdbcEntityStats;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.model.mail.Mail.SenderType;
import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Запрос на удаление полученных сообщений. Удалить можно только письмо без вложения.
 * Отсылается при нажатии на "delete" в списке полученных писем.
 */
public class MailDAO implements JdbcDAO<Integer, Mail> {
  private static final Logger _log = LoggerFactory.getLogger(MailDAO.class);
  private static final String RESTORE_MAIL = "SELECT sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread FROM mail WHERE message_id = ?";
  private static final String STORE_MAIL = "INSERT INTO mail(sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread) VALUES (?,?,?,?,?,?,?,?,?,?)";
  private static final String UPDATE_MAIL = "UPDATE mail SET sender_id = ?, sender_name = ?, receiver_id = ?, receiver_name = ?, expire_time = ?, topic = ?, body = ?, price = ?, type = ?, unread = ? WHERE message_id = ?";
  private static final String REMOVE_MAIL = "DELETE FROM mail WHERE message_id = ?";
  private static final String RESTORE_EXPIRED_MAIL = "SELECT message_id FROM mail WHERE expire_time <= ?";
  private static final String RESTORE_OWN_MAIL = "SELECT message_id FROM character_mail WHERE char_id = ? AND is_sender = ?";
  private static final String STORE_OWN_MAIL = "INSERT INTO character_mail(char_id, message_id, is_sender) VALUES (?,?,?)";
  private static final String REMOVE_OWN_MAIL = "DELETE FROM character_mail WHERE char_id = ? AND message_id = ? AND is_sender = ?";
  private static final String RESTORE_MAIL_ATTACHMENTS = "SELECT item_id FROM mail_attachments WHERE message_id = ?";
  private static final String STORE_MAIL_ATTACHMENT = "REPLACE INTO mail_attachments(message_id, item_id) VALUES (?,?)";
  private static final String REMOVE_MAIL_ATTACHMENTS = "DELETE FROM mail_attachments WHERE message_id = ?";
  private static final MailDAO instance = new MailDAO();
  private AtomicLong load = new AtomicLong();
  private AtomicLong insert = new AtomicLong();
  private AtomicLong update = new AtomicLong();
  private AtomicLong delete = new AtomicLong();
  private Cache<Integer, Mail> cache = CacheManagerBuilder.newCacheManagerBuilder()
    .withCache("preConfigured",
      CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, Mail.class,
        ResourcePoolsBuilder.heap(100))
        .build())
    .build(true)
    .createCache(Mail.class.getName(), CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, Mail.class,
      ResourcePoolsBuilder.heap(100))
      .build());
  //  private final Cache cache = CacheManager.getInstance().getCache(Mail.class.getName());
  private final JdbcEntityStats stats = new JdbcEntityStats() {
    public long getLoadCount() {
      return MailDAO.this.load.get();
    }

    public long getInsertCount() {
      return MailDAO.this.insert.get();
    }

    public long getUpdateCount() {
      return MailDAO.this.update.get();
    }

    public long getDeleteCount() {
      return MailDAO.this.delete.get();
    }
  };

  public static MailDAO getInstance() {
    return instance;
  }

  private MailDAO() {
  }

  public Cache getCache() {
    return this.cache;
  }

  public JdbcEntityStats getStats() {
    return this.stats;
  }

  private void save0(Mail mail) throws SQLException {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("INSERT INTO mail(sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread) VALUES (?,?,?,?,?,?,?,?,?,?)", 1);
      statement.setInt(1, mail.getSenderId());
      statement.setString(2, mail.getSenderName());
      statement.setInt(3, mail.getReceiverId());
      statement.setString(4, mail.getReceiverName());
      statement.setInt(5, mail.getExpireTime());
      statement.setString(6, mail.getTopic());
      statement.setString(7, mail.getBody());
      statement.setLong(8, mail.getPrice());
      statement.setInt(9, mail.getType().ordinal());
      statement.setBoolean(10, mail.isUnread());
      statement.execute();
      rset = statement.getGeneratedKeys();
      rset.next();
      mail.setMessageId(rset.getInt(1));
      if (!mail.getAttachments().isEmpty()) {
        DbUtils.close(statement);
        statement = con.prepareStatement("REPLACE INTO mail_attachments(message_id, item_id) VALUES (?,?)");

        for (ItemInstance item : mail.getAttachments()) {
          statement.setInt(1, mail.getMessageId());
          statement.setInt(2, item.getObjectId());
          statement.addBatch();
        }

        statement.executeBatch();
      }

      DbUtils.close(statement);
      if (mail.getType() == SenderType.NORMAL) {
        statement = con.prepareStatement("INSERT INTO character_mail(char_id, message_id, is_sender) VALUES (?,?,?)");
        statement.setInt(1, mail.getSenderId());
        statement.setInt(2, mail.getMessageId());
        statement.setBoolean(3, true);
        statement.execute();
      }

      DbUtils.close(statement);
      statement = con.prepareStatement("INSERT INTO character_mail(char_id, message_id, is_sender) VALUES (?,?,?)");
      statement.setInt(1, mail.getReceiverId());
      statement.setInt(2, mail.getMessageId());
      statement.setBoolean(3, false);
      statement.execute();
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    this.insert.incrementAndGet();
  }

  private Mail load0(int messageId) throws SQLException {
    Mail mail = null;
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread FROM mail WHERE message_id = ?");
      statement.setInt(1, messageId);
      rset = statement.executeQuery();
      if (rset.next()) {
        mail = new Mail();
        mail.setMessageId(messageId);
        mail.setSenderId(rset.getInt(1));
        mail.setSenderName(rset.getString(2));
        mail.setReceiverId(rset.getInt(3));
        mail.setReceiverName(rset.getString(4));
        mail.setExpireTime(rset.getInt(5));
        mail.setTopic(rset.getString(6));
        mail.setBody(rset.getString(7));
        mail.setPrice(rset.getLong(8));
        mail.setType(SenderType.VALUES[rset.getInt(9)]);
        mail.setUnread(rset.getBoolean(10));
        DbUtils.close(statement, rset);
        statement = con.prepareStatement("SELECT item_id FROM mail_attachments WHERE message_id = ?");
        statement.setInt(1, messageId);
        rset = statement.executeQuery();

        while (rset.next()) {
          int objectId = rset.getInt(1);
          ItemInstance item = ItemsDAO.getInstance().load(objectId);
          if (item != null) {
            mail.addAttachment(item);
          }
        }
      }
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    this.load.incrementAndGet();
    return mail;
  }

  private void update0(Mail mail) throws SQLException {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE mail SET sender_id = ?, sender_name = ?, receiver_id = ?, receiver_name = ?, expire_time = ?, topic = ?, body = ?, price = ?, type = ?, unread = ? WHERE message_id = ?");
      statement.setInt(1, mail.getSenderId());
      statement.setString(2, mail.getSenderName());
      statement.setInt(3, mail.getReceiverId());
      statement.setString(4, mail.getReceiverName());
      statement.setInt(5, mail.getExpireTime());
      statement.setString(6, mail.getTopic());
      statement.setString(7, mail.getBody());
      statement.setLong(8, mail.getPrice());
      statement.setInt(9, mail.getType().ordinal());
      statement.setBoolean(10, mail.isUnread());
      statement.setInt(11, mail.getMessageId());
      statement.execute();
      if (mail.getAttachments().isEmpty()) {
        DbUtils.close(statement);
        statement = con.prepareStatement("DELETE FROM mail_attachments WHERE message_id = ?");
        statement.setInt(1, mail.getMessageId());
        statement.execute();
      }
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    this.update.incrementAndGet();
  }

  private void delete0(Mail mail) throws SQLException {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM mail WHERE message_id = ?");
      statement.setInt(1, mail.getMessageId());
      statement.execute();
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    this.delete.incrementAndGet();
  }

  private List<Mail> getMailByOwnerId(int ownerId, boolean sent) {
    List<Integer> messageIds = Collections.emptyList();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT message_id FROM character_mail WHERE char_id = ? AND is_sender = ?");
      statement.setInt(1, ownerId);
      statement.setBoolean(2, sent);
      rset = statement.executeQuery();
      messageIds = new ArrayList<>();

      while (rset.next()) {
        messageIds.add(rset.getInt(1));
      }
    } catch (SQLException var11) {
      _log.error("Error while restore mail of owner : " + ownerId, var11);
      messageIds.clear();
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return this.load((Collection) messageIds);
  }

  private boolean deleteMailByOwnerIdAndMailId(int ownerId, int messageId, boolean sent) {
    Connection con = null;
    PreparedStatement statement = null;

    boolean var7;
    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM character_mail WHERE char_id = ? AND message_id = ? AND is_sender = ?");
      statement.setInt(1, ownerId);
      statement.setInt(2, messageId);
      statement.setBoolean(3, sent);
      return statement.execute();
    } catch (SQLException var11) {
      _log.error("Error while deleting mail of owner : " + ownerId, var11);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    return false;
  }

  public List<Mail> getReceivedMailByOwnerId(int receiverId) {
    return this.getMailByOwnerId(receiverId, false);
  }

  public List<Mail> getSentMailByOwnerId(int senderId) {
    return this.getMailByOwnerId(senderId, true);
  }

  public Mail getReceivedMailByMailId(int receiverId, int messageId) {
    List<Mail> list = this.getMailByOwnerId(receiverId, false);
    Iterator var4 = list.iterator();

    Mail mail;
    do {
      if (!var4.hasNext()) {
        return null;
      }

      mail = (Mail) var4.next();
    } while (mail.getMessageId() != messageId);

    return mail;
  }

  public Mail getSentMailByMailId(int senderId, int messageId) {
    List<Mail> list = this.getMailByOwnerId(senderId, true);
    Iterator var4 = list.iterator();

    Mail mail;
    do {
      if (!var4.hasNext()) {
        return null;
      }

      mail = (Mail) var4.next();
    } while (mail.getMessageId() != messageId);

    return mail;
  }

  public boolean deleteReceivedMailByMailId(int receiverId, int messageId) {
    return this.deleteMailByOwnerIdAndMailId(receiverId, messageId, false);
  }

  public boolean deleteSentMailByMailId(int senderId, int messageId) {
    return this.deleteMailByOwnerIdAndMailId(senderId, messageId, true);
  }

  public List<Mail> getExpiredMail(int expireTime) {
    List<Integer> messageIds = Collections.emptyList();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT message_id FROM mail WHERE expire_time <= ?");
      statement.setInt(1, expireTime);
      rset = statement.executeQuery();
      messageIds = new ArrayList<>();

      while (rset.next()) {
        messageIds.add(rset.getInt(1));
      }
    } catch (SQLException var10) {
      _log.error("Error while restore expired mail!", var10);
      messageIds.clear();
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return this.load((Collection) messageIds);
  }

  public Mail load(Integer id) {
    Mail ce = this.cache.get(id);
    if (ce != null) {
      return ce;
    } else {
      try {
        Mail mail = this.load0(id);
        if (mail != null) {
          mail.setJdbcState(JdbcEntityState.STORED);
          this.cache.put(mail.getMessageId(), mail);
        }
        return mail;
      } catch (SQLException var5) {
        _log.error("Error while restoring mail : " + id, var5);
        return null;
      }
    }
  }

  public List<Mail> load(Collection<Integer> messageIds) {
    if (messageIds.isEmpty()) {
      return Collections.emptyList();
    } else {
      List<Mail> list = new ArrayList(messageIds.size());

      for (Integer messageId : messageIds) {
        Mail mail = this.load(messageId);
        if (mail != null) {
          list.add(mail);
        }
      }

      return list;
    }
  }

  public void save(Mail mail) {
    if (mail.getJdbcState().isSavable()) {
      try {
        this.save0(mail);
        mail.setJdbcState(JdbcEntityState.STORED);
      } catch (SQLException var3) {
        _log.error("Error while saving mail!", var3);
        return;
      }

      this.cache.put(mail.getMessageId(), mail);
    }
  }

  public void update(Mail mail) {
    if (mail.getJdbcState().isUpdatable()) {
      try {
        this.update0(mail);
        mail.setJdbcState(JdbcEntityState.STORED);
      } catch (SQLException var3) {
        _log.error("Error while updating mail : " + mail.getMessageId(), var3);
        return;
      }
      this.cache.putIfAbsent(mail.getMessageId(), mail);
    }
  }

  public void saveOrUpdate(Mail mail) {
    if (mail.getJdbcState().isSavable()) {
      this.save(mail);
    } else if (mail.getJdbcState().isUpdatable()) {
      this.update(mail);
    }

  }

  public void delete(Mail mail) {
    if (mail.getJdbcState().isDeletable()) {
      try {
        this.delete0(mail);
        mail.setJdbcState(JdbcEntityState.DELETED);
      } catch (SQLException var3) {
        _log.error("Error while deleting mail : " + mail.getMessageId(), var3);
        return;
      }

      this.cache.remove(mail.getExpireTime());
    }
  }
}
