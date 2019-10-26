//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.templates.Henna;

public class HennaUnequipInfo extends L2GameServerPacket {
  private int _str;
  private int _con;
  private int _dex;
  private int _int;
  private int _wit;
  private int _men;
  private long _adena;
  private Henna _henna;

  public HennaUnequipInfo(Henna henna, Player player) {
    this._henna = henna;
    this._adena = player.getAdena();
    this._str = player.getSTR();
    this._dex = player.getDEX();
    this._con = player.getCON();
    this._int = player.getINT();
    this._wit = player.getWIT();
    this._men = player.getMEN();
  }

  protected final void writeImpl() {
    this.writeC(230);
    this.writeD(this._henna.getSymbolId());
    this.writeD(this._henna.getDyeId());
    this.writeD((int)this._henna.getDrawCount());
    this.writeD((int)this._henna.getPrice());
    this.writeD(1);
    this.writeD((int)this._adena);
    this.writeD(this._int);
    this.writeC(this._int + this._henna.getStatINT());
    this.writeD(this._str);
    this.writeC(this._str + this._henna.getStatSTR());
    this.writeD(this._con);
    this.writeC(this._con + this._henna.getStatCON());
    this.writeD(this._men);
    this.writeC(this._men + this._henna.getStatMEN());
    this.writeD(this._dex);
    this.writeC(this._dex + this._henna.getStatDEX());
    this.writeD(this._wit);
    this.writeC(this._wit + this._henna.getStatWIT());
  }
}
