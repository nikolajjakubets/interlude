//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.model.mail.Mail.SenderType;

public class ExShowReceivedPostList extends L2GameServerPacket {
  private final List<Mail> mails;

  public ExShowReceivedPostList(Player cha) {
    this.mails = MailDAO.getInstance().getReceivedMailByOwnerId(cha.getObjectId());
    Collections.sort(this.mails);
  }

  protected void writeImpl() {
    this.writeEx(170);
    this.writeD((int)(System.currentTimeMillis() / 1000L));
    this.writeD(this.mails.size());
    Iterator var1 = this.mails.iterator();

    while(var1.hasNext()) {
      Mail mail = (Mail)var1.next();
      this.writeD(mail.getMessageId());
      this.writeS(mail.getTopic());
      this.writeS(mail.getSenderName());
      this.writeD(mail.isPayOnDelivery() ? 1 : 0);
      this.writeD(mail.getExpireTime());
      this.writeD(mail.isUnread() ? 1 : 0);
      this.writeD(mail.getType() == SenderType.NORMAL ? 0 : 1);
      this.writeD(mail.getAttachments().isEmpty() ? 0 : 1);
      this.writeD(0);
      this.writeD(mail.getType().ordinal());
      this.writeD(0);
    }

  }
}
