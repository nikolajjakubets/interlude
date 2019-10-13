//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.mail;

import l2.gameserver.model.items.ItemInstance;

public class Attachment {
  private int messageId;
  private ItemInstance item;
  private Mail mail;

  public Attachment() {
  }

  public int getMessageId() {
    return this.messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public ItemInstance getItem() {
    return this.item;
  }

  public void setItem(ItemInstance item) {
    this.item = item;
  }

  public Mail getMail() {
    return this.mail;
  }

  public void setMail(Mail mail) {
    this.mail = mail;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else if (o.getClass() != this.getClass()) {
      return false;
    } else {
      return ((Attachment)o).getItem() == this.getItem();
    }
  }
}
