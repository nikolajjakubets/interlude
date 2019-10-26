//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.templates.npc.NpcTemplate;

public class NpcInfoPoly extends L2GameServerPacket {
  private Creature _obj;
  private int _x;
  private int _y;
  private int _z;
  private int _heading;
  private int _npcId;
  private boolean _isSummoned;
  private boolean _isRunning;
  private boolean _isInCombat;
  private boolean _isAlikeDead;
  private int _mAtkSpd;
  private int _pAtkSpd;
  private int _runSpd;
  private int _walkSpd;
  private int _swimRunSpd;
  private int _swimWalkSpd;
  private int _flRunSpd;
  private int _flWalkSpd;
  private int _flyRunSpd;
  private int _flyWalkSpd;
  private int _rhand;
  private int _lhand;
  private String _name;
  private String _title;
  private int _abnormalEffect;
  private int _abnormalEffect2;
  private double colRadius;
  private double colHeight;
  private TeamType _team;

  public NpcInfoPoly(Player cha) {
    this._obj = cha;
    this._npcId = cha.getPolyId();
    NpcTemplate template = NpcHolder.getInstance().getTemplate(this._npcId);
    this._rhand = 0;
    this._lhand = 0;
    this._isSummoned = false;
    this.colRadius = template.collisionRadius;
    this.colHeight = template.collisionHeight;
    this._x = this._obj.getX();
    this._y = this._obj.getY();
    this._z = this._obj.getZ();
    this._rhand = template.rhand;
    this._lhand = template.lhand;
    this._heading = cha.getHeading();
    this._mAtkSpd = cha.getMAtkSpd();
    this._pAtkSpd = cha.getPAtkSpd();
    this._runSpd = cha.getRunSpeed();
    this._walkSpd = cha.getWalkSpeed();
    this._swimRunSpd = this._flRunSpd = this._flyRunSpd = this._runSpd;
    this._swimWalkSpd = this._flWalkSpd = this._flyWalkSpd = this._walkSpd;
    this._isRunning = cha.isRunning();
    this._isInCombat = cha.isInCombat();
    this._isAlikeDead = cha.isAlikeDead();
    this._name = cha.getName();
    this._title = cha.getTitle();
    this._abnormalEffect = cha.getAbnormalEffect();
    this._abnormalEffect2 = cha.getAbnormalEffect2();
    this._team = cha.getTeam();
  }

  protected final void writeImpl() {
    this.writeC(22);
    this.writeD(this._obj.getObjectId());
    this.writeD(this._npcId + 1000000);
    this.writeD(0);
    this.writeD(this._x);
    this.writeD(this._y);
    this.writeD(this._z);
    this.writeD(this._heading);
    this.writeD(0);
    this.writeD(this._mAtkSpd);
    this.writeD(this._pAtkSpd);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeD(this._swimRunSpd);
    this.writeD(this._swimWalkSpd);
    this.writeD(this._flRunSpd);
    this.writeD(this._flWalkSpd);
    this.writeD(this._flyRunSpd);
    this.writeD(this._flyWalkSpd);
    this.writeF(1.0D);
    this.writeF(1.0D);
    this.writeF(this.colRadius);
    this.writeF(this.colHeight);
    this.writeD(this._rhand);
    this.writeD(0);
    this.writeD(this._lhand);
    this.writeC(1);
    this.writeC(this._isRunning ? 1 : 0);
    this.writeC(this._isInCombat ? 1 : 0);
    this.writeC(this._isAlikeDead ? 1 : 0);
    this.writeC(this._isSummoned ? 2 : 0);
    this.writeS(this._name);
    this.writeS(this._title);
    this.writeD(0);
    this.writeD(0);
    this.writeD(0);
    this.writeD(this._abnormalEffect);
    this.writeD(0);
    this.writeD(0);
    this.writeD(0);
    this.writeD(0);
    this.writeC(0);
    this.writeC(this._team.ordinal());
    this.writeF(this.colRadius);
    this.writeF(this.colHeight);
    this.writeD(0);
    this.writeD(0);
  }
}
