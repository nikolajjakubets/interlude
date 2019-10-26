//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Iterator;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;

public class ExReplySentPost extends L2GameServerPacket {
  private final Mail mail;

  public ExReplySentPost(Mail mail) {
    this.mail = mail;
  }

  protected void writeImpl() {
    this.writeEx(173);
    this.writeD(this.mail.getMessageId());
    this.writeD(this.mail.isPayOnDelivery() ? 1 : 0);
    this.writeS(this.mail.getReceiverName());
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
    this.writeD(0);
  }
}
