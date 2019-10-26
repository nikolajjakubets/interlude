//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.model.mail.Mail.SenderType;

public class ExReplyReceivedPost extends L2GameServerPacket {
  private final Mail mail;

  public ExReplyReceivedPost(Mail mail) {
    this.mail = mail;
  }

  protected void writeImpl() {
    this.writeEx(171);
    this.writeD(this.mail.getMessageId());
    this.writeD(this.mail.isPayOnDelivery() ? 1 : 0);
    this.writeD(this.mail.getType() == SenderType.NORMAL ? 0 : 1);
    this.writeS(this.mail.getSenderName());
    this.writeS(this.mail.getTopic());
    this.writeS(this.mail.getBody());
    this.writeD(this.mail.getAttachments().size());
    Iterator var1 = this.mail.getAttachments().iterator();

    while(var1.hasNext()) {
      ItemInstance item = (ItemInstance)var1.next();
      this.writeItemInfo(item);
      this.writeD(item.getObjectId());
    }

    this.writeQ(this.mail.getPrice());
    this.writeD(this.mail.getAttachments().size() > 0 ? 1 : 0);
    this.writeD(this.mail.getType().ordinal());
  }
}
