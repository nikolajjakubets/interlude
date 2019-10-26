//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.dao.JdbcEntityState;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExChangePostState;
import l2.gameserver.network.l2.s2c.ExReplyReceivedPost;
import l2.gameserver.network.l2.s2c.ExShowReceivedPostList;

public class RequestExRequestReceivedPost extends L2GameClientPacket {
  private int postId;

  public RequestExRequestReceivedPost() {
  }

  protected void readImpl() {
    this.postId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), this.postId);
      if (mail != null) {
        if (mail.isUnread()) {
          mail.setUnread(false);
          mail.setJdbcState(JdbcEntityState.UPDATED);
          mail.update();
          activeChar.sendPacket(new ExChangePostState(true, 1, new Mail[]{mail}));
        }

        activeChar.sendPacket(new ExReplyReceivedPost(mail));
      } else {
        activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
      }
    }
  }
}
