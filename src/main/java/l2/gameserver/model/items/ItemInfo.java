//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.base.Element;
import l2.gameserver.templates.item.ItemTemplate;

public class ItemInfo {
  private int ownerId;
  private int lastChange;
  private int type1;
  private int objectId;
  private int itemId;
  private long count;
  private int type2;
  private int customType1;
  private boolean isEquipped;
  private int bodyPart;
  private int enchantLevel;
  private int customType2;
  private int variation_stat1;
  private int variation_stat2;
  private int shadowLifeTime;
  private int attackElement;
  private int attackElementValue;
  private int defenceFire;
  private int defenceWater;
  private int defenceWind;
  private int defenceEarth;
  private int defenceHoly;
  private int defenceUnholy;
  private int equipSlot;
  private int temporalLifeTime;
  private int[] enchantOptions;
  private ItemTemplate item;

  public ItemInfo() {
    this.attackElement = Element.NONE.getId();
    this.enchantOptions = ItemInstance.EMPTY_ENCHANT_OPTIONS;
  }

  public ItemInfo(ItemInstance item) {
    this.attackElement = Element.NONE.getId();
    this.enchantOptions = ItemInstance.EMPTY_ENCHANT_OPTIONS;
    this.setOwnerId(item.getOwnerId());
    this.setObjectId(item.getObjectId());
    this.setItemId(item.getItemId());
    this.setCount(item.getCount());
    this.setCustomType1(item.getBlessed());
    this.setEquipped(item.isEquipped());
    this.setEnchantLevel(item.getEnchantLevel());
    this.setCustomType2(item.getDamaged());
    this.setVariationStat1(item.getVariationStat1());
    this.setVariationStat2(item.getVariationStat2());
    this.setShadowLifeTime(item.getDuration());
    this.setAttackElement(item.getAttackElement().getId());
    this.setAttackElementValue(item.getAttackElementValue());
    this.setDefenceFire(item.getDefenceFire());
    this.setDefenceWater(item.getDefenceWater());
    this.setDefenceWind(item.getDefenceWind());
    this.setDefenceEarth(item.getDefenceEarth());
    this.setDefenceHoly(item.getDefenceHoly());
    this.setDefenceUnholy(item.getDefenceUnholy());
    this.setEquipSlot(item.getEquipSlot());
    this.setTemporalLifeTime(item.getPeriod());
    this.setEnchantOptions(item.getEnchantOptions());
  }

  public ItemTemplate getItem() {
    return this.item;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public void setLastChange(int lastChange) {
    this.lastChange = lastChange;
  }

  public void setType1(int type1) {
    this.type1 = type1;
  }

  public void setObjectId(int objectId) {
    this.objectId = objectId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
    if (itemId > 0) {
      this.item = ItemHolder.getInstance().getTemplate(this.getItemId());
    } else {
      this.item = null;
    }

    if (this.item != null) {
      this.setType1(this.item.getType1());
      this.setType2(this.item.getType2ForPackets());
      this.setBodyPart(this.item.getBodyPart());
    }

  }

  public void setCount(long count) {
    this.count = count;
  }

  public void setType2(int type2) {
    this.type2 = type2;
  }

  public void setCustomType1(int customType1) {
    this.customType1 = customType1;
  }

  public void setEquipped(boolean isEquipped) {
    this.isEquipped = isEquipped;
  }

  public void setBodyPart(int bodyPart) {
    this.bodyPart = bodyPart;
  }

  public void setEnchantLevel(int enchantLevel) {
    this.enchantLevel = enchantLevel;
  }

  public void setCustomType2(int customType2) {
    this.customType2 = customType2;
  }

  public void setVariationStat1(int var1) {
    this.variation_stat1 = var1;
  }

  public void setVariationStat2(int var2) {
    this.variation_stat2 = var2;
  }

  public void setShadowLifeTime(int shadowLifeTime) {
    this.shadowLifeTime = shadowLifeTime;
  }

  public void setAttackElement(int attackElement) {
    this.attackElement = attackElement;
  }

  public void setAttackElementValue(int attackElementValue) {
    this.attackElementValue = attackElementValue;
  }

  public void setDefenceFire(int defenceFire) {
    this.defenceFire = defenceFire;
  }

  public void setDefenceWater(int defenceWater) {
    this.defenceWater = defenceWater;
  }

  public void setDefenceWind(int defenceWind) {
    this.defenceWind = defenceWind;
  }

  public void setDefenceEarth(int defenceEarth) {
    this.defenceEarth = defenceEarth;
  }

  public void setDefenceHoly(int defenceHoly) {
    this.defenceHoly = defenceHoly;
  }

  public void setDefenceUnholy(int defenceUnholy) {
    this.defenceUnholy = defenceUnholy;
  }

  public void setEquipSlot(int equipSlot) {
    this.equipSlot = equipSlot;
  }

  public void setTemporalLifeTime(int temporalLifeTime) {
    this.temporalLifeTime = temporalLifeTime;
  }

  public int getOwnerId() {
    return this.ownerId;
  }

  public int getLastChange() {
    return this.lastChange;
  }

  public int getType1() {
    return this.type1;
  }

  public int getObjectId() {
    return this.objectId;
  }

  public int getItemId() {
    return this.itemId;
  }

  public long getCount() {
    return this.count;
  }

  public int getType2() {
    return this.type2;
  }

  public int getCustomType1() {
    return this.customType1;
  }

  public boolean isEquipped() {
    return this.isEquipped;
  }

  public int getBodyPart() {
    return this.bodyPart;
  }

  public int getEnchantLevel() {
    return this.enchantLevel;
  }

  public int getVariationStat1() {
    return this.variation_stat1;
  }

  public int getVariationStat2() {
    return this.variation_stat2;
  }

  public int getShadowLifeTime() {
    return this.shadowLifeTime;
  }

  public int getCustomType2() {
    return this.customType2;
  }

  public int getAttackElement() {
    return this.attackElement;
  }

  public int getAttackElementValue() {
    return this.attackElementValue;
  }

  public int getDefenceFire() {
    return this.defenceFire;
  }

  public int getDefenceWater() {
    return this.defenceWater;
  }

  public int getDefenceWind() {
    return this.defenceWind;
  }

  public int getDefenceEarth() {
    return this.defenceEarth;
  }

  public int getDefenceHoly() {
    return this.defenceHoly;
  }

  public int getDefenceUnholy() {
    return this.defenceUnholy;
  }

  public int getEquipSlot() {
    return this.equipSlot;
  }

  public int getTemporalLifeTime() {
    return this.temporalLifeTime;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (this.getClass() != obj.getClass()) {
      return false;
    } else if (this.getObjectId() == 0) {
      return this.getItemId() == ((ItemInfo)obj).getItemId();
    } else {
      return this.getObjectId() == ((ItemInfo)obj).getObjectId();
    }
  }

  public int[] getEnchantOptions() {
    return this.enchantOptions;
  }

  public void setEnchantOptions(int[] enchantOptions) {
    this.enchantOptions = enchantOptions;
  }
}
