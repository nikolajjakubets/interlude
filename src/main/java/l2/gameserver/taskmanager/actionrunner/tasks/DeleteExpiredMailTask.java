//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager.actionrunner.tasks;

import java.util.Iterator;
import java.util.List;
import l2.commons.dao.JdbcEntityState;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.model.mail.Mail.SenderType;
import l2.gameserver.network.l2.s2c.ExNoticePostArrived;

public class DeleteExpiredMailTask extends AutomaticTask {
  public DeleteExpiredMailTask() {
  }

  public void doTask() throws Exception {
    int expireTime = (int)(System.currentTimeMillis() / 1000L);
    List<Mail> mails = MailDAO.getInstance().getExpiredMail(expireTime);
    Iterator var3 = mails.iterator();

    while(var3.hasNext()) {
      Mail mail = (Mail)var3.next();
      if (!mail.getAttachments().isEmpty()) {
        if (mail.getType() == SenderType.NORMAL) {
          Player player = World.getPlayer(mail.getSenderId());
          Mail reject = mail.reject();
          MailDAO.getInstance().deleteReceivedMailByMailId(mail.getReceiverId(), mail.getMessageId());
          MailDAO.getInstance().deleteSentMailByMailId(mail.getReceiverId(), mail.getMessageId());
          mail.delete();
          reject.setExpireTime(expireTime + 1296000);
          reject.save();
          if (player != null) {
            player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
            player.sendPacket(Msg.THE_MAIL_HAS_ARRIVED);
          }
        } else {
          mail.setExpireTime(expireTime + 86400);
          mail.setJdbcState(JdbcEntityState.UPDATED);
          mail.update();
        }
      } else {
        MailDAO.getInstance().deleteReceivedMailByMailId(mail.getReceiverId(), mail.getMessageId());
        MailDAO.getInstance().deleteSentMailByMailId(mail.getReceiverId(), mail.getMessageId());
        mail.delete();
      }
    }

  }

  public long reCalcTime(boolean start) {
    return System.currentTimeMillis() + 600000L;
  }
}
