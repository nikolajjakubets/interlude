//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.mysql;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth.SecondPasswordAuthUI;
import l2.gameserver.network.l2.SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType;
import l2.gameserver.network.l2.s2c.CharacterDeleteFail;
import l2.gameserver.network.l2.s2c.CharacterDeleteSuccess;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterDelete extends L2GameClientPacket {
  private static final Logger _log = LoggerFactory.getLogger(CharacterDelete.class);
  private int _charSlot;

  public CharacterDelete() {
  }

  protected void readImpl() {
    this._charSlot = this.readD();
  }

  protected void runImpl() {
    int clan = this.clanStatus();
    int online = this.onlineStatus();
    if (clan <= 0 && online <= 0) {
      final GameClient client = (GameClient)this.getClient();
      Runnable doDelete = new RunnableImpl() {
        public void runImpl() throws Exception {
          boolean var5 = false;

          try {
            var5 = true;
            if (Config.DELETE_DAYS == 0) {
              client.deleteCharacterInSlot(CharacterDelete.this._charSlot);
            } else {
              client.markToDeleteChar(CharacterDelete.this._charSlot);
            }

            client.sendPacket(new CharacterDeleteSuccess());
            var5 = false;
          } finally {
            if (var5) {
              CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
              client.sendPacket(cl);
              client.setCharSelection(cl.getCharInfo());
            }
          }

          CharacterSelectionInfo clx = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
          client.sendPacket(clx);
          client.setCharSelection(clx.getCharInfo());
        }
      };
      if (Config.USE_SECOND_PASSWORD_AUTH && !client.isSecondPasswordAuthed()) {
        if (client.getSecondPasswordAuth().isSecondPasswordSet()) {
          if (client.getSecondPasswordAuth().getUI() == null) {
            client.getSecondPasswordAuth().setUI(new SecondPasswordAuthUI(SecondPasswordAuthUIType.VERIFY));
          }
        } else if (client.getSecondPasswordAuth().getUI() == null) {
          client.getSecondPasswordAuth().setUI(new SecondPasswordAuthUI(SecondPasswordAuthUIType.CREATE));
        }

        client.getSecondPasswordAuth().getUI().verify(client, doDelete);
      } else {
        ThreadPoolManager.getInstance().execute(doDelete);
      }

    } else {
      if (clan == 2) {
        this.sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
      } else if (clan == 1) {
        this.sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
      } else if (online > 0) {
        this.sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_DELETION_FAILED));
      }

    }
  }

  private int clanStatus() {
    int obj = ((GameClient)this.getClient()).getObjectIdForSlot(this._charSlot);
    if (obj == -1) {
      return 0;
    } else if (mysql.simple_get_int("clanid", "characters", "obj_Id=" + obj) > 0) {
      return mysql.simple_get_int("leader_id", "clan_subpledges", "leader_id=" + obj + " AND type = " + 0) > 0 ? 2 : 1;
    } else {
      return 0;
    }
  }

  private int onlineStatus() {
    int obj = ((GameClient)this.getClient()).getObjectIdForSlot(this._charSlot);
    if (obj == -1) {
      return 0;
    } else {
      return mysql.simple_get_int("online", "characters", "obj_Id=" + obj) > 0 ? 1 : 0;
    }
  }
}
