//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.gameserver.Config;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Experience;

public class SubClass {
  private int _class = 0;
  private long _exp;
  private long minExp;
  private long maxExp;
  private int _sp = 0;
  private int _level;
  private double _Hp = 1.0D;
  private double _Mp = 1.0D;
  private double _Cp = 1.0D;
  private boolean _active = false;
  private boolean _isBase = false;
  private DeathPenalty _dp;

  public SubClass() {
    this.minExp = Experience.LEVEL[Config.ALT_LEVEL_AFTER_GET_SUBCLASS];
    this.maxExp = Experience.LEVEL[Experience.LEVEL.length - 1];
    this._level = Config.ALT_LEVEL_AFTER_GET_SUBCLASS;
    this._exp = Math.max(this.minExp, Experience.LEVEL[this._level]);
  }

  public int getClassId() {
    return this._class;
  }

  public long getExp() {
    return this._exp;
  }

  public long getMaxExp() {
    return this.maxExp;
  }

  public void addExp(long val) {
    this.setExp(this._exp + val);
  }

  public long getSp() {
    return (long)Math.min(this._sp, 2147483647);
  }

  public void addSp(long val) {
    this.setSp((long)this._sp + val);
  }

  public int getLevel() {
    return this._level;
  }

  public void setClassId(int classId) {
    this._class = classId;
  }

  public void setExp(long val) {
    val = Math.max(val, this.minExp);
    val = Math.min(val, this.maxExp);
    this._exp = val;
    this._level = Experience.getLevel(this._exp);
  }

  public void setSp(long spValue) {
    spValue = Math.max(spValue, 0L);
    spValue = Math.min(spValue, 2147483647L);
    this._sp = (int)spValue;
  }

  public void setHp(double hpValue) {
    this._Hp = hpValue;
  }

  public double getHp() {
    return this._Hp;
  }

  public void setMp(double mpValue) {
    this._Mp = mpValue;
  }

  public double getMp() {
    return this._Mp;
  }

  public void setCp(double cpValue) {
    this._Cp = cpValue;
  }

  public double getCp() {
    return this._Cp;
  }

  public void setActive(boolean active) {
    this._active = active;
  }

  public boolean isActive() {
    return this._active;
  }

  public void setBase(boolean base) {
    this._isBase = base;
    this.minExp = Experience.LEVEL[this._isBase ? 1 : Config.ALT_LEVEL_AFTER_GET_SUBCLASS];
    this.maxExp = Experience.LEVEL[(this._isBase ? Experience.getMaxLevel() : Experience.getMaxSubLevel()) + 1] - 1L;
  }

  public boolean isBase() {
    return this._isBase;
  }

  public DeathPenalty getDeathPenalty(Player player) {
    if (this._dp == null) {
      this._dp = new DeathPenalty(player, 0);
    }

    return this._dp;
  }

  public void setDeathPenalty(DeathPenalty dp) {
    this._dp = dp;
  }

  public String toString() {
    return ClassId.VALUES[this._class].toString() + " " + this._level;
  }
}
