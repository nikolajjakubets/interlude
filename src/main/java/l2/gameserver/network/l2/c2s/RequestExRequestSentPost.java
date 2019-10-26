//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExReplySentPost;
import l2.gameserver.network.l2.s2c.ExShowSentPostList;

public class RequestExRequestSentPost extends L2GameClientPacket {
  private int postId;

  public RequestExRequestSentPost() {
  }

  protected void readImpl() {
    this.postId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), this.postId);
      if (mail != null) {
        activeChar.sendPacket(new ExReplySentPost(mail));
      } else {
        activeChar.sendPacket(new ExShowSentPostList(activeChar));
      }
    }
  }
}
