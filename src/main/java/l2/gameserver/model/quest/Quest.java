//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.quest;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntObjectHashMap;
import l2.commons.dbutils.DbUtils;
import l2.commons.logging.LogUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.TroveUtils;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExQuestNpcLogList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Quest {
  private static final Logger _log = LoggerFactory.getLogger(Quest.class);
  public static final String SOUND_ITEMGET = "ItemSound.quest_itemget";
  public static final String SOUND_ACCEPT = "ItemSound.quest_accept";
  public static final String SOUND_MIDDLE = "ItemSound.quest_middle";
  public static final String SOUND_FINISH = "ItemSound.quest_finish";
  public static final String SOUND_GIVEUP = "ItemSound.quest_giveup";
  public static final String SOUND_TUTORIAL = "ItemSound.quest_tutorial";
  public static final String SOUND_JACKPOT = "ItemSound.quest_jackpot";
  public static final String SOUND_LIQUID_MIX_01 = "SkillSound5.liquid_mix_01";
  public static final String SOUND_LIQUID_SUCCESS_01 = "SkillSound5.liquid_success_01";
  public static final String SOUND_LIQUID_FAIL_01 = "SkillSound5.liquid_fail_01";
  public static final String SOUND_BEFORE_BATTLE = "Itemsound.quest_before_battle";
  public static final String SOUND_FANFARE_MIDDLE = "ItemSound.quest_fanfare_middle";
  public static final String SOUND_FANFARE2 = "ItemSound.quest_fanfare_2";
  public static final String SOUND_BROKEN_KEY = "ItemSound2.broken_key";
  public static final String SOUND_ENCHANT_SUCESS = "ItemSound3.sys_enchant_sucess";
  public static final String SOUND_ENCHANT_FAILED = "ItemSound3.sys_enchant_failed";
  public static final String SOUND_ED_CHIMES05 = "AmdSound.ed_chimes_05";
  public static final String SOUND_ED_DRONE_02 = "AmbSound.ed_drone_02";
  public static final String SOUND_CD_CRYSTAL_LOOP = "AmbSound.cd_crystal_loop";
  public static final String SOUND_DT_PERCUSSION_01 = "AmbSound.dt_percussion_01";
  public static final String SOUND_AC_PERCUSSION_02 = "AmbSound.ac_percussion_02";
  public static final String SOUND_ARMOR_WOOD_3 = "ItemSound.armor_wood_3";
  public static final String SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH = "ItemSound.item_drop_equip_armor_cloth";
  public static final String SOUND_MT_CREAK01 = "AmbSound.mt_creak01";
  public static final String SOUND_D_WIND_LOOT_02 = "AmdSound.d_wind_loot_02";
  public static final String SOUND_CHARSTAT_OPEN_01 = "InterfaceSound.charstat_open_01";
  public static final String SOUND_DD_HORROR_01 = "AmbSound.dd_horror_01";
  public static final String SOUND_HORROR1 = "SkillSound5.horror_01";
  public static final String SOUND_HORROR2 = "SkillSound5.horror_02";
  public static final String SOUND_ELCROKI_SONG_FULL = "EtcSound.elcroki_song_full";
  public static final String SOUND_ELCROKI_SONG_1ST = "EtcSound.elcroki_song_1st";
  public static final String SOUND_ELCROKI_SONG_2ND = "EtcSound.elcroki_song_2nd";
  public static final String SOUND_ELCROKI_SONG_3RD = "EtcSound.elcroki_song_3rd";
  public static final String SOUND_ITEMDROP_ARMOR_LEATHER = "ItemSound.itemdrop_armor_leather";
  public static final String SOUND_EG_DRON_02 = "AmbSound.eg_dron_02";
  public static final String SOUND_MHFIGHTER_CRY = "ChrSound.MHFighter_cry";
  public static final String SOUND_ITEMDROP_WEAPON_SPEAR = "ItemSound.itemdrop_weapon_spear";
  public static final String SOUND_FDELF_CRY = "ChrSound.FDElf_Cry";
  public static final String SOUND_DD_HORROR_02 = "AmdSound.dd_horror_02";
  public static final String SOUND_D_HORROR_03 = "AmbSound.d_horror_03";
  public static final String SOUND_D_HORROR_15 = "AmbSound.d_horror_15";
  public static final String SOUND_ANTARAS_FEAR = "SkillSound3.antaras_fear";
  public static final String NO_QUEST_DIALOG = "no-quest";
  protected String _descr;
  public static final int ADENA_ID = 57;
  public static final int PARTY_NONE = 0;
  public static final int PARTY_ONE = 1;
  public static final int PARTY_ALL = 2;
  private Map<Integer, Map<String, QuestTimer>> _pausedQuestTimers;
  private TIntHashSet _questItems;
  private TIntObjectHashMap<List<QuestNpcLogInfo>> _npcLogList;
  protected final String _name;
  protected final int _party;
  protected final int _questId;
  public static final int CREATED = 1;
  public static final int STARTED = 2;
  public static final int COMPLETED = 3;
  public static final int DELAYED = 4;

  public void addQuestItem(int... ids) {
    int var3 = ids.length;

    for (int id : ids) {
      if (id != 0) {
        ItemTemplate i = null;
        i = ItemHolder.getInstance().getTemplate(id);
        if (this._questItems.contains(id)) {
          _log.warn("Item " + i + " multiple times in quest drop in " + this.getName());
        }

        this._questItems.add(id);
      }
    }

  }

  public int[] getItems() {
    return this._questItems.toArray();
  }

  public boolean isQuestItem(int id) {
    return this._questItems.contains(id);
  }

  public static void updateQuestInDb(QuestState qs) {
    updateQuestVarInDb(qs, "<state>", qs.getStateName());
  }

  public static void updateQuestVarInDb(QuestState qs, String var, String value) {
    Player player = qs.getPlayer();
    if (player != null) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)");
        statement.setInt(1, qs.getPlayer().getObjectId());
        statement.setString(2, qs.getQuest().getName());
        statement.setString(3, var);
        statement.setString(4, value);
        statement.executeUpdate();
      } catch (Exception var10) {
        _log.error("could not insert char quest:", var10);
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

    }
  }

  public static void deleteQuestInDb(QuestState qs) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=?");
      statement.setInt(1, qs.getPlayer().getObjectId());
      statement.setString(2, qs.getQuest().getName());
      statement.executeUpdate();
    } catch (Exception var7) {
      _log.error("could not delete char quest:", var7);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public static void deleteQuestVarInDb(QuestState qs, String var) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=? AND var=?");
      statement.setInt(1, qs.getPlayer().getObjectId());
      statement.setString(2, qs.getQuest().getName());
      statement.setString(3, var);
      statement.executeUpdate();
    } catch (Exception var8) {
      _log.error("could not delete char quest:", var8);
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

  }

  public static void restoreQuestStates(Player player) {
    Connection con = null;
    PreparedStatement statement = null;
    PreparedStatement invalidQuestData = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? and name=?");
      statement = con.prepareStatement("SELECT name,value FROM character_quests WHERE char_id=? AND var=?");
      statement.setInt(1, player.getObjectId());
      statement.setString(2, "<state>");
      rset = statement.executeQuery();

      String questId;
      String var;
      while(rset.next()) {
        questId = rset.getString("name");
        var = rset.getString("value");
        if (var.equalsIgnoreCase("Start")) {
          invalidQuestData.setInt(1, player.getObjectId());
          invalidQuestData.setString(2, questId);
          invalidQuestData.executeUpdate();
        } else {
          Quest q = QuestManager.getQuest(questId);
          if (q == null) {
            if (!Config.DONTLOADQUEST) {
              _log.warn("Unknown quest " + questId + " for player " + player.getName());
            }
          } else {
            new QuestState(q, player, getStateId(var));
          }
        }
      }

      DbUtils.close(statement, rset);
      statement = con.prepareStatement("SELECT name,var,value FROM character_quests WHERE char_id=?");
      statement.setInt(1, player.getObjectId());
      rset = statement.executeQuery();

      while(rset.next()) {
        questId = rset.getString("name");
        var = rset.getString("var");
        String value = rset.getString("value");
        QuestState qs = player.getQuestState(questId);
        if (qs != null) {
          if (var.equals("cond") && Integer.parseInt(value) < 0) {
            value = String.valueOf(Integer.parseInt(value) | 1);
          }

          qs.set(var, value, false);
        }
      }
    } catch (Exception var12) {
      _log.error("could not insert char quest:", var12);
    } finally {
      DbUtils.closeQuietly(invalidQuestData);
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  public static String getStateName(int state) {
    switch(state) {
      case 1:
        return "Start";
      case 2:
        return "Started";
      case 3:
        return "Completed";
      case 4:
        return "Delayed";
      default:
        return "Start";
    }
  }

  public static int getStateId(String state) {
    if (state.equalsIgnoreCase("Start")) {
      return 1;
    } else if (state.equalsIgnoreCase("Started")) {
      return 2;
    } else if (state.equalsIgnoreCase("Completed")) {
      return 3;
    } else {
      return state.equalsIgnoreCase("Delayed") ? 4 : 1;
    }
  }

  public Quest(boolean party) {
    this(party ? 1 : 0);
  }

  public Quest(int party) {
    this._pausedQuestTimers = new ConcurrentHashMap();
    this._questItems = new TIntHashSet();
    this._npcLogList = TroveUtils.emptyIntObjectMap();
    this._name = this.getClass().getSimpleName();
    this._questId = Integer.parseInt(this._name.split("_")[1]);
    this._party = party;
    QuestManager.addQuest(this);
  }

  public List<QuestNpcLogInfo> getNpcLogList(int cond) {
    return (List)this._npcLogList.get(cond);
  }

  public void addAttackId(int... attackIds) {
    int[] var2 = attackIds;
    int var3 = attackIds.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      int attackId = var2[var4];
      this.addEventId(attackId, QuestEventType.ATTACKED_WITH_QUEST);
    }

  }

  public NpcTemplate addEventId(int npcId, QuestEventType eventType) {
    try {
      NpcTemplate t = NpcHolder.getInstance().getTemplate(npcId);
      if (t != null) {
        t.addQuestEvent(eventType, this);
      }

      return t;
    } catch (Exception var4) {
      _log.error("", var4);
      return null;
    }
  }

  public void addKillId(int... killIds) {
    int[] var2 = killIds;
    int var3 = killIds.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      int killid = var2[var4];
      this.addEventId(killid, QuestEventType.MOB_KILLED_WITH_QUEST);
    }

  }

  public void addKillNpcWithLog(int cond, String varName, int max, int... killIds) {
    if (killIds.length == 0) {
      throw new IllegalArgumentException("Npc list cant be empty!");
    } else {
      this.addKillId(killIds);
      if (this._npcLogList.isEmpty()) {
        this._npcLogList = new TIntObjectHashMap(5);
      }

      List<QuestNpcLogInfo> vars = (List)this._npcLogList.get(cond);
      if (vars == null) {
        this._npcLogList.put(cond, vars = new ArrayList(5));
      }

      ((List)vars).add(new QuestNpcLogInfo(killIds, varName, max));
    }
  }

  public boolean updateKill(NpcInstance npc, QuestState st) {
    Player player = st.getPlayer();
    if (player == null) {
      return false;
    } else {
      List<QuestNpcLogInfo> vars = this.getNpcLogList(st.getCond());
      if (vars == null) {
        return false;
      } else {
        boolean done = true;
        boolean find = false;
        Iterator var7 = vars.iterator();

        while(var7.hasNext()) {
          QuestNpcLogInfo info = (QuestNpcLogInfo)var7.next();
          int count = st.getInt(info.getVarName());
          if (!find && ArrayUtils.contains(info.getNpcIds(), npc.getNpcId())) {
            find = true;
            if (count < info.getMaxCount()) {
              String var10001 = info.getVarName();
              ++count;
              st.set(var10001, count);
              player.sendPacket(new ExQuestNpcLogList(st));
            }
          }

          if (count != info.getMaxCount()) {
            done = false;
          }
        }

        return done;
      }
    }
  }

  public void addKillId(Collection<Integer> killIds) {
    Iterator var2 = killIds.iterator();

    while(var2.hasNext()) {
      int killid = (Integer)var2.next();
      this.addKillId(killid);
    }

  }

  public NpcTemplate addSkillUseId(int npcId) {
    return this.addEventId(npcId, QuestEventType.MOB_TARGETED_BY_SKILL);
  }

  public void addStartNpc(int... npcIds) {
    int[] var2 = npcIds;
    int var3 = npcIds.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      int talkId = var2[var4];
      this.addStartNpc(talkId);
    }

  }

  public NpcTemplate addStartNpc(int npcId) {
    this.addTalkId(npcId);
    return this.addEventId(npcId, QuestEventType.QUEST_START);
  }

  public void addFirstTalkId(int... npcIds) {
    int[] var2 = npcIds;
    int var3 = npcIds.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      int npcId = var2[var4];
      this.addEventId(npcId, QuestEventType.NPC_FIRST_TALK);
    }

  }

  public void addTalkId(int... talkIds) {
    int[] var2 = talkIds;
    int var3 = talkIds.length;

    for(int var4 = 0; var4 < var3; ++var4) {
      int talkId = var2[var4];
      this.addEventId(talkId, QuestEventType.QUEST_TALK);
    }

  }

  public void addTalkId(Collection<Integer> talkIds) {
    Iterator var2 = talkIds.iterator();

    while(var2.hasNext()) {
      int talkId = (Integer)var2.next();
      this.addTalkId(talkId);
    }

  }

  public String getDescr(Player player) {
    QuestState qs = player.getQuestState(this);
    String desc = this._descr != null ? this._descr : (new CustomMessage("q." + this._questId, player, new Object[0])).toString();
    if (qs == null || qs.isCreated() && qs.isNowAvailable()) {
      return desc;
    } else {
      return !qs.isCompleted() && qs.isNowAvailable() ? (new CustomMessage("quest.InProgress", player, new Object[]{desc})).toString() : (new CustomMessage("quest.Done", player, new Object[]{desc})).toString();
    }
  }

  public String getName() {
    return this._name;
  }

  public int getQuestIntId() {
    return this._questId;
  }

  public int getParty() {
    return this._party;
  }

  public QuestState newQuestState(Player player, int state) {
    QuestState qs = new QuestState(this, player, state);
    updateQuestInDb(qs);
    return qs;
  }

  public QuestState newQuestStateAndNotSave(Player player, int state) {
    return new QuestState(this, player, state);
  }

  public void notifyAttack(NpcInstance npc, QuestState qs) {
    String res = null;

    try {
      res = this.onAttack(npc, qs);
    } catch (Exception var5) {
      this.showError(qs.getPlayer(), var5);
      return;
    }

    this.showResult(npc, qs.getPlayer(), res);
  }

  public void notifyDeath(Creature killer, Creature victim, QuestState qs) {
    String res = null;

    try {
      res = this.onDeath(killer, victim, qs);
    } catch (Exception var6) {
      this.showError(qs.getPlayer(), var6);
      return;
    }

    this.showResult((NpcInstance)null, qs.getPlayer(), res);
  }

  public void notifyEvent(String event, QuestState qs, NpcInstance npc) {
    String res = null;

    try {
      res = this.onEvent(event, qs, npc);
    } catch (Exception var6) {
      this.showError(qs.getPlayer(), var6);
      return;
    }

    this.showResult(npc, qs.getPlayer(), res);
  }

  public void notifyKill(NpcInstance npc, QuestState qs) {
    String res = null;

    try {
      res = this.onKill(npc, qs);
    } catch (Exception var5) {
      this.showError(qs.getPlayer(), var5);
      return;
    }

    this.showResult(npc, qs.getPlayer(), res);
  }

  public void notifyKill(Player target, QuestState qs) {
    String res = null;

    try {
      res = this.onKill(target, qs);
    } catch (Exception var5) {
      this.showError(qs.getPlayer(), var5);
      return;
    }

    this.showResult((NpcInstance)null, qs.getPlayer(), res);
  }

  public final boolean notifyFirstTalk(NpcInstance npc, Player player) {
    String res = null;

    try {
      res = this.onFirstTalk(npc, player);
    } catch (Exception var5) {
      this.showError(player, var5);
      return true;
    }

    return this.showResult(npc, player, res, true);
  }

  public boolean notifyTalk(NpcInstance npc, QuestState qs) {
    String res = null;

    try {
      res = this.onTalk(npc, qs);
    } catch (Exception var5) {
      this.showError(qs.getPlayer(), var5);
      return true;
    }

    return this.showResult(npc, qs.getPlayer(), res);
  }

  public boolean notifySkillUse(NpcInstance npc, Skill skill, QuestState qs) {
    String res = null;

    try {
      res = this.onSkillUse(npc, skill, qs);
    } catch (Exception var6) {
      this.showError(qs.getPlayer(), var6);
      return true;
    }

    return this.showResult(npc, qs.getPlayer(), res);
  }

  public void notifyCreate(QuestState qs) {
    try {
      this.onCreate(qs);
    } catch (Exception var3) {
      this.showError(qs.getPlayer(), var3);
    }

  }

  public void notifyOlympiadResult(QuestState qs, CompetitionType type, boolean isWin) {
    try {
      this.onOlympiadResult(qs, type, isWin);
    } catch (Exception var5) {
      this.showError(qs.getPlayer(), var5);
    }

  }

  public void onOlympiadResult(QuestState qs, CompetitionType type, boolean isWin) {
  }

  public void onCreate(QuestState qs) {
  }

  public String onAttack(NpcInstance npc, QuestState qs) {
    return null;
  }

  public String onDeath(Creature killer, Creature victim, QuestState qs) {
    return null;
  }

  public String onEvent(String event, QuestState qs, NpcInstance npc) {
    return null;
  }

  public String onKill(NpcInstance npc, QuestState qs) {
    return null;
  }

  public String onKill(Player killed, QuestState st) {
    return null;
  }

  public String onFirstTalk(NpcInstance npc, Player player) {
    return null;
  }

  public String onTalk(NpcInstance npc, QuestState qs) {
    return null;
  }

  public String onSkillUse(NpcInstance npc, Skill skill, QuestState qs) {
    return null;
  }

  public void onAbort(QuestState qs) {
  }

  public boolean canAbortByPacket() {
    return true;
  }

  private void showError(Player player, Throwable t) {
    _log.error("", t);
    if (player != null && player.isGM()) {
      String res = "<html><body><title>Script error</title>" + LogUtils.dumpStack(t).replace("\n", "<br>") + "</body></html>";
      this.showResult((NpcInstance)null, player, res);
    }

  }

  protected void showHtmlFile(Player player, String fileName, boolean showQuestInfo) {
    this.showHtmlFile(player, fileName, showQuestInfo, ArrayUtils.EMPTY_OBJECT_ARRAY);
  }

  protected void showHtmlFile(Player player, String fileName, boolean showQuestInfo, Object... arg) {
    if (player != null) {
      GameObject target = player.getTarget();
      NpcHtmlMessage npcReply = new NpcHtmlMessage(target == null ? 5 : target.getObjectId());
      npcReply.setFile("quests/" + this.getClass().getSimpleName() + "/" + fileName);
      if (arg.length % 2 == 0) {
        for(int i = 0; i < arg.length; i += 2) {
          npcReply.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
        }
      }

      player.sendPacket(npcReply);
    }
  }

  protected void showSimpleHtmFile(Player player, String fileName) {
    if (player != null) {
      NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
      npcReply.setFile(fileName);
      player.sendPacket(npcReply);
    }
  }

  private boolean showResult(NpcInstance npc, Player player, String res) {
    return this.showResult(npc, player, res, false);
  }

  private boolean showResult(NpcInstance npc, Player player, String res, boolean isFirstTalk) {
    boolean showQuestInfo = this.showQuestInfo(player);
    if (isFirstTalk) {
      showQuestInfo = false;
    }

    if (res == null) {
      return true;
    } else if (res.isEmpty()) {
      return false;
    } else {
      if (!res.startsWith("no_quest") && !res.equalsIgnoreCase("noquest") && !res.equalsIgnoreCase("no-quest")) {
        if (res.equalsIgnoreCase("completed")) {
          this.showSimpleHtmFile(player, "completed-quest.htm");
        } else if (res.endsWith(".htm")) {
          this.showHtmlFile(player, res, showQuestInfo);
        } else {
          NpcHtmlMessage npcReply = new NpcHtmlMessage(npc == null ? 5 : npc.getObjectId());
          npcReply.setHtml(res);
          player.sendPacket(npcReply);
        }
      } else {
        this.showSimpleHtmFile(player, "no-quest.htm");
      }

      return true;
    }
  }

  private boolean showQuestInfo(Player player) {
    QuestState qs = player.getQuestState(this.getName());
    if (qs != null && qs.getState() != 1) {
      return false;
    } else {
      return this.isVisible();
    }
  }

  void pauseQuestTimers(QuestState qs) {
    if (!qs.getTimers().isEmpty()) {
      Iterator var2 = qs.getTimers().values().iterator();

      while(var2.hasNext()) {
        QuestTimer timer = (QuestTimer)var2.next();
        timer.setQuestState((QuestState)null);
        timer.pause();
      }

      this._pausedQuestTimers.put(qs.getPlayer().getObjectId(), qs.getTimers());
    }
  }

  void resumeQuestTimers(QuestState qs) {
    Map<String, QuestTimer> timers = (Map)this._pausedQuestTimers.remove(qs.getPlayer().getObjectId());
    if (timers != null) {
      qs.getTimers().putAll(timers);
      Iterator var3 = qs.getTimers().values().iterator();

      while(var3.hasNext()) {
        QuestTimer timer = (QuestTimer)var3.next();
        timer.setQuestState(qs);
        timer.start();
      }

    }
  }

  protected String str(long i) {
    return String.valueOf(i);
  }

  public NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay) {
    return this.addSpawn(npcId, new Location(x, y, z, heading), randomOffset, despawnDelay);
  }

  public NpcInstance addSpawn(int npcId, Location loc, int randomOffset, int despawnDelay) {
    NpcInstance result = Functions.spawn(randomOffset > 50 ? Location.findPointToStay(loc, 0, randomOffset, ReflectionManager.DEFAULT.getGeoIndex()) : loc, npcId);
    if (despawnDelay > 0 && result != null) {
      ThreadPoolManager.getInstance().schedule(new Quest.DeSpawnScheduleTimerTask(result), (long)despawnDelay);
    }

    return result;
  }

  public static NpcInstance addSpawnToInstance(int npcId, int x, int y, int z, int heading, int randomOffset, int refId) {
    return addSpawnToInstance(npcId, new Location(x, y, z, heading), randomOffset, refId);
  }

  public static NpcInstance addSpawnToInstance(int npcId, Location loc, int randomOffset, int refId) {
    try {
      NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
      if (template != null) {
        NpcInstance npc = NpcHolder.getInstance().getTemplate(npcId).getNewInstance();
        npc.setReflection(refId);
        npc.setSpawnedLoc(randomOffset > 50 ? Location.findPointToStay(loc, 50, randomOffset, npc.getGeoIndex()) : loc);
        npc.spawnMe(npc.getSpawnedLoc());
        return npc;
      }
    } catch (Exception var6) {
      _log.warn("Could not spawn Npc " + npcId);
    }

    return null;
  }

  public boolean isVisible() {
    return true;
  }

  public QuestRates getRates() {
    QuestRates questRates = (QuestRates)Config.QUEST_RATES.get(this.getQuestIntId());
    if (questRates == null) {
      Config.QUEST_RATES.put(this.getQuestIntId(), questRates = new QuestRates(this.getQuestIntId()));
    }

    return questRates;
  }

  public class DeSpawnScheduleTimerTask extends RunnableImpl {
    NpcInstance _npc = null;

    public DeSpawnScheduleTimerTask(NpcInstance npc) {
      this._npc = npc;
    }

    public void runImpl() throws Exception {
      if (this._npc != null) {
        if (this._npc.getSpawn() != null) {
          this._npc.getSpawn().deleteAll();
        } else {
          this._npc.deleteMe();
        }
      }

    }
  }
}
