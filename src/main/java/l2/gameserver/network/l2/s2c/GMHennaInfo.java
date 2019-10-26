//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.templates.Henna;

public class GMHennaInfo extends L2GameServerPacket {
  private int _count;
  private int _str;
  private int _con;
  private int _dex;
  private int _int;
  private int _wit;
  private int _men;
  private final Henna[] _hennas = new Henna[3];

  public GMHennaInfo(Player cha) {
    this._str = cha.getHennaStatSTR();
    this._con = cha.getHennaStatCON();
    this._dex = cha.getHennaStatDEX();
    this._int = cha.getHennaStatINT();
    this._wit = cha.getHennaStatWIT();
    this._men = cha.getHennaStatMEN();
    int j = 0;

    for(int i = 0; i < 3; ++i) {
      Henna h = cha.getHenna(i + 1);
      if (h != null) {
        this._hennas[j++] = h;
      }
    }

    this._count = j;
  }

  protected final void writeImpl() {
    this.writeC(234);
    this.writeC(this._int);
    this.writeC(this._str);
    this.writeC(this._con);
    this.writeC(this._men);
    this.writeC(this._dex);
    this.writeC(this._wit);
    this.writeD(3);
    this.writeD(this._count);

    for(int i = 0; i < this._count; ++i) {
      this.writeD(this._hennas[i].getSymbolId());
      this.writeD(this._hennas[i].getSymbolId());
    }

  }
}
