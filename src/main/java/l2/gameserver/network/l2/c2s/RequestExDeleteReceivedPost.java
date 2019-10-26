//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExShowReceivedPostList;
import org.apache.commons.lang3.ArrayUtils;

public class RequestExDeleteReceivedPost extends L2GameClientPacket {
  private int _count;
  private int[] _list;

  public RequestExDeleteReceivedPost() {
  }

  protected void readImpl() {
    this._count = this.readD();
    if (this._count * 4 <= this._buf.remaining() && this._count <= 32767 && this._count >= 1) {
      this._list = new int[this._count];

      for(int i = 0; i < this._count; ++i) {
        this._list[i] = this.readD();
      }

    } else {
      this._count = 0;
    }
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null && this._count != 0) {
      List<Mail> mails = MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId());
      if (!mails.isEmpty()) {
        Iterator var3 = mails.iterator();

        while(var3.hasNext()) {
          Mail mail = (Mail)var3.next();
          if (ArrayUtils.contains(this._list, mail.getMessageId()) && mail.getAttachments().isEmpty()) {
            MailDAO.getInstance().deleteReceivedMailByMailId(activeChar.getObjectId(), mail.getMessageId());
          }
        }
      }

      activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
    }
  }
}
