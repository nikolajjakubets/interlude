//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

public class PetData {
  private int _id;
  private int _level;
  private int _feedMax;
  private int _feedBattle;
  private int _feedNormal;
  private int _pAtk;
  private int _pDef;
  private int _mAtk;
  private int _mDef;
  private int _hp;
  private int _mp;
  private int _hpRegen;
  private int _mpRegen;
  private long _exp;
  private int _accuracy;
  private int _evasion;
  private int _critical;
  private int _speed;
  private int _atkSpeed;
  private int _castSpeed;
  private int _maxLoad;
  private int _controlItemId;
  private int _foodId;
  private int _minLevel;
  private int _addFed;
  private boolean _isMountable;

  public PetData() {
  }

  public int getFeedBattle() {
    return this._feedBattle;
  }

  public void setFeedBattle(int feedBattle) {
    this._feedBattle = feedBattle;
  }

  public int getFeedNormal() {
    return this._feedNormal;
  }

  public void setFeedNormal(int feedNormal) {
    this._feedNormal = feedNormal;
  }

  public int getHP() {
    return this._hp;
  }

  public void setHP(int petHP) {
    this._hp = petHP;
  }

  public int getID() {
    return this._id;
  }

  public void setID(int petID) {
    this._id = petID;
  }

  public int getLevel() {
    return this._level;
  }

  public void setLevel(int petLevel) {
    this._level = petLevel;
  }

  public int getMAtk() {
    return this._mAtk;
  }

  public void setMAtk(int mAtk) {
    this._mAtk = mAtk;
  }

  public int getFeedMax() {
    return this._feedMax;
  }

  public void setFeedMax(int feedMax) {
    this._feedMax = feedMax;
  }

  public int getMDef() {
    return this._mDef;
  }

  public void setMDef(int mDef) {
    this._mDef = mDef;
  }

  public long getExp() {
    return this._exp;
  }

  public void setExp(long exp) {
    this._exp = exp;
  }

  public int getMP() {
    return this._mp;
  }

  public void setMP(int mp) {
    this._mp = mp;
  }

  public int getPAtk() {
    return this._pAtk;
  }

  public void setPAtk(int pAtk) {
    this._pAtk = pAtk;
  }

  public int getPDef() {
    return this._pDef;
  }

  public int getAccuracy() {
    return this._accuracy;
  }

  public int getEvasion() {
    return this._evasion;
  }

  public int getCritical() {
    return this._critical;
  }

  public int getSpeed() {
    return this._speed;
  }

  public int getAtkSpeed() {
    return this._atkSpeed;
  }

  public int getCastSpeed() {
    return this._castSpeed;
  }

  public int getMaxLoad() {
    return this._maxLoad != 0 ? this._maxLoad : this._level * 300;
  }

  public void setPDef(int pDef) {
    this._pDef = pDef;
  }

  public int getHpRegen() {
    return this._hpRegen;
  }

  public void setHpRegen(int hpRegen) {
    this._hpRegen = hpRegen;
  }

  public int getMpRegen() {
    return this._mpRegen;
  }

  public void setMpRegen(int mpRegen) {
    this._mpRegen = mpRegen;
  }

  public void setAccuracy(int accuracy) {
    this._accuracy = accuracy;
  }

  public void setEvasion(int evasion) {
    this._evasion = evasion;
  }

  public void setCritical(int critical) {
    this._critical = critical;
  }

  public void setSpeed(int speed) {
    this._speed = speed;
  }

  public void setAtkSpeed(int atkSpeed) {
    this._atkSpeed = atkSpeed;
  }

  public void setCastSpeed(int castSpeed) {
    this._castSpeed = castSpeed;
  }

  public void setMaxLoad(int maxLoad) {
    this._maxLoad = maxLoad;
  }

  public int getControlItemId() {
    return this._controlItemId;
  }

  public void setControlItemId(int itemId) {
    this._controlItemId = itemId;
  }

  public int getFoodId() {
    return this._foodId;
  }

  public void setFoodId(int id) {
    this._foodId = id;
  }

  public int getMinLevel() {
    return this._minLevel;
  }

  public void setMinLevel(int level) {
    this._minLevel = level;
  }

  public int getAddFed() {
    return this._addFed;
  }

  public void setAddFed(int addFed) {
    this._addFed = addFed;
  }

  public boolean isMountable() {
    return this._isMountable;
  }

  public void setMountable(boolean mountable) {
    this._isMountable = mountable;
  }
}
