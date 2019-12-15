//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.utils.Location;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CharInfo extends L2GameServerPacket {
  private int[][] _inv;
  private int _mAtkSpd;
  private int _pAtkSpd;
  private int _runSpd;
  private int _walkSpd;
  private int _swimSpd;
  private int _flRunSpd;
  private int _flWalkSpd;
  private int _flyRunSpd;
  private int _flyWalkSpd;
  private Location _loc;
  private Location _fishLoc;
  private String _name;
  private String _title;
  private int _objId;
  private int _race;
  private int _sex;
  private int base_class;
  private int pvp_flag;
  private int karma;
  private int rec_have;
  private double moveAnimMod;
  private double atkAnimMod;
  private double col_radius;
  private double col_height;
  private int hair_style;
  private int hair_color;
  private int face;
  private int _abnormalEffect;
  private int _abnormalEffect2;
  private int clan_id;
  private int clan_crest_id;
  private int large_clan_crest_id;
  private int ally_id;
  private int ally_crest_id;
  private int class_id;
  private int _sit;
  private int _run;
  private int _combat;
  private int _dead;
  private int private_store;
  private int _enchant;
  private int _noble;
  private int _hero;
  private int _fishing;
  private int mount_type;
  private int plg_class;
  private int pledge_type;
  private int clan_rep_score;
  private int cw_level;
  private int mount_id;
  private int _nameColor;
  private int _title_color;
  private int _transform;
  private int _agathion;
  private int _clanBoatObjectId;
  private EffectCubic[] cubics;
  private boolean _isPartyRoomLeader;
  private boolean _isFlying;
  private TeamType _team;
  public static final int[] PAPERDOLL_ORDER = new int[]{0, 6, 7, 8, 9, 10, 11, 12, 13, 7, 15, 16};

  public CharInfo(Player cha) {
    this((Creature) cha);
  }

  public CharInfo(Creature cha) {
    if (cha == null) {
      System.out.println("CharInfo: cha is null!");
      Thread.dumpStack();
    } else if (!cha.isInvisible()) {
      if (!cha.isDeleted()) {
        Player player = cha.getPlayer();
        if (player != null) {
          if (this._loc == null) {
            this._loc = cha.getLoc();
          }

          this._objId = cha.getObjectId();
          if (player.getTransformationName() == null && (player.getReflection() != ReflectionManager.GIRAN_HARBOR || player.getPrivateStoreType() == 0)) {
            this._name = player.getName();
            if (player.getPrivateStoreType() != 0) {
              this._title = "";
            } else if (!player.isConnected()) {
              this._title = player.getDisconnectedTitle();
              this._title_color = player.getDisconnectedTitleColor();
            } else {
              this._title = player.getTitle();
              this._title_color = player.getTitleColor();
            }

            Clan clan = player.getClan();
            Alliance alliance = clan == null ? null : clan.getAlliance();
            this.clan_id = clan == null ? 0 : clan.getClanId();
            this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
            this.large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
            this.ally_id = alliance == null ? 0 : alliance.getAllyId();
            this.ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();
            this.cw_level = 0;
          } else {
            this._name = player.getTransformationName() != null ? player.getTransformationName() : player.getName();
            this._title = player.getTransformationTitle() != null ? player.getTransformationTitle() : "";
            this.clan_id = 0;
            this.clan_crest_id = 0;
            this.ally_id = 0;
            this.ally_crest_id = 0;
            this.large_clan_crest_id = 0;
            if (player.isCursedWeaponEquipped()) {
              this.cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
            }
          }

          if (player.isMounted()) {
            this._enchant = 0;
            this.mount_id = player.getMountNpcId() + 1000000;
            this.mount_type = player.getMountType();
          } else {
            this._enchant = player.getEnchantEffect();
            this.mount_id = 0;
            this.mount_type = 0;
          }

          this._inv = new int[17][2];

          for (int PAPERDOLL_ID : PAPERDOLL_ORDER) {
            this._inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
            this._inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
          }

          this._mAtkSpd = player.getMAtkSpd();
          this._pAtkSpd = player.getPAtkSpd();
          this.moveAnimMod = player.getMovementSpeedMultiplier();
          this._runSpd = (int) ((double) player.getRunSpeed() / this.moveAnimMod);
          this._walkSpd = (int) ((double) player.getWalkSpeed() / this.moveAnimMod);
          this._flRunSpd = 0;
          this._flWalkSpd = 0;
          if (player.isFlying()) {
            this._flyRunSpd = this._runSpd;
            this._flyWalkSpd = this._walkSpd;
          } else {
            this._flyRunSpd = 0;
            this._flyWalkSpd = 0;
          }

          this._swimSpd = player.getSwimSpeed();
          this._race = player.getBaseTemplate().race.ordinal();
          this._sex = player.getSex();
          this.base_class = player.getBaseClassId();
          this.pvp_flag = player.getPvpFlag();
          this.karma = player.getKarma();
          this.atkAnimMod = player.getAttackSpeedMultiplier();
          this.col_radius = player.getColRadius();
          this.col_height = player.getColHeight();
          this.hair_style = player.getHairStyle();
          this.hair_color = player.getHairColor();
          this.face = player.getFace();
          if (this.clan_id > 0 && player.getClan() != null) {
            this.clan_rep_score = player.getClan().getReputationScore();
          } else {
            this.clan_rep_score = 0;
          }

          this._sit = player.isSitting() ? 0 : 1;
          this._run = player.isRunning() ? 1 : 0;
          this._combat = player.isInCombat() ? 1 : 0;
          this._dead = player.isAlikeDead() ? 1 : 0;
          this.private_store = player.isInObserverMode() ? 7 : player.getPrivateStoreType();
          this.cubics = player.getCubics().toArray(new EffectCubic[0]);
          this._abnormalEffect = player.getAbnormalEffect();
          this._abnormalEffect2 = player.getAbnormalEffect2();
          this.rec_have = player.getReceivedRec();
          this.class_id = player.getClassId().getId();
          this._team = player.getTeam();
          this._noble = player.isNoble() ? 1 : 0;
          this._hero = !player.isHero() && (!player.isGM() || !Config.GM_HERO_AURA) ? 0 : 1;
          this._fishing = player.isFishing() ? 1 : 0;
          this._fishLoc = player.getFishLoc();
          this._nameColor = player.getNameColor();
          this.plg_class = player.getPledgeClass();
          this.pledge_type = player.getPledgeType();
          this._transform = player.getTransformation();
          this._agathion = player.getAgathionId();
          this._isPartyRoomLeader = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
          this._isFlying = player.isInFlyingTransform();
        }
      }
    }
  }

  protected final void writeImpl() {
    Player activeChar = this.getClient().getActiveChar();
    if (activeChar != null) {
      if (this._objId != 0) {
        if (activeChar.getObjectId() == this._objId) {
          log.error("You cant send CharInfo about his character to active user!!!");
        } else {
          this.writeC(3);
          this.writeD(this._loc.x);
          this.writeD(this._loc.y);
          this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
          this.writeD(this._loc.h);
          this.writeD(this._objId);
          this.writeS(this._name);
          this.writeD(this._race);
          this.writeD(this._sex);
          this.writeD(this.base_class);
          int[] var2 = PAPERDOLL_ORDER;
          int var3 = var2.length;

          int var4;
          int PAPERDOLL_ID;
          for (var4 = 0; var4 < var3; ++var4) {
            PAPERDOLL_ID = var2[var4];
            this.writeD(this._inv[PAPERDOLL_ID][0]);
          }

          var2 = PAPERDOLL_ORDER;
          var3 = var2.length;

          for (var4 = 0; var4 < var3; ++var4) {
            PAPERDOLL_ID = var2[var4];
            this.writeD(this._inv[PAPERDOLL_ID][1]);
          }

          this.writeD(this.pvp_flag);
          this.writeD(this.karma);
          this.writeD(this._mAtkSpd);
          this.writeD(this._pAtkSpd);
          this.writeD(this.pvp_flag);
          this.writeD(this.karma);
          this.writeD(this._runSpd);
          this.writeD(this._walkSpd);
          this.writeD(this._swimSpd);
          this.writeD(this._swimSpd);
          this.writeD(this._flRunSpd);
          this.writeD(this._flWalkSpd);
          this.writeD(this._flyRunSpd);
          this.writeD(this._flyWalkSpd);
          this.writeF(this.moveAnimMod);
          this.writeF(this.atkAnimMod);
          this.writeF(this.col_radius);
          this.writeF(this.col_height);
          this.writeD(this.hair_style);
          this.writeD(this.hair_color);
          this.writeD(this.face);
          this.writeS(this._title);
          this.writeD(this.clan_id);
          this.writeD(this.clan_crest_id);
          this.writeD(this.ally_id);
          this.writeD(this.ally_crest_id);
          this.writeD(0);
          this.writeC(this._sit);
          this.writeC(this._run);
          this.writeC(this._combat);
          this.writeC(this._dead);
          this.writeC(0);
          this.writeC(this.mount_type);
          this.writeC(this.private_store);
          this.writeH(this.cubics.length);
          EffectCubic[] var6 = this.cubics;
          var3 = var6.length;

          for (var4 = 0; var4 < var3; ++var4) {
            EffectCubic cubic = var6[var4];
            this.writeH(cubic == null ? 0 : cubic.getId());
          }

          this.writeC(this._isPartyRoomLeader ? 1 : 0);
          this.writeD(this._abnormalEffect);
          this.writeC(this._isFlying ? 2 : 0);
          this.writeH(this.rec_have);
          this.writeD(this.mount_id);
          this.writeD(this.class_id);
          this.writeD(0);
          this.writeC(this._enchant);
          this.writeC(this._team.ordinal());
          this.writeD(this.large_clan_crest_id);
          this.writeC(this._noble);
          this.writeC(this._hero);
          this.writeC(this._fishing);
          this.writeD(this._fishLoc.x);
          this.writeD(this._fishLoc.y);
          this.writeD(this._fishLoc.z);
          this.writeD(this._nameColor);
          this.writeD(this._loc.h);
          this.writeD(this.plg_class);
          this.writeD(this.pledge_type);
          this.writeD(this._title_color);
          this.writeD(this.cw_level);
          this.writeD(this.clan_rep_score);
          this.writeD(this._transform);
          this.writeD(this._agathion);
          this.writeD(1);
          this.writeD(this._abnormalEffect2);
        }
      }
    }
  }
}
