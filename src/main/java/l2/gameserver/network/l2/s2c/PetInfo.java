//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.utils.Location;

public class PetInfo extends L2GameServerPacket {
  private int _runSpd;
  private int _walkSpd;
  private int MAtkSpd;
  private int PAtkSpd;
  private int pvp_flag;
  private int karma;
  private int rideable;
  private int _type;
  private int obj_id;
  private int npc_id;
  private int runing;
  private int incombat;
  private int dead;
  private int _sp;
  private int level;
  private int _abnormalEffect;
  private int _abnormalEffect2;
  private int curFed;
  private int maxFed;
  private int curHp;
  private int maxHp;
  private int curMp;
  private int maxMp;
  private int curLoad;
  private int maxLoad;
  private int PAtk;
  private int PDef;
  private int MAtk;
  private int MDef;
  private int Accuracy;
  private int Evasion;
  private int Crit;
  private int sps;
  private int ss;
  private int type;
  private int _showSpawnAnimation;
  private Location _loc;
  private double col_redius;
  private double col_height;
  private long exp;
  private long exp_this_lvl;
  private long exp_next_lvl;
  private String _name;
  private String title;
  private TeamType _team;

  public PetInfo(Summon summon) {
    this._type = summon.getSummonType();
    this.obj_id = summon.getObjectId();
    this.npc_id = summon.getTemplate().npcId;
    this._loc = summon.getLoc();
    this.MAtkSpd = summon.getMAtkSpd();
    this.PAtkSpd = summon.getPAtkSpd();
    this._runSpd = summon.getRunSpeed();
    this._walkSpd = summon.getWalkSpeed();
    this.col_redius = summon.getColRadius();
    this.col_height = summon.getColHeight();
    this.runing = summon.isRunning() ? 1 : 0;
    this.incombat = summon.isInCombat() ? 1 : 0;
    this.dead = summon.isAlikeDead() ? 1 : 0;
    this._name = summon.getName().equalsIgnoreCase(summon.getTemplate().name) ? "" : summon.getName();
    this.title = summon.getTitle();
    this.pvp_flag = summon.getPvpFlag();
    this.karma = summon.getKarma();
    this.curFed = summon.getCurrentFed();
    this.maxFed = summon.getMaxFed();
    this.curHp = (int)summon.getCurrentHp();
    this.maxHp = summon.getMaxHp();
    this.curMp = (int)summon.getCurrentMp();
    this.maxMp = summon.getMaxMp();
    this._sp = summon.getSp();
    this.level = summon.getLevel();
    this.exp = summon.getExp();
    this.exp_this_lvl = summon.getExpForThisLevel();
    this.exp_next_lvl = summon.getExpForNextLevel();
    this.curLoad = summon.isPet() ? summon.getInventory().getTotalWeight() : 0;
    this.maxLoad = summon.getMaxLoad();
    this.PAtk = summon.getPAtk((Creature)null);
    this.PDef = summon.getPDef((Creature)null);
    this.MAtk = summon.getMAtk((Creature)null, (Skill)null);
    this.MDef = summon.getMDef((Creature)null, (Skill)null);
    this.Accuracy = summon.getAccuracy();
    this.Evasion = summon.getEvasionRate((Creature)null);
    this.Crit = summon.getCriticalHit((Creature)null, (Skill)null);
    this._abnormalEffect = summon.getAbnormalEffect();
    this._abnormalEffect2 = summon.getAbnormalEffect2();
    if (summon.getPlayer().getTransformation() == 0 && !summon.getPlayer().isCursedWeaponEquipped()) {
      this.rideable = PetDataTable.isMountable(this.npc_id) ? 1 : 0;
    } else {
      this.rideable = 0;
    }

    this._team = summon.getTeam();
    this.ss = summon.getSoulshotConsumeCount();
    this.sps = summon.getSpiritshotConsumeCount();
    this._showSpawnAnimation = summon.getSpawnAnimation();
    this.type = summon.getFormId();
  }

  public PetInfo update() {
    this._showSpawnAnimation = 1;
    return this;
  }

  protected final void writeImpl() {
    this.writeC(177);
    this.writeD(this._type);
    this.writeD(this.obj_id);
    this.writeD(this.npc_id + 1000000);
    this.writeD(0);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._loc.h);
    this.writeD(0);
    this.writeD(this.MAtkSpd);
    this.writeD(this.PAtkSpd);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeF(1.0D);
    this.writeF(1.0D);
    this.writeF(this.col_redius);
    this.writeF(this.col_height);
    this.writeD(0);
    this.writeD(0);
    this.writeD(0);
    this.writeC(1);
    this.writeC(this.runing);
    this.writeC(this.incombat);
    this.writeC(this.dead);
    this.writeC(this._showSpawnAnimation);
    this.writeS(this._name);
    this.writeS(this.title);
    this.writeD(1);
    this.writeD(this.pvp_flag);
    this.writeD(this.karma);
    this.writeD(this.curFed);
    this.writeD(this.maxFed);
    this.writeD(this.curHp);
    this.writeD(this.maxHp);
    this.writeD(this.curMp);
    this.writeD(this.maxMp);
    this.writeD(this._sp);
    this.writeD(this.level);
    this.writeQ(this.exp);
    this.writeQ(this.exp_this_lvl);
    this.writeQ(this.exp_next_lvl);
    this.writeD(this.curLoad);
    this.writeD(this.maxLoad);
    this.writeD(this.PAtk);
    this.writeD(this.PDef);
    this.writeD(this.MAtk);
    this.writeD(this.MDef);
    this.writeD(this.Accuracy);
    this.writeD(this.Evasion);
    this.writeD(this.Crit);
    this.writeD(this._runSpd);
    this.writeD(this.PAtkSpd);
    this.writeD(this.MAtkSpd);
    this.writeD(this._abnormalEffect);
    this.writeH(this.rideable);
    this.writeC(0);
    this.writeH(0);
    this.writeC(this._team.ordinal());
    this.writeD(this.ss);
    this.writeD(this.sps);
  }
}
