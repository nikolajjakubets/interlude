//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.actor.instances.player.Macro;
import l2.gameserver.model.actor.instances.player.Macro.L2MacroCmd;

public class SendMacroList extends L2GameServerPacket {
  private final int _rev;
  private final int _count;
  private final Macro _macro;

  public SendMacroList(int rev, int count, Macro macro) {
    this._rev = rev;
    this._count = count;
    this._macro = macro;
  }

  protected final void writeImpl() {
    this.writeC(231);
    this.writeD(this._rev);
    this.writeC(0);
    this.writeC(this._count);
    this.writeC(this._macro != null ? 1 : 0);
    if (this._macro != null) {
      this.writeD(this._macro.id);
      this.writeS(this._macro.name);
      this.writeS(this._macro.descr);
      this.writeS(this._macro.acronym);
      this.writeC(this._macro.icon);
      this.writeC(this._macro.commands.length);

      for(int i = 0; i < this._macro.commands.length; ++i) {
        L2MacroCmd cmd = this._macro.commands[i];
        this.writeC(i + 1);
        this.writeC(cmd.type);
        this.writeD(cmd.d1);
        this.writeC(cmd.d2);
        this.writeS(cmd.cmd);
      }
    }

  }
}
