//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.npc;

import gnu.trove.TIntObjectHashMap;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.util.TroveUtils;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Skill;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.instances.ReflectionBossInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.StatsSet;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NpcTemplate extends CharTemplate {
  private static final Logger _log = LoggerFactory.getLogger(NpcTemplate.class);
  public static final Constructor<?> DEFAULT_TYPE_CONSTRUCTOR = NpcInstance.class.getConstructors()[0];
  public static final Constructor<?> DEFAULT_AI_CONSTRUCTOR = CharacterAI.class.getConstructors()[0];
  public final int npcId;
  public final String name;
  public final String title;
  public final int level;
  public final long rewardExp;
  public final int rewardSp;
  public final int rewardRp;
  public final int aggroRange;
  public final int rhand;
  public final int lhand;
  public final double rateHp;
  private Faction faction;
  public final String jClass;
  public final int displayId;
  public final NpcTemplate.ShotsType shots;
  public boolean isRaid;
  private final StatsSet _AIParams;
  private int race;
  private final int _castleId;
  private Map<RewardType, RewardList> _rewards;
  private TIntObjectHashMap<TeleportLocation[]> _teleportList;
  private List<MinionData> _minions;
  private List<AbsorbInfo> _absorbInfo;
  private List<ClassId> _teachInfo;
  private Map<QuestEventType, Quest[]> _questEvents;
  private TIntObjectHashMap<Skill> _skills;
  private Skill[] _damageSkills;
  private Skill[] _dotSkills;
  private Skill[] _debuffSkills;
  private Skill[] _buffSkills;
  private Skill[] _stunSkills;
  private Skill[] _healSkills;
  private Class<NpcInstance> _classType;
  private Constructor<NpcInstance> _constructorType;
  private Class<CharacterAI> _classAI;
  private Constructor<CharacterAI> _constructorAI;
  private String _htmRoot;

  public NpcTemplate(StatsSet set) {
    super(set);
    this.faction = Faction.NONE;
    this.isRaid = false;
    this.race = 0;
    this._rewards = Collections.emptyMap();
    this._teleportList = TroveUtils.emptyIntObjectMap();
    this._minions = Collections.emptyList();
    this._absorbInfo = Collections.emptyList();
    this._teachInfo = Collections.emptyList();
    this._questEvents = Collections.emptyMap();
    this._skills = TroveUtils.emptyIntObjectMap();
    this._damageSkills = Skill.EMPTY_ARRAY;
    this._dotSkills = Skill.EMPTY_ARRAY;
    this._debuffSkills = Skill.EMPTY_ARRAY;
    this._buffSkills = Skill.EMPTY_ARRAY;
    this._stunSkills = Skill.EMPTY_ARRAY;
    this._healSkills = Skill.EMPTY_ARRAY;
    this._classType = NpcInstance.class;
    this._constructorType = (Constructor<NpcInstance>) DEFAULT_TYPE_CONSTRUCTOR;
    this._classAI = CharacterAI.class;
    this._constructorAI = (Constructor<CharacterAI>) DEFAULT_AI_CONSTRUCTOR;
    this.npcId = set.getInteger("npcId");
    this.displayId = set.getInteger("displayId");
    this.name = set.getString("name");
    this.title = set.getString("title");
    this.level = set.getInteger("level");
    this.rewardExp = set.getLong("rewardExp");
    this.rewardSp = set.getInteger("rewardSp");
    this.rewardRp = set.getInteger("rewardRp");
    this.aggroRange = set.getInteger("aggroRange");
    this.rhand = set.getInteger("rhand", 0);
    this.lhand = set.getInteger("lhand", 0);
    this.rateHp = set.getDouble("baseHpRate");
    this.jClass = set.getString("texture", null);
    this._htmRoot = set.getString("htm_root", null);
    this.shots = set.getEnum("shots", ShotsType.class, ShotsType.NONE);
    this._castleId = set.getInteger("castle_id", 0);
    this._AIParams = (StatsSet)set.getObject("aiParams", StatsSet.EMPTY);
    this.setType(set.getString("type", null));
    this.setAI(set.getString("ai_type", null));
  }

  public Class<? extends NpcInstance> getInstanceClass() {
    return this._classType;
  }

  public Constructor<? extends NpcInstance> getInstanceConstructor() {
    return this._constructorType;
  }

  public boolean isInstanceOf(Class<?> _class) {
    return _class.isAssignableFrom(this._classType);
  }

  public NpcInstance getNewInstance() {
    try {
      return this._constructorType.newInstance(IdFactory.getInstance().getNextId(), this);
    } catch (Exception var2) {
      _log.error("Unable to create instance of NPC " + this.npcId, var2);
      return null;
    }
  }

  public CharacterAI getNewAI(NpcInstance npc) {
    try {
      return this._constructorAI.newInstance(npc);
    } catch (Exception var3) {
      _log.error("Unable to create ai of NPC " + this.npcId, var3);
      return new CharacterAI(npc);
    }
  }

  private void setType(String type) {
    Class classType = null;

    try {
      classType = Class.forName("l2.gameserver.model.instances." + type + "Instance");
    } catch (ClassNotFoundException var4) {
      classType = Scripts.getInstance().getClasses().get("npc.model." + type + "Instance");
    }

    if (classType == null) {
      _log.error("Not found type class for type: " + type + ". NpcId: " + this.npcId);
    } else {
      this._classType = classType;
      this._constructorType = (Constructor<NpcInstance>) this._classType.getConstructors()[0];
    }

    if (this._classType.isAnnotationPresent(Deprecated.class)) {
      _log.error("Npc type: " + type + ", is deprecated. NpcId: " + this.npcId);
    }

    this.isRaid = this.isInstanceOf(RaidBossInstance.class) && !this.isInstanceOf(ReflectionBossInstance.class);
  }

  private void setAI(String ai) {
    Class classAI = null;

    try {
      classAI = Class.forName("l2.gameserver.ai." + ai);
    } catch (ClassNotFoundException var4) {
      classAI = Scripts.getInstance().getClasses().get("ai." + ai);
    }

    if (classAI == null) {
      _log.error("Not found ai class for ai: " + ai + ". NpcId: " + this.npcId);
    } else {
      this._classAI = classAI;
      this._constructorAI = (Constructor<CharacterAI>) this._classAI.getConstructors()[0];
    }

    if (this._classAI.isAnnotationPresent(Deprecated.class)) {
      _log.error("Ai type: " + ai + ", is deprecated. NpcId: " + this.npcId);
    }

  }

  public void addTeachInfo(ClassId classId) {
    if (this._teachInfo.isEmpty()) {
      this._teachInfo = new ArrayList<>(1);
    }

    this._teachInfo.add(classId);
  }

  public List<ClassId> getTeachInfo() {
    return this._teachInfo;
  }

  public boolean canTeach(ClassId classId) {
    return this._teachInfo.contains(classId);
  }

  public void addTeleportList(int id, TeleportLocation[] list) {
    if (this._teleportList.isEmpty()) {
      this._teleportList = new TIntObjectHashMap<>(1);
    }

    this._teleportList.put(id, list);
  }

  public TeleportLocation[] getTeleportList(int id) {
    return this._teleportList.get(id);
  }

  public TIntObjectHashMap<TeleportLocation[]> getTeleportList() {
    return this._teleportList;
  }

  public void putRewardList(RewardType rewardType, RewardList list) {
    if (this._rewards.isEmpty()) {
      this._rewards = new HashMap<>(RewardType.values().length);
    }

    this._rewards.put(rewardType, list);
  }

  public RewardList getRewardList(RewardType t) {
    return this._rewards.get(t);
  }

  public Map<RewardType, RewardList> getRewards() {
    return this._rewards;
  }

  public void addAbsorbInfo(AbsorbInfo absorbInfo) {
    if (this._absorbInfo.isEmpty()) {
      this._absorbInfo = new ArrayList<>(1);
    }

    this._absorbInfo.add(absorbInfo);
  }

  public void addMinion(MinionData minion) {
    if (this._minions.isEmpty()) {
      this._minions = new ArrayList<>(1);
    }

    this._minions.add(minion);
  }

  public void setFaction(Faction faction) {
    this.faction = faction;
  }

  public Faction getFaction() {
    return this.faction;
  }

  public void addSkill(Skill skill) {
    if (this._skills.isEmpty()) {
      this._skills = new TIntObjectHashMap<>();
    }

    this._skills.put(skill.getId(), skill);
    if (!skill.isNotUsedByAI() && skill.getTargetType() != SkillTargetType.TARGET_NONE && skill.getSkillType() != SkillType.NOTDONE && skill.isActive()) {
      switch(skill.getSkillType()) {
        case PDAM:
        case MANADAM:
        case MDAM:
        case DRAIN:
        case DRAIN_SOUL:
          boolean added = false;
          if (skill.hasEffects()) {
            EffectTemplate[] var3 = skill.getEffectTemplates();
            int var4 = var3.length;

            for (EffectTemplate eff : var3) {
              switch (eff.getEffectType()) {
                case Stun:
                  this._stunSkills = ArrayUtils.add(this._stunSkills, skill);
                  added = true;
                  break;
                case DamOverTime:
                case DamOverTimeLethal:
                case ManaDamOverTime:
                case LDManaDamOverTime:
                  this._dotSkills = ArrayUtils.add(this._dotSkills, skill);
                  added = true;
              }
            }
          }

          if (!added) {
            this._damageSkills = ArrayUtils.add(this._damageSkills, skill);
          }
          break;
        case DOT:
        case MDOT:
        case POISON:
        case BLEED:
          this._dotSkills = ArrayUtils.add(this._dotSkills, skill);
          break;
        case DEBUFF:
        case SLEEP:
        case ROOT:
        case PARALYZE:
        case MUTE:
        case TELEPORT_NPC:
        case AGGRESSION:
          this._debuffSkills = ArrayUtils.add(this._debuffSkills, skill);
          break;
        case BUFF:
          this._buffSkills = ArrayUtils.add(this._buffSkills, skill);
          break;
        case STUN:
          this._stunSkills = ArrayUtils.add(this._stunSkills, skill);
          break;
        case HEAL:
        case HEAL_PERCENT:
        case HOT:
          this._healSkills = ArrayUtils.add(this._healSkills, skill);
      }

    }
  }

  public Skill[] getDamageSkills() {
    return this._damageSkills;
  }

  public Skill[] getDotSkills() {
    return this._dotSkills;
  }

  public Skill[] getDebuffSkills() {
    return this._debuffSkills;
  }

  public Skill[] getBuffSkills() {
    return this._buffSkills;
  }

  public Skill[] getStunSkills() {
    return this._stunSkills;
  }

  public Skill[] getHealSkills() {
    return this._healSkills;
  }

  public List<MinionData> getMinionData() {
    return this._minions;
  }

  public TIntObjectHashMap<Skill> getSkills() {
    return this._skills;
  }

  public void addQuestEvent(QuestEventType EventType, Quest q) {
    if (this._questEvents.isEmpty()) {
      this._questEvents = new HashMap<>();
    }

    if (this._questEvents.get(EventType) == null) {
      this._questEvents.put(EventType, new Quest[]{q});
    } else {
      Quest[] _quests = this._questEvents.get(EventType);
      int len = _quests.length;
      Quest[] tmp = new Quest[len + 1];

      for(int i = 0; i < len; ++i) {
        if (_quests[i].getName().equals(q.getName())) {
          _quests[i] = q;
          return;
        }

        tmp[i] = _quests[i];
      }

      tmp[len] = q;
      this._questEvents.put(EventType, tmp);
    }

  }

  public Quest[] getEventQuests(QuestEventType EventType) {
    return this._questEvents.get(EventType);
  }

  public int getRace() {
    return this.race;
  }

  public void setRace(int newrace) {
    this.race = newrace;
  }

  public boolean isUndead() {
    return this.race == 1;
  }

  public String toString() {
    return "Npc template " + this.name + "[" + this.npcId + "]";
  }

  public int getNpcId() {
    return this.npcId;
  }

  public String getName() {
    return this.name;
  }

  public final String getJClass() {
    return this.jClass;
  }

  public final StatsSet getAIParams() {
    return this._AIParams;
  }

  public List<AbsorbInfo> getAbsorbInfo() {
    return this._absorbInfo;
  }

  public int getCastleId() {
    return this._castleId;
  }

  public Map<QuestEventType, Quest[]> getQuestEvents() {
    return this._questEvents;
  }

  public String getHtmRoot() {
    return this._htmRoot;
  }

  public enum ShotsType {
    NONE,
    SOUL,
    SPIRIT,
    BSPIRIT,
    SOUL_SPIRIT,
    SOUL_BSPIRIT;

    ShotsType() {
    }
  }
}
