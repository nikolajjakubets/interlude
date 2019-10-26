//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates;

public class SkillEnchant {
  private final int _skillId;
  private final int _skillLevel;
  private final int _enchantLevel;
  private final int _routeId;
  private final long _exp;
  private final int _sp;
  private final int[] _chances;
  private final int _itemId;
  private final long _itemCount;

  public SkillEnchant(int skillId, int skillLevel, int enchantLevel, int routeId, long exp, int sp, int[] chances, int itemId, long itemCount) {
    this._skillId = skillId;
    this._skillLevel = skillLevel;
    this._enchantLevel = enchantLevel;
    this._routeId = routeId;
    this._exp = exp;
    this._sp = sp;
    this._chances = chances;
    this._itemId = itemId;
    this._itemCount = itemCount;
  }

  public int[] getChances() {
    return this._chances;
  }

  public long getExp() {
    return this._exp;
  }

  public long getItemCount() {
    return this._itemCount;
  }

  public int getItemId() {
    return this._itemId;
  }

  public int getEnchantLevel() {
    return this._enchantLevel;
  }

  public int getRouteId() {
    return this._routeId;
  }

  public int getSkillId() {
    return this._skillId;
  }

  public int getSkillLevel() {
    return this._skillLevel;
  }

  public int getSp() {
    return this._sp;
  }
}
