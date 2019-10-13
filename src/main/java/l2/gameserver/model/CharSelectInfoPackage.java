//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import java.util.Collection;
import java.util.Iterator;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemInstance.ItemLocation;

public class CharSelectInfoPackage {
  private String _name;
  private int _objectId = 0;
  private int _charId = 199546;
  private long _exp = 0L;
  private int _sp = 0;
  private int _clanId = 0;
  private int _race = 0;
  private int _classId = 0;
  private int _baseClassId = 0;
  private int _deleteTimer = 0;
  private long _lastAccess = 0L;
  private int _face = 0;
  private int _hairStyle = 0;
  private int _hairColor = 0;
  private int _sex = 0;
  private int _level = 1;
  private int _karma = 0;
  private int _pk = 0;
  private int _pvp = 0;
  private int _maxHp = 0;
  private double _currentHp = 0.0D;
  private int _maxMp = 0;
  private double _currentMp = 0.0D;
  private ItemInstance[] _paperdoll;
  private int _accesslevel = 0;
  private int _x = 0;
  private int _y = 0;
  private int _z = 0;
  private int _vitalityPoints = 20000;

  public CharSelectInfoPackage(int objectId, String name) {
    this.setObjectId(objectId);
    this._name = name;
    Collection<ItemInstance> items = ItemsDAO.getInstance().loadItemsByOwnerIdAndLoc(objectId, ItemLocation.PAPERDOLL);
    this._paperdoll = new ItemInstance[17];
    Iterator var4 = items.iterator();

    while(var4.hasNext()) {
      ItemInstance item = (ItemInstance)var4.next();
      if (item.getEquipSlot() < 17) {
        this._paperdoll[item.getEquipSlot()] = item;
      }
    }

  }

  public int getObjectId() {
    return this._objectId;
  }

  public void setObjectId(int objectId) {
    this._objectId = objectId;
  }

  public int getCharId() {
    return this._charId;
  }

  public void setCharId(int charId) {
    this._charId = charId;
  }

  public int getClanId() {
    return this._clanId;
  }

  public void setClanId(int clanId) {
    this._clanId = clanId;
  }

  public int getClassId() {
    return this._classId;
  }

  public int getBaseClassId() {
    return this._baseClassId;
  }

  public void setBaseClassId(int baseClassId) {
    this._baseClassId = baseClassId;
  }

  public void setClassId(int classId) {
    this._classId = classId;
  }

  public double getCurrentHp() {
    return this._currentHp;
  }

  public void setCurrentHp(double currentHp) {
    this._currentHp = currentHp;
  }

  public double getCurrentMp() {
    return this._currentMp;
  }

  public void setCurrentMp(double currentMp) {
    this._currentMp = currentMp;
  }

  public int getDeleteTimer() {
    return this._deleteTimer;
  }

  public void setDeleteTimer(int deleteTimer) {
    this._deleteTimer = deleteTimer;
  }

  public long getLastAccess() {
    return this._lastAccess;
  }

  public void setLastAccess(long lastAccess) {
    this._lastAccess = lastAccess;
  }

  public long getExp() {
    return this._exp;
  }

  public void setExp(long exp) {
    this._exp = exp;
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

  public int getPaperdollObjectId(int slot) {
    ItemInstance item = this._paperdoll[slot];
    return item != null ? item.getObjectId() : 0;
  }

  public int getPaperdollAugmentationId(int slot) {
    ItemInstance item = this._paperdoll[slot];
    return item != null && item.isAugmented() ? item.getVariationStat1() & '\uffff' | item.getVariationStat2() << 16 : 0;
  }

  public int getPaperdollItemId(int slot) {
    ItemInstance item = this._paperdoll[slot];
    return item != null ? item.getItemId() : 0;
  }

  public int getPaperdollEnchantEffect(int slot) {
    ItemInstance item = this._paperdoll[slot];
    return item != null ? item.getEnchantLevel() : 0;
  }

  public int getLevel() {
    return this._level;
  }

  public void setLevel(int level) {
    this._level = level;
  }

  public int getMaxHp() {
    return this._maxHp;
  }

  public void setMaxHp(int maxHp) {
    this._maxHp = maxHp;
  }

  public int getMaxMp() {
    return this._maxMp;
  }

  public void setMaxMp(int maxMp) {
    this._maxMp = maxMp;
  }

  public String getName() {
    return this._name;
  }

  public void setName(String name) {
    this._name = name;
  }

  public int getRace() {
    return this._race;
  }

  public void setRace(int race) {
    this._race = race;
  }

  public int getSex() {
    return this._sex;
  }

  public void setSex(int sex) {
    this._sex = sex;
  }

  public int getSp() {
    return this._sp;
  }

  public void setSp(int sp) {
    this._sp = sp;
  }

  public int getKarma() {
    return this._karma;
  }

  public void setKarma(int karma) {
    this._karma = karma;
  }

  public int getAccessLevel() {
    return this._accesslevel;
  }

  public void setAccessLevel(int accesslevel) {
    this._accesslevel = accesslevel;
  }

  public int getX() {
    return this._x;
  }

  public void setX(int x) {
    this._x = x;
  }

  public int getY() {
    return this._y;
  }

  public void setY(int y) {
    this._y = y;
  }

  public int getZ() {
    return this._z;
  }

  public void setZ(int z) {
    this._z = z;
  }

  public int getPk() {
    return this._pk;
  }

  public void setPk(int pk) {
    this._pk = pk;
  }

  public int getPvP() {
    return this._pvp;
  }

  public void setPvP(int pvp) {
    this._pvp = pvp;
  }

  public int getVitalityPoints() {
    return this._vitalityPoints;
  }

  public void setVitalityPoints(int points) {
    this._vitalityPoints = points;
  }
}
