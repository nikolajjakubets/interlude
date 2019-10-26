//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.GameTimeController;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Location;

public class CharSelected extends L2GameServerPacket {
  private int _sessionId;
  private int char_id;
  private int clan_id;
  private int sex;
  private int race;
  private int class_id;
  private String _name;
  private String _title;
  private Location _loc;
  private double curHp;
  private double curMp;
  private int _sp;
  private int level;
  private int karma;
  private int _int;
  private int _str;
  private int _con;
  private int _men;
  private int _dex;
  private int _wit;
  private int _pk;
  private long _exp;

  public CharSelected(Player cha, int sessionId) {
    this._sessionId = sessionId;
    this._name = cha.getName();
    this.char_id = cha.getObjectId();
    this._title = cha.getTitle();
    this.clan_id = cha.getClanId();
    this.sex = cha.getSex();
    this.race = cha.getRace().ordinal();
    this.class_id = cha.getClassId().getId();
    this._loc = cha.getLoc();
    this.curHp = cha.getCurrentHp();
    this.curMp = cha.getCurrentMp();
    this._sp = cha.getIntSp();
    this._exp = cha.getExp();
    this.level = cha.getLevel();
    this.karma = cha.getKarma();
    this._pk = cha.getPkKills();
    this._int = cha.getINT();
    this._str = cha.getSTR();
    this._con = cha.getCON();
    this._men = cha.getMEN();
    this._dex = cha.getDEX();
    this._wit = cha.getWIT();
  }

  protected final void writeImpl() {
    this.writeC(21);
    this.writeS(this._name);
    this.writeD(this.char_id);
    this.writeS(this._title);
    this.writeD(this._sessionId);
    this.writeD(this.clan_id);
    this.writeD(0);
    this.writeD(this.sex);
    this.writeD(this.race);
    this.writeD(this.class_id);
    this.writeD(1);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeF(this.curHp);
    this.writeF(this.curMp);
    this.writeD(this._sp);
    this.writeQ(this._exp);
    this.writeD(this.level);
    this.writeD(this.karma);
    this.writeD(this._pk);
    this.writeD(this._int);
    this.writeD(this._str);
    this.writeD(this._con);
    this.writeD(this._men);
    this.writeD(this._dex);
    this.writeD(this._wit);

    for(int i = 0; i < 30; ++i) {
      this.writeD(0);
    }

    this.writeF(0.0D);
    this.writeF(0.0D);
    this.writeD(GameTimeController.getInstance().getGameTime());
    this.writeD(0);
    this.writeD(0);
    this.writeC(0);
    this.writeH(0);
    this.writeH(0);
    this.writeD(0);
  }
}
