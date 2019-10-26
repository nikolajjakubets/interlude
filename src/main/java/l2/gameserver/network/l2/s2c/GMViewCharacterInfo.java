//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.utils.Location;

public class GMViewCharacterInfo extends L2GameServerPacket {
  private Location _loc;
  private int[][] _inv;
  private int obj_id;
  private int _race;
  private int _sex;
  private int class_id;
  private int pvp_flag;
  private int karma;
  private int level;
  private int mount_type;
  private int _str;
  private int _con;
  private int _dex;
  private int _int;
  private int _wit;
  private int _men;
  private int _sp;
  private int curHp;
  private int maxHp;
  private int curMp;
  private int maxMp;
  private int curCp;
  private int maxCp;
  private int curLoad;
  private int maxLoad;
  private int rec_left;
  private int rec_have;
  private int _patk;
  private int _patkspd;
  private int _pdef;
  private int evasion;
  private int accuracy;
  private int crit;
  private int _matk;
  private int _matkspd;
  private int _mdef;
  private int hair_style;
  private int hair_color;
  private int face;
  private int gm_commands;
  private int clan_id;
  private int clan_crest_id;
  private int ally_id;
  private int title_color;
  private int noble;
  private int hero;
  private int private_store;
  private int name_color;
  private int pk_kills;
  private int pvp_kills;
  private int _runSpd;
  private int _walkSpd;
  private int _swimSpd;
  private int DwarvenCraftLevel;
  private int running;
  private int pledge_class;
  private String _name;
  private String title;
  private long _exp;
  private double move_speed;
  private double attack_speed;
  private double col_radius;
  private double col_height;
  private Element attackElement;

  public GMViewCharacterInfo(Player cha) {
    this._loc = cha.getLoc();
    this.obj_id = cha.getObjectId();
    this._name = cha.getName();
    this._race = cha.getRace().ordinal();
    this._sex = cha.getSex();
    this.class_id = cha.getClassId().getId();
    this.level = cha.getLevel();
    this._exp = cha.getExp();
    this._str = cha.getSTR();
    this._dex = cha.getDEX();
    this._con = cha.getCON();
    this._int = cha.getINT();
    this._wit = cha.getWIT();
    this._men = cha.getMEN();
    this.curHp = (int)cha.getCurrentHp();
    this.maxHp = cha.getMaxHp();
    this.curMp = (int)cha.getCurrentMp();
    this.maxMp = cha.getMaxMp();
    this._sp = cha.getIntSp();
    this.curLoad = cha.getCurrentLoad();
    this.maxLoad = cha.getMaxLoad();
    this._patk = cha.getPAtk((Creature)null);
    this._patkspd = cha.getPAtkSpd();
    this._pdef = cha.getPDef((Creature)null);
    this.evasion = cha.getEvasionRate((Creature)null);
    this.accuracy = cha.getAccuracy();
    this.crit = cha.getCriticalHit((Creature)null, (Skill)null);
    this._matk = cha.getMAtk((Creature)null, (Skill)null);
    this._matkspd = cha.getMAtkSpd();
    this._mdef = cha.getMDef((Creature)null, (Skill)null);
    this.pvp_flag = cha.getPvpFlag();
    this.karma = cha.getKarma();
    this._runSpd = cha.getRunSpeed();
    this._walkSpd = cha.getWalkSpeed();
    this._swimSpd = cha.getSwimSpeed();
    this.move_speed = cha.getMovementSpeedMultiplier();
    this.attack_speed = cha.getAttackSpeedMultiplier();
    this.mount_type = cha.getMountType();
    this.col_radius = cha.getColRadius();
    this.col_height = cha.getColHeight();
    this.hair_style = cha.getHairStyle();
    this.hair_color = cha.getHairColor();
    this.face = cha.getFace();
    this.gm_commands = cha.isGM() ? 1 : 0;
    this.title = cha.getTitle();
    Clan clan = cha.getClan();
    Alliance alliance = clan == null ? null : clan.getAlliance();
    this.clan_id = clan == null ? 0 : clan.getClanId();
    this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
    this.ally_id = alliance == null ? 0 : alliance.getAllyId();
    this.private_store = cha.isInObserverMode() ? 7 : cha.getPrivateStoreType();
    this.DwarvenCraftLevel = Math.max(cha.getSkillLevel(1320), 0);
    this.pk_kills = cha.getPkKills();
    this.pvp_kills = cha.getPvpKills();
    this.rec_left = cha.getGivableRec();
    this.rec_have = cha.getReceivedRec();
    this.curCp = (int)cha.getCurrentCp();
    this.maxCp = cha.getMaxCp();
    this.running = cha.isRunning() ? 1 : 0;
    this.pledge_class = cha.getPledgeClass();
    this.noble = cha.isNoble() ? 1 : 0;
    this.hero = cha.isHero() ? 1 : 0;
    this.name_color = cha.getNameColor();
    this.title_color = cha.getTitleColor();
    this.attackElement = cha.getAttackElement();
    this._inv = new int[17][3];
    int[] var4 = Inventory.PAPERDOLL_ORDER;
    int var5 = var4.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      int PAPERDOLL_ID = var4[var6];
      this._inv[PAPERDOLL_ID][0] = cha.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
      this._inv[PAPERDOLL_ID][1] = cha.getInventory().getPaperdollItemId(PAPERDOLL_ID);
      this._inv[PAPERDOLL_ID][2] = cha.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
    }

  }

  protected final void writeImpl() {
    this.writeC(143);
    this.writeD(this._loc.x);
    this.writeD(this._loc.y);
    this.writeD(this._loc.z);
    this.writeD(this._loc.h);
    this.writeD(this.obj_id);
    this.writeS(this._name);
    this.writeD(this._race);
    this.writeD(this._sex);
    this.writeD(this.class_id);
    this.writeD(this.level);
    this.writeQ(this._exp);
    this.writeD(this._str);
    this.writeD(this._dex);
    this.writeD(this._con);
    this.writeD(this._int);
    this.writeD(this._wit);
    this.writeD(this._men);
    this.writeD(this.maxHp);
    this.writeD(this.curHp);
    this.writeD(this.maxMp);
    this.writeD(this.curMp);
    this.writeD(this._sp);
    this.writeD(this.curLoad);
    this.writeD(this.maxLoad);
    this.writeD(this.pk_kills);
    int[] var1 = Inventory.PAPERDOLL_ORDER;
    int var2 = var1.length;

    int var3;
    int PAPERDOLL_ID;
    for(var3 = 0; var3 < var2; ++var3) {
      PAPERDOLL_ID = var1[var3];
      this.writeD(this._inv[PAPERDOLL_ID][0]);
    }

    var1 = Inventory.PAPERDOLL_ORDER;
    var2 = var1.length;

    for(var3 = 0; var3 < var2; ++var3) {
      PAPERDOLL_ID = var1[var3];
      this.writeD(this._inv[PAPERDOLL_ID][1]);
    }

    var1 = Inventory.PAPERDOLL_ORDER;
    var2 = var1.length;

    for(var3 = 0; var3 < var2; ++var3) {
      PAPERDOLL_ID = var1[var3];
      this.writeD(this._inv[PAPERDOLL_ID][2]);
    }

    this.writeD(this._patk);
    this.writeD(this._patkspd);
    this.writeD(this._pdef);
    this.writeD(this.evasion);
    this.writeD(this.accuracy);
    this.writeD(this.crit);
    this.writeD(this._matk);
    this.writeD(this._matkspd);
    this.writeD(this._patkspd);
    this.writeD(this._mdef);
    this.writeD(this.pvp_flag);
    this.writeD(this.karma);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeD(this._swimSpd);
    this.writeD(this._swimSpd);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeD(this._runSpd);
    this.writeD(this._walkSpd);
    this.writeF(this.move_speed);
    this.writeF(this.attack_speed);
    this.writeF(this.col_radius);
    this.writeF(this.col_height);
    this.writeD(this.hair_style);
    this.writeD(this.hair_color);
    this.writeD(this.face);
    this.writeD(this.gm_commands);
    this.writeS(this.title);
    this.writeD(this.clan_id);
    this.writeD(this.clan_crest_id);
    this.writeD(this.ally_id);
    this.writeC(this.mount_type);
    this.writeC(this.private_store);
    this.writeC(this.DwarvenCraftLevel);
    this.writeD(this.pk_kills);
    this.writeD(this.pvp_kills);
    this.writeH(this.rec_left);
    this.writeH(this.rec_have);
    this.writeD(this.class_id);
    this.writeD(0);
    this.writeD(this.maxCp);
    this.writeD(this.curCp);
    this.writeC(this.running);
    this.writeC(321);
    this.writeD(this.pledge_class);
    this.writeC(this.noble);
    this.writeC(this.hero);
    this.writeD(this.name_color);
    this.writeD(this.title_color);
  }
}
