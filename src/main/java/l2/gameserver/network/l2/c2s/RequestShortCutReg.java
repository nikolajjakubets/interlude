//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ShortCutRegister;

public class RequestShortCutReg extends L2GameClientPacket {
  private int _type;
  private int _id;
  private int _slot;
  private int _page;
  private int _lvl;
  private int _characterType;

  public RequestShortCutReg() {
  }

  protected void readImpl() {
    this._type = this.readD();
    int slot = this.readD();
    this._id = this.readD();
    this._characterType = this.readD();
    this._slot = slot % 12;
    this._page = slot / 12;
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (this._page >= 0 && this._page <= 11) {
        switch(this._type) {
          case 2:
            this._lvl = activeChar.getSkillLevel(this._id);
            break;
          default:
            this._lvl = 0;
        }

        ShortCut shortCut = new ShortCut(this._slot, this._page, this._type, this._id, this._lvl, this._characterType);
        activeChar.sendPacket(new ShortCutRegister(activeChar, shortCut));
        activeChar.registerShortCut(shortCut);
      } else {
        activeChar.sendActionFailed();
      }
    }
  }
}
