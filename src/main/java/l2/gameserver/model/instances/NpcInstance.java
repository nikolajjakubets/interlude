//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import gnu.trove.TIntObjectIterator;
import l2.commons.collections.MultiValueSet;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.handler.items.RefineryHandler;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.NpcListener;
import l2.gameserver.model.*;
import l2.gameserver.model.GameObjectTasks.NotifyAITask;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.actor.listener.NpcListenerList;
import l2.gameserver.model.actor.recorder.NpcStatsChangeRecorder;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.scripts.Events;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.taskmanager.DecayTaskManager;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.npc.Faction;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class NpcInstance extends Creature {
  public static final String NO_CHAT_WINDOW = "noChatWindow";
  public static final String NO_RANDOM_WALK = "noRandomWalk";
  public static final String IGNORE_DROP_DIFF = "ignoreDropLevelDiff";
  public static final String NO_RANDOM_ANIMATION = "noRandomAnimation";
  public static final String TARGETABLE = "TargetEnabled";
  public static final String SHOW_NAME = "showName";
  private int _personalAggroRange = -1;
  private int _level = 0;
  private long _dieTime = 0L;
  protected int _spawnAnimation = 2;
  private int _currentLHandId;
  private int _currentRHandId;
  private double _currentCollisionRadius;
  private double _currentCollisionHeight;
  private int npcState = 0;
  protected boolean _hasRandomAnimation;
  protected boolean _hasRandomWalk;
  protected boolean _hasChatWindow;
  protected boolean _ignoreDropDiffPenalty;
  private Future<?> _decayTask;
  private Future<?> _animationTask;
  private AggroList _aggroList;
  private boolean _isTargetable;
  private boolean _showName;
  private Castle _nearestCastle;
  private ClanHall _nearestClanHall;
  private Spawner _spawn;
  private Location _spawnedLoc = new Location();
  private SpawnRange _spawnRange;
  private MultiValueSet<String> _parameters;
  protected boolean _unAggred;
  private int _displayId;
  private ScheduledFuture<?> _broadcastCharInfoTask;
  protected long _lastSocialAction;
  private boolean _isBusy;
  private String _busyMessage;
  private boolean _isUnderground;

  public NpcInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this._parameters = StatsSet.EMPTY;
    this._unAggred = false;
    this._displayId = 0;
    this._busyMessage = "";
    this._isUnderground = false;
    if (template == null) {
      throw new NullPointerException("No template for Npc. Please check your datapack is setup correctly.");
    } else {
      this.setParameters(template.getAIParams());
      this._hasRandomAnimation = !this.getParameter("noRandomAnimation", false) && Config.MAX_NPC_ANIMATION > 0;
      this._hasRandomWalk = !this.getParameter("noRandomWalk", false);
      this._ignoreDropDiffPenalty = this.getParameter("ignoreDropLevelDiff", false);
      this.setHasChatWindow(!this.getParameter("noChatWindow", false));
      this.setTargetable(this.getParameter("TargetEnabled", true));
      this.setShowName(this.getParameter("showName", true));
      if (template.getSkills().size() > 0) {
        TIntObjectIterator iterator = template.getSkills().iterator();

        while (iterator.hasNext()) {
          iterator.advance();
          this.addSkill((Skill) iterator.value());
        }
      }

      this.setName(template.name);
      this.setTitle(template.title);
      this.setLHandId(this.getTemplate().lhand);
      this.setRHandId(this.getTemplate().rhand);
      this.setCollisionHeight(this.getTemplate().collisionHeight);
      this.setCollisionRadius(this.getTemplate().collisionRadius);
      this._aggroList = new AggroList(this);
      this.setFlying(this.getParameter("isFlying", false));
    }
  }

//  public HardReference<NpcInstance> getRef() {
//    return super.getRef();
//  }

  public CharacterAI getAI() {
    if (this._ai == null) {
      synchronized (this) {
        if (this._ai == null) {
          this._ai = this.getTemplate().getNewAI(this);
        }
      }
    }

    return this._ai;
  }

  public Location getSpawnedLoc() {
    return this._spawnedLoc;
  }

  public void setSpawnedLoc(Location loc) {
    this._spawnedLoc = loc;
  }

  public int getRightHandItem() {
    return this._currentRHandId;
  }

  public int getLeftHandItem() {
    return this._currentLHandId;
  }

  public void setLHandId(int newWeaponId) {
    this._currentLHandId = newWeaponId;
  }

  public void setRHandId(int newWeaponId) {
    this._currentRHandId = newWeaponId;
  }

  public double getCollisionHeight() {
    return this._currentCollisionHeight;
  }

  public void setCollisionHeight(double offset) {
    this._currentCollisionHeight = offset;
  }

  public double getCollisionRadius() {
    return this._currentCollisionRadius;
  }

  public void setCollisionRadius(double collisionRadius) {
    this._currentCollisionRadius = collisionRadius;
  }

  protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
    if (attacker.isPlayable()) {
      this.getAggroList().addDamageHate(attacker, (int) damage, 0);
    }

    super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
  }

  protected void onDeath(Creature killer) {
    this._dieTime = System.currentTimeMillis();
    if (!this.isMonster() || !((MonsterInstance) this).isSeeded() && !((MonsterInstance) this).isSpoiled()) {
      if (this.isBoss()) {
        this.startDecay(20000L);
      } else if (this.isFlying()) {
        this.startDecay(4500L);
      } else {
        this.startDecay(8500L);
      }
    } else {
      this.startDecay(20000L);
    }

    this.setLHandId(this.getTemplate().lhand);
    this.setRHandId(this.getTemplate().rhand);
    this.setCollisionHeight(this.getTemplate().collisionHeight);
    this.setCollisionRadius(this.getTemplate().collisionRadius);
    this.getAI().stopAITask();
    this.stopRandomAnimation();
    super.onDeath(killer);
  }

  public long getDeadTime() {
    return this._dieTime <= 0L ? 0L : System.currentTimeMillis() - this._dieTime;
  }

  public AggroList getAggroList() {
    return this._aggroList;
  }

  public MinionList getMinionList() {
    return null;
  }

  public Location getRndMinionPosition() {
    return Location.findPointToStay(this, (int) this.getColRadius() + 30, (int) this.getColRadius() + 50);
  }

  public boolean hasMinions() {
    return false;
  }

  public void dropItem(Player lastAttacker, int itemId, long itemCount) {
    if (itemCount != 0L && lastAttacker != null) {
      for (long i = 0L; i < itemCount; ++i) {
        ItemInstance item = ItemFunctions.createItem(itemId);

        for (GlobalEvent e : this.getEvents()) {
          item.addEvent(e);
        }

        if (item.isStackable()) {
          i = itemCount;
          item.setCount(itemCount);
        }

        if (this.isRaid() || this instanceof ReflectionBossInstance) {
          SystemMessage2 sm;
          if (itemId == 57) {
            sm = new SystemMessage2(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
            sm.addName(this);
            sm.addLong(item.getCount());
          } else {
            sm = new SystemMessage2(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
            sm.addName(this);
            sm.addItemName(itemId);
            sm.addLong(item.getCount());
          }

          this.broadcastPacket(new L2GameServerPacket[]{sm});
        }

        lastAttacker.doAutoLootOrDrop(item, this);
      }

    }
  }

  public void dropItem(Player lastAttacker, ItemInstance item) {
    if (item.getCount() != 0L) {
      if (this.isRaid() || this instanceof ReflectionBossInstance) {
        SystemMessage2 sm;
        if (item.getItemId() == 57) {
          sm = new SystemMessage2(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
          sm.addName(this);
          sm.addLong(item.getCount());
        } else {
          sm = new SystemMessage2(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
          sm.addName(this);
          sm.addItemName(item.getItemId());
          sm.addLong(item.getCount());
        }

        this.broadcastPacket(new L2GameServerPacket[]{sm});
      }

      lastAttacker.doAutoLootOrDrop(item, this);
    }
  }

  public boolean isAttackable(Creature attacker) {
    return true;
  }

  public boolean isAutoAttackable(Creature attacker) {
    return false;
  }

  protected void onSpawn() {
    super.onSpawn();
    this._dieTime = 0L;
    this._spawnAnimation = 0;
    if (this.getAI().isGlobalAI() || this.getCurrentRegion() != null && this.getCurrentRegion().isActive()) {
      this.getAI().startAITask();
      this.startRandomAnimation();
    }

    ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_SPAWN));
    this.getListeners().onSpawn();
  }

  protected void onDespawn() {
    this.getAggroList().clear();
    this.getAI().onEvtDeSpawn();
    this.getAI().stopAITask();
    this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
    this.stopRandomAnimation();
    super.onDespawn();
  }

  public NpcTemplate getTemplate() {
    return (NpcTemplate) this._template;
  }

  public int getNpcId() {
    return this.getTemplate().npcId;
  }

  public void setUnAggred(boolean state) {
    this._unAggred = state;
  }

  public boolean isAggressive() {
    return this.getAggroRange() > 0;
  }

  public int getAggroRange() {
    if (this._unAggred) {
      return 0;
    } else {
      return this._personalAggroRange >= 0 ? this._personalAggroRange : this.getTemplate().aggroRange;
    }
  }

  public void setAggroRange(int aggroRange) {
    this._personalAggroRange = aggroRange;
  }

  public Faction getFaction() {
    return this.getTemplate().getFaction();
  }

  public boolean isInFaction(NpcInstance npc) {
    return this.getFaction().equals(npc.getFaction()) && !this.getFaction().isIgnoreNpcId(npc.getNpcId());
  }

  public int getMAtk(Creature target, Skill skill) {
    return (int) ((double) super.getMAtk(target, skill) * Config.ALT_NPC_MATK_MODIFIER);
  }

  public int getPAtk(Creature target) {
    return (int) ((double) super.getPAtk(target) * Config.ALT_NPC_PATK_MODIFIER);
  }

  public int getMaxHp() {
    return (int) ((double) super.getMaxHp() * Config.ALT_NPC_MAXHP_MODIFIER);
  }

  public int getMaxMp() {
    return (int) ((double) super.getMaxMp() * Config.ALT_NPC_MAXMP_MODIFIER);
  }

  public long getExpReward() {
    return (long) this.calcStat(Stats.EXP, (double) this.getTemplate().rewardExp, (Creature) null, (Skill) null);
  }

  public long getSpReward() {
    return (long) this.calcStat(Stats.SP, (double) this.getTemplate().rewardSp, (Creature) null, (Skill) null);
  }

  protected void onDelete() {
    this.stopDecay();
    if (this._spawn != null) {
      this._spawn.stopRespawn();
    }

    this.setSpawn((Spawner) null);
    super.onDelete();
  }

  public Spawner getSpawn() {
    return this._spawn;
  }

  public void setSpawn(Spawner spawn) {
    this._spawn = spawn;
  }

  protected void onDecay() {
    super.onDecay();
    this._spawnAnimation = 2;
    if (this._spawn != null) {
      this._spawn.decreaseCount(this);
    } else if (!this.isMinion()) {
      this.deleteMe();
    }

  }

  public final void decayOrDelete() {
    this.onDecay();
  }

  protected void startDecay(long delay) {
    this.stopDecay();
    this._decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
  }

  public void stopDecay() {
    if (this._decayTask != null) {
      this._decayTask.cancel(false);
      this._decayTask = null;
    }

  }

  public void endDecayTask() {
    if (this._decayTask != null) {
      this._decayTask.cancel(false);
      this._decayTask = null;
    }

    this.doDecay();
  }

  public boolean isUndead() {
    return this.getTemplate().isUndead();
  }

  public void setLevel(int level) {
    this._level = level;
  }

  public int getLevel() {
    return this._level == 0 ? this.getTemplate().level : this._level;
  }

  public void setDisplayId(int displayId) {
    this._displayId = displayId;
  }

  public int getDisplayId() {
    return this._displayId > 0 ? this._displayId : this.getTemplate().displayId;
  }

  public ItemInstance getActiveWeaponInstance() {
    return null;
  }

  public int getPhysicalAttackRange() {
    return (int) this.calcStat(Stats.POWER_ATTACK_RANGE, (double) this.getTemplate().baseAtkRange, (Creature) null, (Skill) null);
  }

  public WeaponTemplate getActiveWeaponItem() {
    int weaponId = this.getTemplate().rhand;
    if (weaponId < 1) {
      return null;
    } else {
      ItemTemplate item = ItemHolder.getInstance().getTemplate(this.getTemplate().rhand);
      return !(item instanceof WeaponTemplate) ? null : (WeaponTemplate) item;
    }
  }

  public ItemInstance getSecondaryWeaponInstance() {
    return null;
  }

  public WeaponTemplate getSecondaryWeaponItem() {
    int weaponId = this.getTemplate().lhand;
    if (weaponId < 1) {
      return null;
    } else {
      ItemTemplate item = ItemHolder.getInstance().getTemplate(this.getTemplate().lhand);
      return !(item instanceof WeaponTemplate) ? null : (WeaponTemplate) item;
    }
  }

  public void sendChanges() {
    if (!this.isFlying()) {
      super.sendChanges();
    }
  }

  public void broadcastCharInfo() {
    if (this.isVisible()) {
      if (this._broadcastCharInfoTask == null) {
        this._broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new NpcInstance.BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
      }
    }
  }

  public void broadcastCharInfoImpl() {
    Iterator var1 = World.getAroundPlayers(this).iterator();

    while (var1.hasNext()) {
      Player player = (Player) var1.next();
      player.sendPacket((new NpcInfo(this, player)).update());
    }

  }

  public void onRandomAnimation() {
    if (System.currentTimeMillis() - this._lastSocialAction > 10000L) {
      this.broadcastPacket(new L2GameServerPacket[]{new SocialAction(this.getObjectId(), 2)});
      this._lastSocialAction = System.currentTimeMillis();
    }

  }

  public void startRandomAnimation() {
    if (this.hasRandomAnimation()) {
      this._animationTask = LazyPrecisionTaskManager.getInstance().addNpcAnimationTask(this);
    }
  }

  public void stopRandomAnimation() {
    if (this._animationTask != null) {
      this._animationTask.cancel(false);
      this._animationTask = null;
    }

  }

  public boolean hasRandomAnimation() {
    return this._hasRandomAnimation;
  }

  public boolean hasRandomWalk() {
    return this._hasRandomWalk;
  }

  public Castle getCastle() {
    if (this.getReflection() == ReflectionManager.GIRAN_HARBOR && Config.SERVICES_GIRAN_HARBOR_NOTAX) {
      return null;
    } else if (Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && this.getReflection() == ReflectionManager.GIRAN_HARBOR) {
      return null;
    } else if (Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && this.isInZone(ZoneType.offshore)) {
      return null;
    } else {
      if (this._nearestCastle == null) {
        this._nearestCastle = (Castle) ResidenceHolder.getInstance().getResidence(this.getTemplate().getCastleId());
      }

      return this._nearestCastle;
    }
  }

  public Castle getCastle(Player player) {
    return this.getCastle();
  }

  public ClanHall getClanHall() {
    if (this._nearestClanHall == null) {
      this._nearestClanHall = (ClanHall) ResidenceHolder.getInstance().findNearestResidence(ClanHall.class, this.getX(), this.getY(), this.getZ(), this.getReflection(), 32768);
    }

    return this._nearestClanHall;
  }

  public void onAction(Player player, boolean shift) {
    if (!this.isTargetable()) {
      player.sendActionFailed();
    } else if (player.getTarget() != this) {
      player.setTarget(this);
      if (player.getTarget() == this) {
        player.sendPacket(new IStaticPacket[]{new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()), this.makeStatusUpdate(new int[]{9, 10})});
      }

      player.sendPacket(new IStaticPacket[]{new ValidateLocation(this), ActionFail.STATIC});
    } else if (Events.onAction(player, this, shift)) {
      player.sendActionFailed();
    } else if (this.isAutoAttackable(player)) {
      player.getAI().Attack(this, false, shift);
    } else if (!this.isInActingRange(player)) {
      if (!player.getAI().isIntendingInteract(this)) {
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
      }

    } else if (player.getKarma() > 0 && !this.canInteractWithKarmaPlayer() && !player.isGM()) {
      player.sendActionFailed();
    } else if (player.isFlying()) {
      player.sendActionFailed();
    } else if ((Config.ALLOW_TALK_WHILE_SITTING || !player.isSitting()) && !player.isAlikeDead()) {
      if (this.hasRandomAnimation()) {
        this.onRandomAnimation();
      }

      player.sendActionFailed();
      if (player.isMoving()) {
        player.stopMove();
      }

      player.setLastNpcInteractionTime();
      if (this._isBusy) {
        this.showBusyWindow(player);
      } else if (this.isHasChatWindow()) {
        boolean flag = false;
        Quest[] qlst = this.getTemplate().getEventQuests(QuestEventType.NPC_FIRST_TALK);
        if (qlst != null && qlst.length > 0) {
          Quest[] var5 = qlst;
          int var6 = qlst.length;

          for (int var7 = 0; var7 < var6; ++var7) {
            Quest element = var5[var7];
            QuestState qs = player.getQuestState(element.getName());
            if ((qs == null || !qs.isCompleted()) && element.notifyFirstTalk(this, player)) {
              flag = true;
            }
          }
        }

        if (!flag) {
          this.showChatWindow(player, 0);
        }
      }

    }
  }

  protected boolean canInteractWithKarmaPlayer() {
    if (Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP) {
      return true;
    } else {
      return this instanceof WarehouseInstance;
    }
  }

  public void showQuestWindow(Player player, String questId) {
    if (player.isQuestContinuationPossible(true)) {
      int count = 0;
      QuestState[] var4 = player.getAllQuestsStates();
      int var5 = var4.length;

      for (int var6 = 0; var6 < var5; ++var6) {
        QuestState quest = var4[var6];
        if (quest != null && quest.getQuest().isVisible() && quest.isStarted() && quest.getCond() > 0) {
          ++count;
        }
      }

      if (count > 40) {
        this.showChatWindow(player, "quest-limit.htm");
      } else {
        try {
          QuestState qs = player.getQuestState(questId);
          if (qs != null) {
            if (qs.isCompleted()) {
              this.showChatWindow(player, "completed-quest.htm");
              return;
            }

            if (qs.getQuest().notifyTalk(this, qs)) {
              return;
            }
          } else {
            Quest q = QuestManager.getQuest(questId);
            if (q != null) {
              Quest[] qlst = this.getTemplate().getEventQuests(QuestEventType.QUEST_START);
              if (qlst != null && qlst.length > 0) {
                Quest[] var15 = qlst;
                int var8 = qlst.length;

                for (int var9 = 0; var9 < var8; ++var9) {
                  Quest element = var15[var9];
                  if (element == q) {
                    qs = q.newQuestState(player, 1);
                    if (qs.getQuest().notifyTalk(this, qs)) {
                      return;
                    }
                    break;
                  }
                }
              }
            }
          }

          this.showChatWindow(player, "no-quest.htm");
        } catch (Exception var11) {
          log.warn("problem with npc text(questId: " + questId + ") " + var11);
          log.error("", var11);
        }

        player.sendActionFailed();
      }
    }
  }

  public static boolean canBypassCheck(Player player, NpcInstance npc) {
    if (npc != null && !player.isActionsDisabled() && (Config.ALLOW_TALK_WHILE_SITTING || !player.isSitting()) && npc.isInActingRange(player)) {
      return true;
    } else {
      player.sendActionFailed();
      return false;
    }
  }

  public void onBypassFeedback(Player player, String command) {
    if (canBypassCheck(player, this)) {
      try {
        Castle castle;
        if (command.equalsIgnoreCase("TerritoryStatus")) {
          NpcHtmlMessage html = new NpcHtmlMessage(player, this);
          html.setFile("merchant/territorystatus.htm");
          html.replace("%npcname%", this.getName());
          castle = this.getCastle(player);
          if (castle != null && castle.getId() > 0) {
            html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
            html.replace("%taxpercent%", String.valueOf(castle.getTaxPercent()));
            if (castle.getOwnerId() > 0) {
              Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
              if (clan != null) {
                html.replace("%clanname%", clan.getName());
                html.replace("%clanleadername%", clan.getLeaderName());
              } else {
                html.replace("%clanname%", "unexistant clan");
                html.replace("%clanleadername%", "None");
              }
            } else {
              html.replace("%clanname%", "NPC");
              html.replace("%clanleadername%", "None");
            }
          } else {
            html.replace("%castlename%", "Open");
            html.replace("%taxpercent%", "0");
            html.replace("%clanname%", "No");
            html.replace("%clanleadername%", this.getName());
          }

          player.sendPacket(html);
        } else {
          String listId;
          if (command.startsWith("Quest")) {
            listId = command.substring(5).trim();
            if (listId.length() == 0) {
              this.showQuestWindow(player);
            } else {
              this.showQuestWindow(player, listId);
            }
          } else {
            int val;
            if (command.startsWith("Chat")) {
              try {
                val = Integer.parseInt(command.substring(5));
                this.showChatWindow(player, val);
              } catch (NumberFormatException var7) {
                String filename = command.substring(5).trim();
                if (filename.length() == 0) {
                  this.showChatWindow(player, "npcdefault.htm");
                } else {
                  this.showChatWindow(player, filename);
                }
              }
            } else if (command.startsWith("AttributeCancel")) {
              player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
            } else if (command.startsWith("NpcLocationInfo")) {
              val = Integer.parseInt(command.substring(16));
              NpcInstance npc = GameObjectsStorage.getByNpcId(val);
              if (npc != null) {
                player.sendPacket(new RadarControl(2, 2, npc.getLoc()));
                player.sendPacket(new RadarControl(0, 1, npc.getLoc()));
              }
            } else if (!command.startsWith("Multisell") && !command.startsWith("multisell")) {
              if (command.startsWith("EnterRift")) {
                StringTokenizer st = new StringTokenizer(command);
                st.nextToken();
                Integer b1 = Integer.parseInt(st.nextToken());
                DimensionalRiftManager.getInstance().start(player, b1, this);
              } else if (command.startsWith("ChangeRiftRoom")) {
                if (player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift) {
                  ((DimensionalRift) player.getParty().getReflection()).manualTeleport(player, this);
                } else {
                  DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
                }
              } else if (command.startsWith("ExitRift")) {
                if (player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift) {
                  ((DimensionalRift) player.getParty().getReflection()).manualExitRift(player, this);
                } else {
                  DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
                }
              } else if (command.equalsIgnoreCase("SkillList")) {
                this.showSkillList(player);
              } else if (command.startsWith("AltSkillList")) {
                val = Integer.parseInt(command.substring(13).trim());
                this.showSkillList(player, ClassId.VALUES[val]);
              } else if (command.equalsIgnoreCase("SkillEnchantList")) {
                this.showSkillEnchantList(player);
              } else if (command.equalsIgnoreCase("ClanSkillList")) {
                showClanSkillList(player);
              } else if (command.startsWith("Augment")) {
                val = Integer.parseInt(command.substring(8, 9).trim());
                if (val == 1) {
                  player.setRefineryHandler(RefineryHandler.getInstance());
                  RefineryHandler.getInstance().onInitRefinery(player);
                } else if (val == 2) {
                  player.setRefineryHandler(RefineryHandler.getInstance());
                  RefineryHandler.getInstance().onInitRefineryCancel(player);
                }
              } else if (command.startsWith("Link")) {
                this.showChatWindow(player, command.substring(5));
              } else {
                TeleportLocation[] list;
                if (command.startsWith("Teleport")) {
                  val = Integer.parseInt(command.substring(9).trim());
                  list = this.getTemplate().getTeleportList(val);
                  if (list != null) {
                    this.showTeleportList(player, list);
                  } else {
                    player.sendMessage(new CustomMessage("Common.BrokenLink", player, new Object[0]));
                  }
                } else if (command.startsWith("Tele20Lvl")) {
                  val = Integer.parseInt(command.substring(10, 11).trim());
                  list = this.getTemplate().getTeleportList(val);
                  if (player.getLevel() > 20) {
                    this.showChatWindow(player, "teleporter/" + this.getNpcId() + "-no.htm");
                  } else if (list != null) {
                    this.showTeleportList(player, list);
                  } else {
                    player.sendMessage(new CustomMessage("Common.BrokenLink", player, new Object[0]));
                  }
                } else if (command.startsWith("open_gate")) {
                  val = Integer.parseInt(command.substring(10));
                  ReflectionUtils.getDoor(val).openMe();
                  player.sendActionFailed();
                } else if (command.startsWith("ExitFromQuestInstance")) {
                  Reflection r = player.getReflection();
                  r.startCollapseTimer(60000L);
                  player.teleToLocation(r.getReturnLoc(), 0);
                  if (command.length() > 22) {
                    try {
                      int anInt = Integer.parseInt(command.substring(22));
                      this.showChatWindow(player, anInt);
                    } catch (NumberFormatException var6) {
                      String filename = command.substring(22).trim();
                      if (filename.length() > 0) {
                        this.showChatWindow(player, filename);
                      }
                    }
                  }
                }
              }
            } else {
              listId = command.substring(9).trim();
              castle = this.getCastle(player);
              MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(listId), player, castle != null ? castle.getTaxRate() : 0.0D);
            }
          }
        }
      } catch (StringIndexOutOfBoundsException var8) {
        log.info("Incorrect htm bypass! npcId=" + this.getTemplate().npcId + " command=[" + command + "]");
      } catch (NumberFormatException var9) {
        log.info("Invalid bypass to Server command parameter! npcId=" + this.getTemplate().npcId + " command=[" + command + "]");
      }

    }
  }

  public void showTeleportList(Player player, TeleportLocation[] list) {
    StringBuilder sb = new StringBuilder();
    sb.append("&$556;").append("<br><br>");
    if (list != null && player.getPlayerAccess().UseTeleport) {
      TeleportLocation[] var4 = list;
      int var5 = list.length;

      for (int var6 = 0; var6 < var5; ++var6) {
        TeleportLocation tl = var4[var6];
        if (tl.getItem().getItemId() == 57) {
          double pricemod = player.getLevel() <= Config.GATEKEEPER_FREE ? 0.0D : Config.GATEKEEPER_MODIFIER;
          if (tl.getPrice() > 0L && pricemod > 0.0D) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(7);
            int hour = Calendar.getInstance().get(11);
            if ((day == 1 || day == 7) && hour >= 20 && hour <= 12) {
              pricemod /= 2.0D;
            }
          }

          sb.append("[scripts_Util:Gatekeeper ").append(tl.getX()).append(" ").append(tl.getY()).append(" ").append(tl.getZ());
          if (tl.getCastleId() != 0) {
            sb.append(" ").append(tl.getCastleId());
          }

          String name = (new CustomMessage(tl.getName(), player, new Object[0])).toString();
          sb.append(" ").append((long) ((double) tl.getPrice() * pricemod)).append(" @811;F;").append(name).append("|").append(name);
          if ((double) tl.getPrice() * pricemod > 0.0D) {
            sb.append(" - ").append((long) ((double) tl.getPrice() * pricemod)).append(" ").append(HtmlUtils.htmlItemName(57));
          }

          if (tl.getMinLevel() > 0) {
            sb.append(" - ").append((new CustomMessage("l2.gameserver.model.instances.NpcInstance.TeleportListMinLevel", player, new Object[]{tl.getMinLevel()})).toString());
          }

          if (tl.getMaxLevel() > 0) {
            sb.append(" - ").append((new CustomMessage("l2.gameserver.model.instances.NpcInstance.TeleportListMaxLevel", player, new Object[]{tl.getMaxLevel()})).toString());
          }

          sb.append("]<br1>\n");
        } else {
          String name = (new CustomMessage(tl.getName(), player, new Object[0])).toString();
          sb.append("[scripts_Util:QuestGatekeeper ").append(tl.getX()).append(" ").append(tl.getY()).append(" ").append(tl.getZ()).append(" ").append(tl.getPrice()).append(" ").append(tl.getItem().getItemId()).append(" @811;F;").append(name).append("|").append(name).append(" - ").append(tl.getPrice()).append(" ").append(HtmlUtils.htmlItemName(tl.getItem().getItemId()));
          if (tl.getMinLevel() > 0) {
            sb.append(" - ").append((new CustomMessage("l2.gameserver.model.instances.NpcInstance.TeleportListMinLevel", player, new Object[]{tl.getMinLevel()})).toString());
          }

          if (tl.getMaxLevel() > 0) {
            sb.append(" - ").append((new CustomMessage("l2.gameserver.model.instances.NpcInstance.TeleportListMaxLevel", player, new Object[]{tl.getMaxLevel()})).toString());
          }

          sb.append("]<br1>\n");
        }
      }
    } else {
      sb.append("No teleports available for you.");
    }

    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
    html.setHtml(Strings.bbParse(sb.toString()));
    player.sendPacket(html);
  }

  public void showQuestWindow(Player player) {
    List<Quest> options = new ArrayList<>();
    List<QuestState> awaits = player.getQuestsForEvent(this, QuestEventType.QUEST_TALK);
    Quest[] starts = this.getTemplate().getEventQuests(QuestEventType.QUEST_START);
    if (awaits != null) {
      Iterator var5 = awaits.iterator();

      while (var5.hasNext()) {
        QuestState x = (QuestState) var5.next();
        if (!options.contains(x.getQuest()) && x.getQuest().getQuestIntId() > 0) {
          options.add(x.getQuest());
        }
      }
    }

    if (starts != null) {
      Quest[] var9 = starts;
      int var10 = starts.length;

      for (int var7 = 0; var7 < var10; ++var7) {
        Quest x = var9[var7];
        if (!options.contains(x) && x.getQuestIntId() > 0) {
          options.add(x);
        }
      }
    }

    if (options.size() > 1) {
      this.showQuestChooseWindow(player, (Quest[]) options.toArray(new Quest[options.size()]));
    } else if (options.size() == 1) {
      this.showQuestWindow(player, ((Quest) options.get(0)).getName());
    } else {
      this.showQuestWindow(player, "");
    }

  }

  public void showQuestChooseWindow(Player player, Quest[] quests) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><body><title>Talk about:</title><br>");
    Quest[] var4 = quests;
    int var5 = quests.length;

    for (int var6 = 0; var6 < var5; ++var6) {
      Quest q = var4[var6];
      if (q.isVisible()) {
        sb.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_Quest ").append(q.getName()).append("\">[").append(q.getDescr(player)).append("]</a><br>");
      }
    }

    sb.append("</body></html>");
    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
    html.setHtml(sb.toString());
    player.sendPacket(html);
  }

  public void showChatWindow(Player player, int val, Object... replace) {
    String filename;
    int i;
    filename = "seven_signs/";
    int npcId = this.getNpcId();
    label55:
    switch (npcId) {
      case 30298:
        if (player.getPledgeType() == -1) {
          filename = this.getHtmlPath(npcId, 1, player);
        } else {
          filename = this.getHtmlPath(npcId, 0, player);
        }
        break;
      case 31111:
        int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(1);
        i = SevenSigns.getInstance().getPlayerCabal(player);
        int compWinner = SevenSigns.getInstance().getCabalHighestScore();
        if (i == sealAvariceOwner && i == compWinner) {
          switch (sealAvariceOwner) {
            case 0:
              filename = filename + "spirit_null.htm";
              break label55;
            case 1:
              filename = filename + "spirit_dusk.htm";
              break label55;
            case 2:
              filename = filename + "spirit_dawn.htm";
          }
        } else {
          filename = filename + "spirit_null.htm";
        }
        break;
      case 31112:
        filename = filename + "spirit_exit.htm";
        break;
      default:
        if (npcId >= 31093 && npcId <= 31094 || npcId >= 31172 && npcId <= 31201 || npcId >= 31239 && npcId <= 31254) {
          return;
        }

        filename = this.getHtmlPath(npcId, val, player);
    }

    NpcHtmlMessage packet = new NpcHtmlMessage(player, this, filename, val);
    if (replace.length % 2 == 0) {
      for (i = 0; i < replace.length; i += 2) {
        packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
      }
    }

    player.sendPacket(packet);
  }

  public void showChatWindow(Player player, String filename, Object... replace) {
    NpcHtmlMessage packet = new NpcHtmlMessage(player, this, filename, 0);
    if (replace.length % 2 == 0) {
      for (int i = 0; i < replace.length; i += 2) {
        packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
      }
    }

    player.sendPacket(packet);
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    if (this.getTemplate().getHtmRoot() != null) {
      return this.getTemplate().getHtmRoot() + pom + ".htm";
    } else {
      String temp = "default/" + pom + ".htm";
      if (HtmCache.getInstance().getNullable(temp, player) != null) {
        return temp;
      } else {
        temp = "trainer/" + pom + ".htm";
        return HtmCache.getInstance().getNullable(temp, player) != null ? temp : "npcdefault.htm";
      }
    }
  }

  public final boolean isBusy() {
    return this._isBusy;
  }

  public void setBusy(boolean isBusy) {
    this._isBusy = isBusy;
  }

  public final String getBusyMessage() {
    return this._busyMessage;
  }

  public void setBusyMessage(String message) {
    this._busyMessage = message;
  }

  public void showBusyWindow(Player player) {
    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
    html.setFile("npcbusy.htm");
    html.replace("%npcname%", this.getName());
    html.replace("%playername%", player.getName());
    html.replace("%busymessage%", this._busyMessage);
    player.sendPacket(html);
  }

  public void showSkillEnchantList(Player player) {
    ClassId classId = player.getClassId();
    NpcHtmlMessage html;
    StringBuilder sb;
    if (player.getClassId().getLevel() < 4) {
      html = new NpcHtmlMessage(player, this);
      sb = new StringBuilder();
      sb.append("<html><head><body>");
      if (player.isLangRus()) {
        sb.append("Мастер:<br>");
        sb.append("Вы должны выполнить квест на получение третьей профессии.");
      } else {
        sb.append("Trainer:<br>");
        sb.append("You must have 3rd class change quest completed.");
      }

      sb.append("</body></html>");
      html.setHtml(sb.toString());
      player.sendPacket(html);
    } else if (player.getLevel() < 76) {
      player.sendPacket(new SystemMessage(1438));
    } else if (!this.getTemplate().canTeach(classId) && !this.getTemplate().canTeach(classId.getParent(player.getSex()))) {
      if (this instanceof TrainerInstance) {
        this.showChatWindow(player, "trainer/" + this.getNpcId() + "-noteach.htm");
      } else {
        html = new NpcHtmlMessage(player, this);
        sb = new StringBuilder();
        sb.append("<html><head><body>");
        sb.append(new CustomMessage("l2p.gameserver.model.instances.L2NpcInstance.WrongTeacherClass", player, new Object[0]));
        sb.append("</body></html>");
        html.setHtml(sb.toString());
        player.sendPacket(html);
      }

    } else {
      player.sendPacket(ExEnchantSkillList.packetFor(player));
    }
  }

  public void showSkillList(Player player) {
    ClassId classId = player.getClassId();
    this.showSkillList(player, player.getClassId());
  }

  public void showSkillList(Player player, ClassId classId) {
    if (classId != null) {
      int npcId = this.getTemplate().npcId;
      NpcHtmlMessage html;
      StringBuilder sb;
      if (this.getTemplate().getTeachInfo().isEmpty()) {
        html = new NpcHtmlMessage(player, this);
        sb = new StringBuilder();
        sb.append("<html><head><body>");
        if (player.getVar("lang@").equalsIgnoreCase("en")) {
          sb.append("I cannot teach you. My class list is empty.<br> Ask admin to fix it. <br>NpcId:" + npcId + ", Your classId:" + classId.name() + "<br>");
        } else {
          sb.append("Я не могу обучить тебя. Для твоего класса мой список пуст.<br> Свяжись с админом для фикса этого. <br>NpcId:" + npcId + ", твой classId:" + classId.name() + "<br>");
        }

        sb.append("</body></html>");
        html.setHtml(sb.toString());
        player.sendPacket(html);
      } else if (!this.getTemplate().canTeach(classId) && !this.getTemplate().canTeach(classId.getParent(player.getSex()))) {
        if (this instanceof WarehouseInstance) {
          this.showChatWindow(player, "warehouse/" + this.getNpcId() + "-noteach.htm");
        } else if (this instanceof TrainerInstance) {
          this.showChatWindow(player, "trainer/" + this.getNpcId() + "-noteach.htm");
        } else {
          html = new NpcHtmlMessage(player, this);
          sb = new StringBuilder();
          sb.append("<html><head><body>");
          sb.append(new CustomMessage("l2p.gameserver.model.instances.L2NpcInstance.WrongTeacherClass", player, new Object[0]));
          sb.append("</body></html>");
          html.setHtml(sb.toString());
          player.sendPacket(html);
        }

      } else {
        Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, classId, AcquireType.NORMAL, (SubUnit) null);
        AcquireSkillList asl = new AcquireSkillList(AcquireType.NORMAL, skills.size());
        int counts = 0;
        Iterator var7 = skills.iterator();

        while (true) {
          SkillLearn s;
          Skill sk;
          do {
            do {
              do {
                if (!var7.hasNext()) {
                  if (counts == 0) {
                    int minlevel = SkillAcquireHolder.getInstance().getMinLevelForNewSkill(classId, player.getLevel(), AcquireType.NORMAL);
                    if (minlevel > 0) {
                      SystemMessage2 sm = new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                      sm.addInteger((double) minlevel);
                      player.sendPacket(sm);
                    } else {
                      player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
                    }

                    player.sendPacket(AcquireSkillDone.STATIC);
                  } else {
                    player.setVar("AcquireSkillClassId", classId.getId(), -1L);
                    player.sendPacket(asl);
                  }

                  player.sendActionFailed();
                  return;
                }

                s = (SkillLearn) var7.next();
              } while (s.isClicked());

              sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            } while (sk == null);
          } while (!Config.ALT_WEAK_SKILL_LEARN && (!sk.getCanLearn(player.getClassId()) || !sk.canTeachBy(npcId)));

          ++counts;
          asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), 0);
        }
      }
    }
  }

  public static void showFishingSkillList(Player player) {
    showAcquireList(AcquireType.FISHING, player);
  }

  public static void showClanSkillList(Player player) {
    if (player.getClan() != null && player.isClanLeader()) {
      showAcquireList(AcquireType.CLAN, player);
    } else {
      player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
      player.sendActionFailed();
    }
  }

  public static void showAcquireList(AcquireType t, Player player) {
    Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, t);
    AcquireSkillList asl = new AcquireSkillList(t, skills.size());
    Iterator var4 = skills.iterator();

    while (var4.hasNext()) {
      SkillLearn s = (SkillLearn) var4.next();
      asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), 0);
    }

    if (skills.size() == 0) {
      player.sendPacket(AcquireSkillDone.STATIC);
      player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
    } else {
      player.unsetVar("AcquireSkillClassId");
      player.sendPacket(asl);
    }

    player.sendActionFailed();
  }

  public int getSpawnAnimation() {
    return this._spawnAnimation;
  }

  public double getColRadius() {
    return this.getCollisionRadius();
  }

  public double getColHeight() {
    return this.getCollisionHeight();
  }

  public int calculateLevelDiffForDrop(int charLevel) {
    if (Config.DEEPBLUE_DROP_RULES && !this._ignoreDropDiffPenalty) {
      int mobLevel = this.getLevel();
      int deepblue_maxdiff = this instanceof RaidBossInstance ? Config.DEEPBLUE_DROP_RAID_MAXDIFF : Config.DEEPBLUE_DROP_MAXDIFF;
      return Math.max(charLevel - mobLevel - deepblue_maxdiff, 0);
    } else {
      return 0;
    }
  }

  public boolean isSevenSignsMonster() {
    return this.getFaction().getName().equalsIgnoreCase("c_dungeon_clan");
  }

  public String toString() {
    return this.getNpcId() + " " + this.getName();
  }

  public void refreshID() {
    this.objectId = IdFactory.getInstance().getNextId();
    this._storedId = GameObjectsStorage.refreshId(this);
  }

  public void setUnderground(boolean b) {
    this._isUnderground = b;
  }

  public boolean isUnderground() {
    return this._isUnderground;
  }

  public boolean isTargetable() {
    return this._isTargetable;
  }

  public void setTargetable(boolean value) {
    this._isTargetable = value;
  }

  public boolean isShowName() {
    return this._showName;
  }

  public void setShowName(boolean value) {
    this._showName = value;
  }

  public NpcListenerList getListeners() {
    if (this.listeners == null) {
      synchronized (this) {
        if (this.listeners == null) {
          this.listeners = new NpcListenerList(this);
        }
      }
    }

    return (NpcListenerList) this.listeners;
  }

  public <T extends NpcListener> boolean addListener(T listener) {
    return this.getListeners().add(listener);
  }

  public <T extends NpcListener> boolean removeListener(T listener) {
    return this.getListeners().remove(listener);
  }

  public NpcStatsChangeRecorder getStatsRecorder() {
    if (this._statsRecorder == null) {
      synchronized (this) {
        if (this._statsRecorder == null) {
          this._statsRecorder = new NpcStatsChangeRecorder(this);
        }
      }
    }

    return (NpcStatsChangeRecorder) this._statsRecorder;
  }

  public void setNpcState(int stateId) {
    this.broadcastPacket(new L2GameServerPacket[]{new ExChangeNpcState(this.getObjectId(), stateId)});
    this.npcState = stateId;
  }

  public int getNpcState() {
    return this.npcState;
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    List<L2GameServerPacket> list = new ArrayList(3);
    list.add(new NpcInfo(this, forPlayer));
    if (this.isInCombat()) {
      list.add(new AutoAttackStart(this.getObjectId()));
    }

    if (this.isMoving() || this.isFollowing()) {
      list.add(this.movePacket());
    }

    return list;
  }

  public Clan getClan() {
    Castle castle = this.getCastle();
    return castle != null ? castle.getOwner() : null;
  }

  public boolean isNpc() {
    return true;
  }

  public int getGeoZ(Location loc) {
    if (!this.isFlying() && !this.isInWater() && !this.isInBoat() && !this.isBoat() && !this.isDoor()) {
      if (this.isNpc()) {
        return this._spawnRange instanceof Territory ? GeoEngine.getHeight(loc, this.getGeoIndex()) : loc.z;
      } else {
        return super.getGeoZ(loc);
      }
    } else {
      return loc.z;
    }
  }

  public boolean isMerchantNpc() {
    return false;
  }

  public SpawnRange getSpawnRange() {
    return this._spawnRange;
  }

  public void setSpawnRange(SpawnRange spawnRange) {
    this._spawnRange = spawnRange;
  }

  public void setParameter(String str, Object val) {
    if (this._parameters == StatsSet.EMPTY) {
      this._parameters = new StatsSet();
    }

    this._parameters.set(str, val);
  }

  public void setParameters(MultiValueSet<String> set) {
    if (!set.isEmpty()) {
      if (this._parameters == StatsSet.EMPTY) {
        this._parameters = new MultiValueSet(set.size());
      }

      this._parameters.putAll(set);
    }
  }

  public int getParameter(String str, int val) {
    return this._parameters.getInteger(str, val);
  }

  public long getParameter(String str, long val) {
    return this._parameters.getLong(str, val);
  }

  public boolean getParameter(String str, boolean val) {
    return this._parameters.getBool(str, val);
  }

  public String getParameter(String str, String val) {
    return this._parameters.getString(str, val);
  }

  public MultiValueSet<String> getParameters() {
    return this._parameters;
  }

  public boolean isInvul() {
    return true;
  }

  public boolean isHasChatWindow() {
    return this._hasChatWindow;
  }

  public void setHasChatWindow(boolean hasChatWindow) {
    this._hasChatWindow = hasChatWindow;
  }

  public class BroadcastCharInfoTask extends RunnableImpl {
    public BroadcastCharInfoTask() {
    }

    public void runImpl() throws Exception {
      NpcInstance.this.broadcastCharInfoImpl();
      NpcInstance.this._broadcastCharInfoTask = null;
    }
  }
}
