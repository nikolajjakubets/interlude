//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.collections.LazyArrayList;
import l2.commons.util.concurrent.atomic.AtomicEnumBitFlag;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.attachment.ItemAttachment;
import l2.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2.gameserver.network.l2.s2c.DropItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SpawnItem;
import l2.gameserver.scripts.Events;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.taskmanager.ItemsAutoDestroy;
import l2.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemType;
import l2.gameserver.templates.item.ItemTemplate.Grade;
import l2.gameserver.templates.item.ItemTemplate.ItemClass;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public final class ItemInstance extends GameObject {
  public static final int[] EMPTY_ENCHANT_OPTIONS = new int[3];
  private static final long serialVersionUID = 3162753878915133228L;
  public static final long MAX_AMMOUNT = 2147483647L;
  private static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();
  public static final int CHARGED_NONE = 0;
  public static final int CHARGED_SOULSHOT = 1;
  public static final int CHARGED_SPIRITSHOT = 1;
  public static final int CHARGED_BLESSED_SPIRITSHOT = 2;
  public static final int FLAG_NO_DROP = 1;
  public static final int FLAG_NO_TRADE = 2;
  public static final int FLAG_NO_TRANSFER = 4;
  public static final int FLAG_NO_CRYSTALLIZE = 8;
  public static final int FLAG_NO_ENCHANT = 16;
  public static final int FLAG_NO_DESTROY = 32;
  private ItemAttributes attrs = new ItemAttributes();
  private int[] _enchantOptions;
  private int _owner_id;
  private int _item_id;
  private long _ammount;
  private ItemInstance.ItemLocation _location;
  private int _slot;
  private int _enchant;
  private int _duaration;
  private int _period;
  private int _variation_stat1;
  private int _variation_stat2;
  private int _blessed;
  private int _damaged;
  private int _cflags;
  private int _visItemId;
  private AtomicEnumBitFlag<ItemStateFlags> _stateFlags;
  private ItemTemplate template;
  private boolean isEquipped;
  private long _dropTime;
  private IntSet _dropPlayers;
  private long _dropTimeOwner;
  private int _chargedSoulshot;
  private int _chargedSpiritshot;
  private boolean _chargedFishtshot;
  private ItemAttachment _attachment;
  private ScheduledFuture<?> _timerTask;

  public ItemInstance(int objectId) {
    super(objectId);
    this._enchantOptions = EMPTY_ENCHANT_OPTIONS;
    this._duaration = -1;
    this._period = -9999;
    this._stateFlags = new AtomicEnumBitFlag();
    this._dropPlayers = Containers.EMPTY_INT_SET;
    this._chargedSoulshot = 0;
    this._chargedSpiritshot = 0;
    this._chargedFishtshot = false;
  }

  public ItemInstance(int objectId, int itemId) {
    super(objectId);
    this._enchantOptions = EMPTY_ENCHANT_OPTIONS;
    this._duaration = -1;
    this._period = -9999;
    this._stateFlags = new AtomicEnumBitFlag();
    this._dropPlayers = Containers.EMPTY_INT_SET;
    this._chargedSoulshot = 0;
    this._chargedSpiritshot = 0;
    this._chargedFishtshot = false;
    this.setItemId(itemId);
    this.setDuration(this.getTemplate().getDurability());
    this.setPeriodBegin(this.getTemplate().isTemporal() ? (int)(System.currentTimeMillis() / 1000L) + this.getTemplate().getDurability() * 60 : -9999);
    this.setLocData(-1);
    this.setEnchantLevel(0);
  }

  public int getOwnerId() {
    return this._owner_id;
  }

  public void setOwnerId(int ownerId) {
    if (this._owner_id != ownerId) {
      this._owner_id = ownerId;
      this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }
  }

  public int getItemId() {
    return this._item_id;
  }

  public void setVisibleItemId(int visItemId) {
    this._visItemId = visItemId;
  }

  public int getVisibleItemId() {
    return this._visItemId > 0 ? this._visItemId : this.getItemId();
  }

  public void setItemId(int id) {
    this._item_id = id;
    this.template = ItemHolder.getInstance().getTemplate(id);
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public long getCount() {
    return this._ammount;
  }

  public AtomicEnumBitFlag<ItemStateFlags> getItemStateFlag() {
    return this._stateFlags;
  }

  public void setCount(long count) {
    if (count < 0L) {
      count = 0L;
    }

    if (this.isStackable() && count > 2147483647L) {
      this._ammount = 2147483647L;
      this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    } else if (!this.isStackable() && count > 1L) {
      this._ammount = 1L;
      this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    } else if (this._ammount != count) {
      this._ammount = count;
      this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }
  }

  public int getEnchantLevel() {
    return this._enchant;
  }

  public void setEnchantLevel(int enchantLevel) {
    int old = this._enchant;
    this._enchant = enchantLevel;
    if (old != this._enchant && this.getTemplate().getEnchantOptions().size() > 0) {
      Player player = GameObjectsStorage.getPlayer(this.getOwnerId());
      if (this.isEquipped() && player != null) {
        ItemEnchantOptionsListener.getInstance().onUnequip(this.getEquipSlot(), this, player);
      }

      int[] enchantOptions = (int[])this.getTemplate().getEnchantOptions().get(this._enchant);
      this._enchantOptions = enchantOptions == null ? EMPTY_ENCHANT_OPTIONS : enchantOptions;
      if (this.isEquipped() && player != null) {
        ItemEnchantOptionsListener.getInstance().onEquip(this.getEquipSlot(), this, player);
      }
    }

    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public void setLocName(String loc) {
    this.setLocation(ItemInstance.ItemLocation.valueOf(loc));
  }

  public String getLocName() {
    return this._location.name();
  }

  public void setLocation(ItemInstance.ItemLocation loc) {
    if (this._location != loc) {
      this._location = loc;
      this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }
  }

  public ItemInstance.ItemLocation getLocation() {
    return this._location;
  }

  public void setLocData(int slot) {
    if (this._slot != slot) {
      this._slot = slot;
      this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }
  }

  public int getLocData() {
    return this._slot;
  }

  public int getBlessed() {
    return this._blessed;
  }

  public void setBlessed(int val) {
    this._blessed = val;
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public int getDamaged() {
    return this._damaged;
  }

  public void setDamaged(int val) {
    this._damaged = val;
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public int getCustomFlags() {
    return this._cflags;
  }

  public void setCustomFlags(int flags) {
    if (this._cflags != flags) {
      this._cflags = flags;
      this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
    }
  }

  public ItemAttributes getAttributes() {
    return this.attrs;
  }

  public void setAttributes(ItemAttributes attrs) {
    this.attrs = attrs;
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public int getDuration() {
    return !this.isShadowItem() ? -1 : this._duaration;
  }

  public void setDuration(int duration) {
    this._duaration = duration;
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public int getPeriod() {
    return !this.isTemporalItem() ? -9999 : this._period - (int)(System.currentTimeMillis() / 1000L);
  }

  public int getPeriodBegin() {
    return !this.isTemporalItem() ? -9999 : this._period;
  }

  public void setPeriodBegin(int period) {
    this._period = period;
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public void startTimer(Runnable r) {
    this._timerTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(r, 0L, 60000L);
  }

  public void stopTimer() {
    if (this._timerTask != null) {
      this._timerTask.cancel(false);
      this._timerTask = null;
    }

  }

  public boolean isEquipable() {
    return this.template.isEquipable();
  }

  public boolean isEquipped() {
    return this.isEquipped;
  }

  public void setEquipped(boolean isEquipped) {
    this.isEquipped = isEquipped;
  }

  public int getBodyPart() {
    return this.template.getBodyPart();
  }

  public int getEquipSlot() {
    return this.getLocData();
  }

  public ItemTemplate getTemplate() {
    return this.template;
  }

  public void setDropTime(long time) {
    this._dropTime = time;
  }

  public long getLastDropTime() {
    return this._dropTime;
  }

  public long getDropTimeOwner() {
    return this._dropTimeOwner;
  }

  public ItemType getItemType() {
    return this.template.getItemType();
  }

  public boolean isArmor() {
    return this.template.isArmor();
  }

  public boolean isAccessory() {
    return this.template.isAccessory();
  }

  public boolean isWeapon() {
    return this.template.isWeapon();
  }

  public int getReferencePrice() {
    return this.template.getReferencePrice();
  }

  public boolean isStackable() {
    return this.template.isStackable();
  }

  public void onAction(Player player, boolean shift) {
    if (!Events.onAction(player, this, shift)) {
      if (!player.isCursedWeaponEquipped() || !CursedWeaponsManager.getInstance().isCursed(this.getItemId())) {
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, this, (Object)null);
      }
    }
  }

  public int getActingRange() {
    return 16;
  }

  public boolean isAugmented() {
    return this.getVariationStat1() != 0 || this.getVariationStat2() != 0;
  }

  public int getVariationStat1() {
    return this._variation_stat1;
  }

  public int getVariationStat2() {
    return this._variation_stat2;
  }

  public void setVariationStat1(int stat) {
    this._variation_stat1 = stat;
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public void setVariationStat2(int stat) {
    this._variation_stat2 = stat;
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public int getChargedSoulshot() {
    return this._chargedSoulshot;
  }

  public int getChargedSpiritshot() {
    return this._chargedSpiritshot;
  }

  public boolean getChargedFishshot() {
    return this._chargedFishtshot;
  }

  public void setChargedSoulshot(int type) {
    this._chargedSoulshot = type;
  }

  public void setChargedSpiritshot(int type) {
    this._chargedSpiritshot = type;
  }

  public void setChargedFishshot(boolean type) {
    this._chargedFishtshot = type;
  }

  public Func[] getStatFuncs() {
    Func[] result = Func.EMPTY_FUNC_ARRAY;
    LazyArrayList<Func> funcs = LazyArrayList.newInstance();
    int var4;
    int var5;
    if (this.template.getAttachedFuncs().length > 0) {
      FuncTemplate[] var3 = this.template.getAttachedFuncs();
      var4 = var3.length;

      for(var5 = 0; var5 < var4; ++var5) {
        FuncTemplate t = var3[var5];
        Func f = t.getFunc(this);
        if (f != null) {
          funcs.add(f);
        }
      }
    }

    Element[] var8 = Element.VALUES;
    var4 = var8.length;

    for(var5 = 0; var5 < var4; ++var5) {
      Element e = var8[var5];
      if (this.isWeapon()) {
        funcs.add(new ItemInstance.FuncAttack(e, 64, this));
      }

      if (this.isArmor()) {
        funcs.add(new ItemInstance.FuncDefence(e, 64, this));
      }
    }

    if (!funcs.isEmpty()) {
      result = (Func[])funcs.toArray(new Func[funcs.size()]);
    }

    LazyArrayList.recycle(funcs);
    return result;
  }

  public boolean isHeroWeapon() {
    return this.template.isHeroWeapon();
  }

  public boolean canBeDestroyed(Player player) {
    if ((this.getCustomFlags() & 32) == 32) {
      return false;
    } else if (this.isHeroWeapon()) {
      return false;
    } else if (PetDataTable.isPetControlItem(this) && player.isMounted()) {
      return false;
    } else if (player.getPetControlItem() == this) {
      return false;
    } else if (player.getEnchantScroll() == this) {
      return false;
    } else {
      return this.isCursed() ? false : this.template.isDestroyable();
    }
  }

  public boolean canBeDropped(Player player, boolean pk) {
    if (player.getPlayerAccess().CanDropAnyItems) {
      return true;
    } else if ((this.getCustomFlags() & 1) == 1) {
      return false;
    } else if (this.isShadowItem()) {
      return false;
    } else if (this.isTemporalItem()) {
      return false;
    } else if (this.isAugmented() && (!pk || !Config.DROP_ITEMS_AUGMENTED) && !Config.ALT_ALLOW_DROP_AUGMENTED) {
      return false;
    } else {
      return !ItemFunctions.checkIfCanDiscard(player, this) ? false : this.template.isDropable();
    }
  }

  public boolean canBeTraded(Player player) {
    if (this.isEquipped()) {
      return false;
    } else if (player.getPlayerAccess().CanTradeAnyItem) {
      return true;
    } else if ((this.getCustomFlags() & 2) == 2) {
      return false;
    } else if (this.isShadowItem()) {
      return false;
    } else if (this.isTemporalItem()) {
      return false;
    } else if (this.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) {
      return false;
    } else {
      return !ItemFunctions.checkIfCanDiscard(player, this) ? false : this.template.isTradeable();
    }
  }

  public boolean canBeSold(Player player) {
    if ((this.getCustomFlags() & 32) == 32) {
      return false;
    } else if (this.getItemId() == 57) {
      return false;
    } else if (this.template.getReferencePrice() == 0) {
      return false;
    } else if (this.isShadowItem()) {
      return false;
    } else if (this.isTemporalItem()) {
      return false;
    } else if (this.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) {
      return false;
    } else if (this.isEquipped()) {
      return false;
    } else {
      return !ItemFunctions.checkIfCanDiscard(player, this) ? false : this.template.isSellable();
    }
  }

  public boolean canBeStored(Player player, boolean privatewh) {
    if ((this.getCustomFlags() & 4) == 4) {
      return false;
    } else if (!this.getTemplate().isStoreable()) {
      return false;
    } else if (!privatewh && (this.isShadowItem() || this.isTemporalItem())) {
      return false;
    } else if (!privatewh && this.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) {
      return false;
    } else if (this.isEquipped()) {
      return false;
    } else if (!ItemFunctions.checkIfCanDiscard(player, this)) {
      return false;
    } else {
      return privatewh || this.template.isTradeable();
    }
  }

  public boolean canBeCrystallized(Player player) {
    if ((this.getCustomFlags() & 8) == 8) {
      return false;
    } else if (this.isShadowItem()) {
      return false;
    } else if (this.isTemporalItem()) {
      return false;
    } else {
      return !ItemFunctions.checkIfCanDiscard(player, this) ? false : this.template.isCrystallizable();
    }
  }

  public boolean canBeEnchanted(boolean gradeCheck) {
    return (this.getCustomFlags() & 16) == 16 ? false : this.template.canBeEnchanted(gradeCheck);
  }

  public boolean canBeExchanged(Player player) {
    if ((this.getCustomFlags() & 32) == 32) {
      return false;
    } else if (this.isShadowItem()) {
      return false;
    } else if (this.isTemporalItem()) {
      return false;
    } else {
      return !ItemFunctions.checkIfCanDiscard(player, this) ? false : this.template.isDestroyable();
    }
  }

  public boolean isShadowItem() {
    return this.template.isShadowItem();
  }

  public boolean isTemporalItem() {
    return this.template.isTemporal();
  }

  public boolean isAltSeed() {
    return this.template.isAltSeed();
  }

  public boolean isCursed() {
    return this.template.isCursed();
  }

  public void dropToTheGround(Player lastAttacker, NpcInstance fromNpc) {
    Creature dropper = fromNpc;
    if (fromNpc == null) {
      dropper = lastAttacker;
    }

    Location pos = Location.findAroundPosition((GameObject)dropper, 128);
    if (lastAttacker != null) {
      this._dropPlayers = new HashIntSet(1, 2.0F);
      Iterator var5 = lastAttacker.getPlayerGroup().iterator();

      while(var5.hasNext()) {
        Player $member = (Player)var5.next();
        this._dropPlayers.add($member.getObjectId());
      }

      this._dropTimeOwner = System.currentTimeMillis();
      if (fromNpc != null && fromNpc.isRaid()) {
        this._dropTimeOwner += Config.NONOWNER_ITEM_PICKUP_DELAY_RAID;
      } else {
        this._dropTimeOwner += Config.NONOWNER_ITEM_PICKUP_DELAY;
      }
    }

    this.dropMe((Creature)dropper, pos);
    if (this.isHerb()) {
      ItemsAutoDestroy.getInstance().addHerb(this);
    } else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !this.isCursed()) {
      ItemsAutoDestroy.getInstance().addItem(this);
    }

  }

  public void dropToTheGround(Creature dropper, Location dropPos) {
    if (GeoEngine.canMoveToCoord(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, dropper.getGeoIndex())) {
      this.dropMe(dropper, dropPos);
    } else {
      this.dropMe(dropper, dropper.getLoc());
    }

    if (this.isHerb()) {
      ItemsAutoDestroy.getInstance().addHerb(this);
    } else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !this.isCursed()) {
      ItemsAutoDestroy.getInstance().addItem(this);
    }

  }

  public void dropToTheGround(Playable dropper, Location dropPos) {
    this.setLocation(ItemInstance.ItemLocation.VOID);
    this.save();
    if (GeoEngine.canMoveToCoord(dropper.getX(), dropper.getY(), dropper.getZ(), dropPos.x, dropPos.y, dropPos.z, dropper.getGeoIndex())) {
      this.dropMe(dropper, dropPos);
    } else {
      this.dropMe(dropper, dropper.getLoc());
    }

    if (this.isHerb()) {
      ItemsAutoDestroy.getInstance().addHerb(this);
    } else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !this.isCursed()) {
      ItemsAutoDestroy.getInstance().addItem(this);
    }

  }

  public void dropMe(Creature dropper, Location loc) {
    if (dropper != null) {
      this.setReflection(dropper.getReflection());
    }

    this.spawnMe0(loc, dropper);
  }

  public final void pickupMe() {
    this.decayMe();
    this.setReflection(ReflectionManager.DEFAULT);
  }

  public ItemClass getItemClass() {
    return this.template.getItemClass();
  }

  private int getDefence(Element element) {
    return this.isArmor() ? this.getAttributeElementValue(element, true) : 0;
  }

  public int getDefenceFire() {
    return this.getDefence(Element.FIRE);
  }

  public int getDefenceWater() {
    return this.getDefence(Element.WATER);
  }

  public int getDefenceWind() {
    return this.getDefence(Element.WIND);
  }

  public int getDefenceEarth() {
    return this.getDefence(Element.EARTH);
  }

  public int getDefenceHoly() {
    return this.getDefence(Element.HOLY);
  }

  public int getDefenceUnholy() {
    return this.getDefence(Element.UNHOLY);
  }

  public int getAttributeElementValue(Element element, boolean withBase) {
    return this.attrs.getValue(element) + (withBase ? this.template.getBaseAttributeValue(element) : 0);
  }

  public Element getAttributeElement() {
    return this.attrs.getElement();
  }

  public int getAttributeElementValue() {
    return this.attrs.getValue();
  }

  public Element getAttackElement() {
    Element element = this.isWeapon() ? this.getAttributeElement() : Element.NONE;
    if (element == Element.NONE) {
      Element[] var2 = Element.VALUES;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Element e = var2[var4];
        if (this.template.getBaseAttributeValue(e) > 0) {
          return e;
        }
      }
    }

    return element;
  }

  public int getAttackElementValue() {
    return this.isWeapon() ? this.getAttributeElementValue(this.getAttackElement(), true) : 0;
  }

  public void setAttributeElement(Element element, int value) {
    this.attrs.setValue(element, value);
    this.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, true);
  }

  public boolean isHerb() {
    return this.getTemplate().isHerb();
  }

  public Grade getCrystalType() {
    return this.template.getCrystalType();
  }

  public String getName() {
    return this.getTemplate().getName();
  }

  public void save() {
    _itemsDAO.store(this);
  }

  public void delete() {
    _itemsDAO.delete(this);
  }

  public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
    L2GameServerPacket packet = null;
    if (dropper != null) {
      packet = new DropItem(this, dropper.getObjectId());
    } else {
      packet = new SpawnItem(this);
    }

    return Collections.singletonList(packet);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getTemplate().getItemId());
    sb.append(" ");
    if (this.getEnchantLevel() > 0) {
      sb.append("+");
      sb.append(this.getEnchantLevel());
      sb.append(" ");
    }

    sb.append(this.getTemplate().getName());
    if (!this.getTemplate().getAdditionalName().isEmpty()) {
      sb.append(" ");
      sb.append("\\").append(this.getTemplate().getAdditionalName()).append("\\");
    }

    sb.append(" ");
    sb.append("(");
    sb.append(this.getCount());
    sb.append(")");
    sb.append("[");
    sb.append(this.getObjectId());
    sb.append("]");
    return sb.toString();
  }

  public boolean isItem() {
    return true;
  }

  public ItemAttachment getAttachment() {
    return this._attachment;
  }

  public void setAttachment(ItemAttachment attachment) {
    ItemAttachment old = this._attachment;
    this._attachment = attachment;
    if (this._attachment != null) {
      this._attachment.setItem(this);
    }

    if (old != null) {
      old.setItem((ItemInstance)null);
    }

  }

  public int[] getEnchantOptions() {
    return this._enchantOptions;
  }

  public IntSet getDropPlayers() {
    return this._dropPlayers;
  }

  public class FuncDefence extends Func {
    private final Element element;

    public FuncDefence(Element element, int order, Object owner) {
      super(element.getDefence(), order, owner);
      this.element = element;
    }

    public void calc(Env env) {
      env.value += (double)ItemInstance.this.getAttributeElementValue(this.element, true);
    }
  }

  public class FuncAttack extends Func {
    private final Element element;

    public FuncAttack(Element element, int order, Object owner) {
      super(element.getAttack(), order, owner);
      this.element = element;
    }

    public void calc(Env env) {
      env.value += (double)ItemInstance.this.getAttributeElementValue(this.element, true);
    }
  }

  public static enum ItemLocation {
    VOID,
    INVENTORY,
    PAPERDOLL,
    PET_INVENTORY,
    PET_PAPERDOLL,
    WAREHOUSE,
    CLANWH,
    FREIGHT,
    /** @deprecated */
    @Deprecated
    LEASE,
    MAIL;

    private ItemLocation() {
    }
  }
}
