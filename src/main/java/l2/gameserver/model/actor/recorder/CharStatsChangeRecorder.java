//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.recorder;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.TeamType;

public class CharStatsChangeRecorder<T extends Creature> {
  public static final int BROADCAST_CHAR_INFO = 1;
  public static final int SEND_CHAR_INFO = 2;
  public static final int SEND_STATUS_INFO = 4;
  protected final T _activeChar;
  protected int _level;
  protected int _accuracy;
  protected int _attackSpeed;
  protected int _castSpeed;
  protected int _criticalHit;
  protected int _evasion;
  protected int _magicAttack;
  protected int _magicDefence;
  protected int _maxHp;
  protected int _maxMp;
  protected int _physicAttack;
  protected int _physicDefence;
  protected int _runSpeed;
  protected int _abnormalEffects;
  protected int _abnormalEffects2;
  protected int _abnormalEffects3;
  protected TeamType _team;
  protected int _changes;

  public CharStatsChangeRecorder(T actor) {
    this._activeChar = actor;
  }

  protected int set(int flag, int oldValue, int newValue) {
    if (oldValue != newValue) {
      this._changes |= flag;
    }

    return newValue;
  }

  protected long set(int flag, long oldValue, long newValue) {
    if (oldValue != newValue) {
      this._changes |= flag;
    }

    return newValue;
  }

  protected String set(int flag, String oldValue, String newValue) {
    if (!oldValue.equals(newValue)) {
      this._changes |= flag;
    }

    return newValue;
  }

  protected <E extends Enum<E>> E set(int flag, E oldValue, E newValue) {
    if (oldValue != newValue) {
      this._changes |= flag;
    }

    return newValue;
  }

  protected void refreshStats() {
    this._accuracy = this.set(2, this._accuracy, this._activeChar.getAccuracy());
    this._attackSpeed = this.set(1, this._attackSpeed, this._activeChar.getPAtkSpd());
    this._castSpeed = this.set(1, this._castSpeed, this._activeChar.getMAtkSpd());
    this._criticalHit = this.set(2, this._criticalHit, this._activeChar.getCriticalHit((Creature)null, (Skill)null));
    this._evasion = this.set(2, this._evasion, this._activeChar.getEvasionRate((Creature)null));
    this._runSpeed = this.set(1, this._runSpeed, this._activeChar.getRunSpeed());
    this._physicAttack = this.set(2, this._physicAttack, this._activeChar.getPAtk((Creature)null));
    this._physicDefence = this.set(2, this._physicDefence, this._activeChar.getPDef((Creature)null));
    this._magicAttack = this.set(2, this._magicAttack, this._activeChar.getMAtk((Creature)null, (Skill)null));
    this._magicDefence = this.set(2, this._magicDefence, this._activeChar.getMDef((Creature)null, (Skill)null));
    this._maxHp = this.set(4, this._maxHp, this._activeChar.getMaxHp());
    this._maxMp = this.set(4, this._maxMp, this._activeChar.getMaxMp());
    this._level = this.set(2, this._level, this._activeChar.getLevel());
    this._abnormalEffects = this.set(1, this._abnormalEffects, this._activeChar.getAbnormalEffect());
    this._abnormalEffects2 = this.set(1, this._abnormalEffects2, this._activeChar.getAbnormalEffect2());
    this._abnormalEffects3 = this.set(1, this._abnormalEffects3, this._activeChar.getAbnormalEffect3());
    this._team = (TeamType)this.set(1, (Enum)this._team, (Enum)this._activeChar.getTeam());
  }

  public final void sendChanges() {
    this.refreshStats();
    this.onSendChanges();
    this._changes = 0;
  }

  protected void onSendChanges() {
    if ((this._changes & 4) == 4) {
      this._activeChar.broadcastStatusUpdate();
    }

  }
}
