//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

import java.util.List;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Territory;
import l2.gameserver.model.Zone.ZoneTarget;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.Race;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class ZoneTemplate {
  private final String _name;
  private final ZoneType _type;
  private final Territory _territory;
  private final boolean _isEnabled;
  private final boolean _isDefault;
  private final List<Location> _restartPoints;
  private final List<Location> _PKrestartPoints;
  private final long _restartTime;
  private final int _enteringMessageId;
  private final int _leavingMessageId;
  private final Race _affectRace;
  private final ZoneTarget _target;
  private final Skill _skill;
  private final int _skillProb;
  private final int _initialDelay;
  private final int _unitTick;
  private final int _randomTick;
  private final int _damageMessageId;
  private final int _damageOnHP;
  private final int _damageOnMP;
  private final double _moveBonus;
  private final double _regenBonusHP;
  private final double _regenBonusMP;
  private final int _eventId;
  private final String[] _blockedActions;
  private final int _index;
  private final int _taxById;
  private final StatsSet _params;

  public ZoneTemplate(StatsSet set) {
    this._name = set.getString("name");
    this._type = ZoneType.valueOf(set.getString("type"));
    this._territory = (Territory)set.get("territory");
    this._enteringMessageId = set.getInteger("entering_message_no", 0);
    this._leavingMessageId = set.getInteger("leaving_message_no", 0);
    this._target = ZoneTarget.valueOf(set.getString("target", "pc"));
    this._affectRace = set.getString("affect_race", "all").equals("all") ? null : Race.valueOf(set.getString("affect_race"));
    String s = set.getString("skill_name", (String)null);
    Skill skill = null;
    if (s != null) {
      String[] sk = s.split("[\\s,;]+");
      skill = SkillTable.getInstance().getInfo(Integer.parseInt(sk[0]), Integer.parseInt(sk[1]));
    }

    this._skill = skill;
    this._skillProb = set.getInteger("skill_prob", 100);
    this._initialDelay = set.getInteger("initial_delay", 1);
    this._unitTick = set.getInteger("unit_tick", 1);
    this._randomTick = set.getInteger("random_time", 0);
    this._moveBonus = set.getDouble("move_bonus", 0.0D);
    this._regenBonusHP = set.getDouble("hp_regen_bonus", 0.0D);
    this._regenBonusMP = set.getDouble("mp_regen_bonus", 0.0D);
    this._damageOnHP = set.getInteger("damage_on_hp", 0);
    this._damageOnMP = set.getInteger("damage_on_mp", 0);
    this._damageMessageId = set.getInteger("message_no", 0);
    this._eventId = set.getInteger("eventId", 0);
    this._isEnabled = set.getBool("enabled", true);
    this._isDefault = set.getBool("default", true);
    this._restartPoints = (List)set.get("restart_points");
    this._PKrestartPoints = (List)set.get("PKrestart_points");
    this._restartTime = set.getLong("restart_time", 0L);
    s = (String)set.get("blocked_actions");
    if (s != null) {
      this._blockedActions = s.split("[\\s,;]+");
    } else {
      this._blockedActions = null;
    }

    this._index = set.getInteger("index", 0);
    this._taxById = set.getInteger("taxById", 0);
    this._params = set;
  }

  public boolean isEnabled() {
    return this._isEnabled;
  }

  public boolean isDefault() {
    return this._isDefault;
  }

  public String getName() {
    return this._name;
  }

  public ZoneType getType() {
    return this._type;
  }

  public Territory getTerritory() {
    return this._territory;
  }

  public int getEnteringMessageId() {
    return this._enteringMessageId;
  }

  public int getLeavingMessageId() {
    return this._leavingMessageId;
  }

  public Skill getZoneSkill() {
    return this._skill;
  }

  public int getSkillProb() {
    return this._skillProb;
  }

  public int getInitialDelay() {
    return this._initialDelay;
  }

  public int getUnitTick() {
    return this._unitTick;
  }

  public int getRandomTick() {
    return this._randomTick;
  }

  public ZoneTarget getZoneTarget() {
    return this._target;
  }

  public Race getAffectRace() {
    return this._affectRace;
  }

  public String[] getBlockedActions() {
    return this._blockedActions;
  }

  public int getDamageMessageId() {
    return this._damageMessageId;
  }

  public int getDamageOnHP() {
    return this._damageOnHP;
  }

  public int getDamageOnMP() {
    return this._damageOnMP;
  }

  public double getMoveBonus() {
    return this._moveBonus;
  }

  public double getRegenBonusHP() {
    return this._regenBonusHP;
  }

  public double getRegenBonusMP() {
    return this._regenBonusMP;
  }

  public long getRestartTime() {
    return this._restartTime;
  }

  public List<Location> getRestartPoints() {
    return this._restartPoints;
  }

  public List<Location> getPKRestartPoints() {
    return this._PKrestartPoints;
  }

  public int getIndex() {
    return this._index;
  }

  public int getTaxById() {
    return this._taxById;
  }

  public int getEventId() {
    return this._eventId;
  }

  public MultiValueSet<String> getParams() {
    return this._params.clone();
  }
}
