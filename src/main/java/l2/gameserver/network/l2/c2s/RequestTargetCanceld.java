//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.GameClient;

public class RequestTargetCanceld extends L2GameClientPacket {
  private int _unselect;

  public RequestTargetCanceld() {
  }

  protected void readImpl() {
    this._unselect = this.readH();
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._unselect == 0) {
        if (activeChar.isCastingNow()) {
          Skill skill = activeChar.getCastingSkill();
          activeChar.abortCast(skill != null && (skill.isHandler() || skill.getHitTime() > 1000), false);
        } else if (activeChar.getTarget() != null) {
          activeChar.setTarget((GameObject)null);
        }
      } else if (activeChar.getTarget() != null) {
        activeChar.setTarget((GameObject)null);
      }

    }
  }
}
