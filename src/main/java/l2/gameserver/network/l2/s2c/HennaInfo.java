//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;

public class HennaInfo extends L2GameServerPacket {
  private final HennaInfo.Henna[] _hennas = new HennaInfo.Henna[3];
  private final int _str;
  private final int _con;
  private final int _dex;
  private final int _int;
  private final int _wit;
  private final int _men;
  private int _count = 0;
  private int slots;

  public HennaInfo(Player player) {
    for(int i = 0; i < 3; ++i) {
      l2.gameserver.templates.Henna h;
      if ((h = player.getHenna(i + 1)) != null) {
        this._hennas[this._count++] = new HennaInfo.Henna(h.getSymbolId(), h.isForThisClass(player));
      }
    }

    this._str = player.getHennaStatSTR();
    this._con = player.getHennaStatCON();
    this._dex = player.getHennaStatDEX();
    this._int = player.getHennaStatINT();
    this._wit = player.getHennaStatWIT();
    this._men = player.getHennaStatMEN();
    this.slots = player.getLevel() < 40 ? 2 : 3;
  }

  protected final void writeImpl() {
    this.writeC(228);
    this.writeC(this._int);
    this.writeC(this._str);
    this.writeC(this._con);
    this.writeC(this._men);
    this.writeC(this._dex);
    this.writeC(this._wit);
    this.writeD(this.slots);
    this.writeD(this._count);

    for(int i = 0; i < this._count; ++i) {
      this.writeD(this._hennas[i]._symbolId);
      this.writeD(this._hennas[i]._valid ? this._hennas[i]._symbolId : 0);
    }

  }

  private static class Henna {
    private int _symbolId;
    private boolean _valid;

    public Henna(int sy, boolean valid) {
      this._symbolId = sy;
      this._valid = valid;
    }
  }
}
