//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Macro;
import l2.gameserver.model.actor.instances.player.Macro.L2MacroCmd;
import l2.gameserver.network.l2.GameClient;

public class RequestMakeMacro extends L2GameClientPacket {
  private Macro _macro;

  public RequestMakeMacro() {
  }

  protected void readImpl() {
    int _id = this.readD();
    String _name = this.readS(32);
    String _desc = this.readS(64);
    String _acronym = this.readS(4);
    int _icon = this.readC();
    int _count = this.readC();
    if (_count > 12) {
      _count = 12;
    }

    L2MacroCmd[] commands = new L2MacroCmd[_count];

    for(int i = 0; i < _count; ++i) {
      int entry = this.readC();
      int type = this.readC();
      int d1 = this.readD();
      int d2 = this.readC();
      String command = this.readS().replace(";", "").replace(",", "");
      commands[i] = new L2MacroCmd(entry, type, d1, d2, command);
    }

    this._macro = new Macro(_id, _icon, _name, _desc, _acronym, commands);
  }

  protected void runImpl() {
    Player activeChar = ((GameClient)this.getClient()).getActiveChar();
    if (activeChar != null) {
      if (activeChar.getMacroses().getAllMacroses().length > 48) {
        activeChar.sendPacket(Msg.YOU_MAY_CREATE_UP_TO_48_MACROS);
      } else if (this._macro.name.length() == 0) {
        activeChar.sendPacket(Msg.ENTER_THE_NAME_OF_THE_MACRO);
      } else if (this._macro.descr.length() > 32) {
        activeChar.sendPacket(Msg.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
      } else {
        activeChar.registerMacro(this._macro);
      }
    }
  }
}
