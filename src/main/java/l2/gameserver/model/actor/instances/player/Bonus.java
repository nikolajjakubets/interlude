//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.actor.instances.player;

public class Bonus {
  public static final float DEFAULT_RATE_XP = 1.0F;
  public static final float DEFAULT_RATE_SP = 1.0F;
  public static final float DEFAULT_QUEST_REWARD_RATE = 1.0F;
  public static final float DEFAULT_QUEST_DROP_RATE = 1.0F;
  public static final float DEFAULT_DROP_ADENA = 1.0F;
  public static final float DEFAULT_DROP_ITEMS = 1.0F;
  public static final float DEFAULT_DROP_RAID_ITEMS = 1.0F;
  public static final float DEFAULT_DROP_SPOIL = 1.0F;
  public static final float DEFAULT_ENCHANT_ITEM = 1.0F;
  private float rateXp = 1.0F;
  private float rateSp = 1.0F;
  private float questRewardRate = 1.0F;
  private float questDropRate = 1.0F;
  private float dropAdena = 1.0F;
  private float dropItems = 1.0F;
  private float dropRaidItems = 1.0F;
  private float dropSpoil = 1.0F;
  private float enchantItem = 1.0F;
  private long bonusExpire = 0L;

  public Bonus() {
  }

  public void reset() {
    this.setRateXp(1.0F);
    this.setRateSp(1.0F);
    this.setQuestRewardRate(1.0F);
    this.setQuestDropRate(1.0F);
    this.setDropAdena(1.0F);
    this.setDropItems(1.0F);
    this.setDropRaidItems(1.0F);
    this.setDropSpoil(1.0F);
    this.bonusExpire = 0L;
  }

  public float getRateXp() {
    return this.rateXp;
  }

  public void setRateXp(float rateXp) {
    this.rateXp = rateXp;
  }

  public float getRateSp() {
    return this.rateSp;
  }

  public void setRateSp(float rateSp) {
    this.rateSp = rateSp;
  }

  public float getQuestRewardRate() {
    return this.questRewardRate;
  }

  public void setQuestRewardRate(float questRewardRate) {
    this.questRewardRate = questRewardRate;
  }

  public float getQuestDropRate() {
    return this.questDropRate;
  }

  public void setQuestDropRate(float questDropRate) {
    this.questDropRate = questDropRate;
  }

  public float getDropAdena() {
    return this.dropAdena;
  }

  public void setDropAdena(float dropAdena) {
    this.dropAdena = dropAdena;
  }

  public float getDropItems() {
    return this.dropItems;
  }

  public void setDropItems(float dropItems) {
    this.dropItems = dropItems;
  }

  public float getDropRaidItems() {
    return this.dropRaidItems;
  }

  public void setDropRaidItems(float dropRaidItems) {
    this.dropRaidItems = dropRaidItems;
  }

  public float getDropSpoil() {
    return this.dropSpoil;
  }

  public void setDropSpoil(float dropSpoil) {
    this.dropSpoil = dropSpoil;
  }

  public float getEnchantItemMul() {
    return this.enchantItem;
  }

  public void setEnchantItem(float enchantItem) {
    this.enchantItem = enchantItem;
  }

  public long getBonusExpire() {
    return this.bonusExpire;
  }

  public void setBonusExpire(long bonusExpire) {
    this.bonusExpire = bonusExpire;
  }
}
