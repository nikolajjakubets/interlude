//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.collections.LazyArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.dbutils.DbUtils;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.NextAction;
import l2.gameserver.ai.PlayerAI;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.*;
import l2.gameserver.data.xml.holder.*;
import l2.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.handler.items.IRefineryHandler;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.*;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.listener.actor.player.impl.SummonAnswerListener;
import l2.gameserver.model.Effect.EEffectSlot;
import l2.gameserver.model.GameObjectTasks.*;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.model.Skill.AddedSkill;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.actor.instances.player.FriendList;
import l2.gameserver.model.actor.instances.player.*;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.actor.recorder.PlayerStatsChangeRecorder;
import l2.gameserver.model.base.*;
import l2.gameserver.model.chat.chatfilter.ChatMsg;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.oly.*;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.*;
import l2.gameserver.model.items.*;
import l2.gameserver.model.items.Warehouse.WarehouseType;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.model.items.attachment.PickableAttachment;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.pledge.*;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SceneMovie;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.*;
import l2.gameserver.scripts.Events;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.skills.skillclasses.Transformation;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.*;
import l2.gameserver.taskmanager.AutoSaveManager;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.templates.FishTemplate;
import l2.gameserver.templates.Henna;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.*;
import l2.gameserver.utils.Log.ItemLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.Containers;
import org.napile.primitive.iterators.IntIterator;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j
public class Player extends Playable implements PlayerGroup {
  public static final int DEFAULT_TITLE_COLOR = 16777079;
  public static final int MAX_POST_FRIEND_SIZE = 100;
  public static final int MAX_FRIEND_SIZE = 128;
  public static final String NO_TRADERS_VAR = "notraders";
  public static final String CUSTOM_HERO_END_TIME_VAR = "CustomHeroEndTime";
  public static final String ANIMATION_OF_CAST_RANGE_VAR = "buffAnimRange";
  public static final String LAST_PVP_PK_KILL_VAR_NAME = "LastPVPPKKill";
  private static final String NOT_CONNECTED = "<not connected>";
  public Map<Integer, SubClass> _classlist;
  public static final int OBSERVER_NONE = 0;
  public static final int OBSERVER_STARTING = 1;
  public static final int OBSERVER_STARTED = 3;
  public static final int OBSERVER_LEAVING = 2;
  public static final int STORE_PRIVATE_NONE = 0;
  public static final int STORE_PRIVATE_SELL = 1;
  public static final int STORE_PRIVATE_BUY = 3;
  public static final int STORE_PRIVATE_MANUFACTURE = 5;
  public static final int STORE_OBSERVING_GAMES = 7;
  public static final int STORE_PRIVATE_SELL_PACKAGE = 8;
  public static final int RANK_VAGABOND = 0;
  public static final int RANK_VASSAL = 1;
  public static final int RANK_HEIR = 2;
  public static final int RANK_KNIGHT = 3;
  public static final int RANK_WISEMAN = 4;
  public static final int RANK_BARON = 5;
  public static final int RANK_VISCOUNT = 6;
  public static final int RANK_COUNT = 7;
  public static final int RANK_MARQUIS = 8;
  public static final int LANG_ENG = 0;
  public static final int LANG_RUS = 1;
  public static final int LANG_UNK = -1;
  public static final int PLAYER_SEX_MALE = 0;
  public static final int PLAYER_SEX_FEMALE = 1;
  public static final int[] EXPERTISE_LEVELS = new int[]{0, 20, 40, 52, 61, 76, 2147483647};
  private GameClient _connection;
  private String _login;
  private int _karma;
  private int _pkKills;
  private int _pvpKills;
  private int _face;
  private int _hairStyle;
  private int _hairColor;
  private boolean _isUndying;
  private int _deleteTimer;
  private Stadium _olyObserveStadium;
  private Participant _olyParticipant;
  private long _createTime;
  private long _onlineTime;
  private long _onlineBeginTime;
  private long _leaveClanTime;
  private long _deleteClanTime;
  private long _NoChannel;
  private long _NoChannelBegin;
  private long _uptime;
  private long _lastAccess;
  private int _nameColor;
  private int _titlecolor;
  private String _disconnectedTitle;
  private int _disconnectedTitleColor;
  private boolean _overloaded;
  volatile boolean sittingTaskLaunched;
  private int _fakeDeath;
  private int _waitTimeWhenSit;
  private boolean _autoLoot;
  private boolean AutoLootHerbs;
  private boolean AutoLootAdena;
  private final PcInventory _inventory;
  private final Warehouse _warehouse;
  private final ItemContainer _refund;
  private final PcFreight _freight;
  private long _lastNpcInteractionTime;
  private final Deque<ChatMsg> _msgBucket;
  private final Map<Integer, Recipe> _recipebook;
  private final Map<Integer, Recipe> _commonrecipebook;
  private Map<Integer, PremiumItem> _premiumItems;
  private final Map<String, QuestState> _quests;
  private final ShortCutList _shortCuts;
  private final MacroList _macroses;
  private AtomicInteger _privatestore;
  private String _manufactureName;
  private List<ManufactureItem> _createList;
  private String _sellStoreName;
  private List<TradeItem> _sellList;
  private List<TradeItem> _packageSellList;
  private String _buyStoreName;
  private List<TradeItem> _buyList;
  private List<TradeItem> _tradeList;
  private final Henna[] _henna;
  private int _hennaSTR;
  private int _hennaINT;
  private int _hennaDEX;
  private int _hennaMEN;
  private int _hennaWIT;
  private int _hennaCON;
  private Party _party;
  private Location _lastPartyPosition;
  private Clan _clan;
  private int _pledgeClass;
  private int _pledgeType;
  private int _powerGrade;
  private int _lvlJoinedAcademy;
  private int _apprentice;
  private int _accessLevel;
  private PlayerAccess _playerAccess;
  private boolean _messageRefusal;
  private boolean _tradeRefusal;
  private boolean _blockAll;
  private Summon _summon;
  private boolean _riding;
  private Map<Integer, EffectCubic> _cubics;
  private int _agathionId;
  private Request _request;
  private ItemInstance _arrowItem;
  private WeaponTemplate _fistsWeaponItem;
  private Map<Integer, String> _chars;
  public int expertiseIndex;
  private ItemInstance _enchantScroll;
  private IRefineryHandler _refineryHandler;
  private WarehouseType _usingWHType;
  private boolean _isOnline;
  private AtomicBoolean _isLogout;
  private HardReference<NpcInstance> _lastNpc;
  private MultiSellListContainer _multisell;
  private Set<Integer> _activeSoulShots;
  private WorldRegion _observerRegion;
  private AtomicInteger _observerMode;
  public int _telemode;
  public boolean entering;
  public Location _stablePoint;
  public int[] _loto;
  public int[] _race;
  private final Map<Integer, String> _blockList;
  private final FriendList _friendList;
  private boolean _hero;
  private Boat _boat;
  private Location _inBoatPosition;
  protected int _baseClass;
  protected SubClass _activeClass;
  private Bonus _bonus;
  private Future<?> _bonusExpiration;
  private boolean _isSitting;
  private StaticObjectInstance _sittingObject;
  private boolean _noble;
  private int _varka;
  private int _ketra;
  private int _ram;
  private byte[] _keyBindings;
  private int _cursedWeaponEquippedId;
  private final Fishing _fishing;
  private boolean _isFishing;
  private Future<?> _taskWater;
  private Future<?> _autoSaveTask;
  private Future<?> _kickTask;
  private Future<?> _pcCafePointsTask;
  private Future<?> _unjailTask;
  private Future<?> _customHeroRemoveTask;
  private final Lock _storeLock;
  private int _zoneMask;
  private boolean _offline;
  private int _transformationId;
  private int _transformationTemplate;
  private String _transformationName;
  private String _transformationTitle;
  private int _pcBangPoints;
  Map<Integer, Skill> _transformationSkills;
  private int _expandInventory;
  private int _expandWarehouse;
  private int _buffAnimRange;
  private int _battlefieldChatId;
  private InvisibleType _invisibleType;
  private IntObjectMap<String> _postFriends;
  private List<String> _blockedActions;
  private boolean _notShowTraders;
  private boolean _debug;
  private long _dropDisabled;
  private long _lastItemAuctionInfoRequest;
  private IntObjectMap<TimeStamp> _sharedGroupReuses;
  private Pair<Integer, OnAnswerListener> _askDialog;
  private MatchingRoom _matchingRoom;
  private final Map<Integer, Long> _instancesReuses;
  private int _receivedRec;
  private int _givableRec;
  private IntSet _recommendedCharIds;
  private Future<?> _updateEffectIconsTask;
  private final AtomicReference<Player.MoveToLocationOffloadData> _mtlOffloadData;
  private ScheduledFuture<?> _broadcastCharInfoTask;
  private int _polyNpcId;
  private Future<?> _userInfoTask;
  private int _mountNpcId;
  private int _mountObjId;
  private int _mountLevel;
  private final MultiValueSet<String> _vars;
  private boolean _resurect_prohibited;
  private boolean _maried;
  private int _partnerId;
  private int _coupleId;
  private boolean _maryrequest;
  private boolean _maryaccepted;
  private boolean _charmOfCourage;
  private int _increasedForce;
  private long _increasedForceLastUpdateTimeStamp;
  private Future<?> _increasedForceCleanupTask;
  private int _consumedSouls;
  private long _lastFalling;
  private Location _lastClientPosition;
  private Location _lastServerPosition;
  private int _useSeed;
  protected int _pvpFlag;
  private Future<?> _PvPRegTask;
  private long _lastPvpAttack;
  private TamedBeastInstance _tamedBeast;
  private long _lastAttackPacket;
  private Location _groundSkillLoc;
  private int _buyListId;
  private int _incorrectValidateCount;
  private int _movieId;
  private boolean _isInMovie;
  private ItemInstance _petControlItem;
  private AtomicBoolean isActive;
  private Map<Integer, Long> _traps;
  private Future<?> _hourlyTask;
  private int _hoursInGame;
  private Map<String, String> _userSession;
  private long _afterTeleportPortectionTime;

  public int buffAnimRange() {
    return this._buffAnimRange;
  }

  public void setBuffAnimRange(int value) {
    this._buffAnimRange = value;
  }

  public Player(int objectId, PlayerTemplate template, String accountName) {
    super(objectId, template);
    this._classlist = new HashMap(4);
    this._isUndying = false;
    this._disconnectedTitle = Config.DISCONNECTED_PLAYER_TITLE;
    this._disconnectedTitleColor = Config.DISCONNECTED_PLAYER_TITLE_COLOR;
    this._fakeDeath = 0;
    this._autoLoot = Config.AUTO_LOOT;
    this.AutoLootHerbs = Config.AUTO_LOOT_HERBS;
    this.AutoLootAdena = Config.AUTO_LOOT_ADENA;
    this._inventory = new PcInventory(this);
    this._warehouse = new PcWarehouse(this);
    this._refund = new PcRefund(this);
    this._freight = new PcFreight(this);
    this._lastNpcInteractionTime = 0L;
    this._msgBucket = new LinkedList();
    this._recipebook = new TreeMap<>();
    this._commonrecipebook = new TreeMap<>();
    this._premiumItems = new TreeMap<>();
    this._quests = new HashMap<>();
    this._shortCuts = new ShortCutList(this);
    this._macroses = new MacroList(this);
    this._privatestore = new AtomicInteger(0);
    this._createList = Collections.emptyList();
    this._sellList = Collections.emptyList();
    this._packageSellList = Collections.emptyList();
    this._buyList = Collections.emptyList();
    this._tradeList = Collections.emptyList();
    this._henna = new Henna[3];
    this._pledgeClass = 0;
    this._pledgeType = -128;
    this._powerGrade = 0;
    this._lvlJoinedAcademy = 0;
    this._apprentice = 0;
    this._playerAccess = new PlayerAccess();
    this._messageRefusal = false;
    this._tradeRefusal = false;
    this._blockAll = false;
    this._summon = null;
    this._cubics = null;
    this._agathionId = 0;
    this._chars = new HashMap(8);
    this.expertiseIndex = 0;
    this._enchantScroll = null;
    this._refineryHandler = null;
    this._isOnline = false;
    this._isLogout = new AtomicBoolean();
    this._lastNpc = HardReferences.emptyRef();
    this._multisell = null;
    this._activeSoulShots = new CopyOnWriteArraySet();
    this._observerMode = new AtomicInteger(0);
    this._telemode = 0;
    this.entering = true;
    this._stablePoint = null;
    this._loto = new int[5];
    this._race = new int[2];
    this._blockList = new ConcurrentSkipListMap();
    this._friendList = new FriendList(this);
    this._hero = false;
    this._baseClass = -1;
    this._activeClass = null;
    this._bonus = new Bonus();
    this._noble = false;
    this._varka = 0;
    this._ketra = 0;
    this._ram = 0;
    this._keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;
    this._cursedWeaponEquippedId = 0;
    this._fishing = new Fishing(this);
    this._storeLock = new ReentrantLock();
    this._offline = false;
    this._transformationSkills = new HashMap<>();
    this._expandInventory = 0;
    this._expandWarehouse = 0;
    this._buffAnimRange = 1500;
    this._invisibleType = InvisibleType.NONE;
    this._postFriends = Containers.emptyIntObjectMap();
    this._blockedActions = new ArrayList<>();
    this._notShowTraders = false;
    this._debug = false;
    this._sharedGroupReuses = new CHashIntObjectMap();
    this._askDialog = null;
    this._instancesReuses = new ConcurrentHashMap();
    this._receivedRec = 0;
    this._givableRec = 0;
    this._recommendedCharIds = new HashIntSet();
    this._mtlOffloadData = new AtomicReference(null);
    this._vars = new MultiValueSet();
    this._resurect_prohibited = false;
    this._maried = false;
    this._partnerId = 0;
    this._coupleId = 0;
    this._maryrequest = false;
    this._maryaccepted = false;
    this._charmOfCourage = false;
    this._increasedForce = 0;
    this._increasedForceLastUpdateTimeStamp = 0L;
    this._increasedForceCleanupTask = null;
    this._consumedSouls = 0;
    this._useSeed = 0;
    this._lastAttackPacket = 0L;
    this._incorrectValidateCount = 0;
    this._movieId = 0;
    this._petControlItem = null;
    this.isActive = new AtomicBoolean();
    this._hoursInGame = 0;
    this._afterTeleportPortectionTime = 0L;
    this._login = accountName;
    this._nameColor = 16777215;
    this._titlecolor = 16777079;
    this._baseClass = this.getClassId().getId();
  }

  private Player(int objectId, PlayerTemplate template) {
    this(objectId, template, null);
    this._ai = new PlayerAI(this);
    if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS) {
      this.setPlayerAccess(Config.gmlist.get(objectId));
    } else {
      this.setPlayerAccess(Config.gmlist.get(0));
    }

  }

  public HardReference<Player> getRef() {
    return (HardReference<Player>) super.getRef();
  }

  public String getAccountName() {
    return this._connection == null ? this._login : this._connection.getLogin();
  }

  public String getIP() {
    return this._connection == null ? "<not connected>" : this._connection.getIpAddr();
  }

  public Map<Integer, String> getAccountChars() {
    return this._chars;
  }

  public final PlayerTemplate getTemplate() {
    return (PlayerTemplate)this._template;
  }

  public PlayerTemplate getBaseTemplate() {
    return (PlayerTemplate)this._baseTemplate;
  }

  public void changeSex() {
    boolean male = true;
    if (this.getSex() == 1) {
      male = false;
    }

    this._template = CharTemplateTable.getInstance().getTemplate(this.getClassId(), !male);
  }

  public PlayerAI getAI() {
    return (PlayerAI)this._ai;
  }

  public void doCast(Skill skill, Creature target, boolean forceUse) {
    if (skill != null) {
      super.doCast(skill, target, forceUse);
      if (Config.ALT_TELEPORT_PROTECTION && this.getAfterTeleportPortectionTime() > System.currentTimeMillis()) {
        this.setAfterTeleportPortectionTime(0L);
        this.sendMessage(new CustomMessage("alt.teleport_protect_gonna", this));
      }

    }
  }

  public void altUseSkill(Skill skill, Creature target) {
    super.altUseSkill(skill, target);
    if (Config.ALT_TELEPORT_PROTECTION && this.isPlayer()) {
      if (this.getPlayer().getAfterTeleportPortectionTime() > System.currentTimeMillis()) {
        this.getPlayer().setAfterTeleportPortectionTime(0L);
        this.getPlayer().sendMessage(new CustomMessage("alt.teleport_protect_gonna", this.getPlayer()));
      }
    }

  }

  public void sendReuseMessage(Skill skill) {
    if (!this.isCastingNow()) {
      TimeStamp sts = this.getSkillReuse(skill);
      if (sts != null && sts.hasNotPassed()) {
        long timeleft = sts.getReuseCurrent();
        if ((Config.ALT_SHOW_REUSE_MSG || timeleft >= 10000L) && timeleft >= 500L) {
          this.sendPacket((new SystemMessage(48)).addSkillName(skill.getDisplayId(), skill.getDisplayLevel()));
        }
      }
    }
  }

  public final int getLevel() {
    return this._activeClass == null ? 1 : this._activeClass.getLevel();
  }

  public int getSex() {
    return this.getTemplate().isMale ? 0 : 1;
  }

  public int getFace() {
    return this._face;
  }

  public void setFace(int face) {
    this._face = face;
  }

  public int getHairColor() {
    return this._hairColor;
  }

  public void setHairColor(int hairColor) {
    this._hairColor = hairColor;
  }

  public int getHairStyle() {
    return this._hairStyle;
  }

  public void setHairStyle(int hairStyle) {
    this._hairStyle = hairStyle;
  }

  public void offline() {
    if (this._connection != null) {
      this._connection.setActiveChar(null);
      this._connection.close(ServerClose.STATIC);
      this.setNetConnection(null);
    }

    if (Config.SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE) {
      this.setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
    }

    if (Config.SERVICES_OFFLINE_TRADE_ABNORMAL != AbnormalEffect.NULL) {
      this.startAbnormalEffect(Config.SERVICES_OFFLINE_TRADE_ABNORMAL);
    }

    this.setOfflineMode(true);
    this.setVar("offline", String.valueOf(System.currentTimeMillis() / 1000L), -1L);
    if (Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0L) {
      this.startKickTask(Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK * 1000L);
    }

    Party party = this.getParty();
    if (party != null) {
      if (this.isFestivalParticipant()) {
        party.broadcastMessageToPartyMembers(this.getName() + " has been removed from the upcoming festival.");
      }

      this.leaveParty();
    }

    if (this.getPet() != null) {
      this.getPet().unSummon();
    }

    CursedWeaponsManager.getInstance().doLogout(this);
    if (this.isOlyParticipant()) {
      this.getOlyParticipant().OnDisconnect(this);
    }

    this.broadcastCharInfo();
    this.stopWaterTask();
    this.stopBonusTask();
    this.stopHourlyTask();
    this.stopPcBangPointsTask();
    this.stopAutoSaveTask();
    this.stopQuestTimers();

    try {
      this.getInventory().store();
    } catch (Throwable var4) {
      log.error("", var4);
    }

    try {
      this.store(false);
    } catch (Throwable var3) {
      log.error("", var3);
    }

  }

  public void kick() {
    if (this._connection != null) {
      this._connection.close(LeaveWorld.STATIC);
      this.setNetConnection(null);
    }

    this.prepareToLogout();
    this.deleteMe();
  }

  public void restart() {
    if (this._connection != null) {
      this._connection.setActiveChar(null);
      this.setNetConnection(null);
    }

    this.prepareToLogout();
    this.deleteMe();
  }

  public void logout() {
    if (this._connection != null) {
      this._connection.close(ServerClose.STATIC);
      this.setNetConnection(null);
    }

    this.prepareToLogout();
    this.deleteMe();
  }

  private void prepareToLogout() {
    if (!this._isLogout.getAndSet(true)) {
      this.setNetConnection(null);
      this.setIsOnline(false);
      this.getListeners().onExit();
      if (this.isFlying() && !this.checkLandingState()) {
        this._stablePoint = TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE);
      }

      if (this.isCastingNow()) {
        this.abortCast(true, true);
      }

      Party party = this.getParty();
      if (party != null) {
        if (this.isFestivalParticipant()) {
          party.broadcastMessageToPartyMembers(this.getName() + " has been removed from the upcoming festival.");
        }

        this.leaveParty();
      }

      if (Config.OLY_ENABLED && OlyController.getInstance().isCompetitionsActive()) {
        if (this.isOlyParticipant()) {
          this.getOlyParticipant().OnDisconnect(this);
        }

        if (ParticipantPool.getInstance().isRegistred(this)) {
          ParticipantPool.getInstance().onLogout(this);
        }
      }

      CursedWeaponsManager.getInstance().doLogout(this);
      if (this.isOlyObserver()) {
        this.leaveOlympiadObserverMode();
      }

      if (this.isInObserverMode()) {
        this.leaveObserverMode();
      }

      this.stopFishing();
      if (this._stablePoint != null) {
        this.teleToLocation(this._stablePoint);
      }

      Summon pet = this.getPet();
      if (pet != null) {
        pet.saveEffects();
        pet.unSummon();
      }

      this._friendList.notifyFriends(false);
      if (this.isProcessingRequest()) {
        this.getRequest().cancel();
      }

      this.stopAllTimers();
      if (this.isInBoat()) {
        this.getBoat().removePlayer(this);
      }

      SubUnit unit = this.getSubUnit();
      UnitMember member = unit == null ? null : unit.getUnitMember(this.getObjectId());
      if (member != null) {
        int sponsor = member.getSponsor();
        int apprentice = this.getApprentice();
        PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(this);

        for (Player clanMember : this._clan.getOnlineMembers(this.getObjectId())) {
          clanMember.sendPacket(memberUpdate);
          if (clanMember.getObjectId() == sponsor) {
            clanMember.sendPacket((new SystemMessage(1757)).addString(this._name));
          } else if (clanMember.getObjectId() == apprentice) {
            clanMember.sendPacket((new SystemMessage(1759)).addString(this._name));
          }
        }

        member.setPlayerInstance(this, true);
      }

      FlagItemAttachment attachment = this.getActiveWeaponFlagAttachment();
      if (attachment != null) {
        attachment.onLogout(this);
      }

      if (CursedWeaponsManager.getInstance().getCursedWeapon(this.getCursedWeaponEquippedId()) != null) {
        CursedWeaponsManager.getInstance().getCursedWeapon(this.getCursedWeaponEquippedId()).setPlayer(null);
      }

      MatchingRoom room = this.getMatchingRoom();
      if (room != null) {
        if (room.getLeader() == this) {
          room.disband();
        } else {
          room.removeMember(this, false);
        }
      }

      this.setMatchingRoom(null);
      MatchingRoomManager.getInstance().removeFromWaitingList(this);
      this.destroyAllTraps();
      this.stopPvPFlag();
      Reflection ref = this.getReflection();
      if (ref != ReflectionManager.DEFAULT) {
        if (ref.getReturnLoc() != null) {
          this._stablePoint = ref.getReturnLoc();
        }

        ref.removeObject(this);
      }

      try {
        this.getInventory().store();
        this.getRefund().clear();
      } catch (Throwable var11) {
        log.error("", var11);
      }

      try {
        this.store(false);
      } catch (Throwable var10) {
        log.error("", var10);
      }

    }
  }

  public Collection<Recipe> getDwarvenRecipeBook() {
    return this._recipebook.values();
  }

  public Collection<Recipe> getCommonRecipeBook() {
    return this._commonrecipebook.values();
  }

  public int recipesCount() {
    return this._commonrecipebook.size() + this._recipebook.size();
  }

  public boolean hasRecipe(Recipe id) {
    return this._recipebook.containsValue(id) || this._commonrecipebook.containsValue(id);
  }

  public boolean findRecipe(int id) {
    return this._recipebook.containsKey(id) || this._commonrecipebook.containsKey(id);
  }

  public void registerRecipe(Recipe recipe, boolean saveDB) {
    if (recipe != null) {
      switch(recipe.getType()) {
        case ERT_COMMON:
          this._commonrecipebook.put(recipe.getId(), recipe);
          break;
        case ERT_DWARF:
          this._recipebook.put(recipe.getId(), recipe);
          break;
        default:
          return;
      }

      if (saveDB) {
        mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", this.getObjectId(), recipe.getId());
      }

    }
  }

  public void unregisterRecipe(int RecipeID) {
    if (this._recipebook.containsKey(RecipeID)) {
      mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", this.getObjectId(), RecipeID);
      this._recipebook.remove(RecipeID);
    } else if (this._commonrecipebook.containsKey(RecipeID)) {
      mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", this.getObjectId(), RecipeID);
      this._commonrecipebook.remove(RecipeID);
    } else {
      log.warn("Attempted to remove unknown RecipeList" + RecipeID);
    }

  }

  public QuestState getQuestState(Quest quest) {
    return this.getQuestState(quest.getName());
  }

  public QuestState getQuestState(String quest) {
    this.questRead.lock();

    QuestState var2;
    try {
      var2 = this._quests.get(quest);
    } finally {
      this.questRead.unlock();
    }

    return var2;
  }

  public QuestState getQuestState(Class<?> quest) {
    return this.getQuestState(quest.getSimpleName());
  }

  public boolean isQuestCompleted(String quest) {
    QuestState q = this.getQuestState(quest);
    return q != null && q.isCompleted();
  }

  public boolean isQuestCompleted(Class<?> quest) {
    QuestState q = this.getQuestState(quest);
    return q != null && q.isCompleted();
  }

  public void setQuestState(QuestState qs) {
    this.questWrite.lock();

    try {
      this._quests.put(qs.getQuest().getName(), qs);
    } finally {
      this.questWrite.unlock();
    }

  }

  public void removeQuestState(String quest) {
    this.questWrite.lock();

    try {
      this._quests.remove(quest);
    } finally {
      this.questWrite.unlock();
    }

  }

  public Quest[] getAllActiveQuests() {
    List<Quest> quests = new ArrayList(this._quests.size());
    this.questRead.lock();

    try {

      for (QuestState qs : this._quests.values()) {
        if (qs.isStarted()) {
          quests.add(qs.getQuest());
        }
      }
    } finally {
      this.questRead.unlock();
    }

    return quests.toArray(new Quest[0]);
  }

  public QuestState[] getAllQuestsStates() {
    this.questRead.lock();

    QuestState[] var1;
    try {
      var1 = this._quests.values().toArray(new QuestState[this._quests.size()]);
    } finally {
      this.questRead.unlock();
    }

    return var1;
  }

  public List<QuestState> getQuestsForEvent(NpcInstance npc, QuestEventType event) {
    List<QuestState> states = new ArrayList<>();
    Quest[] quests = npc.getTemplate().getEventQuests(event);
    if (quests != null) {
      int var7 = quests.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        Quest quest = quests[var8];
        QuestState qs = this.getQuestState(quest.getName());
        if (qs != null && !qs.isCompleted()) {
          states.add(this.getQuestState(quest.getName()));
        }
      }
    }

    return states;
  }

  public void processQuestEvent(String quest, String event, NpcInstance npc) {
    if (event == null) {
      event = "";
    }

    QuestState qs = this.getQuestState(quest);
    if (qs == null) {
      Quest q = QuestManager.getQuest(quest);
      if (q == null) {
        log.warn("Quest " + quest + " not found!");
        return;
      }

      qs = q.newQuestState(this, 1);
    }

    if (qs != null && !qs.isCompleted()) {
      qs.getQuest().notifyEvent(event, qs, npc);
      this.sendPacket(new QuestList(this));
    }
  }

  public boolean isQuestContinuationPossible(boolean msg) {
    if (this.getWeightPenalty() < 3 && (double)this.getInventoryLimit() * 0.8D >= (double)this.getInventory().getSize()) {
      return true;
    } else {
      if (msg) {
        this.sendPacket(Msg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_VOLUME_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
      }

      return false;
    }
  }

  public void stopQuestTimers() {
    QuestState[] var1 = this.getAllQuestsStates();
    int var2 = var1.length;

    for (QuestState qs : var1) {
      if (qs.isStarted()) {
        qs.pauseQuestTimers();
      } else {
        qs.stopQuestTimers();
      }
    }

  }

  public void resumeQuestTimers() {
    QuestState[] var1 = this.getAllQuestsStates();
    int var2 = var1.length;

    for (QuestState qs : var1) {
      qs.resumeQuestTimers();
    }

  }

  public Collection<ShortCut> getAllShortCuts() {
    return this._shortCuts.getAllShortCuts();
  }

  public ShortCut getShortCut(int slot, int page) {
    return this._shortCuts.getShortCut(slot, page);
  }

  public void registerShortCut(ShortCut shortcut) {
    this._shortCuts.registerShortCut(shortcut);
  }

  public void deleteShortCut(int slot, int page) {
    this._shortCuts.deleteShortCut(slot, page);
  }

  public void registerMacro(Macro macro) {
    this._macroses.registerMacro(macro);
  }

  public void deleteMacro(int id) {
    this._macroses.deleteMacro(id);
  }

  public MacroList getMacroses() {
    return this._macroses;
  }

  public boolean isCastleLord(int castleId) {
    return this._clan != null && this.isClanLeader() && this._clan.getCastle() == castleId;
  }

  public int getPkKills() {
    return this._pkKills;
  }

  public void setPkKills(int pkKills) {
    this._pkKills = pkKills;
  }

  public long getCreateTime() {
    return this._createTime;
  }

  public void setCreateTime(long createTime) {
    this._createTime = createTime;
  }

  public int getDeleteTimer() {
    return this._deleteTimer;
  }

  public void setDeleteTimer(int deleteTimer) {
    this._deleteTimer = deleteTimer;
  }

  public int getCurrentLoad() {
    return this.getInventory().getTotalWeight();
  }

  public long getLastAccess() {
    return this._lastAccess;
  }

  public void setLastAccess(long value) {
    this._lastAccess = value;
  }

  public boolean isRecommended(Player target) {
    return this._recommendedCharIds.contains(target.getObjectId());
  }

  public void setReceivedRec(int value) {
    this._receivedRec = value;
  }

  public int getReceivedRec() {
    return this._receivedRec;
  }

  public void setGivableRec(int value) {
    this._givableRec = value;
  }

  public int getGivableRec() {
    return this._givableRec;
  }

  public void updateRecommends() {
    this._recommendedCharIds.clear();
    if (this.getLevel() >= 40) {
      this._givableRec = 9;
      this._receivedRec = Math.max(0, this._receivedRec - 4);
    } else if (this.getLevel() >= 20) {
      this._givableRec = 6;
      this._receivedRec = Math.max(0, this._receivedRec - 2);
    } else if (this.getLevel() >= 10) {
      this._givableRec = 3;
      this._receivedRec = Math.max(0, this._receivedRec - 1);
    } else {
      this._givableRec = 0;
      this._receivedRec = 0;
    }

  }

  public void restoreGivableAndReceivedRec(int givableRecs, int receivedRecs) {
    this._givableRec = givableRecs;
    this._receivedRec = receivedRecs;
    Calendar temp = Calendar.getInstance();
    temp.set(11, Config.REC_FLUSH_HOUR);
    temp.set(12, Config.REC_FLUSH_MINUTE);
    temp.set(13, 0);
    temp.set(14, 0);
    long daysElapsed = Math.round((float)((System.currentTimeMillis() / 1000L - this.getLastAccess()) / 86400L));
    if (daysElapsed == 0L && this.getLastAccess() < temp.getTimeInMillis() / 1000L && System.currentTimeMillis() > temp.getTimeInMillis()) {
      ++daysElapsed;
    }

    for(int i = 1; (long)i < daysElapsed; ++i) {
      this.updateRecommends();
    }

  }

  public void giveRecommendation(Player target) {
    if (target != null) {
      if (this.getGivableRec() > 0 && target.getReceivedRec() < 255) {
        if (!this._recommendedCharIds.contains(target.getObjectId())) {
          this._recommendedCharIds.add(target.getObjectId());
          this.setGivableRec(this.getGivableRec() - 1);
          this.sendUserInfo();
          target.setReceivedRec(target.getReceivedRec() + 1);
          target.broadcastUserInfo(true);
        }
      }
    }
  }

  private void restoreRecommendedCharacters() {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      pstmt = conn.prepareStatement("SELECT `targetId` AS `recommendedObjId` FROM `character_recommends` WHERE `objId` = ?");
      pstmt.setInt(1, this.getObjectId());
      rset = pstmt.executeQuery();
      this._recommendedCharIds.clear();

      while(rset.next()) {
        int recommendedCharId = rset.getInt("recommendedObjId");
        this._recommendedCharIds.add(recommendedCharId);
      }
    } catch (SQLException var8) {
      log.error("Can't load recommended characters", var8);
    } finally {
      DbUtils.closeQuietly(conn, pstmt, rset);
    }

  }

  private void storeRecommendedCharacters() {
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      pstmt = conn.prepareStatement("DELETE FROM `character_recommends` WHERE `objId` = ?");
      pstmt.setInt(1, this.getObjectId());
      pstmt.executeUpdate();
      DbUtils.close(pstmt);
      if (!this._recommendedCharIds.isEmpty()) {
        pstmt = conn.prepareStatement("INSERT INTO `character_recommends` (`objId`, `targetId`) VALUES (?, ?)");
        IntIterator $it = this._recommendedCharIds.iterator();

        while($it.hasNext()) {
          pstmt.setInt(1, this.getObjectId());
          pstmt.setInt(2, $it.next());
          pstmt.executeUpdate();
        }
      }
    } catch (SQLException var7) {
      log.error("Can't store recommended characters", var7);
    } finally {
      DbUtils.closeQuietly(conn, pstmt);
    }

  }

  public int getKarma() {
    return this._karma;
  }

  public void setKarma(int karma) {
    if (karma < 0) {
      karma = 0;
    }

    if (this._karma != karma) {
      this._karma = karma;
      this.sendChanges();
      if (this.getPet() != null) {
        this.getPet().broadcastCharInfo();
      }

    }
  }

  public int getMaxLoad() {
    int con = this.getCON();
    if (con < 1) {
      return (int)(31000.0D * Config.MAXLOAD_MODIFIER);
    } else {
      return con > 59 ? (int)(176000.0D * Config.MAXLOAD_MODIFIER) : (int)this.calcStat(Stats.MAX_LOAD, Math.pow(1.029993928D, con) * 30495.627366D * Config.MAXLOAD_MODIFIER, this, null);
    }
  }

  public void updateEffectIcons() {
    if (!this.entering && !this.isLogoutStarted()) {
      if (Config.USER_INFO_INTERVAL == 0L) {
        if (this._updateEffectIconsTask != null) {
          this._updateEffectIconsTask.cancel(false);
          this._updateEffectIconsTask = null;
        }

        this.updateEffectIconsImpl();
      } else if (this._updateEffectIconsTask == null) {
        this._updateEffectIconsTask = ThreadPoolManager.getInstance().schedule(new Player.UpdateEffectIcons(), Config.USER_INFO_INTERVAL);
      }
    }
  }

  private void updateEffectIconsImpl() {
    Effect[] effects = this.getEffectList().getAllFirstEffects();
    PartySpelled ps = new PartySpelled(this, false);
    AbnormalStatusUpdate mi = new AbnormalStatusUpdate();
    EEffectSlot[] var4 = EEffectSlot.VALUES;
    int var5 = var4.length;

    for (EEffectSlot ees : var4) {
      int var9 = effects.length;

      for (int var10 = 0; var10 < var9; ++var10) {
        Effect eff = effects[var10];
        if (eff.isInUse() && eff.getEffectSlot() == ees) {
          if (eff.isStackTypeMatch("HpRecoverCast")) {
            this.sendPacket(new ShortBuffStatusUpdate(eff));
          } else {
            eff.addIcon(mi);
          }

          if (this._party != null) {
            eff.addPartySpelledIcon(ps);
          }
        }
      }
    }

    this.sendPacket(mi);
    if (this._party != null) {
      this._party.broadCast(ps);
    }

    if (this.isOlyParticipant()) {
      this.getOlyParticipant().getCompetition().broadcastEffectIcons(this, effects);
    }

  }

  public int getWeightPenalty() {
    return this.getSkillLevel(4270, 0);
  }

  public void refreshOverloaded() {
    if (!this.isLogoutStarted() && this.getMaxLoad() > 0) {
      this.setOverloaded(this.getCurrentLoad() > this.getMaxLoad());
      double weightproc = 100.0D * ((double)this.getCurrentLoad() - this.calcStat(Stats.MAX_NO_PENALTY_LOAD, 0.0D, this, null)) / (double)this.getMaxLoad();
//      int newWeightPenalty = false;
      byte newWeightPenalty;
      if (weightproc < 50.0D) {
        newWeightPenalty = 0;
      } else if (weightproc < 66.6D) {
        newWeightPenalty = 1;
      } else if (weightproc < 80.0D) {
        newWeightPenalty = 2;
      } else if (weightproc < 100.0D) {
        newWeightPenalty = 3;
      } else {
        newWeightPenalty = 4;
      }

      int current = this.getWeightPenalty();
      if (current != newWeightPenalty) {
        if (newWeightPenalty > 0) {
          super.addSkill(SkillTable.getInstance().getInfo(4270, newWeightPenalty));
        } else {
          super.removeSkill(this.getKnownSkill(4270));
        }

        this.sendPacket(new SkillList(this));
        this.sendEtcStatusUpdate();
        this.updateStats();
      }
    }
  }

  public int getGradePenalty() {
    return this.getSkillLevel(4267, 0);
  }

  public int getExpertisePenalty(ItemInstance item) {
    return item.getTemplate().getType2() != 0 && item.getTemplate().getType2() != 1 && item.getTemplate().getType2() != 2 ? 0 : this.getGradePenalty();
  }

  public void refreshExpertisePenalty() {
    if (!this.isLogoutStarted()) {
      int level = (int)this.calcStat(Stats.GRADE_EXPERTISE_LEVEL, this.getLevel(), null, null);
//      int i = false;

      int i;
      for(i = 0; i < EXPERTISE_LEVELS.length && level >= EXPERTISE_LEVELS[i + 1]; ++i) {
      }

      boolean skillUpdate = false;
      if (this.expertiseIndex != i) {
        this.expertiseIndex = i;
        if (this.expertiseIndex > 0) {
          this.addSkill(SkillTable.getInstance().getInfo(239, this.expertiseIndex), false);
          skillUpdate = true;
        }
      }

      int newGradePenalty = 0;
      ItemInstance[] items = this.getInventory().getPaperdollItems();
      int var7 = items.length;

      for (ItemInstance item : items) {
        if (item != null) {
          int crystaltype = item.getTemplate().getCrystalType().ordinal();
          if ((item.getTemplate().getType2() == 0 || item.getTemplate().getType2() == 1 || item.getTemplate().getType2() == 2) && crystaltype > newGradePenalty) {
            newGradePenalty = crystaltype;
          }
        }
      }

      newGradePenalty -= this.expertiseIndex;
      if (newGradePenalty <= 0) {
        newGradePenalty = 0;
      } else if (newGradePenalty >= 4) {
        newGradePenalty = 4;
      }

      int PenaltyExpertise = this.getGradePenalty();
      if (PenaltyExpertise != newGradePenalty) {
        if (newGradePenalty > 0) {
          super.addSkill(SkillTable.getInstance().getInfo(4267, newGradePenalty));
        } else {
          super.removeSkill(this.getKnownSkill(4267));
        }

        skillUpdate = true;
      }

      if (skillUpdate) {
        this.getInventory().validateItemsSkills();
        this.sendPacket(new SkillList(this));
        this.sendEtcStatusUpdate();
        this.updateStats();
      }

    }
  }

  public int getPvpKills() {
    return this._pvpKills;
  }

  public void setPvpKills(int pvpKills) {
    this._pvpKills = pvpKills;
  }

  public ClassId getClassId() {
    return this.getTemplate().classId;
  }

  public void addClanPointsOnProfession(int id) {
    if (this.getLvlJoinedAcademy() != 0 && this._clan != null && this._clan.getLevel() >= 5 && ClassId.VALUES[id].getLevel() == 2) {
      this._clan.incReputation(100, true, "Academy");
    } else if (this.getLvlJoinedAcademy() != 0 && this._clan != null && this._clan.getLevel() >= 5 && ClassId.VALUES[id].getLevel() == 3) {
//      int earnedPoints = false;
      int earnedPoints;
      if (this.getLvlJoinedAcademy() > 39) {
        earnedPoints = 160;
      } else if (this.getLvlJoinedAcademy() > 16) {
        earnedPoints = 400 - (this.getLvlJoinedAcademy() - 16) * 10;
      } else {
        earnedPoints = 400;
      }

      this._clan.removeClanMember(this.getObjectId());
      SystemMessage sm = new SystemMessage(1748);
      sm.addString(this.getName());
      sm.addNumber(this._clan.incReputation(earnedPoints, true, "Academy"));
      this._clan.broadcastToOnlineMembers(sm);
      this._clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListDelete(this.getName()), this);
      this.setClan(null);
      this.setTitle("");
      this.sendPacket(Msg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);
      this.setLeaveClanTime(0L);
      this.broadcastCharInfo();
      this.sendPacket(PledgeShowMemberListDeleteAll.STATIC);
      ItemFunctions.addItem(this, 8181, 1L, true);
    }

  }

  public synchronized void setClassId(int id, boolean noban, boolean fromQuest) {
    if (!noban && !ClassId.VALUES[id].equalsOrChildOf(ClassId.VALUES[this.getActiveClassId()]) && !this.getPlayerAccess().CanChangeClass && !Config.EVERYBODY_HAS_ADMIN_RIGHTS) {
      Thread.dumpStack();
    } else {
      boolean isNewSub = !this.getSubClasses().containsKey(id);
      if (isNewSub) {
        SubClass cclass = this.getActiveClass();
        this.getSubClasses().remove(this.getActiveClassId());
        this.changeClassInDb(cclass.getClassId(), id);
        if (cclass.isBase()) {
          this.setBaseClass(id);
          this.addClanPointsOnProfession(id);
          ItemInstance coupons = null;
          if (ClassId.VALUES[id].getLevel() == 2) {
            if (fromQuest && Config.ALT_ALLOW_SHADOW_WEAPONS) {
              coupons = ItemFunctions.createItem(8869);
            }

            this.unsetVar("newbieweapon");
            this.unsetVar("p1q2");
            this.unsetVar("p1q3");
            this.unsetVar("p1q4");
            this.unsetVar("prof1");
            this.unsetVar("ng1");
            this.unsetVar("ng2");
            this.unsetVar("ng3");
            this.unsetVar("ng4");
          } else if (ClassId.VALUES[id].getLevel() == 3) {
            if (fromQuest && Config.ALT_ALLOW_SHADOW_WEAPONS) {
              coupons = ItemFunctions.createItem(8870);
            }

            this.unsetVar("newbiearmor");
            this.unsetVar("dd1");
            this.unsetVar("dd2");
            this.unsetVar("dd3");
            this.unsetVar("prof2.1");
            this.unsetVar("prof2.2");
            this.unsetVar("prof2.3");
          }

          if (coupons != null) {
            coupons.setCount(15L);
            this.sendPacket(SystemMessage2.obtainItems(coupons));
            this.getInventory().addItem(coupons);
          }
        }

        cclass.setClassId(id);
        this.getSubClasses().put(id, cclass);
        this.rewardSkills(true);
        this.storeCharSubClasses();
        if (fromQuest) {
          this.broadcastPacket(new SocialAction(this.getObjectId(), 16));
          this.broadcastPacket(new SocialAction(this.getObjectId(), 3));
          this.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
        }

        this.broadcastCharInfo();
      }

      PlayerTemplate t = CharTemplateTable.getInstance().getTemplate(id, this.getSex() == 1);
      if (t == null) {
        log.error("Missing template for classId: " + id);
      } else {
        this._template = t;
        if (this.isInParty()) {
          this.getParty().broadCast(new PartySmallWindowUpdate(this));
        }

        if (this.getClan() != null) {
          this.getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
        }

        if (this._matchingRoom != null) {
          this._matchingRoom.broadcastPlayerUpdate(this);
        }

        this.sendPacket(new SkillList(this));
      }
    }
  }

  public long getExp() {
    return this._activeClass == null ? 0L : this._activeClass.getExp();
  }

  public long getMaxExp() {
    return this._activeClass == null ? Experience.LEVEL[Experience.getMaxLevel() + 1] : this._activeClass.getMaxExp();
  }

  public void setEnchantScroll(ItemInstance scroll) {
    this._enchantScroll = scroll;
  }

  public ItemInstance getEnchantScroll() {
    return this._enchantScroll;
  }

  public IRefineryHandler getRefineryHandler() {
    return this._refineryHandler;
  }

  public void setRefineryHandler(IRefineryHandler refineryHandler) {
    this._refineryHandler = refineryHandler;
  }

  public void setFistsWeaponItem(WeaponTemplate weaponItem) {
    this._fistsWeaponItem = weaponItem;
  }

  public WeaponTemplate getFistsWeaponItem() {
    return this._fistsWeaponItem;
  }

  public WeaponTemplate findFistsWeaponItem(int classId) {
    if (classId >= 0 && classId <= 9) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(246);
    } else if (classId >= 10 && classId <= 17) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(251);
    } else if (classId >= 18 && classId <= 24) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(244);
    } else if (classId >= 25 && classId <= 30) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(249);
    } else if (classId >= 31 && classId <= 37) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(245);
    } else if (classId >= 38 && classId <= 43) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(250);
    } else if (classId >= 44 && classId <= 48) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(248);
    } else if (classId >= 49 && classId <= 52) {
      return (WeaponTemplate)ItemHolder.getInstance().getTemplate(252);
    } else {
      return classId >= 53 && classId <= 57 ? (WeaponTemplate)ItemHolder.getInstance().getTemplate(247) : null;
    }
  }

  public void addExpAndCheckBonus(MonsterInstance mob, double noRateExp, double noRateSp) {
    if (this._activeClass != null) {
      double neededExp = this.calcStat(Stats.SOULS_CONSUME_EXP, 0.0D, mob, null);
      if (neededExp > 0.0D && noRateExp > neededExp) {
        mob.broadcastPacket(new SpawnEmitter(mob, this));
        ThreadPoolManager.getInstance().schedule(new SoulConsumeTask(this), 1000L);
      }

      double expRate = Config.RATE_XP;
      double spRate = Config.RATE_SP;
      if (mob.isRaid()) {
        expRate = Config.RATE_RAIDBOSS_XP;
        spRate = Config.RATE_RAIDBOSS_SP;
      }

      long normalExp = (long)(noRateExp * expRate * this.getRateExp());
      long normalSp = (long)(noRateSp * spRate * this.getRateSp());
      this.addExpAndSp(normalExp, normalSp, false, true);
    }
  }

  public void addExpAndSp(long exp, long sp) {
    this.addExpAndSp(exp, sp, false, false);
  }

  public void addExpAndSp(long addToExp, long addToSp, boolean applyRate, boolean applyToPet) {
    if (this._activeClass != null) {
      if (applyRate) {
        addToExp = (long)((double)addToExp * Config.RATE_XP * this.getRateExp());
        addToSp = (long)((double)addToSp * Config.RATE_SP * this.getRateSp());
      }

      Summon pet = this.getPet();
      boolean updatePetInfo = false;
      if (addToExp > 0L) {
        if (!this.isCursedWeaponEquipped() && addToSp > 0L && this._karma > 0) {
          this._karma = (int)((double)this._karma - (double)addToSp / ((double)Config.KARMA_SP_DIVIDER * Config.RATE_SP));
          updatePetInfo = true;
        }

        if (this._karma < 0) {
          this._karma = 0;
        }

        if (applyToPet && pet != null && !pet.isDead()) {
          if (pet.getNpcId() == 12564) {
            pet.addExpAndSp(addToExp, 0L);
            addToExp = 0L;
          } else if (pet.isPet() && pet.getExpPenalty() > 0.0D) {
            if (pet.getLevel() > this.getLevel() - 20 && pet.getLevel() < this.getLevel() + 5) {
              pet.addExpAndSp((long)((double)addToExp * pet.getExpPenalty()), 0L);
              addToExp = (long)((double)addToExp * (1.0D - pet.getExpPenalty()));
            } else {
              pet.addExpAndSp((long)((double)addToExp * pet.getExpPenalty() / 5.0D), 0L);
              addToExp = (long)((double)addToExp * (1.0D - pet.getExpPenalty() / 5.0D));
            }
          } else if (pet.isSummon()) {
            addToExp = (long)((double)addToExp * (1.0D - pet.getExpPenalty()));
          }
        }

        long max_xp = this.getVarB("NoExp") ? Experience.LEVEL[this.getLevel() + 1] - 1L : this.getMaxExp();
        addToExp = Math.min(addToExp, max_xp - this.getExp());
      }

      int oldLvl = this._activeClass.getLevel();
      this._activeClass.addExp(addToExp);
      this._activeClass.addSp(addToSp);
      if (addToSp > 0L && addToExp == 0L) {
        this.sendPacket((new SystemMessage(331)).addNumber(addToSp));
      } else if (addToSp > 0L && addToExp > 0L) {
        this.sendPacket((new SystemMessage(95)).addNumber(addToExp).addNumber(addToSp));
      } else if (addToSp == 0L && addToExp > 0L) {
        this.sendPacket((new SystemMessage(45)).addNumber(addToExp));
      }

      int level = this._activeClass.getLevel();
      if (level != oldLvl) {
        int levels = level - oldLvl;
        this.levelSet(levels);
      }

      this.updateStats();
      if (pet != null && updatePetInfo) {
        pet.broadcastCharInfo();
      }

      this.getListeners().onGainExpSp(addToExp, addToSp);
    }
  }

  private void rewardSkills(boolean send) {
    boolean update = false;
    if (Config.AUTO_LEARN_SKILLS) {
      int unLearnable = 0;

      label78:
      for(Collection skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, ClassId.VALUES[this.getActiveClassId()], AcquireType.NORMAL, null); skills.size() > unLearnable; skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL)) {
        unLearnable = 0;
        Iterator var5 = skills.iterator();

        while(true) {
          while(true) {
            if (!var5.hasNext()) {
              continue label78;
            }

            SkillLearn s = (SkillLearn)var5.next();
            Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            if (sk != null && sk.getCanLearn(this.getClassId()) && s.canAutoLearn()) {
              this.addSkill(sk, true);
            } else {
              ++unLearnable;
            }
          }
        }
      }

      update = true;
    } else {
      Iterator var9 = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL).iterator();

      label60:
      while(true) {
        SkillLearn skill;
        do {
          do {
            if (!var9.hasNext()) {
              break label60;
            }

            skill = (SkillLearn)var9.next();
          } while(skill.getCost() != 0);
        } while(skill.getItemId() != 0);

        Skill sk = SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel());
        this.addSkill(sk, true);
        if (this.getAllShortCuts().size() > 0 && sk.getLevel() > 1) {

          for (ShortCut sc : this.getAllShortCuts()) {
            if (sc.getId() == sk.getId() && sc.getType() == 2) {
              ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), sk.getLevel(), 1);
              this.sendPacket(new ShortCutRegister(this, newsc));
              this.registerShortCut(newsc);
            }
          }
        }

        update = true;
      }
    }

    if (send && update) {
      this.sendPacket(new SkillList(this));
    }

    this.updateStats();
  }

  public Race getRace() {
    return this.getBaseTemplate().race;
  }

  public int getIntSp() {
    return (int)this.getSp();
  }

  public long getSp() {
    return this._activeClass == null ? 0L : this._activeClass.getSp();
  }

  public void setSp(long sp) {
    if (this._activeClass != null) {
      this._activeClass.setSp(sp);
    }

  }

  public int getClanId() {
    return this._clan == null ? 0 : this._clan.getClanId();
  }

  public long getLeaveClanTime() {
    return this._leaveClanTime;
  }

  public long getDeleteClanTime() {
    return this._deleteClanTime;
  }

  public void setLeaveClanTime(long time) {
    this._leaveClanTime = time;
  }

  public void setDeleteClanTime(long time) {
    this._deleteClanTime = time;
  }

  public void setOnlineTime(long time) {
    this._onlineTime = time;
    this._onlineBeginTime = System.currentTimeMillis();
  }

  public long getOnlineBeginTime() {
    return this._onlineBeginTime;
  }

  public long getOnlineTime() {
    return this._onlineTime;
  }

  public void setNoChannel(long time) {
    this._NoChannel = time;
    if (this._NoChannel > 2145909600000L || this._NoChannel < 0L) {
      this._NoChannel = -1L;
    }

    if (this._NoChannel > 0L) {
      this._NoChannelBegin = System.currentTimeMillis();
    } else {
      this._NoChannelBegin = 0L;
    }

  }

  public long getNoChannel() {
    return this._NoChannel;
  }

  public long getNoChannelRemained() {
    if (this._NoChannel == 0L) {
      return 0L;
    } else if (this._NoChannel < 0L) {
      return -1L;
    } else {
      long remained = this._NoChannel - System.currentTimeMillis() + this._NoChannelBegin;
      return Math.max(remained, 0L);
    }
  }

  public void setLeaveClanCurTime() {
    this._leaveClanTime = System.currentTimeMillis();
  }

  public void setDeleteClanCurTime() {
    this._deleteClanTime = System.currentTimeMillis();
  }

  public boolean canJoinClan() {
    if (this._leaveClanTime == 0L) {
      return true;
    } else if (System.currentTimeMillis() - this._leaveClanTime >= Config.CLAN_LEAVE_TIME_PERNALTY) {
      this._leaveClanTime = 0L;
      return true;
    } else {
      return false;
    }
  }

  public boolean canCreateClan() {
    if (this._deleteClanTime == 0L) {
      return true;
    } else if (System.currentTimeMillis() - this._deleteClanTime >= Config.NEW_CLAN_CREATE_PENALTY) {
      this._deleteClanTime = 0L;
      return true;
    } else {
      return false;
    }
  }

  public IStaticPacket canJoinParty(Player inviter) {
    Request request = this.getRequest();
    if (request != null && request.isInProgress() && request.getOtherPlayer(this) != inviter) {
      return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter);
    } else if (!this.isBlockAll() && !this.getMessageRefusal()) {
      if (this.isInParty()) {
        return (new SystemMessage2(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED)).addName(this);
      } else if (inviter.getReflection() != this.getReflection() && inviter.getReflection() != ReflectionManager.DEFAULT && this.getReflection() != ReflectionManager.DEFAULT) {
        return SystemMsg.INVALID_TARGET.packet(inviter);
      } else if (!this.isCursedWeaponEquipped() && !inviter.isCursedWeaponEquipped()) {
        if (!inviter.isOlyParticipant() && !this.isOlyParticipant()) {
          if (inviter.getPlayerAccess().CanJoinParty && this.getPlayerAccess().CanJoinParty) {
            return this.getTeam() != TeamType.NONE ? SystemMsg.INVALID_TARGET.packet(inviter) : null;
          } else {
            return SystemMsg.INVALID_TARGET.packet(inviter);
          }
        } else {
          return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
        }
      } else {
        return SystemMsg.INVALID_TARGET.packet(inviter);
      }
    } else {
      return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
    }
  }

  public PcInventory getInventory() {
    return this._inventory;
  }

  public long getWearedMask() {
    return this._inventory.getWearedMask();
  }

  public PcFreight getFreight() {
    return this._freight;
  }

  public void removeItemFromShortCut(int objectId) {
    this._shortCuts.deleteShortCutByObjectId(objectId);
  }

  public void removeSkillFromShortCut(int skillId) {
    this._shortCuts.deleteShortCutBySkillId(skillId);
  }

  public boolean isSitting() {
    return this._isSitting;
  }

  public void setSitting(boolean val) {
    this._isSitting = val;
  }

  public boolean getSittingTask() {
    return this.sittingTaskLaunched;
  }

  public void sitDown(StaticObjectInstance throne) {
    if (!this.isSitting() && !this.sittingTaskLaunched && !this.isAlikeDead()) {
      if (!this.isStunned() && !this.isSleeping() && !this.isParalyzed() && !this.isAttackingNow() && !this.isCastingNow() && !this.isMoving()) {
        this.resetWaitSitTime();
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);
        if (throne == null) {
          this.broadcastPacket(new ChangeWaitType(this, 0));
        } else {
          this.broadcastPacket(new ChairSit(this, throne));
        }

        this._sittingObject = throne;
        this.setSitting(true);
        this.sittingTaskLaunched = true;
        ThreadPoolManager.getInstance().schedule(new EndSitDownTask(this), 2500L);
      } else {
        this.getAI().setNextAction(NextAction.REST, null, null, false, false);
      }
    }
  }

  public void standUp() {
    if (this.isSitting() && !this.sittingTaskLaunched && !this.isInStoreMode() && !this.isAlikeDead()) {
      this.getAI().clearNextAction();
      this.broadcastPacket(new ChangeWaitType(this, 1));
      this._sittingObject = null;
      this.sittingTaskLaunched = true;
      ThreadPoolManager.getInstance().schedule(new EndStandUpTask(this), 2500L);
    }
  }

  protected MoveActionBase createMoveToLocation(Location dest, int indent, boolean pathFind) {
    boolean ignoreGeo = !Config.ALLOW_GEODATA;
    Location from = this.getLoc();
    Location to = dest.clone();
    if (this.isInBoat()) {
      indent = (int)((double)indent + (from.distance(to) - (double)(3 * this.getBoat().getActingRange())));
      ignoreGeo = true;
    }

    return Config.MOVE_OFFLOAD_MTL_PC ? new MoveToLocationActionForOffload(this, from, to, ignoreGeo, indent, pathFind) : new MoveToLocationAction(this, from, to, ignoreGeo, indent, pathFind);
  }

  public void moveBackwardToLocationForPacket(Location loc, boolean pathfinding) {
    if (this.isMoving() && Config.MOVE_OFFLOAD_MTL_PC) {
      this._mtlOffloadData.set(new Player.MoveToLocationOffloadData(loc, 0, pathfinding));
    } else {
      this.moveToLocation(loc, 0, pathfinding);
    }
  }

  public void updateWaitSitTime() {
    if (this._waitTimeWhenSit < 200) {
      this._waitTimeWhenSit += 2;
    }

  }

  public int getWaitSitTime() {
    return this._waitTimeWhenSit;
  }

  public void resetWaitSitTime() {
    this._waitTimeWhenSit = 0;
  }

  public Warehouse getWarehouse() {
    return this._warehouse;
  }

  public ItemContainer getRefund() {
    return this._refund;
  }

  public long getAdena() {
    return this.getInventory().getAdena();
  }

  public boolean reduceAdena(long adena) {
    return this.reduceAdena(adena, false);
  }

  public boolean reduceAdena(long adena, boolean notify) {
    if (adena < 0L) {
      return false;
    } else if (adena == 0L) {
      return true;
    } else {
      boolean result = this.getInventory().reduceAdena(adena);
      if (notify && result) {
        this.sendPacket(SystemMessage2.removeItems(57, adena));
      }

      return result;
    }
  }

  public ItemInstance addAdena(long adena) {
    return this.addAdena(adena, false);
  }

  public ItemInstance addAdena(long adena, boolean notify) {
    if (adena < 1L) {
      return null;
    } else {
      ItemInstance item = this.getInventory().addAdena(adena);
      if (item != null && notify) {
        this.sendPacket(SystemMessage2.obtainItems(57, adena, 0));
      }

      return item;
    }
  }

  public GameClient getNetConnection() {
    return this._connection;
  }

  public int getRevision() {
    return this._connection == null ? 0 : this._connection.getRevision();
  }

  public void setNetConnection(GameClient connection) {
    this._connection = connection;
  }

  public boolean isConnected() {
    return this._connection != null && this._connection.isConnected();
  }

  public void onAction(Player player, boolean shift) {
    if (this.isFrozen()) {
      player.sendActionFailed();
    } else if (Events.onAction(player, this, shift)) {
      player.sendActionFailed();
    } else {
      if (player.getTarget() != this) {
        player.setTarget(this);
        if (player.getTarget() == this) {
          player.sendPacket(new MyTargetSelected(this.getObjectId(), 0));
          player.sendPacket(this.makeStatusUpdate(9, 10, 11, 12));
        } else {
          player.sendActionFailed();
        }
      } else if (this.getPrivateStoreType() != 0) {
        if (this.getRealDistance(player) > (double)this.getActingRange() && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
          if (!shift) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
          } else {
            player.sendActionFailed();
          }
        } else {
          player.doInteract(this);
        }
      } else if (this.isAutoAttackable(player)) {
        player.getAI().Attack(this, false, shift);
      } else if (player != this) {
        if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
          if (!shift) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this);
          } else {
            player.sendActionFailed();
          }
        } else {
          player.sendActionFailed();
        }
      } else {
        player.sendActionFailed();
      }

    }
  }

  public void broadcastStatusUpdate() {
    if (this.needStatusUpdate()) {
      StatusUpdate su = this.makeStatusUpdate(10, 12, 34, 9, 11, 33);
      this.sendPacket(su);
      if (this.isInParty()) {
        this.getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdate(this));
      }

      DuelEvent duelEvent = this.getEvent(DuelEvent.class);
      if (duelEvent != null) {
        duelEvent.sendPacket(new ExDuelUpdateUserInfo(this), this.getTeam().revert().name());
      }

      if (this.isOlyCompetitionStarted()) {
        this.broadcastPacket(new ExOlympiadUserInfo(this));
      }

    }
  }

  public void broadcastCharInfo() {
    this.broadcastUserInfo(false);
  }

  public void broadcastUserInfo(boolean force) {
    this.sendUserInfo(force);
    if (this.isVisible() && !this.isInvisible()) {
      if (Config.BROADCAST_CHAR_INFO_INTERVAL == 0L) {
        force = true;
      }

      if (force) {
        if (this._broadcastCharInfoTask != null) {
          this._broadcastCharInfoTask.cancel(false);
          this._broadcastCharInfoTask = null;
        }

        this.broadcastCharInfoImpl();
      } else if (this._broadcastCharInfoTask == null) {
        this._broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new Player.BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
      }
    }
  }

  public void setPolyId(int polyid) {
    this._polyNpcId = polyid;
    this.teleToLocation(this.getLoc());
    this.broadcastUserInfo(true);
  }

  public boolean isPolymorphed() {
    return this._polyNpcId != 0;
  }

  public int getPolyId() {
    return this._polyNpcId;
  }

  private void broadcastCharInfoImpl() {
    if (this.isVisible() && !this.isInvisible()) {
      L2GameServerPacket ci = this.isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this);

      for (Player player : World.getAroundPlayers(this)) {
        player.sendPacket(ci);
        player.sendPacket(RelationChanged.create(player, this, player));
      }

    }
  }

  public void setLastNpcInteractionTime() {
    this._lastNpcInteractionTime = System.currentTimeMillis();
  }

  public boolean canMoveAfterInteraction() {
    return this._lastNpcInteractionTime + 1000L < System.currentTimeMillis();
  }

  public void broadcastRelationChanged() {
    if (this.isVisible() && !this.isInvisible()) {

      for (Player player : World.getAroundPlayers(this)) {
        player.sendPacket(RelationChanged.create(player, this, player));
      }

    }
  }

  public void sendEtcStatusUpdate() {
    if (this.isVisible()) {
      this.sendPacket(new EtcStatusUpdate(this));
    }
  }

  private void sendUserInfoImpl() {
    this.sendPacket(new UserInfo(this));
  }

  public void sendUserInfo() {
    this.sendUserInfo(false);
  }

  public void sendUserInfo(boolean force) {
    if (this.isVisible() && !this.entering && !this.isLogoutStarted()) {
      if (Config.USER_INFO_INTERVAL != 0L && !force) {
        if (this._userInfoTask == null) {
          this._userInfoTask = ThreadPoolManager.getInstance().schedule(new Player.UserInfoTask(), Config.USER_INFO_INTERVAL);
        }
      } else {
        if (this._userInfoTask != null) {
          this._userInfoTask.cancel(false);
          this._userInfoTask = null;
        }

        this.sendUserInfoImpl();
      }
    }
  }

  public StatusUpdate makeStatusUpdate(int... fields) {
    StatusUpdate su = new StatusUpdate(this);
    int var4 = fields.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      int field = fields[var5];
      switch(field) {
        case 9:
          su.addAttribute(field, (int)this.getCurrentHp());
          break;
        case 10:
          su.addAttribute(field, this.getMaxHp());
          break;
        case 11:
          su.addAttribute(field, (int)this.getCurrentMp());
          break;
        case 12:
          su.addAttribute(field, this.getMaxMp());
        case 13:
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        default:
          break;
        case 14:
          su.addAttribute(field, this.getCurrentLoad());
          break;
        case 15:
          su.addAttribute(field, this.getMaxLoad());
          break;
        case 26:
          su.addAttribute(field, this._pvpFlag);
          break;
        case 27:
          su.addAttribute(field, this.getKarma());
          break;
        case 33:
          su.addAttribute(field, (int)this.getCurrentCp());
          break;
        case 34:
          su.addAttribute(field, this.getMaxCp());
      }
    }

    return su;
  }

  public void sendStatusUpdate(boolean broadCast, boolean withPet, int... fields) {
    if (fields.length != 0 && (!this.entering || broadCast)) {
      StatusUpdate su = this.makeStatusUpdate(fields);
      if (su.hasAttributes()) {
        List<L2GameServerPacket> packets = new ArrayList(withPet ? 2 : 1);
        if (withPet && this.getPet() != null) {
          packets.add(this.getPet().makeStatusUpdate(fields));
        }

        packets.add(su);
        if (!broadCast) {
          this.sendPacket(packets);
        } else if (this.entering) {
          this.broadcastPacketToOthers(packets);
        } else {
          this.broadcastPacket(packets);
        }

      }
    }
  }

  public int getAllyId() {
    return this._clan == null ? 0 : this._clan.getAllyId();
  }

  public void sendPacket(IStaticPacket p) {
    if (this.isConnected()) {
      if (!this.isPacketIgnored(p.packet(this))) {
        this._connection.sendPacket(p.packet(this));
      }
    }
  }

  public void sendPacket(IStaticPacket... packets) {
    if (this.isConnected()) {
      IStaticPacket[] var2 = packets;
      int var3 = packets.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        IStaticPacket p = var2[var4];
        if (!this.isPacketIgnored(p)) {
          this._connection.sendPacket(p.packet(this));
        }
      }

    }
  }

  private boolean isPacketIgnored(IStaticPacket p) {
    return p == null;
  }

  public void sendPacket(List<? extends IStaticPacket> packets) {
    if (this.isConnected()) {

      for (IStaticPacket p : packets) {
        this._connection.sendPacket(p.packet(this));
      }

    }
  }

  public void doInteract(GameObject target) {
    if (target != null && !this.isActionsDisabled()) {
      if (target.isPlayer()) {
        Player temp = (Player)target;
        if (this.getRealDistance(target) <= (double)target.getActingRange()) {
          switch(temp.getPrivateStoreType()) {
            case 1:
            case 8:
              this.sendPacket(new PrivateStoreListSell(this, temp));
            case 2:
            case 4:
            case 6:
            case 7:
            default:
              break;
            case 3:
              this.sendPacket(new PrivateStoreListBuy(this, temp));
              break;
            case 5:
              this.sendPacket(new RecipeShopSellList(this, temp));
          }

          this.sendActionFailed();
        } else if (!this.getAI().isIntendingInteract(temp)) {
          this.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, temp);
        }
      } else {
        target.onAction(this, false);
      }

    } else {
      this.sendActionFailed();
    }
  }

  public void doAutoLootOrDrop(ItemInstance item, NpcInstance fromNpc) {
    boolean forceAutoloot = fromNpc.isFlying() || this.getReflection().isAutolootForced();
    if ((fromNpc.isRaid() || fromNpc instanceof ReflectionBossInstance) && !Config.AUTO_LOOT_FROM_RAIDS && !item.isHerb() && !forceAutoloot) {
      item.dropToTheGround(this, fromNpc);
    } else if (item.isHerb()) {
      if (!this.AutoLootHerbs && !forceAutoloot) {
        item.dropToTheGround(this, fromNpc);
      } else {
        Skill[] skills = item.getTemplate().getAttachedSkills();
        if (skills.length > 0) {
          int var6 = skills.length;

          for(int var7 = 0; var7 < var6; ++var7) {
            Skill skill = skills[var7];
            this.altUseSkill(skill, this);
            if (this.getPet() != null && this.getPet().isSummon() && !this.getPet().isDead()) {
              this.getPet().altUseSkill(skill, this.getPet());
            }
          }
        }

        item.deleteMe();
      }
    } else if (forceAutoloot || this._autoLoot || item.getItemId() == 57 && this.AutoLootAdena) {
      if (!this.isInParty()) {
        if (!this.pickupItem(item, ItemLog.Pickup)) {
          item.dropToTheGround(this, fromNpc);
          return;
        }
      } else {
        this.getParty().distributeItem(this, item, fromNpc);
      }

      this.broadcastPickUpMsg(item);
    } else {
      item.dropToTheGround(this, fromNpc);
    }
  }

  public void doPickupItem(GameObject object) {
    if (!object.isItem()) {
      log.warn("trying to pickup wrong target." + this.getTarget());
    } else {
      this.sendActionFailed();
      this.stopMove();
      ItemInstance item = (ItemInstance)object;
      synchronized(item) {
        if (item.isVisible()) {
          if (!ItemFunctions.checkIfCanPickup(this, item)) {
            SystemMessage sm;
            if (item.getItemId() == 57) {
              sm = new SystemMessage(55);
              sm.addNumber(item.getCount());
            } else {
              sm = new SystemMessage(56);
              sm.addItemName(item.getItemId());
            }

            this.sendPacket(sm);
          } else if (item.isHerb()) {
            Skill[] skills = item.getTemplate().getAttachedSkills();
            if (skills.length > 0) {
              int var6 = skills.length;

              for(int var7 = 0; var7 < var6; ++var7) {
                Skill skill = skills[var7];
                this.altUseSkill(skill, this);
              }
            }

            this.broadcastPacket(new GetItem(item, this.getObjectId()));
            item.deleteMe();
          } else {
            FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment)item.getAttachment() : null;
            if (this.isInParty() && attachment == null) {
              this.getParty().distributeItem(this, item, null);
            } else if (this.pickupItem(item, ItemLog.Pickup)) {
              this.broadcastPacket(new GetItem(item, this.getObjectId()));
              this.broadcastPickUpMsg(item);
              item.pickupMe();
            }

          }
        }
      }
    }
  }

  public boolean pickupItem(ItemInstance item, ItemLog log) {
    PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment)item.getAttachment() : null;
    if (!ItemFunctions.canAddItem(this, item)) {
      return false;
    } else {
      if (item.getItemId() == 57 || item.getItemId() == 6353) {
        Quest q = QuestManager.getQuest(255);
        if (q != null) {
          this.processQuestEvent(q.getName(), "CE" + item.getItemId(), null);
        }
      }

      Log.LogItem(this, log, item);
      this.sendPacket(SystemMessage2.obtainItems(item));
      this.getInventory().addItem(item);
      if (attachment != null) {
        attachment.pickUp(this);
      }

      this.sendChanges();
      return true;
    }
  }

  public void setObjectTarget(GameObject target) {
    this.setTarget(target);
    if (target != null) {
      if (target == this.getTarget()) {
        if (target.isNpc()) {
          NpcInstance npc = (NpcInstance)target;
          this.sendPacket(new MyTargetSelected(npc.getObjectId(), this.getLevel() - npc.getLevel()));
          this.sendPacket(npc.makeStatusUpdate(new int[]{9, 10}));
          this.sendPacket(new ValidateLocation(npc), ActionFail.STATIC);
        } else {
          this.sendPacket(new MyTargetSelected(target.getObjectId(), 0));
        }
      }

    }
  }

  public void setTarget(GameObject newTarget) {
    if (newTarget != null && !newTarget.isVisible()) {
      newTarget = null;
    }

    if (newTarget instanceof FestivalMonsterInstance && !this.isFestivalParticipant()) {
      newTarget = null;
    }

    Party party = this.getParty();
    if (party != null && party.isInDimensionalRift()) {
      int riftType = party.getDimensionalRift().getType();
      int riftRoom = party.getDimensionalRift().getCurrentRoom();
      if (newTarget != null && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(newTarget.getX(), newTarget.getY(), newTarget.getZ())) {
        newTarget = null;
      }
    }

    GameObject oldTarget = this.getTarget();
    if (oldTarget != null) {
      if (oldTarget.equals(newTarget)) {
        return;
      }

      if (oldTarget.isCreature()) {
        ((Creature)oldTarget).removeStatusListener(this);
      }

      this.broadcastPacket(new TargetUnselected(this));
    }

    if (newTarget != null) {
      if (newTarget.isCreature()) {
        ((Creature)newTarget).addStatusListener(this);
      }

      this.broadcastPacket(new TargetSelected(this.getObjectId(), newTarget.getObjectId(), this.getLoc()));
    }

    super.setTarget(newTarget);
  }

  public ItemInstance getActiveWeaponInstance() {
    return this.getInventory().getPaperdollItem(7);
  }

  public WeaponTemplate getActiveWeaponItem() {
    ItemInstance weapon = this.getActiveWeaponInstance();
    return weapon == null ? this.getFistsWeaponItem() : (WeaponTemplate)weapon.getTemplate();
  }

  public ItemInstance getSecondaryWeaponInstance() {
    return this.getInventory().getPaperdollItem(8);
  }

  public WeaponTemplate getSecondaryWeaponItem() {
    ItemInstance weapon = this.getSecondaryWeaponInstance();
    if (weapon == null) {
      return this.getFistsWeaponItem();
    } else {
      ItemTemplate item = weapon.getTemplate();
      return item instanceof WeaponTemplate ? (WeaponTemplate)item : null;
    }
  }

  public boolean isWearingArmor(ArmorType armorType) {
    ItemInstance chest = this.getInventory().getPaperdollItem(10);
    if (chest == null) {
      return armorType == ArmorType.NONE;
    } else if (chest.getItemType() != armorType) {
      return false;
    } else if (chest.getBodyPart() == 32768) {
      return true;
    } else if (chest.getBodyPart() == 131072) {
      return true;
    } else {
      ItemInstance legs = this.getInventory().getPaperdollItem(11);
      return legs == null ? armorType == ArmorType.NONE : legs.getItemType() == armorType;
    }
  }

  public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
    if (attacker != null && !this.isDead() && (!attacker.isDead() || isDot)) {
      if (attacker.isPlayer() && Math.abs(attacker.getLevel() - this.getLevel()) > 10) {
        if (attacker.getKarma() > 0 && this.getEffectList().getEffectsBySkillId(5182) != null && !this.isInZone(ZoneType.SIEGE)) {
          return;
        }

        if (this.getKarma() > 0 && attacker.getEffectList().getEffectsBySkillId(5182) != null && !attacker.isInZone(ZoneType.SIEGE)) {
          return;
        }
      }

      super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }
  }

  protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
    if (standUp) {
      this.standUp();
      if (this.isFakeDeath()) {
        this.breakFakeDeath();
      }
    }

    if (this.isOlyParticipant()) {
      if (this.isOlyCompetitionStarted()) {
        this.getOlyParticipant().OnDamaged(this, attacker, damage, directHp ? this.getCurrentHp() : this.getCurrentHp() + this.getCurrentCp());
      }

      if (!this.getOlyParticipant().isAlive()) {
        return;
      }
    }

    double cp;
    if (attacker.isPlayable() && !directHp && this.getCurrentCp() > 0.0D) {
      cp = this.getCurrentCp();
      if (cp >= damage) {
        cp -= damage;
        damage = 0.0D;
      } else {
        damage -= cp;
        cp = 0.0D;
      }

      this.setCurrentCp(cp);
    }

    cp = this.getCurrentHp();
    DuelEvent duelEvent = this.getEvent(DuelEvent.class);
    if (duelEvent != null && cp - damage <= 1.0D) {
      this.setCurrentHp(1.0D, false);
      duelEvent.onDie(this);
    } else {
      super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }
  }

  public boolean isAlikeDead() {
    return this._fakeDeath == 1 || super.isAlikeDead();
  }

  public boolean isMovementDisabled() {
    return this.isFakeDeath() || super.isMovementDisabled();
  }

  public boolean isActionsDisabled() {
    return this.isFakeDeath() || super.isActionsDisabled();
  }

  public void doAttack(Creature target) {
    if (!this.isFakeDeath() && !this.isInMountTransform()) {
      super.doAttack(target);
    }
  }

  public void onHitTimer(Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS) {
    if (this.isFakeDeath()) {
      this.sendActionFailed();
    } else {
      super.onHitTimer(target, damage, crit, miss, soulshot, shld, unchargeSS);
    }
  }

  public boolean isFakeDeath() {
    return this._fakeDeath != 0;
  }

  public void setFakeDeath(int value) {
    this._fakeDeath = value;
  }

  public void breakFakeDeath() {
    this.getEffectList().stopAllSkillEffects(EffectType.FakeDeath);
  }

  private void altDeathPenalty(Creature killer) {
    if (Config.ALT_GAME_DELEVEL) {
      if (!this.isInZoneBattle() && !this.isInZone(ZoneType.fun)) {
        this.deathPenalty(killer);
      }
    }
  }

  public final boolean atWarWith(Player player) {
    return this._clan != null && player.getClan() != null && this.getPledgeType() != -1 && player.getPledgeType() != -1 && this._clan.isAtWarWith(player.getClan().getClanId());
  }

  public boolean atMutualWarWith(Player player) {
    return this._clan != null && player.getClan() != null && this.getPledgeType() != -1 && player.getPledgeType() != -1 && this._clan.isAtWarWith(player.getClan().getClanId()) && player.getClan().isAtWarWith(this._clan.getClanId());
  }

  public void doPurePk(Player killer) {
    super.doPurePk(killer);
    killer.setPkKills(killer.getPkKills() + 1);
    if (Config.SERVICES_PK_ANNOUNCE) {
      Announcements.getInstance().announceByCustomMessage("player.pkannounce", new String[]{killer.getName(), this.getName()});
    }

  }

  private final void processRewardPvpPkKill(Player killer, boolean isThisPlayerKiller) {
    if (isThisPlayerKiller) {
      this.doPurePk(killer);
      killer.getListeners().onPvpPkKill(this, true);
    } else {
      killer.setPvpKills(killer.getPvpKills() + 1);
      killer.getListeners().onPvpPkKill(this, false);
    }

    if (Config.SERVICES_PK_KILL_BONUS_ENABLE || Config.SERVICES_PVP_KILL_BONUS_ENABLE) {
      boolean ipCheckSuccess = true;
      boolean hwidCheckSuccess = true;
      if (Config.SERVICES_PK_PVP_BONUS_TIE_IF_SAME_IP) {
        ipCheckSuccess = this.getIP() == null && killer.getIP() != null || this.getIP() != null && !this.getIP().equals(killer.getIP());
      }

      if (Config.SERVICES_PK_PVP_BONUS_TIE_IF_SAME_HWID) {
        String myHwid = this.getNetConnection() != null ? this.getNetConnection().getHwid() : null;
        String killerHwid = killer.getNetConnection() != null ? killer.getNetConnection().getHwid() : null;
        hwidCheckSuccess = myHwid == null && killerHwid == null || myHwid != null && !myHwid.equals(killerHwid);
      }

      long now = System.currentTimeMillis();
      long lastKillTime = killer.getVarLong("LastPVPPKKill", 0L);
      if ((ipCheckSuccess || hwidCheckSuccess) && now - lastKillTime > Config.SERVICES_PK_KILL_BONUS_INTERVAL) {
        if (isThisPlayerKiller) {
          ItemFunctions.addItem(killer, Config.SERVICES_PK_KILL_BONUS_REWARD_ITEM, Config.SERVICES_PK_KILL_BONUS_REWARD_COUNT, true);
        } else {
          ItemFunctions.addItem(killer, Config.SERVICES_PVP_KILL_BONUS_REWARD_ITEM, Config.SERVICES_PVP_KILL_BONUS_REWARD_COUNT, true);
        }

        killer.setVar("LastPVPPKKill", now, -1L);
      }
    }

  }

  public void checkAddItemToDrop(List<ItemInstance> array, List<ItemInstance> items, int maxCount) {
    for(int i = 0; i < maxCount && !items.isEmpty(); ++i) {
      array.add(items.remove(Rnd.get(items.size())));
    }

  }

  public FlagItemAttachment getActiveWeaponFlagAttachment() {
    ItemInstance item = this.getActiveWeaponInstance();
    return item != null && item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment)item.getAttachment() : null;
  }

  protected void doPKPVPManage(Creature killer) {
    FlagItemAttachment attachment = this.getActiveWeaponFlagAttachment();
    if (attachment != null) {
      attachment.onDeath(this, killer);
    }

    if (killer != null && killer != this._summon && killer != this) {
      if (!this.isInZoneBattle() && !killer.isInZoneBattle()) {
        boolean inFunZone = this.isInZone(ZoneType.fun);
        if (Config.FUN_ZONE_PVP_COUNT || !inFunZone) {
          if (!(killer instanceof Summon) || (killer = killer.getPlayer()) != null) {
            if (killer.isPlayer()) {
              Player pk = (Player)killer;
              int repValue = this.getLevel() - pk.getLevel() >= 20 ? Config.CRP_REWARD_ON_WAR_KILL_OVER_LEVEL : Config.CRP_REWARD_ON_WAR_KILL;
              boolean war = this.atMutualWarWith(pk);
              if (war && pk.getClan().getReputationScore() > 0 && this._clan.getLevel() >= 5 && this._clan.getReputationScore() > 0 && pk.getClan().getLevel() >= 5) {
                this._clan.broadcastToOtherOnlineMembers((new SystemMessage(1782)).addString(this.getName()).addNumber(-this._clan.incReputation(-repValue, true, "ClanWar")), this);
                pk.getClan().broadcastToOtherOnlineMembers((new SystemMessage(1783)).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
              }

              if (this.isOnSiegeField()) {
                return;
              }

              if (Config.FUN_ZONE_PVP_COUNT && inFunZone) {
                this.processRewardPvpPkKill(pk, false);
                pk.sendChanges();
                return;
              }

              if (this._pvpFlag <= 0 && !war) {
                this.processRewardPvpPkKill(pk, this._karma <= 0);
              } else {
                this.processRewardPvpPkKill(pk, false);
              }

              pk.sendChanges();
            }

            int karma = this._karma;
            this.decreaseKarma(Config.KARMA_LOST_BASE);
            boolean isPvP = killer.isPlayable() || killer instanceof GuardInstance;
            if ((!killer.isMonster() || Config.DROP_ITEMS_ON_DIE) && (!isPvP || this._pkKills >= Config.MIN_PK_TO_ITEMS_DROP && (karma != 0 || !Config.KARMA_NEEDED_TO_DROP)) && !this.isFestivalParticipant() && (killer.isMonster() || isPvP)) {
              if (Config.KARMA_DROP_GM || !this.isGM()) {
                if (Config.ITEM_ANTIDROP_FROM_PK <= 0 || this.getInventory().getItemByItemId(Config.ITEM_ANTIDROP_FROM_PK) == null) {
                  int max_drop_count = isPvP ? Config.KARMA_DROP_ITEM_LIMIT : 1;
                  double dropRate;
                  if (isPvP) {
                    dropRate = (double)this._pkKills * Config.KARMA_DROPCHANCE_MOD + Config.KARMA_DROPCHANCE_BASE;
                  } else {
                    dropRate = Config.NORMAL_DROPCHANCE_BASE;
                  }

                  int dropEquipCount = 0;
                  int dropWeaponCount = 0;
                  int dropItemCount = 0;

                  for(int i = 0; (double)i < Math.ceil(dropRate / 100.0D) && i < max_drop_count; ++i) {
                    if (Rnd.chance(dropRate)) {
                      int rand = Rnd.get(Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT + Config.DROPCHANCE_ITEM) + 1;
                      if (rand > Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT) {
                        ++dropItemCount;
                      } else if (rand > Config.DROPCHANCE_EQUIPPED_WEAPON) {
                        ++dropEquipCount;
                      } else {
                        ++dropWeaponCount;
                      }
                    }
                  }

                  List<ItemInstance> drop = new LazyArrayList();
                  List<ItemInstance> dropItem = new LazyArrayList();
                  List<ItemInstance> dropEquip = new LazyArrayList();
                  List<ItemInstance> dropWeapon = new LazyArrayList();
                  this.getInventory().writeLock();

                  try {
                    ItemInstance[] var16 = this.getInventory().getItems();
                    int var17 = var16.length;

                    for(int var18 = 0; var18 < var17; ++var18) {
                      ItemInstance item = var16[var18];
                      if (item.canBeDropped(this, true) && !Config.KARMA_LIST_NONDROPPABLE_ITEMS.contains(item.getItemId())) {
                        if (item.getTemplate().getType2() == 0) {
                          dropWeapon.add(item);
                        } else if (item.getTemplate().getType2() != 1 && item.getTemplate().getType2() != 2) {
                          if (item.getTemplate().getType2() == 5) {
                            dropItem.add(item);
                          }
                        } else {
                          dropEquip.add(item);
                        }
                      }
                    }

                    this.checkAddItemToDrop(drop, dropWeapon, dropWeaponCount);
                    this.checkAddItemToDrop(drop, dropEquip, dropEquipCount);
                    this.checkAddItemToDrop(drop, dropItem, dropItemCount);
                    if (drop.isEmpty()) {
                      return;
                    }

                    for (ItemInstance item : drop) {
                      if (item.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) {
                        item.setVariationStat1(0);
                        item.setVariationStat2(0);
                      }

                      item = this.getInventory().removeItem(item);
                      Log.LogItem(this, ItemLog.PvPDrop, item);
                      if (item.getEnchantLevel() > 0) {
                        this.sendPacket((new SystemMessage(375)).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
                      } else {
                        this.sendPacket((new SystemMessage(298)).addItemName(item.getItemId()));
                      }

                      if (!killer.isPlayable() || (!Config.AUTO_LOOT || !Config.AUTO_LOOT_PK) && !this.isInFlyingTransform()) {
                        item.dropToTheGround(this, Location.findAroundPosition(this, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
                      } else {
                        killer.getPlayer().getInventory().addItem(item);
                        Log.LogItem(this, ItemLog.Pickup, item);
                        killer.getPlayer().sendPacket(SystemMessage2.obtainItems(item));
                      }
                    }
                  } finally {
                    this.getInventory().writeUnlock();
                  }

                }
              }
            }
          }
        }
      }
    }
  }

  protected void onDeath(Creature killer) {
    this.getDeathPenalty().checkCharmOfLuck();
    if (this.isInStoreMode()) {
      this.setPrivateStoreType(0);
    }

    if (this.isProcessingRequest()) {
      Request request = this.getRequest();
      if (this.isInTrade()) {
        Player parthner = request.getOtherPlayer(this);
        this.sendPacket(SendTradeDone.FAIL);
        parthner.sendPacket(SendTradeDone.FAIL);
      }

      request.cancel();
    }

    this.setAgathion(0);
    boolean checkPvp = true;
    if (Config.ALLOW_CURSED_WEAPONS) {
      if (this.isCursedWeaponEquipped()) {
        CursedWeaponsManager.getInstance().dropPlayer(this);
        checkPvp = false;
      } else if (killer != null && killer.isPlayer() && killer.isCursedWeaponEquipped()) {
        CursedWeaponsManager.getInstance().increaseKills(((Player)killer).getCursedWeaponEquippedId());
        checkPvp = false;
      }
    }

    if (checkPvp) {
      this.doPKPVPManage(killer);
      this.altDeathPenalty(killer);
    }

    this.getDeathPenalty().notifyDead(killer);
    this.setIncreasedForce(0);
    if (this.isInParty() && this.getParty().isInReflection() && this.getParty().getReflection() instanceof DimensionalRift) {
      ((DimensionalRift)this.getParty().getReflection()).memberDead(this);
    }

    this.stopWaterTask();
    if (!this.isSalvation() && this.isOnSiegeField() && this.isCharmOfCourage()) {
      this.setCharmOfCourage(false);
    }

    if (this.getLevel() < 6) {
      Quest q = QuestManager.getQuest(255);
      if (q != null) {
        this.processQuestEvent(q.getName(), "CE30", null);
      }
    }

    super.onDeath(killer);
  }

  public void restoreExp() {
    this.restoreExp(100.0D);
  }

  public void restoreExp(double percent) {
    if (percent != 0.0D) {
      int lostexp = 0;
      String lostexps = this.getVar("lostexp");
      if (lostexps != null) {
        lostexp = Integer.parseInt(lostexps);
        this.unsetVar("lostexp");
      }

      if (lostexp != 0) {
        this.addExpAndSp((long)((double)lostexp * percent / 100.0D), 0L);
      }

    }
  }

  public void deathPenalty(Creature killer) {
    if (killer != null) {
      boolean atwar = killer.getPlayer() != null && this.atWarWith(killer.getPlayer());
      double deathPenaltyBonus = this.getDeathPenalty().getLevel() * Config.ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
      if (deathPenaltyBonus < 2.0D) {
        deathPenaltyBonus = 1.0D;
      } else {
        deathPenaltyBonus /= 2.0D;
      }

      double percentLost = 8.0D;
      int level = this.getLevel();
      if (level >= 79) {
        percentLost = 1.0D;
      } else if (level >= 78) {
        percentLost = 1.5D;
      } else if (level >= 76) {
        percentLost = 2.0D;
      } else if (level >= 40) {
        percentLost = 4.0D;
      }

      if (Config.ALT_DEATH_PENALTY) {
        percentLost = percentLost * Config.RATE_XP + (double)this._pkKills * Config.ALT_PK_DEATH_RATE;
      }

      if (this.isFestivalParticipant() || atwar) {
        percentLost /= 4.0D;
      }

      int lostexp = (int)Math.round((double)(Experience.LEVEL[level + 1] - Experience.LEVEL[level]) * percentLost / 100.0D);
      lostexp = (int)((double)lostexp * deathPenaltyBonus);
      lostexp = (int)this.calcStat(Stats.EXP_LOST, lostexp, killer, null);
      if (this.isOnSiegeField()) {
        SiegeEvent<?, ?> siegeEvent = (SiegeEvent)this.getEvent(SiegeEvent.class);
        if (siegeEvent != null) {
          lostexp = 0;
        }
      }

      long before = this.getExp();
      this.addExpAndSp(-lostexp, 0L);
      long lost = before - this.getExp();
      if (lost > 0L) {
        this.setVar("lostexp", String.valueOf(lost), -1L);
      }

    }
  }

  public void setRequest(Request transaction) {
    this._request = transaction;
  }

  public Request getRequest() {
    return this._request;
  }

  public boolean isBusy() {
    return this.isProcessingRequest() || this.isOutOfControl() || this.isOlyParticipant() || this.getTeam() != TeamType.NONE || this.isInStoreMode() || this.isInDuel() || this.getMessageRefusal() || this.isBlockAll() || this.isInvisible();
  }

  public boolean isProcessingRequest() {
    if (this._request == null) {
      return false;
    } else {
      return this._request.isInProgress();
    }
  }

  public boolean isInTrade() {
    return this.isProcessingRequest() && this.getRequest().isTypeOf(L2RequestType.TRADE);
  }

  public List<L2GameServerPacket> addVisibleObject(GameObject object, Creature dropper) {
    return !this.isLogoutStarted() && object != null && object.getObjectId() != this.getObjectId() && object.isVisible() ? object.addPacketList(this, dropper) : Collections.emptyList();
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    if (this.isInvisible() && forPlayer.getObjectId() != this.getObjectId()) {
      return Collections.emptyList();
    } else if (this.getPrivateStoreType() != 0 && forPlayer.getVarB("notraders")) {
      return Collections.emptyList();
    } else if (this.isInObserverMode() && this.getCurrentRegion() != this.getObserverRegion() && this.getObserverRegion() == forPlayer.getCurrentRegion()) {
      return Collections.emptyList();
    } else {
      List<L2GameServerPacket> list = new ArrayList<>();
      if (forPlayer.getObjectId() != this.getObjectId()) {
        list.add(this.isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this));
      }

      if (this.isSitting() && this._sittingObject != null) {
        list.add(new ChairSit(this, this._sittingObject));
      }

      if (this.getPrivateStoreType() != 0) {
        if (this.getPrivateStoreType() == 3) {
          list.add(new PrivateStoreMsgBuy(this));
        } else if (this.getPrivateStoreType() != 1 && this.getPrivateStoreType() != 8) {
          if (this.getPrivateStoreType() == 5) {
            list.add(new RecipeShopMsg(this));
          }
        } else {
          list.add(new PrivateStoreMsgSell(this));
        }

        if (forPlayer.isInZonePeace()) {
          return list;
        }
      }

      if (this.isCastingNow()) {
        Creature castingTarget = this.getCastingTarget();
        Skill castingSkill = this.getCastingSkill();
        long animationEndTime = this.getAnimationEndTime();
        if (castingSkill != null && castingTarget != null && castingTarget.isCreature() && this.getAnimationEndTime() > 0L) {
          list.add(new MagicSkillUse(this, castingTarget, castingSkill, (int)(animationEndTime - System.currentTimeMillis()), 0L));
        }
      }

      if (this.isInCombat()) {
        list.add(new AutoAttackStart(this.getObjectId()));
      }

      list.add(RelationChanged.create(forPlayer, this, forPlayer));
      if (this.isInBoat()) {
        list.add(this.getBoat().getOnPacket(this, this.getInBoatPosition()));
      } else if (this.isMoving() || this.isFollowing()) {
        list.add(this.movePacket());
      }

      return list;
    }
  }

  public List<L2GameServerPacket> removeVisibleObject(GameObject object, List<L2GameServerPacket> list) {
    if (!this.isLogoutStarted() && object != null && object.getObjectId() != this.getObjectId()) {
      List<L2GameServerPacket> result = list == null ? object.deletePacketList() : list;
      if (this.isFollowing() && this.getFollowTarget() == object) {
        this.stopMove();
      }

      this.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
      return result;
    } else {
      return null;
    }
  }

  private void levelSet(int levels) {
    if (levels > 0) {
      this.sendPacket(Msg.YOU_HAVE_INCREASED_YOUR_LEVEL);
      this.broadcastPacket(new SocialAction(this.getObjectId(), 15));
      this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp());
      this.setCurrentCp(this.getMaxCp());
      Quest q = QuestManager.getQuest(255);
      if (q != null) {
        this.processQuestEvent(q.getName(), "CE40", null);
      }
    } else if (levels < 0) {
      this.checkSkills();
    }

    if (this.isInParty()) {
      this.getParty().recalculatePartyData();
    }

    if (this._clan != null) {
      this._clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
    }

    if (this._matchingRoom != null) {
      this._matchingRoom.broadcastPlayerUpdate(this);
    }

    this.rewardSkills(true);
  }

  public void checkSkills() {
    if (!Config.ALT_WEAK_SKILL_LEARN) {
      Skill[] var1 = this.getAllSkillsArray();
      int var2 = var1.length;

      for (Skill sk : var1) {
        SkillTreeTable.checkSkill(this, sk);
      }

      this.sendPacket(new SkillList(this));
    }
  }

  public void startTimers() {
    this.startAutoSaveTask();
    this.startPcBangPointsTask();
    this.startBonusTask();
    this.getInventory().startTimers();
    this.resumeQuestTimers();
  }

  public void stopAllTimers() {
    this.setAgathion(0);
    this.stopWaterTask();
    this.stopBonusTask();
    this.stopHourlyTask();
    this.stopKickTask();
    this.stopPcBangPointsTask();
    this.stopAutoSaveTask();
    this.getInventory().stopAllTimers();
    this.stopQuestTimers();
    this.stopCustomHeroEndTask();
  }

  public Summon getPet() {
    return this._summon;
  }

  public void setPet(Summon summon) {
    boolean isPet = false;
    if (this._summon != null && this._summon.isPet()) {
      isPet = true;
    }

    this.unsetVar("pet");
    this._summon = summon;
    this.autoShot();
    if (summon == null) {
      if (isPet) {
        if (this.isLogoutStarted() && this.getPetControlItem() != null) {
          this.setVar("pet", String.valueOf(this.getPetControlItem().getObjectId()), -1L);
        }

        this.setPetControlItem(null);
      }

      this.getEffectList().stopEffect(4140);
    }

  }

  public void scheduleDelete() {
    long time = 0L;
    if (Config.SERVICES_ENABLE_NO_CARRIER) {
      time = NumberUtils.toInt(this.getVar("noCarrier"), Config.SERVICES_NO_CARRIER_DEFAULT_TIME);
    }

    this.scheduleDelete(time * 1000L);
  }

  public void scheduleDelete(long time) {
    if (!this.isLogoutStarted() && !this.isInOfflineMode()) {
      this.broadcastCharInfo();
      ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
        public void runImpl() throws Exception {
          if (!Player.this.isConnected()) {
            Player.this.prepareToLogout();
            Player.this.deleteMe();
          }

        }
      }, time);
    }
  }

  protected void onDelete() {
    super.onDelete();
    WorldRegion observerRegion = this.getObserverRegion();
    if (observerRegion != null) {
      observerRegion.removeObject(this);
    }

    this._friendList.notifyFriends(false);
    this._inventory.clear();
    this._warehouse.clear();
    this._summon = null;
    this._arrowItem = null;
    this._fistsWeaponItem = null;
    this._chars = null;
    this._enchantScroll = null;
    this._lastNpc = HardReferences.emptyRef();
    this._observerRegion = null;
  }

  public void setTradeList(List<TradeItem> list) {
    this._tradeList = list;
  }

  public List<TradeItem> getTradeList() {
    return this._tradeList;
  }

  public String getSellStoreName() {
    return this._sellStoreName;
  }

  public void setSellStoreName(String name) {
    this._sellStoreName = Strings.stripToSingleLine(name);
  }

  public void setSellList(boolean packageSell, List<TradeItem> list) {
    if (packageSell) {
      this._packageSellList = list;
    } else {
      this._sellList = list;
    }

  }

  public List<TradeItem> getSellList() {
    switch(this.getPrivateStoreType()) {
      case 1:
        return this.getSellList(false);
      case 8:
        return this.getSellList(true);
      default:
        return Collections.emptyList();
    }
  }

  public List<TradeItem> getSellList(boolean packageSell) {
    return packageSell ? this._packageSellList : this._sellList;
  }

  public String getBuyStoreName() {
    return this._buyStoreName;
  }

  public void setBuyStoreName(String name) {
    this._buyStoreName = Strings.stripToSingleLine(name);
  }

  public void setBuyList(List<TradeItem> list) {
    this._buyList = list;
  }

  public List<TradeItem> getBuyList() {
    List<TradeItem> buyList = this._buyList;
    return buyList != null ? buyList : Collections.emptyList();
  }

  public void setManufactureName(String name) {
    this._manufactureName = Strings.stripToSingleLine(name);
  }

  public String getManufactureName() {
    return this._manufactureName;
  }

  public List<ManufactureItem> getCreateList() {
    return this._createList;
  }

  public void setCreateList(List<ManufactureItem> list) {
    this._createList = list;
  }

  public void setPrivateStoreType(int newMode) {
    int prevMode = this._privatestore.get();
    if (prevMode != newMode && this._privatestore.compareAndSet(prevMode, newMode)) {
      if (newMode != 0) {
        if (prevMode == 0) {
          this.sitDown(null);
          this.broadcastCharInfo();
        }

        this.setVar("storemode", String.valueOf(newMode), -1L);
      } else {
        this.unsetVar("storemode");
        if (!this.isDead()) {
          this.standUp();
          this.broadcastCharInfo();
        }
      }
    }

  }

  public boolean isInStoreMode() {
    return this._privatestore.get() != 0;
  }

  public int getPrivateStoreType() {
    return this._privatestore.get();
  }

  public void setClan(Clan clan) {
    if (this._clan != clan && this._clan != null) {
      this.unsetVar("canWhWithdraw");
    }

    Clan oldClan = this._clan;
    if (oldClan != null && clan == null) {
      Skill[] var3 = oldClan.getAllSkills();
      int var4 = var3.length;

      for (Skill skill : var3) {
        this.removeSkill(skill, false);
      }
    }

    this._clan = clan;
    if (clan == null) {
      this._pledgeType = -128;
      this._pledgeClass = 0;
      this._powerGrade = 0;
      this._apprentice = 0;
      this.getInventory().validateItems();
    } else {
      if (!clan.isAnyMember(this.getObjectId())) {
        this.setClan(null);
        if (!this.isNoble()) {
          this.setTitle("");
        }
      }

    }
  }

  public Clan getClan() {
    return this._clan;
  }

  public SubUnit getSubUnit() {
    return this._clan == null ? null : this._clan.getSubUnit(this._pledgeType);
  }

  public ClanHall getClanHall() {
    int id = this._clan != null ? this._clan.getHasHideout() : 0;
    return (ClanHall) ResidenceHolder.getInstance().getResidence(ClanHall.class, id);
  }

  public Castle getCastle() {
    int id = this._clan != null ? this._clan.getCastle() : 0;
    return (Castle) ResidenceHolder.getInstance().getResidence(Castle.class, id);
  }

  public Alliance getAlliance() {
    return this._clan == null ? null : this._clan.getAlliance();
  }

  public boolean isClanLeader() {
    return this._clan != null && this.getObjectId() == this._clan.getLeaderId();
  }

  public boolean isAllyLeader() {
    return this.getAlliance() != null && this.getAlliance().getLeader().getLeaderId() == this.getObjectId();
  }

  public void reduceArrowCount() {
    this.sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
    if (!this.getInventory().destroyItemByObjectId(this.getInventory().getPaperdollObjectId(8), 1L)) {
      this.getInventory().setPaperdollItem(8, null);
      this._arrowItem = null;
    }

  }

  protected boolean checkAndEquipArrows() {
    if (this.getInventory().getPaperdollItem(8) == null) {
      ItemInstance activeWeapon = this.getActiveWeaponInstance();
      if (activeWeapon != null && activeWeapon.getItemType() == WeaponType.BOW) {
        this._arrowItem = this.getInventory().findArrowForBow(activeWeapon.getTemplate());
      }

      if (this._arrowItem != null) {
        this.getInventory().setPaperdollItem(8, this._arrowItem);
      }
    } else {
      this._arrowItem = this.getInventory().getPaperdollItem(8);
    }

    return this._arrowItem != null;
  }

  public void setUptime(long time) {
    this._uptime = time;
  }

  public long getUptime() {
    return System.currentTimeMillis() - this._uptime;
  }

  public boolean isInParty() {
    return this._party != null;
  }

  public void setParty(Party party) {
    this._party = party;
  }

  public void joinParty(Party party) {
    if (party != null) {
      party.addPartyMember(this);
    }

  }

  public void leaveParty() {
    if (this.isInParty()) {
      this._party.removePartyMember(this, false);
    }

  }

  public Party getParty() {
    return this._party;
  }

  public void setLastPartyPosition(Location loc) {
    this._lastPartyPosition = loc;
  }

  public Location getLastPartyPosition() {
    return this._lastPartyPosition;
  }

  public boolean isGM() {
    return this._playerAccess != null && this._playerAccess.IsGM;
  }

  public void setAccessLevel(int level) {
    this._accessLevel = level;
  }

  public int getAccessLevel() {
    return this._accessLevel;
  }

  public void setPlayerAccess(PlayerAccess pa) {
    if (pa != null) {
      this._playerAccess = pa;
    } else {
      this._playerAccess = new PlayerAccess();
    }

    this.setAccessLevel(!this.isGM() && !this._playerAccess.Menu ? 0 : 100);
  }

  public PlayerAccess getPlayerAccess() {
    return this._playerAccess;
  }

  public double getLevelMod() {
    return (89.0D + (double)this.getLevel()) / 100.0D;
  }

  public void updateStats() {
    if (!this.entering && !this.isLogoutStarted()) {
      this.refreshOverloaded();
      this.refreshExpertisePenalty();
      super.updateStats();
    }
  }

  public void sendChanges() {
    if (!this.entering && !this.isLogoutStarted()) {
      super.sendChanges();
    }
  }

  public void updateKarma(boolean flagChanged) {
    this.sendStatusUpdate(true, true, 27);
    if (flagChanged) {
      this.broadcastRelationChanged();
    }

  }

  public boolean isOnline() {
    return this._isOnline;
  }

  public void setIsOnline(boolean isOnline) {
    this._isOnline = isOnline;
  }

  public void setOnlineStatus(boolean isOnline) {
    this._isOnline = isOnline;
    this.updateOnlineStatus();
  }

  private void updateOnlineStatus() {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?");
      statement.setInt(1, this.isOnline() && !this.isInOfflineMode() ? 1 : 0);
      statement.setLong(2, System.currentTimeMillis() / 1000L);
      statement.setInt(3, this.getObjectId());
      statement.execute();
    } catch (Exception var7) {
      log.error("", var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void increaseKarma(long add_karma) {
    boolean flagChanged = this._karma == 0;
    long new_karma = (long)this._karma + add_karma;
    if (new_karma > 2147483647L) {
      new_karma = 2147483647L;
    }

    if (this._karma == 0 && new_karma > 0L) {
      if (this._pvpFlag > 0) {
        this._pvpFlag = 0;
        if (this._PvPRegTask != null) {
          this._PvPRegTask.cancel(true);
          this._PvPRegTask = null;
        }

        this.sendStatusUpdate(true, true, 26);
      }

      this._karma = (int)new_karma;
    } else {
      this._karma = (int)new_karma;
    }

    this.updateKarma(flagChanged);
  }

  public void decreaseKarma(int i) {
    boolean flagChanged = this._karma > 0;
    this._karma -= i;
    if (this._karma <= 0) {
      this._karma = 0;
      this.updateKarma(flagChanged);
    } else {
      this.updateKarma(false);
    }

  }

  public static Player create(int classId, int sex, String accountName, String name, int hairStyle, int hairColor, int face) {
    PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(classId, sex != 0);
    Player player = new Player(IdFactory.getInstance().getNextId(), template, accountName);
    player.setName(name);
    player.setTitle("");
    player.setHairStyle(hairStyle);
    player.setHairColor(hairColor);
    player.setFace(face);
    player.setCreateTime(System.currentTimeMillis());
    return !CharacterDAO.getInstance().insert(player) ? null : player;
  }

  public static Player restore(int objectId) {
    Player player = null;
    Connection con = null;
    Statement statement = null;
    Statement statement2 = null;
    PreparedStatement statement3 = null;
    ResultSet rset = null;
    ResultSet rset2 = null;
    ResultSet rset3 = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.createStatement();
      statement2 = con.createStatement();
      rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1");
      rset2 = statement2.executeQuery("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `isBase`=1 LIMIT 1");
      if (rset.next() && rset2.next()) {
        int classId = rset2.getInt("class_id");
        boolean female = rset.getInt("sex") == 1;
        PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(classId, female);
        player = new Player(objectId, template);
        CharacterVariablesDAO.getInstance().loadVariables(objectId, player.getVars());
        player.loadInstanceReuses();
        player.loadPremiumItemList();
        player._friendList.restore();
        player._postFriends = CharacterPostFriendDAO.getInstance().select(player);
        CharacterGroupReuseDAO.getInstance().select(player);
        player.setBaseClass(classId);
        player._login = rset.getString("account_name");
        String name = rset.getString("char_name");
        player.setName(name);
        player.setFace(rset.getInt("face"));
        player.setHairStyle(rset.getInt("hairStyle"));
        player.setHairColor(rset.getInt("hairColor"));
        player.setHeading(0);
        player.setKarma(rset.getInt("karma"));
        player.setPvpKills(rset.getInt("pvpkills"));
        player.setPkKills(rset.getInt("pkkills"));
        player.setLeaveClanTime(rset.getLong("leaveclan") * 1000L);
        if (player.getLeaveClanTime() > 0L && player.canJoinClan()) {
          player.setLeaveClanTime(0L);
        }

        player.setDeleteClanTime(rset.getLong("deleteclan") * 1000L);
        if (player.getDeleteClanTime() > 0L && player.canCreateClan()) {
          player.setDeleteClanTime(0L);
        }

        player.setNoChannel(rset.getLong("nochannel") * 1000L);
        if (player.getNoChannel() > 0L && player.getNoChannelRemained() < 0L) {
          player.setNoChannel(0L);
        }

        player.setOnlineTime(rset.getLong("onlinetime") * 1000L);
        int clanId = rset.getInt("clanid");
        if (clanId > 0) {
          player.setClan(ClanTable.getInstance().getClan(clanId));
          player.setPledgeType(rset.getInt("pledge_type"));
          player.setPowerGrade(rset.getInt("pledge_rank"));
          player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
          player.setApprentice(rset.getInt("apprentice"));
        }

        player.setCreateTime(rset.getLong("createtime") * 1000L);
        player.setDeleteTimer(rset.getInt("deletetime"));
        player.setTitle(rset.getString("title"));
        if (player.getVar("titlecolor") != null) {
          player.setTitleColor(Integer.decode("0x" + player.getVar("titlecolor")));
        }

        if (player.getVar("namecolor") == null) {
          if (player.isGM()) {
            player.setNameColor(Config.GM_NAME_COLOUR);
          } else if (player.getClan() != null && player.getClan().getLeaderId() == player.getObjectId()) {
            player.setNameColor(Config.CLANLEADER_NAME_COLOUR);
          } else {
            player.setNameColor(Config.NORMAL_NAME_COLOUR);
          }
        } else {
          player.setNameColor(Integer.decode("0x" + player.getVar("namecolor")));
        }

        if (Config.AUTO_LOOT_INDIVIDUAL) {
          player._autoLoot = player.getVarB("AutoLoot", Config.AUTO_LOOT);
          player.AutoLootHerbs = player.getVarB("AutoLootHerbs", Config.AUTO_LOOT_HERBS);
        }

        player.setFistsWeaponItem(player.findFistsWeaponItem(classId));
        player.setUptime(System.currentTimeMillis());
        player.setLastAccess(rset.getLong("lastAccess"));
        int givableRecs = rset.getInt("rec_left");
        int receibedRecs = rset.getInt("rec_have");
        player.setKeyBindings(rset.getBytes("key_bindings"));
        player.setPcBangPoints(rset.getInt("pcBangPoints"));
        player.restoreRecipeBook();
        boolean removeHeroSkills = false;
        player.setNoble(NoblesController.getInstance().isNobles(player));
        if (Config.OLY_ENABLED) {
          player.setHero(HeroController.getInstance().isCurrentHero(player));
          if (player.isHero()) {
            HeroController.getInstance().loadDiary(player.getObjectId());
          }

          if (Config.ALT_ALLOW_CUSTOM_HERO && !player.isHero() && player.getVar("CustomHeroEndTime") != null) {
            long customHeroEndTime = player.getVarLong("CustomHeroEndTime", 0L);
            long customHeroLeftTimeSec = customHeroEndTime - System.currentTimeMillis() / 1000L;
            if (customHeroLeftTimeSec > 0L) {
              player.setCustomHero(true, customHeroLeftTimeSec, false);
            } else {
              player.setCustomHero(false, 0L, false);
              removeHeroSkills = true;
            }
          }
        }

        player.updatePledgeClass();
        int reflection = 0;
        String var;
        String charName;
        if (player.getVar("jailed") != null && System.currentTimeMillis() / 1000L < (long)(Integer.parseInt(player.getVar("jailed")) + 60)) {
          player.setXYZ(-114648, -249384, -2984);
          player.sitDown(null);
          player.block();
          player._unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), (long)Integer.parseInt(player.getVar("jailed")) * 1000L);
        } else {
          player.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
          var = player.getVar("reflection");
          if (var != null) {
            reflection = Integer.parseInt(var);
            if (reflection > 0) {
              charName = player.getVar("backCoords");
              if (charName != null) {
                player.setLoc(Location.parseLoc(charName));
                player.unsetVar("backCoords");
              }

              reflection = 0;
            }
          }
        }

        player.setReflection(reflection);
        EventHolder.getInstance().findEvent(player);
        Quest.restoreQuestStates(player);
        player.getInventory().restore();
        restoreCharSubClasses(player);
        player.restoreRecommendedCharacters();
        player.restoreGivableAndReceivedRec(givableRecs, receibedRecs);

        try {
          var = player.getVar("ExpandInventory");
          if (var != null) {
            player.setExpandInventory(Integer.parseInt(var));
          }
        } catch (Exception var35) {
          log.error("", var35);
        }

        try {
          var = player.getVar("ExpandWarehouse");
          if (var != null) {
            player.setExpandWarehouse(Integer.parseInt(var));
          }
        } catch (Exception var34) {
          log.error("", var34);
        }

        try {
          var = player.getVar("buffAnimRange");
          if (var != null) {
            player.setBuffAnimRange(Integer.parseInt(var));
          }
        } catch (Exception var33) {
          log.error("", var33);
        }

        try {
          var = player.getVar("notraders");
          if (var != null) {
            player.setNotShowTraders(Boolean.parseBoolean(var));
          }
        } catch (Exception var32) {
          log.error("", var32);
        }

        try {
          var = player.getVar("pet");
          if (var != null) {
            player.setPetControlItem(Integer.parseInt(var));
          }
        } catch (Exception var31) {
          log.error("", var31);
        }

        statement3 = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id!=?");
        statement3.setString(1, player._login);
        statement3.setInt(2, objectId);
        rset3 = statement3.executeQuery();

        while(rset3.next()) {
          Integer charId = rset3.getInt("obj_Id");
          charName = rset3.getString("char_name");
          player._chars.put(charId, charName);
        }

        DbUtils.close(statement3, rset3);
        if (removeHeroSkills) {
          HeroController.removeSkills(player);
        }

        LazyArrayList<Zone> zones = LazyArrayList.newInstance();
        World.getZones(zones, player.getLoc(), player.getReflection());
        if (!zones.isEmpty()) {

          for (Zone zone : zones) {
            if (zone.getType() == ZoneType.no_restart) {
              if (System.currentTimeMillis() / 1000L - player.getLastAccess() > zone.getRestartTime()) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.EnterWorld.TeleportedReasonNoRestart", player));
                player.setLoc(TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE));
              }
            } else if (zone.getType() == ZoneType.SIEGE) {
              SiegeEvent<?, ?> siegeEvent = (SiegeEvent) player.getEvent(SiegeEvent.class);
              if (siegeEvent != null) {
                player.setLoc(siegeEvent.getEnterLoc(player));
              } else {
                Residence r = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence"));
                player.setLoc(r.getNotOwnerRestartPoint(player));
              }
            }
          }
        }

        LazyArrayList.recycle(zones);
        if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getLoc(), false)) {
          player.setLoc(DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords());
        }

        player.restoreBlockList();
        player._macroses.restore();
        player.refreshExpertisePenalty();
        player.refreshOverloaded();
        player.getWarehouse().restore();
        player.getFreight().restore();
        player.restoreTradeList();
        if (player.getVar("storemode") != null) {
          player.setPrivateStoreType(Integer.parseInt(player.getVar("storemode")));
          player.setSitting(true);
        }

        player.updateKetraVarka();
        player.updateRam();
        if (player.getVar("lang@") == null) {
          player.setVar("lang@", Config.DEFAULT_LANG, -1L);
        }

        if (Config.SERVICES_ENABLE_NO_CARRIER && player.getVar("noCarrier") == null) {
          player.setVar("noCarrier", Config.SERVICES_NO_CARRIER_DEFAULT_TIME, -1L);
        }
      }
    } catch (Exception var36) {
      log.error("Could not restore char data!", var36);
    } finally {
      DbUtils.closeQuietly(statement2, rset2);
      DbUtils.closeQuietly(statement3, rset3);
      DbUtils.closeQuietly(con, statement, rset);
    }

    return player;
  }

  private void loadPremiumItemList() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?");
      statement.setInt(1, this.getObjectId());
      rs = statement.executeQuery();

      while(rs.next()) {
        int itemNum = rs.getInt("itemNum");
        int itemId = rs.getInt("itemId");
        long itemCount = rs.getLong("itemCount");
        String itemSender = rs.getString("itemSender");
        PremiumItem item = new PremiumItem(itemId, itemCount, itemSender);
        this._premiumItems.put(itemNum, item);
      }
    } catch (Exception var13) {
      log.error("", var13);
    } finally {
      DbUtils.closeQuietly(con, statement, rs);
    }

  }

  public void updatePremiumItem(int itemNum, long newcount) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=?");
      statement.setLong(1, newcount);
      statement.setInt(2, this.getObjectId());
      statement.setInt(3, itemNum);
      statement.execute();
    } catch (Exception var10) {
      log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void deletePremiumItem(int itemNum) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND itemNum=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, itemNum);
      statement.execute();
    } catch (Exception var8) {
      log.error("", var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public Map<Integer, PremiumItem> getPremiumItemList() {
    return this._premiumItems;
  }

  public void store(boolean fast) {
    if (this._storeLock.tryLock()) {
      try {
        Connection con = null;
        PreparedStatement statement = null;

        try {
          con = DatabaseFactory.getInstance().getConnection();
          statement = con.prepareStatement("UPDATE characters SET face=?,hairStyle=?,hairColor=?,x=?,y=?,z=?,karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,rec_bonus_time=?,hunting_bonus_time=?,rec_tick_cnt=?,hunting_bonus=?,clanid=?,deletetime=?,title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?,onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,bookmarks=? WHERE obj_Id=? LIMIT 1");
          statement.setInt(1, this.getFace());
          statement.setInt(2, this.getHairStyle());
          statement.setInt(3, this.getHairColor());
          if (this._stablePoint == null) {
            statement.setInt(4, this.getX());
            statement.setInt(5, this.getY());
            statement.setInt(6, this.getZ());
          } else {
            statement.setInt(4, this._stablePoint.x);
            statement.setInt(5, this._stablePoint.y);
            statement.setInt(6, this._stablePoint.z);
          }

          statement.setInt(7, this.getKarma());
          statement.setInt(8, this.getPvpKills());
          statement.setInt(9, this.getPkKills());
          statement.setInt(10, this.getReceivedRec());
          statement.setInt(11, this.getGivableRec());
          statement.setInt(12, 0);
          statement.setInt(13, 0);
          statement.setInt(14, 0);
          statement.setInt(15, 0);
          statement.setInt(16, this.getClanId());
          statement.setInt(17, this.getDeleteTimer());
          statement.setString(18, this._title);
          statement.setInt(19, this._accessLevel);
          statement.setInt(20, this.isOnline() && !this.isInOfflineMode() ? 1 : 0);
          statement.setLong(21, this.getLeaveClanTime() / 1000L);
          statement.setLong(22, this.getDeleteClanTime() / 1000L);
          statement.setLong(23, this._NoChannel > 0L ? this.getNoChannelRemained() / 1000L : this._NoChannel);
          statement.setInt(24, (int)(this._onlineBeginTime > 0L ? (this._onlineTime + System.currentTimeMillis() - this._onlineBeginTime) / 1000L : this._onlineTime / 1000L));
          statement.setInt(25, this.getPledgeType());
          statement.setInt(26, this.getPowerGrade());
          statement.setInt(27, this.getLvlJoinedAcademy());
          statement.setInt(28, this.getApprentice());
          statement.setBytes(29, this.getKeyBindings());
          statement.setInt(30, this.getPcBangPoints());
          statement.setString(31, this.getName());
          statement.setInt(32, 0);
          statement.setInt(33, this.getObjectId());
          statement.executeUpdate();
          GameStats.increaseUpdatePlayerBase();
          if (!fast) {
            EffectsDAO.getInstance().insert(this);
            CharacterGroupReuseDAO.getInstance().insert(this);
            this.storeDisableSkills();
            this.storeBlockList();
          }

          this.storeCharSubClasses();
          this.storeRecommendedCharacters();
        } catch (Exception var13) {
          log.error("Could not store char data: " + this + "!", var13);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }
      } finally {
        this._storeLock.unlock();
      }

    }
  }

  public Skill addSkill(Skill newSkill, boolean store) {
    if (newSkill == null) {
      return null;
    } else {
      Skill oldSkill = super.addSkill(newSkill);
      if (newSkill.equals(oldSkill)) {
        return oldSkill;
      } else {
        if (store) {
          this.storeSkill(newSkill, oldSkill);
        }

        return oldSkill;
      }
    }
  }

  public Skill removeSkill(Skill skill, boolean fromDB) {
    return skill == null ? null : this.removeSkill(skill.getId(), fromDB);
  }

  public Skill removeSkill(int id, boolean fromDB) {
    Skill oldSkill = super.removeSkillById(id);
    if (!fromDB) {
      return oldSkill;
    } else {
      if (oldSkill != null) {
        Connection con = null;
        PreparedStatement statement = null;

        try {
          con = DatabaseFactory.getInstance().getConnection();
          statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?");
          statement.setInt(1, oldSkill.getId());
          statement.setInt(2, this.getObjectId());
          statement.setInt(3, this.getActiveClassId());
          statement.execute();
        } catch (Exception var10) {
          log.error("Could not delete skill!", var10);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }
      }

      return oldSkill;
    }
  }

  private void storeSkill(Skill newSkill, Skill oldSkill) {
    if (newSkill == null) {
      log.warn("could not store new skill. its NULL");
    } else {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,?)");
        statement.setInt(1, this.getObjectId());
        statement.setInt(2, newSkill.getId());
        statement.setInt(3, newSkill.getLevel());
        statement.setInt(4, this.getActiveClassId());
        statement.execute();
      } catch (Exception var9) {
        log.error("Error could not store skills!", var9);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }

  private void restoreSkills() {
    this.restoreSkills(this.getActiveClassId());
  }

  private void restoreSkills(int classId) {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, classId);
      rset = statement.executeQuery();

      while(true) {
        while(true) {
          Skill skill;
          do {
            if (!rset.next()) {
              if (this.getActiveClassId() != classId) {
                return;
              }

              if (this.isNoble()) {
                this.updateNobleSkills();
              }

              if (this._hero && (this.getBaseClassId() == this.getActiveClassId() || Config.ALT_ALLOW_HERO_SKILLS_ON_SUB_CLASS)) {
                HeroController.addSkills(this);
              }

              if (this._clan != null) {
                this._clan.addSkillsQuietly(this);
                if (this._clan.getLeaderId() == this.getObjectId() && this._clan.getLevel() >= 5) {
                  SiegeUtils.addSiegeSkills(this);
                }
              }

              ClassId activeClassId = null;
              ClassId[] var16 = ClassId.VALUES;
              int var17 = var16.length;

              for (ClassId clsId : var16) {
                if (clsId.getId() == this.getActiveClassId()) {
                  activeClassId = clsId;
                }
              }

              switch(activeClassId) {
                case dwarvenFighter:
                case scavenger:
                case bountyHunter:
                case artisan:
                case warsmith:
                case fortuneSeeker:
                case maestro:
                  this.addSkill(SkillTable.getInstance().getInfo(1321, 1));
                default:
                  this.addSkill(SkillTable.getInstance().getInfo(1322, 1));
                  if (Config.UNSTUCK_SKILL && this.getSkillLevel(1050) < 0) {
                    this.addSkill(SkillTable.getInstance().getInfo(2099, 1));
                  }

                  if (Config.BLOCK_BUFF_SKILL) {
                    this.addSkill(SkillTable.getInstance().getInfo(5088, 1));
                  }

                  if (Config.NOBLES_BUFF_SKILL) {
                    this.addSkill(SkillTable.getInstance().getInfo(1323, 1));
                  }

                  return;
              }
            }

            int id = rset.getInt("skill_id");
            int level = rset.getInt("skill_level");
            skill = SkillTable.getInstance().getInfo(id, level);
          } while(skill == null);

          if (!this.isGM() && !Config.ALT_WEAK_SKILL_LEARN && !SkillAcquireHolder.getInstance().isSkillPossible(this, skill)) {
            log.warn("Skill " + skill.toString() + " not possible for player " + this.toString() + " with classId " + this.getActiveClassId());
            this.removeSkill(skill, true);
            this.removeSkillFromShortCut(skill.getId());
          } else {
            super.addSkill(skill);
          }
        }
      }
    } catch (Exception var13) {
      log.warn("Could not restore skills for player objId: " + this.getObjectId());
      log.error("", var13);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public void storeDisableSkills() {
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      pstmt = conn.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id = ? AND (class_index=? OR class_index=-1) AND `end_time` < ?");
      pstmt.setInt(1, this.getObjectId());
      pstmt.setInt(2, this.getActiveClassId());
      pstmt.setLong(3, System.currentTimeMillis());
      pstmt.executeUpdate();
      DbUtils.close(pstmt);
      if (!this._skillReuses.isEmpty()) {
        pstmt = conn.prepareStatement("REPLACE INTO `character_skills_save`(`char_obj_id`, `skill_id`, `skill_level`, `class_index`, `end_time`, `reuse_delay_org`) VALUES\t(?,?,?,?,?,?)");
        CHashIntObjectMap<TimeStamp> skillReuses = new CHashIntObjectMap();
        synchronized(this._skillReuses) {
          skillReuses.putAll(this._skillReuses);
        }

        for (TimeStamp timeStamp : skillReuses.values()) {
          Skill skill = SkillTable.getInstance().getInfo(timeStamp.getId(), timeStamp.getLevel());
          if (skill != null) {
            pstmt.setInt(1, this.getObjectId());
            pstmt.setInt(2, skill.getId());
            pstmt.setInt(3, skill.getLevel());
            pstmt.setInt(4, !skill.isSharedClassReuse() ? this.getActiveClassId() : -1);
            pstmt.setLong(5, timeStamp.getEndTime());
            pstmt.setLong(6, timeStamp.getReuseBasic());
            pstmt.executeUpdate();
          }
        }

        return;
      }
    } catch (Exception var12) {
      log.warn("Could not store disable skills data: " + var12);
      return;
    } finally {
      DbUtils.closeQuietly(conn, pstmt);
    }

  }

  public void restoreDisableSkills() {
    this._skillReuses.clear();
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;

    try {
      conn = DatabaseFactory.getInstance().getConnection();
      pstmt = conn.prepareStatement("SELECT skill_id, skill_level, end_time, reuse_delay_org FROM character_skills_save WHERE char_obj_id=? AND (class_index=? OR class_index=-1)");
      pstmt.setInt(1, this.getObjectId());
      pstmt.setInt(2, this.getActiveClassId());
      rset = pstmt.executeQuery();

      while(rset.next()) {
        int skillId = rset.getInt("skill_id");
        int skillLevel = rset.getInt("skill_level");
        long endTime = rset.getLong("end_time");
        long rDelayOrg = rset.getLong("reuse_delay_org");
        long curTime = System.currentTimeMillis();
        Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill != null && endTime - curTime > 500L) {
          this._skillReuses.put(skill.hashCode(), new TimeStamp(skill, endTime, rDelayOrg));
        }
      }

      DbUtils.close(pstmt);
      pstmt = conn.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id = ? AND (class_index=? OR class_index=-1) AND `end_time` < ?");
      pstmt.setInt(1, this.getObjectId());
      pstmt.setInt(2, this.getActiveClassId());
      pstmt.setLong(3, System.currentTimeMillis());
      pstmt.executeUpdate();
    } catch (Exception var16) {
      log.error("Could not restore active skills data!", var16);
    } finally {
      DbUtils.closeQuietly(conn, pstmt, rset);
    }

  }

  private void restoreHenna() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("select slot, symbol_id from character_hennas where char_obj_id=? AND class_index=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, this.getActiveClassId());
      rset = statement.executeQuery();

      int slot;
      for(slot = 0; slot < 3; ++slot) {
        this._henna[slot] = null;
      }

      while(rset.next()) {
        slot = rset.getInt("slot");
        if (slot >= 1 && slot <= 3) {
          int symbol_id = rset.getInt("symbol_id");
          if (symbol_id != 0) {
            Henna tpl = HennaHolder.getInstance().getHenna(symbol_id);
            if (tpl != null) {
              this._henna[slot - 1] = tpl;
            }
          }
        }
      }
    } catch (Exception var10) {
      log.warn("could not restore henna: " + var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    this.recalcHennaStats();
  }

  public int getHennaEmptySlots() {
    int totalSlots = 1 + this.getClassId().level();

    for(int i = 0; i < 3; ++i) {
      if (this._henna[i] != null) {
        --totalSlots;
      }
    }

    return Math.max(totalSlots, 0);
  }

  public boolean removeHenna(int slot) {
    if (slot >= 1 && slot <= 3) {
      --slot;
      if (this._henna[slot] == null) {
        return false;
      } else {
        Henna henna = this._henna[slot];
        int dyeID = henna.getDyeId();
        this._henna[slot] = null;
        Connection con = null;
        PreparedStatement statement = null;

        try {
          con = DatabaseFactory.getInstance().getConnection();
          statement = con.prepareStatement("DELETE FROM character_hennas where char_obj_id=? and slot=? and class_index=?");
          statement.setInt(1, this.getObjectId());
          statement.setInt(2, slot + 1);
          statement.setInt(3, this.getActiveClassId());
          statement.execute();
        } catch (Exception var10) {
          log.warn("could not remove char henna: " + var10, var10);
        } finally {
          DbUtils.closeQuietly(con, statement);
        }

        this.recalcHennaStats();
        this.sendPacket(new HennaInfo(this));
        this.sendUserInfo(true);
        ItemFunctions.addItem(this, dyeID, henna.getDrawCount() / 2L, true);
        return true;
      }
    } else {
      return false;
    }
  }

  public boolean addHenna(Henna henna) {
    if (this.getHennaEmptySlots() == 0) {
      this.sendPacket(SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
      return false;
    } else {
      for(int i = 0; i < 3; ++i) {
        if (this._henna[i] == null) {
          this._henna[i] = henna;
          this.recalcHennaStats();
          Connection con = null;
          PreparedStatement statement = null;

          try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO `character_hennas` (char_obj_id, symbol_id, slot, class_index) VALUES (?,?,?,?)");
            statement.setInt(1, this.getObjectId());
            statement.setInt(2, henna.getSymbolId());
            statement.setInt(3, i + 1);
            statement.setInt(4, this.getActiveClassId());
            statement.execute();
          } catch (Exception var9) {
            log.warn("could not save char henna: " + var9);
          } finally {
            DbUtils.closeQuietly(con, statement);
          }

          this.sendPacket(new HennaInfo(this));
          this.sendUserInfo(true);
          return true;
        }
      }

      return false;
    }
  }

  private void recalcHennaStats() {
    this._hennaINT = 0;
    this._hennaSTR = 0;
    this._hennaCON = 0;
    this._hennaMEN = 0;
    this._hennaWIT = 0;
    this._hennaDEX = 0;

    for(int i = 0; i < 3; ++i) {
      Henna henna = this._henna[i];
      if (henna != null && henna.isForThisClass(this)) {
        this._hennaINT += henna.getStatINT();
        this._hennaSTR += henna.getStatSTR();
        this._hennaMEN += henna.getStatMEN();
        this._hennaCON += henna.getStatCON();
        this._hennaWIT += henna.getStatWIT();
        this._hennaDEX += henna.getStatDEX();
      }
    }

    if (this._hennaINT > 5) {
      this._hennaINT = 5;
    }

    if (this._hennaSTR > 5) {
      this._hennaSTR = 5;
    }

    if (this._hennaMEN > 5) {
      this._hennaMEN = 5;
    }

    if (this._hennaCON > 5) {
      this._hennaCON = 5;
    }

    if (this._hennaWIT > 5) {
      this._hennaWIT = 5;
    }

    if (this._hennaDEX > 5) {
      this._hennaDEX = 5;
    }

  }

  public Henna getHenna(int slot) {
    return slot >= 1 && slot <= 3 ? this._henna[slot - 1] : null;
  }

  public int getHennaStatINT() {
    return this._hennaINT;
  }

  public int getHennaStatSTR() {
    return this._hennaSTR;
  }

  public int getHennaStatCON() {
    return this._hennaCON;
  }

  public int getHennaStatMEN() {
    return this._hennaMEN;
  }

  public int getHennaStatWIT() {
    return this._hennaWIT;
  }

  public int getHennaStatDEX() {
    return this._hennaDEX;
  }

  public boolean consumeItem(int itemConsumeId, long itemCount) {
    if (this.getInventory().destroyItemByItemId(itemConsumeId, itemCount)) {
      this.sendPacket(SystemMessage2.removeItems(itemConsumeId, itemCount));
      return true;
    } else {
      return false;
    }
  }

  public boolean consumeItemMp(int itemId, int mp) {
    ItemInstance[] var3 = this.getInventory().getPaperdollItems();
    int var4 = var3.length;

    for (ItemInstance item : var3) {
      if (item != null && item.getItemId() == itemId) {
        int newMp = item.getDuration() - mp;
        if (newMp >= 0) {
          item.setDuration(newMp);
          this.sendPacket((new InventoryUpdate()).addModifiedItem(item));
          return true;
        }
        break;
      }
    }

    return false;
  }

  public boolean isMageClass() {
    ClassId classId = this.getClassId();
    return classId.isMage();
  }

  public boolean isMounted() {
    return this._mountNpcId > 0;
  }

  public final boolean isRiding() {
    return this._riding;
  }

  public final void setRiding(boolean mode) {
    this._riding = mode;
  }

  public boolean checkLandingState() {
    if (this.isInZone(ZoneType.no_landing)) {
      return false;
    } else {
      SiegeEvent<?, ?> siege = (SiegeEvent)this.getEvent(SiegeEvent.class);
      if (siege != null) {
        Residence unit = siege.getResidence();
        return unit != null && this.getClan() != null && this.isClanLeader() && this.getClan().getCastle() == unit.getId();
      } else {
        return true;
      }
    }
  }

  public void setMount(int npcId, int obj_id, int level) {
    if (!this.isCursedWeaponEquipped()) {
      switch(npcId) {
        case 0:
          this.setFlying(false);
          this.setRiding(false);
          if (this.getTransformation() > 0) {
            this.setTransformation(0);
          }

          this.removeSkillById(4289);
          this.getEffectList().stopEffect(4258);
          break;
        case 12526:
        case 12527:
        case 12528:
        case 16038:
        case 16039:
        case 16040:
        case 16068:
          this.setRiding(true);
          break;
        case 12621:
          this.setFlying(true);
          this.setLoc(this.getLoc().changeZ(32));
          this.addSkill(SkillTable.getInstance().getInfo(4289, 1), false);
          break;
        case 16037:
        case 16041:
        case 16042:
          this.setRiding(true);
      }

      if (npcId > 0) {
        this.unEquipWeapon();
      }

      this._mountNpcId = npcId;
      this._mountObjId = obj_id;
      this._mountLevel = level;
      this.broadcastUserInfo(true);
      this.broadcastPacket(new Ride(this));
      this.broadcastUserInfo(true);
      this.sendPacket(new SkillList(this));
    }
  }

  public void unEquipWeapon() {
    ItemInstance wpn = this.getSecondaryWeaponInstance();
    if (wpn != null) {
      this.sendDisarmMessage(wpn);
      this.getInventory().unEquipItem(wpn);
    }

    wpn = this.getActiveWeaponInstance();
    if (wpn != null) {
      this.sendDisarmMessage(wpn);
      this.getInventory().unEquipItem(wpn);
    }

    this.abortAttack(true, true);
    this.abortCast(true, true);
  }

  public int getSpeed(int baseSpeed) {
    if (this.isMounted()) {
      PetData petData = PetDataTable.getInstance().getInfo(this._mountNpcId, this._mountLevel);
      int speed = 187;
      if (petData != null) {
        speed = petData.getSpeed();
      }

      double mod = 1.0D;
      int level = this.getLevel();
      if (this._mountLevel > level && level - this._mountLevel > 10) {
        mod = 0.5D;
      }

      baseSpeed = (int)(mod * (double)speed);
    }

    return super.getSpeed(baseSpeed);
  }

  public int getMountNpcId() {
    return this._mountNpcId;
  }

  public int getMountObjId() {
    return this._mountObjId;
  }

  public int getMountLevel() {
    return this._mountLevel;
  }

  public void sendDisarmMessage(ItemInstance wpn) {
    SystemMessage sm;
    if (wpn.getEnchantLevel() > 0) {
      sm = new SystemMessage(1064);
      sm.addNumber(wpn.getEnchantLevel());
      sm.addItemName(wpn.getItemId());
      this.sendPacket(sm);
    } else {
      sm = new SystemMessage(417);
      sm.addItemName(wpn.getItemId());
      this.sendPacket(sm);
    }

  }

  public void setUsingWarehouseType(WarehouseType type) {
    this._usingWHType = type;
  }

  public WarehouseType getUsingWarehouseType() {
    return this._usingWHType;
  }

  public Collection<EffectCubic> getCubics() {
    return (Collection)(this._cubics == null ? Collections.emptyList() : this._cubics.values());
  }

  public void addCubic(EffectCubic cubic) {
    if (this._cubics == null) {
      this._cubics = new ConcurrentHashMap(3);
    }

    this._cubics.put(cubic.getId(), cubic);
  }

  public void removeCubic(int id) {
    if (this._cubics != null) {
      this._cubics.remove(id);
    }

  }

  public EffectCubic getCubic(int id) {
    return this._cubics == null ? null : this._cubics.get(id);
  }

  public String toString() {
    return this.getName() + "[" + this.getObjectId() + "]";
  }

  public int getEnchantEffect() {
    ItemInstance wpn = this.getActiveWeaponInstance();
    return wpn == null ? 0 : Math.min(127, wpn.getEnchantLevel());
  }

  public void setLastNpc(NpcInstance npc) {
    if (npc == null) {
      this._lastNpc = HardReferences.emptyRef();
    } else {
      this._lastNpc = (HardReference<NpcInstance>) npc.getRef();
    }

  }

  public NpcInstance getLastNpc() {
    return this._lastNpc.get();
  }

  public void setMultisell(MultiSellListContainer multisell) {
    this._multisell = multisell;
  }

  public MultiSellListContainer getMultisell() {
    return this._multisell;
  }

  public boolean isFestivalParticipant() {
    return this.getReflection() instanceof DarknessFestival;
  }

  public boolean unChargeShots(boolean spirit) {
    ItemInstance weapon = this.getActiveWeaponInstance();
    if (weapon == null) {
      return false;
    } else {
      if (spirit) {
        weapon.setChargedSpiritshot(0);
      } else {
        weapon.setChargedSoulshot(0);
      }

      this.autoShot();
      return true;
    }
  }

  public boolean unChargeFishShot() {
    ItemInstance weapon = this.getActiveWeaponInstance();
    if (weapon == null) {
      return false;
    } else {
      weapon.setChargedFishshot(false);
      this.autoShot();
      return true;
    }
  }

  public void autoShot() {

    for (Integer shotId : this._activeSoulShots) {
      ItemInstance item = this.getInventory().getItemByItemId(shotId);
      if (item == null) {
        this.removeAutoSoulShot(shotId);
      } else if (item.getTemplate().testCondition(this, item, false)) {
        IItemHandler handler = item.getTemplate().getHandler();
        if (handler != null) {
          handler.useItem(this, item, false);
        }
      }
    }

  }

  public boolean getChargedFishShot() {
    ItemInstance weapon = this.getActiveWeaponInstance();
    return weapon != null && weapon.getChargedFishshot();
  }

  public boolean getChargedSoulShot() {
    ItemInstance weapon = this.getActiveWeaponInstance();
    return weapon != null && weapon.getChargedSoulshot() == 1;
  }

  public int getChargedSpiritShot() {
    ItemInstance weapon = this.getActiveWeaponInstance();
    return weapon == null ? 0 : weapon.getChargedSpiritshot();
  }

  public void addAutoSoulShot(Integer itemId) {
    this._activeSoulShots.add(itemId);
  }

  public void removeAutoSoulShot(Integer itemId) {
    this._activeSoulShots.remove(itemId);
  }

  public Set<Integer> getAutoSoulShot() {
    return this._activeSoulShots;
  }

  public void setInvisibleType(InvisibleType vis) {
    this._invisibleType = vis;
  }

  public InvisibleType getInvisibleType() {
    return this._invisibleType;
  }

  public int getClanPrivileges() {
    if (this._clan == null) {
      return 0;
    } else if (this.isClanLeader()) {
      return 8388606;
    } else if (this._powerGrade >= 1 && this._powerGrade <= 9) {
      RankPrivs privs = this._clan.getRankPrivs(this._powerGrade);
      return privs != null ? privs.getPrivs() : 0;
    } else {
      return 0;
    }
  }

  public void teleToClosestTown() {
    this.teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE), ReflectionManager.DEFAULT);
  }

  public void teleToCastle() {
    this.teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CASTLE), ReflectionManager.DEFAULT);
  }

  public void teleToClanhall() {
    this.teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CLANHALL), ReflectionManager.DEFAULT);
  }

  public void sendMessage(CustomMessage message) {
    this.sendMessage(message.toString());
  }

  public void teleToLocation(int x, int y, int z, int refId) {
    if (!this.isDeleted()) {
      super.teleToLocation(x, y, z, refId);
    }
  }

  public boolean onTeleported() {
    if (!super.onTeleported()) {
      return false;
    } else {
      if (this.isFakeDeath()) {
        this.breakFakeDeath();
      }

      if (this.isInBoat()) {
        this.setLoc(this.getBoat().getLoc());
      }

      this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
      this.spawnMe();
      this.setLastClientPosition(this.getLoc());
      this.setLastServerPosition(this.getLoc());
      if (this.isPendingRevive()) {
        this.doRevive();
      }

      this.sendActionFailed();
      this.getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);
      if (this.isLockedTarget() && this.getTarget() != null) {
        this.sendPacket(new MyTargetSelected(this.getTarget().getObjectId(), 0));
      }

      this.sendUserInfo(true);
      if (this.getPet() != null) {
        this.getPet().teleportToOwner();
      }

      if (Config.ALT_TELEPORT_PROTECTION && !this.isInZone(ZoneType.peace_zone) && !this.isInZone(ZoneType.SIEGE) && !this.isInZone(ZoneType.offshore) && !this.isOlyParticipant()) {
        this.setAfterTeleportPortectionTime(System.currentTimeMillis() + 1000L * Config.ALT_TELEPORT_PROTECTION_TIME);
        this.sendMessage(new CustomMessage("alt.teleport_protect", this, Config.ALT_TELEPORT_PROTECTION_TIME));
      }

      return true;
    }
  }

  public boolean enterObserverMode(Location loc) {
    WorldRegion observerRegion = World.getRegion(loc);
    if (observerRegion == null) {
      return false;
    } else if (!this._observerMode.compareAndSet(0, 1)) {
      return false;
    } else {
      this.setTarget(null);
      this.stopMove();
      this.sitDown(null);
      this.setFlying(true);
      World.removeObjectsFromPlayer(this);
      this.setObserverRegion(observerRegion);
      this.broadcastCharInfo();
      this.sendPacket(new ObserverStart(loc));
      return true;
    }
  }

  public void appearObserverMode() {
    if (this._observerMode.compareAndSet(1, 3)) {
      WorldRegion currentRegion = this.getCurrentRegion();
      WorldRegion observerRegion = this.getObserverRegion();
      if (!observerRegion.equals(currentRegion)) {
        observerRegion.addObject(this);
      }

      World.showObjectsToPlayer(this);
      if (this.isOlyObserver()) {

        for (Player p : this.getOlyObservingStadium().getPlayers()) {
          if (p.isOlyCompetitionStarted()) {
            this.sendPacket(new ExOlympiadUserInfo(p));
          }
        }
      }

    }
  }

  public void leaveObserverMode() {
    if (this._observerMode.compareAndSet(3, 2)) {
      WorldRegion currentRegion = this.getCurrentRegion();
      WorldRegion observerRegion = this.getObserverRegion();
      if (!observerRegion.equals(currentRegion)) {
        observerRegion.removeObject(this);
      }

      World.removeObjectsFromPlayer(this);
      this.setObserverRegion(null);
      this.setTarget(null);
      this.stopMove();
      this.sendPacket(new ObserverEnd(this.getLoc()));
    }
  }

  public void returnFromObserverMode() {
    if (this._observerMode.compareAndSet(2, 0)) {
      this.setLastClientPosition(null);
      this.setLastServerPosition(null);
      this.unblock();
      this.standUp();
      this.setFlying(false);
      this.broadcastCharInfo();
      World.showObjectsToPlayer(this);
    }
  }

  public void enterOlympiadObserverMode(Stadium stadium) {
    WorldRegion observerRegion = World.getRegion(stadium.getObservingLoc());
    if (observerRegion != null && this._olyObserveStadium == null) {
      if (this._observerMode.compareAndSet(0, 1)) {
        this.setTarget(null);
        this.setLastNpc(null);
        this.stopMove();
        this._olyObserveStadium = stadium;
        World.removeObjectsFromPlayer(this);
        this.setObserverRegion(observerRegion);
        this.block();
        this.broadcastCharInfo();
        this.setReflection(stadium);
        this.setLastClientPosition(null);
        this.setLastServerPosition(null);
        this.sendPacket(new ExOlympiadMode(3), new TeleportToLocation(this, stadium.getObservingLoc()));
      }
    }
  }

  public void switchOlympiadObserverArena(Stadium stadium) {
    if (this._olyObserveStadium != null && stadium != this._olyObserveStadium) {
      WorldRegion oldObserverRegion = World.getRegion(this._olyObserveStadium.getObservingLoc());
      if (this._observerMode.compareAndSet(3, 0)) {
        if (oldObserverRegion != null) {
          oldObserverRegion.removeObject(this);
          oldObserverRegion.removeFromPlayers(this);
        }

        this._olyObserveStadium = null;
        World.removeObjectsFromPlayer(this);
        this.sendPacket(new ExOlympiadMode(0));
        this.enterOlympiadObserverMode(stadium);
      }
    }
  }

  public void leaveOlympiadObserverMode() {
    if (this._olyObserveStadium != null) {
      if (this._observerMode.compareAndSet(3, 2)) {
        WorldRegion currentRegion = this.getCurrentRegion();
        WorldRegion observerRegion = this.getObserverRegion();
        if (observerRegion != null && currentRegion != null && !observerRegion.equals(currentRegion)) {
          observerRegion.removeObject(this);
        }

        World.removeObjectsFromPlayer(this);
        this.setObserverRegion(null);
        this._olyObserveStadium = null;
        this.setTarget(null);
        this.stopMove();
        this.sendPacket(new ExOlympiadMode(0));
        this.setReflection(ReflectionManager.DEFAULT);
        this.sendPacket(new TeleportToLocation(this, this.getLoc()));
      }
    }
  }

  public boolean isOlyObserver() {
    return this._olyObserveStadium != null;
  }

  public Stadium getOlyObservingStadium() {
    return this._olyObserveStadium;
  }

  public boolean isInObserverMode() {
    return this._observerMode.get() > 0;
  }

  public int getObserverMode() {
    return this._observerMode.get();
  }

  public Participant getOlyParticipant() {
    return this._olyParticipant;
  }

  public void setOlyParticipant(Participant participant) {
    this._olyParticipant = participant;
  }

  public boolean isOlyParticipant() {
    return this._olyParticipant != null;
  }

  public boolean isOlyCompetitionStarted() {
    return this.isOlyParticipant() && this._olyParticipant.getCompetition().getState() == CompetitionState.PLAYING;
  }

  public boolean isOlyCompetitionStandby() {
    return this.isOlyParticipant() && this._olyParticipant.getCompetition().getState() == CompetitionState.STAND_BY;
  }

  public boolean isOlyCompetitionPreparing() {
    return this.isOlyParticipant() && (this._olyParticipant.getCompetition().getState() == CompetitionState.INIT || this._olyParticipant.getCompetition().getState() == CompetitionState.STAND_BY);
  }

  public boolean isOlyCompetitionFinished() {
    return this.isOlyParticipant() && this._olyParticipant.getCompetition().getState() == CompetitionState.FINISH;
  }

  public boolean isLooseOlyCompetition() {
    if (this.isOlyParticipant()) {
      if (this.isOlyCompetitionFinished()) {
        return !this._olyParticipant.isAlive();
      } else {
        return this._olyParticipant.isPlayerLoose(this);
      }
    } else {
      return false;
    }
  }

  public WorldRegion getObserverRegion() {
    return this._observerRegion;
  }

  public void setObserverRegion(WorldRegion region) {
    this._observerRegion = region;
  }

  public int getTeleMode() {
    return this._telemode;
  }

  public void setTeleMode(int mode) {
    this._telemode = mode;
  }

  public void setLoto(int i, int val) {
    this._loto[i] = val;
  }

  public int getLoto(int i) {
    return this._loto[i];
  }

  public void setRace(int i, int val) {
    this._race[i] = val;
  }

  public int getRace(int i) {
    return this._race[i];
  }

  public boolean getMessageRefusal() {
    return this._messageRefusal;
  }

  public void setMessageRefusal(boolean mode) {
    this._messageRefusal = mode;
  }

  public void setTradeRefusal(boolean mode) {
    this._tradeRefusal = mode;
  }

  public boolean getTradeRefusal() {
    return this._tradeRefusal;
  }

  public void addToBlockList(String charName) {
    if (charName != null && !charName.equalsIgnoreCase(this.getName()) && !this.isInBlockList(charName)) {
      Player block_target = World.getPlayer(charName);
      if (block_target != null) {
        if (block_target.isGM()) {
          this.sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
        } else {
          this._blockList.put(block_target.getObjectId(), block_target.getName());
          this.sendPacket((new SystemMessage(617)).addString(block_target.getName()));
          block_target.sendPacket((new SystemMessage(619)).addString(this.getName()));
        }
      } else {
        int charId = CharacterDAO.getInstance().getObjectIdByName(charName);
        if (charId == 0) {
          this.sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
        } else if (Config.gmlist.containsKey(charId) && Config.gmlist.get(charId).IsGM) {
          this.sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
        } else {
          this._blockList.put(charId, charName);
          this.sendPacket((new SystemMessage(617)).addString(charName));
        }
      }
    } else {
      this.sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
    }
  }

  public void removeFromBlockList(String charName) {
    int charId = 0;

    for (int blockId : this._blockList.keySet()) {
      if (charName.equalsIgnoreCase(this._blockList.get(blockId))) {
        charId = blockId;
        break;
      }
    }

    if (charId == 0) {
      this.sendPacket(Msg.YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_FROM_IGNORE_LIST);
    } else {
      this.sendPacket((new SystemMessage(618)).addString(this._blockList.remove(charId)));
      Player block_target = GameObjectsStorage.getPlayer(charId);
      if (block_target != null) {
        block_target.sendMessage(this.getName() + " has removed you from his/her Ignore List.");
      }

    }
  }

  public boolean isInBlockList(Player player) {
    return this.isInBlockList(player.getObjectId());
  }

  public boolean isInBlockList(int charId) {
    return this._blockList != null && this._blockList.containsKey(charId);
  }

  public boolean isInBlockList(String charName) {
    Iterator var2 = this._blockList.keySet().iterator();

    int blockId;
    do {
      if (!var2.hasNext()) {
        return false;
      }

      blockId = (Integer)var2.next();
    } while(!charName.equalsIgnoreCase(this._blockList.get(blockId)));

    return true;
  }

  private void restoreBlockList() {
    this._blockList.clear();
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT target_Id, char_name FROM character_blocklist LEFT JOIN characters ON ( character_blocklist.target_Id = characters.obj_Id ) WHERE character_blocklist.obj_Id = ?");
      statement.setInt(1, this.getObjectId());
      rs = statement.executeQuery();

      while(rs.next()) {
        int targetId = rs.getInt("target_Id");
        String name = rs.getString("char_name");
        if (name != null) {
          this._blockList.put(targetId, name);
        }
      }
    } catch (SQLException var9) {
      log.warn("Can't restore player blocklist " + var9, var9);
    } finally {
      DbUtils.closeQuietly(con, statement, rs);
    }

  }

  private void storeBlockList() {
    Connection con = null;
    Statement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.createStatement();
      statement.executeUpdate("DELETE FROM character_blocklist WHERE obj_Id=" + this.getObjectId());
      if (!this._blockList.isEmpty()) {
        SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_blocklist` (`obj_Id`,`target_Id`) VALUES");
        synchronized(this._blockList) {
          Iterator var6 = this._blockList.entrySet().iterator();

          while (var6.hasNext()) {

            Entry<Integer, String> e = (Entry) var6.next();
            String sb = "(" + this.getObjectId() + "," +
              e.getKey() + ")";
            b.write(sb);
          }
        }

        if (!b.isEmpty()) {
          statement.executeUpdate(b.close());
        }

        return;
      }
    } catch (Exception var14) {
      log.warn("Can't store player blocklist " + var14);
      return;
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public boolean isBlockAll() {
    return this._blockAll;
  }

  public void setBlockAll(boolean state) {
    this._blockAll = state;
  }

  public Collection<String> getBlockList() {
    return this._blockList.values();
  }

  public Map<Integer, String> getBlockListMap() {
    return this._blockList;
  }

  public void setHero(boolean hero) {
    this._hero = hero;
  }

  private void stopCustomHeroEndTask() {
    if (this._customHeroRemoveTask != null) {
      this._customHeroRemoveTask.cancel(true);
      this._customHeroRemoveTask = null;
    }

  }

  public void setCustomHero(boolean customHero, long customHeroStatusDuration, boolean processSkills) {
    if (!this.isHero() && customHero && customHeroStatusDuration > 0L) {
      this.setVar("CustomHeroEndTime", System.currentTimeMillis() / 1000L + customHeroStatusDuration, -1L);
      this.setHero(true);
      if (processSkills) {
        HeroController.addSkills(this);
      }

      this._customHeroRemoveTask = ThreadPoolManager.getInstance().schedule(new EndCustomHeroTask(this), customHeroStatusDuration * 1000L);
    } else if (!customHero) {
      this.unsetVar("CustomHeroEndTime");
      this.stopCustomHeroEndTask();
      if (HeroController.getInstance().isCurrentHero(this)) {
        return;
      }

      this.setHero(false);
      if (processSkills) {
        HeroController.removeSkills(this);
      }
    }

  }

  public boolean isHero() {
    return this._hero;
  }

  public void updateNobleSkills() {
    if (this.isNoble()) {
      if (this.isClanLeader() && this.getClan().getCastle() > 0) {
        super.addSkill(SkillTable.getInstance().getInfo(327, 1));
      }

      super.addSkill(SkillTable.getInstance().getInfo(325, 1));
      super.addSkill(SkillTable.getInstance().getInfo(1323, 1));
      super.addSkill(SkillTable.getInstance().getInfo(1324, 1));
      super.addSkill(SkillTable.getInstance().getInfo(1325, 1));
      super.addSkill(SkillTable.getInstance().getInfo(1326, 1));
      super.addSkill(SkillTable.getInstance().getInfo(1327, 1));
    } else {
      super.removeSkillById(327);
      super.removeSkillById(325);
      super.removeSkillById(1323);
      super.removeSkillById(1324);
      super.removeSkillById(1325);
      super.removeSkillById(1326);
      super.removeSkillById(1327);
    }

  }

  public void setNoble(boolean noble) {
    this._noble = noble;
    if (noble) {
      this.broadcastPacket(new SocialAction(this.getObjectId(), 16));
    }

  }

  public boolean isNoble() {
    return this._noble;
  }

  public int getSubLevel() {
    return this.isSubClassActive() ? this.getLevel() : 0;
  }

  public void updateKetraVarka() {
    if (ItemFunctions.getItemCount(this, 7215) > 0L) {
      this._ketra = 5;
    } else if (ItemFunctions.getItemCount(this, 7214) > 0L) {
      this._ketra = 4;
    } else if (ItemFunctions.getItemCount(this, 7213) > 0L) {
      this._ketra = 3;
    } else if (ItemFunctions.getItemCount(this, 7212) > 0L) {
      this._ketra = 2;
    } else if (ItemFunctions.getItemCount(this, 7211) > 0L) {
      this._ketra = 1;
    } else if (ItemFunctions.getItemCount(this, 7225) > 0L) {
      this._varka = 5;
    } else if (ItemFunctions.getItemCount(this, 7224) > 0L) {
      this._varka = 4;
    } else if (ItemFunctions.getItemCount(this, 7223) > 0L) {
      this._varka = 3;
    } else if (ItemFunctions.getItemCount(this, 7222) > 0L) {
      this._varka = 2;
    } else if (ItemFunctions.getItemCount(this, 7221) > 0L) {
      this._varka = 1;
    } else {
      this._varka = 0;
      this._ketra = 0;
    }

  }

  public int getVarka() {
    return this._varka;
  }

  public int getKetra() {
    return this._ketra;
  }

  public void updateRam() {
    if (ItemFunctions.getItemCount(this, 7247) > 0L) {
      this._ram = 2;
    } else if (ItemFunctions.getItemCount(this, 7246) > 0L) {
      this._ram = 1;
    } else {
      this._ram = 0;
    }

  }

  public int getRam() {
    return this._ram;
  }

  public void setPledgeType(int typeId) {
    this._pledgeType = typeId;
  }

  public int getPledgeType() {
    return this._pledgeType;
  }

  public void setLvlJoinedAcademy(int lvl) {
    this._lvlJoinedAcademy = lvl;
  }

  public int getLvlJoinedAcademy() {
    return this._lvlJoinedAcademy;
  }

  public int getPledgeClass() {
    return this._pledgeClass;
  }

  public Player.EPledgeRank getPledgeRank() {
    return Player.EPledgeRank.getPledgeRank(this.getPledgeClass());
  }

  public void updatePledgeClass() {
    int CLAN_LEVEL = this._clan == null ? -1 : this._clan.getLevel();
    boolean IN_ACADEMY = this._clan != null && Clan.isAcademy(this._pledgeType);
    boolean IS_GUARD = this._clan != null && Clan.isRoyalGuard(this._pledgeType);
    boolean IS_KNIGHT = this._clan != null && Clan.isOrderOfKnights(this._pledgeType);
    boolean IS_GUARD_CAPTAIN = false;
    boolean IS_KNIGHT_COMMANDER = false;
    boolean IS_LEADER = false;
    SubUnit unit = this.getSubUnit();
    if (unit != null) {
      UnitMember unitMember = unit.getUnitMember(this.getObjectId());
      if (unitMember == null) {
        log.warn("Player: unitMember null, clan: " + this._clan.getClanId() + "; pledgeType: " + unit.getType());
        return;
      }

      IS_GUARD_CAPTAIN = Clan.isRoyalGuard(unitMember.getLeaderOf());
      IS_KNIGHT_COMMANDER = Clan.isOrderOfKnights(unitMember.getLeaderOf());
      IS_LEADER = unitMember.getLeaderOf() == 0;
    }

    switch(CLAN_LEVEL) {
      case -1:
        this._pledgeClass = 0;
        break;
      case 0:
      case 1:
      case 2:
      case 3:
        if (IS_LEADER) {
          this._pledgeClass = 2;
        } else {
          this._pledgeClass = 1;
        }
        break;
      case 4:
        if (IS_LEADER) {
          this._pledgeClass = 3;
        } else {
          this._pledgeClass = 2;
        }
        break;
      case 5:
        if (IS_LEADER) {
          this._pledgeClass = 4;
        } else if (IN_ACADEMY) {
          this._pledgeClass = 1;
        } else {
          this._pledgeClass = 2;
        }
        break;
      case 6:
        if (IS_LEADER) {
          this._pledgeClass = 5;
        } else if (IN_ACADEMY) {
          this._pledgeClass = 1;
        } else if (IS_GUARD_CAPTAIN) {
          this._pledgeClass = 4;
        } else if (IS_GUARD) {
          this._pledgeClass = 2;
        } else {
          this._pledgeClass = 3;
        }
        break;
      case 7:
        if (IS_LEADER) {
          this._pledgeClass = 7;
        } else if (IN_ACADEMY) {
          this._pledgeClass = 1;
        } else if (IS_GUARD_CAPTAIN) {
          this._pledgeClass = 6;
        } else if (IS_GUARD) {
          this._pledgeClass = 3;
        } else if (IS_KNIGHT_COMMANDER) {
          this._pledgeClass = 5;
        } else if (IS_KNIGHT) {
          this._pledgeClass = 2;
        } else {
          this._pledgeClass = 4;
        }
        break;
      case 8:
        if (IS_LEADER) {
          this._pledgeClass = 8;
        } else if (IN_ACADEMY) {
          this._pledgeClass = 1;
        } else if (IS_GUARD_CAPTAIN) {
          this._pledgeClass = 7;
        } else if (IS_GUARD) {
          this._pledgeClass = 4;
        } else if (IS_KNIGHT_COMMANDER) {
          this._pledgeClass = 6;
        } else if (IS_KNIGHT) {
          this._pledgeClass = 3;
        } else {
          this._pledgeClass = 5;
        }
    }

    if (this._hero && this._pledgeClass < 8) {
      this._pledgeClass = 8;
    } else if (this._noble && this._pledgeClass < 5) {
      this._pledgeClass = 5;
    }

  }

  public void setPowerGrade(int grade) {
    this._powerGrade = grade;
  }

  public int getPowerGrade() {
    return this._powerGrade;
  }

  public void setApprentice(int apprentice) {
    this._apprentice = apprentice;
  }

  public int getApprentice() {
    return this._apprentice;
  }

  public int getSponsor() {
    return this._clan == null ? 0 : this._clan.getAnyMember(this.getObjectId()).getSponsor();
  }

  public int getNameColor() {
    return this.isInObserverMode() ? Color.black.getRGB() : this._nameColor;
  }

  public void setNameColor(int nameColor) {
    if (nameColor != Config.NORMAL_NAME_COLOUR && nameColor != Config.CLANLEADER_NAME_COLOUR && nameColor != Config.GM_NAME_COLOUR && nameColor != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR) {
      this.setVar("namecolor", Integer.toHexString(nameColor), -1L);
    } else if (nameColor == Config.NORMAL_NAME_COLOUR) {
      this.unsetVar("namecolor");
    }

    this._nameColor = nameColor;
  }

  public void setVar(String name, String value, long expirationTime) {
    this._vars.put(name, value);
    CharacterVariablesDAO.getInstance().setVar(this.getObjectId(), name, value, expirationTime);
  }

  public void setVar(String name, int value, long expirationTime) {
    this.setVar(name, String.valueOf(value), expirationTime);
  }

  public void setVar(String name, long value, long expirationTime) {
    this.setVar(name, String.valueOf(value), expirationTime);
  }

  public void unsetVar(String name) {
    if (name != null) {
      if (this._vars.remove(name) != null) {
        CharacterVariablesDAO.getInstance().deleteVar(this.getObjectId(), name);
      }

    }
  }

  public String getVar(String name) {
    return this._vars.getString(name, null);
  }

  public boolean getVarB(String name, boolean defaultVal) {
    String var = this._vars.getString(name, null);
    if (var == null) {
      return defaultVal;
    } else {
      return !var.equals("0") && !var.equalsIgnoreCase("false");
    }
  }

  public boolean getVarB(String name) {
    String var = this._vars.getString(name, null);
    return var != null && !var.equals("0") && !var.equalsIgnoreCase("false");
  }

  public long getVarLong(String name) {
    return this.getVarLong(name, 0L);
  }

  public long getVarLong(String name, long defaultVal) {
    long result = defaultVal;
    String var = this.getVar(name);
    if (var != null) {
      result = Long.parseLong(var);
    }

    return result;
  }

  public int getVarInt(String name) {
    return this.getVarInt(name, 0);
  }

  public int getVarInt(String name, int defaultVal) {
    int result = defaultVal;
    String var = this.getVar(name);
    if (var != null) {
      result = Integer.parseInt(var);
    }

    return result;
  }

  public MultiValueSet<String> getVars() {
    return this._vars;
  }

  public String getLang() {
    return this.getVar("lang@");
  }

  public String getHWIDLock() {
    return this.getVar("hwidlock@");
  }

  public void setHWIDLock(String HWIDLock) {
    if (HWIDLock == null) {
      this.unsetVar("hwidlock@");
    } else {
      this.setVar("hwidlock@", HWIDLock, -1L);
    }

  }

  public String getIPLock() {
    return this.getVar("iplock@");
  }

  public void setIPLock(String IPLock) {
    if (IPLock == null) {
      this.unsetVar("iplock@");
    } else {
      this.setVar("iplock@", IPLock, -1L);
    }

  }

  public int getLangId() {
    String lang = this.getLang();
    if (!lang.equalsIgnoreCase("en") && !lang.equalsIgnoreCase("e") && !lang.equalsIgnoreCase("eng")) {
      return !lang.equalsIgnoreCase("ru") && !lang.equalsIgnoreCase("r") && !lang.equalsIgnoreCase("rus") ? -1 : 1;
    } else {
      return 0;
    }
  }

  public Language getLanguage() {
    String lang = this.getLang();
    if (lang != null && !lang.equalsIgnoreCase("en") && !lang.equalsIgnoreCase("e") && !lang.equalsIgnoreCase("eng")) {
      return !lang.equalsIgnoreCase("ru") && !lang.equalsIgnoreCase("r") && !lang.equalsIgnoreCase("rus") ? Language.ENGLISH : Language.RUSSIAN;
    } else {
      return Language.ENGLISH;
    }
  }

  public boolean isLangRus() {
    return this.getLangId() == 1;
  }

  public int isAtWarWith(Integer id) {
    return this._clan != null && this._clan.isAtWarWith(id) ? 1 : 0;
  }

  public int isAtWar() {
    return this._clan != null && this._clan.isAtWarOrUnderAttack() > 0 ? 1 : 0;
  }

  public void stopWaterTask() {
    if (this._taskWater != null) {
      this._taskWater.cancel(false);
      this._taskWater = null;
      this.sendPacket(new SetupGauge(this, 2, 0));
      this.sendChanges();
    }

  }

  public void startWaterTask() {
    if (this.isDead()) {
      this.stopWaterTask();
    } else if (Config.ALLOW_WATER && this._taskWater == null) {
      int timeinwater = (int)(this.calcStat(Stats.BREATH, 86.0D, null, null) * 1000.0D);
      this.sendPacket(new SetupGauge(this, 2, timeinwater));
      if (this.getTransformation() > 0 && this.getTransformationTemplate() > 0 && !this.isCursedWeaponEquipped()) {
        this.setTransformation(0);
      }

      this._taskWater = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000L);
      this.sendChanges();
    }

  }

  public void doRevive(double percent) {
    this.restoreExp(percent);
    this.doRevive();
  }

  public void doRevive() {
    super.doRevive();
    this.unsetVar("lostexp");
    this.updateEffectIcons();
    this.autoShot();
  }

  public void reviveRequest(Player reviver, double percent, boolean pet, int expireResurrectTime) {
    ReviveAnswerListener reviveAsk = this._askDialog != null && this._askDialog.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)this._askDialog.getValue() : null;
    if (reviveAsk != null) {
      if (reviveAsk.isForPet() == pet && reviveAsk.getPower() >= percent) {
        reviver.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
        return;
      }

      if (pet && !reviveAsk.isForPet()) {
        reviver.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
        return;
      }

      if (pet && this.isDead()) {
        reviver.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
        return;
      }
    }

    if (pet && this.getPet() != null && this.getPet().isDead() || !pet && this.isDead()) {
      ConfirmDlg pkt = new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, expireResurrectTime);
      pkt.addName(reviver).addString(Math.round(percent) + " percent");
      this.ask(pkt, new ReviveAnswerListener(this, percent, pet, expireResurrectTime));
    }

  }

  public void summonCharacterRequest(Creature summoner, Location loc, int summonConsumeCrystal) {
    ConfirmDlg cd = new ConfirmDlg(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 60000);
    cd.addName(summoner).addZoneName(loc);
    this.ask(cd, new SummonAnswerListener(this, loc, summonConsumeCrystal, 60000));
  }

  public void scriptRequest(String text, String scriptName, Object[] args) {
//    this.ask((new ConfirmDlg(SystemMsg.S1, 30000)).addString(text), new ScriptAnswerListener(this, scriptName, args, 30000L));
  }

  public void updateNoChannel(long time) {
    this.setNoChannel(time);
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      String stmt = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";
      statement = con.prepareStatement("UPDATE characters SET nochannel = ? WHERE obj_Id=?");
      statement.setLong(1, this._NoChannel > 0L ? this._NoChannel / 1000L : this._NoChannel);
      statement.setInt(2, this.getObjectId());
      statement.executeUpdate();
    } catch (Exception var9) {
      log.warn("Could not activate nochannel:" + var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    this.sendPacket(new EtcStatusUpdate(this));
  }

  public boolean canTalkWith(Player player) {
    return this._NoChannel >= 0L || player == this;
  }

  public Deque<ChatMsg> getMessageBucket() {
    return this._msgBucket;
  }

  public boolean isInBoat() {
    return this._boat != null;
  }

  public Boat getBoat() {
    return this._boat;
  }

  public void setBoat(Boat boat) {
    this._boat = boat;
  }

  protected L2GameServerPacket stopMovePacket() {
    if (this.isInBoat()) {
      this.getBoat().inStopMovePacket(this);
    }

    return super.stopMovePacket();
  }

  public Location getInBoatPosition() {
    return this._inBoatPosition;
  }

  public void setInBoatPosition(Location loc) {
    this._inBoatPosition = loc;
  }

  public Map<Integer, SubClass> getSubClasses() {
    return this._classlist;
  }

  public void setBaseClass(int baseClass) {
    this._baseClass = baseClass;
  }

  public int getBaseClassId() {
    return this._baseClass;
  }

  public void setActiveClass(SubClass activeClass) {
    this._activeClass = activeClass;
  }

  public SubClass getActiveClass() {
    return this._activeClass;
  }

  public int getActiveClassId() {
    return this.getActiveClass().getClassId();
  }

  public synchronized void changeClassInDb(int oldclass, int newclass) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("UPDATE character_subclasses SET class_id=? WHERE char_obj_id=? AND class_id=?");
      statement.setInt(1, newclass);
      statement.setInt(2, this.getObjectId());
      statement.setInt(3, oldclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, newclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE character_hennas SET class_index=? WHERE char_obj_id=? AND class_index=?");
      statement.setInt(1, newclass);
      statement.setInt(2, this.getObjectId());
      statement.setInt(3, oldclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, newclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE object_id=? AND class_index=?");
      statement.setInt(1, newclass);
      statement.setInt(2, this.getObjectId());
      statement.setInt(3, oldclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, newclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?");
      statement.setInt(1, newclass);
      statement.setInt(2, this.getObjectId());
      statement.setInt(3, oldclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, newclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE character_effects_save SET id=? WHERE object_id=? AND id=?");
      statement.setInt(1, newclass);
      statement.setInt(2, this.getObjectId());
      statement.setInt(3, oldclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?");
      statement.setInt(1, this.getObjectId());
      statement.setInt(2, newclass);
      statement.executeUpdate();
      DbUtils.close(statement);
      statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?");
      statement.setInt(1, newclass);
      statement.setInt(2, this.getObjectId());
      statement.setInt(3, oldclass);
      statement.executeUpdate();
      DbUtils.close(statement);
    } catch (SQLException var9) {
      log.error("", var9);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public void storeCharSubClasses() {
    SubClass main = this.getActiveClass();
    if (main != null) {
      main.setCp(this.getCurrentCp());
      main.setHp(this.getCurrentHp());
      main.setMp(this.getCurrentMp());
      main.setActive(true);
      this.getSubClasses().put(this.getActiveClassId(), main);
    } else {
      log.warn("Could not store char sub data, main class " + this.getActiveClassId() + " not found for " + this);
    }

    Connection con = null;
    Statement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.createStatement();
      Iterator var5 = this.getSubClasses().values().iterator();

      StringBuilder sb;
      while(var5.hasNext()) {
        SubClass subClass = (SubClass)var5.next();
        sb = new StringBuilder("UPDATE character_subclasses SET ");
        sb.append("exp=").append(subClass.getExp()).append(",");
        sb.append("sp=").append(subClass.getSp()).append(",");
        sb.append("curHp=").append(subClass.getHp()).append(",");
        sb.append("curMp=").append(subClass.getMp()).append(",");
        sb.append("curCp=").append(subClass.getCp()).append(",");
        sb.append("level=").append(subClass.getLevel()).append(",");
        sb.append("active=").append(subClass.isActive() ? 1 : 0).append(",");
        sb.append("isBase=").append(subClass.isBase() ? 1 : 0).append(",");
        sb.append("death_penalty=").append(subClass.getDeathPenalty(this).getLevelOnSaveDB());
        sb.append(" WHERE char_obj_id=").append(this.getObjectId()).append(" AND class_id=").append(subClass.getClassId()).append(" LIMIT 1");
        statement.executeUpdate(sb.toString());
      }

      sb = new StringBuilder("UPDATE character_subclasses SET ");
      sb.append("maxHp=").append(this.getMaxHp()).append(",");
      sb.append("maxMp=").append(this.getMaxMp()).append(",");
      sb.append("maxCp=").append(this.getMaxCp());
      sb.append(" WHERE char_obj_id=").append(this.getObjectId()).append(" AND active=1 LIMIT 1");
      statement.executeUpdate(sb.toString());
    } catch (Exception var10) {
      log.warn("Could not store char sub data: " + var10);
      log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public static void restoreCharSubClasses(Player player) {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT class_id,exp,sp,curHp,curCp,curMp,active,isBase,death_penalty FROM character_subclasses WHERE char_obj_id=?");
      statement.setInt(1, player.getObjectId());
      rset = statement.executeQuery();

      SubClass activeSubclass;
      SubClass subClass;
      for(activeSubclass = null; rset.next(); player.getSubClasses().put(subClass.getClassId(), subClass)) {
        subClass = new SubClass();
        subClass.setBase(rset.getInt("isBase") != 0);
        subClass.setClassId(rset.getInt("class_id"));
        subClass.setExp(rset.getLong("exp"));
        subClass.setSp(rset.getInt("sp"));
        subClass.setHp(rset.getDouble("curHp"));
        subClass.setMp(rset.getDouble("curMp"));
        subClass.setCp(rset.getDouble("curCp"));
        subClass.setDeathPenalty(new DeathPenalty(player, rset.getInt("death_penalty")));
        boolean active = rset.getInt("active") != 0;
        if (active) {
          activeSubclass = subClass;
        }
      }

      if (player.getSubClasses().size() == 0) {
        throw new Exception("There are no one subclass for player: " + player);
      }

      int BaseClassId = player.getBaseClassId();
      if (BaseClassId == -1) {
        throw new Exception("There are no base subclass for player: " + player);
      }

      if (activeSubclass != null) {
        player.setActiveSubClass(activeSubclass.getClassId(), false);
      }

      if (player.getActiveClass() == null) {
        SubClass pBaseClassId = player.getSubClasses().get(BaseClassId);
        pBaseClassId.setActive(true);
        player.setActiveSubClass(pBaseClassId.getClassId(), false);
      }
    } catch (Exception var10) {
      log.warn("Could not restore char sub-classes: " + var10);
      log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public boolean addSubClass(int classId, boolean storeOld) {
    if (this._classlist.size() >= Config.ALT_GAME_BASE_SUB) {
      return false;
    } else {
      ClassId newId = ClassId.VALUES[classId];
      SubClass newClass = new SubClass();
      newClass.setBase(false);
      if (newId.getRace() == null) {
        return false;
      } else {
        newClass.setClassId(classId);
        this._classlist.put(classId, newClass);
        Connection con = null;
        PreparedStatement statement = null;

        label106: {
          boolean var8;
          try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO  `character_subclasses`  (\t`char_obj_id`,   `class_id`,   `exp`,   `sp`,   `curHp`,   `curMp`,   `curCp`,   `maxHp`,   `maxMp`,   `maxCp`,   `level`,   `active`,   `isBase`,   `death_penalty`)VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, this.getObjectId());
            statement.setInt(2, newClass.getClassId());
            statement.setLong(3, newClass.getExp());
            statement.setInt(4, 0);
            statement.setDouble(5, this.getCurrentHp());
            statement.setDouble(6, this.getCurrentMp());
            statement.setDouble(7, this.getCurrentCp());
            statement.setDouble(8, this.getCurrentHp());
            statement.setDouble(9, this.getCurrentMp());
            statement.setDouble(10, this.getCurrentCp());
            statement.setInt(11, newClass.getLevel());
            statement.setInt(12, 0);
            statement.setInt(13, 0);
            statement.setInt(14, 0);
            statement.execute();
            break label106;
          } catch (Exception var15) {
            log.warn("Could not add character sub-class: " + var15, var15);
            var8 = false;
          } finally {
            DbUtils.closeQuietly(con, statement);
          }

          return var8;
        }

        this.setActiveSubClass(classId, storeOld);
        boolean countUnlearnable = true;
        int unLearnable = 0;

        for(Collection skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL); skills.size() > unLearnable; skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL)) {
          Iterator var10 = skills.iterator();

          while(true) {
            while(var10.hasNext()) {
              SkillLearn s = (SkillLearn)var10.next();
              Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
              if (sk != null && sk.getCanLearn(newId)) {
                this.addSkill(sk, true);
              } else if (countUnlearnable) {
                ++unLearnable;
              }
            }

            countUnlearnable = false;
            break;
          }
        }

        this.sendPacket(new SkillList(this));
        this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
        this.setCurrentCp(this.getMaxCp());
        return true;
      }
    }
  }

  public boolean modifySubClass(int oldClassId, int newClassId) {
    SubClass originalClass = this._classlist.get(oldClassId);
    if (originalClass != null && !originalClass.isBase()) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND isBase = 0");
        statement.setInt(1, this.getObjectId());
        statement.setInt(2, oldClassId);
        statement.execute();
        DbUtils.close(statement);
        statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? ");
        statement.setInt(1, this.getObjectId());
        statement.setInt(2, oldClassId);
        statement.execute();
        DbUtils.close(statement);
        statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? ");
        statement.setInt(1, this.getObjectId());
        statement.setInt(2, oldClassId);
        statement.execute();
        DbUtils.close(statement);
        statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=? ");
        statement.setInt(1, this.getObjectId());
        statement.setInt(2, oldClassId);
        statement.execute();
        DbUtils.close(statement);
        statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=? ");
        statement.setInt(1, this.getObjectId());
        statement.setInt(2, oldClassId);
        statement.execute();
        DbUtils.close(statement);
        statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=? ");
        statement.setInt(1, this.getObjectId());
        statement.setInt(2, oldClassId);
        statement.execute();
        DbUtils.close(statement);
      } catch (Exception var10) {
        log.warn("Could not delete char sub-class: " + var10);
        log.error("", var10);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

      this._classlist.remove(oldClassId);
      return newClassId <= 0 || this.addSubClass(newClassId, false);
    } else {
      return false;
    }
  }

  public void setActiveSubClass(int subId, boolean store) {
    SubClass sub = this.getSubClasses().get(subId);
    if (sub != null) {
      try {
        if (this.getActiveClass() != null) {
          EffectsDAO.getInstance().insert(this);
          this.storeDisableSkills();
          if (QuestManager.getQuest(422) != null) {
            String qn = QuestManager.getQuest(422).getName();
            if (qn != null) {
              QuestState qs = this.getQuestState(qn);
              if (qs != null) {
                qs.exitCurrentQuest(true);
              }
            }
          }
        }
      } catch (Exception var7) {
        log.warn("", var7);
      }

      SubClass oldsub = this.getActiveClass();
      if (oldsub != null) {
        oldsub.setActive(false);
        if (store) {
          oldsub.setCp(this.getCurrentCp());
          oldsub.setHp(this.getCurrentHp());
          oldsub.setMp(this.getCurrentMp());
          this.getSubClasses().put(this.getActiveClassId(), oldsub);
        }
      }

      sub.setActive(true);
      this.setActiveClass(sub);
      this.getSubClasses().put(this.getActiveClassId(), sub);
      this.setClassId(subId, false, false);
      this.removeAllSkills();
      this.getEffectList().stopAllEffects();
      if (this.getPet() != null && (this.getPet().isSummon() || Config.ALT_IMPROVED_PETS_LIMITED_USE && (this.getPet().getNpcId() == 16035 && !this.isMageClass() || this.getPet().getNpcId() == 16034 && this.isMageClass()))) {
        this.getPet().unSummon();
      }

      this.setAgathion(0);
      this.restoreSkills();
      Iterator var9;
      if (Config.ALT_SUBLASS_SKILL_TRANSFER && this.getBaseClassId() == subId) {
        var9 = this.getSubClasses().values().iterator();

        while(var9.hasNext()) {
          SubClass ssc = (SubClass)var9.next();
          if (ssc.getClassId() != subId) {
            this.restoreSkills(ssc.getClassId());
          }
        }
      }

      this.rewardSkills(false);
      this.checkSkills();
      this.sendPacket(new ExStorageMaxCount(this));
      this.refreshExpertisePenalty();
      this.sendPacket(new SkillList(this));
      this.getInventory().refreshEquip();
      this.getInventory().validateItems();

      for(int i = 0; i < 3; ++i) {
        this._henna[i] = null;
      }

      this.restoreHenna();
      this.sendPacket(new HennaInfo(this));
      EffectsDAO.getInstance().restoreEffects(this);
      this.restoreDisableSkills();
      this.setCurrentHpMp(sub.getHp(), sub.getMp());
      this.setCurrentCp(sub.getCp());
      this._shortCuts.restore();
      this.sendPacket(new ShortCutInit(this));
      var9 = this.getAutoSoulShot().iterator();

      while(var9.hasNext()) {
        int shotId = (Integer)var9.next();
        this.sendPacket(new ExAutoSoulShot(shotId, true));
      }

      this.sendPacket(new SkillCoolTime(this));
      this.broadcastPacket(new SocialAction(this.getObjectId(), 15));
      this.getDeathPenalty().restore(this);
      this.setIncreasedForce(0);
      this.startHourlyTask();
      this.broadcastCharInfo();
      this.updateEffectIcons();
      this.updateStats();
    }
  }

  public void startKickTask(long delayMillis) {
    this.stopKickTask();
    this._kickTask = ThreadPoolManager.getInstance().schedule(new KickTask(this), delayMillis);
  }

  public void stopKickTask() {
    if (this._kickTask != null) {
      this._kickTask.cancel(false);
      this._kickTask = null;
    }

  }

  public void startBonusTask() {
    if (Config.SERVICES_RATE_ENABLED) {
      AccountBonusDAO.getInstance().load(this.getAccountName(), this.getBonus());
      long bonusExpireTime = this.getBonus().getBonusExpire();
      if (bonusExpireTime > System.currentTimeMillis() / 1000L) {
        if (this._bonusExpiration == null) {
          this._bonusExpiration = LazyPrecisionTaskManager.getInstance().startBonusExpirationTask(this);
        }
      } else if (bonusExpireTime > 0L) {
        AccountBonusDAO.getInstance().delete(this.getAccountName());
      }
    }

  }

  public void stopBonusTask() {
    if (this._bonusExpiration != null) {
      this._bonusExpiration.cancel(false);
      this._bonusExpiration = null;
    }

  }

  public int getInventoryLimit() {
    return (int)this.calcStat(Stats.INVENTORY_LIMIT, 0.0D, null, null);
  }

  public int getWarehouseLimit() {
    return (int)this.calcStat(Stats.STORAGE_LIMIT, 0.0D, null, null);
  }

  public int getTradeLimit() {
    return (int)this.calcStat(Stats.TRADE_LIMIT, 0.0D, null, null);
  }

  public int getDwarvenRecipeLimit() {
    return (int)this.calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50.0D, null, null) + Config.ALT_ADD_RECIPES;
  }

  public int getCommonRecipeLimit() {
    return (int)this.calcStat(Stats.COMMON_RECIPE_LIMIT, 50.0D, null, null) + Config.ALT_ADD_RECIPES;
  }

  public Element getAttackElement() {
    return Formulas.getAttackElement(this, null);
  }

  public int getAttack(Element element) {
    return element == Element.NONE ? 0 : (int)this.calcStat(element.getAttack(), 0.0D, null, null);
  }

  public int getDefence(Element element) {
    return element == Element.NONE ? 0 : (int)this.calcStat(element.getDefence(), 0.0D, null, null);
  }

  public boolean getAndSetLastItemAuctionRequest() {
    if (this._lastItemAuctionInfoRequest + 2000L < System.currentTimeMillis()) {
      this._lastItemAuctionInfoRequest = System.currentTimeMillis();
      return true;
    } else {
      this._lastItemAuctionInfoRequest = System.currentTimeMillis();
      return false;
    }
  }

  public int getNpcId() {
    return -2;
  }

  public GameObject getVisibleObject(int id) {
    if (this.getObjectId() == id) {
      return this;
    } else {
      GameObject target = null;
      if (this.getTargetId() == id) {
        target = this.getTarget();
      }

      if (target == null && this._party != null) {

        for (Player p : this._party.getPartyMembers()) {
          if (p != null && p.getObjectId() == id) {
            target = p;
            break;
          }
        }
      }

      if (target == null) {
        target = World.getAroundObjectById(this, id);
      }

      return target != null && !target.isInvisible() ? target : null;
    }
  }

  public int getPAtk(Creature target) {
    double init = this.getActiveWeaponInstance() == null ? (double)(this.isMageClass() ? 3 : 4) : 0.0D;
    return (int)this.calcStat(Stats.POWER_ATTACK, init, target, null);
  }

  public int getPDef(Creature target) {
    double init = 4.0D;
    ItemInstance chest = this.getInventory().getPaperdollItem(10);
    if (chest == null) {
      init += this.isMageClass() ? 15.0D : 31.0D;
    }

    if (this.getInventory().getPaperdollItem(11) == null && (chest == null || chest.getBodyPart() != 32768)) {
      init += this.isMageClass() ? 8.0D : 18.0D;
    }

    if (this.getInventory().getPaperdollItem(6) == null) {
      init += 12.0D;
    }

    if (this.getInventory().getPaperdollItem(9) == null) {
      init += 8.0D;
    }

    if (this.getInventory().getPaperdollItem(12) == null) {
      init += 7.0D;
    }

    return (int)this.calcStat(Stats.POWER_DEFENCE, init, target, null);
  }

  public int getMDef(Creature target, Skill skill) {
    double init = 0.0D;
    if (this.getInventory().getPaperdollItem(2) == null) {
      init += 9.0D;
    }

    if (this.getInventory().getPaperdollItem(1) == null) {
      init += 9.0D;
    }

    if (this.getInventory().getPaperdollItem(3) == null) {
      init += 13.0D;
    }

    if (this.getInventory().getPaperdollItem(5) == null) {
      init += 5.0D;
    }

    if (this.getInventory().getPaperdollItem(4) == null) {
      init += 5.0D;
    }

    return (int)this.calcStat(Stats.MAGIC_DEFENCE, init, target, skill);
  }

  public boolean isSubClassActive() {
    return this.getBaseClassId() != this.getActiveClassId();
  }

  public String getTitle() {
    return super.getTitle();
  }

  public int getTitleColor() {
    return this._titlecolor;
  }

  public void setTitleColor(int titlecolor) {
    if (titlecolor != 16777079) {
      this.setVar("titlecolor", Integer.toHexString(titlecolor), -1L);
    } else {
      this.unsetVar("titlecolor");
    }

    this._titlecolor = titlecolor;
  }

  public String getDisconnectedTitle() {
    return this._disconnectedTitle;
  }

  public void setDisconnectedTitle(String disconnectedTitle) {
    this._disconnectedTitle = disconnectedTitle;
  }

  public int getDisconnectedTitleColor() {
    return this._disconnectedTitleColor;
  }

  public void setDisconnectedTitleColor(int disconnectedTitleColor) {
    this._disconnectedTitleColor = disconnectedTitleColor;
  }

  public boolean isCursedWeaponEquipped() {
    return this._cursedWeaponEquippedId != 0;
  }

  public void setCursedWeaponEquippedId(int value) {
    this._cursedWeaponEquippedId = value;
  }

  public int getCursedWeaponEquippedId() {
    return this._cursedWeaponEquippedId;
  }

  public boolean isImmobilized() {
    return super.isImmobilized() || this.isOverloaded() || this.isSitting() || this.isFishing();
  }

  public boolean isBlocked() {
    return super.isBlocked() || this.isInMovie() || this.isInObserverMode() || this.isTeleporting() || this.isLogoutStarted();
  }

  public boolean isInvul() {
    return super.isInvul() || this.isInMovie() || this.getAfterTeleportPortectionTime() > System.currentTimeMillis();
  }

  public boolean isResurectProhibited() {
    return this._resurect_prohibited;
  }

  public void setResurectProhibited(boolean prohibited) {
    this._resurect_prohibited = prohibited;
  }

  public void setOverloaded(boolean overloaded) {
    this._overloaded = overloaded;
  }

  public boolean isOverloaded() {
    return this._overloaded;
  }

  public boolean isFishing() {
    return this._isFishing;
  }

  public Fishing getFishing() {
    return this._fishing;
  }

  public void setFishing(boolean value) {
    this._isFishing = value;
  }

  public void startFishing(FishTemplate fish, int lureId) {
    this._fishing.setFish(fish);
    this._fishing.setLureId(lureId);
    this._fishing.startFishing();
  }

  public void stopFishing() {
    this._fishing.stopFishing();
  }

  public Location getFishLoc() {
    return this._fishing.getFishLoc();
  }

  public Bonus getBonus() {
    return this._bonus;
  }

  public boolean hasBonus() {
    return this._bonus.getBonusExpire() > System.currentTimeMillis() / 1000L;
  }

  public double getRateAdena() {
    return this.calcStat(Stats.ADENA_REWARD_MULTIPLIER, this._party == null ? (double)this._bonus.getDropAdena() : this._party._rateAdena);
  }

  public double getRateItems() {
    return this.calcStat(Stats.ITEM_REWARD_MULTIPLIER, this._party == null ? (double)this._bonus.getDropItems() : this._party._rateDrop);
  }

  public double getRateExp() {
    return this.calcStat(Stats.EXP, this._party == null ? (double)this._bonus.getRateXp() : this._party._rateExp, null, null);
  }

  public double getRateSp() {
    return this.calcStat(Stats.SP, this._party == null ? (double)this._bonus.getRateSp() : this._party._rateSp, null, null);
  }

  public double getRateSpoil() {
    return this.calcStat(Stats.SPOIL_REWARD_MULTIPLIER, this._party == null ? (double)this._bonus.getDropSpoil() : this._party._rateSpoil);
  }

  public boolean isMaried() {
    return this._maried;
  }

  public void setMaried(boolean state) {
    this._maried = state;
  }

  public void setMaryRequest(boolean state) {
    this._maryrequest = state;
  }

  public boolean isMaryRequest() {
    return this._maryrequest;
  }

  public void setMaryAccepted(boolean state) {
    this._maryaccepted = state;
  }

  public boolean isMaryAccepted() {
    return this._maryaccepted;
  }

  public int getPartnerId() {
    return this._partnerId;
  }

  public void setPartnerId(int partnerid) {
    this._partnerId = partnerid;
  }

  public int getCoupleId() {
    return this._coupleId;
  }

  public void setCoupleId(int coupleId) {
    this._coupleId = coupleId;
  }

  public void setUndying(boolean val) {
    if (this.isGM()) {
      this._isUndying = val;
    }
  }

  public boolean isUndying() {
    return this._isUndying;
  }

  public void resetReuse() {
    this._skillReuses.clear();
    this._sharedGroupReuses.clear();
  }

  public DeathPenalty getDeathPenalty() {
    return this._activeClass == null ? null : this._activeClass.getDeathPenalty(this);
  }

  public boolean isCharmOfCourage() {
    return this._charmOfCourage;
  }

  public void setCharmOfCourage(boolean val) {
    this._charmOfCourage = val;
    if (!val) {
      this.getEffectList().stopEffect(5041);
    }

    this.sendEtcStatusUpdate();
  }

  public int getIncreasedForce() {
    return this._increasedForce;
  }

  public int getConsumedSouls() {
    return this._consumedSouls;
  }

  public void setConsumedSouls(int i, NpcInstance monster) {
    if (i != this._consumedSouls) {
      int max = (int)this.calcStat(Stats.SOULS_LIMIT, 0.0D, monster, null);
      if (i > max) {
        i = max;
      }

      if (i <= 0) {
        this._consumedSouls = 0;
        this.sendEtcStatusUpdate();
      } else {
        if (this._consumedSouls != i) {
          int diff = i - this._consumedSouls;
          if (diff > 0) {
            SystemMessage sm = new SystemMessage(2162);
            sm.addNumber(diff);
            sm.addNumber(i);
            this.sendPacket(sm);
          }
        } else if (max == i) {
          this.sendPacket(Msg.SOUL_CANNOT_BE_ABSORBED_ANY_MORE);
          return;
        }

        this._consumedSouls = i;
        this.sendPacket(new EtcStatusUpdate(this));
      }
    }
  }

  public void setIncreasedForce(int i) {
    if (this._increasedForce != i) {
      i = Math.min(i, 7);
      i = Math.max(i, 0);
      if (i != 0 && i > this._increasedForce) {
        this._increasedForceLastUpdateTimeStamp = System.currentTimeMillis();
        if (this._increasedForceCleanupTask == null) {
          this._increasedForceCleanupTask = ThreadPoolManager.getInstance().schedule(new Player.ForceCleanupTask(), 600000L);
        }

        this.sendPacket((new SystemMessage(323)).addNumber(i));
      }

      this._increasedForce = i;
      this.sendEtcStatusUpdate();
    }
  }

  public boolean isFalling() {
    return System.currentTimeMillis() - this._lastFalling < 5000L;
  }

  public void falling(int height) {
    if (Config.DAMAGE_FROM_FALLING && !this.isDead() && !this.isFlying() && !this.isInWater() && !this.isInBoat()) {
      this._lastFalling = System.currentTimeMillis();
      int damage = (int)this.calcStat(Stats.FALL, (double)this.getMaxHp() / 2000.0D * (double)height, null, null);
      if (damage > 0) {
        int curHp = (int)this.getCurrentHp();
        if (curHp - damage < 1) {
          this.setCurrentHp(1.0D, false);
        } else {
          this.setCurrentHp(curHp - damage, false);
        }

        this.sendPacket((new SystemMessage(296)).addNumber(damage));
      }

    }
  }

  public void checkHpMessages(double curHp, double newHp) {
    int[] _hp = new int[]{30, 30};
    int[] skills = new int[]{290, 291};
    double percent = this.getMaxHp() / 100;
    double _curHpPercent = curHp / percent;
    double _newHpPercent = newHp / percent;
    boolean needsUpdate = false;

    for(int i = 0; i < skills.length; ++i) {
      int level = this.getSkillLevel(skills[i]);
      if (level > 0) {
        if (_curHpPercent > (double)_hp[i] && _newHpPercent <= (double)_hp[i]) {
          this.sendPacket((new SystemMessage(1133)).addSkillName(skills[i], level));
          needsUpdate = true;
        } else if (_curHpPercent <= (double)_hp[i] && _newHpPercent > (double)_hp[i]) {
          this.sendPacket((new SystemMessage(1134)).addSkillName(skills[i], level));
          needsUpdate = true;
        }
      }
    }

    if (needsUpdate) {
      this.sendChanges();
    }

  }

  public void checkDayNightMessages() {
    int level = this.getSkillLevel(294);
    if (level > 0) {
      if (GameTimeController.getInstance().isNowNight()) {
        this.sendPacket((new SystemMessage(1131)).addSkillName(294, level));
      } else {
        this.sendPacket((new SystemMessage(1132)).addSkillName(294, level));
      }
    }

    this.sendChanges();
  }

  public int getZoneMask() {
    return this._zoneMask;
  }

  public boolean updateZones() {
    if (!super.updateZones()) {
      return false;
    } else {
      boolean lastInCombatZone = (this._zoneMask & 16384) == 16384;
      boolean lastInDangerArea = (this._zoneMask & 256) == 256;
      boolean lastOnSiegeField = (this._zoneMask & 2048) == 2048;
      boolean lastInPeaceZone = (this._zoneMask & 4096) == 4096;
      boolean isInCombatZone = this.isInCombatZone();
      boolean isInDangerArea = this.isInDangerArea();
      boolean isInFunZone = this.isInZone(ZoneType.fun);
      boolean isOnSiegeField = this.isOnSiegeField() || isInFunZone;
      boolean isInPeaceZone = this.isInPeaceZone();
      boolean isInSSQZone = this.isInSSQZone();
      int lastZoneMask = this._zoneMask;
      this._zoneMask = 0;
      if (isInCombatZone) {
        this._zoneMask |= 16384;
      }

      if (isInDangerArea) {
        this._zoneMask |= 256;
      }

      if (isOnSiegeField) {
        this._zoneMask |= 2048;
      }

      if (isInPeaceZone) {
        this._zoneMask |= 4096;
      }

      if (isInSSQZone) {
        this._zoneMask |= 8192;
      }

      if (lastZoneMask != this._zoneMask) {
        this.sendPacket(new ExSetCompassZoneCode(this));
      }

      if (lastInCombatZone != isInCombatZone) {
        this.broadcastRelationChanged();
      }

      if (lastInDangerArea != isInDangerArea) {
        this.sendPacket(new EtcStatusUpdate(this));
      }

      if (lastOnSiegeField != isOnSiegeField) {
        this.broadcastRelationChanged();
        if (isOnSiegeField) {
          this.sendPacket(Msg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
          if (Config.FUN_ZONE_FLAG_ON_ENTER && isInFunZone && !this.isTeleporting() && this.getPvpFlag() == 0) {
            this.startPvPFlag(null);
          }
        } else {
          this.sendPacket(Msg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
          if (!this.isTeleporting() && this.getPvpFlag() == 0) {
            this.startPvPFlag(null);
          }
        }
      }

      if (isInPeaceZone && !lastInPeaceZone) {
        FlagItemAttachment attachment = this.getActiveWeaponFlagAttachment();
        if (attachment != null) {
          attachment.onEnterPeace(this);
        }
      }

      if (this.isInWater()) {
        this.startWaterTask();
      } else {
        this.stopWaterTask();
      }

      return true;
    }
  }

  public void startAutoSaveTask() {
    if (Config.AUTOSAVE) {
      if (this._autoSaveTask == null) {
        this._autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
      }

    }
  }

  public void stopAutoSaveTask() {
    if (this._autoSaveTask != null) {
      this._autoSaveTask.cancel(false);
    }

    this._autoSaveTask = null;
  }

  public void startPcBangPointsTask() {
    if (Config.ALT_PCBANG_POINTS_ENABLED && Config.ALT_PCBANG_POINTS_DELAY > 0) {
      if (this._pcCafePointsTask == null) {
        this._pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
      }

    }
  }

  public void stopPcBangPointsTask() {
    if (this._pcCafePointsTask != null) {
      this._pcCafePointsTask.cancel(false);
    }

    this._pcCafePointsTask = null;
  }

  public void startUnjailTask(Player player, int time) {
    if (this._unjailTask != null) {
      this._unjailTask.cancel(false);
    }

    this._unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), time * '\uea60');
  }

  public void stopUnjailTask() {
    if (this._unjailTask != null) {
      this._unjailTask.cancel(false);
    }

    this._unjailTask = null;
  }

  public void sendMessage(String message) {
    this.sendPacket(new SystemMessage(message));
  }

  public void setLastClientPosition(Location position) {
    this._lastClientPosition = position;
  }

  public Location getLastClientPosition() {
    return this._lastClientPosition;
  }

  public void setLastServerPosition(Location position) {
    this._lastServerPosition = position;
  }

  public Location getLastServerPosition() {
    return this._lastServerPosition;
  }

  public void setUseSeed(int id) {
    this._useSeed = id;
  }

  public int getUseSeed() {
    return this._useSeed;
  }

  public int getRelation(Player target) {
    int result = 0;
    if (this.getClan() != null) {
      result |= 64;
    }

    if (this.isClanLeader()) {
      result |= 128;
    }

    Party party = this.getParty();
    if (party != null && party == target.getParty()) {
      result |= 32;
      switch(party.getPartyMembers().indexOf(this)) {
        case 0:
          result |= 16;
          break;
        case 1:
          result |= 8;
          break;
        case 2:
          result |= 7;
          break;
        case 3:
          result |= 6;
          break;
        case 4:
          result |= 5;
          break;
        case 5:
          result |= 4;
          break;
        case 6:
          result |= 3;
          break;
        case 7:
          result |= 2;
          break;
        case 8:
          result |= 1;
      }
    }

    Clan clan1 = this.getClan();
    Clan clan2 = target.getClan();
    if (clan1 != null && clan2 != null && target.getPledgeType() != -1 && this.getPledgeType() != -1 && clan2.isAtWarWith(clan1.getClanId())) {
      result |= 65536;
      if (clan1.isAtWarWith(clan2.getClanId())) {
        result |= 32768;
      }
    }

    GlobalEvent e;
    for(Iterator var6 = this.getEvents().iterator(); var6.hasNext(); result = e.getRelation(this, target, result)) {
      e = (GlobalEvent)var6.next();
    }

    return result;
  }

  public long getlastPvpAttack() {
    return this._lastPvpAttack;
  }

  public void startPvPFlag(Creature target) {
    if (this._karma <= 0) {
      long startTime = System.currentTimeMillis();
      if (target != null && target.getPvpFlag() != 0) {
        startTime -= Math.max(0, Config.PVP_TIME - Config.PVP_FLAG_ON_UN_FLAG_TIME);
      }

      if (this._pvpFlag == 0 || this._lastPvpAttack <= startTime) {
        this._lastPvpAttack = startTime;
        this.updatePvPFlag(1);
        if (this._PvPRegTask == null) {
          this._PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(this), 1000L, 1000L);
        }

      }
    }
  }

  public void stopPvPFlag() {
    if (this._PvPRegTask != null) {
      this._PvPRegTask.cancel(false);
      this._PvPRegTask = null;
    }

    this.updatePvPFlag(0);
  }

  public void updatePvPFlag(int value) {
    if (this._pvpFlag != value) {
      this.setPvpFlag(value);
      this.sendStatusUpdate(true, true, 26);
      this.broadcastRelationChanged();
    }
  }

  public void setPvpFlag(int pvpFlag) {
    this._pvpFlag = pvpFlag;
  }

  public int getPvpFlag() {
    return this._pvpFlag;
  }

  public boolean isInDuel() {
    return this.getEvent(DuelEvent.class) != null;
  }

  public TamedBeastInstance getTrainedBeast() {
    return this._tamedBeast;
  }

  public void setTrainedBeast(TamedBeastInstance tamedBeast) {
    this._tamedBeast = tamedBeast;
  }

  public long getLastAttackPacket() {
    return this._lastAttackPacket;
  }

  public void setLastAttackPacket() {
    this._lastAttackPacket = System.currentTimeMillis();
  }

  public byte[] getKeyBindings() {
    return this._keyBindings;
  }

  public void setKeyBindings(byte[] keyBindings) {
    if (keyBindings == null) {
      keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    this._keyBindings = keyBindings;
  }

  public void setTransformation(int transformationId) {
    if (transformationId != this._transformationId && (this._transformationId == 0 || transformationId == 0)) {
      Iterator var2;
      Effect effect;
      Skill s;
      if (transformationId == 0) {
        var2 = this.getEffectList().getAllEffects().iterator();

        while(var2.hasNext()) {
          effect = (Effect)var2.next();
          if (effect != null && effect.getEffectType() == EffectType.Transformation && effect.calc() != 0.0D) {
            effect.exit();
            this.preparateToTransform(effect.getSkill());
            break;
          }
        }

        if (!this._transformationSkills.isEmpty()) {
          var2 = this._transformationSkills.values().iterator();

          while(var2.hasNext()) {
            s = (Skill)var2.next();
            if (!s.isCommon() && !SkillAcquireHolder.getInstance().isSkillPossible(this, s) && !s.isHeroic()) {
              super.removeSkill(s);
            }
          }

          this._transformationSkills.clear();
        }
      } else {
        if (this.isCursedWeaponEquipped()) {
          this.preparateToTransform(null);
        } else {
          var2 = this.getEffectList().getAllEffects().iterator();

          while(var2.hasNext()) {
            effect = (Effect)var2.next();
            if (effect != null && effect.getEffectType() == EffectType.Transformation) {
              if (effect.getSkill() instanceof Transformation && ((Transformation)effect.getSkill()).isDisguise) {
                Iterator var13 = this.getAllSkills().iterator();

                label117:
                while(true) {
                  Skill nextSkill;
                  do {
                    do {
                      if (!var13.hasNext()) {
                        break label117;
                      }

                      nextSkill = (Skill)var13.next();
                    } while(nextSkill == null);
                  } while(!nextSkill.isActive() && !nextSkill.isToggle());

                  this._transformationSkills.put(nextSkill.getId(), nextSkill);
                }
              } else {
                AddedSkill[] var4 = effect.getSkill().getAddedSkills();
                int var5 = var4.length;

                for (AddedSkill addedSkill : var4) {
                  int learnLevel;
                  if (addedSkill.level == 0) {
                    learnLevel = this.getSkillLevel(addedSkill.id);
                    if (learnLevel > 0) {
                      this._transformationSkills.put(addedSkill.id, SkillTable.getInstance().getInfo(addedSkill.id, learnLevel));
                    }
                  } else if (addedSkill.level == -2) {
                    learnLevel = Math.max(effect.getSkill().getMagicLevel(), 40);
                    int maxLevel = SkillTable.getInstance().getBaseLevel(addedSkill.id);
                    int curSkillLevel = 1;
                    if (maxLevel > 3) {
                      curSkillLevel = curSkillLevel + (this.getLevel() - learnLevel);
                    } else {
                      curSkillLevel = curSkillLevel + (this.getLevel() - learnLevel) / ((76 - learnLevel) / maxLevel);
                    }

                    curSkillLevel = Math.min(Math.max(curSkillLevel, 1), maxLevel);
                    this._transformationSkills.put(addedSkill.id, SkillTable.getInstance().getInfo(addedSkill.id, curSkillLevel));
                  } else {
                    this._transformationSkills.put(addedSkill.id, addedSkill.getSkill());
                  }
                }
              }

              this.preparateToTransform(effect.getSkill());
              break;
            }
          }
        }

        if (!this.isOlyParticipant() && this.isCursedWeaponEquipped() && this._hero && this.getBaseClassId() == this.getActiveClassId()) {
          this._transformationSkills.put(395, SkillTable.getInstance().getInfo(395, 1));
          this._transformationSkills.put(396, SkillTable.getInstance().getInfo(396, 1));
          this._transformationSkills.put(1374, SkillTable.getInstance().getInfo(1374, 1));
          this._transformationSkills.put(1375, SkillTable.getInstance().getInfo(1375, 1));
          this._transformationSkills.put(1376, SkillTable.getInstance().getInfo(1376, 1));
        }

        var2 = this._transformationSkills.values().iterator();

        while(var2.hasNext()) {
          s = (Skill)var2.next();
          this.addSkill(s, false);
        }
      }

      this._transformationId = transformationId;
      this.sendPacket(new ExBasicActionList(this));
      this.sendPacket(new SkillList(this));
      this.sendPacket(new ShortCutInit(this));
      var2 = this.getAutoSoulShot().iterator();

      while(var2.hasNext()) {
        int shotId = (Integer)var2.next();
        this.sendPacket(new ExAutoSoulShot(shotId, true));
      }

      this.broadcastUserInfo(true);
    }
  }

  private void preparateToTransform(Skill transSkill) {
    if (transSkill == null || !transSkill.isBaseTransformation()) {

      for (Effect effect : this.getEffectList().getAllEffects()) {
        if (effect != null && effect.getSkill().isToggle()) {
          effect.exit();
        }
      }
    }

  }

  public boolean isInFlyingTransform() {
    return this._transformationId == 8 || this._transformationId == 9 || this._transformationId == 260;
  }

  public boolean isInMountTransform() {
    return this._transformationId == 106 || this._transformationId == 109 || this._transformationId == 110 || this._transformationId == 20001;
  }

  public int getTransformation() {
    return this._transformationId;
  }

  public String getTransformationName() {
    return this._transformationName;
  }

  public void setTransformationName(String name) {
    this._transformationName = name;
  }

  public String getTransformationTitle() {
    return this._transformationTitle;
  }

  public void setTransformationTitle(String transformationTitle) {
    this._transformationTitle = transformationTitle;
  }

  public void setTransformationTemplate(int template) {
    this._transformationTemplate = template;
  }

  public int getTransformationTemplate() {
    return this._transformationTemplate;
  }

  public final Collection<Skill> getAllSkills() {
    if (this._transformationId == 0 && !this.isCursedWeaponEquipped()) {
      return super.getAllSkills();
    } else {
      Map<Integer, Skill> tempSkills = new HashMap<>();

      for (Skill s : super.getAllSkills()) {
        if (s != null && !s.isActive() && !s.isToggle()) {
          tempSkills.put(s.getId(), s);
        }
      }

      tempSkills.putAll(this._transformationSkills);
      return tempSkills.values();
    }
  }

  public void setAgathion(int id) {
    if (this._agathionId != id) {
      this._agathionId = id;
      this.broadcastCharInfo();
    }
  }

  public int getAgathionId() {
    return this._agathionId;
  }

  public int getPcBangPoints() {
    return this._pcBangPoints;
  }

  public void setPcBangPoints(int val) {
    this._pcBangPoints = val;
  }

  public void addPcBangPoints(int count, boolean doublePoints) {
    if (doublePoints) {
      count *= 2;
    }

    this._pcBangPoints += count;
    this.sendPacket((new SystemMessage(doublePoints ? 1708 : 1707)).addNumber(count));
    this.sendPacket(new ExPCCafePointInfo(this, count, 1, 2, 12));
  }

  public boolean reducePcBangPoints(int count) {
    if (this._pcBangPoints < count) {
      return false;
    } else {
      this._pcBangPoints -= count;
      this.sendPacket((new SystemMessage(1709)).addNumber(count));
      this.sendPacket(new ExPCCafePointInfo(this, 0, 1, 2, 12));
      return true;
    }
  }

  public void setGroundSkillLoc(Location location) {
    this._groundSkillLoc = location;
  }

  public Location getGroundSkillLoc() {
    return this._groundSkillLoc;
  }

  public boolean isLogoutStarted() {
    return this._isLogout.get();
  }

  public void setOfflineMode(boolean val) {
    if (!val) {
      this.unsetVar("offline");
    }

    this._offline = val;
  }

  public boolean isInOfflineMode() {
    return this._offline;
  }

  public void saveTradeList() {
    String val = "";
    Iterator var2;
    TradeItem i;
    if (this._sellList != null && !this._sellList.isEmpty()) {
      for(var2 = this._sellList.iterator(); var2.hasNext(); val = val + i.getObjectId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":") {
        i = (TradeItem)var2.next();
      }

      this.setVar("selllist", val, -1L);
      val = "";
      if (this._tradeList != null && this.getSellStoreName() != null) {
        this.setVar("sellstorename", this.getSellStoreName(), -1L);
      }
    } else {
      this.unsetVar("selllist");
    }

    if (this._packageSellList != null && !this._packageSellList.isEmpty()) {
      for(var2 = this._packageSellList.iterator(); var2.hasNext(); val = val + i.getObjectId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":") {
        i = (TradeItem)var2.next();
      }

      this.setVar("packageselllist", val, -1L);
      val = "";
      if (this._tradeList != null && this.getSellStoreName() != null) {
        this.setVar("sellstorename", this.getSellStoreName(), -1L);
      }
    } else {
      this.unsetVar("packageselllist");
    }

    if (this._buyList != null && !this._buyList.isEmpty()) {
      for(var2 = this._buyList.iterator(); var2.hasNext(); val = val + i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ";" + i.getEnchantLevel() + ":") {
        i = (TradeItem)var2.next();
      }

      this.setVar("buylist", val, -1L);
      val = "";
      if (this._tradeList != null && this.getBuyStoreName() != null) {
        this.setVar("buystorename", this.getBuyStoreName(), -1L);
      }
    } else {
      this.unsetVar("buylist");
    }

    if (this._createList != null && !this._createList.isEmpty()) {
      ManufactureItem manufactureItem;
      for(var2 = this._createList.iterator(); var2.hasNext(); val = val + manufactureItem.getRecipeId() + ";" + manufactureItem.getCost() + ":") {
        manufactureItem = (ManufactureItem)var2.next();
      }

      this.setVar("createlist", val, -1L);
      if (this.getManufactureName() != null) {
        this.setVar("manufacturename", this.getManufactureName(), -1L);
      }
    } else {
      this.unsetVar("createlist");
    }

  }

  public void restoreTradeList() {
    String var = this.getVar("selllist");
    String[] items;
    String[] var3;
    int var4;
    int var5;
    String item;
    String[] values;
    int recId;
    long price;
    ItemInstance itemToSell;
    TradeItem i;
    if (var != null) {
      this._sellList = new CopyOnWriteArrayList();
      items = var.split(":");
      var3 = items;
      var4 = items.length;

      for(var5 = 0; var5 < var4; ++var5) {
        item = var3[var5];
        if (!item.equals("")) {
          values = item.split(";");
          if (values.length >= 3) {
            recId = Integer.parseInt(values[0]);
            price = Long.parseLong(values[1]);
            price = Long.parseLong(values[2]);
            itemToSell = this.getInventory().getItemByObjectId(recId);
            if (price >= 1L && itemToSell != null) {
              if (price > itemToSell.getCount()) {
                price = itemToSell.getCount();
              }

              i = new TradeItem(itemToSell);
              i.setCount(price);
              i.setOwnersPrice(price);
              this._sellList.add(i);
            }
          }
        }
      }

      var = this.getVar("sellstorename");
      if (var != null) {
        this.setSellStoreName(var);
      }
    }

    var = this.getVar("packageselllist");
    if (var != null) {
      this._packageSellList = new CopyOnWriteArrayList();
      items = var.split(":");
      var3 = items;
      var4 = items.length;

      for(var5 = 0; var5 < var4; ++var5) {
        item = var3[var5];
        if (!item.equals("")) {
          values = item.split(";");
          if (values.length >= 3) {
            recId = Integer.parseInt(values[0]);
            price = Long.parseLong(values[1]);
            price = Long.parseLong(values[2]);
            itemToSell = this.getInventory().getItemByObjectId(recId);
            if (price >= 1L && itemToSell != null) {
              if (price > itemToSell.getCount()) {
                price = itemToSell.getCount();
              }

              i = new TradeItem(itemToSell);
              i.setCount(price);
              i.setOwnersPrice(price);
              this._packageSellList.add(i);
            }
          }
        }
      }

      var = this.getVar("sellstorename");
      if (var != null) {
        this.setSellStoreName(var);
      }
    }

    var = this.getVar("buylist");
    if (var != null) {
      this._buyList = new CopyOnWriteArrayList();
      items = var.split(":");
      var3 = items;
      var4 = items.length;

      for(var5 = 0; var5 < var4; ++var5) {
        item = var3[var5];
        if (!item.equals("")) {
          values = item.split(";");
          if (values.length >= 3) {
            TradeItem tradeItem = new TradeItem();
            tradeItem.setItemId(Integer.parseInt(values[0]));
            tradeItem.setCount(Long.parseLong(values[1]));
            tradeItem.setOwnersPrice(Long.parseLong(values[2]));
            if (values.length >= 4) {
              tradeItem.setEnchantLevel(Integer.parseInt(values[3]));
            }

            this._buyList.add(tradeItem);
          }
        }
      }

      var = this.getVar("buystorename");
      if (var != null) {
        this.setBuyStoreName(var);
      }
    }

    var = this.getVar("createlist");
    if (var != null) {
      this._createList = new CopyOnWriteArrayList();
      items = var.split(":");
      var3 = items;
      var4 = items.length;

      for(var5 = 0; var5 < var4; ++var5) {
        item = var3[var5];
        if (!item.equals("")) {
          values = item.split(";");
          if (values.length >= 2) {
            recId = Integer.parseInt(values[0]);
            price = Long.parseLong(values[1]);
            if (this.findRecipe(recId)) {
              this._createList.add(new ManufactureItem(recId, price));
            }
          }
        }
      }

      var = this.getVar("manufacturename");
      if (var != null) {
        this.setManufactureName(var);
      }
    }

  }

  public void restoreRecipeBook() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("SELECT id FROM character_recipebook WHERE char_id=?");
      statement.setInt(1, this.getObjectId());
      rset = statement.executeQuery();

      while(rset.next()) {
        int recipeId = rset.getInt("id");
        Recipe recipe = RecipeHolder.getInstance().getRecipeById(recipeId);
        this.registerRecipe(recipe, false);
      }
    } catch (Exception var9) {
      log.warn("count not recipe skills:" + var9);
      log.error("", var9);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public int getMountType() {
    switch(this.getMountNpcId()) {
      case 12526:
      case 12527:
      case 12528:
      case 16038:
      case 16039:
      case 16040:
      case 16068:
        return 1;
      case 12621:
        return 2;
      case 16037:
      case 16041:
      case 16042:
        return 3;
      default:
        return 0;
    }
  }

  public double getColRadius() {
    int mountTemplate;
    NpcTemplate mountNpcTemplate;
    if (this.getTransformation() != 0) {
      mountTemplate = this.getTransformationTemplate();
      if (mountTemplate != 0) {
        mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
        if (mountNpcTemplate != null) {
          return mountNpcTemplate.collisionRadius;
        }
      }
    } else if (this.isMounted()) {
      mountTemplate = this.getMountNpcId();
      if (mountTemplate != 0) {
        mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
        if (mountNpcTemplate != null) {
          return mountNpcTemplate.collisionRadius;
        }
      }
    }

    return this.getBaseTemplate().collisionRadius;
  }

  public double getColHeight() {
    int mountTemplate;
    NpcTemplate mountNpcTemplate;
    if (this.getTransformation() != 0) {
      mountTemplate = this.getTransformationTemplate();
      if (mountTemplate != 0) {
        mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
        if (mountNpcTemplate != null) {
          return mountNpcTemplate.collisionHeight;
        }
      }
    } else if (this.isMounted()) {
      mountTemplate = this.getMountNpcId();
      if (mountTemplate != 0) {
        mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
        if (mountNpcTemplate != null) {
          return mountNpcTemplate.collisionHeight;
        }
      }
    }

    return this.getBaseTemplate().collisionHeight;
  }

  public void setReflection(Reflection reflection) {
    if (this.getReflection() != reflection) {
      super.setReflection(reflection);
      if (this._summon != null && !this._summon.isDead()) {
        this._summon.setReflection(reflection);
      }

      if (reflection != ReflectionManager.DEFAULT) {
        String var = this.getVar("reflection");
        if (var == null || !var.equals(String.valueOf(reflection.getId()))) {
          this.setVar("reflection", String.valueOf(reflection.getId()), -1L);
        }
      } else {
        this.unsetVar("reflection");
      }

      if (this.getActiveClass() != null) {
        this.getInventory().validateItems();
        if (this.getPet() != null && (this.getPet().getNpcId() == 14916 || this.getPet().getNpcId() == 14917)) {
          this.getPet().unSummon();
        }
      }

    }
  }

  public void setBuyListId(int listId) {
    this._buyListId = listId;
  }

  public int getBuyListId() {
    return this._buyListId;
  }

  public int getIncorrectValidateCount() {
    return this._incorrectValidateCount;
  }

  public int setIncorrectValidateCount(int count) {
    return this._incorrectValidateCount = count;
  }

  public int getExpandInventory() {
    return this._expandInventory;
  }

  public void setExpandInventory(int inventory) {
    this._expandInventory = inventory;
  }

  public int getExpandWarehouse() {
    return this._expandWarehouse;
  }

  public void setExpandWarehouse(int warehouse) {
    this._expandWarehouse = warehouse;
  }

  public void enterMovieMode() {
    if (!this.isInMovie()) {
      this.setTarget(null);
      this.stopMove();
      this.setIsInMovie(true);
      this.sendPacket(new CameraMode(1));
    }
  }

  public void leaveMovieMode() {
    this.setIsInMovie(false);
    this.sendPacket(new CameraMode(0));
    this.broadcastCharInfo();
  }

  public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration) {
    this.sendPacket(new SpecialCamera(target.getObjectId(), dist, yaw, pitch, time, duration));
  }

  public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk) {
    this.sendPacket(new SpecialCamera(target.getObjectId(), dist, yaw, pitch, time, duration, turn, rise, widescreen, unk));
  }

  public void setMovieId(int id) {
    this._movieId = id;
  }

  public int getMovieId() {
    return this._movieId;
  }

  public boolean isInMovie() {
    return this._isInMovie;
  }

  public void setIsInMovie(boolean state) {
    this._isInMovie = state;
  }

  public void showQuestMovie(SceneMovie movie) {
    if (!this.isInMovie()) {
      this.sendActionFailed();
      this.setTarget(null);
      this.stopMove();
      this.setMovieId(movie.getId());
      this.setIsInMovie(true);
      this.sendPacket(movie.packet(this));
    }
  }

  public void showQuestMovie(int movieId) {
    if (!this.isInMovie()) {
      this.sendActionFailed();
      this.setTarget(null);
      this.stopMove();
      this.setMovieId(movieId);
      this.setIsInMovie(true);
      this.sendPacket(new ExStartScenePlayer(movieId));
    }
  }

  public void setAutoLoot(boolean enable) {
    if (Config.AUTO_LOOT_INDIVIDUAL) {
      this._autoLoot = enable;
      this.setVar("AutoLoot", String.valueOf(enable), -1L);
    }

  }

  public void setAutoLootHerbs(boolean enable) {
    if (Config.AUTO_LOOT_INDIVIDUAL) {
      this.AutoLootHerbs = enable;
      this.setVar("AutoLootHerbs", String.valueOf(enable), -1L);
    }

  }

  public void setAutoLootAdena(boolean enable) {
    if (Config.AUTO_LOOT_INDIVIDUAL) {
      this.AutoLootAdena = enable;
      this.setVar("AutoLootAdend", String.valueOf(enable), -1L);
    }

  }

  public boolean isAutoLootEnabled() {
    return this._autoLoot;
  }

  public boolean isAutoLootHerbsEnabled() {
    return this.AutoLootHerbs;
  }

  public boolean isAutoLootAdenaEnabled() {
    return this.AutoLootAdena;
  }

  public final void reName(String name, boolean saveToDB) {
    this.setName(name);
    if (saveToDB) {
      this.saveNameToDB();
    }

    if (this.isNoble()) {
      NoblesController.getInstance().renameNoble(this.getObjectId(), name);
    }

  }

  public final void reName(String name) {
    this.reName(name, false);
  }

  public final void saveNameToDB() {
    Connection con = null;
    PreparedStatement st = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?");
      st.setString(1, this.getName());
      st.setInt(2, this.getObjectId());
      st.executeUpdate();
    } catch (Exception var7) {
      log.error("", var7);
    } finally {
      DbUtils.closeQuietly(con, st);
    }

  }

  public Player getPlayer() {
    return this;
  }

  public int getTalismanCount() {
    return (int)this.calcStat(Stats.TALISMANS_LIMIT, 0.0D, null, null);
  }

  public final void disableDrop(int time) {
    this._dropDisabled = System.currentTimeMillis() + (long)time;
  }

  public final boolean isDropDisabled() {
    return this._dropDisabled > System.currentTimeMillis();
  }

  public void setPetControlItem(int itemObjId) {
    this.setPetControlItem(this.getInventory().getItemByObjectId(itemObjId));
  }

  public void setPetControlItem(ItemInstance item) {
    this._petControlItem = item;
  }

  public ItemInstance getPetControlItem() {
    return this._petControlItem;
  }

  public boolean isActive() {
    return this.isActive.get();
  }

  public void setActive() {
    this.setNonAggroTime(0L);
    if (!this.isActive.getAndSet(true)) {
      this.onActive();
    }
  }

  private void onActive() {
    this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONLOGIN);
    if (this.getPetControlItem() != null) {
      ThreadPoolManager.getInstance().execute(new RunnableImpl() {
        public void runImpl() {
          if (Player.this.getPetControlItem() != null) {
            Player.this.summonPet();
          }

        }
      });
    }

  }

  public void summonPet() {
    if (this.getPet() == null) {
      ItemInstance controlItem = this.getPetControlItem();
      if (controlItem != null) {
        int npcId = PetDataTable.getSummonId(controlItem);
        if (npcId != 0) {
          NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
          if (petTemplate != null) {
            PetInstance pet = PetInstance.restore(controlItem, petTemplate, this);
            if (pet != null) {
              this.setPet(pet);
              pet.setTitle(this.getName());
              if (!pet.isRespawned()) {
                pet.setCurrentHp(pet.getMaxHp(), false);
                pet.setCurrentMp(pet.getMaxMp());
                pet.setCurrentFed(pet.getMaxFed());
                pet.updateControlItem();
                pet.store();
              }

              pet.getInventory().restore();
              pet.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
              pet.setReflection(this.getReflection());
              pet.spawnMe(Location.findPointToStay(this, 50, 70));
              pet.setRunning();
              pet.setFollowMode(true);
              pet.getInventory().validateItems();
              if (pet instanceof PetBabyInstance) {
                ((PetBabyInstance)pet).startBuffTask();
              }

            }
          }
        }
      }
    }
  }

  public Collection<TrapInstance> getTraps() {
    if (this._traps == null) {
      return null;
    } else {
      Collection<TrapInstance> result = new ArrayList(this.getTrapsCount());

      for (Integer trapId : this._traps.keySet()) {
        TrapInstance trap;
        if ((trap = (TrapInstance) GameObjectsStorage.get(this._traps.get(trapId))) != null) {
          result.add(trap);
        } else {
          this._traps.remove(trapId);
        }
      }

      return result;
    }
  }

  public int getTrapsCount() {
    return this._traps == null ? 0 : this._traps.size();
  }

  public void addTrap(TrapInstance trap) {
    if (this._traps == null) {
      this._traps = new HashMap<>();
    }

    this._traps.put(trap.getObjectId(), trap.getStoredId());
  }

  public void removeTrap(TrapInstance trap) {
    Map<Integer, Long> traps = this._traps;
    if (traps != null && !traps.isEmpty()) {
      traps.remove(trap.getObjectId());
    }
  }

  public void destroyFirstTrap() {
    Map<Integer, Long> traps = this._traps;
    if (traps != null && !traps.isEmpty()) {
      Iterator var3 = traps.keySet().iterator();
      if (var3.hasNext()) {
        Integer trapId = (Integer)var3.next();
        TrapInstance trap;
        if ((trap = (TrapInstance)GameObjectsStorage.get(traps.get(trapId))) != null) {
          trap.deleteMe();
        }
      }
    }
  }

  public void destroyAllTraps() {
    Map<Integer, Long> traps = this._traps;
    if (traps != null && !traps.isEmpty()) {
      List<TrapInstance> toRemove = new ArrayList<>();
      Iterator var3 = traps.keySet().iterator();

      while(var3.hasNext()) {
        Integer trapId = (Integer)var3.next();
        toRemove.add((TrapInstance)GameObjectsStorage.get(traps.get(trapId)));
      }

      var3 = toRemove.iterator();

      while(var3.hasNext()) {
        TrapInstance t = (TrapInstance)var3.next();
        if (t != null) {
          t.deleteMe();
        }
      }

    }
  }

  public PlayerListenerList getListeners() {
    if (this.listeners == null) {
      synchronized(this) {
        if (this.listeners == null) {
          this.listeners = new PlayerListenerList(this);
        }
      }
    }

    return (PlayerListenerList)this.listeners;
  }

  public PlayerStatsChangeRecorder getStatsRecorder() {
    if (this._statsRecorder == null) {
      synchronized(this) {
        if (this._statsRecorder == null) {
          this._statsRecorder = new PlayerStatsChangeRecorder(this);
        }
      }
    }

    return (PlayerStatsChangeRecorder)this._statsRecorder;
  }

  public int getHoursInGame() {
    ++this._hoursInGame;
    return this._hoursInGame;
  }

  public void startHourlyTask() {
    this._hourlyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HourlyTask(this), 3600000L, 3600000L);
  }

  public void stopHourlyTask() {
    if (this._hourlyTask != null) {
      this._hourlyTask.cancel(false);
      this._hourlyTask = null;
    }

  }

  public long getPremiumPoints() {
    return Config.GAME_POINT_ITEM_ID > 0 ? ItemFunctions.getItemCount(this, Config.GAME_POINT_ITEM_ID) : 0L;
  }

  public void reducePremiumPoints(int val) {
    if (Config.GAME_POINT_ITEM_ID > 0) {
      ItemFunctions.removeItem(this, Config.GAME_POINT_ITEM_ID, val, true);
    }

  }

  public String getSessionVar(String key) {
    return this._userSession == null ? null : this._userSession.get(key);
  }

  public void setSessionVar(String key, String val) {
    if (this._userSession == null) {
      this._userSession = new ConcurrentHashMap();
    }

    if (val != null && !val.isEmpty()) {
      this._userSession.put(key, val);
    } else {
      this._userSession.remove(key);
    }

  }

  public FriendList getFriendList() {
    return this._friendList;
  }

  public boolean isNotShowTraders() {
    return this._notShowTraders;
  }

  public void setNotShowTraders(boolean notShowTraders) {
    this._notShowTraders = notShowTraders;
  }

  public boolean isDebug() {
    return this._debug;
  }

  public void setDebug(boolean b) {
    this._debug = b;
  }

  public void sendItemList(boolean show) {
    ItemInstance[] items = this.getInventory().getItems();
    LockType lockType = this.getInventory().getLockType();
    int[] lockItems = this.getInventory().getLockItems();
    this.sendPacket(new ItemList(items.length, items, show, lockType, lockItems));
  }

  public boolean isPlayer() {
    return true;
  }

  public void startAttackStanceTask() {
    this.startAttackStanceTask0();
    Summon summon = this.getPet();
    if (summon != null) {
      summon.startAttackStanceTask0();
    }

  }

  public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
    super.displayGiveDamageMessage(target, damage, crit, miss, shld, magic);
    if (crit) {
      if (magic) {
        this.sendPacket(new SystemMessage(1280));
      } else {
        this.sendPacket(new SystemMessage(44));
      }
    }

    if (miss) {
      this.sendPacket(new SystemMessage(43));
    } else if (!target.isDamageBlocked()) {
      this.sendPacket((new SystemMessage(35)).addNumber(damage));
    }

    if (target.isPlayer()) {
      if (shld && damage > 1) {
        target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
      } else if (shld && damage == 1) {
        target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
      }
    }

  }

  public void displayReceiveDamageMessage(Creature attacker, int damage) {
    if (attacker != this) {
      this.sendPacket((new SystemMessage(36)).addName(attacker).addNumber((long)damage));
    }

  }

  public IntObjectMap<String> getPostFriends() {
    return this._postFriends;
  }

  public boolean isSharedGroupDisabled(int groupId) {
    TimeStamp sts = this._sharedGroupReuses.get(groupId);
    if (sts == null) {
      return false;
    } else if (sts.hasNotPassed()) {
      return true;
    } else {
      this._sharedGroupReuses.remove(groupId);
      return false;
    }
  }

  public TimeStamp getSharedGroupReuse(int groupId) {
    return this._sharedGroupReuses.get(groupId);
  }

  public void addSharedGroupReuse(int group, TimeStamp stamp) {
    this._sharedGroupReuses.put(group, stamp);
  }

  public Collection<org.napile.primitive.maps.IntObjectMap.Entry<TimeStamp>> getSharedGroupReuses() {
    return this._sharedGroupReuses.entrySet();
  }

  public void sendReuseMessage(ItemInstance item) {
    TimeStamp sts = this.getSharedGroupReuse(item.getTemplate().getReuseGroup());
    if (sts == null || !sts.hasNotPassed()) {
      this.sendPacket((new SystemMessage(48)).addItemName(item.getTemplate().getItemId()));
    }

  }

  public void ask(ConfirmDlg dlg, OnAnswerListener listener) {
    if (this._askDialog == null) {
      int rnd = Rnd.nextInt();
      this._askDialog = new ImmutablePair(rnd, listener);
      dlg.setRequestId(rnd);
      this.sendPacket(dlg);
    }
  }

  public Pair<Integer, OnAnswerListener> getAskListener(boolean clear) {
    if (!clear) {
      return this._askDialog;
    } else {
      Pair<Integer, OnAnswerListener> ask = this._askDialog;
      this._askDialog = null;
      return ask;
    }
  }

  public boolean isDead() {
    return !this.isOlyParticipant() && !this.isInDuel() ? super.isDead() : this.getCurrentHp() <= 1.0D;
  }

  public boolean hasPrivilege(Privilege privilege) {
    return this._clan != null && (this.getClanPrivileges() & privilege.mask()) == privilege.mask();
  }

  public MatchingRoom getMatchingRoom() {
    return this._matchingRoom;
  }

  public void setMatchingRoom(MatchingRoom matchingRoom) {
    this._matchingRoom = matchingRoom;
  }

  public void dispelBuffs() {
    Iterator var1 = this.getEffectList().getAllEffects().iterator();

    Effect e;
    while(var1.hasNext()) {
      e = (Effect)var1.next();
      if (!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath()) {
        this.sendPacket((new SystemMessage(749)).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
        e.exit();
      }
    }

    if (this.getPet() != null) {
      var1 = this.getPet().getEffectList().getAllEffects().iterator();

      while(var1.hasNext()) {
        e = (Effect)var1.next();
        if (!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath()) {
          e.exit();
        }
      }
    }

  }

  public void setInstanceReuse(int id, long time) {
    CustomMessage msg = (new CustomMessage("INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE", this)).addString(this.getName());
    this.sendMessage(msg);
    this._instancesReuses.put(id, time);
    mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", this.getObjectId(), id, time);
  }

  public void removeInstanceReuse(int id) {
    if (this._instancesReuses.remove(id) != null) {
      mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", this.getObjectId(), id);
    }

  }

  public void removeAllInstanceReuses() {
    this._instancesReuses.clear();
    mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=?", this.getObjectId());
  }

  public void removeInstanceReusesByGroupId(int groupId) {

    for (int i : InstantZoneHolder.getInstance().getSharedReuseInstanceIdsByGroup(groupId)) {
      if (this.getInstanceReuse(i) != null) {
        this.removeInstanceReuse(i);
      }
    }

  }

  public Long getInstanceReuse(int id) {
    return this._instancesReuses.get(id);
  }

  public Map<Integer, Long> getInstanceReuses() {
    return this._instancesReuses;
  }

  private void loadInstanceReuses() {
    Connection con = null;
    PreparedStatement offline = null;
    ResultSet rs = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      offline = con.prepareStatement("SELECT * FROM character_instances WHERE obj_id = ?");
      offline.setInt(1, this.getObjectId());
      rs = offline.executeQuery();

      while(rs.next()) {
        int id = rs.getInt("id");
        long reuse = rs.getLong("reuse");
        this._instancesReuses.put(id, reuse);
      }
    } catch (Exception var10) {
      log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, offline, rs);
    }

  }

  public Reflection getActiveReflection() {
    Reflection[] var1 = ReflectionManager.getInstance().getAll();
    int var2 = var1.length;

    for (Reflection r : var1) {
      if (r != null && ArrayUtils.contains(r.getVisitors(), this.getObjectId())) {
        return r;
      }
    }

    return null;
  }

  public boolean canEnterInstance(int instancedZoneId) {
    InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
    if (this.isDead()) {
      return false;
    } else if (ReflectionManager.getInstance().size() > Config.MAX_REFLECTIONS_COUNT) {
      this.sendMessage(new CustomMessage("THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED", this));
      return false;
    } else if (iz == null) {
      this.sendPacket(SystemMsg.SYSTEM_ERROR);
      return false;
    } else if (ReflectionManager.getInstance().getCountByIzId(instancedZoneId) >= iz.getMaxChannels()) {
      this.sendMessage(new CustomMessage("THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED", this));
      return false;
    } else {
      return iz.getEntryType().canEnter(this, iz);
    }
  }

  public boolean canReenterInstance(int instancedZoneId) {
    InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
    if (this.getActiveReflection() != null && this.getActiveReflection().getInstancedZoneId() != instancedZoneId) {
      this.sendMessage(new CustomMessage("YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON", this));
      return false;
    } else {
      if (iz.isDispelBuffs()) {
        this.dispelBuffs();
      }

      return iz.getEntryType().canReEnter(this, iz);
    }
  }

  public int getBattlefieldChatId() {
    return this._battlefieldChatId;
  }

  public void setBattlefieldChatId(int battlefieldChatId) {
    this._battlefieldChatId = battlefieldChatId;
  }

  public void broadCast(IStaticPacket... packet) {
    this.sendPacket(packet);
  }

  public Iterator<Player> iterator() {
    return Collections.singleton(this).iterator();
  }

  public PlayerGroup getPlayerGroup() {
    if (this.getParty() != null) {
      return this.getParty().getCommandChannel() != null ? this.getParty().getCommandChannel() : this.getParty();
    } else {
      return this;
    }
  }

  public boolean isActionBlocked(String action) {
    return this._blockedActions.contains(action);
  }

  public void blockActions(String... actions) {
    Collections.addAll(this._blockedActions, actions);
  }

  public void unblockActions(String... actions) {
    String[] var2 = actions;
    int var3 = actions.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      String action = var2[var4];
      this._blockedActions.remove(action);
    }

  }

  public void addRadar(int x, int y, int z) {
    this.sendPacket(new RadarControl(0, 1, x, y, z));
  }

  public void addRadarWithMap(int x, int y, int z) {
    this.sendPacket(new RadarControl(0, 2, x, y, z));
  }

  public long getAfterTeleportPortectionTime() {
    return this._afterTeleportPortectionTime;
  }

  public void setAfterTeleportPortectionTime(long afterTeleportPortectionTime) {
    this._afterTeleportPortectionTime = afterTeleportPortectionTime;
  }

  private class ForceCleanupTask implements Runnable {
    private ForceCleanupTask() {
    }

    public void run() {
      long nextDelay = 600000L - (System.currentTimeMillis() - Player.this._increasedForceLastUpdateTimeStamp);
      if (nextDelay > 1000L) {
        Player.this._increasedForceCleanupTask = ThreadPoolManager.getInstance().schedule(Player.this.new ForceCleanupTask(), nextDelay);
      } else {
        Player.this._increasedForce = 0;
        Player.this.sendEtcStatusUpdate();
        Player.this._increasedForceCleanupTask = null;
      }
    }
  }

  private class UserInfoTask extends RunnableImpl {
    private UserInfoTask() {
    }

    public void runImpl() throws Exception {
      Player.this.sendUserInfoImpl();
      Player.this._userInfoTask = null;
    }
  }

  public class BroadcastCharInfoTask extends RunnableImpl {
    public BroadcastCharInfoTask() {
    }

    public void runImpl() throws Exception {
      Player.this.broadcastCharInfoImpl();
      Player.this._broadcastCharInfoTask = null;
    }
  }

  private static class MoveToLocationActionForOffload extends MoveToLocationAction {
    public MoveToLocationActionForOffload(Creature actor, Location moveFrom, Location moveTo, boolean ignoreGeo, int indent, boolean pathFind) {
      super(actor, moveFrom, moveTo, ignoreGeo, indent, pathFind);
    }

    private void tryOffloadedMove() {
      Player player = (Player)this.getActor();
      Player.MoveToLocationOffloadData mtlOffloadData;
      if (player != null && (mtlOffloadData = player._mtlOffloadData.get()) != null && player._mtlOffloadData.compareAndSet(mtlOffloadData, null)) {
        player.moveToLocation(mtlOffloadData.getDest(), mtlOffloadData.getIndent(), mtlOffloadData.isPathfind());
      }

    }

    protected boolean onTick(double done) {
      boolean result;
      try {
        result = super.onTick(done);
      } finally {
        this.tryOffloadedMove();
      }

      return result;
    }

    protected void onFinish(boolean finishedWell, boolean isInterrupted) {
      try {
        super.onFinish(finishedWell, isInterrupted);
      } finally {
        this.tryOffloadedMove();
      }

    }
  }

  private static class MoveToLocationOffloadData {
    private final Location _dest;
    private final int _indent;
    private final boolean _pathfind;

    public MoveToLocationOffloadData(Location dest, int indent, boolean pathfind) {
      this._dest = dest;
      this._indent = indent;
      this._pathfind = pathfind;
    }

    public Location getDest() {
      return this._dest;
    }

    public int getIndent() {
      return this._indent;
    }

    public boolean isPathfind() {
      return this._pathfind;
    }
  }

  private class UpdateEffectIcons extends RunnableImpl {
    private UpdateEffectIcons() {
    }

    public void runImpl() throws Exception {
      Player.this.updateEffectIconsImpl();
      Player.this._updateEffectIconsTask = null;
    }
  }

  public enum EPledgeRank {
    VAGABOND(0),
    VASSAL(1),
    HEIR(2),
    KNIGHT(3),
    WISEMAN(4),
    BARON(5),
    VISCOUNT(6),
    COUNT(7),
    MARQUIS(8);

    private final int _rankId;
    public static Player.EPledgeRank[] VALUES = values();

    EPledgeRank(int rankId) {
      this._rankId = rankId;
    }

    public int getRankId() {
      return this._rankId;
    }

    public static Player.EPledgeRank getPledgeRank(int pledgeRankId) {
      Player.EPledgeRank[] var1 = VALUES;
      int var2 = var1.length;

      for (EPledgeRank pledgeRank : var1) {
        if (pledgeRank.getRankId() == pledgeRankId) {
          return pledgeRank;
        }
      }

      return null;
    }
  }
}
