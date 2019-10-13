//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import org.apache.commons.lang3.tuple.Pair;

public class ConfirmDlg extends L2GameClientPacket {
  private int _answer;
  private int _requestId;

  public ConfirmDlg() {
  }

  protected void readImpl() {
    this.readD();
    this._answer = this.readD();
    this._requestId = this.readD();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(true);
      if (entry != null && (Integer)entry.getKey() == this._requestId) {
        OnAnswerListener listener = (OnAnswerListener)entry.getValue();
        if (this._answer == 1) {
          listener.sayYes();
        } else {
          listener.sayNo();
        }

      }
    }
  }
}
