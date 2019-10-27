//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Manor;
import l2.gameserver.model.MinionList;
import l2.gameserver.model.Party;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.PlayerGroup;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.AggroList.HateInfo;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.Faction;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class MonsterInstance extends NpcInstance {
  private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;
  private ScheduledFuture<?> minionMaintainTask;
  private MinionList minionList = new MinionList(this);
  private boolean _isSeeded;
  private int _seederId;
  private boolean _altSeed;
  private RewardItem _harvestItem;
  private final Lock harvestLock = new ReentrantLock();
  private int overhitAttackerId;
  private double _overhitDamage;
  private TIntHashSet _absorbersIds;
  private final Lock absorbLock = new ReentrantLock();
  private boolean _isSpoiled;
  private int spoilerId;
  private List<RewardItem> _sweepItems;
  private final Lock sweepLock = new ReentrantLock();
  private int _isChampion;
  private final double MIN_DISTANCE_FOR_USE_UD = 200.0D;
  private final double MIN_DISTANCE_FOR_CANCEL_UD = 50.0D;
  private final double UD_USE_CHANCE = 30.0D;

  public MonsterInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public boolean isMovementDisabled() {
    return this.getNpcId() == 18344 || this.getNpcId() == 18345 || super.isMovementDisabled();
  }

  public boolean isLethalImmune() {
    return this.getMaxHp() >= 50000 || this._isChampion > 0 || this.getNpcId() == 22215 || this.getNpcId() == 22216 || this.getNpcId() == 22217 || super.isLethalImmune();
  }

  public boolean isFearImmune() {
    return this._isChampion > 0 || super.isFearImmune();
  }

  public boolean isParalyzeImmune() {
    return this._isChampion > 0 || super.isParalyzeImmune();
  }

  public boolean isAutoAttackable(Creature attacker) {
    return !attacker.isMonster();
  }

  public int getChampion() {
    return this._isChampion;
  }

  public void setChampion() {
    if (this.getReflection().canChampions() && this.canChampion()) {
      double random = Rnd.nextDouble();
      if (Config.ALT_CHAMPION_CHANCE2 / 100.0D >= random) {
        this.setChampion(2);
      } else if ((Config.ALT_CHAMPION_CHANCE1 + Config.ALT_CHAMPION_CHANCE2) / 100.0D >= random) {
        this.setChampion(1);
      } else {
        this.setChampion(0);
      }
    } else {
      this.setChampion(0);
    }

  }

  public void setChampion(int level) {
    if (level == 0) {
      this.removeSkillById(4407);
      this._isChampion = 0;
    } else {
      this.addSkill(SkillTable.getInstance().getInfo(4407, level));
      this._isChampion = level;
    }

  }

  public boolean canChampion() {
    return this.getTemplate().rewardExp > 0L && this.getTemplate().level >= Config.ALT_CHAMPION_MIN_LEVEL && this.getTemplate().level <= Config.ALT_CHAMPION_TOP_LEVEL;
  }

  public TeamType getTeam() {
    return this.getChampion() == 2 ? TeamType.RED : (this.getChampion() == 1 ? TeamType.BLUE : TeamType.NONE);
  }

  protected void onSpawn() {
    super.onSpawn();
    this.setCurrentHpMp((double)this.getMaxHp(), (double)this.getMaxMp(), true);
    if (this.getMinionList().hasMinions()) {
      if (this.minionMaintainTask != null) {
        this.minionMaintainTask.cancel(false);
        this.minionMaintainTask = null;
      }

      this.minionMaintainTask = ThreadPoolManager.getInstance().schedule(new MonsterInstance.MinionMaintainTask(), 1000L);
    }

  }

  protected void onDespawn() {
    this.setOverhitDamage(0.0D);
    this.setOverhitAttacker((Creature)null);
    this.clearSweep();
    this.clearHarvest();
    this.clearAbsorbers();
    super.onDespawn();
  }

  public MinionList getMinionList() {
    return this.minionList;
  }

  public Location getMinionPosition() {
    return Location.findPointToStay(this, 100, 150);
  }

  public void notifyMinionDied(MinionInstance minion) {
  }

  public void spawnMinion(MonsterInstance minion) {
    minion.setReflection(this.getReflection());
    if (this.getChampion() == 2) {
      minion.setChampion(1);
    } else {
      minion.setChampion(0);
    }

    minion.setHeading(this.getHeading());
    minion.setCurrentHpMp((double)minion.getMaxHp(), (double)minion.getMaxMp(), true);
    minion.spawnMe(this.getMinionPosition());
  }

  public boolean hasMinions() {
    return this.getMinionList().hasMinions();
  }

  public void setReflection(Reflection reflection) {
    super.setReflection(reflection);
    if (this.hasMinions()) {
      Iterator var2 = this.getMinionList().getAliveMinions().iterator();

      while(var2.hasNext()) {
        MinionInstance m = (MinionInstance)var2.next();
        m.setReflection(reflection);
      }
    }

  }

  protected void onDelete() {
    if (this.minionMaintainTask != null) {
      this.minionMaintainTask.cancel(false);
      this.minionMaintainTask = null;
    }

    this.getMinionList().deleteMinions();
    super.onDelete();
  }

  protected void onDeath(Creature killer) {
    if (this.minionMaintainTask != null) {
      this.minionMaintainTask.cancel(false);
      this.minionMaintainTask = null;
    }

    this.calculateRewards(killer);
    super.onDeath(killer);
  }

  protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
    if (skill != null && skill.isOverhit()) {
      double overhitDmg = (this.getCurrentHp() - damage) * -1.0D;
      if (overhitDmg <= 0.0D) {
        this.setOverhitDamage(0.0D);
        this.setOverhitAttacker((Creature)null);
      } else {
        this.setOverhitDamage(overhitDmg);
        this.setOverhitAttacker(attacker);
      }
    }

    super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
  }

  private int getPlayersDamage(Iterable<Player> players, Map<Playable, HateInfo> hateMap) {
    int damage = 0;
    Iterator var4 = players.iterator();

    while(var4.hasNext()) {
      Player player = (Player)var4.next();
      if (!player.isDead()) {
        HateInfo hateInfo = (HateInfo)hateMap.get(player);
        if (hateInfo != null && hateInfo.damage > 1) {
          damage += hateInfo.damage;
          Summon playerSummon;
          if ((playerSummon = player.getPet()) != null) {
            HateInfo summonHateInfo = (HateInfo)hateMap.get(playerSummon);
            if (summonHateInfo != null) {
              damage += summonHateInfo.damage;
            }
          }
        }
      }
    }

    return damage;
  }

  private Creature getTopDamager(Iterable<? extends PlayerGroup> playerGroups, Map<Playable, HateInfo> hateMap) {
    if (hateMap.isEmpty()) {
      return null;
    } else {
      int topDamage = -2147483648;
      PlayerGroup topPlayerGroup = null;
      Iterator var5 = playerGroups.iterator();

      while(var5.hasNext()) {
        PlayerGroup playerGroup = (PlayerGroup)var5.next();
        if (playerGroup != topPlayerGroup) {
          int playerGroupDamage = this.getPlayersDamage(playerGroup, hateMap);
          if (playerGroupDamage > topDamage) {
            topDamage = playerGroupDamage;
            topPlayerGroup = playerGroup;
          }
        }
      }

      if (topPlayerGroup == null) {
        return null;
      } else if (topPlayerGroup instanceof Player) {
        return (Player)topPlayerGroup;
      } else if (topPlayerGroup instanceof Party) {
        Party party = (Party)topPlayerGroup;
        return this.getTopDamager(party.getPartyMembers(), hateMap);
      } else if (topPlayerGroup instanceof CommandChannel) {
        CommandChannel commandChannel = (CommandChannel)topPlayerGroup;
        return this.getTopDamager(commandChannel.getParties(), hateMap);
      } else {
        return null;
      }
    }
  }

  private Creature getTopDamager(Map<Playable, HateInfo> hateMap) {
    Set<PlayerGroup> players = new HashSet<>();
    Iterator var3 = hateMap.keySet().iterator();

    while(var3.hasNext()) {
      Playable playable = (Playable)var3.next();
      if (playable instanceof Player) {
        players.add(((Player)playable).getPlayerGroup());
      }
    }

    return this.getTopDamager(players, hateMap);
  }

  protected Creature getTopDamager() {
    return this.getTopDamager(this.getAggroList().getPlayableMap());
  }

  public void calculateRewards(Creature lastAttacker) {
    Creature topDamager = this.getTopDamager();
    if (lastAttacker == null || !lastAttacker.isPlayable()) {
      lastAttacker = topDamager;
    }

    if (lastAttacker != null && lastAttacker.isPlayable()) {
      Player killer = lastAttacker.getPlayer();
      if (killer != null) {
        Map<Playable, HateInfo> aggroMap = this.getAggroList().getPlayableMap();
        Quest[] quests = this.getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
        Iterator var7;
        int itemIdx;
        if (quests != null && quests.length > 0) {
          List<Player> players = null;
          if (this.isRaid() && Config.ALT_NO_LASTHIT) {
            players = new ArrayList<>();
            var7 = aggroMap.keySet().iterator();

            label224:
            while(true) {
              Playable pl;
              do {
                do {
                  if (!var7.hasNext()) {
                    break label224;
                  }

                  pl = (Playable)var7.next();
                } while(pl.isDead());
              } while(!this.isInRangeZ(pl, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE) && !killer.isInRangeZ(pl, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE));

              if (!players.contains(pl.getPlayer())) {
                players.add(pl.getPlayer());
              }
            }
          } else if (killer.getParty() != null) {
            players = new ArrayList(killer.getParty().getMemberCount());
            var7 = killer.getParty().getPartyMembers().iterator();

            label240:
            while(true) {
              Player pl;
              do {
                do {
                  if (!var7.hasNext()) {
                    break label240;
                  }

                  pl = (Player)var7.next();
                } while(pl.isDead());
              } while(!this.isInRangeZ(pl, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE) && !killer.isInRangeZ(pl, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE));

              players.add(pl);
            }
          }

          Quest[] var23 = quests;
          int var26 = quests.length;

          for(itemIdx = 0; itemIdx < var26; ++itemIdx) {
            Quest quest = var23[itemIdx];
            Player toReward = killer;
            if (quest.getParty() != 0 && players != null) {
              if (!this.isRaid() && quest.getParty() != 2) {
                List<Player> interested = new ArrayList(players.size());
                Iterator var40 = players.iterator();

                while(var40.hasNext()) {
                  Player pl = (Player)var40.next();
                  QuestState qs = pl.getQuestState(quest.getName());
                  if (qs != null && !qs.isCompleted()) {
                    interested.add(pl);
                  }
                }

                if (interested.isEmpty()) {
                  continue;
                }

                toReward = (Player)interested.get(Rnd.get(interested.size()));
                if (toReward == null) {
                  toReward = killer;
                }
              } else {
                Iterator var12 = players.iterator();

                while(var12.hasNext()) {
                  Player pl = (Player)var12.next();
                  QuestState qs = pl.getQuestState(quest.getName());
                  if (qs != null && !qs.isCompleted()) {
                    quest.notifyKill(this, qs);
                  }
                }

                toReward = null;
              }
            }

            if (toReward != null) {
              QuestState qs = toReward.getQuestState(quest.getName());
              if (qs != null && !qs.isCompleted()) {
                quest.notifyKill(this, qs);
              }
            }
          }
        }

        Map<Player, MonsterInstance.RewardInfo> rewards = new HashMap<>();
        var7 = aggroMap.values().iterator();

        while(var7.hasNext()) {
          HateInfo info = (HateInfo)var7.next();
          if (info.damage > 1) {
            Playable attacker = (Playable)info.attacker;
            Player player = attacker.getPlayer();
            MonsterInstance.RewardInfo reward = (MonsterInstance.RewardInfo)rewards.get(player);
            if (reward == null) {
              rewards.put(player, new MonsterInstance.RewardInfo(player, info.damage));
            } else {
              reward.addDamage(info.damage);
            }
          }
        }

        Player[] attackers = (Player[])rewards.keySet().toArray(new Player[rewards.size()]);
        double[] xpsp = new double[2];
        Player[] var30 = attackers;
        int var33 = attackers.length;

        for(int var38 = 0; var38 < var33; ++var38) {
          Player attacker = var30[var38];
          if (!attacker.isDead()) {
            MonsterInstance.RewardInfo reward = (MonsterInstance.RewardInfo)rewards.get(attacker);
            if (reward != null) {
              Party party = attacker.getParty();
              int maxHp = this.getMaxHp();
              xpsp[0] = 0.0D;
              xpsp[1] = 0.0D;
              int partyDmg;
              if (party == null) {
                partyDmg = Math.min(reward._dmg, maxHp);
                if (partyDmg > 0) {
                  if (this.isInRangeZ(attacker, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    xpsp = this.calculateExpAndSp(attacker.getLevel(), (long)partyDmg);
                  }

                  xpsp[0] = this.applyOverhit(killer, xpsp[0]);
                  attacker.addExpAndCheckBonus(this, (double)((long)xpsp[0]), (double)((long)xpsp[1]));
                }

                rewards.remove(attacker);
              } else {
                partyDmg = 0;
                int partyMaxLevel = 1;
                List<Player> rewardedMembers = new ArrayList<>();
                Iterator var19 = party.getPartyMembers().iterator();

                while(var19.hasNext()) {
                  Player partyMember = (Player)var19.next();
                  MonsterInstance.RewardInfo ai = (MonsterInstance.RewardInfo)rewards.remove(partyMember);
                  if (!partyMember.isDead() && this.isInRangeZ(partyMember, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    if (ai != null) {
                      partyDmg += ai._dmg;
                    }

                    rewardedMembers.add(partyMember);
                    if (partyMember.getLevel() > partyMaxLevel) {
                      partyMaxLevel = partyMember.getLevel();
                    }
                  }
                }

                partyDmg = Math.min(partyDmg, maxHp);
                if (partyDmg > 0) {
                  xpsp = this.calculateExpAndSp(partyMaxLevel, (long)partyDmg);
                  double partyMul = (double)partyDmg / (double)maxHp;
                  xpsp[0] *= partyMul;
                  xpsp[1] *= partyMul;
                  xpsp[0] = this.applyOverhit(killer, xpsp[0]);
                  party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
                }
              }
            }
          }
        }

        CursedWeaponsManager.getInstance().dropAttackable(this, killer);
        if (topDamager != null && topDamager.isPlayable()) {
          Iterator var32 = this.getTemplate().getRewards().entrySet().iterator();

          while(var32.hasNext()) {
            Entry<RewardType, RewardList> entry = (Entry)var32.next();
            this.rollRewards(entry, lastAttacker, topDamager);
          }

          if (this.getChampion() > 0 && Config.ALT_CHAMPION_DROP_ITEM_IDS != null && Config.ALT_CHAMPION_DROP_ITEM_IDS.length > 0 && Math.abs(this.getLevel() - topDamager.getLevel()) < 9) {
            for(itemIdx = 0; itemIdx < Config.ALT_CHAMPION_DROP_ITEM_IDS.length; ++itemIdx) {
              if (Config.ALT_CHAMPION_DROP_ITEM_IDS[itemIdx] > 0 && Rnd.chance((double)Config.ALT_CHAMPION_DROP_CHANCES[itemIdx])) {
                this.dropItem(topDamager.getPlayer(), Config.ALT_CHAMPION_DROP_ITEM_IDS[itemIdx], Config.ALT_CHAMPION_DROP_COUNTS[itemIdx]);
              }
            }
          }

        }
      }
    }
  }

  public void onRandomAnimation() {
    if (System.currentTimeMillis() - this._lastSocialAction > 10000L) {
      this.broadcastPacket(new L2GameServerPacket[]{new SocialAction(this.getObjectId(), 1)});
      this._lastSocialAction = System.currentTimeMillis();
    }

  }

  public void startRandomAnimation() {
  }

  public int getKarma() {
    return 0;
  }

  public void addAbsorber(Player attacker) {
    if (attacker != null) {
      if (this.getCurrentHpPercents() <= 50.0D) {
        this.absorbLock.lock();

        try {
          if (this._absorbersIds == null) {
            this._absorbersIds = new TIntHashSet();
          }

          this._absorbersIds.add(attacker.getObjectId());
        } finally {
          this.absorbLock.unlock();
        }

      }
    }
  }

  public boolean isAbsorbed(Player player) {
    this.absorbLock.lock();

    boolean var2;
    try {
      if (this._absorbersIds == null) {
        var2 = false;
        return var2;
      }

      if (this._absorbersIds.contains(player.getObjectId())) {
        return true;
      }

      var2 = false;
    } finally {
      this.absorbLock.unlock();
    }

    return var2;
  }

  public void clearAbsorbers() {
    this.absorbLock.lock();

    try {
      if (this._absorbersIds != null) {
        this._absorbersIds.clear();
      }
    } finally {
      this.absorbLock.unlock();
    }

  }

  public RewardItem takeHarvest() {
    this.harvestLock.lock();

    RewardItem var2;
    try {
      RewardItem harvest = this._harvestItem;
      this.clearHarvest();
      var2 = harvest;
    } finally {
      this.harvestLock.unlock();
    }

    return var2;
  }

  public void clearHarvest() {
    this.harvestLock.lock();

    try {
      this._harvestItem = null;
      this._altSeed = false;
      this._seederId = 0;
      this._isSeeded = false;
    } finally {
      this.harvestLock.unlock();
    }

  }

  public boolean setSeeded(Player player, int seedId, boolean altSeed) {
    this.harvestLock.lock();

    boolean var4;
    try {
      if (!this.isSeeded()) {
        this._isSeeded = true;
        this._altSeed = altSeed;
        this._seederId = player.getObjectId();
        this._harvestItem = new RewardItem(Manor.getInstance().getCropType(seedId));
        if (this.getTemplate().rateHp > 1.0D) {
          this._harvestItem.count = Rnd.get(Math.round(this.getTemplate().rateHp), Math.round(1.5D * this.getTemplate().rateHp));
        }

        return true;
      }

      var4 = false;
    } finally {
      this.harvestLock.unlock();
    }

    return var4;
  }

  public boolean isSeeded(Player player) {
    return this.isSeeded() && this._seederId == player.getObjectId() && this.getDeadTime() < 20000L;
  }

  public boolean isSeeded() {
    return this._isSeeded;
  }

  public boolean isSpoiled() {
    return this._isSpoiled;
  }

  public boolean isSpoiled(Player player) {
    if (!this.isSpoiled()) {
      return false;
    } else if (player.getObjectId() == this.spoilerId && this.getDeadTime() < 20000L) {
      return true;
    } else {
      if (player.isInParty()) {
        Iterator var2 = player.getParty().getPartyMembers().iterator();

        while(var2.hasNext()) {
          Player pm = (Player)var2.next();
          if (pm.getObjectId() == this.spoilerId && this.getDistance(pm) < (double)Config.ALT_PARTY_DISTRIBUTION_RANGE) {
            return true;
          }
        }
      }

      return false;
    }
  }

  public boolean setSpoiled(Player player) {
    this.sweepLock.lock();

    try {
      if (this.isSpoiled()) {
        boolean var2 = false;
        return var2;
      }

      this._isSpoiled = true;
      this.spoilerId = player.getObjectId();
    } finally {
      this.sweepLock.unlock();
    }

    return true;
  }

  public boolean isSweepActive() {
    this.sweepLock.lock();

    boolean var1;
    try {
      var1 = this._sweepItems != null && this._sweepItems.size() > 0;
    } finally {
      this.sweepLock.unlock();
    }

    return var1;
  }

  public List<RewardItem> takeSweep() {
    this.sweepLock.lock();

    List var2;
    try {
      List<RewardItem> sweep = this._sweepItems;
      this.clearSweep();
      var2 = sweep;
    } finally {
      this.sweepLock.unlock();
    }

    return var2;
  }

  public void clearSweep() {
    this.sweepLock.lock();

    try {
      this._isSpoiled = false;
      this.spoilerId = 0;
      this._sweepItems = null;
    } finally {
      this.sweepLock.unlock();
    }

  }

  public void rollRewards(Entry<RewardType, RewardList> entry, Creature lastAttacker, Creature topDamager) {
    RewardType type = (RewardType)entry.getKey();
    RewardList list = (RewardList)entry.getValue();
    if (type != RewardType.SWEEP || this.isSpoiled()) {
      Creature activeChar = type == RewardType.SWEEP ? lastAttacker : topDamager;
      Player activePlayer = activeChar.getPlayer();
      if (activePlayer != null) {
        int diff = this.calculateLevelDiffForDrop(topDamager.getLevel());
        double mod = this.calcStat(Stats.ITEM_REWARD_MULTIPLIER, 1.0D, activeChar, (Skill)null);
        mod *= Experience.penaltyModifier((long)diff, 9.0D);
        List<RewardItem> rewardItems = list.roll(activePlayer, mod, this instanceof RaidBossInstance);
        switch(type) {
          case SWEEP:
            this._sweepItems = rewardItems;
            return;
          default:
            Iterator var12 = rewardItems.iterator();

            while(true) {
              RewardItem drop;
              do {
                if (!var12.hasNext()) {
                  return;
                }

                drop = (RewardItem)var12.next();
              } while(this.isSeeded() && !this._altSeed && !drop.isAdena && !drop.isSealStone);

              this.dropItem(activePlayer, drop.itemId, drop.count);
            }
        }
      }
    }
  }

  private double[] calculateExpAndSp(int level, long damage) {
    int diff = level - this.getLevel();
    double xp = (double)(this.getExpReward() * damage / (long)this.getMaxHp());
    double sp = (double)(this.getSpReward() * damage / (long)this.getMaxHp());
    if (Config.EXP_SP_DIFF_LIMIT != 0 && Math.abs(diff) > Config.EXP_SP_DIFF_LIMIT) {
      xp = 0.0D;
      sp = 0.0D;
    }

    if (diff > Config.THRESHOLD_LEVEL_DIFF) {
      double mod = Math.pow(0.83D, (double)(diff - 5));
      xp *= mod;
      sp *= mod;
    }

    xp = Math.max(0.0D, xp);
    sp = Math.max(0.0D, sp);
    return new double[]{xp, sp};
  }

  private double applyOverhit(Player killer, double xp) {
    if (xp > 0.0D && killer.getObjectId() == this.overhitAttackerId) {
      int overHitExp = this.calculateOverhitExp(xp * Config.RATE_OVERHIT);
      killer.sendPacket(new IStaticPacket[]{Msg.OVER_HIT, (new SystemMessage(362)).addNumber(overHitExp)});
      xp += (double)overHitExp;
    }

    return xp;
  }

  public void setOverhitAttacker(Creature attacker) {
    this.overhitAttackerId = attacker == null ? 0 : attacker.getObjectId();
  }

  public double getOverhitDamage() {
    return this._overhitDamage;
  }

  public void setOverhitDamage(double damage) {
    this._overhitDamage = damage;
  }

  public int calculateOverhitExp(double normalExp) {
    double overhitPercentage = this.getOverhitDamage() * 100.0D / (double)this.getMaxHp();
    if (overhitPercentage > 25.0D) {
      overhitPercentage = 25.0D;
    }

    double overhitExp = overhitPercentage / 100.0D * normalExp;
    this.setOverhitAttacker((Creature)null);
    this.setOverhitDamage(0.0D);
    return (int)Math.round(overhitExp);
  }

  public boolean isAggressive() {
    return (Config.ALT_CHAMPION_CAN_BE_AGGRO || this.getChampion() == 0) && super.isAggressive();
  }

  public Faction getFaction() {
    return !Config.ALT_CHAMPION_CAN_BE_SOCIAL && this.getChampion() != 0 ? Faction.NONE : super.getFaction();
  }

  public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
    this.checkUD(attacker, i);
    super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
  }

  private void checkUD(Creature attacker, double damage) {
    if ((double)this.getTemplate().baseAtkRange <= 200.0D && this.getLevel() >= 20 && this.getLevel() <= 78 && attacker.getLevel() - this.getLevel() <= 9 && this.getLevel() - attacker.getLevel() <= 9) {
      if (!this.isMinion() && this.getMinionList() == null && !this.isRaid() && !(this instanceof ReflectionBossInstance) && !(this instanceof ChestInstance) && this.getChampion() <= 0) {
        int skillId = 5044;
        int skillLvl = 1;
        if (this.getLevel() < 41 && this.getLevel() > 60) {
          if (this.getLevel() > 60) {
            skillLvl = 3;
          }
        } else {
          skillLvl = 2;
        }

        double distance = this.getDistance(attacker);
        if (distance <= 50.0D) {
          if (this.getEffectList() != null && this.getEffectList().getEffectsBySkillId(skillId) != null) {
            Iterator var8 = this.getEffectList().getEffectsBySkillId(skillId).iterator();

            while(var8.hasNext()) {
              Effect e = (Effect)var8.next();
              e.exit();
            }
          }
        } else if (distance >= 200.0D) {
          double chance = 30.0D / ((double)this.getMaxHp() / damage);
          if (Rnd.chance(chance)) {
            Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
            if (skill != null) {
              skill.getEffects(this, this, false, false);
            }
          }
        }

      }
    }
  }

  public boolean isMonster() {
    return true;
  }

  public Clan getClan() {
    return null;
  }

  public boolean isInvul() {
    return this._isInvul;
  }

  public class MinionMaintainTask extends RunnableImpl {
    public MinionMaintainTask() {
    }

    public void runImpl() throws Exception {
      if (!MonsterInstance.this.isDead()) {
        MonsterInstance.this.getMinionList().spawnMinions();
      }
    }
  }

  protected static final class RewardInfo {
    protected Creature _attacker;
    protected int _dmg = 0;

    public RewardInfo(Creature attacker, int dmg) {
      this._attacker = attacker;
      this._dmg = dmg;
    }

    public void addDamage(int dmg) {
      if (dmg < 0) {
        dmg = 0;
      }

      this._dmg += dmg;
    }

    public int hashCode() {
      return this._attacker.getObjectId();
    }
  }
}
