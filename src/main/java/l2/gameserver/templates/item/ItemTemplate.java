//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.templates.item;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

import java.util.Calendar;

public abstract class ItemTemplate extends StatTemplate {
  public static final int ITEM_ID_PC_BANG_POINTS = -100;
  public static final int ITEM_ID_CLAN_REPUTATION_SCORE = -200;
  public static final int ITEM_ID_FAME = -300;
  public static final int ITEM_ID_ADENA = 57;
  public static final int[] ITEM_ID_CASTLE_CIRCLET = new int[]{0, 6838, 6835, 6839, 6837, 6840, 6834, 6836, 8182, 8183};
  public static final int ITEM_ID_FORMAL_WEAR = 6408;
  public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
  public static final int TYPE1_SHIELD_ARMOR = 1;
  public static final int TYPE1_OTHER = 2;
  public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;
  public static final int TYPE2_WEAPON = 0;
  public static final int TYPE2_SHIELD_ARMOR = 1;
  public static final int TYPE2_ACCESSORY = 2;
  public static final int TYPE2_QUEST = 3;
  public static final int TYPE2_MONEY = 4;
  public static final int TYPE2_OTHER = 5;
  public static final int TYPE2_PET_WOLF = 6;
  public static final int TYPE2_PET_HATCHLING = 7;
  public static final int TYPE2_PET_STRIDER = 8;
  public static final int TYPE2_NODROP = 9;
  public static final int TYPE2_PET_GWOLF = 10;
  public static final int TYPE2_PENDANT = 11;
  public static final int TYPE2_PET_BABY = 12;
  public static final int SLOT_NONE = 0;
  public static final int SLOT_UNDERWEAR = 1;
  public static final int SLOT_R_EAR = 2;
  public static final int SLOT_L_EAR = 4;
  public static final int SLOT_NECK = 8;
  public static final int SLOT_R_FINGER = 16;
  public static final int SLOT_L_FINGER = 32;
  public static final int SLOT_HEAD = 64;
  public static final int SLOT_R_HAND = 128;
  public static final int SLOT_L_HAND = 256;
  public static final int SLOT_GLOVES = 512;
  public static final int SLOT_CHEST = 1024;
  public static final int SLOT_LEGS = 2048;
  public static final int SLOT_FEET = 4096;
  public static final int SLOT_BACK = 8192;
  public static final int SLOT_LR_HAND = 16384;
  public static final int SLOT_FULL_ARMOR = 32768;
  public static final int SLOT_HAIR = 65536;
  public static final int SLOT_FORMAL_WEAR = 131072;
  public static final int SLOT_DHAIR = 262144;
  public static final int SLOT_HAIRALL = 524288;
  public static final int SLOT_R_BRACELET = 1048576;
  public static final int SLOT_L_BRACELET = 2097152;
  public static final int SLOT_DECO = 4194304;
  public static final int SLOT_BELT = 268435456;
  public static final int SLOT_WOLF = -100;
  public static final int SLOT_HATCHLING = -101;
  public static final int SLOT_STRIDER = -102;
  public static final int SLOT_BABYPET = -103;
  public static final int SLOT_GWOLF = -104;
  public static final int SLOT_PENDANT = -105;
  public static final int SLOTS_ARMOR = 180032;
  public static final int SLOTS_JEWELRY = 62;
  public static final int CRYSTAL_NONE = 0;
  public static final int CRYSTAL_D = 1458;
  public static final int CRYSTAL_C = 1459;
  public static final int CRYSTAL_B = 1460;
  public static final int CRYSTAL_A = 1461;
  public static final int CRYSTAL_S = 1462;
  public static final int ATTRIBUTE_NONE = -2;
  public static final int ATTRIBUTE_FIRE = 0;
  public static final int ATTRIBUTE_WATER = 1;
  public static final int ATTRIBUTE_WIND = 2;
  public static final int ATTRIBUTE_EARTH = 3;
  public static final int ATTRIBUTE_HOLY = 4;
  public static final int ATTRIBUTE_DARK = 5;
  protected final int _itemId;
  private final ItemTemplate.ItemClass _class;
  protected final String _name;
  protected final String _addname;
  protected final String _icon;
  protected final String _icon32;
  protected int _type1;
  protected int _type2;
  private final int _weight;
  protected final ItemTemplate.Grade _crystalType;
  private final int _durability;
  protected int _bodyPart;
  private final int _referencePrice;
  private final int _crystalCount;
  private final boolean _temporal;
  private final boolean _stackable;
  private final boolean _crystallizable;
  private int _flags;
  private final ItemTemplate.ReuseType _reuseType;
  private final int _reuseDelay;
  private final int _reuseGroup;
  protected Skill[] _skills;
  private Skill _enchant4Skill = null;
  public ItemType type;
  private int[] _baseAttributes = new int[6];
  private IntObjectMap<int[]> _enchantOptions = Containers.emptyIntObjectMap();
  private Condition[] _conditions;
  private IItemHandler _handler;
  private boolean _isShotItem;
  private boolean _isStatDisabled;

  protected ItemTemplate(StatsSet set) {
    this._conditions = Condition.EMPTY_ARRAY;
    this._handler = IItemHandler.NULL;
    this._isStatDisabled = false;
    this._itemId = set.getInteger("item_id");
    this._class = set.getEnum("class", ItemClass.class, ItemClass.OTHER);
    this._name = set.getString("name");
    this._addname = set.getString("add_name", "");
    this._icon = set.getString("icon", "");
    this._icon32 = "<img src=" + this._icon + " width=32 height=32>";
    this._weight = set.getInteger("weight", 0);
    this._crystallizable = set.getBool("crystallizable", false);
    this._stackable = set.getBool("stackable", false);
    this._crystalType = set.getEnum("crystal_type", Grade.class, Grade.NONE);
    this._durability = set.getInteger("durability", -1);
    this._temporal = set.getBool("temporal", false);
    this._bodyPart = set.getInteger("bodypart", 0);
    this._referencePrice = set.getInteger("price", 0);
    this._crystalCount = set.getInteger("crystal_count", 0);
    this._reuseType = set.getEnum("reuse_type", ReuseType.class, ReuseType.NORMAL);
    this._reuseDelay = set.getInteger("reuse_delay", 0);
    this._reuseGroup = set.getInteger("delay_share_group", -this._itemId);
    ItemFlags[] var2 = ItemFlags.VALUES;

    for (ItemFlags f : var2) {
      boolean flag = set.getBool(f.name().toLowerCase(), f.getDefaultValue());
      if (this._name.contains("{PvP}")) {
        if (f == ItemFlags.TRADEABLE && Config.ALT_PVP_ITEMS_TREDABLE) {
          flag = true;
        }

        if (f == ItemFlags.ATTRIBUTABLE && Config.ALT_PVP_ITEMS_ATTRIBUTABLE) {
          flag = true;
        }

        if (f == ItemFlags.AUGMENTABLE && Config.ALT_PVP_ITEMS_AUGMENTABLE) {
          flag = true;
        }
      }

      if (flag) {
        this.activeFlag(f);
      }
    }

    this._funcTemplates = FuncTemplate.EMPTY_ARRAY;
    this._skills = Skill.EMPTY_ARRAY;
  }

  public ItemType getItemType() {
    return this.type;
  }

  public String getIcon() {
    return this._icon;
  }

  public String getIcon32() {
    return this._icon32;
  }

  public final int getDurability() {
    return this._durability;
  }

  public final boolean isTemporal() {
    return this._temporal;
  }

  public final int getItemId() {
    return this._itemId;
  }

  public abstract long getItemMask();

  public final int getType2() {
    return this._type2;
  }

  public final int getBaseAttributeValue(Element element) {
    return element == Element.NONE ? 0 : this._baseAttributes[element.getId()];
  }

  public void setBaseAtributeElements(int[] val) {
    this._baseAttributes = val;
  }

  public final int getType2ForPackets() {
    int type2 = this._type2;
    switch(this._type2) {
      case 6:
      case 7:
      case 8:
      case 10:
      case 12:
        if (this._bodyPart == 1024) {
          type2 = 1;
        } else {
          type2 = 0;
        }
      case 9:
      default:
        break;
      case 11:
        type2 = 2;
    }

    return type2;
  }

  public final int getWeight() {
    return this._weight;
  }

  public final boolean isCrystallizable() {
    return this._crystallizable && !this.isStackable() && this.getCrystalType() != ItemTemplate.Grade.NONE && this.getCrystalCount() > 0;
  }

  public final ItemTemplate.Grade getCrystalType() {
    return this._crystalType;
  }

  public final ItemTemplate.Grade getItemGrade() {
    return this.getCrystalType();
  }

  public final int getCrystalCount() {
    return this._crystalCount;
  }

  public final String getName() {
    return this._name;
  }

  public final String getAdditionalName() {
    return this._addname;
  }

  public final int getBodyPart() {
    return this._bodyPart;
  }

  public final int getType1() {
    return this._type1;
  }

  public final boolean isStackable() {
    return this._stackable;
  }

  public final int getReferencePrice() {
    return this._referencePrice;
  }

  public boolean isForHatchling() {
    return this._type2 == 7;
  }

  public boolean isForStrider() {
    return this._type2 == 8;
  }

  public boolean isForWolf() {
    return this._type2 == 6;
  }

  public boolean isForPetBaby() {
    return this._type2 == 12;
  }

  public boolean isForGWolf() {
    return this._type2 == 10;
  }

  public boolean isPendant() {
    return this._type2 == 11;
  }

  public boolean isForPet() {
    return this._type2 == 11 || this._type2 == 7 || this._type2 == 6 || this._type2 == 8 || this._type2 == 10 || this._type2 == 12;
  }

  public void attachSkill(Skill skill) {
    this._skills = ArrayUtils.add(this._skills, skill);
  }

  public Skill[] getAttachedSkills() {
    return this._skills;
  }

  public Skill getFirstSkill() {
    return this._skills.length > 0 ? this._skills[0] : null;
  }

  public Skill getEnchant4Skill() {
    return this._enchant4Skill;
  }

  public String toString() {
    return this._itemId + " " + this._name;
  }

  public boolean isShadowItem() {
    return this._durability > 0 && !this.isTemporal();
  }

  public boolean isSealedItem() {
    return this._name.startsWith("Sealed");
  }

  public boolean isAltSeed() {
    return this._name.contains("Alternative");
  }

  public ItemTemplate.ItemClass getItemClass() {
    return this._class;
  }

  public boolean isSealStone() {
    switch(this._itemId) {
      case 6360:
      case 6361:
      case 6362:
        return true;
      default:
        return false;
    }
  }

  public boolean isAdena() {
    return this._itemId == 57;
  }

  public boolean isEquipment() {
    return this._type1 != 4;
  }

  public boolean isKeyMatherial() {
    return this._class == ItemTemplate.ItemClass.PIECES;
  }

  public boolean isRecipe() {
    return this._class == ItemTemplate.ItemClass.RECIPIES;
  }

  public boolean isArrow() {
    return this.type == EtcItemType.ARROW;
  }

  public boolean isBelt() {
    return this._bodyPart == 268435456;
  }

  public boolean isBracelet() {
    return this._bodyPart == 1048576 || this._bodyPart == 2097152;
  }

  public boolean isUnderwear() {
    return this._bodyPart == 1;
  }

  public boolean isCloak() {
    return this._bodyPart == 8192;
  }

  public boolean isTalisman() {
    return this._bodyPart == 4194304;
  }

  public boolean isHerb() {
    return this.type == EtcItemType.HERB;
  }

  public boolean isAttributeCrystal() {
    return this._itemId == 9552 || this._itemId == 9553 || this._itemId == 9554 || this._itemId == 9555 || this._itemId == 9556 || this._itemId == 9557;
  }

  public boolean isAttributeEnergy() {
    return this._itemId >= 9564 && this._itemId <= 9569;
  }

  public boolean isHeroWeapon() {
    return this._itemId >= 6611 && this._itemId <= 6621 || this._itemId >= 9388 && this._itemId <= 9390;
  }

  public boolean isCursed() {
    return CursedWeaponsManager.getInstance().isCursed(this._itemId);
  }

  public boolean isMercenaryTicket() {
    return this.type == EtcItemType.MERCENARY_TICKET;
  }

  public boolean isRod() {
    return this.getItemType() == WeaponType.ROD;
  }

  public boolean isWeapon() {
    return this.getType2() == 0;
  }

  public boolean isArmor() {
    return this.getType2() == 1;
  }

  public boolean isAccessory() {
    return this.getType2() == 2;
  }

  public boolean isQuest() {
    return this.getType2() == 3;
  }

  public boolean isMageItem() {
    return false;
  }

  public boolean canBeEnchanted(@Deprecated boolean gradeCheck) {
    if (gradeCheck && this.getCrystalType() == ItemTemplate.Grade.NONE) {
      return false;
    } else if (this.isCursed()) {
      return false;
    } else {
      return !this.isQuest() && this.isEnchantable();
    }
  }

  public boolean isEquipable() {
    return this.getItemType() == EtcItemType.BAIT || this.getItemType() == EtcItemType.ARROW || this.getBodyPart() != 0 && !(this instanceof EtcItemTemplate);
  }

  public void setEnchant4Skill(Skill enchant4Skill) {
    this._enchant4Skill = enchant4Skill;
  }

  public boolean testCondition(Playable player, ItemInstance instance) {
    return this.testCondition(player, instance, true);
  }

  public boolean testCondition(Playable player, ItemInstance instance, boolean showMessage) {
    if (this.getConditions().length == 0) {
      return true;
    } else {
      Env env = new Env();
      env.character = player;
      env.item = instance;
      Condition[] var5 = this.getConditions();

      for (Condition con : var5) {
        if (!con.test(env)) {
          if (showMessage && con.getSystemMsg() != null) {
            if (con.getSystemMsg().size() > 0) {
              player.sendPacket((new SystemMessage2(con.getSystemMsg())).addItemName(this.getItemId()));
            } else {
              player.sendPacket(con.getSystemMsg());
            }
          }

          return false;
        }
      }

      return true;
    }
  }

  public void addCondition(Condition condition) {
    this._conditions = ArrayUtils.add(this._conditions, condition);
  }

  public Condition[] getConditions() {
    return this._conditions;
  }

  public boolean isEnchantable() {
    return this.hasFlag(ItemFlags.ENCHANTABLE);
  }

  public boolean isTradeable() {
    return this.hasFlag(ItemFlags.TRADEABLE);
  }

  public boolean isDestroyable() {
    return this.hasFlag(ItemFlags.DESTROYABLE);
  }

  public boolean isDropable() {
    return this.hasFlag(ItemFlags.DROPABLE);
  }

  public final boolean isSellable() {
    return this.hasFlag(ItemFlags.SELLABLE);
  }

  public final boolean isAugmentable() {
    return this.hasFlag(ItemFlags.AUGMENTABLE);
  }

  public final boolean isAttributable() {
    return this.hasFlag(ItemFlags.ATTRIBUTABLE);
  }

  public final boolean isStoreable() {
    return this.hasFlag(ItemFlags.STOREABLE);
  }

  public final boolean isFreightable() {
    return this.hasFlag(ItemFlags.FREIGHTABLE);
  }

  public boolean hasFlag(ItemFlags f) {
    return (this._flags & f.mask()) == f.mask();
  }

  private void activeFlag(ItemFlags f) {
    this._flags |= f.mask();
  }

  public IItemHandler getHandler() {
    return this._handler;
  }

  public void setHandler(IItemHandler handler) {
    this._handler = handler;
  }

  public boolean isShotItem() {
    return this._isShotItem;
  }

  public void setIsShotItem(boolean isShotItem) {
    this._isShotItem = isShotItem;
  }

  public int getReuseDelay() {
    return this._reuseDelay;
  }

  public int getReuseGroup() {
    return this._reuseGroup;
  }

  public int getDisplayReuseGroup() {
    return this._reuseGroup < 0 ? -1 : this._reuseGroup;
  }

  public void addEnchantOptions(int level, int[] options) {
    if (this._enchantOptions.isEmpty()) {
      this._enchantOptions = new HashIntObjectMap<>();
    }

    this._enchantOptions.put(level, options);
  }

  public IntObjectMap<int[]> getEnchantOptions() {
    return this._enchantOptions;
  }

  public ItemTemplate.ReuseType getReuseType() {
    return this._reuseType;
  }

  public void setStatDisabled(boolean val) {
    this._isStatDisabled = val;
  }

  public FuncTemplate[] getAttachedFuncs() {
    return this._isStatDisabled ? FuncTemplate.EMPTY_ARRAY : super.getAttachedFuncs();
  }

  public Func[] getStatFuncs(Object owner) {
    return this._isStatDisabled ? Func.EMPTY_FUNC_ARRAY : super.getStatFuncs(owner);
  }

  public enum Grade {
    NONE(0, 0),
    D(1458, 1),
    C(1459, 2),
    B(1460, 3),
    A(1461, 4),
    S(1462, 5);

    public final int cry;
    public final int externalOrdinal;

    Grade(int crystal, int ext) {
      this.cry = crystal;
      this.externalOrdinal = ext;
    }

    public int gradeOrd() {
      return this.externalOrdinal;
    }
  }

  public enum ItemClass {
    ALL,
    WEAPON,
    ARMOR,
    JEWELRY,
    ACCESSORY,
    CONSUMABLE,
    MATHERIALS,
    PIECES,
    RECIPIES,
    SPELLBOOKS,
    MISC,
    OTHER;

    ItemClass() {
    }
  }

  public enum ReuseType {
    NORMAL(SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME) {
      public long next(ItemInstance item) {
        return System.currentTimeMillis() + (long)item.getTemplate().getReuseDelay();
      }
    },
    EVERY_DAY_AT_6_30(SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_FOR_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1S_REUSE_TIME) {
      public long next(ItemInstance item) {
        Calendar nextTime = Calendar.getInstance();
        if (nextTime.get(11) > 6 || nextTime.get(11) == 6 && nextTime.get(12) >= 30) {
          nextTime.add(5, 1);
        }

        nextTime.set(11, 6);
        nextTime.set(12, 30);
        return nextTime.getTimeInMillis();
      }
    };

    private SystemMsg[] _messages;

    ReuseType(SystemMsg... msg) {
      this._messages = msg;
    }

    public abstract long next(ItemInstance var1);

    public SystemMsg[] getMessages() {
      return this._messages;
    }
  }
}
