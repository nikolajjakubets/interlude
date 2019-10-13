//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.entity.oly;

import gnu.trove.TIntIntHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.oly.NoblesController.NobleRecord;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.ExOlympiadMode;
import l2.gameserver.network.l2.s2c.ExOlympiadSpelledInfo;
import l2.gameserver.network.l2.s2c.ExOlympiadUserInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class Competition {
  private final Stadium _stadium;
  public Participant[] _participants;
  private final CompetitionType _type;
  private long _start_time;
  private static final double RESTORE_MOD = 0.8D;
  private CompetitionState _state;
  private ScheduledFuture<?> _currentTask;

  public Competition(CompetitionType type, Stadium stadium) {
    this._type = type;
    this._stadium = stadium;
    this._state = null;
    this._start_time = 0L;
  }

  public CompetitionState getState() {
    return this._state;
  }

  public void setState(CompetitionState state) {
    if (this._state == CompetitionState.STAND_BY && state == CompetitionState.PLAYING) {
      this._start_time = System.currentTimeMillis();
    }

    this._state = state;
  }

  public CompetitionType getType() {
    return this._type;
  }

  public Stadium getStadium() {
    return this._stadium;
  }

  public void setPlayers(Participant[] participants) {
    this._participants = participants;
    Participant[] var2 = this._participants;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Participant participant = var2[var4];
      Player[] var6 = participant.getPlayers();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        Player player = var6[var8];
        player.setOlyParticipant(participant);
      }
    }

  }

  public void start() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant participant = var1[var3];
      participant.OnStart();
    }

  }

  private void prepareParticipantsForReturn() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant part = var1[var3];
      Player[] var5 = part.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];

        try {
          if (player.getClan() != null) {
            player.getClan().enableSkills(player);
          }

          for(int restrictedSkillIdx = 0; restrictedSkillIdx < Config.OLY_RESTRICTED_SKILL_IDS.length; ++restrictedSkillIdx) {
            int restrictedSkillId = Config.OLY_RESTRICTED_SKILL_IDS[restrictedSkillIdx];
            if (player.isUnActiveSkill(restrictedSkillId)) {
              Skill skill = player.getKnownSkill(restrictedSkillId);
              if (skill != null) {
                player.removeUnActiveSkill(skill);
              }
            }
          }

          if (player.isDead()) {
            player.broadcastPacket(new L2GameServerPacket[]{new Revive(player)});
            player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp(), true);
            player.setCurrentCp((double)player.getMaxCp());
          } else {
            player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp(), false);
            player.setCurrentCp((double)player.getMaxCp());
          }

          Collection<TimeStamp> reuse = player.getSkillReuses();
          Iterator var15 = reuse.iterator();

          while(var15.hasNext()) {
            TimeStamp ts = (TimeStamp)var15.next();
            Skill skill = SkillTable.getInstance().getInfo(ts.getId(), ts.getLevel());
            player.enableSkill(skill);
          }

          if (player.isHero()) {
            HeroController.addSkills(player);
          }

          player.sendPacket(new IStaticPacket[]{new ExOlympiadMode(0), new SkillList(player), new SkillCoolTime(player)});
          player.updateStats();
          player.updateEffectIcons();
        } catch (Exception var13) {
          var13.printStackTrace();
        }
      }
    }

  }

  private void prepareParticipantsForCompetition() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant part = var1[var3];
      Player[] var5 = part.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];

        try {
          boolean update = false;
          Effect[] var10 = player.getEffectList().getAllFirstEffects();
          int restrictedSkillIdx = var10.length;

          int restrictedSkillId;
          for(restrictedSkillId = 0; restrictedSkillId < restrictedSkillIdx; ++restrictedSkillId) {
            Effect e = var10[restrictedSkillId];
            if (!(e instanceof EffectCubic) || e.getSkill().getTargetType() != SkillTargetType.TARGET_SELF) {
              e.exit();
              update = true;
            }
          }

          Collection<TimeStamp> reuse = player.getSkillReuses();

          Skill skill;
          for(Iterator var24 = reuse.iterator(); var24.hasNext(); update = true) {
            TimeStamp ts = (TimeStamp)var24.next();
            skill = SkillTable.getInstance().getInfo(ts.getId(), ts.getLevel());
            player.enableSkill(skill);
          }

          if (update) {
            player.sendPacket(new SkillCoolTime(player));
            player.updateStats();
            player.updateEffectIcons();
          }

          if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR >= 0) {
            player.sendMessage(new CustomMessage("l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantArmorLevelLimited", player, new Object[]{Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR}));
          }

          if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS >= 0) {
            player.sendMessage(new CustomMessage("l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantWeaponPhysLevelLimited", player, new Object[]{Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS}));
          }

          if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE >= 0) {
            player.sendMessage(new CustomMessage("l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantWeaponMageLevelLimited", player, new Object[]{Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE}));
          }

          if (Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY >= 0) {
            player.sendMessage(new CustomMessage("l2p.gameserver.model.entity.OlympiadGame.Competition.EnchantAccessoryLevelLimited", player, new Object[]{Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY}));
          }

          if (player.getClan() != null) {
            player.getClan().disableSkills(player);
          }

          for(restrictedSkillIdx = 0; restrictedSkillIdx < Config.OLY_RESTRICTED_SKILL_IDS.length; ++restrictedSkillIdx) {
            restrictedSkillId = Config.OLY_RESTRICTED_SKILL_IDS[restrictedSkillIdx];
            skill = player.getKnownSkill(restrictedSkillId);
            if (skill != null) {
              player.addUnActiveSkill(skill);
            }
          }

          if (player.isHero()) {
            HeroController.removeSkills(player);
          }

          if (player.isCastingNow()) {
            player.abortCast(true, false);
          }

          if (player.isMounted()) {
            player.setMount(0, 0, 0);
          }

          if (player.getPet() != null) {
            Summon summon = player.getPet();
            if (summon.isPet()) {
              summon.unSummon();
            } else {
              summon.getEffectList().stopAllEffects();
            }
          }

          if (player.getAgathionId() > 0) {
            player.setAgathion(0);
          }

          player.sendPacket(new SkillList(player));
          ItemInstance wpn = player.getInventory().getPaperdollItem(7);
          if (wpn != null && wpn.isHeroWeapon()) {
            player.getInventory().unEquipItem(wpn);
            player.abortAttack(true, true);
            player.refreshExpertisePenalty();
          }

          Set<Integer> activeSoulShots = player.getAutoSoulShot();
          Iterator var30 = activeSoulShots.iterator();

          while(var30.hasNext()) {
            int itemId = (Integer)var30.next();
            player.removeAutoSoulShot(itemId);
            player.sendPacket(new ExAutoSoulShot(itemId, false));
          }

          ItemInstance weapon = player.getActiveWeaponInstance();
          if (weapon != null) {
            weapon.setChargedSpiritshot(0);
            weapon.setChargedSoulshot(0);
          }

          this.restoreHPCPMP();
          player.broadcastUserInfo(true);
          if (this.getType() != CompetitionType.TEAM_CLASS_FREE) {
            if (player.getParty() != null) {
              player.getParty().removePartyMember(player, false);
            }
          } else {
            boolean upp = false;
            if (player.getParty() != null) {
              if (player.getParty().getPartyMembers().size() != part.getPlayers().length) {
                upp = true;
              } else {
                Iterator var15 = player.getParty().getPartyMembers().iterator();

                while(var15.hasNext()) {
                  Player pm0 = (Player)var15.next();
                  boolean contains = false;
                  Player[] var18 = part.getPlayers();
                  int var19 = var18.length;

                  for(int var20 = 0; var20 < var19; ++var20) {
                    Player pm1 = var18[var20];
                    if (pm0 == pm1) {
                      contains = true;
                    }
                  }

                  if (!contains) {
                    upp = true;
                  }
                }
              }
            } else {
              upp = true;
            }

            if (upp) {
              Player[] party_r = part.getPlayers();
              if (party_r[0].getParty() != null) {
                party_r[0].getParty().removePartyMember(party_r[0], false);
              }

              Party party = new Party(party_r[0], 0);
              party_r[0].setParty(party);
              if (party_r[1].isInParty()) {
                party_r[1].getParty().removePartyMember(party_r[1], false);
              }

              party.addPartyMember(party_r[1]);
              if (party_r[2].isInParty()) {
                party_r[2].getParty().removePartyMember(party_r[2], false);
              }

              party.addPartyMember(party_r[2]);
            }
          }
        } catch (Exception var22) {
          var22.printStackTrace();
        }
      }
    }

  }

  public void applyBuffs() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant part = var1[var3];
      Player[] var5 = part.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];

        try {
          TIntIntHashMap buffs = (TIntIntHashMap)Config.OLY_BUFFS.get(player.getActiveClassId());
          int[] var10 = buffs.keys();
          int var11 = var10.length;

          for(int var12 = 0; var12 < var11; ++var12) {
            int skillId = var10[var12];
            Skill buff = SkillTable.getInstance().getInfo(skillId, buffs.get(skillId));
            buff.getEffects(player, player, false, false);
          }
        } catch (Exception var15) {
          var15.printStackTrace();
        }
      }
    }

  }

  public void restoreHPCPMP() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant part = var1[var3];
      Player[] var5 = part.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];

        try {
          player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
          player.setCurrentCp((double)player.getMaxCp());
        } catch (Exception var10) {
          var10.printStackTrace();
        }
      }
    }

  }

  public void finish() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant participant = var1[var3];
      Player[] var5 = participant.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];
        if ((double)player.getMaxCp() * 0.8D > player.getCurrentCp()) {
          player.setCurrentCp((double)player.getMaxCp() * 0.8D);
        }

        if ((double)player.getMaxHp() * 0.8D > player.getCurrentHp()) {
          player.setCurrentHp((double)player.getMaxHp() * 0.8D, false);
        }

        if ((double)player.getMaxMp() * 0.8D > player.getCurrentMp()) {
          player.setCurrentMp((double)player.getMaxMp() * 0.8D);
        }
      }

      participant.OnFinish();
    }

  }

  private static int CalcPoints(int points) {
    return Math.max(1, (Math.min(50, points) - 1) / 5 + 1);
  }

  private int getParticipantsMinPoint() {
    int pmin = 2147483647;
    Participant[] var2 = this._participants;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Participant participant = var2[var4];
      Player[] var6 = participant.getPlayers();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        Player player = var6[var8];
        if (player != null) {
          int ppoint = NoblesController.getInstance().getPointsOf(player.getObjectId());
          if (ppoint < pmin) {
            pmin = ppoint;
          }
        }
      }
    }

    return pmin;
  }

  private void processPoints(Participant winn, Participant loose, boolean tie) {
    this.processPoints(winn, loose, tie, false);
  }

  private void processPoints(Participant winn, Participant loose, boolean tie, boolean looserDisconnected) {
    if (!looserDisconnected) {
      this.broadcastPacket(tie ? Msg.THE_GAME_ENDED_IN_A_TIE : (new SystemMessage(1497)).addString(winn.getName()), true, true);
    }

    long comp_spend_time = 0L;
    if (this._start_time > 0L) {
      comp_spend_time = Math.min(Config.OLYMPIAD_COMPETITION_TIME, System.currentTimeMillis() - this._start_time) / 1000L;
    }

    Player[] loose_arr = loose.getPlayers();
    Player[] winn_arr = winn.getPlayers();
    int loosed_points_sum = 0;
    int looser_sum = 0;
    int winner_sum = 0;

    int min_points;
    Player wp;
    for(min_points = 0; min_points < loose_arr.length; ++min_points) {
      try {
        wp = loose_arr[min_points];
        if (wp != null) {
          looser_sum += NoblesController.getInstance().getPointsOf(wp.getObjectId());
        }
      } catch (Exception var24) {
        var24.printStackTrace();
      }
    }

    for(min_points = 0; min_points < winn_arr.length; ++min_points) {
      try {
        wp = winn_arr[min_points];
        if (wp != null) {
          winner_sum += NoblesController.getInstance().getPointsOf(wp.getObjectId());
        }
      } catch (Exception var23) {
        var23.printStackTrace();
      }
    }

    min_points = Math.max(0, Math.min(winner_sum, looser_sum));

    int loose_points;
    int var20;
    int points;
    for(points = 0; points < loose_arr.length; ++points) {
      try {
        Player lp = loose_arr[points];
        if (lp != null && NoblesController.getInstance() != null) {
          int curr_points = NoblesController.getInstance().getPointsOf(lp.getObjectId());
          loose_points = Math.max(1, (int)((double)min_points * Config.OLY_LOOSE_POINTS_MUL));
          int looser_points = Math.max(0, curr_points - loose_points);
          loosed_points_sum += loose_points;
          NobleRecord lnr = NoblesController.getInstance().getNobleRecord(lp.getObjectId());
          lnr.points_current = looser_points;
          ++lnr.comp_loose;
          ++lnr.comp_done;
          switch(this.getType()) {
            case CLASS_FREE:
              ++lnr.class_free_cnt;
              break;
            case CLASS_INDIVIDUAL:
              ++lnr.class_based_cnt;
              break;
            case TEAM_CLASS_FREE:
              ++lnr.team_cnt;
          }

          NoblesController.getInstance().SaveNobleRecord(lnr);
          lp.sendPacket((new SystemMessage(1658)).addName(lp).addNumber(loose_points));
          QuestState[] var19 = lp.getAllQuestsStates();
          var20 = var19.length;

          for(int var21 = 0; var21 < var20; ++var21) {
            QuestState qs = var19[var21];
            if (qs.isStarted()) {
              qs.getQuest().notifyOlympiadResult(qs, this.getType(), false);
            }
          }

          Iterator var34 = lp.getEffectList().getAllEffects().iterator();

          while(var34.hasNext()) {
            Effect e = (Effect)var34.next();
            if (e != null && e.isCancelable()) {
              e.exit();
            }
          }

          lp.sendChanges();
          lp.updateEffectIcons();
          CompetitionController.getInstance().addCompetitionResult(OlyController.getInstance().getCurrentSeason(), NoblesController.getInstance().getNobleRecord(winn_arr[points].getObjectId()), loose_points, NoblesController.getInstance().getNobleRecord(lp.getObjectId()), loose_points, this.getType(), tie, looserDisconnected, comp_spend_time);
          lp.getListeners().onOlyCompetitionCompleted(this, false);
        }
      } catch (Exception var26) {
        var26.printStackTrace();
      }
    }

    if (!looserDisconnected) {
      points = loosed_points_sum / loose_arr.length;

      for(int i = 0; i < winn_arr.length; ++i) {
        try {
          Player wp = winn_arr[i];
          loose_points = Math.max(0, NoblesController.getInstance().getPointsOf(wp.getObjectId()) + points);
          NoblesController.getInstance().setPointsOf(wp.getObjectId(), loose_points);
          NobleRecord wnr = NoblesController.getInstance().getNobleRecord(wp.getObjectId());
          wnr.points_current = loose_points;
          ++wnr.comp_win;
          ++wnr.comp_done;
          switch(this.getType()) {
            case CLASS_FREE:
              ++wnr.class_free_cnt;
              break;
            case CLASS_INDIVIDUAL:
              ++wnr.class_based_cnt;
              break;
            case TEAM_CLASS_FREE:
              ++wnr.team_cnt;
          }

          NoblesController.getInstance().SaveNobleRecord(wnr);
          wp.sendPacket((new SystemMessage(1657)).addName(wp).addNumber(points));
          QuestState[] var31 = wp.getAllQuestsStates();
          int var35 = var31.length;

          for(var20 = 0; var20 < var35; ++var20) {
            QuestState qs = var31[var20];
            if (qs.isStarted()) {
              qs.getQuest().notifyOlympiadResult(qs, this.getType(), !tie);
            }
          }

          Iterator var32 = wp.getEffectList().getAllEffects().iterator();

          while(var32.hasNext()) {
            Effect e = (Effect)var32.next();
            if (e != null && e.isCancelable()) {
              e.exit();
            }
          }

          wp.sendChanges();
          wp.updateEffectIcons();
          int rvicnt = 0;
          switch(this.getType()) {
            case CLASS_FREE:
              rvicnt = Config.OLY_VICTORY_CFREE_RITEMCNT;
              break;
            case CLASS_INDIVIDUAL:
              rvicnt = Config.OLY_VICTORY_CBASE_RITEMCNT;
              break;
            case TEAM_CLASS_FREE:
              rvicnt = Config.OLY_VICTORY_3TEAM_RITEMCNT;
          }

          if (rvicnt > 0) {
            wp.getInventory().addItem(Config.OLY_VICTORY_RITEMID, (long)rvicnt);
            wp.sendPacket(SystemMessage2.obtainItems(Config.OLY_VICTORY_RITEMID, (long)rvicnt, 0));
          }

          CompetitionController.getInstance().addCompetitionResult(OlyController.getInstance().getCurrentSeason(), NoblesController.getInstance().getNobleRecord(wp.getObjectId()), points, NoblesController.getInstance().getNobleRecord(loose_arr[i].getObjectId()), points, this.getType(), tie, looserDisconnected, comp_spend_time);
          wp.getListeners().onOlyCompetitionCompleted(this, !tie);
        } catch (Exception var25) {
          var25.printStackTrace();
        }
      }
    }

  }

  public boolean ValidateParticipants() {
    boolean cancel = false;
    Participant[] var2 = this._participants;
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      Participant participant = var2[var4];
      if (!participant.validateThis()) {
        cancel = true;
        break;
      }
    }

    if (cancel) {
      this.cancelTask();
      CompetitionController.getInstance().FinishCompetition(this);
      return true;
    } else {
      return false;
    }
  }

  public synchronized void ValidateWinner() {
    if (this.getState() == CompetitionState.INIT) {
      this.broadcastPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME, true, false);
      if (!this._participants[0].isAlive()) {
        this.processPoints(this._participants[1], this._participants[0], false, true);
      } else if (!this._participants[1].isAlive()) {
        this.processPoints(this._participants[0], this._participants[1], false, true);
      }

      this.cancelTask();
      CompetitionController.getInstance().FinishCompetition(this);
    } else {
      if (this.getState() != CompetitionState.FINISH && (!this._participants[0].isAlive() || !this._participants[1].isAlive())) {
        this.cancelTask();
        this.setState(CompetitionState.FINISH);
        CompetitionController.getInstance().scheduleFinishCompetition(this, 20, 100L);
      }

      if (this.getState() == CompetitionState.FINISH) {
        if (!this._participants[0].isAlive()) {
          this.processPoints(this._participants[1], this._participants[0], false);
        } else if (!this._participants[1].isAlive()) {
          this.processPoints(this._participants[0], this._participants[1], false);
        } else {
          double dmg0 = this._participants[0].getTotalDamage();
          double dmg1 = this._participants[1].getTotalDamage();
          if (dmg0 < dmg1) {
            this.processPoints(this._participants[0], this._participants[1], false);
          } else if (dmg0 > dmg1) {
            this.processPoints(this._participants[1], this._participants[0], false);
          } else {
            this.processPoints(this._participants[0], this._participants[1], true);
          }
        }

        Participant[] var9 = this._participants;
        int var2 = var9.length;

        for(int var10 = 0; var10 < var2; ++var10) {
          Participant participant = var9[var10];
          Player[] var5 = participant.getPlayers();
          int var6 = var5.length;

          for(int var7 = 0; var7 < var6; ++var7) {
            Player player = var5[var7];
            if (player.isDead()) {
              player.doRevive(100.0D);
            }

            player.block();
            player.sendPacket(new ExOlympiadMode(0));
          }
        }

        this.broadcastPacket(new ExOlympiadMode(3), false, true);
      }

    }
  }

  public void teleportParticipantsOnStadium() {
    this.prepareParticipantsForCompetition();
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant participant = var1[var3];
      Player[] var5 = participant.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];

        try {
          if (player != null) {
            Location loc = Location.findAroundPosition(this.getStadium().getLocForParticipant(participant), 0, 32);
            player.setVar("backCoords", player.getLoc().toXYZString(), -1L);
            player.teleToLocation(loc, this._stadium);
            player.sendPacket(new ExOlympiadMode(participant.getSide()));
            if (this.getType() == CompetitionType.TEAM_CLASS_FREE) {
              player.setTeam(participant.getSide() == 1 ? TeamType.BLUE : TeamType.RED);
            }

            Summon summon = player.getPet();
            if (summon != null) {
              if (summon.isPet()) {
                summon.unSummon();
              } else {
                summon.teleToLocation(loc, this._stadium);
              }
            }
          }
        } catch (Exception var11) {
          var11.printStackTrace();
        }
      }
    }

  }

  public void teleportParticipantsBack() {
    this.prepareParticipantsForReturn();
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant participant = var1[var3];
      Player[] var5 = participant.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];

        try {
          if (player != null && player.getVar("backCoords") != null) {
            Location loc = Location.parseLoc(player.getVar("backCoords"));
            player.unsetVar("backCoords");
            player.sendPacket(new ExOlympiadMode(0));
            if (player.isBlocked()) {
              player.unblock();
            }

            if (this.getType() == CompetitionType.TEAM_CLASS_FREE) {
              player.setTeam(TeamType.NONE);
            }

            player.setReflection(0);
            player.teleToLocation(loc);
            Summon summon = player.getPet();
            if (summon != null) {
              if (summon.isPet()) {
                summon.unSummon();
              } else {
                summon.setReflection(0);
                summon.teleToLocation(loc);
              }
            }
          }
        } catch (Exception var11) {
          var11.printStackTrace();
        }
      }
    }

  }

  public void broadcastEverybodyOlympiadUserInfo() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant participant = var1[var3];
      Player[] var5 = participant.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];
        if (player != null) {
          ExOlympiadUserInfo oui = new ExOlympiadUserInfo(player);
          this.broadcastPacket(oui, true, true);
          player.broadcastRelationChanged();
        }
      }
    }

  }

  public void broadcastEverybodyEffectIcons() {
    Participant[] var1 = this._participants;
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      Participant participant = var1[var3];
      Player[] var5 = participant.getPlayers();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
        Player player = var5[var7];
        this.broadcastEffectIcons(player, player.getEffectList().getAllFirstEffects());
      }
    }

  }

  public void broadcastEffectIcons(Player player, Effect[] effects) {
    ExOlympiadSpelledInfo osi = new ExOlympiadSpelledInfo();
    Effect[] var4 = effects;
    int var5 = effects.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      Effect effect = var4[var6];
      if (effect != null && effect.isInUse()) {
        effect.addOlympiadSpelledIcon(player, osi);
      }
    }

    if (this.getState() == CompetitionState.PLAYING) {
      this.broadcastPacket(osi, true, true);
    } else {
      player.getOlyParticipant().sendPacket(osi);
    }

  }

  public void broadcastPacket(L2GameServerPacket gsp, boolean toParticipants, boolean toObservers) {
    if (this.getState() != null) {
      if (this.getState() == CompetitionState.INIT && toParticipants) {
        Participant[] var12 = this._participants;
        int var13 = var12.length;

        for(int var6 = 0; var6 < var13; ++var6) {
          Participant participant = var12[var6];
          Player[] var8 = participant.getPlayers();
          int var9 = var8.length;

          for(int var10 = 0; var10 < var9; ++var10) {
            Player player = var8[var10];
            player.sendPacket(gsp);
          }
        }
      } else {
        Iterator var4 = this.getStadium().getPlayers().iterator();

        while(true) {
          Player player;
          do {
            if (!var4.hasNext()) {
              return;
            }

            player = (Player)var4.next();
          } while((!toParticipants || !player.isOlyParticipant()) && (!toObservers || !player.isOlyObserver()));

          player.sendPacket(gsp);
        }
      }
    }

  }

  public synchronized void scheduleTask(Runnable task, long delay) {
    this._currentTask = ThreadPoolManager.getInstance().schedule(task, delay);
  }

  public synchronized void cancelTask() {
    if (this._currentTask != null) {
      this._currentTask.cancel(false);
      this._currentTask = null;
    }

  }

  public Participant[] getParticipants() {
    return this._participants;
  }

  public static SystemMessage checkPlayer(Player player) {
    if (!player.isNoble()) {
      return (new SystemMessage(1501)).addName(player);
    } else if (player.isInDuel()) {
      return new SystemMessage(1599);
    } else if (player.getBaseClassId() == player.getClassId().getId() && player.getClassId().getLevel() >= 4) {
      if ((double)player.getInventoryLimit() * 0.8D <= (double)player.getInventory().getSize()) {
        return (new SystemMessage(1691)).addName(player);
      } else if (player.isCursedWeaponEquipped()) {
        return (new SystemMessage(1857)).addName(player).addItemName(player.getCursedWeaponEquippedId());
      } else {
        return NoblesController.getInstance().getPointsOf(player.getObjectId()) < 1 ? (new SystemMessage(1983)).addString((new CustomMessage("THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_REQUIREMENTS_ARE_NOT_MET_IN_ORDER_TO_PARTICIPATE_IN", player, new Object[0])).toString()) : null;
      }
    } else {
      return (new SystemMessage(1500)).addName(player);
    }
  }
}
