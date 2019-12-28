//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CharacterVariablesDAO;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.GameClient.GameClientState;
import l2.gameserver.network.l2.SecondPasswordAuth.SecondPasswordAuthUI;
import l2.gameserver.network.l2.SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.CharSelected;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.utils.AutoBan;

public class CharacterSelected extends L2GameClientPacket {
  private int _charSlot;

  public CharacterSelected() {
  }

  protected void readImpl() {
    this._charSlot = this.readD();
  }

  protected void runImpl() {
    final GameClient client = (GameClient)this.getClient();
    if (client.getActiveChar() == null) {
      int objId = client.getObjectIdForSlot(this._charSlot);
      if (AutoBan.isBanned(objId)) {
        this.sendPacket(ActionFail.getStatic());
      } else {
        String hwidLock = CharacterVariablesDAO.getInstance().getVar(objId, "hwidlock@");
        if (hwidLock != null && !hwidLock.isEmpty() && client.getHwid() != null && !client.getHwid().isEmpty() && !hwidLock.equalsIgnoreCase(client.getHwid())) {
          this.sendPacket(new ExShowScreenMessage("HWID is locked.", 10000, ScreenMessageAlign.TOP_CENTER, true));
          this.sendPacket(ActionFail.getStatic());
        } else {
          String ipLock = CharacterVariablesDAO.getInstance().getVar(objId, "iplock@");
          if (ipLock != null && !ipLock.isEmpty() && client.getIpAddr() != null && !client.getIpAddr().isEmpty() && !ipLock.equalsIgnoreCase(client.getIpAddr())) {
            this.sendPacket(new ExShowScreenMessage("IP address is locked.", 10000, ScreenMessageAlign.TOP_CENTER, true));
            this.sendPacket(ActionFail.getStatic());
          } else {
            Runnable doSelect = new RunnableImpl() {
              public void runImpl() throws Exception {
                Player activeChar = client.loadCharFromDisk(CharacterSelected.this._charSlot);
                if (activeChar == null) {
                  CharacterSelected.this.sendPacket(ActionFail.getStatic());
                } else {
                  if (activeChar.getAccessLevel() < 0) {
                    activeChar.setAccessLevel(0);
                  }

                  client.setState(GameClientState.IN_GAME);
                  client.sendPacket(new CharSelected(activeChar, client.getSessionKey().playOkID1));
                }
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

              client.getSecondPasswordAuth().getUI().verify(client, doSelect);
            } else {
              ThreadPoolManager.getInstance().execute(doSelect);
            }

          }
        }
      }
    }
  }
}
