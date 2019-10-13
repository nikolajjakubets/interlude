//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.mail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import l2.commons.dao.JdbcEntity;
import l2.commons.dao.JdbcEntityState;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.items.ItemInstance;

public class Mail implements JdbcEntity, Comparable<Mail> {
  private static final long serialVersionUID = -8704970972611917153L;
  public static final int DELETED = 0;
  public static final int READED = 1;
  public static final int REJECTED = 2;
  private static final MailDAO _mailDAO = MailDAO.getInstance();
  private int messageId;
  private int senderId;
  private String senderName;
  private int receiverId;
  private String receiverName;
  private int expireTime;
  private String topic;
  private String body;
  private long price;
  private Mail.SenderType _type;
  private boolean isUnread;
  private Set<ItemInstance> attachments;
  private JdbcEntityState _state;

  public Mail() {
    this._type = Mail.SenderType.NORMAL;
    this.attachments = new HashSet();
    this._state = JdbcEntityState.CREATED;
  }

  public int getMessageId() {
    return this.messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public int getSenderId() {
    return this.senderId;
  }

  public void setSenderId(int senderId) {
    this.senderId = senderId;
  }

  public String getSenderName() {
    return this.senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public int getReceiverId() {
    return this.receiverId;
  }

  public void setReceiverId(int receiverId) {
    this.receiverId = receiverId;
  }

  public String getReceiverName() {
    return this.receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public int getExpireTime() {
    return this.expireTime;
  }

  public void setExpireTime(int expireTime) {
    this.expireTime = expireTime;
  }

  public String getTopic() {
    return this.topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getBody() {
    return this.body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public boolean isPayOnDelivery() {
    return this.price > 0L;
  }

  public long getPrice() {
    return this.price;
  }

  public void setPrice(long price) {
    this.price = price;
  }

  public boolean isUnread() {
    return this.isUnread;
  }

  public void setUnread(boolean isUnread) {
    this.isUnread = isUnread;
  }

  public Set<ItemInstance> getAttachments() {
    return this.attachments;
  }

  public void addAttachment(ItemInstance item) {
    this.attachments.add(item);
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else if (o.getClass() != this.getClass()) {
      return false;
    } else {
      return ((Mail)o).getMessageId() == this.getMessageId();
    }
  }

  public void setJdbcState(JdbcEntityState state) {
    this._state = state;
  }

  public JdbcEntityState getJdbcState() {
    return this._state;
  }

  public void save() {
    _mailDAO.save(this);
  }

  public void update() {
    _mailDAO.update(this);
  }

  public void delete() {
    _mailDAO.delete(this);
  }

  public Mail reject() {
    Mail mail = new Mail();
    mail.setSenderId(this.getReceiverId());
    mail.setSenderName(this.getReceiverName());
    mail.setReceiverId(this.getSenderId());
    mail.setReceiverName(this.getSenderName());
    mail.setTopic(this.getTopic());
    mail.setBody(this.getBody());
    synchronized(this.getAttachments()) {
      Iterator var3 = this.getAttachments().iterator();

      while(true) {
        if (!var3.hasNext()) {
          this.getAttachments().clear();
          break;
        }

        ItemInstance item = (ItemInstance)var3.next();
        mail.addAttachment(item);
      }
    }

    mail.setType(Mail.SenderType.NEWS_INFORMER);
    mail.setUnread(true);
    return mail;
  }

  public Mail reply() {
    Mail mail = new Mail();
    mail.setSenderId(this.getReceiverId());
    mail.setSenderName(this.getReceiverName());
    mail.setReceiverId(this.getSenderId());
    mail.setReceiverName(this.getSenderName());
    mail.setTopic("[Re]" + this.getTopic());
    mail.setBody(this.getBody());
    mail.setType(Mail.SenderType.NEWS_INFORMER);
    mail.setUnread(true);
    return mail;
  }

  public int compareTo(Mail o) {
    return o.getMessageId() - this.getMessageId();
  }

  public Mail.SenderType getType() {
    return this._type;
  }

  public void setType(Mail.SenderType type) {
    this._type = type;
  }

  public static enum SenderType {
    NORMAL,
    NEWS_INFORMER,
    NONE,
    BIRTHDAY;

    public static Mail.SenderType[] VALUES = values();

    private SenderType() {
    }
  }
}
