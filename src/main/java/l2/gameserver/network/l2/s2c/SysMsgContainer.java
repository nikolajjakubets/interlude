//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.utils.Location;

public abstract class SysMsgContainer<T extends SysMsgContainer<T>> extends L2GameServerPacket {
  protected SystemMsg _message;
  protected List<SysMsgContainer.IArgument> _arguments;

  /** @deprecated */
  @Deprecated
  protected SysMsgContainer(int messageId) {
    this(SystemMsg.valueOf(messageId));
  }

  protected SysMsgContainer(SystemMsg message) {
    if (message == null) {
      throw new IllegalArgumentException("SystemMsg is null");
    } else {
      this._message = message;
      this._arguments = new ArrayList(this._message.size());
    }
  }

  protected void writeElements() {
    if (this._message.size() != this._arguments.size()) {
      throw new IllegalArgumentException("Wrong count of arguments: " + this._message);
    } else {
      this.writeD(this._message.getId());
      this.writeD(this._arguments.size());
      Iterator var1 = this._arguments.iterator();

      while(var1.hasNext()) {
        SysMsgContainer.IArgument argument = (SysMsgContainer.IArgument)var1.next();
        argument.write(this);
      }

    }
  }

  public T addName(GameObject object) {
    if (object == null) {
      return this.add(new SysMsgContainer.StringArgument((String)null));
    } else if (object.isNpc()) {
      return this.add(new SysMsgContainer.NpcNameArgument(((NpcInstance)object).getNpcId() + 1000000));
    } else if (object instanceof Summon) {
      return this.add(new SysMsgContainer.NpcNameArgument(((Summon)object).getNpcId() + 1000000));
    } else if (object.isItem()) {
      return this.add(new SysMsgContainer.ItemNameArgument(((ItemInstance)object).getItemId()));
    } else if (object.isPlayer()) {
      return this.add(new SysMsgContainer.PlayerNameArgument((Player)object));
    } else if (object.isDoor()) {
      return this.add(new SysMsgContainer.StaticObjectNameArgument(((DoorInstance)object).getDoorId()));
    } else {
      return object instanceof StaticObjectInstance ? this.add(new SysMsgContainer.StaticObjectNameArgument(((StaticObjectInstance)object).getUId())) : this.add(new SysMsgContainer.StringArgument(object.getName()));
    }
  }

  public T addInstanceName(int id) {
    return this.add(new SysMsgContainer.InstanceNameArgument(id));
  }

  public T addSysString(int id) {
    return this.add(new SysMsgContainer.SysStringArgument(id));
  }

  public T addSkillName(Skill skill) {
    return this.addSkillName(skill.getDisplayId(), skill.getDisplayLevel());
  }

  public T addSkillName(int id, int level) {
    return this.add(new SysMsgContainer.SkillArgument(id, level));
  }

  public T addItemName(int item_id) {
    return this.add(new SysMsgContainer.ItemNameArgument(item_id));
  }

  /** @deprecated */
  @Deprecated
  public T addItemNameWithAugmentation(ItemInstance item) {
    return this.add(new SysMsgContainer.ItemNameWithAugmentationArgument(item.getItemId(), item.getVariationStat1(), item.getVariationStat2()));
  }

  public T addZoneName(Creature c) {
    return this.addZoneName(c.getX(), c.getY(), c.getZ());
  }

  public T addZoneName(Location loc) {
    return this.add(new SysMsgContainer.ZoneArgument(loc.x, loc.y, loc.z));
  }

  public T addZoneName(int x, int y, int z) {
    return this.add(new SysMsgContainer.ZoneArgument(x, y, z));
  }

  public T addResidenceName(Residence r) {
    return this.add(new SysMsgContainer.ResidenceArgument(r.getId()));
  }

  public T addResidenceName(int i) {
    return this.add(new SysMsgContainer.ResidenceArgument(i));
  }

  public T addElementName(int i) {
    return this.add(new SysMsgContainer.ElementNameArgument(i));
  }

  public T addElementName(Element i) {
    return this.add(new SysMsgContainer.ElementNameArgument(i.getId()));
  }

  public T addInteger(double i) {
    return this.add(new SysMsgContainer.IntegerArgument((int)i));
  }

  public T addLong(long i) {
    return this.add(new SysMsgContainer.LongArgument(i));
  }

  public T addString(String t) {
    return this.add(new SysMsgContainer.StringArgument(t));
  }

  protected T add(SysMsgContainer.IArgument arg) {
    this._arguments.add(arg);
    return this;
  }

  public static class PlayerNameArgument extends SysMsgContainer.StringArgument {
    public PlayerNameArgument(Creature creature) {
      super(creature.getName());
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.TEXT;
    }
  }

  public static class ElementNameArgument extends SysMsgContainer.IntegerArgument {
    public ElementNameArgument(int type) {
      super(type);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.ELEMENT_NAME;
    }
  }

  public static class ZoneArgument extends SysMsgContainer.IArgument {
    private final int _x;
    private final int _y;
    private final int _z;

    public ZoneArgument(int t1, int t2, int t3) {
      this._x = t1;
      this._y = t2;
      this._z = t3;
    }

    void writeData(SysMsgContainer message) {
      message.writeD(this._x);
      message.writeD(this._y);
      message.writeD(this._z);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.ZONE_NAME;
    }
  }

  public static class SkillArgument extends SysMsgContainer.IArgument {
    private final int _skillId;
    private final int _skillLevel;

    public SkillArgument(int t1, int t2) {
      this._skillId = t1;
      this._skillLevel = t2;
    }

    void writeData(SysMsgContainer message) {
      message.writeD(this._skillId);
      message.writeD(this._skillLevel);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.SKILL_NAME;
    }
  }

  public static class StringArgument extends SysMsgContainer.IArgument {
    private final String _data;

    public StringArgument(String da) {
      this._data = da == null ? "null" : da;
    }

    void writeData(SysMsgContainer message) {
      message.writeS(this._data);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.TEXT;
    }
  }

  public static class LongArgument extends SysMsgContainer.IArgument {
    private final long _data;

    public LongArgument(long da) {
      this._data = da;
    }

    void writeData(SysMsgContainer message) {
      message.writeQ(this._data);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.LONG;
    }
  }

  public static class StaticObjectNameArgument extends SysMsgContainer.IntegerArgument {
    public StaticObjectNameArgument(int da) {
      super(da);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.STATIC_OBJECT_NAME;
    }
  }

  public static class ResidenceArgument extends SysMsgContainer.IntegerArgument {
    public ResidenceArgument(int da) {
      super(da);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.RESIDENCE_NAME;
    }
  }

  public static class SysStringArgument extends SysMsgContainer.IntegerArgument {
    public SysStringArgument(int da) {
      super(da);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.SYSTEM_STRING;
    }
  }

  public static class InstanceNameArgument extends SysMsgContainer.IntegerArgument {
    public InstanceNameArgument(int da) {
      super(da);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.INSTANCE_NAME;
    }
  }

  public static class ItemNameWithAugmentationArgument extends SysMsgContainer.IArgument {
    private final int _itemId;
    private final int _variation1;
    private final int _variation2;

    public ItemNameWithAugmentationArgument(int itemId, int variation1, int variation2) {
      this._itemId = itemId;
      this._variation1 = variation1;
      this._variation2 = variation2;
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.ITEM_NAME_WITH_AUGMENTATION;
    }

    void writeData(SysMsgContainer message) {
      message.writeD(this._itemId);
      message.writeH(this._variation1);
      message.writeH(this._variation2);
    }
  }

  public static class ItemNameArgument extends SysMsgContainer.IntegerArgument {
    public ItemNameArgument(int da) {
      super(da);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.ITEM_NAME;
    }
  }

  public static class NpcNameArgument extends SysMsgContainer.IntegerArgument {
    public NpcNameArgument(int da) {
      super(da);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.NPC_NAME;
    }
  }

  public static class IntegerArgument extends SysMsgContainer.IArgument {
    private final int _data;

    public IntegerArgument(int da) {
      this._data = da;
    }

    public void writeData(SysMsgContainer message) {
      message.writeD(this._data);
    }

    SysMsgContainer.Types getType() {
      return SysMsgContainer.Types.NUMBER;
    }
  }

  public abstract static class IArgument {
    public IArgument() {
    }

    void write(SysMsgContainer m) {
      m.writeD(this.getType().ordinal());
      this.writeData(m);
    }

    abstract SysMsgContainer.Types getType();

    abstract void writeData(SysMsgContainer var1);
  }

  public static enum Types {
    TEXT,
    NUMBER,
    NPC_NAME,
    ITEM_NAME,
    SKILL_NAME,
    RESIDENCE_NAME,
    LONG,
    ZONE_NAME,
    ITEM_NAME_WITH_AUGMENTATION,
    ELEMENT_NAME,
    INSTANCE_NAME,
    STATIC_OBJECT_NAME,
    PLAYER_NAME,
    SYSTEM_STRING;

    private Types() {
    }
  }
}
