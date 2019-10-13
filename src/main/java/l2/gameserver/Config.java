//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.configuration.ExProperties;
import l2.commons.net.nio.impl.SelectorConfig;
import l2.gameserver.data.xml.parser.ChatFilterParser;
import l2.gameserver.model.actor.instances.player.Bonus;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.base.PlayerAccess;
import l2.gameserver.model.quest.QuestRates;
import l2.gameserver.network.authcomm.ServerType;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.templates.item.ItemTemplate.Grade;
import l2.gameserver.templates.item.ItemTemplate.ItemClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.napile.primitive.lists.impl.CArrayIntList;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class Config {
  private static final Logger _log = LoggerFactory.getLogger(Config.class);
  public static final int NCPUS = Runtime.getRuntime().availableProcessors();
  public static final String OTHER_CONFIG_FILE = "config/other.properties";
  public static final String RESIDENCE_CONFIG_FILE = "config/residence.properties";
  public static final String SPOIL_CONFIG_FILE = "config/spoil.properties";
  public static final String CLAN_CONFIG_FILE = "config/clan.properties";
  public static final String ALT_SETTINGS_FILE = "config/altsettings.properties";
  public static final String BOSS_SETTINGS_FILE = "config/bosses.properties";
  public static final String FORMULAS_CONFIGURATION_FILE = "config/formulas.properties";
  public static final String PVP_CONFIG_FILE = "config/pvp.properties";
  public static final String TELNET_CONFIGURATION_FILE = "config/telnet.properties";
  public static final String CONFIGURATION_FILE = "config/server.properties";
  public static final String AI_CONFIG_FILE = "config/ai.properties";
  public static final String GEODATA_CONFIG_FILE = "config/geodata.properties";
  public static final String EVENTS_CONFIG_FILE = "config/events.properties";
  public static final String SERVICES_FILE = "config/services.properties";
  public static final String SERVICES_RATE_BONUS_XML_FILE = "config/services_rate_bonus.xml";
  public static final String OLYMPIAD = "config/olympiad.properties";
  public static final String QUEST_RATE_FILE = "config/quest_rates.properties";
  public static final String CHATFILTERS_CONFIG_FILE = "config/chatfilters.xml";
  public static final String GM_PERSONAL_ACCESS_FILE = "config/GMAccess.xml";
  public static final String GM_ACCESS_FILES_DIR = "config/GMAccess.d/";
  public static int HTM_CACHE_MODE;
  public static int[] PORTS_GAME;
  public static String GAMESERVER_HOSTNAME;
  public static String DATABASE_HOST;
  public static int DATABASE_PORT;
  public static String DATABASE_NAME;
  public static String DATABASE_USER;
  public static String DATABASE_PASS;
  public static int DATABASE_MAX_CONN;
  public static int DATABASE_TIMEOUT;
  public static boolean AUTOSAVE;
  public static boolean AUTOSAVE_ITEMS;
  public static boolean ALLOW_MULILOGIN;
  public static long USER_INFO_INTERVAL;
  public static boolean BROADCAST_STATS_INTERVAL;
  public static long BROADCAST_CHAR_INFO_INTERVAL;
  public static int EFFECT_TASK_MANAGER_COUNT;
  public static int MAXIMUM_ONLINE_USERS;
  public static boolean DONTLOADSPAWN;
  public static boolean DONTLOADQUEST;
  public static int MAX_REFLECTIONS_COUNT;
  public static int SHIFT_BY;
  public static int SHIFT_BY_Z;
  public static int MAP_MIN_Z;
  public static int MAP_MAX_Z;
  public static int CHAT_MESSAGE_MAX_LEN;
  public static ChatType[] BAN_CHANNEL_LIST;
  public static boolean BANCHAT_ANNOUNCE;
  public static boolean BANCHAT_ANNOUNCE_FOR_ALL_WORLD;
  public static boolean BANCHAT_ANNOUNCE_NICK;
  public static boolean SAVING_SPS;
  public static boolean MANAHEAL_SPS_BONUS;
  public static int ALT_ADD_RECIPES;
  public static int ALT_MAX_ALLY_SIZE;
  public static int ALT_PARTY_DISTRIBUTION_RANGE;
  public static int ALT_PARTY_DISTRIBUTION_DIFF_LEVEL_LIMIT;
  public static double[] ALT_PARTY_BONUS;
  public static double ALT_ABSORB_DAMAGE_MODIFIER;
  public static double ALT_POLE_DAMAGE_MODIFIER;
  public static int ALT_REMOVE_SKILLS_ON_DELEVEL;
  public static boolean ALT_USE_HOT_SPIRIT_DEBUFF;
  public static int ALT_HOT_SPIRIT_CHANCE_DEBUFF;
  public static boolean ALT_TELEPORT_FROM_SEVEN_SING_MONSTER;
  public static boolean ALT_ENABLE_SEVEN_SING_TELEPORTER_PROTECTION;
  public static boolean ALT_MAMONS_CHECK_SEVEN_SING_STATUS;
  public static long L2TOPRU_DELAY;
  public static String L2TOPRU_PREFIX;
  public static String L2TOPRU_WEB_VOTE_URL;
  public static String L2TOPRU_SMS_VOTE_URL;
  public static int L2TOPRU_WEB_REWARD_ITEMID;
  public static int L2TOPRU_WEB_REWARD_ITEMCOUNT;
  public static int L2TOPRU_SMS_REWARD_ITEMID;
  public static int L2TOPRU_SMS_REWARD_ITEMCOUNT;
  public static boolean L2TOPRU_SMS_REWARD_VOTE_MULTI;
  public static boolean L2TOPZONE_ENABLED;
  public static long L2TOPZONE_VOTE_TIME_TO_LIVE;
  public static int L2TOPZONE_SERVER_ID;
  public static String L2TOPZONE_API_KEY;
  public static int L2TOPZONE_REWARD_ITEM_ID;
  public static int L2TOPZONE_REWARD_ITEM_COUNT;
  public static Calendar CASTLE_VALIDATION_DATE;
  public static boolean ALT_PCBANG_POINTS_ENABLED;
  public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
  public static int ALT_PCBANG_POINTS_BONUS;
  public static int ALT_PCBANG_POINTS_DELAY;
  public static int ALT_PCBANG_POINTS_MIN_LVL;
  public static boolean ALT_DEBUG_ENABLED;
  public static boolean ALT_DEBUG_PVP_ENABLED;
  public static boolean ALT_DEBUG_PVP_DUEL_ONLY;
  public static boolean ALT_DEBUG_PVE_ENABLED;
  public static boolean ALT_CHECK_CERTIFICATION_ITEMS;
  public static int SCHEDULED_THREAD_POOL_SIZE;
  public static int EXECUTOR_THREAD_POOL_SIZE;
  public static boolean ENABLE_RUNNABLE_STATS;
  public static SelectorConfig SELECTOR_CONFIG = new SelectorConfig();
  public static boolean AUTO_LOOT;
  public static boolean AUTO_LOOT_HERBS;
  public static boolean AUTO_LOOT_INDIVIDUAL;
  public static boolean AUTO_LOOT_FROM_RAIDS;
  public static boolean AUTO_LOOT_ADENA;
  public static boolean AUTO_LOOT_PK;
  public static String CNAME_TEMPLATE;
  public static String CNAME_FORBIDDEN_PATTERN;
  public static String[] CNAME_FORBIDDEN_NAMES;
  public static String CUSTOM_CNAME_TEMPLATE;
  public static int CNAME_MAXLEN = 32;
  public static String CLAN_NAME_TEMPLATE;
  public static String CLAN_TITLE_TEMPLATE;
  public static String ALLY_NAME_TEMPLATE;
  public static boolean GLOBAL_SHOUT;
  public static int GLOBAL_SHOUT_MIN_LEVEL;
  public static int GLOBAL_SHOUT_MIN_PVP_COUNT;
  public static boolean GLOBAL_TRADE_CHAT;
  public static int GLOBAL_TRADE_CHAT_MIN_LEVEL;
  public static int GLOBAL_TRADE_MIN_PVP_COUNT;
  public static int CHAT_RANGE;
  public static int SHOUT_OFFSET;
  public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
  public static boolean RAID_TELE_TO_HOME_FROM_PVP_ZONES;
  public static boolean RAID_TELE_TO_HOME_FROM_TOWN_ZONES;
  public static double ALT_RAID_RESPAWN_MULTIPLIER;
  public static long FWA_LIMITUNTILSLEEPANTHARAS;
  public static long FWA_LIMITMAXUNTILSLEEPANTHARAS;
  public static long VALAKAS_CLEAR_ZONE_IF_ALL_DIE;
  public static long FWA_FIXTIMEINTERVALOFANTHARAS;
  public static long FWA_RANDOMINTERVALOFANTHARAS;
  public static String FWA_FIXTIMEPATTERNOFANTHARAS;
  public static long FWA_APPTIMEOFANTHARAS;
  public static long FWV_LIMITUNTILSLEEPVALAKAS;
  public static String FWA_FIXTIMEPATTERNOFVALAKAS;
  public static long ANTHARAS_CLEAR_ZONE_IF_ALL_DIE;
  public static long SAILREN_CLEAR_ZONE_IF_ALL_DIE;
  public static long BAIUM_CLEAR_ZONE_IF_ALL_DIE;
  public static long FWV_APPTIMEOFVALAKAS;
  public static long FWA_LIMITMAXUNTILSLEEPVALAKAS;
  public static long FWV_FIXINTERVALOFVALAKAS;
  public static long FWV_RANDOMINTERVALOFVALAKAS;
  public static long FWB_LIMITUNTILSLEEPBAIUM;
  public static long FWB_FIXINTERVALOFBAIUM;
  public static long FWB_RANDOMINTERVALOFBAIUM;
  public static long FWA_LIMITMAXUNTILSLEEPBAIUM;
  public static String FWA_FIXTIMEPATTERNOFBAIUM;
  public static long FWS_ACTIVITYTIMEOFMOBS;
  public static long FWS_FIXINTERVALOFSAILRENSPAWN;
  public static long FWS_RANDOMINTERVALOFSAILRENSPAWN;
  public static String FWA_FIXTIMEPATTERNOFSAILREN;
  public static long FWS_INTERVALOFNEXTMONSTER;
  public static long FWF_FIXINTERVALOFFRINTEZZA;
  public static long FWF_RANDOMINTERVALOFFRINTEZZA;
  public static String FWA_FIXTIMEPATTERNOFFRINTEZZA;
  public static int FRINTEZZA_TOMB_TIMEOUT;
  public static int MINIONS_RESPAWN_INTERVAL;
  public static boolean ENABLE_ON_TIME_DOOR_OPEN;
  public static int ON_TIME_OPEN_DOOR_ID;
  public static String ON_TIME_OPEN_PATTERN;
  public static boolean ZAKEN_USE_TELEPORT;
  public static boolean ALT_ALLOW_DROP_AUGMENTED;
  public static boolean ALT_GAME_UNREGISTER_RECIPE;
  public static int SS_ANNOUNCE_PERIOD;
  public static boolean PETITIONING_ALLOWED;
  public static boolean CAN_PETITION_TO_OFFLINE_GM;
  public static int MAX_PETITIONS_PER_PLAYER;
  public static int MAX_PETITIONS_PENDING;
  public static boolean ALT_GAME_SHOW_DROPLIST;
  public static boolean ALT_FULL_NPC_STATS_PAGE;
  public static boolean ALLOW_NPC_SHIFTCLICK;
  public static boolean ALT_ALLOW_SELL_COMMON;
  public static boolean ALT_ALLOW_SHADOW_WEAPONS;
  public static boolean ALT_ALLOW_REMOTE_SELL_ITEMS_TO_SHOP;
  public static boolean ALT_ALLOW_REMOTE_USE_CARGO_BOX;
  public static int[] ALT_DISABLED_MULTISELL;
  public static int[] ALT_SHOP_PRICE_LIMITS;
  public static int[] ALT_SHOP_UNALLOWED_ITEMS;
  public static long ALT_SHOP_REFUND_SELL_DIVISOR;
  public static int[] ALT_ALLOWED_PET_POTIONS;
  public static int[] ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS;
  public static int ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY;
  public static double SKILLS_CHANCE_MOD;
  public static double SKILLS_CHANCE_MIN;
  public static double SKILLS_CHANCE_POW;
  public static double SKILLS_CHANCE_CAP;
  public static boolean ALT_SAVE_UNSAVEABLE;
  public static int ALT_SAVE_EFFECTS_REMAINING_TIME;
  public static boolean ALT_SUBLASS_SKILL_TRANSFER;
  public static boolean ALT_SHOW_REUSE_MSG;
  public static boolean ALT_DELETE_SA_BUFFS;
  public static boolean BUFF_STICK_FOR_ALL;
  public static int ALT_PASSIVE_NOBLESS_ID;
  public static int SKILLS_CAST_TIME_MIN;
  public static int SKILLS_DISPEL_MOD_MAX;
  public static int SKILLS_DISPEL_MOD_MIN;
  public static boolean CHAR_TITLE;
  public static String ADD_CHAR_TITLE;
  public static String DISCONNECTED_PLAYER_TITLE;
  public static int DISCONNECTED_PLAYER_TITLE_COLOR;
  public static boolean ALT_SOCIAL_ACTION_REUSE;
  public static boolean ALT_DISABLE_SPELLBOOKS;
  public static boolean ALT_WEAK_SKILL_LEARN;
  public static boolean ALT_GAME_DELEVEL;
  public static boolean ALT_ALLOW_CUSTOM_HERO;
  public static long CUSTOM_HERO_STATUS_TIME;
  public static boolean ALT_HAIR_TO_ACC_SLOT;
  public static int ALT_NEW_CHARACTER_LEVEL;
  public static int ALT_DEFAULT_ACCESS_LEVEL;
  public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
  public static boolean ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM;
  public static int ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID;
  public static int ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT;
  public static boolean ALTSUBCLASS_LIST_ALL;
  public static boolean ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL;
  public static boolean ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF;
  public static int ALT_GAME_LEVEL_TO_GET_SUBCLASS;
  public static int ALT_LEVEL_AFTER_GET_SUBCLASS;
  public static int ALT_MAX_LEVEL;
  public static int ALT_MAX_SUB_LEVEL;
  public static boolean ALT_ALLOW_HERO_SKILLS_ON_SUB_CLASS;
  public static int ALT_GAME_BASE_SUB;
  public static boolean ALT_NO_LASTHIT;
  public static boolean ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY;
  public static boolean ALT_KAMALOKA_NIGHTMARE_REENTER;
  public static boolean ALT_KAMALOKA_ABYSS_REENTER;
  public static boolean ALT_KAMALOKA_LAB_REENTER;
  public static boolean ALT_PET_HEAL_BATTLE_ONLY;
  public static boolean ALT_SIMPLE_SIGNS;
  public static boolean ALT_TELE_TO_CATACOMBS;
  public static boolean ALT_BS_CRYSTALLIZE;
  public static boolean ALT_ALLOW_TATTOO;
  public static int ALT_BUFF_LIMIT;
  public static int ALT_DEBUFF_LIMIT;
  public static int ALT_TRIGGER_SLOT_ADDER;
  public static List<Pair<Integer, Integer>> OTHER_MAGE_BUFF_ON_CHAR_CREATE;
  public static List<Pair<Integer, Integer>> OTHER_WARRIOR_BUFF_ON_CHAR_CREATE;
  public static long ALT_NPC_BUFFER_EFFECT_TIME;
  public static long ALT_NPC_BUFFER_REUSE_DELAY;
  public static boolean ALT_ALLOW_DELAY_NPC_TALK;
  public static int ALT_MAX_PARTY_SIZE;
  public static boolean PRIVATE_BUY_MATCH_ENCHANT;
  public static int MULTISELL_SIZE;
  public static boolean SERVICES_CHANGE_NICK_ENABLED;
  public static int SERVICES_CHANGE_NICK_PRICE;
  public static int SERVICES_CHANGE_NICK_ITEM;
  public static boolean QUEST_SELL_ENABLE;
  public static String QUEST_SELL_QUEST_PRICES;
  public static int APPEARANCE_APPLY_ITEM_ID;
  public static int APPEARANCE_SUPPORT_ITEM_ID;
  public static long APPEARANCE_SUPPORT_ITEM_CNT;
  public static int APPEARANCE_CANCEL_ITEM_ID;
  public static long APPEARANCE_CANCEL_PRICE;
  public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED;
  public static int SERVICES_CHANGE_CLAN_NAME_PRICE;
  public static int SERVICES_CHANGE_CLAN_NAME_ITEM;
  public static boolean SERVICES_CHANGE_PET_NAME_ENABLED;
  public static int SERVICES_CHANGE_PET_NAME_PRICE;
  public static int SERVICES_CHANGE_PET_NAME_ITEM;
  public static boolean SERVICES_EXCHANGE_BABY_PET_ENABLED;
  public static int SERVICES_EXCHANGE_BABY_PET_PRICE;
  public static int SERVICES_EXCHANGE_BABY_PET_ITEM;
  public static boolean SERVICES_CHANGE_SEX_ENABLED;
  public static int SERVICES_CHANGE_SEX_PRICE;
  public static int SERVICES_CHANGE_SEX_ITEM;
  public static boolean SERVICES_CHANGE_BASE_ENABLED;
  public static boolean SERVICES_CHANGE_BASE_LIST_UNCOMPATABLE;
  public static int SERVICES_CHANGE_BASE_PRICE;
  public static int SERVICES_CHANGE_BASE_ITEM;
  public static boolean SERVICES_SEPARATE_SUB_ENABLED;
  public static int SERVICES_SEPARATE_SUB_PRICE;
  public static int SERVICES_SEPARATE_SUB_ITEM;
  public static int SERVICES_SEPARATE_SUB_MIN_LEVEL;
  public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED;
  public static int SERVICES_CHANGE_NICK_COLOR_PRICE;
  public static int SERVICES_CHANGE_NICK_COLOR_ITEM;
  public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;
  public static boolean SERVICES_CHANGE_TITLE_COLOR_ENABLED;
  public static int SERVICES_CHANGE_TITLE_COLOR_PRICE;
  public static int SERVICES_CHANGE_TITLE_COLOR_ITEM;
  public static String[] SERVICES_CHANGE_TITLE_COLOR_LIST;
  public static boolean SERVICE_CAPTCHA_BOT_CHECK;
  public static int CAPTCHA_MONSTER_KILLS_THRESHOLD;
  public static int CAPTCHA_PENALTY_SKILL_ID;
  public static int CAPTCHA_PENALTY_SKILL_LEVEL;
  public static boolean SERVICES_RATE_ENABLED;
  public static boolean SERVICES_RATE_COMMAND_ENABLED;
  public static Config.RateBonusInfo[] SERVICES_RATE_BONUS_INFO = new Config.RateBonusInfo[0];
  public static boolean SERVICES_NOBLESS_SELL_ENABLED;
  public static boolean SERVICES_NOBLESS_SELL_WITHOUT_SUBCLASS;
  public static int SERVICES_NOBLESS_SELL_PRICE;
  public static int SERVICES_NOBLESS_SELL_ITEM;
  public static int NOBLESS_LEVEL_FOR_SELL;
  public static boolean SERVICES_DELEVEL_SELL_ENABLED;
  public static int SERVICES_DELEVEL_SELL_PRICE;
  public static int SERVICES_DELEVEL_SELL_ITEM;
  public static int SERVICES_DELEVEL_REDUCTION;
  public static boolean SERVICE_RESET_LEVEL_ENABLED;
  public static int SERVICE_RESET_LEVEL_ITEM_ID;
  public static int SERVICE_RESET_LEVEL_ITEM_COUNT;
  public static int SERVICE_RESET_LEVEL_NEED;
  public static int SERVICE_RESET_LEVEL_REMOVE;
  public static int SERVICE_RESET_LEVEL_LIMIT;
  public static boolean SERVICES_LEVEL_UP_SELL_ENABLED;
  public static int SERVICES_LEVEL_UP_SELL_PRICE;
  public static int SERVICES_LEVEL_UP_SELL_ITEM;
  public static int SERVICES_LEVEL_UP_COUNT;
  public static boolean SERVICES_HERO_SELL_ENABLED;
  public static int SERVICES_HERO_SELLER_ITEM_ID;
  public static long SERVICES_HERO_SELLER_ITEM_COUNT;
  public static long SERVICE_HERO_STATUS_DURATION;
  public static boolean SERVICES_OLY_POINTS_RESET;
  public static int SERVICES_OLY_POINTS_RESET_ITEM_ID;
  public static int SERVICES_OLY_POINTS_RESET_ITEM_AMOUNT;
  public static int SERVICES_OLY_POINTS_THRESHOLD;
  public static int SERVICES_OLY_POINTS_REWARD;
  public static boolean SERVICES_PK_CLEAN_ENABLED;
  public static int SERVICES_PK_CLEAN_SELL_ITEM;
  public static long SERVICES_PK_CLEAN_SELL_PRICE;
  public static boolean SERVICES_PK_ANNOUNCE;
  public static boolean SERVICES_ALLOW_WYVERN_RIDE;
  public static int SERVICES_WYVERN_ITEM_ID;
  public static boolean SERVICES_KARMA_CLEAN_ENABLED;
  public static int SERVICES_KARMA_CLEAN_SELL_ITEM;
  public static long SERVICES_KARMA_CLEAN_SELL_PRICE;
  public static boolean SERVICES_CLANLEVEL_SELL_ENABLED;
  public static long[] SERVICES_CLANLEVEL_SELL_PRICE;
  public static int[] SERVICES_CLANLEVEL_SELL_ITEM;
  public static boolean SERVICES_CLAN_REPUTATION_ENABLE;
  public static int SERVICES_CLAN_REPUTATION_ITEM_ID;
  public static int SERVICES_CLAN_REPUTATION_ITEM_COUNT;
  public static int SERVICES_CLAN_REPUTATION_AMOUNT;
  public static boolean SERVICES_CLANHELPER_ENABLED;
  public static String SERVICES_CLANHELPER_CONFIG;
  public static boolean SERVICES_CLANHELPER_ADD_FULL_CLAN_SKILLS;
  public static boolean SERVICES_CLAN_BUFF_ENABLED;
  public static int SERVICES_CLAN_BUFF_SKILL_ID;
  public static String SERVICES_CLAN_BUFF_LEVELS_MIN_ONLINE;
  public static long SERVICES_CLAN_BUFF_TASK_DELAY;
  public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;
  public static int SERVICES_EXPAND_INVENTORY_PRICE;
  public static int SERVICES_EXPAND_INVENTORY_ITEM;
  public static int SERVICES_EXPAND_INVENTORY_MAX;
  public static int SERVICES_EXPAND_INVENTORY_SLOT_AMOUNT;
  public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;
  public static int SERVICES_EXPAND_WAREHOUSE_PRICE;
  public static int SERVICES_EXPAND_WAREHOUSE_ITEM;
  public static int SERVICES_EXPAND_WAREHOUSE_MAX;
  public static int SERVICES_EXPAND_WAREHOUSE_SLOT_AMOUNT;
  public static boolean SERVICES_EXPAND_CWH_ENABLED;
  public static int SERVICES_EXPAND_CWH_PRICE;
  public static int SERVICES_EXPAND_CWH_SLOT_AMOUNT;
  public static int SERVICES_EXPAND_CWH_ITEM;
  public static boolean SERVICES_AUTO_HEAL_ACTIVE;
  public static String SERVICES_SELLPETS;
  public static boolean SERVICES_OFFLINE_TRADE_ALLOW;
  public static boolean ALLOW_TRADE_ON_THE_MOVE;
  public static boolean SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE;
  public static int SERVICES_OFFLINE_TRADE_MIN_LEVEL;
  public static int SERVICES_OFFLINE_TRADE_NAME_COLOR;
  public static boolean SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE;
  public static AbnormalEffect SERVICES_OFFLINE_TRADE_ABNORMAL;
  public static int SERVICES_OFFLINE_TRADE_PRICE;
  public static int SERVICES_OFFLINE_TRADE_PRICE_ITEM;
  public static long SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK;
  public static boolean SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART;
  public static boolean SERVICES_GIRAN_HARBOR_ENABLED;
  public static boolean SERVICES_GIRAN_HARBOR_NOTAX;
  public static int SERVICES_GIRAN_HARBOR_PRICE;
  public static boolean SERVICES_ALLOW_LOTTERY;
  public static int SERVICES_LOTTERY_PRIZE;
  public static int SERVICES_ALT_LOTTERY_PRICE;
  public static int SERVICES_LOTTERY_TICKET_PRICE;
  public static double SERVICES_LOTTERY_5_NUMBER_RATE;
  public static double SERVICES_LOTTERY_4_NUMBER_RATE;
  public static double SERVICES_LOTTERY_3_NUMBER_RATE;
  public static int SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
  public static boolean SERVICES_ALLOW_ROULETTE;
  public static int SERVICES_ROULETTE_MIN_BET;
  public static int SERVICES_ROULETTE_MAX_BET;
  public static boolean ITEM_CLAN_REPUTATION_ENABLE;
  public static int[] ITEM_CLAN_REPUTATION_ID;
  public static int[] ITEM_CLAN_REPUTATION_AMOUNT;
  public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
  public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;
  public static boolean ALT_ALLOW_CLAN_COMMAND_ALLOW_WH;
  public static boolean ALT_ALLOW_MENU_COMMAND;
  public static boolean ALT_ALLOW_RELOG_COMMAND;
  public static boolean ALT_ALLOW_LANG_COMMAND;
  public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
  public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
  public static boolean ALT_GAME_ALLOW_ADENA_DAWN;
  public static long NONOWNER_ITEM_PICKUP_DELAY;
  public static long NONOWNER_ITEM_PICKUP_DELAY_RAID;
  public static boolean LOG_CHAT;
  public static Map<Integer, PlayerAccess> gmlist = new HashMap();
  public static double RATE_XP;
  public static double RATE_SP;
  public static double RATE_OVERHIT;
  public static double RATE_QUESTS_REWARD;
  public static double RATE_QUESTS_REWARD_EXP_SP;
  public static double RATE_QUESTS_DROP;
  public static double RATE_CLAN_REP_SCORE;
  public static int RATE_CLAN_REP_SCORE_MAX_AFFECTED;
  public static boolean ALT_MULTI_DROP;
  public static double RATE_DROP_ADENA;
  public static double RATE_DROP_ITEMS;
  public static double RATE_DROP_SEAL_STONES;
  public static double RATE_DROP_HERBS;
  public static double RATE_DROP_RAIDBOSS;
  public static double RATE_RAIDBOSS_XP;
  public static double RATE_RAIDBOSS_SP;
  public static double RATE_DROP_SPOIL;
  public static int[] NO_RATE_ITEMS;
  public static int[] NO_DROP_ITEMS;
  public static int[] NO_DROP_ITEMS_FOR_SWEEP;
  public static boolean NO_RATE_EQUIPMENT;
  public static boolean NO_RATE_KEY_MATERIAL;
  public static boolean NO_RATE_RECIPES;
  public static double RATE_DROP_SIEGE_GUARD;
  public static double RATE_MANOR;
  public static double RATE_FISH_DROP_COUNT;
  public static boolean RATE_PARTY_MIN;
  public static int SKILL_COST_RATE;
  public static int RATE_MOB_SPAWN;
  public static int RATE_MOB_SPAWN_MIN_LEVEL;
  public static int RATE_MOB_SPAWN_MAX_LEVEL;
  public static boolean KARMA_DROP_GM;
  public static boolean KARMA_NEEDED_TO_DROP;
  public static int ITEM_ANTIDROP_FROM_PK;
  public static long SERVICES_PK_KILL_BONUS_INTERVAL;
  public static boolean SERVICES_PK_KILL_BONUS_ENABLE;
  public static int SERVICES_PVP_KILL_BONUS_REWARD_COUNT;
  public static int SERVICES_PVP_KILL_BONUS_REWARD_ITEM;
  public static boolean SERVICES_PVP_KILL_BONUS_ENABLE;
  public static int SERVICES_PK_KILL_BONUS_REWARD_COUNT;
  public static int SERVICES_PK_KILL_BONUS_REWARD_ITEM;
  public static boolean SERVICES_PK_PVP_BONUS_TIE_IF_SAME_IP;
  public static boolean SERVICES_PK_PVP_BONUS_TIE_IF_SAME_HWID;
  public static int PVP_COUNT_TO_CHAT;
  public static boolean PVP_HERO_CHAT_SYSTEM;
  public static int KARMA_DROP_ITEM_LIMIT;
  public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;
  public static double KARMA_DROPCHANCE_BASE;
  public static double KARMA_DROPCHANCE_MOD;
  public static double NORMAL_DROPCHANCE_BASE;
  public static int DROPCHANCE_EQUIPMENT;
  public static int DROPCHANCE_EQUIPPED_WEAPON;
  public static int DROPCHANCE_ITEM;
  public static boolean FUN_ZONE_PVP_COUNT;
  public static boolean FUN_ZONE_FLAG_ON_ENTER;
  public static int AUTODESTROY_ITEM_AFTER;
  public static int AUTODESTROY_PLAYER_ITEM_AFTER;
  public static int DELETE_DAYS;
  public static int PURGE_BYPASS_TASK_FREQUENCY;
  public static File DATAPACK_ROOT;
  public static double CLANHALL_BUFFTIME_MODIFIER;
  public static double SONGDANCETIME_MODIFIER;
  public static int CLNHALL_REWARD_CYCLE;
  public static int CLAN_MIN_LEVEL_FOR_BID;
  public static Map<Integer, Integer> SKILL_DURATION_MOD = new ConcurrentHashMap();
  public static double MAXLOAD_MODIFIER;
  public static double GATEKEEPER_MODIFIER;
  public static boolean ALT_IMPROVED_PETS_LIMITED_USE;
  public static int GATEKEEPER_FREE;
  public static int CRUMA_GATEKEEPER_LVL;
  public static double ALT_CHAMPION_CHANCE1;
  public static double ALT_CHAMPION_CHANCE2;
  public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
  public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
  public static boolean ALT_CHAMPION_CAN_BE_SPECIAL_MONSTERS;
  public static int ALT_CHAMPION_TOP_LEVEL;
  public static int ALT_CHAMPION_MIN_LEVEL;
  public static int[] ALT_CHAMPION_DROP_ITEM_IDS;
  public static float[] ALT_CHAMPION_DROP_CHANCES;
  public static long[] ALT_CHAMPION_DROP_COUNTS;
  public static boolean ALT_PVP_ITEMS_TREDABLE;
  public static boolean ALT_PVP_ITEMS_ATTRIBUTABLE;
  public static boolean ALT_PVP_ITEMS_AUGMENTABLE;
  public static int[] ALT_INITIAL_QUESTS;
  public static boolean ALLOW_DISCARDITEM;
  public static boolean ALLOW_MAIL;
  public static boolean ALLOW_WAREHOUSE;
  public static boolean ALLOW_WATER;
  public static boolean ALLOW_CURSED_WEAPONS;
  public static boolean DROP_CURSED_WEAPONS_ON_KICK;
  public static boolean ALLOW_NOBLE_TP_TO_ALL;
  public static int SWIMING_SPEED;
  public static int MIN_PROTOCOL_REVISION;
  public static int MAX_PROTOCOL_REVISION;
  public static String ALT_CG_MODULE;
  public static int MIN_NPC_ANIMATION;
  public static int MAX_NPC_ANIMATION;
  public static String DEFAULT_LANG;
  public static String RESTART_AT_TIME;
  public static int GAME_SERVER_LOGIN_PORT;
  public static boolean GAME_SERVER_LOGIN_CRYPT;
  public static String GAME_SERVER_LOGIN_HOST;
  public static String INTERNAL_HOSTNAME;
  public static String EXTERNAL_HOSTNAME;
  public static boolean SERVER_SIDE_NPC_NAME;
  public static boolean SERVER_SIDE_NPC_TITLE;
  public static boolean TEST_SERVER_HELPER_ENABLED;
  public static String CLASS_MASTERS_CLASSES;
  public static boolean ALLOW_EVENT_GATEKEEPER;
  public static boolean ALLOW_GLOBAL_GK;
  public static boolean ALLOW_BUFFER;
  public static boolean ALLOW_GMSHOP;
  public static boolean ALLOW_AUCTIONER;
  public static boolean ALLOW_GLOBAL_SERVICES;
  public static boolean ALLOW_PVP_EVENT_MANAGER;
  public static boolean ALLOW_TREASURE_BOX;
  public static boolean COMMAND_CLASS_MASTER_ENABLED;
  public static String COMMAND_CLASS_MASTER_CLASSES;
  public static int COMMAND_CLASS_POPUP_LIMIT;
  public static String[] COMMAND_CLASS_MASTER_VOICE_COMMANDS;
  public static boolean PAWNSHOP_ENABLED;
  public static ItemClass[] PAWNSHOP_ITEMS_CLASSES;
  public static int PAWNSHOP_MIN_ENCHANT_LEVEL;
  public static boolean PAWNSHOP_ALLOW_SELL_AUGMENTED_ITEMS;
  public static Grade PAWNSHOP_MIN_GRADE;
  public static int PAWNSHOP_ITEMS_PER_PAGE;
  public static int[] PAWNSHOP_CURRENCY_ITEM_IDS;
  public static int[] PAWNSHOP_PROHIBITED_ITEM_IDS;
  public static int PAWNSHOP_MIN_QUERY_LENGTH;
  public static int PAWNSHOP_TAX_ITEM_ID;
  public static long PAWNSHOP_TAX_ITEM_COUNT;
  public static int PAWNSHOP_REFUND_ITEM_ID;
  public static long PAWNSHOP_REFUND_ITEM_COUNT;
  public static boolean PAWNSHOP_PRICE_SORT;
  public static boolean ITEM_BROKER_ITEM_SEARCH;
  public static long ITEM_BROKER_UPDATE_TIME;
  public static int INVENTORY_MAXIMUM_NO_DWARF;
  public static int INVENTORY_MAXIMUM_DWARF;
  public static boolean DWARF_AUTOMATICALLY_CRYSTALLIZE_ON_ITEM_DELETE;
  public static int INVENTORY_MAXIMUM_GM;
  public static int WAREHOUSE_SLOTS_NO_DWARF;
  public static int WAREHOUSE_SLOTS_DWARF;
  public static int WAREHOUSE_SLOTS_CLAN;
  public static int FREIGHT_SLOTS;
  public static int FOLLOW_ARRIVE_DISTANCE;
  public static boolean SEND_LINEAGE2_WELCOME_MESSAGE;
  public static boolean SEND_SSQ_WELCOME_MESSAGE;
  public static double MINIMUM_SPOIL_RATE;
  public static double MANOR_SOWING_BASIC_SUCCESS;
  public static double MANOR_SOWING_ALT_BASIC_SUCCESS;
  public static double MANOR_HARVESTING_BASIC_SUCCESS;
  public static double MANOR_HARVESTING_REWARD_RATE;
  public static int MANOR_DIFF_PLAYER_TARGET;
  public static double MANOR_DIFF_PLAYER_TARGET_PENALTY;
  public static int MANOR_DIFF_SEED_TARGET;
  public static double MANOR_DIFF_SEED_TARGET_PENALTY;
  public static int KARMA_MIN_KARMA;
  public static int KARMA_SP_DIVIDER;
  public static int KARMA_LOST_BASE;
  public static int MIN_PK_TO_ITEMS_DROP;
  public static boolean DROP_ITEMS_ON_DIE;
  public static boolean DROP_ITEMS_AUGMENTED;
  public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList();
  public static int PVP_TIME;
  public static int PVP_BLINKING_UNFLAG_TIME;
  public static int PVP_FLAG_ON_UN_FLAG_TIME;
  public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
  public static double[] ENCHANT_CHANCES_ARMOR;
  public static double[] ENCHANT_CHANCES_FULL_ARMOR;
  public static double[] ENCHANT_CHANCES_JEWELRY;
  public static double[] ENCHANT_CHANCES_WEAPON;
  public static int ENCHANT_MAX;
  public static int ARMOR_ENCHANT_6_SKILL;
  public static int ENCHANT_ATTRIBUTE_STONE_CHANCE;
  public static int ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE;
  public static int ENCHANT_ATTRIBUTE_ENERGY_CHANCE;
  public static int ARMOR_OVERENCHANT_HPBONUS_LIMIT;
  public static boolean SHOW_ENCHANT_EFFECT_RESULT;
  public static boolean REGEN_SIT_WAIT;
  public static double RATE_RAID_REGEN;
  public static double RATE_RAID_DEFENSE;
  public static double RATE_RAID_ATTACK;
  public static int RATE_MOD_MIN_LEVEL_LIMIT;
  public static int RATE_MOD_MAX_LEVEL_LIMIT;
  public static double RATE_EPIC_DEFENSE;
  public static double RATE_EPIC_ATTACK;
  public static int RAID_MAX_LEVEL_DIFF;
  public static boolean PARALIZE_ON_RAID_DIFF;
  public static double ALT_PK_DEATH_RATE;
  public static int STARTING_ADENA;
  public static int[] STARTING_ITEMS_MAGE;
  public static int[] STARTING_ITEMS_FIGHTER;
  public static boolean ALT_CONSUME_ARROWS;
  public static boolean ALT_CONSUME_SOULSHOTS;
  public static boolean ALT_CONSUME_SPIRITSHOTS;
  public static boolean ALT_CONSUME_BEAST_SHOTS;
  public static boolean DEEPBLUE_DROP_RULES;
  public static int DEEPBLUE_DROP_MAXDIFF;
  public static int DEEPBLUE_DROP_RAID_MAXDIFF;
  public static int EXP_SP_DIFF_LIMIT;
  public static int THRESHOLD_LEVEL_DIFF;
  public static int PARTY_PENALTY_EXP_SP_MAX_LEVEL;
  public static int PARTY_PENALTY_EXP_SP_MIN_LEVEL;
  public static boolean UNSTUCK_SKILL;
  public static boolean BLOCK_BUFF_SKILL;
  public static boolean NOBLES_BUFF_SKILL;
  public static IntSet BLOCK_BUFF_EXCLUDE;
  public static boolean IS_TELNET_ENABLED;
  public static String TELNET_DEFAULT_ENCODING;
  public static String TELNET_PASSWORD;
  public static String TELNET_HOSTNAME;
  public static int TELNET_PORT;
  public static double RESPAWN_RESTORE_CP;
  public static double RESPAWN_RESTORE_HP;
  public static double RESPAWN_RESTORE_MP;
  public static int MAX_PVTSTORE_SLOTS_DWARF;
  public static int MAX_PVTSTORE_SLOTS_DWARF_FIRST_JOB;
  public static int MAX_PVTSTORE_SLOTS_OTHER;
  public static int MAX_PVTSTORE_SLOTS_OTHER_FIRST_JOB;
  public static int MAX_PVTCRAFT_SLOTS;
  public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;
  public static double SENDSTATUS_TRADE_MOD;
  public static double MUL_PLAYERS_ONLINE;
  public static boolean ALLOW_CH_DOOR_OPEN_ON_CLICK;
  public static boolean ALT_CH_ALL_BUFFS;
  public static boolean ALT_CH_ALLOW_1H_BUFFS;
  public static boolean ALT_CH_SIMPLE_DIALOG;
  public static int CH_BID_CURRENCY_ITEM_ID;
  public static double RESIDENCE_LEASE_FUNC_MULTIPLIER;
  public static double RESIDENCE_LEASE_MULTIPLIER;
  public static TIntHashSet CH_DISPLAY_IDS;
  public static boolean ACCEPT_ALTERNATE_ID;
  public static int REQUEST_ID;
  public static boolean ANNOUNCE_MAMMON_SPAWN;
  public static int GM_NAME_COLOUR;
  public static boolean GM_HERO_AURA;
  public static int NORMAL_NAME_COLOUR;
  public static int CLANLEADER_NAME_COLOUR;
  public static boolean SHOW_HTML_WELCOME;
  public static int AI_TASK_MANAGER_COUNT;
  public static long AI_TASK_ATTACK_DELAY;
  public static long AI_TASK_ACTIVE_DELAY;
  public static boolean BLOCK_ACTIVE_TASKS;
  public static boolean ALWAYS_TELEPORT_HOME;
  public static boolean RND_WALK;
  public static int RND_WALK_RATE;
  public static int RND_ANIMATION_RATE;
  public static int AGGRO_CHECK_INTERVAL;
  public static long NONAGGRO_TIME_ONTELEPORT;
  public static long NONAGGRO_TIME_ONLOGIN;
  public static boolean ALT_TELEPORT_PROTECTION;
  public static long ALT_TELEPORT_PROTECTION_TIME;
  public static boolean ALT_SPREADING_AFTER_TELEPORT;
  public static int MAX_DRIFT_RANGE;
  public static int MAX_PURSUE_RANGE;
  public static int MAX_PURSUE_UNDERGROUND_RANGE;
  public static int MAX_PURSUE_RANGE_RAID;
  public static boolean RESTORE_HP_MP_ON_TELEPORT_HOME;
  public static boolean ALT_DEATH_PENALTY;
  public static boolean ALLOW_DEATH_PENALTY_C5;
  public static int ALT_DEATH_PENALTY_C5_CHANCE;
  public static boolean ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY;
  public static int ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
  public static int ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
  public static int ALT_MUSIC_COST_GUARD_INTERVAL;
  public static boolean ALT_ADDITIONAL_DANCE_SONG_MANA_CONSUME;
  public static boolean HIDE_GM_STATUS;
  public static boolean SHOW_GM_LOGIN;
  public static boolean SAVE_GM_EFFECTS;
  public static boolean AUTO_LEARN_SKILLS;
  public static boolean AUTO_LEARN_FORGOTTEN_SKILLS;
  public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
  public static int MOVE_TASK_QUANTUM_PC;
  public static int MOVE_TASK_QUANTUM_NPC;
  public static boolean MOVE_OFFLOAD_MTL_PC;
  public static int ATTACK_PACKET_DELAY;
  public static int ATTACK_END_DELAY;
  public static int PICKUP_PACKET_DELAY;
  public static int ENCHANT_PACKET_DELAY;
  public static int RELOAD_PACKET_DELAY;
  public static boolean DAMAGE_FROM_FALLING;
  public static boolean COMMUNITYBOARD_ENABLED;
  public static String BBS_DEFAULT;
  public static boolean ALLOW_WEDDING;
  public static int WEDDING_PRICE;
  public static int WEDDING_ITEM_ID_PRICE;
  public static boolean WEDDING_PUNISH_INFIDELITY;
  public static boolean WEDDING_TELEPORT;
  public static int WEDDING_TELEPORT_PRICE;
  public static int WEDDING_TELEPORT_INTERVAL;
  public static boolean WEDDING_SAMESEX;
  public static boolean WEDDING_FORMALWEAR;
  public static int WEDDING_DIVORCE_COSTS;
  public static boolean WEDDING_GIVE_SALVATION_BOW;
  public static boolean WEDDING_USE_COLOR;
  public static int WEDDING_NORMAL_COLOR;
  public static int WEDDING_GAY_COLOR;
  public static int WEDDING_LESBIAN_COLOR;
  public static boolean WEDDING_ANNOUNCE;
  public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
  public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
  public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
  public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
  public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
  public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
  public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
  public static boolean ALT_HBCE_FAIR_PLAY;
  public static int ALT_PET_INVENTORY_LIMIT;
  public static int LIM_PATK;
  public static int LIM_MATK;
  public static int LIM_PDEF;
  public static int LIM_MDEF;
  public static int LIM_MATK_SPD;
  public static int LIM_PATK_SPD;
  public static int LIM_CRIT_DAM;
  public static int LIM_CRIT;
  public static int LIM_MCRIT;
  public static double MCRITICAL_CRIT_POWER;
  public static int BASE_MAGE_CAST_SPEED;
  public static int BASE_WARRIOR_CAST_SPEED;
  public static int LIM_ACCURACY;
  public static int LIM_EVASION;
  public static int LIM_MOVE;
  public static int LIM_MAX_CP;
  public static int LIM_MAX_HP;
  public static int LIM_MAX_MP;
  public static int LIM_FAME;
  public static double MCRITICAL_BASE_STAT;
  public static boolean MDAM_CRIT_POSSIBLE;
  public static boolean HEAL_CRIT_POSSIBLE;
  public static boolean CALC_EFFECT_TIME_YIELD_AND_RESIST;
  public static int MIN_ATK_DELAY;
  public static double ALT_NPC_PATK_MODIFIER;
  public static double ALT_NPC_MATK_MODIFIER;
  public static double ALT_NPC_MAXHP_MODIFIER;
  public static double ALT_NPC_MAXMP_MODIFIER;
  public static int ALT_NPC_LIM_MCRIT;
  public static int FESTIVAL_MIN_PARTY_SIZE;
  public static double FESTIVAL_RATE_PRICE;
  public static int RIFT_MIN_PARTY_SIZE;
  public static int RIFT_SPAWN_DELAY;
  public static int RIFT_MAX_JUMPS;
  public static int RIFT_AUTO_JUMPS_TIME;
  public static int RIFT_AUTO_JUMPS_TIME_RAND;
  public static int RIFT_ENTER_COST_RECRUIT;
  public static int RIFT_ENTER_COST_SOLDIER;
  public static int RIFT_ENTER_COST_OFFICER;
  public static int RIFT_ENTER_COST_CAPTAIN;
  public static int RIFT_ENTER_COST_COMMANDER;
  public static int RIFT_ENTER_COST_HERO;
  public static boolean FOUR_SEPULCHER_ENABLE;
  public static int FOUR_SEPULCHER_MIN_PARTY_MEMBERS;
  public static boolean ALLOW_TALK_WHILE_SITTING;
  public static boolean PARTY_LEADER_ONLY_CAN_INVITE;
  public static long EXPELLED_MEMBER_PENALTY;
  public static long NEW_CLAN_CREATE_PENALTY;
  public static long CLAN_LEAVE_TIME_PERNALTY;
  public static long CLAN_DISBAND_TIME;
  public static long CLAN_DISBAND_PENALTY;
  public static boolean ALLY_ALLOW_BUFF_DEBUFFS;
  public static int ALT_NPC_CLAN;
  public static boolean ALLOW_TEMPORARILY_ALLY_ON_FIRST_SIEGE;
  public static long LEAVED_ALLY_PENALTY;
  public static long DISSOLVED_ALLY_PENALTY;
  public static boolean ALLOW_CLANSKILLS;
  public static int CLAN_INIT_LEVEL;
  public static int CLAN_REPUTATION_BONUS_ON_CREATE;
  public static boolean FULL_CLAN_SKILLS_ON_CREATE;
  public static int CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE;
  public static boolean CLAN_LEADER_CHANGE_METHOD;
  public static int LIMIT_CLAN_LEVEL0;
  public static int LIMIT_CLAN_LEVEL1;
  public static int LIMIT_CLAN_LEVEL2;
  public static int LIMIT_CLAN_LEVEL3;
  public static int LIMIT_CLAN_LEVEL_4_AND_HIGH;
  public static int MIN_CLAN_LEVEL_FOR_DECLARED_WAR;
  public static int MIN_CLAN_MEMBER_FOR_DECLARED_WAR;
  public static int CRP_REWARD_ON_WAR_KILL_OVER_LEVEL;
  public static int CRP_REWARD_ON_WAR_KILL;
  public static int LIMIT_CLAN_HIGH_UNITS;
  public static int LIMIT_CLAN_LOW_UNITS;
  public static int LIMIT_CLAN_ACADEMY;
  public static int CLAN_FIRST_LEVEL_SP;
  public static int CLAN_FIRST_LEVEL_ADENA;
  public static int CLAN_SECOND_LEVEL_SP;
  public static int CLAN_SECOND_LEVEL_ADENA;
  public static int CLAN_THIRD_LEVEL_SP;
  public static int CLAN_FOUR_LEVEL_SP;
  public static int CLAN_FIVE_LEVEL_SP;
  public static int CLAN_SIX_LEVEL_CLAN_REPUTATION;
  public static int CLAN_SIX_LEVEL_CLAN_MEMBER_COUNT;
  public static int CLAN_SEVEN_LEVEL_CLAN_REPUTATION;
  public static int CLAN_SEVEN_LEVEL_CLAN_MEMBER_COUNT;
  public static int CLAN_EIGHT_LEVEL_CLAN_REPUTATION;
  public static int CLAN_EIGHT_LEVEL_CLAN_MEMBER_COUNT;
  public static boolean CHECK_CLAN_RANK_ON_COMMAND_CHANNEL_CREATE;
  public static int MIN_CLAN_LEVEL_FOR_REPUTATION;
  public static boolean ALLOW_MANOR;
  public static int MANOR_REFRESH_TIME;
  public static int MANOR_REFRESH_MIN;
  public static int MANOR_APPROVE_TIME;
  public static int MANOR_APPROVE_MIN;
  public static int MANOR_MAINTENANCE_PERIOD;
  public static double EVENT_CofferOfShadowsPriceRate;
  public static double EVENT_CofferOfShadowsRewardRate;
  public static double EVENT_APIL_FOOLS_DROP_CHANCE;
  public static int EVENT_LastHeroItemID;
  public static double EVENT_LastHeroItemCOUNT;
  public static int EVENT_LastHeroTime;
  public static boolean EVENT_LastHeroRate;
  public static double EVENT_LastHeroItemCOUNTFinal;
  public static boolean EVENT_LastHeroRateFinal;
  public static int EVENT_LastHeroChanceToStart;
  public static int EVENT_TvTItemID;
  public static double EVENT_TvTItemCOUNT;
  public static int EVENT_TvTTime;
  public static boolean EVENT_TvT_rate;
  public static int EVENT_TvTChanceToStart;
  public static int EVENT_CtFItemID;
  public static double EVENT_CtFItemCOUNT;
  public static int EVENT_CtFTime;
  public static boolean EVENT_CtF_rate;
  public static int EVENT_CtFChanceToStart;
  public static String EVENT_DropEvent_Items;
  public static boolean EVENT_DropEvent_Rate;
  public static double EVENT_TFH_POLLEN_CHANCE;
  public static double EVENT_GLITTMEDAL_NORMAL_CHANCE;
  public static double EVENT_GLITTMEDAL_GLIT_CHANCE;
  public static double EVENT_L2DAY_LETTER_CHANCE;
  public static double EVENT_CHANGE_OF_HEART_CHANCE;
  public static double EVENT_CHRISTMAS_CHANCE;
  public static double EVENT_TRICK_OF_TRANS_CHANCE;
  public static double EVENT_MARCH8_DROP_CHANCE;
  public static double EVENT_MARCH8_PRICE_RATE;
  public static int GVG_REWARD_ID;
  public static long GVG_REWARD_AMOUNT;
  public static long EVENT_SAVING_SNOWMAN_LOTERY_PRICE;
  public static int EVENT_SAVING_SNOWMAN_LOTERY_CURENCY;
  public static int EVENT_SAVING_SNOWMAN_REWARDER_CHANCE;
  public static int[] EVENT_StraightHands_Items;
  public static String[] EVENT_FinderHostageStartTime;
  public static int EVENT_FINDER_REWARD_ID;
  public static int EVENT_FINDER_ITEM_COUNT;
  public static String EVENT_GVG_START_TIME;
  public static int EVENT_GVG_MIN_LEVEL;
  public static int EVENT_GVG_MAX_LEVEL;
  public static int EVENT_GVG_GROUPS_LIMIT;
  public static int EVENT_GVG_MIN_PARTY_SIZE;
  public static long EVENT_GVG_REG_TIME;
  public static int EVENT_PUMPKIN_GHOST_ID;
  public static int EVENT_SKOOLDIE_REWARDER;
  public static int EVENT_PUMPKIN_DELAY;
  public static int EVENT_PUMPKIN_GHOST_SHOW_TIME;
  public static int EVENT_SKOOLDIE_TIME;
  public static int EVENT_HALLOWEEN_CANDY;
  public static int EVENT_HALLOWEEN_CANDY_ITEM_COUNT_NEEDED;
  public static int EVENT_HALLOWEEN_TOY_CHEST_REWARD_AMOUNT;
  public static int EVENT_HALLOWEEN_TOY_CHEST;
  public static int EVENT_PUMPKIN_DROP_CHANCE;
  public static int[] EVENT_PUMPKIN_DROP_ITEMS;
  public static boolean SERVICES_NO_TRADE_ONLY_OFFLINE;
  public static double SERVICES_TRADE_TAX;
  public static double SERVICES_OFFSHORE_TRADE_TAX;
  public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;
  public static boolean SERVICES_TRADE_TAX_ONLY_OFFLINE;
  public static boolean SERVICES_TRADE_ONLY_FAR;
  public static int SERVICES_TRADE_RADIUS;
  public static int SERVICES_TRADE_MIN_LEVEL;
  public static boolean SERVICES_CLANSKILL_SELL_ENABLED;
  public static int SERVICES_CLAN_SKILL_SELL_ITEM;
  public static int SERVICES_CLAN_SKILL_SELL_PRICE;
  public static int SERVICES_CLANSKIL_SELL_MIN_LEVEL;
  public static boolean SERVICES_ENABLE_NO_CARRIER;
  public static int SERVICES_NO_CARRIER_DEFAULT_TIME;
  public static int SERVICES_NO_CARRIER_MAX_TIME;
  public static int SERVICES_NO_CARRIER_MIN_TIME;
  public static int SERVICES_CLAN_MAX_SELL_LEVEL;
  public static boolean SERVICES_HPACP_ENABLE;
  public static boolean SERVICES_HPACP_WORK_IN_PEACE_ZONE;
  public static int SERVICES_HPACP_MIN_PERCENT;
  public static int SERVICES_HPACP_MAX_PERCENT;
  public static int SERVICES_HPACP_DEF_PERCENT;
  public static int[] SERVICES_HPACP_POTIONS_ITEM_IDS;
  public static boolean SERVICES_ONLINE_COMMAND_ENABLE;
  public static double SERVICE_COMMAND_MULTIPLIER;
  public static boolean SERVICES_BANKING_ENABLED;
  public static int SERVICES_DEPOSIT_ITEM_ID_NEEDED;
  public static int SERVICES_DEPOSIT_ITEM_COUNT_NEEDED;
  public static int SERVICES_DEPOSIT_ITEM_ID_GIVED;
  public static int SERVICES_DEPOSIT_ITEM_COUNT_GIVED;
  public static int SERVICES_WITHDRAW_ITEM_ID_NEEDED;
  public static int SERVICES_WITHDRAW_ITEM_COUNT_NEEDED;
  public static int SERVICES_WITHDRAW_ITEM_ID_GIVED;
  public static int SERVICES_WITHDRAW_ITEM_COUNT_GIVED;
  public static boolean SERVICES_CLAN_SUMMON_COMMAND_ENABLE;
  public static int SERVICES_CLAN_SUMMON_COMMAND_SELL_ITEM;
  public static int SERVICES_CLAN_SUMMON_COMMAND_SELL_PRICE;
  public static int SERVICES_CLAN_SUMMON_COMMAND_SUMMON_CRYSTAL_COUNT;
  public static long REUSE_DELAY_FOR_CLAN_SUMMON;
  public static boolean SERVICES_WHOIAM_COMMAND_ENABLE;
  public static boolean SERVICES_CPACP_ENABLE;
  public static int SERVICES_CPACP_MIN_PERCENT;
  public static int SERVICES_CPACP_MAX_PERCENT;
  public static int SERVICES_CPACP_DEF_PERCENT;
  public static int[] SERVICES_CPACP_POTIONS_ITEM_IDS;
  public static boolean SERVICES_MPACP_ENABLE;
  public static int SERVICES_MPACP_MIN_PERCENT;
  public static int SERVICES_MPACP_MAX_PERCENT;
  public static int SERVICES_MPACP_DEF_PERCENT;
  public static int[] SERVICES_MPACP_POTIONS_ITEM_IDS;
  public static int[] SERVICES_BOSS_STATUS_ADDITIONAL_IDS;
  public static boolean SERVICES_BOSS_STATUS_ENABLE;
  public static String SERVICES_BOSS_STATUS_FORMAT;
  public static boolean SERVICES_PVP_PK_STATISTIC;
  public static long PVP_PK_STAT_CACHE_UPDATE_INTERVAL;
  public static int PVP_PK_STAT_RECORD_LIMIT;
  public static boolean SERVICE_AUTO_ANNOUNCE;
  public static boolean ALT_SHOW_SERVER_TIME;
  public static int GEO_X_FIRST;
  public static int GEO_Y_FIRST;
  public static int GEO_X_LAST;
  public static int GEO_Y_LAST;
  public static String GEOFILES_PATTERN;
  public static boolean ALLOW_GEODATA;
  public static boolean ALLOW_FALL_FROM_WALLS;
  public static boolean ALLOW_KEYBOARD_MOVE;
  public static boolean COMPACT_GEO;
  public static int CLIENT_Z_SHIFT;
  public static int MAX_Z_DIFF;
  public static int MIN_LAYER_HEIGHT;
  public static int PATHFIND_BOOST;
  public static boolean PATHFIND_DIAGONAL;
  public static boolean PATH_CLEAN;
  public static int PATHFIND_MAX_Z_DIFF;
  public static long PATHFIND_MAX_TIME;
  public static String PATHFIND_BUFFERS;
  public static boolean ALLOW_PAWN_PATHFIND;
  public static boolean DEBUG;
  public static int GAME_POINT_ITEM_ID;
  public static int WEAR_DELAY;
  public static boolean GOODS_INVENTORY_ENABLED = false;
  public static boolean AUTH_SERVER_GM_ONLY;
  public static boolean AUTH_SERVER_BRACKETS;
  public static boolean AUTH_SERVER_IS_PVP;
  public static int AUTH_SERVER_AGE_LIMIT;
  public static int AUTH_SERVER_SERVER_TYPE;
  public static boolean USE_SECOND_PASSWORD_AUTH;
  public static long SECOND_AUTH_BLOCK_TIME;
  public static int SECOND_AUTH_MAX_TRYS;
  public static int SECOND_AUTH_MIN_LENG;
  public static int SECOND_AUTH_MAX_LENG;
  public static int REC_FLUSH_HOUR;
  public static int REC_FLUSH_MINUTE;
  public static int HUNTING_POINTS_PER_TICK;
  public static int HUNTING_BONUS_TIME;
  public static int HUNTING_TICK_DELAY;
  public static boolean OLY_ENABLED;
  public static boolean OLY_SPECTATION_ALLOWED;
  public static boolean NPC_OLYMPIAD_GAME_ANNOUNCE;
  public static boolean ANNOUNCE_OLYMPIAD_GAME_END;
  public static boolean OLY_RECALC_NEW_SEASON;
  public static boolean OLY_RESTRICT_HWID;
  public static boolean OLY_RESTRICT_IP;
  public static long OLYMPIAD_COMPETITION_TIME;
  public static int OLYMPIAD_STADIUM_TELEPORT_DELAY;
  public static int OLY_MAX_SPECTATORS_PER_STADIUM;
  public static int[] OLY_RESTRICT_CLASS_IDS;
  public static Config.OlySeasonTimeCalcMode OLY_SEASON_TIME_CALC_MODE;
  public static String OLY_SEASON_START_TIME;
  public static String OLY_SEASON_END_TIME;
  public static String OLY_COMPETITION_START_TIME;
  public static String OLY_COMPETITION_END_TIME;
  public static String OLY_BONUS_TIME;
  public static String OLY_NOMINATE_TIME;
  public static int OLY_MIN_HERO_COMPS;
  public static int OLY_MIN_HERO_WIN;
  public static int OLY_MIN_NOBLE_COMPS;
  public static int OLY_SEASON_START_POINTS;
  public static int[] OLY_POINTS_SETTLEMENT;
  public static TIntObjectHashMap<TIntIntHashMap> OLY_BUFFS;
  public static int OLY_HERO_POINT_BONUS;
  public static int OLY_DEFAULT_POINTS;
  public static int OLY_WBONUS_POINTS;
  public static int OLY_VICTORY_RITEMID;
  public static int OLY_VICTORY_CFREE_RITEMCNT;
  public static int OLY_VICTORY_CBASE_RITEMCNT;
  public static int OLY_VICTORY_3TEAM_RITEMCNT;
  public static double OLY_LOOSE_POINTS_MUL;
  public static int OLY_ITEMS_SETTLEMENT_PER_POINT;
  public static int OLY_MAX_TOTAL_MATCHES;
  public static int OLY_CF_MATCHES;
  public static int OLY_CB_MATCHES;
  public static int OLY_TB_MATCHES;
  public static int OLY_MIN_CF_START;
  public static int OLY_MIN_CB_START;
  public static int OLY_MIN_TB_START;
  public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR;
  public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE;
  public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS;
  public static int OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY;
  public static boolean OLYMPIAD_WINNER_ANNOUCE;
  public static String OLYMPIAD_WINNER_MESSAGE;
  public static int[] OLY_RESTRICTED_SKILL_IDS;
  public static Map<Integer, QuestRates> QUEST_RATES = new ConcurrentHashMap();

  public static void loadServerConfig() {
    ExProperties serverSettings = load("config/server.properties");
    GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
    GAME_SERVER_LOGIN_PORT = serverSettings.getProperty("LoginPort", 9013);
    GAME_SERVER_LOGIN_CRYPT = serverSettings.getProperty("LoginUseCrypt", true);
    AUTH_SERVER_AGE_LIMIT = serverSettings.getProperty("ServerAgeLimit", 0);
    AUTH_SERVER_GM_ONLY = serverSettings.getProperty("ServerGMOnly", false);
    AUTH_SERVER_BRACKETS = serverSettings.getProperty("ServerBrackets", false);
    AUTH_SERVER_IS_PVP = serverSettings.getProperty("PvPServer", false);
    USE_SECOND_PASSWORD_AUTH = serverSettings.getProperty("UseSecondPasswordAuth", false);
    SECOND_AUTH_BLOCK_TIME = (long)serverSettings.getProperty("SecondAuthBlockTime", 28800);
    SECOND_AUTH_MAX_TRYS = serverSettings.getProperty("SecondAuthBlockMaxTry", 5);
    SECOND_AUTH_MIN_LENG = serverSettings.getProperty("SecondAuthPinMinLength", 6);
    SECOND_AUTH_MAX_LENG = serverSettings.getProperty("SecondAuthPinMaxLength", 8);
    String[] var1 = serverSettings.getProperty("ServerType", ArrayUtils.EMPTY_STRING_ARRAY);
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      String a = var1[var3];
      if (!a.trim().isEmpty()) {
        ServerType t = ServerType.valueOf(a.toUpperCase());
        AUTH_SERVER_SERVER_TYPE |= t.getMask();
      }
    }

    INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "*");
    EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "*");
    REQUEST_ID = serverSettings.getProperty("RequestServerID", 0);
    ACCEPT_ALTERNATE_ID = serverSettings.getProperty("AcceptAlternateID", true);
    GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname");
    PORTS_GAME = serverSettings.getProperty("GameserverPort", new int[]{7777});
    EVERYBODY_HAS_ADMIN_RIGHTS = serverSettings.getProperty("EverybodyHasAdminRights", false);
    HIDE_GM_STATUS = serverSettings.getProperty("HideGMStatus", false);
    SHOW_GM_LOGIN = serverSettings.getProperty("ShowGMLogin", true);
    SAVE_GM_EFFECTS = serverSettings.getProperty("SaveGMEffects", false);
    CNAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", "[A-Za-z0-9А-Яа-я]{2,16}");
    CLAN_NAME_TEMPLATE = serverSettings.getProperty("ClanNameTemplate", "[A-Za-z0-9А-Яа-я]{3,16}");
    CLAN_TITLE_TEMPLATE = serverSettings.getProperty("ClanTitleTemplate", "[A-Za-z0-9А-Яа-я \\p{Punct}]{1,16}");
    ALLY_NAME_TEMPLATE = serverSettings.getProperty("AllyNameTemplate", "[A-Za-z0-9А-Яа-я]{3,16}");
    CNAME_FORBIDDEN_PATTERN = serverSettings.getProperty("ForbiddenCharName", "(adm)|(admin)");
    CNAME_FORBIDDEN_NAMES = serverSettings.getProperty("ForbiddenCharNames", ArrayUtils.EMPTY_STRING_ARRAY);
    GLOBAL_SHOUT = serverSettings.getProperty("GlobalShout", false);
    GLOBAL_SHOUT_MIN_LEVEL = serverSettings.getProperty("GlobalShoutMinLevel", 40);
    GLOBAL_SHOUT_MIN_PVP_COUNT = serverSettings.getProperty("GlobalShoutMinPvPCount", 25);
    GLOBAL_TRADE_CHAT = serverSettings.getProperty("GlobalTradeChat", false);
    GLOBAL_TRADE_CHAT_MIN_LEVEL = serverSettings.getProperty("GlobalTradeChatMinLevel", 40);
    GLOBAL_TRADE_MIN_PVP_COUNT = serverSettings.getProperty("GlobalTradeChatMinPvPCount", 25);
    CHAT_RANGE = serverSettings.getProperty("ChatRange", 1250);
    SHOUT_OFFSET = serverSettings.getProperty("ShoutOffset", 0);
    LOG_CHAT = serverSettings.getProperty("LogChat", false);
    RATE_XP = serverSettings.getProperty("RateXp", 1.0D);
    RATE_SP = serverSettings.getProperty("RateSp", 1.0D);
    RATE_QUESTS_REWARD = serverSettings.getProperty("RateQuestsReward", 1.0D);
    RATE_QUESTS_REWARD_EXP_SP = serverSettings.getProperty("RateQuestsRewardExpSp", 1.0D);
    RATE_QUESTS_DROP = serverSettings.getProperty("RateQuestsDrop", 1.0D);
    RATE_CLAN_REP_SCORE = serverSettings.getProperty("RateClanRepScore", 1.0D);
    RATE_CLAN_REP_SCORE_MAX_AFFECTED = serverSettings.getProperty("RateClanRepScoreMaxAffected", 2);
    RATE_DROP_ADENA = serverSettings.getProperty("RateDropAdena", 1.0D);
    RATE_DROP_ITEMS = serverSettings.getProperty("RateDropItems", 1.0D);
    RATE_DROP_HERBS = serverSettings.getProperty("RateDropHerbs", 1.0D);
    ALLOW_TREASURE_BOX = serverSettings.getProperty("TreasureBox", true);
    RATE_DROP_RAIDBOSS = serverSettings.getProperty("RateRaidBoss", 1.0D);
    RATE_RAIDBOSS_XP = serverSettings.getProperty("RateExpRaidBoss", RATE_XP);
    RATE_RAIDBOSS_SP = serverSettings.getProperty("RateSpRaidBoss", RATE_SP);
    RATE_DROP_SPOIL = serverSettings.getProperty("RateDropSpoil", 1.0D);
    RATE_DROP_SEAL_STONES = serverSettings.getProperty("RateDropSealStones", 1.0D);
    ALT_MULTI_DROP = serverSettings.getProperty("AltMultiDrop", true);
    NO_RATE_ITEMS = serverSettings.getProperty("NoRateItemIds", new int[]{6660, 6662, 6661, 6659, 6656, 6658, 8191, 6657});
    NO_DROP_ITEMS = serverSettings.getProperty("NoDropItems", new int[0]);
    NO_DROP_ITEMS_FOR_SWEEP = serverSettings.getProperty("NoDropItemsForSweep", new int[0]);
    NO_RATE_EQUIPMENT = serverSettings.getProperty("NoRateEquipment", true);
    NO_RATE_KEY_MATERIAL = serverSettings.getProperty("NoRateKeyMaterial", true);
    NO_RATE_RECIPES = serverSettings.getProperty("NoRateRecipes", true);
    RATE_DROP_SIEGE_GUARD = serverSettings.getProperty("RateSiegeGuard", 1.0D);
    RATE_MANOR = serverSettings.getProperty("RateManor", 1.0D);
    RATE_FISH_DROP_COUNT = serverSettings.getProperty("RateFishDropCount", 1.0D);
    RATE_PARTY_MIN = serverSettings.getProperty("RatePartyMin", false);
    RATE_MOB_SPAWN = serverSettings.getProperty("RateMobSpawn", 1);
    RATE_MOB_SPAWN_MIN_LEVEL = serverSettings.getProperty("RateMobMinLevel", 1);
    RATE_MOB_SPAWN_MAX_LEVEL = serverSettings.getProperty("RateMobMaxLevel", 100);
    RATE_RAID_REGEN = serverSettings.getProperty("RateRaidRegen", 1.0D);
    RATE_RAID_DEFENSE = serverSettings.getProperty("RateRaidDefense", 1.0D);
    RATE_RAID_ATTACK = serverSettings.getProperty("RateRaidAttack", 1.0D);
    RATE_MOD_MIN_LEVEL_LIMIT = serverSettings.getProperty("RateRaidMinLevelLimit", 1);
    RATE_MOD_MAX_LEVEL_LIMIT = serverSettings.getProperty("RateRaidMaxLevelLimit", 90);
    RATE_EPIC_DEFENSE = serverSettings.getProperty("RateEpicDefense", 1.0D);
    RATE_EPIC_ATTACK = serverSettings.getProperty("RateEpicAttack", 1.0D);
    RAID_MAX_LEVEL_DIFF = serverSettings.getProperty("RaidMaxLevelDiff", 8);
    PARALIZE_ON_RAID_DIFF = serverSettings.getProperty("ParalizeOnRaidLevelDiff", true);
    SKILL_COST_RATE = serverSettings.getProperty("SkillCostRate", 1);
    AUTODESTROY_ITEM_AFTER = serverSettings.getProperty("AutoDestroyDroppedItemAfter", 0);
    AUTODESTROY_PLAYER_ITEM_AFTER = serverSettings.getProperty("AutoDestroyPlayerDroppedItemAfter", 0);
    DELETE_DAYS = serverSettings.getProperty("DeleteCharAfterDays", 7);
    PURGE_BYPASS_TASK_FREQUENCY = serverSettings.getProperty("PurgeTaskFrequency", 60);

    try {
      DATAPACK_ROOT = (new File(serverSettings.getProperty("DatapackRoot", "."))).getCanonicalFile();
    } catch (IOException var6) {
      _log.error("", var6);
    }

    ALLOW_DISCARDITEM = serverSettings.getProperty("AllowDiscardItem", true);
    ALLOW_MAIL = serverSettings.getProperty("AllowMail", true);
    ALLOW_WAREHOUSE = serverSettings.getProperty("AllowWarehouse", true);
    ALLOW_WATER = serverSettings.getProperty("AllowWater", true);
    ALLOW_CURSED_WEAPONS = serverSettings.getProperty("AllowCursedWeapons", false);
    DROP_CURSED_WEAPONS_ON_KICK = serverSettings.getProperty("DropCursedWeaponsOnKick", false);
    MIN_PROTOCOL_REVISION = serverSettings.getProperty("MinProtocolRevision", 740);
    MAX_PROTOCOL_REVISION = serverSettings.getProperty("MaxProtocolRevision", 770);
    MIN_NPC_ANIMATION = serverSettings.getProperty("MinNPCAnimation", 5);
    MAX_NPC_ANIMATION = serverSettings.getProperty("MaxNPCAnimation", 90);
    SERVER_SIDE_NPC_NAME = serverSettings.getProperty("ServerSideNpcName", false);
    SERVER_SIDE_NPC_TITLE = serverSettings.getProperty("ServerSideNpcTitle", false);
    AUTOSAVE = serverSettings.getProperty("Autosave", true);
    AUTOSAVE_ITEMS = serverSettings.getProperty("AutosaveWithItems", false);
    MAXIMUM_ONLINE_USERS = serverSettings.getProperty("MaximumOnlineUsers", 3000);
    DATABASE_HOST = serverSettings.getProperty("DatabaseHost", "127.0.0.1");
    DATABASE_PORT = serverSettings.getProperty("DatabasePort", 3306);
    DATABASE_NAME = serverSettings.getProperty("DatabaseName", "l2db");
    DATABASE_USER = serverSettings.getProperty("DatabaseUser", "root");
    DATABASE_PASS = serverSettings.getProperty("DatabasePassword", "");
    DATABASE_MAX_CONN = serverSettings.getProperty("DatabaseMaxConnections", 64);
    DATABASE_TIMEOUT = serverSettings.getProperty("DatabaseConnectionTimeout", 30);
    USER_INFO_INTERVAL = serverSettings.getProperty("UserInfoInterval", 100L);
    BROADCAST_STATS_INTERVAL = serverSettings.getProperty("BroadcastStatsInterval", true);
    BROADCAST_CHAR_INFO_INTERVAL = serverSettings.getProperty("BroadcastCharInfoInterval", 100L);
    EFFECT_TASK_MANAGER_COUNT = serverSettings.getProperty("EffectTaskManagers", 2);
    SCHEDULED_THREAD_POOL_SIZE = serverSettings.getProperty("ScheduledThreadPoolSize", NCPUS * 4);
    EXECUTOR_THREAD_POOL_SIZE = serverSettings.getProperty("ExecutorThreadPoolSize", NCPUS * 2);
    ENABLE_RUNNABLE_STATS = serverSettings.getProperty("EnableRunnableStats", false);
    SELECTOR_CONFIG.SLEEP_TIME = serverSettings.getProperty("SelectorSleepTime", 10L);
    SELECTOR_CONFIG.INTEREST_DELAY = serverSettings.getProperty("InterestDelay", 30L);
    SELECTOR_CONFIG.MAX_SEND_PER_PASS = serverSettings.getProperty("MaxSendPerPass", 32);
    SELECTOR_CONFIG.READ_BUFFER_SIZE = serverSettings.getProperty("ReadBufferSize", 65536);
    SELECTOR_CONFIG.WRITE_BUFFER_SIZE = serverSettings.getProperty("WriteBufferSize", 131072);
    SELECTOR_CONFIG.HELPER_BUFFER_COUNT = serverSettings.getProperty("BufferPoolSize", 64);
    CHAT_MESSAGE_MAX_LEN = serverSettings.getProperty("ChatMessageLimit", 1000);
    StringTokenizer st = new StringTokenizer(serverSettings.getProperty("ChatBanChannels", "ALL,SHOUT,TELL,TRADE,HERO_VOICE"), ",");
    ArrayList channels = new ArrayList();

    while(st.hasMoreTokens()) {
      channels.add(ChatType.valueOf(st.nextToken()));
    }

    BAN_CHANNEL_LIST = (ChatType[])channels.toArray(new ChatType[channels.size()]);
    BANCHAT_ANNOUNCE = serverSettings.getProperty("BANCHAT_ANNOUNCE", true);
    BANCHAT_ANNOUNCE_FOR_ALL_WORLD = serverSettings.getProperty("BANCHAT_ANNOUNCE_FOR_ALL_WORLD", true);
    BANCHAT_ANNOUNCE_NICK = serverSettings.getProperty("BANCHAT_ANNOUNCE_NICK", true);
    DEFAULT_LANG = serverSettings.getProperty("DefaultLang", "ru");
    RESTART_AT_TIME = serverSettings.getProperty("AutoRestartAt", "0 5 * * *");
    SHIFT_BY = serverSettings.getProperty("HShift", 12);
    SHIFT_BY_Z = serverSettings.getProperty("VShift", 11);
    MAP_MIN_Z = serverSettings.getProperty("MapMinZ", -32768);
    MAP_MAX_Z = serverSettings.getProperty("MapMaxZ", 32767);
    MOVE_TASK_QUANTUM_PC = serverSettings.getProperty("MoveTaskQuantumForPC", 400);
    MOVE_TASK_QUANTUM_NPC = serverSettings.getProperty("MoveTaskQuantumForNPC", 800);
    MOVE_OFFLOAD_MTL_PC = serverSettings.getProperty("OffloadMTLForPC", false);
    ATTACK_PACKET_DELAY = serverSettings.getProperty("AttackPacketDelay", 500);
    ATTACK_END_DELAY = serverSettings.getProperty("AttackEndTime", 50);
    PICKUP_PACKET_DELAY = serverSettings.getProperty("PickUpPacketDelay", 200);
    ENCHANT_PACKET_DELAY = serverSettings.getProperty("EnchantPacketDelay", 500);
    RELOAD_PACKET_DELAY = serverSettings.getProperty("RequestReloadPacketDelay", 500);
    DAMAGE_FROM_FALLING = serverSettings.getProperty("DamageFromFalling", true);
    DONTLOADSPAWN = serverSettings.getProperty("StartWithoutSpawn", false);
    DONTLOADQUEST = serverSettings.getProperty("StartWithoutQuest", false);
    MAX_REFLECTIONS_COUNT = serverSettings.getProperty("MaxReflectionsCount", 300);
    WEAR_DELAY = serverSettings.getProperty("WearDelay", 5);
    COMMUNITYBOARD_ENABLED = serverSettings.getProperty("AllowCommunityBoard", true);
    BBS_DEFAULT = serverSettings.getProperty("BBSDefault", "_bbshome");
    HTM_CACHE_MODE = serverSettings.getProperty("HtmCacheMode", 1);
    ALLOW_MULILOGIN = serverSettings.getProperty("AllowMultilogin", false);
    ALT_CG_MODULE = serverSettings.getProperty("AltClientGuard", "NONE");
  }

  public static void loadTelnetConfig() {
    ExProperties telnetSettings = load("config/telnet.properties");
    IS_TELNET_ENABLED = telnetSettings.getProperty("EnableTelnet", false);
    TELNET_DEFAULT_ENCODING = telnetSettings.getProperty("TelnetEncoding", "UTF-8");
    TELNET_PORT = telnetSettings.getProperty("Port", 7000);
    TELNET_HOSTNAME = telnetSettings.getProperty("BindAddress", "127.0.0.1");
    TELNET_PASSWORD = telnetSettings.getProperty("Password", "");
  }

  public static void loadResidenceConfig() {
    ExProperties residenceSettings = load("config/residence.properties");
    CH_BID_CURRENCY_ITEM_ID = residenceSettings.getProperty("ClanHallCurrencyItemId", 57);
    RESIDENCE_LEASE_FUNC_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseFuncMultiplier", 1.0D);
    RESIDENCE_LEASE_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseMultiplier", 1.0D);
    CLNHALL_REWARD_CYCLE = residenceSettings.getProperty("ClanHallSellingCycle", 168);
    CLAN_MIN_LEVEL_FOR_BID = residenceSettings.getProperty("ClanMinLevelForBid", 2);
    CH_DISPLAY_IDS = new TIntHashSet();
    CH_DISPLAY_IDS.addAll(residenceSettings.getProperty("ClanHallSellListIds", new int[]{22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61}));
    int[] tempCastleValidatonTime = residenceSettings.getProperty("CastleValidationDate", new int[]{2, 4, 2003});
    CASTLE_VALIDATION_DATE = Calendar.getInstance();
    CASTLE_VALIDATION_DATE.set(5, tempCastleValidatonTime[0]);
    CASTLE_VALIDATION_DATE.set(2, tempCastleValidatonTime[1] - 1);
    CASTLE_VALIDATION_DATE.set(1, tempCastleValidatonTime[2]);
    CASTLE_VALIDATION_DATE.set(11, 0);
    CASTLE_VALIDATION_DATE.set(12, 0);
    CASTLE_VALIDATION_DATE.set(13, 0);
    CASTLE_VALIDATION_DATE.set(14, 0);
  }

  public static void loadOtherConfig() {
    ExProperties otherSettings = load("config/other.properties");
    DEEPBLUE_DROP_RULES = otherSettings.getProperty("UseDeepBlueDropRules", true);
    DEEPBLUE_DROP_MAXDIFF = otherSettings.getProperty("DeepBlueDropMaxDiff", 8);
    DEEPBLUE_DROP_RAID_MAXDIFF = otherSettings.getProperty("DeepBlueDropRaidMaxDiff", 2);
    EXP_SP_DIFF_LIMIT = otherSettings.getProperty("ExpSpMaxDiffLimit", 0);
    SWIMING_SPEED = otherSettings.getProperty("SwimingSpeedTemplate", 50);
    THRESHOLD_LEVEL_DIFF = otherSettings.getProperty("ExpSpThresholdLevelDiff", 5);
    PARTY_PENALTY_EXP_SP_MAX_LEVEL = otherSettings.getProperty("ExpSpPartyPenaltyLevelMax", 10);
    PARTY_PENALTY_EXP_SP_MIN_LEVEL = otherSettings.getProperty("ExpSpPartyPenaltyLevelMin", 14);
    INVENTORY_MAXIMUM_NO_DWARF = otherSettings.getProperty("MaximumSlotsForNoDwarf", 80);
    INVENTORY_MAXIMUM_DWARF = otherSettings.getProperty("MaximumSlotsForDwarf", 100);
    DWARF_AUTOMATICALLY_CRYSTALLIZE_ON_ITEM_DELETE = otherSettings.getProperty("DwarfAutoCrystallizeOnItemDelete", false);
    INVENTORY_MAXIMUM_GM = otherSettings.getProperty("MaximumSlotsForGMPlayer", 250);
    MULTISELL_SIZE = otherSettings.getProperty("MultisellPageSize", 10);
    WAREHOUSE_SLOTS_NO_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForNoDwarf", 100);
    WAREHOUSE_SLOTS_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForDwarf", 120);
    WAREHOUSE_SLOTS_CLAN = otherSettings.getProperty("MaximumWarehouseSlotsForClan", 200);
    FREIGHT_SLOTS = otherSettings.getProperty("MaximumFreightSlots", 10);
    FOLLOW_ARRIVE_DISTANCE = otherSettings.getProperty("FollowArriveDistance", 150);
    SEND_LINEAGE2_WELCOME_MESSAGE = otherSettings.getProperty("SendLineage2WelcomeMessage", true);
    SEND_SSQ_WELCOME_MESSAGE = otherSettings.getProperty("SendSSQWelcomeMessage", true);
    ENCHANT_MAX = otherSettings.getProperty("EnchantMax", 20);
    ARMOR_ENCHANT_6_SKILL = otherSettings.getProperty("EnchantSkill6ChangeOnEnchantLevel", 6);
    ENCHANT_CHANCES_ARMOR = otherSettings.getProperty("EnchantChancesArmor", new double[]{100.0D, 100.0D, 100.0D, 50.0D, 50.0D, 33.0D, 25.0D, 20.0D, 14.0D, 11.0D, 8.0D, 6.0D, 5.0D, 4.0D, 3.0D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D});
    ENCHANT_CHANCES_FULL_ARMOR = otherSettings.getProperty("EnchantChancesFullArmor", new double[]{100.0D, 100.0D, 100.0D, 100.0D, 50.0D, 50.0D, 33.0D, 25.0D, 20.0D, 14.0D, 11.0D, 8.0D, 6.0D, 5.0D, 4.0D, 3.0D, 2.0D, 2.0D, 1.0D, 1.0D});
    ENCHANT_CHANCES_JEWELRY = otherSettings.getProperty("EnchantChancesJewelry", new double[]{100.0D, 100.0D, 100.0D, 50.0D, 50.0D, 33.0D, 25.0D, 20.0D, 14.0D, 11.0D, 8.0D, 6.0D, 5.0D, 4.0D, 3.0D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D});
    ENCHANT_CHANCES_WEAPON = otherSettings.getProperty("EnchantChancesWeapon", new double[]{100.0D, 100.0D, 100.0D, 50.0D, 50.0D, 33.0D, 25.0D, 20.0D, 14.0D, 11.0D, 8.0D, 6.0D, 5.0D, 4.0D, 3.0D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D});
    ARMOR_OVERENCHANT_HPBONUS_LIMIT = otherSettings.getProperty("ArmorOverEnchantHPBonusLimit", 10) - 3;
    SHOW_ENCHANT_EFFECT_RESULT = otherSettings.getProperty("ShowEnchantEffectResult", false);
    ENCHANT_ATTRIBUTE_STONE_CHANCE = otherSettings.getProperty("EnchantAttributeChance", 50);
    ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE = otherSettings.getProperty("EnchantAttributeCrystalChance", 30);
    ENCHANT_ATTRIBUTE_ENERGY_CHANCE = otherSettings.getProperty("EnchantAttributeEnergyChance", 100);
    REGEN_SIT_WAIT = otherSettings.getProperty("RegenSitWait", false);
    STARTING_ADENA = otherSettings.getProperty("StartingAdena", 0);
    STARTING_ITEMS_MAGE = otherSettings.getProperty("StartingMageItems", ArrayUtils.EMPTY_INT_ARRAY);
    STARTING_ITEMS_FIGHTER = otherSettings.getProperty("StartingFighterItems", ArrayUtils.EMPTY_INT_ARRAY);
    if (STARTING_ITEMS_MAGE.length % 2 == 0 && STARTING_ITEMS_FIGHTER.length % 2 == 0) {
      UNSTUCK_SKILL = otherSettings.getProperty("UnstuckSkill", false);
      BLOCK_BUFF_SKILL = otherSettings.getProperty("BlockBuffSkill", false);
      BLOCK_BUFF_EXCLUDE = new HashIntSet(new CArrayIntList(otherSettings.getProperty("BlockBuffExcludeSkill", new int[]{1323})));
      NOBLES_BUFF_SKILL = otherSettings.getProperty("NoblesBuffSkill", false);
      RESPAWN_RESTORE_CP = otherSettings.getProperty("RespawnRestoreCP", 0.0D) / 100.0D;
      RESPAWN_RESTORE_HP = otherSettings.getProperty("RespawnRestoreHP", 65.0D) / 100.0D;
      RESPAWN_RESTORE_MP = otherSettings.getProperty("RespawnRestoreMP", 0.0D) / 100.0D;
      MAX_PVTSTORE_SLOTS_DWARF = otherSettings.getProperty("MaxPvtStoreSlotsDwarf", 5);
      MAX_PVTSTORE_SLOTS_DWARF_FIRST_JOB = otherSettings.getProperty("MaxPvtStoreSlotsDwarfOnFirstProf", 3);
      MAX_PVTSTORE_SLOTS_OTHER = otherSettings.getProperty("MaxPvtStoreSlotsOther", 4);
      MAX_PVTSTORE_SLOTS_OTHER_FIRST_JOB = otherSettings.getProperty("MaxPvtStoreSlotsOtherOnFirstProf", 2);
      MAX_PVTCRAFT_SLOTS = otherSettings.getProperty("MaxPvtManufactureSlots", 20);
      PRIVATE_BUY_MATCH_ENCHANT = otherSettings.getProperty("AltPrivateBuyMatchEnchant", true);
      SENDSTATUS_TRADE_JUST_OFFLINE = otherSettings.getProperty("SendStatusTradeJustOffline", false);
      SENDSTATUS_TRADE_MOD = otherSettings.getProperty("SendStatusTradeMod", 1.0D);
      MUL_PLAYERS_ONLINE = (double)otherSettings.getProperty("MulOnlinePlayers", 1);
      ANNOUNCE_MAMMON_SPAWN = otherSettings.getProperty("AnnounceMammonSpawn", true);
      GM_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("GMNameColour", "FFFFFF"));
      GM_HERO_AURA = otherSettings.getProperty("GMHeroAura", false);
      NORMAL_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("NormalNameColour", "FFFFFF"));
      CLANLEADER_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("ClanleaderNameColour", "FFFFFF"));
      SHOW_HTML_WELCOME = otherSettings.getProperty("ShowHTMLWelcome", false);
      GAME_POINT_ITEM_ID = otherSettings.getProperty("GamePointItemId", -1);
      OTHER_MAGE_BUFF_ON_CHAR_CREATE = new ArrayList();
      String[] var1 = StringUtils.split(otherSettings.getProperty("StartMageBuff", "1303-1"), ";,");
      int var2 = var1.length;

      int var3;
      String s;
      String[] ss;
      for(var3 = 0; var3 < var2; ++var3) {
        s = var1[var3];
        ss = StringUtils.split(s, "-:");
        OTHER_MAGE_BUFF_ON_CHAR_CREATE.add(Pair.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])));
      }

      OTHER_WARRIOR_BUFF_ON_CHAR_CREATE = new ArrayList();
      var1 = StringUtils.split(otherSettings.getProperty("StartWarriorBuff", "1086-1"), ";,");
      var2 = var1.length;

      for(var3 = 0; var3 < var2; ++var3) {
        s = var1[var3];
        ss = StringUtils.split(s, "-:");
        OTHER_WARRIOR_BUFF_ON_CHAR_CREATE.add(Pair.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])));
      }

    } else {
      throw new RuntimeException("ERROR: CONFIG StartingItems WRONG SIZE");
    }
  }

  public static void loadClanConfig() {
    ExProperties clanSettings = load("config/clan.properties");
    CLAN_LEAVE_TIME_PERNALTY = clanSettings.getProperty("ClanLeaveTimePenalty", 24L) * 3600000L;
    EXPELLED_MEMBER_PENALTY = clanSettings.getProperty("ExpelledClanPenalty", 24L) * 3600000L;
    LEAVED_ALLY_PENALTY = clanSettings.getProperty("AllyLeavedPenalty", 24L) * 3600000L;
    DISSOLVED_ALLY_PENALTY = clanSettings.getProperty("DissolvedAllyPenalty", 24L) * 3600000L;
    NEW_CLAN_CREATE_PENALTY = clanSettings.getProperty("DissolvedClanPenalty", 240L) * 3600000L;
    CLAN_DISBAND_TIME = clanSettings.getProperty("ClanDisbandTimeDelay", 48L) * 3600000L;
    CLAN_DISBAND_PENALTY = clanSettings.getProperty("ClanDisbandCancelPenalty", 168L) * 3600000L;
    CLAN_INIT_LEVEL = clanSettings.getProperty("ClanInitLevel", 0);
    CLAN_REPUTATION_BONUS_ON_CREATE = clanSettings.getProperty("ClanReputationBonusOnCreate", 0);
    FULL_CLAN_SKILLS_ON_CREATE = clanSettings.getProperty("FullClanSkillsOnCreate", false);
    CHARACTER_MIN_LEVEL_FOR_CLAN_CREATE = clanSettings.getProperty("CharacterMinLevelForClanCreate", 10);
    CLAN_LEADER_CHANGE_METHOD = clanSettings.getProperty("ClanLeaderChangeMethod", false);
    LIMIT_CLAN_LEVEL0 = clanSettings.getProperty("MainClanMembersLimitOnLevel0", 10);
    LIMIT_CLAN_LEVEL1 = clanSettings.getProperty("MainClanMembersLimitOnLevel1", 15);
    LIMIT_CLAN_LEVEL2 = clanSettings.getProperty("MainClanMembersLimitOnLevel2", 20);
    LIMIT_CLAN_LEVEL3 = clanSettings.getProperty("MainClanMembersLimitOnLevel3", 30);
    LIMIT_CLAN_LEVEL_4_AND_HIGH = clanSettings.getProperty("MainClanMembersLimitOnLevel4", 40);
    LIMIT_CLAN_HIGH_UNITS = clanSettings.getProperty("SubUnitRoyalLimitMembers", 20);
    LIMIT_CLAN_LOW_UNITS = clanSettings.getProperty("SubUnitKnightLimitMembers", 10);
    LIMIT_CLAN_ACADEMY = clanSettings.getProperty("ClanAcademyLimit", 20);
    MIN_CLAN_LEVEL_FOR_DECLARED_WAR = clanSettings.getProperty("MinClanLevelForDeclaredWar", 3);
    MIN_CLAN_MEMBER_FOR_DECLARED_WAR = clanSettings.getProperty("MinClanMembersForDeclaredWar", 15);
    CRP_REWARD_ON_WAR_KILL_OVER_LEVEL = clanSettings.getProperty("OnWarKillCRPRewardOverLevel", 2);
    CRP_REWARD_ON_WAR_KILL = clanSettings.getProperty("OnWarKillCRPReward", 1);
    CLAN_FIRST_LEVEL_SP = clanSettings.getProperty("FirstClanLevelUpSP", 20000);
    CLAN_FIRST_LEVEL_ADENA = clanSettings.getProperty("FirstClanLevelUpAdena", 650000);
    CLAN_SECOND_LEVEL_SP = clanSettings.getProperty("SecondClanLevelUpSP", 100000);
    CLAN_SECOND_LEVEL_ADENA = clanSettings.getProperty("SecondClanLevelUpAdena", 2500000);
    CLAN_THIRD_LEVEL_SP = clanSettings.getProperty("ThirdClanLevelUpSP", 350000);
    CLAN_FOUR_LEVEL_SP = clanSettings.getProperty("FourClanLevelUpSP", 1000000);
    CLAN_FIVE_LEVEL_SP = clanSettings.getProperty("FiveClanLevelUpSP", 2500000);
    CLAN_SIX_LEVEL_CLAN_REPUTATION = clanSettings.getProperty("SixClanLevelUpReputationCount", 10000);
    CLAN_SIX_LEVEL_CLAN_MEMBER_COUNT = clanSettings.getProperty("SixClanLevelUpMemberCount", 30);
    CLAN_SEVEN_LEVEL_CLAN_REPUTATION = clanSettings.getProperty("SevenClanLevelUpReputationCount", 20000);
    CLAN_SEVEN_LEVEL_CLAN_MEMBER_COUNT = clanSettings.getProperty("SevenClanLevelUpMemberCount", 50);
    CLAN_EIGHT_LEVEL_CLAN_REPUTATION = clanSettings.getProperty("EightClanLevelUpReputationCount", 40000);
    CLAN_EIGHT_LEVEL_CLAN_MEMBER_COUNT = clanSettings.getProperty("EightClanLevelUpMemberCount", 80);
    ALLY_ALLOW_BUFF_DEBUFFS = clanSettings.getProperty("AllyRelationLikeClan", false);
    ALLOW_TEMPORARILY_ALLY_ON_FIRST_SIEGE = clanSettings.getProperty("AllowTemporarilyAllyOnFirstSiege", false);
    ALT_NPC_CLAN = clanSettings.getProperty("NpcClanCrestDisplay", -1);
    CHECK_CLAN_RANK_ON_COMMAND_CHANNEL_CREATE = clanSettings.getProperty("CheckClanRankForCommandChannelCreate", true);
    MIN_CLAN_LEVEL_FOR_REPUTATION = clanSettings.getProperty("MinClanLevelForReputationReceiving", 5);
  }

  public static void loadSpoilConfig() {
    ExProperties spoilSettings = load("config/spoil.properties");
    MINIMUM_SPOIL_RATE = spoilSettings.getProperty("MinimumPercentChanceOfSpoilSuccess", 1.0D);
    MANOR_SOWING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingSuccess", 100.0D);
    MANOR_SOWING_ALT_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingAltSuccess", 10.0D);
    MANOR_HARVESTING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfHarvestingSuccess", 90.0D);
    MANOR_HARVESTING_REWARD_RATE = spoilSettings.getProperty("HarvestingRewardRate", 1.0D);
    MANOR_DIFF_PLAYER_TARGET = spoilSettings.getProperty("MinDiffPlayerMob", 5);
    MANOR_DIFF_PLAYER_TARGET_PENALTY = spoilSettings.getProperty("DiffPlayerMobPenalty", 5.0D);
    MANOR_DIFF_SEED_TARGET = spoilSettings.getProperty("MinDiffSeedMob", 5);
    MANOR_DIFF_SEED_TARGET_PENALTY = spoilSettings.getProperty("DiffSeedMobPenalty", 5.0D);
    ALLOW_MANOR = spoilSettings.getProperty("AllowManor", true);
    MANOR_REFRESH_TIME = spoilSettings.getProperty("AltManorRefreshTime", 20);
    MANOR_REFRESH_MIN = spoilSettings.getProperty("AltManorRefreshMin", 0);
    MANOR_APPROVE_TIME = spoilSettings.getProperty("AltManorApproveTime", 6);
    MANOR_APPROVE_MIN = spoilSettings.getProperty("AltManorApproveMin", 0);
    MANOR_MAINTENANCE_PERIOD = spoilSettings.getProperty("AltManorMaintenancePeriod", 360000);
  }

  public static void loadFormulasConfig() {
    ExProperties formulasSettings = load("config/formulas.properties");
    SKILLS_CHANCE_MOD = formulasSettings.getProperty("SkillsChanceMod", 11.0D);
    SKILLS_CHANCE_POW = formulasSettings.getProperty("SkillsChancePow", 0.5D);
    SKILLS_CHANCE_MIN = formulasSettings.getProperty("SkillsChanceMin", 5.0D);
    SKILLS_CHANCE_CAP = formulasSettings.getProperty("SkillsChanceCap", 95.0D);
    SKILLS_CAST_TIME_MIN = formulasSettings.getProperty("SkillsCastTimeMin", 333);
    SKILLS_DISPEL_MOD_MAX = formulasSettings.getProperty("DispelMaxChance", 75);
    SKILLS_DISPEL_MOD_MIN = formulasSettings.getProperty("DispelMinChance", 25);
    ALT_ABSORB_DAMAGE_MODIFIER = formulasSettings.getProperty("AbsorbDamageModifier", 1.0D);
    RATE_OVERHIT = formulasSettings.getProperty("RateOverHitXp", 1.0D);
    LIM_PATK = formulasSettings.getProperty("LimitPatk", 20000);
    LIM_MATK = formulasSettings.getProperty("LimitMAtk", 25000);
    LIM_PDEF = formulasSettings.getProperty("LimitPDef", 15000);
    LIM_MDEF = formulasSettings.getProperty("LimitMDef", 15000);
    LIM_PATK_SPD = formulasSettings.getProperty("LimitPatkSpd", 1500);
    LIM_MATK_SPD = formulasSettings.getProperty("LimitMatkSpd", 1999);
    LIM_CRIT_DAM = formulasSettings.getProperty("LimitCriticalDamage", 2000);
    LIM_CRIT = formulasSettings.getProperty("LimitCritical", 500);
    LIM_MCRIT = formulasSettings.getProperty("LimitMCritical", 20);
    MCRITICAL_CRIT_POWER = formulasSettings.getProperty("mCritDamagePower", 4.0D);
    BASE_MAGE_CAST_SPEED = formulasSettings.getProperty("BaseMageCastSpeed", 333);
    BASE_WARRIOR_CAST_SPEED = formulasSettings.getProperty("BaseWarriorCastSpeed", 333);
    LIM_ACCURACY = formulasSettings.getProperty("LimitAccuracy", 200);
    LIM_EVASION = formulasSettings.getProperty("LimitEvasion", 200);
    LIM_MOVE = formulasSettings.getProperty("LimitMove", 250);
    LIM_MAX_CP = formulasSettings.getProperty("LimitMaxCP", 100000);
    LIM_MAX_HP = formulasSettings.getProperty("LimitMaxHP", 40000);
    LIM_MAX_MP = formulasSettings.getProperty("LimitMaxMP", 40000);
    MCRITICAL_BASE_STAT = (double)formulasSettings.getProperty("MCriticalBaseStat", 1);
    MDAM_CRIT_POSSIBLE = formulasSettings.getProperty("MDamCritPossibale", true);
    HEAL_CRIT_POSSIBLE = formulasSettings.getProperty("HealCritPossibale", false);
    LIM_FAME = formulasSettings.getProperty("LimitFame", 50000);
    ALT_NPC_PATK_MODIFIER = formulasSettings.getProperty("NpcPAtkModifier", 1.0D);
    ALT_NPC_MATK_MODIFIER = formulasSettings.getProperty("NpcMAtkModifier", 1.0D);
    ALT_NPC_MAXHP_MODIFIER = formulasSettings.getProperty("NpcMaxHpModifier", 1.58D);
    ALT_NPC_MAXMP_MODIFIER = formulasSettings.getProperty("NpcMapMpModifier", 1.11D);
    ALT_NPC_LIM_MCRIT = formulasSettings.getProperty("NpcLimitNpcMCritical", 20);
    ALT_POLE_DAMAGE_MODIFIER = formulasSettings.getProperty("PoleDamageModifier", 1.0D);
    MIN_ATK_DELAY = formulasSettings.getProperty("MinAttackDelay", 333);
    CALC_EFFECT_TIME_YIELD_AND_RESIST = formulasSettings.getProperty("CalcEffectTimeYieldAndResist", false);
  }

  public static void loadAltSettings() {
    ExProperties altSettings = load("config/altsettings.properties");
    ALT_GAME_DELEVEL = altSettings.getProperty("Delevel", true);
    ALT_SAVE_UNSAVEABLE = altSettings.getProperty("AltSaveUnsaveable", false);
    ALT_SAVE_EFFECTS_REMAINING_TIME = altSettings.getProperty("AltSaveEffectsRemainingTime", 5);
    ALT_SHOW_REUSE_MSG = altSettings.getProperty("AltShowSkillReuseMessage", true);
    ALT_DELETE_SA_BUFFS = altSettings.getProperty("AltDeleteSABuffs", false);
    BUFF_STICK_FOR_ALL = altSettings.getProperty("AltBuffStickUse", false);
    AUTO_LOOT = altSettings.getProperty("AutoLoot", false);
    AUTO_LOOT_HERBS = altSettings.getProperty("AutoLootHerbs", false);
    AUTO_LOOT_ADENA = altSettings.getProperty("AutoLootAdena", false);
    AUTO_LOOT_INDIVIDUAL = altSettings.getProperty("AutoLootIndividual", false);
    AUTO_LOOT_FROM_RAIDS = altSettings.getProperty("AutoLootFromRaids", false);
    AUTO_LOOT_PK = altSettings.getProperty("AutoLootPK", false);
    ALT_GAME_KARMA_PLAYER_CAN_SHOP = altSettings.getProperty("AltKarmaPlayerCanShop", false);
    SAVING_SPS = altSettings.getProperty("SavingSpS", false);
    MANAHEAL_SPS_BONUS = altSettings.getProperty("ManahealSpSBonus", false);
    ALT_RAID_RESPAWN_MULTIPLIER = altSettings.getProperty("AltRaidRespawnMultiplier", 1.0D);
    ALT_RAID_BOSS_SPAWN_ANNOUNCE_IDS = altSettings.getProperty("AltRaidBossSpawnAnnounceIds", ArrayUtils.EMPTY_INT_ARRAY);
    ALT_RAID_BOSS_SPAWN_ANNOUNCE_DELAY = altSettings.getProperty("AltRaidBossSpawnAnnounceDelay", 5) * 60;
    ALT_ALLOW_DROP_AUGMENTED = altSettings.getProperty("AlowDropAugmented", false);
    ALT_ALLOW_REMOTE_SELL_ITEMS_TO_SHOP = altSettings.getProperty("AltAllowRemoteSellItems", false);
    ALT_ALLOW_REMOTE_USE_CARGO_BOX = altSettings.getProperty("AltAllowRemoteUseCargoBox", false);
    ALT_GAME_UNREGISTER_RECIPE = altSettings.getProperty("AltUnregisterRecipe", true);
    ALT_GAME_SHOW_DROPLIST = altSettings.getProperty("AltShowDroplist", true);
    ALLOW_NPC_SHIFTCLICK = altSettings.getProperty("AllowShiftClick", true);
    ALT_FULL_NPC_STATS_PAGE = altSettings.getProperty("AltFullStatsPage", false);
    ALT_GAME_SUBCLASS_WITHOUT_QUESTS = altSettings.getProperty("AltAllowSubClassWithoutQuest", false);
    ALT_ALLOW_SUBCLASS_FOR_CUSTOM_ITEM = altSettings.getProperty("AltAllowSubClassForCustomItem", false);
    ALT_SUBCLASS_FOR_CUSTOM_ITEM_ID = altSettings.getProperty("AltSubClassForCustomItemId", 4037);
    ALT_SUBCLASS_FOR_CUSTOM_ITEM_COUNT = altSettings.getProperty("AltSubClassForCustomItemCount", 100);
    ALTSUBCLASS_LIST_ALL = altSettings.getProperty("AltAllowShowAllSubclassesList", false);
    ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL = altSettings.getProperty("AltAllowLearnOverlordAndWarsmithToAll", false);
    ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF = altSettings.getProperty("AltAllowLearnSubElfToDarkElf", false);
    ALT_GAME_LEVEL_TO_GET_SUBCLASS = altSettings.getProperty("AltLevelToGetSubclass", 75);
    ALT_LEVEL_AFTER_GET_SUBCLASS = altSettings.getProperty("AltLevelAfterGetSubClass", 40);
    ALT_NEW_CHARACTER_LEVEL = altSettings.getProperty("AltNewCharacterLevel", 0);
    ALT_DEFAULT_ACCESS_LEVEL = altSettings.getProperty("AltNewCharacterAccessLevel", 0);
    ALT_GAME_BASE_SUB = altSettings.getProperty("AltSubLimit", 4);
    ALT_MAX_LEVEL = Math.min(altSettings.getProperty("AltMaxLevel", 80), Experience.LEVEL.length - 1);
    ALT_MAX_SUB_LEVEL = Math.min(altSettings.getProperty("AltMaxSubLevel", 80), Experience.LEVEL.length - 1);
    ALT_ALLOW_HERO_SKILLS_ON_SUB_CLASS = altSettings.getProperty("AltAllowHeroSkillsonSubClass", false);
    ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE = altSettings.getProperty("AltAllowOthersWithdrawFromClanWarehouse", false);
    ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER = altSettings.getProperty("AltAllowClanCommandOnlyForClanLeader", true);
    ALT_ALLOW_CLAN_COMMAND_ALLOW_WH = altSettings.getProperty("AltClanCommandAllowPrivToWH", false);
    ALT_ALLOW_MENU_COMMAND = altSettings.getProperty("AltAllowMenuCommand", true);
    ALT_ALLOW_RELOG_COMMAND = altSettings.getProperty("AltAllowRestartChatCommand", false);
    ALT_ALLOW_LANG_COMMAND = altSettings.getProperty("AltAllowChangeLangCommand", true);
    ALT_SUBLASS_SKILL_TRANSFER = altSettings.getProperty("AltTransferSubSkillsToMain", false);
    ALT_GAME_REQUIRE_CLAN_CASTLE = altSettings.getProperty("AltRequireClanCastle", false);
    ALT_GAME_REQUIRE_CASTLE_DAWN = altSettings.getProperty("AltRequireCastleDawn", true);
    ALT_GAME_ALLOW_ADENA_DAWN = altSettings.getProperty("AltAllowAdenaDawn", true);
    ALT_ADD_RECIPES = altSettings.getProperty("AltAddRecipes", 0);
    SS_ANNOUNCE_PERIOD = altSettings.getProperty("SSAnnouncePeriod", 0);
    PETITIONING_ALLOWED = altSettings.getProperty("PetitioningAllowed", true);
    CAN_PETITION_TO_OFFLINE_GM = altSettings.getProperty("CanMakePetitionToOfflineGM", false);
    MAX_PETITIONS_PER_PLAYER = altSettings.getProperty("MaxPetitionsPerPlayer", 5);
    MAX_PETITIONS_PENDING = altSettings.getProperty("MaxPetitionsPending", 25);
    AUTO_LEARN_SKILLS = altSettings.getProperty("AutoLearnSkills", false);
    AUTO_LEARN_FORGOTTEN_SKILLS = altSettings.getProperty("AutoLearnForgottenSkills", false);
    AUTO_LEARN_DIVINE_INSPIRATION = altSettings.getProperty("AutoLearnDivineInspiration", false);
    ALT_SOCIAL_ACTION_REUSE = altSettings.getProperty("AltSocialActionReuse", false);
    ALT_DISABLE_SPELLBOOKS = altSettings.getProperty("AltDisableSpellbooks", false);
    ALT_WEAK_SKILL_LEARN = altSettings.getProperty("AltWeakSkillLearn", false);
    ALT_SIMPLE_SIGNS = altSettings.getProperty("PushkinSignsOptions", false);
    ALT_TELE_TO_CATACOMBS = altSettings.getProperty("TeleToCatacombs", false);
    ALT_BS_CRYSTALLIZE = altSettings.getProperty("BSCrystallize", false);
    ALT_ALLOW_TATTOO = altSettings.getProperty("AllowTattoo", false);
    ALT_ALLOW_CUSTOM_HERO = altSettings.getProperty("AltAllowCustomHero", false);
    ALT_BUFF_LIMIT = altSettings.getProperty("BuffLimit", 20);
    ALT_DEBUFF_LIMIT = altSettings.getProperty("DebuffLimit", 8);
    ALT_TRIGGER_SLOT_ADDER = altSettings.getProperty("AltTriggerSlotAdder", 0);
    ALT_NPC_BUFFER_EFFECT_TIME = altSettings.getProperty("AltNpcBufferEffectTime", 0L) * 1000L;
    ALT_NPC_BUFFER_REUSE_DELAY = altSettings.getProperty("AltNpcBufferReuseBuffDelay", 300L);
    ALT_DEATH_PENALTY = altSettings.getProperty("EnableAltDeathPenalty", false);
    ALLOW_DEATH_PENALTY_C5 = altSettings.getProperty("EnableDeathPenaltyC5", true);
    ALT_DEATH_PENALTY_C5_CHANCE = altSettings.getProperty("DeathPenaltyC5Chance", 10);
    ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY = altSettings.getProperty("ChaoticCanUseScrollOfRecovery", false);
    ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY = altSettings.getProperty("DeathPenaltyC5RateExpPenalty", 1);
    ALT_DEATH_PENALTY_C5_KARMA_PENALTY = altSettings.getProperty("DeathPenaltyC5RateKarma", 1);
    ALT_PK_DEATH_RATE = altSettings.getProperty("AltPKDeathRate", 0.0D);
    ALT_HAIR_TO_ACC_SLOT = altSettings.getProperty("AltEnchantHairAccessory", false);
    ALT_CONSUME_ARROWS = altSettings.getProperty("ConsumeArrows", true);
    ALT_CONSUME_SOULSHOTS = altSettings.getProperty("ConsumeSoulShots", true);
    ALT_CONSUME_SPIRITSHOTS = altSettings.getProperty("ConsumeSpiritShots", true);
    ALT_CONSUME_BEAST_SHOTS = altSettings.getProperty("ConsumeBeastShots", true);
    NONOWNER_ITEM_PICKUP_DELAY = altSettings.getProperty("NonOwnerItemPickupDelay", 15L) * 1000L;
    NONOWNER_ITEM_PICKUP_DELAY_RAID = altSettings.getProperty("NonOwnerItemFromRaidPickupDelay", 300L) * 1000L;
    ALT_NO_LASTHIT = altSettings.getProperty("NoLasthitOnRaid", false);
    ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY = altSettings.getProperty("KamalokaNightmaresPremiumOnly", false);
    ALT_KAMALOKA_NIGHTMARE_REENTER = altSettings.getProperty("SellReenterNightmaresTicket", true);
    ALT_KAMALOKA_ABYSS_REENTER = altSettings.getProperty("SellReenterAbyssTicket", true);
    ALT_KAMALOKA_LAB_REENTER = altSettings.getProperty("SellReenterLabyrinthTicket", true);
    ALT_PET_HEAL_BATTLE_ONLY = altSettings.getProperty("PetsHealOnlyInBattle", true);
    CHAR_TITLE = altSettings.getProperty("CharTitle", false);
    ADD_CHAR_TITLE = altSettings.getProperty("CharAddTitle", "");
    ALT_ALLOW_SELL_COMMON = altSettings.getProperty("AllowSellCommon", true);
    ALT_ALLOW_SHADOW_WEAPONS = altSettings.getProperty("AllowShadowWeapons", true);
    ALT_DISABLED_MULTISELL = altSettings.getProperty("DisabledMultisells", ArrayUtils.EMPTY_INT_ARRAY);
    ALT_SHOP_PRICE_LIMITS = altSettings.getProperty("ShopPriceLimits", ArrayUtils.EMPTY_INT_ARRAY);
    ALT_SHOP_UNALLOWED_ITEMS = altSettings.getProperty("ShopUnallowedItems", ArrayUtils.EMPTY_INT_ARRAY);
    ALT_SHOP_REFUND_SELL_DIVISOR = altSettings.getProperty("ShopRefundSellDivisor", 2L);
    ALT_ALLOWED_PET_POTIONS = altSettings.getProperty("AllowedPetPotions", new int[]{735, 1060, 1061, 1062, 1374, 1375, 1539, 1540, 6035, 6036});
    FESTIVAL_MIN_PARTY_SIZE = altSettings.getProperty("FestivalMinPartySize", 5);
    FESTIVAL_RATE_PRICE = altSettings.getProperty("FestivalRatePrice", 1.0D);
    RIFT_MIN_PARTY_SIZE = altSettings.getProperty("RiftMinPartySize", 5);
    RIFT_SPAWN_DELAY = altSettings.getProperty("RiftSpawnDelay", 10000);
    RIFT_MAX_JUMPS = altSettings.getProperty("MaxRiftJumps", 4);
    RIFT_AUTO_JUMPS_TIME = altSettings.getProperty("AutoJumpsDelay", 8);
    RIFT_AUTO_JUMPS_TIME_RAND = altSettings.getProperty("AutoJumpsDelayRandom", 120000);
    RIFT_ENTER_COST_RECRUIT = altSettings.getProperty("RecruitFC", 18);
    RIFT_ENTER_COST_SOLDIER = altSettings.getProperty("SoldierFC", 21);
    RIFT_ENTER_COST_OFFICER = altSettings.getProperty("OfficerFC", 24);
    RIFT_ENTER_COST_CAPTAIN = altSettings.getProperty("CaptainFC", 27);
    RIFT_ENTER_COST_COMMANDER = altSettings.getProperty("CommanderFC", 30);
    RIFT_ENTER_COST_HERO = altSettings.getProperty("HeroFC", 33);
    FOUR_SEPULCHER_ENABLE = altSettings.getProperty("FourSepulcherEnable", true);
    FOUR_SEPULCHER_MIN_PARTY_MEMBERS = altSettings.getProperty("FourSepulcherMinPartyMembers", 4);
    ALLOW_CLANSKILLS = altSettings.getProperty("AllowClanSkills", true);
    PARTY_LEADER_ONLY_CAN_INVITE = altSettings.getProperty("PartyLeaderOnlyCanInvite", true);
    ALLOW_TALK_WHILE_SITTING = altSettings.getProperty("AllowTalkWhileSitting", true);
    ALLOW_NOBLE_TP_TO_ALL = altSettings.getProperty("AllowNobleTPToAll", false);
    CLANHALL_BUFFTIME_MODIFIER = altSettings.getProperty("ClanHallBuffTimeModifier", 1.0D);
    SONGDANCETIME_MODIFIER = altSettings.getProperty("SongDanceTimeModifier", 1.0D);
    ALT_MUSIC_COST_GUARD_INTERVAL = altSettings.getProperty("AltMusicCostInterval", 0);
    ALT_ADDITIONAL_DANCE_SONG_MANA_CONSUME = altSettings.getProperty("AltAddMusicCost", true);
    MAXLOAD_MODIFIER = altSettings.getProperty("MaxLoadModifier", 1.0D);
    GATEKEEPER_MODIFIER = altSettings.getProperty("GkCostMultiplier", 1.0D);
    GATEKEEPER_FREE = altSettings.getProperty("GkFree", 40);
    CRUMA_GATEKEEPER_LVL = altSettings.getProperty("GkCruma", 65);
    ALT_IMPROVED_PETS_LIMITED_USE = altSettings.getProperty("ImprovedPetsLimitedUse", false);
    String[] rec_time = altSettings.getProperty("SkillDurationMod", new String[]{""}, ";");
    int var2 = rec_time.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      String skill = rec_time[var3];
      String[] parts = skill.trim().split("-");
      if (parts.length == 2) {
        SKILL_DURATION_MOD.put(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()) * 1000);
      }
    }

    ALT_CHAMPION_CHANCE1 = altSettings.getProperty("AltChampionChance1", 0.0D);
    ALT_CHAMPION_CHANCE2 = altSettings.getProperty("AltChampionChance2", 0.0D);
    ALT_CHAMPION_CAN_BE_AGGRO = altSettings.getProperty("AltChampionAggro", false);
    ALT_CHAMPION_CAN_BE_SOCIAL = altSettings.getProperty("AltChampionSocial", false);
    ALT_CHAMPION_CAN_BE_SPECIAL_MONSTERS = altSettings.getProperty("AltChampionCanBeSpecialMonster", false);
    ALT_CHAMPION_TOP_LEVEL = altSettings.getProperty("AltChampionTopLevel", 75);
    ALT_CHAMPION_MIN_LEVEL = altSettings.getProperty("AltChampionMinLevel", 1);
    ALT_CHAMPION_DROP_ITEM_IDS = altSettings.getProperty("AltChampionDropItemId", ArrayUtils.EMPTY_INT_ARRAY);
    ALT_CHAMPION_DROP_COUNTS = altSettings.getProperty("AltChampionDropItemCount", ArrayUtils.EMPTY_LONG_ARRAY);
    ALT_CHAMPION_DROP_CHANCES = altSettings.getProperty("AltChampionDropItemChance", ArrayUtils.EMPTY_FLOAT_ARRAY);
    ALT_PCBANG_POINTS_ENABLED = altSettings.getProperty("AltPcBangPointsEnabled", false);
    ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE = altSettings.getProperty("AltPcBangPointsDoubleChance", 10.0D);
    ALT_PCBANG_POINTS_BONUS = altSettings.getProperty("AltPcBangPointsBonus", 0);
    ALT_PCBANG_POINTS_DELAY = altSettings.getProperty("AltPcBangPointsDelay", 20);
    ALT_PCBANG_POINTS_MIN_LVL = altSettings.getProperty("AltPcBangPointsMinLvl", 1);
    ALT_DEBUG_ENABLED = altSettings.getProperty("AltDebugEnabled", false);
    ALT_DEBUG_PVP_ENABLED = altSettings.getProperty("AltDebugPvPEnabled", false);
    ALT_DEBUG_PVP_DUEL_ONLY = altSettings.getProperty("AltDebugPvPDuelOnly", true);
    ALT_DEBUG_PVE_ENABLED = altSettings.getProperty("AltDebugPvEEnabled", false);
    ALT_MAX_ALLY_SIZE = altSettings.getProperty("AltMaxAllySize", 3);
    ALT_PARTY_DISTRIBUTION_RANGE = altSettings.getProperty("AltPartyDistributionRange", 1500);
    ALT_PARTY_DISTRIBUTION_DIFF_LEVEL_LIMIT = altSettings.getProperty("AltPartyDistributionLevelLimit", 20);
    ALT_PARTY_BONUS = altSettings.getProperty("AltPartyBonus", new double[]{1.0D, 1.1D, 1.2D, 1.3D, 1.4D, 1.5D, 2.0D, 2.1D, 2.2D});
    ALT_REMOVE_SKILLS_ON_DELEVEL = altSettings.getProperty("AltRemoveSkillsOnDelevelThreshold", 10);
    ALT_USE_HOT_SPIRIT_DEBUFF = altSettings.getProperty("AltUseDebuffsHotSpirit", false);
    ALT_HOT_SPIRIT_CHANCE_DEBUFF = altSettings.getProperty("AltDebuffsHotSpiritChance", 5);
    ALT_TELEPORT_FROM_SEVEN_SING_MONSTER = altSettings.getProperty("SeventSingMobProtection", true);
    ALT_ENABLE_SEVEN_SING_TELEPORTER_PROTECTION = altSettings.getProperty("SeventSingTeleporterProtection", true);
    ALT_MAMONS_CHECK_SEVEN_SING_STATUS = altSettings.getProperty("MammonCheckRegistrationOnSevenSing", true);
    ALLOW_CH_DOOR_OPEN_ON_CLICK = altSettings.getProperty("AllowChDoorOpenOnClick", true);
    ALT_CH_ALL_BUFFS = altSettings.getProperty("AltChAllBuffs", false);
    ALT_CH_ALLOW_1H_BUFFS = altSettings.getProperty("AltChAllowHourBuff", false);
    ALT_CH_SIMPLE_DIALOG = altSettings.getProperty("AltChSimpleDialog", false);
    ALT_SHOW_SERVER_TIME = altSettings.getProperty("ShowServerTime", false);
    ALT_FISH_CHAMPIONSHIP_ENABLED = altSettings.getProperty("AltFishChampionshipEnabled", true);
    ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = altSettings.getProperty("AltFishChampionshipRewardItemId", 57);
    ALT_FISH_CHAMPIONSHIP_REWARD_1 = altSettings.getProperty("AltFishChampionshipReward1", 800000);
    ALT_FISH_CHAMPIONSHIP_REWARD_2 = altSettings.getProperty("AltFishChampionshipReward2", 500000);
    ALT_FISH_CHAMPIONSHIP_REWARD_3 = altSettings.getProperty("AltFishChampionshipReward3", 300000);
    ALT_FISH_CHAMPIONSHIP_REWARD_4 = altSettings.getProperty("AltFishChampionshipReward4", 200000);
    ALT_FISH_CHAMPIONSHIP_REWARD_5 = altSettings.getProperty("AltFishChampionshipReward5", 100000);
    ALT_PVP_ITEMS_TREDABLE = altSettings.getProperty("AltPvPItemsTredable", false);
    ALT_PVP_ITEMS_ATTRIBUTABLE = altSettings.getProperty("AltPvPItemsAttributable", false);
    ALT_PVP_ITEMS_AUGMENTABLE = altSettings.getProperty("AltPvPItemsAugmentable", false);
    ALT_HBCE_FAIR_PLAY = altSettings.getProperty("HBCEFairPlay", false);
    ALT_PET_INVENTORY_LIMIT = altSettings.getProperty("AltPetInventoryLimit", 12);
    rec_time = altSettings.getProperty("RecommendationFlushTime", new String[]{"13", "00"}, ":");
    REC_FLUSH_HOUR = Integer.parseInt(rec_time[0]);
    REC_FLUSH_MINUTE = Integer.parseInt(rec_time[1]);
    HUNTING_POINTS_PER_TICK = altSettings.getProperty("HuntingPointsPerTisk", 72);
    HUNTING_BONUS_TIME = altSettings.getProperty("HuntingBonusTime", 14400);
    HUNTING_TICK_DELAY = altSettings.getProperty("HuntingTickDelay", 25);
    ALT_CHECK_CERTIFICATION_ITEMS = altSettings.getProperty("AltCheckCertificationItems", true);
    ALT_PASSIVE_NOBLESS_ID = altSettings.getProperty("NoblessPassiveSkillId", 0);
    CUSTOM_HERO_STATUS_TIME = altSettings.getProperty("CustomHeroExpireTime", 1L) * 60L * 60L;
    ALT_ALLOW_DELAY_NPC_TALK = altSettings.getProperty("AltDelayOnMoveAfterTalkWithNPC", false);
    ALT_SPREADING_AFTER_TELEPORT = altSettings.getProperty("AltSpreadingTeleportPoints", true);
    ALT_TELEPORT_PROTECTION = altSettings.getProperty("AltTeleportProtection", false);
    ALT_TELEPORT_PROTECTION_TIME = (long)altSettings.getProperty("AltTeleportProtectionTime", 10);
    ALT_INITIAL_QUESTS = altSettings.getProperty("AltInitialQuests", new int[]{255});
    ALT_MAX_PARTY_SIZE = altSettings.getProperty("AltMaxPartySize", 9);
  }

  public static void loadBossSettings() {
    ExProperties bossSettings = load("config/bosses.properties");
    FWA_LIMITUNTILSLEEPANTHARAS = bossSettings.getProperty("AntharasSleepTime", 20L) * 60000L;
    FWA_FIXTIMEINTERVALOFANTHARAS = bossSettings.getProperty("AntharasRespawnTimeInterval", 264L) * 3600000L;
    FWA_RANDOMINTERVALOFANTHARAS = bossSettings.getProperty("AntharasRandomSpawnAddTime", 0L) * 3600000L;
    ANTHARAS_CLEAR_ZONE_IF_ALL_DIE = bossSettings.getProperty("AntharasClearZoneifAllDie", 1L) * 60000L;
    FWA_FIXTIMEPATTERNOFANTHARAS = bossSettings.getProperty("AntharasRespawnTimePattern", "");
    FWA_APPTIMEOFANTHARAS = bossSettings.getProperty("AntharasSpawnIntervalInZone", 10L) * 60000L;
    FWA_LIMITMAXUNTILSLEEPANTHARAS = bossSettings.getProperty("AntharasMaxTime", -1L) * 60000L;
    FWV_LIMITUNTILSLEEPVALAKAS = bossSettings.getProperty("ValakasSleepTime", 20L) * 60000L;
    FWV_FIXINTERVALOFVALAKAS = bossSettings.getProperty("ValakasRespawnTimeInterval", 264L) * 3600000L;
    FWV_RANDOMINTERVALOFVALAKAS = bossSettings.getProperty("ValakasRandomSpawnAddTime", 0L) * 3600000L;
    FWA_FIXTIMEPATTERNOFVALAKAS = bossSettings.getProperty("ValakasRespawnTimePattern", "");
    VALAKAS_CLEAR_ZONE_IF_ALL_DIE = bossSettings.getProperty("ValakasClearZoneifAllDie", 1L) * 60000L;
    FWA_LIMITMAXUNTILSLEEPVALAKAS = bossSettings.getProperty("ValakasMaxTime", -1L) * 60000L;
    FWF_FIXINTERVALOFFRINTEZZA = bossSettings.getProperty("FrintezzaRespawnTimeInterval", 48L) * 3600000L;
    FWF_RANDOMINTERVALOFFRINTEZZA = bossSettings.getProperty("FrintezzaRandomSpawnAddTime", 0L) * 3600000L;
    FWA_FIXTIMEPATTERNOFFRINTEZZA = bossSettings.getProperty("FrintezzaRespawnTimePattern", "");
    FRINTEZZA_TOMB_TIMEOUT = bossSettings.getProperty("FrintezzaTombPassTime", 35);
    FWV_APPTIMEOFVALAKAS = bossSettings.getProperty("ValakasSpawnIntervalInZone", 10L) * 60000L;
    MINIONS_RESPAWN_INTERVAL = bossSettings.getProperty("AllMinionsRespawnInterval", 6) * '\uea60';
    RAID_TELE_TO_HOME_FROM_PVP_ZONES = bossSettings.getProperty("ReturnToHomeBossesFromPvPZones", false);
    RAID_TELE_TO_HOME_FROM_TOWN_ZONES = bossSettings.getProperty("ReturnToHomeBossesFromTownZones", true);
    FWB_LIMITUNTILSLEEPBAIUM = bossSettings.getProperty("BaiumSleepTime", 30L) * 60000L;
    FWB_FIXINTERVALOFBAIUM = bossSettings.getProperty("BaiumSpawnTimeInterval", 120L) * 3600000L;
    FWA_FIXTIMEPATTERNOFBAIUM = bossSettings.getProperty("BaiumRespawnTimePattern", "");
    BAIUM_CLEAR_ZONE_IF_ALL_DIE = bossSettings.getProperty("BaiumClearZoneifAllDie", 1L) * 60000L;
    FWB_RANDOMINTERVALOFBAIUM = bossSettings.getProperty("BaiumRandomSpawnAddTime", 8L) * 3600000L;
    FWA_LIMITMAXUNTILSLEEPBAIUM = bossSettings.getProperty("BaiumMaxTime", -1L) * 60000L;
    FWS_ACTIVITYTIMEOFMOBS = bossSettings.getProperty("SailrenActivityMobsTime", 120L) * 60000L;
    SAILREN_CLEAR_ZONE_IF_ALL_DIE = bossSettings.getProperty("SailrenClearZoneifAllDie", 1L) * 60000L;
    FWA_FIXTIMEPATTERNOFSAILREN = bossSettings.getProperty("SailrenRespawnTimePattern", "");
    FWS_FIXINTERVALOFSAILRENSPAWN = bossSettings.getProperty("RespawnSailrenInterval", 24L) * 3600000L;
    FWS_RANDOMINTERVALOFSAILRENSPAWN = bossSettings.getProperty("RandomRespawnSailrenInterval", 24L) * 60L * 60000L;
    FWS_INTERVALOFNEXTMONSTER = bossSettings.getProperty("SailrenMobsRespawnInterval", 1L) * 60000L;
    ENABLE_ON_TIME_DOOR_OPEN = bossSettings.getProperty("EnableOnTimeDoorOpen", false);
    ON_TIME_OPEN_DOOR_ID = bossSettings.getProperty("EnableOnTimeDoorId", 21240006);
    ON_TIME_OPEN_PATTERN = bossSettings.getProperty("DoorInTimePattern", "* 22 * * *");
    ZAKEN_USE_TELEPORT = bossSettings.getProperty("ZakenUseTeleport", true);
  }

  private static Config.RateBonusInfo[] loadRateBonusInfo() {
    ArrayList result = new ArrayList();

    try {
      SAXReader reader = new SAXReader(true);
      Document document = reader.read(new File("config/services_rate_bonus.xml"));
      Element root = document.getRootElement();
      Iterator rateBonusIt = root.elementIterator();

      while(true) {
        Element rateBonusNode;
        do {
          if (!rateBonusIt.hasNext()) {
            return (Config.RateBonusInfo[])result.toArray(new Config.RateBonusInfo[result.size()]);
          }

          rateBonusNode = (Element)rateBonusIt.next();
        } while(!"rate_bonus".equalsIgnoreCase(rateBonusNode.getName()));

        int id = Integer.parseInt(rateBonusNode.attributeValue("id"));
        int consumeItemId = Integer.parseInt(rateBonusNode.attributeValue("consume_item_id"));
        long consumeItemAmount = Long.parseLong(rateBonusNode.attributeValue("consume_item_amount"));
        Config.RateBonusInfo rateBonusInfo = new Config.RateBonusInfo(id, consumeItemId, consumeItemAmount);
        Iterator rateIt = rateBonusNode.elementIterator();

        while(rateIt.hasNext()) {
          Element rateNode = (Element)rateIt.next();
          if ("exp".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.rateXp = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("sp".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.rateSp = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("quest_reward".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.questRewardRate = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("quest_drop".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.questDropRate = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("drop_adena".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.dropAdena = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("drop_items".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.dropItems = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("drop_spoil".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.dropSpoil = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("drop_raid_item".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.dropRaidItems = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("enchant_item_mul".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.enchantItemMul = Float.parseFloat(rateNode.attributeValue("value"));
          } else if ("bonus_days".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.bonusTimeSeconds = (long)(Integer.parseInt(rateNode.attributeValue("value")) * 24 * 60 * 60);
          } else if ("reward".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.rewardItem.add(Pair.of(Integer.parseInt(rateNode.attributeValue("item_id")), Long.parseLong(rateNode.attributeValue("item_count"))));
          } else if ("name_color".equalsIgnoreCase(rateNode.getName())) {
            rateBonusInfo.nameColor = Integer.decode("0x" + rateNode.attributeValue("value"));
          }
        }

        result.add(rateBonusInfo);
      }
    } catch (Exception var13) {
      _log.error("", var13);
      return (Config.RateBonusInfo[])result.toArray(new Config.RateBonusInfo[result.size()]);
    }
  }

  public static void loadServicesSettings() {
    ExProperties servicesSettings = load("config/services.properties");
    TEST_SERVER_HELPER_ENABLED = servicesSettings.getProperty("TestServerHelperEnabled", false);
    CLASS_MASTERS_CLASSES = servicesSettings.getProperty("ClassMasterClasses", "");
    COMMAND_CLASS_MASTER_ENABLED = servicesSettings.getProperty("ChatClassMasterEnabled", false);
    COMMAND_CLASS_MASTER_CLASSES = servicesSettings.getProperty("ChatClassMasterId", "");
    COMMAND_CLASS_POPUP_LIMIT = servicesSettings.getProperty("ChatClassMasterPopUpOnExp", 0);
    COMMAND_CLASS_MASTER_VOICE_COMMANDS = servicesSettings.getProperty("ChatClassMasterVoiceCommands", new String[]{"prof", "class"});
    SERVICES_CHANGE_NICK_ENABLED = servicesSettings.getProperty("NickChangeEnabled", false);
    SERVICES_CHANGE_NICK_PRICE = servicesSettings.getProperty("NickChangePrice", 100);
    SERVICES_CHANGE_NICK_ITEM = servicesSettings.getProperty("NickChangeItem", 4037);
    CUSTOM_CNAME_TEMPLATE = servicesSettings.getProperty("NickNameCustomTemplate", "[A-Za-z0-9А-Яа-я]{2,16}");
    QUEST_SELL_ENABLE = servicesSettings.getProperty("QuestSellEnabled", false);
    QUEST_SELL_QUEST_PRICES = servicesSettings.getProperty("QuestSellQuestPrices", "");
    APPEARANCE_APPLY_ITEM_ID = servicesSettings.getProperty("ChangeAppearanceItemId", 3435);
    APPEARANCE_SUPPORT_ITEM_ID = servicesSettings.getProperty("ChangeAppearanceCostItemId", 57);
    APPEARANCE_SUPPORT_ITEM_CNT = (long)servicesSettings.getProperty("ChangeAppearanceCostCount", 2000000);
    APPEARANCE_CANCEL_ITEM_ID = servicesSettings.getProperty("CancelAppearanceCostItemId", 3434);
    APPEARANCE_CANCEL_PRICE = (long)servicesSettings.getProperty("CancelAppearanceAdenaPrice", 1000);
    SERVICES_CHANGE_CLAN_NAME_ENABLED = servicesSettings.getProperty("ClanNameChangeEnabled", false);
    SERVICES_CHANGE_CLAN_NAME_PRICE = servicesSettings.getProperty("ClanNameChangePrice", 100);
    SERVICES_CHANGE_CLAN_NAME_ITEM = servicesSettings.getProperty("ClanNameChangeItem", 4037);
    SERVICES_CHANGE_PET_NAME_ENABLED = servicesSettings.getProperty("PetNameChangeEnabled", false);
    SERVICES_CHANGE_PET_NAME_PRICE = servicesSettings.getProperty("PetNameChangePrice", 100);
    SERVICES_CHANGE_PET_NAME_ITEM = servicesSettings.getProperty("PetNameChangeItem", 4037);
    SERVICES_EXCHANGE_BABY_PET_ENABLED = servicesSettings.getProperty("BabyPetExchangeEnabled", false);
    SERVICES_EXCHANGE_BABY_PET_PRICE = servicesSettings.getProperty("BabyPetExchangePrice", 100);
    SERVICES_EXCHANGE_BABY_PET_ITEM = servicesSettings.getProperty("BabyPetExchangeItem", 4037);
    SERVICES_CHANGE_SEX_ENABLED = servicesSettings.getProperty("SexChangeEnabled", false);
    SERVICES_CHANGE_SEX_PRICE = servicesSettings.getProperty("SexChangePrice", 100);
    SERVICES_CHANGE_SEX_ITEM = servicesSettings.getProperty("SexChangeItem", 4037);
    SERVICES_CHANGE_BASE_ENABLED = servicesSettings.getProperty("BaseChangeEnabled", false);
    SERVICES_CHANGE_BASE_PRICE = servicesSettings.getProperty("BaseChangePrice", 100);
    SERVICES_CHANGE_BASE_ITEM = servicesSettings.getProperty("BaseChangeItem", 4037);
    SERVICES_CHANGE_BASE_LIST_UNCOMPATABLE = servicesSettings.getProperty("BaseChangeForAnySubFromList", false);
    SERVICES_SEPARATE_SUB_ENABLED = servicesSettings.getProperty("SeparateSubEnabled", false);
    SERVICES_SEPARATE_SUB_PRICE = servicesSettings.getProperty("SeparateSubPrice", 100);
    SERVICES_SEPARATE_SUB_ITEM = servicesSettings.getProperty("SeparateSubItem", 4037);
    SERVICES_SEPARATE_SUB_MIN_LEVEL = servicesSettings.getProperty("SeparateSubMinLevel", 1);
    SERVICES_CHANGE_NICK_COLOR_ENABLED = servicesSettings.getProperty("NickColorChangeEnabled", false);
    SERVICES_CHANGE_NICK_COLOR_PRICE = servicesSettings.getProperty("NickColorChangePrice", 100);
    SERVICES_CHANGE_NICK_COLOR_ITEM = servicesSettings.getProperty("NickColorChangeItem", 4037);
    SERVICES_CHANGE_NICK_COLOR_LIST = servicesSettings.getProperty("NickColorChangeList", new String[]{"00FF00"});
    SERVICES_CHANGE_TITLE_COLOR_ENABLED = servicesSettings.getProperty("TitleColorChangeEnabled", false);
    SERVICES_CHANGE_TITLE_COLOR_PRICE = servicesSettings.getProperty("TitleColorChangePrice", 100);
    SERVICES_CHANGE_TITLE_COLOR_ITEM = servicesSettings.getProperty("TitleColorChangeItem", 4037);
    SERVICES_CHANGE_TITLE_COLOR_LIST = servicesSettings.getProperty("TitleColorChangeList", new String[]{"00FF00"});
    SERVICE_CAPTCHA_BOT_CHECK = servicesSettings.getProperty("CaptchaBotCheckService", false);
    CAPTCHA_MONSTER_KILLS_THRESHOLD = servicesSettings.getProperty("CaptchaMonsterCheckThreshold", 100);
    CAPTCHA_PENALTY_SKILL_ID = servicesSettings.getProperty("CaptchaPenaltySkillId", 5098);
    CAPTCHA_PENALTY_SKILL_LEVEL = servicesSettings.getProperty("CaptchaPenaltySkillLevel", 1);
    SERVICES_RATE_ENABLED = servicesSettings.getProperty("RateBonusEnabled", false);
    SERVICES_RATE_COMMAND_ENABLED = servicesSettings.getProperty("RateBonusVoiceCommandEnabled", false);
    SERVICES_RATE_BONUS_INFO = SERVICES_RATE_ENABLED ? loadRateBonusInfo() : new Config.RateBonusInfo[0];
    SERVICES_NOBLESS_SELL_ENABLED = servicesSettings.getProperty("NoblessSellEnabled", false);
    SERVICES_NOBLESS_SELL_WITHOUT_SUBCLASS = servicesSettings.getProperty("NoblessSellWithoutSubClass", true);
    SERVICES_NOBLESS_SELL_PRICE = servicesSettings.getProperty("NoblessSellPrice", 1000);
    SERVICES_NOBLESS_SELL_ITEM = servicesSettings.getProperty("NoblessSellItem", 4037);
    NOBLESS_LEVEL_FOR_SELL = servicesSettings.getProperty("NoblessLevelForSell", 76);
    SERVICES_CLAN_MAX_SELL_LEVEL = servicesSettings.getProperty("ClanMaxLevelForSell", 8);
    SERVICES_CLANLEVEL_SELL_ENABLED = servicesSettings.getProperty("ClanLevelSellEnabled", false);
    SERVICES_CLANLEVEL_SELL_ITEM = servicesSettings.getProperty("ClanLevelSellItem", new int[]{4037, 4037, 4037, 4037, 4037, 4037, 4037});
    SERVICES_CLANLEVEL_SELL_PRICE = servicesSettings.getProperty("ClanLevelSellPrice", new long[]{100L, 200L, 300L, 400L, 500L, 600L, 700L});
    SERVICES_CLAN_REPUTATION_ENABLE = servicesSettings.getProperty("ClanReputationSellEnable", false);
    SERVICES_CLAN_REPUTATION_ITEM_ID = servicesSettings.getProperty("ClanReputationItemId", 4037);
    SERVICES_CLAN_REPUTATION_ITEM_COUNT = servicesSettings.getProperty("ClanReputationItemCount", 1000);
    SERVICES_CLAN_REPUTATION_AMOUNT = servicesSettings.getProperty("ClanReputationAmountAdd", 10);
    ITEM_CLAN_REPUTATION_ENABLE = servicesSettings.getProperty("ClanReputationItemEnable", false);
    ITEM_CLAN_REPUTATION_ID = servicesSettings.getProperty("ClanReputationItemHandlerId", new int[]{9299});
    ITEM_CLAN_REPUTATION_AMOUNT = servicesSettings.getProperty("ClanReputationItemAmount", new int[]{100});
    SERVICES_CLANHELPER_ENABLED = servicesSettings.getProperty("ClanHelperEnable", false);
    SERVICES_CLANHELPER_CONFIG = servicesSettings.getProperty("ClanHelperRewardParameters", "1=2:2;2=2:3,[-200-10000,57-100500];3=2:4,[-200-20000]");
    SERVICES_CLANHELPER_ADD_FULL_CLAN_SKILLS = servicesSettings.getProperty("ClanHelperAddFullSkills", false);
    SERVICES_CLAN_BUFF_ENABLED = servicesSettings.getProperty("ClanBuffServiceEnable", false);
    SERVICES_CLAN_BUFF_SKILL_ID = servicesSettings.getProperty("ClanBuffServiceBuffId", 9200);
    SERVICES_CLAN_BUFF_LEVELS_MIN_ONLINE = servicesSettings.getProperty("ClanBuffServiceOnlineLevelUp", "10-1;20-2;30-3");
    SERVICES_CLAN_BUFF_TASK_DELAY = (long)servicesSettings.getProperty("ClanBuffServiceTaskDelay", 5);
    SERVICES_CLANSKILL_SELL_ENABLED = servicesSettings.getProperty("ClanSkillsSellEnable", false);
    SERVICES_CLAN_SKILL_SELL_ITEM = servicesSettings.getProperty("ClanSkillsSellItemId", 4037);
    SERVICES_CLAN_SKILL_SELL_PRICE = servicesSettings.getProperty("ClanSkillsSellItemCount", 1000);
    SERVICES_CLANSKIL_SELL_MIN_LEVEL = servicesSettings.getProperty("ClanSkillMinLevel", 8);
    SERVICES_DELEVEL_SELL_ENABLED = servicesSettings.getProperty("DelevelSellEnabled", false);
    SERVICES_DELEVEL_SELL_PRICE = servicesSettings.getProperty("DelevelSellPrice", 1000);
    SERVICES_DELEVEL_SELL_ITEM = servicesSettings.getProperty("DelevelSellItem", 4037);
    SERVICES_DELEVEL_REDUCTION = servicesSettings.getProperty("DelevelReduction", 2);
    SERVICE_RESET_LEVEL_ENABLED = servicesSettings.getProperty("ResetLevelService", false);
    SERVICE_RESET_LEVEL_NEED = servicesSettings.getProperty("ResetLevelServiceLevelNeed", 80);
    SERVICE_RESET_LEVEL_REMOVE = servicesSettings.getProperty("ResetLevelServiceLevelRemove", 79);
    SERVICE_RESET_LEVEL_ITEM_ID = servicesSettings.getProperty("ResetLevelServiceRewardId", 57);
    SERVICE_RESET_LEVEL_ITEM_COUNT = servicesSettings.getProperty("ResetLevelServiceRewardCount", 100);
    SERVICE_RESET_LEVEL_LIMIT = servicesSettings.getProperty("ResetLevelServiceCountLimit", -1);
    SERVICES_LEVEL_UP_SELL_ENABLED = servicesSettings.getProperty("LevelUpSellEnabled", false);
    SERVICES_LEVEL_UP_SELL_PRICE = servicesSettings.getProperty("LevelUpSellPrice", 1000);
    SERVICES_LEVEL_UP_SELL_ITEM = servicesSettings.getProperty("LevelUpSellItem", 4037);
    SERVICES_LEVEL_UP_COUNT = servicesSettings.getProperty("LevelUpForPurchasing", 1);
    SERVICES_HERO_SELL_ENABLED = servicesSettings.getProperty("HeroSellEnabled", false);
    SERVICES_HERO_SELLER_ITEM_ID = servicesSettings.getProperty("HeroSellItemId", 4037);
    SERVICES_HERO_SELLER_ITEM_COUNT = servicesSettings.getProperty("HeroSellItemCount", 1000L);
    SERVICE_HERO_STATUS_DURATION = servicesSettings.getProperty("HeroSellDays", 1L) * 24L * 60L * 60L;
    SERVICES_OLY_POINTS_RESET = servicesSettings.getProperty("ServiceOlyPointsReset", false);
    SERVICES_OLY_POINTS_RESET_ITEM_ID = servicesSettings.getProperty("ServiceOlyPointsResetItemId", 4037);
    SERVICES_OLY_POINTS_RESET_ITEM_AMOUNT = servicesSettings.getProperty("ServiceOlyPointsResetItemAmount", 1000);
    SERVICES_OLY_POINTS_THRESHOLD = servicesSettings.getProperty("ServiceOlyPointsResetThreshold", 18);
    SERVICES_OLY_POINTS_REWARD = servicesSettings.getProperty("ServiceOlyPointsResetAmount", 18);
    SERVICES_PK_CLEAN_ENABLED = servicesSettings.getProperty("PKCleanEnabled", false);
    SERVICES_PK_CLEAN_SELL_ITEM = servicesSettings.getProperty("PKCleanItemId", 4037);
    SERVICES_PK_CLEAN_SELL_PRICE = (long)servicesSettings.getProperty("PKCleanPrice", 1000);
    SERVICES_PK_ANNOUNCE = servicesSettings.getProperty("AnnouncePK", false);
    SERVICES_ALLOW_WYVERN_RIDE = servicesSettings.getProperty("AllowRideWyvernService", false);
    SERVICES_WYVERN_ITEM_ID = servicesSettings.getProperty("RideWyvernServiceItemId", 4037);
    SERVICES_KARMA_CLEAN_ENABLED = servicesSettings.getProperty("KarmaCleanServiceEnabled", false);
    SERVICES_KARMA_CLEAN_SELL_ITEM = servicesSettings.getProperty("KarmaCleanItemId", 4037);
    SERVICES_KARMA_CLEAN_SELL_PRICE = (long)servicesSettings.getProperty("KarmaCleanPrice", 1000);
    SERVICES_EXPAND_INVENTORY_ENABLED = servicesSettings.getProperty("ExpandInventoryEnabled", false);
    SERVICES_EXPAND_INVENTORY_PRICE = servicesSettings.getProperty("ExpandInventoryPrice", 1000);
    SERVICES_EXPAND_INVENTORY_ITEM = servicesSettings.getProperty("ExpandInventoryItem", 4037);
    SERVICES_EXPAND_INVENTORY_MAX = servicesSettings.getProperty("ExpandInventoryMax", 250);
    SERVICES_EXPAND_INVENTORY_SLOT_AMOUNT = servicesSettings.getProperty("ExpandInventorySlotAmount", 1);
    SERVICES_EXPAND_WAREHOUSE_ENABLED = servicesSettings.getProperty("ExpandWarehouseEnabled", false);
    SERVICES_EXPAND_WAREHOUSE_PRICE = servicesSettings.getProperty("ExpandWarehousePrice", 1000);
    SERVICES_EXPAND_WAREHOUSE_ITEM = servicesSettings.getProperty("ExpandWarehouseItem", 4037);
    SERVICES_EXPAND_WAREHOUSE_MAX = servicesSettings.getProperty("ExpandWarehouseMax", 400);
    SERVICES_EXPAND_WAREHOUSE_SLOT_AMOUNT = servicesSettings.getProperty("ExpandWarehouseSlotAmount", 1);
    SERVICES_EXPAND_CWH_ENABLED = servicesSettings.getProperty("ExpandCWHEnabled", false);
    SERVICES_EXPAND_CWH_PRICE = servicesSettings.getProperty("ExpandCWHPrice", 1000);
    SERVICES_EXPAND_CWH_ITEM = servicesSettings.getProperty("ExpandCWHItem", 4037);
    SERVICES_EXPAND_CWH_SLOT_AMOUNT = servicesSettings.getProperty("ExpandCWHSlotAmount", 1);
    SERVICES_AUTO_HEAL_ACTIVE = servicesSettings.getProperty("AutoHealActive", false);
    SERVICES_SELLPETS = servicesSettings.getProperty("SellPets", "");
    SERVICES_OFFLINE_TRADE_ALLOW = servicesSettings.getProperty("AllowOfflineTrade", false);
    ALLOW_TRADE_ON_THE_MOVE = servicesSettings.getProperty("AllowTradeOnTheMove", true);
    SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE = servicesSettings.getProperty("AllowOfflineTradeOnlyOffshore", true);
    SERVICES_OFFLINE_TRADE_MIN_LEVEL = servicesSettings.getProperty("OfflineMinLevel", 0);
    SERVICES_OFFLINE_TRADE_NAME_COLOR = Integer.decode("0x" + servicesSettings.getProperty("OfflineTradeNameColor", "B0FFFF"));
    SERVICES_OFFLINE_TRADE_NAME_COLOR_CHANGE = servicesSettings.getProperty("OfflineTradeNameColorChange", false);
    SERVICES_OFFLINE_TRADE_ABNORMAL = AbnormalEffect.getByName(servicesSettings.getProperty("OfflineTradeAbnormalEffectName", "null"));
    SERVICES_OFFLINE_TRADE_PRICE_ITEM = servicesSettings.getProperty("OfflineTradePriceItem", 0);
    SERVICES_OFFLINE_TRADE_PRICE = servicesSettings.getProperty("OfflineTradePrice", 0);
    SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK = (long)servicesSettings.getProperty("OfflineTradeDaysToKick", 14) * 86400L;
    SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART = servicesSettings.getProperty("OfflineRestoreAfterRestart", true);
    SERVICES_NO_TRADE_ONLY_OFFLINE = servicesSettings.getProperty("NoTradeOnlyOffline", false);
    SERVICES_TRADE_TAX = servicesSettings.getProperty("TradeTax", 0.0D);
    SERVICES_OFFSHORE_TRADE_TAX = servicesSettings.getProperty("OffshoreTradeTax", 0.0D);
    SERVICES_TRADE_TAX_ONLY_OFFLINE = servicesSettings.getProperty("TradeTaxOnlyOffline", false);
    SERVICES_OFFSHORE_NO_CASTLE_TAX = servicesSettings.getProperty("NoCastleTaxInOffshore", false);
    SERVICES_TRADE_ONLY_FAR = servicesSettings.getProperty("TradeOnlyFar", false);
    SERVICES_TRADE_MIN_LEVEL = servicesSettings.getProperty("MinLevelForTrade", 0);
    SERVICES_TRADE_RADIUS = servicesSettings.getProperty("TradeRadius", 30);
    SERVICES_GIRAN_HARBOR_ENABLED = servicesSettings.getProperty("GiranHarborZone", false);
    SERVICES_GIRAN_HARBOR_NOTAX = servicesSettings.getProperty("GiranHarborNoTax", false);
    SERVICES_GIRAN_HARBOR_PRICE = servicesSettings.getProperty("GiranHarborTelePrice", 5000);
    SERVICES_ALLOW_LOTTERY = servicesSettings.getProperty("AllowLottery", false);
    SERVICES_LOTTERY_PRIZE = servicesSettings.getProperty("LotteryPrize", 50000);
    SERVICES_ALT_LOTTERY_PRICE = servicesSettings.getProperty("AltLotteryPrice", 2000);
    SERVICES_LOTTERY_TICKET_PRICE = servicesSettings.getProperty("LotteryTicketPrice", 2000);
    SERVICES_LOTTERY_5_NUMBER_RATE = servicesSettings.getProperty("Lottery5NumberRate", 0.6D);
    SERVICES_LOTTERY_4_NUMBER_RATE = servicesSettings.getProperty("Lottery4NumberRate", 0.4D);
    SERVICES_LOTTERY_3_NUMBER_RATE = servicesSettings.getProperty("Lottery3NumberRate", 0.2D);
    SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE = servicesSettings.getProperty("Lottery2and1NumberPrize", 200);
    SERVICES_ALLOW_ROULETTE = servicesSettings.getProperty("AllowRoulette", false);
    SERVICES_ROULETTE_MIN_BET = servicesSettings.getProperty("RouletteMinBet", 1);
    SERVICES_ROULETTE_MAX_BET = servicesSettings.getProperty("RouletteMaxBet", 2147483647);
    SERVICES_ENABLE_NO_CARRIER = servicesSettings.getProperty("EnableNoCarrier", false);
    SERVICES_NO_CARRIER_MIN_TIME = servicesSettings.getProperty("NoCarrierMinTime", 0);
    SERVICES_NO_CARRIER_MAX_TIME = servicesSettings.getProperty("NoCarrierMaxTime", 90);
    SERVICES_NO_CARRIER_DEFAULT_TIME = servicesSettings.getProperty("NoCarrierDefaultTime", 60);
    DISCONNECTED_PLAYER_TITLE = servicesSettings.getProperty("NoCarrierCharTitle", "DISCONNECTED");
    DISCONNECTED_PLAYER_TITLE_COLOR = Integer.decode("0x" + servicesSettings.getProperty("NoCarrierCharTitleColor", "FF0000"));
    SERVICES_HPACP_ENABLE = servicesSettings.getProperty("ACPServiceEnableHP", false);
    SERVICES_HPACP_WORK_IN_PEACE_ZONE = servicesSettings.getProperty("ACPServiceWokAtPeaceZone", false);
    SERVICES_HPACP_MIN_PERCENT = Math.min(Math.max(0, servicesSettings.getProperty("ACPServiceHPMinPercent", 10)), 100);
    SERVICES_HPACP_MAX_PERCENT = Math.min(Math.max(0, servicesSettings.getProperty("ACPServiceHPMaxPercent", 90)), 100);
    SERVICES_HPACP_DEF_PERCENT = Math.min(Math.max(SERVICES_HPACP_MIN_PERCENT, servicesSettings.getProperty("ACPServiceHPDefaultPercent", 50)), SERVICES_HPACP_MAX_PERCENT);
    SERVICES_HPACP_POTIONS_ITEM_IDS = servicesSettings.getProperty("ACPServiceHPItemIds", new int[]{1539, 1540, 8627});
    SERVICES_ONLINE_COMMAND_ENABLE = servicesSettings.getProperty("OnlineCommandForAll", false);
    SERVICE_COMMAND_MULTIPLIER = servicesSettings.getProperty("OnlineCommandForAllMultiplier", 1.0D);
    SERVICES_BANKING_ENABLED = servicesSettings.getProperty("BankingServiceEnabled", false);
    SERVICES_DEPOSIT_ITEM_ID_NEEDED = servicesSettings.getProperty("DepositItemIdRemove", 57);
    SERVICES_DEPOSIT_ITEM_COUNT_NEEDED = servicesSettings.getProperty("DepositItemCountRemove", 1000000);
    SERVICES_DEPOSIT_ITEM_ID_GIVED = servicesSettings.getProperty("DepositItemIdGive", 6673);
    SERVICES_DEPOSIT_ITEM_COUNT_GIVED = servicesSettings.getProperty("DepositCountGive", 1);
    SERVICES_WITHDRAW_ITEM_ID_NEEDED = servicesSettings.getProperty("WithdrawItemIdRemove", 6673);
    SERVICES_WITHDRAW_ITEM_COUNT_NEEDED = servicesSettings.getProperty("WithdrawItemCountRemove", 1);
    SERVICES_WITHDRAW_ITEM_ID_GIVED = servicesSettings.getProperty("WithdrawItemIdGive", 57);
    SERVICES_WITHDRAW_ITEM_COUNT_GIVED = servicesSettings.getProperty("WithdrawCountGive", 1000000);
    SERVICES_CLAN_SUMMON_COMMAND_ENABLE = servicesSettings.getProperty("ClanLeaderSummonCommand", false);
    SERVICES_CLAN_SUMMON_COMMAND_SELL_ITEM = servicesSettings.getProperty("ClanLeaderSummonItemId", 4037);
    SERVICES_CLAN_SUMMON_COMMAND_SELL_PRICE = servicesSettings.getProperty("ClanLeaderSummonItemCount", 1);
    SERVICES_CLAN_SUMMON_COMMAND_SUMMON_CRYSTAL_COUNT = servicesSettings.getProperty("ClanSummonCrystalCount", 1);
    REUSE_DELAY_FOR_CLAN_SUMMON = (long)(servicesSettings.getProperty("ClanSummonReuseDelay", 30) * 60);
    SERVICES_WHOIAM_COMMAND_ENABLE = servicesSettings.getProperty("WhoiameCommandForAll", true);
    SERVICES_CPACP_ENABLE = servicesSettings.getProperty("ACPServiceEnableCP", false);
    SERVICES_CPACP_MIN_PERCENT = Math.min(Math.max(0, servicesSettings.getProperty("ACPServiceCPMinPercent", 10)), 100);
    SERVICES_CPACP_MAX_PERCENT = Math.min(Math.max(0, servicesSettings.getProperty("ACPServiceCPMaxPercent", 90)), 100);
    SERVICES_CPACP_DEF_PERCENT = Math.min(Math.max(SERVICES_CPACP_MIN_PERCENT, servicesSettings.getProperty("ACPServiceCPDefaultPercent", 50)), SERVICES_CPACP_MAX_PERCENT);
    SERVICES_CPACP_POTIONS_ITEM_IDS = servicesSettings.getProperty("ACPServiceCPItemIds", new int[]{5592, 5591});
    SERVICES_MPACP_ENABLE = servicesSettings.getProperty("ACPServiceEnableMP", false);
    SERVICES_MPACP_MIN_PERCENT = Math.min(Math.max(0, servicesSettings.getProperty("ACPServiceMPMinPercent", 10)), 100);
    SERVICES_MPACP_MAX_PERCENT = Math.min(Math.max(0, servicesSettings.getProperty("ACPServiceMPMaxPercent", 90)), 100);
    SERVICES_MPACP_DEF_PERCENT = Math.min(Math.max(SERVICES_MPACP_MIN_PERCENT, servicesSettings.getProperty("ACPServiceMPDefaultPercent", 50)), SERVICES_MPACP_MAX_PERCENT);
    SERVICES_MPACP_POTIONS_ITEM_IDS = servicesSettings.getProperty("ACPServiceMPItemIds", new int[]{728});
    SERVICES_BOSS_STATUS_ENABLE = servicesSettings.getProperty("BossStatusServiceEnable", false);
    SERVICES_BOSS_STATUS_ADDITIONAL_IDS = servicesSettings.getProperty("BossStatusAdditionalIds", new int[]{29001, 29006, 29014, 29022});
    SERVICES_BOSS_STATUS_FORMAT = servicesSettings.getProperty("BossStatusRespawnFormat", "HH:mm dd.MM.yyyy");
    SERVICES_PVP_PK_STATISTIC = servicesSettings.getProperty("TopPvPPKStatistic", false);
    SERVICE_AUTO_ANNOUNCE = servicesSettings.getProperty("ServiceAutoAnnounce", false);
    PVP_PK_STAT_CACHE_UPDATE_INTERVAL = (long)(servicesSettings.getProperty("TopPvPPKCacheUpdateTimeInterval", 1) * 60) * 1000L;
    PVP_PK_STAT_RECORD_LIMIT = servicesSettings.getProperty("TopPvPPKRecordLimit", 15);
    PAWNSHOP_ENABLED = servicesSettings.getProperty("PawnShopEnabled", true);
    List<ItemClass> pawnShopItemClasses = new ArrayList();
    String[] var2 = servicesSettings.getProperty("PawnShopItemClasses", new String[]{"WEAPON", "ARMOR", "JEWELRY"});
    int var3 = var2.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      String pwics = var2[var4];
      pawnShopItemClasses.add(ItemClass.valueOf(pwics));
    }

    PAWNSHOP_ITEMS_CLASSES = (ItemClass[])pawnShopItemClasses.toArray(new ItemClass[pawnShopItemClasses.size()]);
    PAWNSHOP_ITEMS_PER_PAGE = servicesSettings.getProperty("PawnShopItemsPerPage", 5);
    PAWNSHOP_MIN_ENCHANT_LEVEL = servicesSettings.getProperty("PawnShopMinEnchantLevel", 0);
    PAWNSHOP_ALLOW_SELL_AUGMENTED_ITEMS = servicesSettings.getProperty("PawnShopAllowTradeAugmentedItems", false);
    PAWNSHOP_MIN_GRADE = Grade.valueOf(servicesSettings.getProperty("PawnShopMinGrade", "A").toUpperCase());
    PAWNSHOP_CURRENCY_ITEM_IDS = servicesSettings.getProperty("PawnShopCurrencyItemIds", new int[]{57});
    PAWNSHOP_MIN_QUERY_LENGTH = servicesSettings.getProperty("PawnShopMinQueryLength", 3);
    PAWNSHOP_PRICE_SORT = servicesSettings.getProperty("PawnShopPriceSort", true);
    PAWNSHOP_TAX_ITEM_ID = servicesSettings.getProperty("PawnShopTaxItemId", 57);
    PAWNSHOP_TAX_ITEM_COUNT = servicesSettings.getProperty("PawnShopTaxItemCount", 2000L);
    PAWNSHOP_REFUND_ITEM_ID = servicesSettings.getProperty("PawnShopRefundItemId", 57);
    PAWNSHOP_REFUND_ITEM_COUNT = servicesSettings.getProperty("PawnShopRefundItemCount", 1000L);
    PAWNSHOP_PROHIBITED_ITEM_IDS = servicesSettings.getProperty("PawnShopProhibitedItemIds", new int[]{6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621, 6842});
    ITEM_BROKER_ITEM_SEARCH = servicesSettings.getProperty("UseItemBrokerItemSearch", false);
    ITEM_BROKER_UPDATE_TIME = (long)(servicesSettings.getProperty("BrokerItemListTimeUpdate", 2) * 60) * 1000L;
    ALLOW_EVENT_GATEKEEPER = servicesSettings.getProperty("AllowEventGatekeeper", false);
    ALLOW_GLOBAL_GK = servicesSettings.getProperty("AllowSpawnGlobalGatekeeper", false);
    ALLOW_BUFFER = servicesSettings.getProperty("AllowSpawnBuffer", false);
    ALLOW_GMSHOP = servicesSettings.getProperty("AllowSpawnGMShop", false);
    ALLOW_AUCTIONER = servicesSettings.getProperty("AllowSpawnAuctioner", false);
    ALLOW_GLOBAL_SERVICES = servicesSettings.getProperty("AllowSpawnGlobalServices", false);
    ALLOW_PVP_EVENT_MANAGER = servicesSettings.getProperty("AllowSpawnPvPEventManager", false);
    ALLOW_WEDDING = servicesSettings.getProperty("AllowWedding", false);
    WEDDING_PRICE = servicesSettings.getProperty("WeddingPrice", 500000);
    WEDDING_ITEM_ID_PRICE = servicesSettings.getProperty("WeddingPriceItemId", 57);
    WEDDING_PUNISH_INFIDELITY = servicesSettings.getProperty("WeddingPunishInfidelity", true);
    WEDDING_TELEPORT = servicesSettings.getProperty("WeddingTeleport", true);
    WEDDING_TELEPORT_PRICE = servicesSettings.getProperty("WeddingTeleportPrice", 500000);
    WEDDING_TELEPORT_INTERVAL = servicesSettings.getProperty("WeddingTeleportInterval", 120);
    WEDDING_SAMESEX = servicesSettings.getProperty("WeddingAllowSameSex", true);
    WEDDING_FORMALWEAR = servicesSettings.getProperty("WeddingFormalWear", true);
    WEDDING_DIVORCE_COSTS = servicesSettings.getProperty("WeddingDivorceCosts", 20);
    WEDDING_GIVE_SALVATION_BOW = servicesSettings.getProperty("GiveWeddingBow", true);
    WEDDING_ANNOUNCE = servicesSettings.getProperty("WeddingAnnouncement", false);
    WEDDING_USE_COLOR = servicesSettings.getProperty("WeddingUseNickColor", false);
    WEDDING_NORMAL_COLOR = Integer.decode("0x" + servicesSettings.getProperty("WeddingNormalCoupleNickColor", "BF0000"));
    WEDDING_GAY_COLOR = Integer.decode("0x" + servicesSettings.getProperty("WeddingGayCoupleNickColor", "0000BF"));
    WEDDING_LESBIAN_COLOR = Integer.decode("0x" + servicesSettings.getProperty("WeddingLesbianCoupleNickColor", "BF00BF"));
    L2TOPRU_DELAY = (long)(servicesSettings.getProperty("L2TopRuDelay", 0) * 1000 * 60);
    L2TOPRU_PREFIX = servicesSettings.getProperty("L2TopRuServerPrefix");
    L2TOPRU_WEB_VOTE_URL = servicesSettings.getProperty("L2TopRuWebURL");
    L2TOPRU_SMS_VOTE_URL = servicesSettings.getProperty("L2TopRuSMSURL");
    L2TOPRU_WEB_REWARD_ITEMID = servicesSettings.getProperty("L2TopRuWebRewardItemId", 6673);
    L2TOPRU_WEB_REWARD_ITEMCOUNT = servicesSettings.getProperty("L2TopRuWebRewardItemCount", 1);
    L2TOPRU_SMS_REWARD_ITEMID = servicesSettings.getProperty("L2TopRuSMSRewardItemId", 6673);
    L2TOPRU_SMS_REWARD_ITEMCOUNT = servicesSettings.getProperty("L2TopRuSMSRewardItemCount", 10);
    L2TOPRU_SMS_REWARD_VOTE_MULTI = servicesSettings.getProperty("L2TopRuSMSRewardMultiVote", false);
    L2TOPZONE_ENABLED = servicesSettings.getProperty("L2TopZoneServiceEnabled", false);
    L2TOPZONE_VOTE_TIME_TO_LIVE = (long)(servicesSettings.getProperty("L2TopZoneVoteTimeToLive", 12) * 3600);
    L2TOPZONE_SERVER_ID = servicesSettings.getProperty("L2TopZoneServerId", 0);
    L2TOPZONE_API_KEY = servicesSettings.getProperty("L2TopZoneApiKey", "");
    L2TOPZONE_REWARD_ITEM_ID = servicesSettings.getProperty("L2TopZoneRewardItemId", 57);
    L2TOPZONE_REWARD_ITEM_COUNT = servicesSettings.getProperty("L2TopZoneRewardItemCount", 1);
  }

  public static void loadPvPSettings() {
    ExProperties pvpSettings = load("config/pvp.properties");
    KARMA_MIN_KARMA = pvpSettings.getProperty("MinKarma", 240);
    KARMA_SP_DIVIDER = pvpSettings.getProperty("SPDivider", 7);
    KARMA_LOST_BASE = pvpSettings.getProperty("BaseKarmaLost", 0);
    SERVICES_PK_KILL_BONUS_INTERVAL = pvpSettings.getProperty("PvPPKRewardBonusInterval", 0L) * 60000L;
    SERVICES_PK_KILL_BONUS_ENABLE = pvpSettings.getProperty("PkKillEnable", false);
    SERVICES_PVP_KILL_BONUS_ENABLE = pvpSettings.getProperty("PvPKillEnable", false);
    SERVICES_PVP_KILL_BONUS_REWARD_COUNT = pvpSettings.getProperty("PvPKillRewardCount", 1);
    SERVICES_PVP_KILL_BONUS_REWARD_ITEM = pvpSettings.getProperty("PvPkillRewardItem", 57);
    SERVICES_PK_KILL_BONUS_REWARD_COUNT = pvpSettings.getProperty("PkKillRewardCount", 1);
    SERVICES_PK_KILL_BONUS_REWARD_ITEM = pvpSettings.getProperty("PkkillRewardItem", 57);
    SERVICES_PK_PVP_BONUS_TIE_IF_SAME_IP = pvpSettings.getProperty("PkPvPSameIPKiller", false);
    SERVICES_PK_PVP_BONUS_TIE_IF_SAME_HWID = pvpSettings.getProperty("PkPvPSameHWIDKiller", false);
    PVP_HERO_CHAT_SYSTEM = pvpSettings.getProperty("PvPCountHeroVoice", false);
    PVP_COUNT_TO_CHAT = pvpSettings.getProperty("PvPCountForHeroVoice", 2000);
    KARMA_DROP_GM = pvpSettings.getProperty("CanGMDropEquipment", false);
    KARMA_NEEDED_TO_DROP = pvpSettings.getProperty("KarmaNeededToDrop", true);
    DROP_ITEMS_ON_DIE = pvpSettings.getProperty("DropOnDie", false);
    DROP_ITEMS_AUGMENTED = pvpSettings.getProperty("DropAugmented", false);
    ITEM_ANTIDROP_FROM_PK = pvpSettings.getProperty("ItemAntiDropFromPK", 0);
    KARMA_DROP_ITEM_LIMIT = pvpSettings.getProperty("MaxItemsDroppable", 10);
    MIN_PK_TO_ITEMS_DROP = pvpSettings.getProperty("MinPKToDropItems", 5);
    KARMA_RANDOM_DROP_LOCATION_LIMIT = pvpSettings.getProperty("MaxDropThrowDistance", 70);
    KARMA_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfPKDropBase", 20.0D);
    KARMA_DROPCHANCE_MOD = pvpSettings.getProperty("ChanceOfPKsDropMod", 1.0D);
    NORMAL_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfNormalDropBase", 1.0D);
    DROPCHANCE_EQUIPPED_WEAPON = pvpSettings.getProperty("ChanceOfDropWeapon", 3);
    DROPCHANCE_EQUIPMENT = pvpSettings.getProperty("ChanceOfDropEquippment", 17);
    DROPCHANCE_ITEM = pvpSettings.getProperty("ChanceOfDropOther", 80);
    FUN_ZONE_PVP_COUNT = pvpSettings.getProperty("PvPCountingInFunZone", false);
    FUN_ZONE_FLAG_ON_ENTER = pvpSettings.getProperty("FlagOnEnterInFunZone", false);
    KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList();
    int[] var1 = pvpSettings.getProperty("ListOfNonDroppableItems", new int[]{57, 1147, 425, 1146, 461, 10, 2368, 7, 6, 2370, 2369, 3500, 3501, 3502, 4422, 4423, 4424, 2375, 6648, 6649, 6650, 6842, 6834, 6835, 6836, 6837, 6838, 6839, 6840, 5575, 7694, 6841, 8181});
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      int id = var1[var3];
      KARMA_LIST_NONDROPPABLE_ITEMS.add(id);
    }

    PVP_TIME = pvpSettings.getProperty("PvPFlagTime", 40000);
    PVP_FLAG_ON_UN_FLAG_TIME = pvpSettings.getProperty("PvPFlagTimeOnFlag", 20000);
    PVP_BLINKING_UNFLAG_TIME = pvpSettings.getProperty("PvPBlinkingFlagTime", 10000);
  }

  public static void loadAISettings() {
    ExProperties aiSettings = load("config/ai.properties");
    AI_TASK_MANAGER_COUNT = aiSettings.getProperty("AiTaskManagers", 1);
    AI_TASK_ATTACK_DELAY = (long)aiSettings.getProperty("AiTaskDelay", 1000);
    AI_TASK_ACTIVE_DELAY = (long)aiSettings.getProperty("AiTaskActiveDelay", 1000);
    BLOCK_ACTIVE_TASKS = aiSettings.getProperty("BlockActiveTasks", false);
    ALWAYS_TELEPORT_HOME = aiSettings.getProperty("AlwaysTeleportHome", false);
    RND_WALK = aiSettings.getProperty("RndWalk", true);
    RND_WALK_RATE = aiSettings.getProperty("RndWalkRate", 1);
    RND_ANIMATION_RATE = aiSettings.getProperty("RndAnimationRate", 2);
    AGGRO_CHECK_INTERVAL = aiSettings.getProperty("AggroCheckInterval", 250);
    NONAGGRO_TIME_ONTELEPORT = (long)aiSettings.getProperty("NonAggroTimeOnTeleport", 15000);
    NONAGGRO_TIME_ONLOGIN = (long)aiSettings.getProperty("NonAggroTimeOnLogin", 15000);
    MAX_DRIFT_RANGE = aiSettings.getProperty("MaxDriftRange", 100);
    MAX_PURSUE_RANGE = aiSettings.getProperty("MaxPursueRange", 4000);
    MAX_PURSUE_UNDERGROUND_RANGE = aiSettings.getProperty("MaxPursueUndergoundRange", 2000);
    MAX_PURSUE_RANGE_RAID = aiSettings.getProperty("MaxPursueRangeRaid", 5000);
    RESTORE_HP_MP_ON_TELEPORT_HOME = aiSettings.getProperty("RestoreHealthOnTeleportToHome", true);
  }

  public static void loadGeodataSettings() {
    ExProperties geodataSettings = load("config/geodata.properties");
    GEO_X_FIRST = geodataSettings.getProperty("GeoFirstX", 15);
    GEO_Y_FIRST = geodataSettings.getProperty("GeoFirstY", 10);
    GEO_X_LAST = geodataSettings.getProperty("GeoLastX", 26);
    GEO_Y_LAST = geodataSettings.getProperty("GeoLastY", 26);
    GEOFILES_PATTERN = geodataSettings.getProperty("GeoFilesPattern", "(\\d{2}_\\d{2})\\.l2j");
    ALLOW_GEODATA = geodataSettings.getProperty("AllowGeodata", true);
    ALLOW_FALL_FROM_WALLS = geodataSettings.getProperty("AllowFallFromWalls", false);
    ALLOW_KEYBOARD_MOVE = geodataSettings.getProperty("AllowMoveWithKeyboard", true);
    ALLOW_PAWN_PATHFIND = geodataSettings.getProperty("AllowPawnPathFind", true);
    COMPACT_GEO = geodataSettings.getProperty("CompactGeoData", false);
    CLIENT_Z_SHIFT = geodataSettings.getProperty("ClientZShift", 16);
    PATHFIND_BOOST = geodataSettings.getProperty("PathFindBoost", 2);
    PATHFIND_DIAGONAL = geodataSettings.getProperty("PathFindDiagonal", true);
    PATH_CLEAN = geodataSettings.getProperty("PathClean", true);
    PATHFIND_MAX_Z_DIFF = geodataSettings.getProperty("PathFindMaxZDiff", 32);
    MAX_Z_DIFF = geodataSettings.getProperty("MaxZDiff", 64);
    MIN_LAYER_HEIGHT = geodataSettings.getProperty("MinLayerHeight", 64);
    PATHFIND_MAX_TIME = (long)geodataSettings.getProperty("PathFindMaxTime", 10000000);
    PATHFIND_BUFFERS = geodataSettings.getProperty("PathFindBuffers", "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512");
  }

  public static void loadEventsSettings() {
    ExProperties eventSettings = load("config/events.properties");
    EVENT_CofferOfShadowsPriceRate = eventSettings.getProperty("CofferOfShadowsPriceRate", 1.0D);
    EVENT_CofferOfShadowsRewardRate = eventSettings.getProperty("CofferOfShadowsRewardRate", 1.0D);
    EVENT_LastHeroItemID = eventSettings.getProperty("LastHero_bonus_id", 57);
    EVENT_LastHeroItemCOUNT = eventSettings.getProperty("LastHero_bonus_count", 5000.0D);
    EVENT_LastHeroTime = eventSettings.getProperty("LastHero_time", 3);
    EVENT_LastHeroRate = eventSettings.getProperty("LastHero_rate", true);
    EVENT_LastHeroChanceToStart = eventSettings.getProperty("LastHero_ChanceToStart", 5);
    EVENT_LastHeroItemCOUNTFinal = eventSettings.getProperty("LastHero_bonus_count_final", 10000.0D);
    EVENT_LastHeroRateFinal = eventSettings.getProperty("LastHero_rate_final", true);
    EVENT_TvTItemID = eventSettings.getProperty("TvT_bonus_id", 57);
    EVENT_TvTItemCOUNT = eventSettings.getProperty("TvT_bonus_count", 5000.0D);
    EVENT_TvTTime = eventSettings.getProperty("TvT_time", 3);
    EVENT_TvT_rate = eventSettings.getProperty("TvT_rate", true);
    EVENT_TvTChanceToStart = eventSettings.getProperty("TvT_ChanceToStart", 5);
    EVENT_DropEvent_Items = eventSettings.getProperty("DropEvent_Items", "");
    EVENT_DropEvent_Rate = eventSettings.getProperty("DropEvent_Rated", false);
    EVENT_CtFItemID = eventSettings.getProperty("CtF_bonus_id", 57);
    EVENT_CtFItemCOUNT = eventSettings.getProperty("CtF_bonus_count", 5000.0D);
    EVENT_CtFTime = eventSettings.getProperty("CtF_time", 3);
    EVENT_CtF_rate = eventSettings.getProperty("CtF_rate", true);
    EVENT_CtFChanceToStart = eventSettings.getProperty("CtF_ChanceToStart", 5);
    EVENT_TFH_POLLEN_CHANCE = eventSettings.getProperty("TFH_POLLEN_CHANCE", 5.0D);
    EVENT_GLITTMEDAL_NORMAL_CHANCE = eventSettings.getProperty("MEDAL_CHANCE", 10.0D);
    EVENT_GLITTMEDAL_GLIT_CHANCE = eventSettings.getProperty("GLITTMEDAL_CHANCE", 0.1D);
    EVENT_L2DAY_LETTER_CHANCE = eventSettings.getProperty("L2DAY_LETTER_CHANCE", 1.0D);
    EVENT_CHANGE_OF_HEART_CHANCE = eventSettings.getProperty("EVENT_CHANGE_OF_HEART_CHANCE", 5.0D);
    EVENT_CHRISTMAS_CHANCE = eventSettings.getProperty("EVENT_CHRISTMAS_CHANCE", 1.0D);
    EVENT_APIL_FOOLS_DROP_CHANCE = eventSettings.getProperty("AprilFollsDropChance", 50.0D);
    EVENT_SAVING_SNOWMAN_LOTERY_PRICE = (long)eventSettings.getProperty("SavingSnowmanLoteryPrice", 50000);
    EVENT_SAVING_SNOWMAN_REWARDER_CHANCE = eventSettings.getProperty("SavingSnowmanRewarderChance", 2);
    EVENT_SAVING_SNOWMAN_LOTERY_CURENCY = eventSettings.getProperty("SavingSnowmanLoteryCurency", 20076);
    EVENT_FINDER_REWARD_ID = eventSettings.getProperty("EventFinderRewardId", 57);
    EVENT_FINDER_ITEM_COUNT = eventSettings.getProperty("EventFinderRewardCount", 10000);
    EVENT_TRICK_OF_TRANS_CHANCE = eventSettings.getProperty("TRICK_OF_TRANS_CHANCE", 10.0D);
    EVENT_MARCH8_DROP_CHANCE = eventSettings.getProperty("March8DropChance", 10.0D);
    EVENT_MARCH8_PRICE_RATE = eventSettings.getProperty("March8PriceRate", 1.0D);
    EVENT_StraightHands_Items = eventSettings.getProperty("StraightHandsRestrictedItems", new int[]{13390});
    EVENT_FinderHostageStartTime = eventSettings.getProperty("FinderHostageStartTime", new String[0], ",");
    GVG_REWARD_ID = eventSettings.getProperty("EventGvGItemIdRewrd", 57);
    GVG_REWARD_AMOUNT = eventSettings.getProperty("EventGvGRewardAmount", 100L);
    EVENT_GVG_START_TIME = eventSettings.getProperty("EventGvGStartTime", "22:00:00");
    EVENT_GVG_MIN_LEVEL = eventSettings.getProperty("EventGvGMinParticipantLevel", 76);
    EVENT_GVG_MAX_LEVEL = eventSettings.getProperty("EventGvGMaxParticipantLevel", 81);
    EVENT_GVG_GROUPS_LIMIT = eventSettings.getProperty("EventGvGMaxGroups", 50);
    EVENT_GVG_MIN_PARTY_SIZE = eventSettings.getProperty("EventGvGMinPartySize", 6);
    EVENT_GVG_REG_TIME = (long)(Math.max(5, eventSettings.getProperty("EventGvGRegistrationTime", 10)) * 60) * 1000L;
    EVENT_PUMPKIN_GHOST_ID = eventSettings.getProperty("HalloweenEventPumpkinGhostNPC", 40028);
    EVENT_SKOOLDIE_REWARDER = eventSettings.getProperty("HalloweenSkooldieRewarderNPC", 40029);
    EVENT_PUMPKIN_DELAY = eventSettings.getProperty("HalloweenEventPumpkinDelay", 120) * 60 * 1000;
    EVENT_PUMPKIN_GHOST_SHOW_TIME = eventSettings.getProperty("HalloweenEventPumpkinTownShowTime", 10) * 60 * 1000;
    EVENT_SKOOLDIE_TIME = eventSettings.getProperty("HalloweenEventSkooldieSpawnTime", 20) * 60 * 1000;
    EVENT_HALLOWEEN_CANDY = eventSettings.getProperty("HalloweenCandyId", 6633);
    EVENT_HALLOWEEN_CANDY_ITEM_COUNT_NEEDED = eventSettings.getProperty("HalloweenCandyAmount", 1);
    EVENT_HALLOWEEN_TOY_CHEST = eventSettings.getProperty("HalloweenRewardId", 6634);
    EVENT_HALLOWEEN_TOY_CHEST_REWARD_AMOUNT = eventSettings.getProperty("HalloweenRewardAmount", 1);
    EVENT_PUMPKIN_DROP_ITEMS = eventSettings.getProperty("HalloweenPumpkinGhosDropItemId", new int[]{6633, 6406, 6407});
    EVENT_PUMPKIN_DROP_CHANCE = eventSettings.getProperty("HalloweenPumpkinGhosDropItemChance", 1);
  }

  public static void loadOlympiadSettings() {
    ExProperties olympSettings = load("config/olympiad.properties");
    OLY_ENABLED = olympSettings.getProperty("OlympiadEnabled", true);
    OLY_SPECTATION_ALLOWED = olympSettings.getProperty("SpectationgAllowed", true);
    NPC_OLYMPIAD_GAME_ANNOUNCE = olympSettings.getProperty("OlympiadNpcAnnounceTeleportOnStdium", true);
    ANNOUNCE_OLYMPIAD_GAME_END = olympSettings.getProperty("EndOlympiadAnnounceSeason", false);
    OLY_RESTRICT_HWID = olympSettings.getProperty("OlympiadHWIDCheck", true);
    OLY_RESTRICT_IP = olympSettings.getProperty("OlympiadIPCheck", true);
    OLY_RESTRICT_CLASS_IDS = olympSettings.getProperty("OlympiadProhibitClassIds", ArrayUtils.EMPTY_INT_ARRAY);
    OLYMPIAD_COMPETITION_TIME = (long)(olympSettings.getProperty("OlympiadCompetitionTime", 5) * 60) * 1000L;
    OLYMPIAD_STADIUM_TELEPORT_DELAY = olympSettings.getProperty("OlympiadStadiumTeleportDelay", 45);
    OLY_MAX_SPECTATORS_PER_STADIUM = olympSettings.getProperty("MaxSpectatorPerStadium", 18);
    OLY_SEASON_TIME_CALC_MODE = Config.OlySeasonTimeCalcMode.valueOf(olympSettings.getProperty("SeasonTimeCalcMode", "NORMAL").toUpperCase());
    OLY_SEASON_START_TIME = olympSettings.getProperty("SeasonStartTime", "2 00:00");
    OLY_SEASON_END_TIME = olympSettings.getProperty("SeasonEndTime", "+1:1 00:00");
    OLY_COMPETITION_START_TIME = olympSettings.getProperty("CompetitionStartTime", "18:00");
    OLY_COMPETITION_END_TIME = olympSettings.getProperty("CompetitionEndTime", "+1 00:00");
    OLY_BONUS_TIME = olympSettings.getProperty("WeaklyBonusTime", "+7 18:30");
    OLY_NOMINATE_TIME = olympSettings.getProperty("NominateTime", "+1:1 12:00");
    OLY_SEASON_START_POINTS = olympSettings.getProperty("SeasonStartPoints", 10);
    OLY_MIN_HERO_COMPS = olympSettings.getProperty("MinRewardableHeroComps", 15);
    OLY_MIN_HERO_WIN = olympSettings.getProperty("MinWinHeroComps", 1);
    OLY_MIN_NOBLE_COMPS = olympSettings.getProperty("MinRewardableNobleComps", 15);
    OLY_POINTS_SETTLEMENT = olympSettings.getProperty("PointSettlement", new int[]{100, 75, 55, 40, 30});
    String olyBuffsStr = olympSettings.getProperty("OlyBuffs", "99:30-1,31-2,3050-1;100:32-1,33-2");
    OLY_BUFFS = new TIntObjectHashMap();
    StringTokenizer olyBuffTok = new StringTokenizer(olyBuffsStr, ";", false);

    while(true) {
      while(true) {
        String olyBuff;
        do {
          if (!olyBuffTok.hasMoreTokens()) {
            OLY_ITEMS_SETTLEMENT_PER_POINT = olympSettings.getProperty("ItemsSettlementPerPoint", 1000);
            OLY_HERO_POINT_BONUS = olympSettings.getProperty("HeroPointBonus", 200);
            OLY_DEFAULT_POINTS = olympSettings.getProperty("DefaultPoints", 10);
            OLY_WBONUS_POINTS = olympSettings.getProperty("WeaklyBonusPoints", 10);
            OLY_LOOSE_POINTS_MUL = olympSettings.getProperty("LoosePointsMultiplier", 0.2D);
            OLY_RESTRICTED_SKILL_IDS = olympSettings.getProperty("OlyRestrictedSkillIds", new int[0]);
            OLY_VICTORY_RITEMID = olympSettings.getProperty("VictoryRewardItemID", 13722);
            OLY_VICTORY_CFREE_RITEMCNT = olympSettings.getProperty("VictoryRewardClassFreeCount", 40);
            OLY_VICTORY_CBASE_RITEMCNT = olympSettings.getProperty("VictoryRewardClassBasedCount", 65);
            OLY_VICTORY_3TEAM_RITEMCNT = olympSettings.getProperty("VictoryRewardTeamBasedCount", 85);
            OLY_MAX_TOTAL_MATCHES = olympSettings.getProperty("MaxTotalMatches", 70);
            OLY_CF_MATCHES = olympSettings.getProperty("MaxClassFreeMatches", 60);
            OLY_CB_MATCHES = olympSettings.getProperty("MaxClassBaseMatches", 30);
            OLY_TB_MATCHES = olympSettings.getProperty("MaxTeamBaseMatches", 10);
            OLY_MIN_CF_START = olympSettings.getProperty("MinParticipantClassFree", 11);
            OLY_MIN_CB_START = olympSettings.getProperty("MinParticipantClassBase", 11);
            OLY_MIN_TB_START = olympSettings.getProperty("MinParticipantTeamBase", 6);
            OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR = olympSettings.getProperty("LimitEnchantStatLevelArmor", -1);
            OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE = olympSettings.getProperty("LimitEnchantStatLevelMageWeapon", -1);
            OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS = olympSettings.getProperty("LimitEnchantStatLevelPhysWeapon", -1);
            OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY = olympSettings.getProperty("LimitEnchantStatLevelAccessory", -1);
            OLYMPIAD_WINNER_ANNOUCE = olympSettings.getProperty("OlympiadWinnerAnnounce", false);
            OLYMPIAD_WINNER_MESSAGE = olympSettings.getProperty("OlympiadWinnerAnnounceMessage", "3:RAMPAGE_CUSTOM_MESSAGE;5:OTHER_MESSAGE");
            return;
          }

          olyBuff = olyBuffTok.nextToken();
        } while(olyBuff.isEmpty());

        int olyBuffSkillDelimIdx = olyBuff.indexOf(58);
        if (olyBuffSkillDelimIdx < 0) {
          _log.warn("Can't parse \"" + olyBuff + "\"");
        } else {
          int classId = Integer.parseInt(olyBuff.substring(0, olyBuffSkillDelimIdx));
          TIntIntHashMap buffs = (TIntIntHashMap)OLY_BUFFS.get(classId);
          if (buffs == null) {
            OLY_BUFFS.put(classId, buffs = new TIntIntHashMap());
          }

          String olyPlayerJobBuffsStr = olyBuff.substring(olyBuffSkillDelimIdx + 1);
          StringTokenizer olyPlayerJobBuffsTok = new StringTokenizer(olyPlayerJobBuffsStr, ",", false);

          while(olyPlayerJobBuffsTok.hasMoreTokens()) {
            String olyPlayerJobBuffStr = olyPlayerJobBuffsTok.nextToken();
            int buffIdLvlDelimIdx = olyPlayerJobBuffStr.indexOf("-");
            int skillId = Integer.parseInt(olyPlayerJobBuffStr.substring(0, buffIdLvlDelimIdx));
            int skillLvl = Integer.parseInt(olyPlayerJobBuffStr.substring(buffIdLvlDelimIdx + 1));
            buffs.put(skillId, skillLvl);
          }
        }
      }
    }
  }

  public static void loadQuestRateSettings() {
    ExProperties questRateSettings = load("config/quest_rates.properties");
    Map<Integer, QuestRates> questRates = new HashMap();
    Iterator var2 = questRateSettings.keySet().iterator();

    while(var2.hasNext()) {
      Object keyObj = var2.next();
      String key = keyObj.toString();
      int questId = 0;

      int paramIdx;
      for(paramIdx = 0; paramIdx < key.length(); ++paramIdx) {
        if (key.charAt(paramIdx) != '_') {
          if (!Character.isDigit(key.charAt(paramIdx))) {
            break;
          }

          questId = questId * 10 + (key.charAt(paramIdx) - 48);
        }
      }

      if (questId == 0) {
        _log.warn("Can't parse quest id for quest rate \"" + key + "\"");
      } else {
        paramIdx = key.indexOf(46);
        if (paramIdx < 0) {
          _log.warn("Can't parse quest rate param \"" + key + "\"");
        } else {
          String questRateParam = key.substring(paramIdx + 1);
          QuestRates questRate = (QuestRates)questRates.get(questId);
          if (questRate == null) {
            questRates.put(questId, questRate = new QuestRates(questId));
          }

          try {
            questRate.updateParam(questRateParam, questRateSettings.getProperty(key));
          } catch (Exception var10) {
            _log.warn("Can't process quest rate setting \"" + key + "\"", var10);
          }
        }
      }
    }

    QUEST_RATES.clear();
    QUEST_RATES.putAll(questRates);
  }

  public static void load() {
    loadServerConfig();
    loadTelnetConfig();
    loadResidenceConfig();
    loadOtherConfig();
    loadSpoilConfig();
    loadClanConfig();
    loadFormulasConfig();
    loadAltSettings();
    loadBossSettings();
    loadServicesSettings();
    loadPvPSettings();
    loadAISettings();
    loadGeodataSettings();
    loadEventsSettings();
    loadOlympiadSettings();
    loadQuestRateSettings();
    loadChatFilters();
    loadGMAccess();
  }

  private Config() {
  }

  public static void loadChatFilters() {
    ChatFilterParser.getInstance().load();
  }

  public static void loadGMAccess() {
    gmlist.clear();
    loadGMAccess(new File("config/GMAccess.xml"));
    File dir = new File("config/GMAccess.d/");
    if (dir.exists() && dir.isDirectory()) {
      File[] var1 = dir.listFiles();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
        File f = var1[var3];
        if (!f.isDirectory() && f.getName().endsWith(".xml")) {
          loadGMAccess(f);
        }
      }

    } else {
      _log.info("Dir " + dir.getAbsolutePath() + " not exists.");
    }
  }

  public static void loadGMAccess(File file) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setIgnoringComments(true);
      org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(file);

      for(Node z = doc.getFirstChild(); z != null; z = z.getNextSibling()) {
        for(Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) {
          if (n.getNodeName().equalsIgnoreCase("char")) {
            PlayerAccess pa = new PlayerAccess();

            for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              Class<?> cls = pa.getClass();
              String node = d.getNodeName();
              if (!"#text".equalsIgnoreCase(node)) {
                Field fld;
                try {
                  fld = cls.getField(node);
                } catch (NoSuchFieldException var11) {
                  _log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
                  continue;
                }

                if (fld.getType().getName().equalsIgnoreCase("boolean")) {
                  fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
                } else if (fld.getType().getName().equalsIgnoreCase("int")) {
                  fld.setInt(pa, Integer.valueOf(d.getAttributes().getNamedItem("set").getNodeValue()));
                }
              }
            }

            gmlist.put(pa.PlayerID, pa);
          }
        }
      }
    } catch (Exception var12) {
      var12.printStackTrace();
    }

  }

  public static String getField(String fieldName) {
    Field field = FieldUtils.getField(Config.class, fieldName);
    if (field == null) {
      return null;
    } else {
      try {
        return String.valueOf(field.get((Object)null));
      } catch (IllegalArgumentException var3) {
      } catch (IllegalAccessException var4) {
      }

      return null;
    }
  }

  public static boolean setField(String fieldName, String value) {
    Field field = FieldUtils.getField(Config.class, fieldName);
    if (field == null) {
      return false;
    } else {
      try {
        if (field.getType() == Boolean.TYPE) {
          field.setBoolean((Object)null, BooleanUtils.toBoolean(value));
        } else if (field.getType() == Integer.TYPE) {
          field.setInt((Object)null, NumberUtils.toInt(value));
        } else if (field.getType() == Long.TYPE) {
          field.setLong((Object)null, NumberUtils.toLong(value));
        } else if (field.getType() == Double.TYPE) {
          field.setDouble((Object)null, NumberUtils.toDouble(value));
        } else {
          if (field.getType() != String.class) {
            return false;
          }

          field.set((Object)null, value);
        }

        return true;
      } catch (IllegalArgumentException var4) {
        return false;
      } catch (IllegalAccessException var5) {
        return false;
      }
    }
  }

  public static ExProperties load(String filename) {
    return load(new File(filename));
  }

  public static ExProperties load(File file) {
    ExProperties result = new ExProperties();

    try {
      result.load(file);
    } catch (IOException var3) {
      _log.error("Error loading config : " + file.getName() + "!");
    }

    return result;
  }

  public static enum OlySeasonTimeCalcMode {
    NORMAL,
    CUSTOM;

    private OlySeasonTimeCalcMode() {
    }
  }

  public static class RateBonusInfo {
    public final int id;
    public final int consumeItemId;
    public final long consumeItemAmount;
    public float rateXp = 1.0F;
    public float rateSp = 1.0F;
    public float questRewardRate = 1.0F;
    public float questDropRate = 1.0F;
    public float dropAdena = 1.0F;
    public float dropItems = 1.0F;
    public float dropRaidItems = 1.0F;
    public float dropSpoil = 1.0F;
    public float enchantItemMul = 1.0F;
    public List<Pair<Integer, Long>> rewardItem = new ArrayList();
    public Integer nameColor = null;
    public long bonusTimeSeconds = 0L;

    public RateBonusInfo(int id, int consumeItemId, long consumeItemAmount) {
      this.id = id;
      this.consumeItemId = consumeItemId;
      this.consumeItemAmount = consumeItemAmount;
    }

    public Bonus makeBonus() {
      Bonus bonus = new Bonus();
      bonus.setBonusExpire(this.bonusTimeSeconds + System.currentTimeMillis() / 1000L);
      bonus.setRateXp(this.rateXp);
      bonus.setRateSp(this.rateSp);
      bonus.setQuestRewardRate(this.questRewardRate);
      bonus.setQuestDropRate(this.questDropRate);
      bonus.setDropAdena(this.dropAdena);
      bonus.setDropItems(this.dropItems);
      bonus.setDropRaidItems(this.dropRaidItems);
      bonus.setDropSpoil(this.dropSpoil);
      bonus.setEnchantItem(this.enchantItemMul);
      return bonus;
    }
  }
}
