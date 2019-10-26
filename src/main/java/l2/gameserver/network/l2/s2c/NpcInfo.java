//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.Location;

public class NpcInfo extends L2GameServerPacket {
  private boolean can_writeImpl = false;
  private int _npcObjId;
  private int _npcId;
  private int running;
  private int incombat;
  private int dead;
  private int _showSpawnAnimation;
  private int _runSpd;
  private int _walkSpd;
  private int _mAtkSpd;
  private int _pAtkSpd;
  private int _rhand;
  private int _lhand;
  private int _enchantEffect;
  private int karma;
  private int pvp_flag;
  private int _abnormalEffect;
  private int _abnormalEffect2;
  private int clan_id;
  private int clan_crest_id;
  private int ally_id;
  private int ally_crest_id;
  private int _formId;
  private int _titleColor;
  private double colHeight;
  private double colRadius;
  private double currentColHeight;
  private double currentColRadius;
  private double moveAnimMod;
  private double atkAnimMod;
  private boolean _isAttackable;
  private boolean _isNameAbove;
  private boolean isFlying;
  private Location _loc;
  private String _name = "";
  private String _title = "";
  private boolean _showName;
  private int _state;
  private TeamType _team;

  public NpcInfo(NpcInstance cha, Creature attacker) {
    this._npcId = cha.getDisplayId() != 0 ? cha.getDisplayId() : cha.getTemplate().npcId;
    this._isAttackable = attacker != null && cha.isAutoAttackable(attacker);
    this._rhand = cha.getRightHandItem();
    this._lhand = cha.getLeftHandItem();
    this._enchantEffect = 0;
    if (Config.SERVER_SIDE_NPC_NAME || cha.getTemplate().displayId != 0 || cha.getName() != cha.getTemplate().name) {
      this._name = cha.getName();
    }

    if (Config.SERVER_SIDE_NPC_TITLE || cha.getTemplate().displayId != 0 || cha.getTitle() != cha.getTemplate().title) {
      this._title = cha.getTitle();
    }

    this._showSpawnAnimation = cha.getSpawnAnimation();
    this._showName = cha.isShowName();
    this._state = cha.getNpcState();
    this.common(cha);
  }

  public NpcInfo(Summon cha, Creature attacker) {
    if (cha.getPlayer() == null || !cha.getPlayer().isInvisible()) {
      this._npcId = cha.getTemplate().npcId;
      this._isAttackable = cha.isAutoAttackable(attacker);
      this._rhand = 0;
      this._lhand = 0;
      this._enchantEffect = 0;
      this._showName = true;
      this._name = cha.getName();
      this._title = cha.getTitle();
      this._showSpawnAnimation = cha.getSpawnAnimation();
      this.common(cha);
    }
  }

  private void common(Creature cha) {
    this.colHeight = cha.getTemplate().collisionHeight;
    this.colRadius = cha.getTemplate().collisionRadius;
    this.currentColHeight = cha.getColHeight();
    this.currentColRadius = cha.getColRadius();
    this._npcObjId = cha.getObjectId();
    this._loc = cha.getLoc();
    this._mAtkSpd = cha.getMAtkSpd();
    Clan clan;
    Alliance alliance;
    if (Config.ALT_NPC_CLAN == 0) {
      clan = cha.getClan();
      alliance = clan == null ? null : clan.getAlliance();
      this.clan_id = clan == null ? 0 : clan.getClanId();
      this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
      this.ally_id = alliance == null ? 0 : alliance.getAllyId();
      this.ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();
    } else if (cha instanceof NpcInstance && Config.ALT_NPC_CLAN > 0 && ((NpcInstance)cha).getCastle() != null) {
      clan = ClanTable.getInstance().getClan(Config.ALT_NPC_CLAN);
      alliance = clan == null ? null : clan.getAlliance();
      this.clan_id = clan == null ? 0 : clan.getClanId();
      this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
      this.ally_id = alliance == null ? 0 : alliance.getAllyId();
      this.ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();
    } else {
      this.clan_id = 0;
      this.clan_crest_id = 0;
      this.ally_id = 0;
      this.ally_crest_id = 0;
    }

    this.moveAnimMod = cha.getMovementSpeedMultiplier();
    this.atkAnimMod = cha.getAttackSpeedMultiplier();
    this._runSpd = (int)((double)cha.getRunSpeed() / this.moveAnimMod);
    this._walkSpd = (int)((double)cha.getWalkSpeed() / this.moveAnimMod);
    this.karma = cha.getKarma();
    this.pvp_flag = cha.getPvpFlag();
    this._pAtkSpd = cha.getPAtkSpd();
    this.running = cha.isRunning() ? 1 : 0;
    this.incombat = cha.isInCombat() ? 1 : 0;
    this.dead = cha.isAlikeDead() ? 1 : 0;
    this._abnormalEffect = cha.getAbnormalEffect();
    this._abnormalEffect2 = cha.getAbnormalEffect2();
    this.isFlying = cha.isFlying();
    this._team = cha.getTeam();
    this._formId = cha.getFormId();
    this._isNameAbove = cha.isNameAbove();
    this._titleColor = !cha.isSummon() && !cha.isPet() ? 0 : 1;
    this.can_writeImpl = true;
  }

  public NpcInfo update() {
    this._showSpawnAnimation = 1;
    return this;
  }

  protected final void writeImpl() {
    if (this.can_writeImpl) {
      this.writeC(22);
      this.writeD(this._npcObjId);
      this.writeD(this._npcId + 1000000);
      this.writeD(this._isAttackable ? 1 : 0);
      this.writeD(this._loc.x);
      this.writeD(this._loc.y);
      this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
      this.writeD(this._loc.h);
      this.writeD(0);
      this.writeD(this._mAtkSpd);
      this.writeD(this._pAtkSpd);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeD(this._runSpd);
      this.writeD(this._walkSpd);
      this.writeF(this.moveAnimMod);
      this.writeF(this.atkAnimMod);
      this.writeF(this.colRadius);
      this.writeF(this.colHeight);
      this.writeD(this._rhand);
      this.writeD(0);
      this.writeD(this._lhand);
      this.writeC(this._isNameAbove ? 1 : 0);
      this.writeC(this.running);
      this.writeC(this.incombat);
      this.writeC(this.dead);
      this.writeC(this._showSpawnAnimation);
      this.writeS(this._name);
      this.writeS(this._title);
      this.writeD(this._titleColor);
      this.writeD(this.pvp_flag);
      this.writeD(this.karma);
      this.writeD(this._abnormalEffect);
      this.writeD(this.clan_id);
      this.writeD(this.clan_crest_id);
      this.writeD(this.ally_id);
      this.writeD(this.ally_crest_id);
      this.writeC(this.isFlying ? 2 : 0);
      this.writeC(this._team.ordinal());
      this.writeF(this.currentColRadius);
      this.writeF(this.currentColHeight);
      this.writeD(this._enchantEffect);
      this.writeD(this.isFlying ? 1 : 0);
    }
  }
}
