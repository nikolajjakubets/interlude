//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;

public class StatusUpdate extends L2GameServerPacket {
  public static final int CUR_HP = 9;
  public static final int MAX_HP = 10;
  public static final int CUR_MP = 11;
  public static final int MAX_MP = 12;
  public static final int CUR_LOAD = 14;
  public static final int MAX_LOAD = 15;
  public static final int PVP_FLAG = 26;
  public static final int KARMA = 27;
  public static final int CUR_CP = 33;
  public static final int MAX_CP = 34;
  private final int _objectId;
  private final List<StatusUpdate.Attribute> _attributes = new ArrayList<>();

  public StatusUpdate(Creature creature) {
    this._objectId = creature.getObjectId();
  }

  public StatusUpdate(int objectId) {
    this._objectId = objectId;
  }

  public StatusUpdate addAttribute(int id, int level) {
    this._attributes.add(new StatusUpdate.Attribute(id, level));
    return this;
  }

  protected final void writeImpl() {
    this.writeC(14);
    this.writeD(this._objectId);
    this.writeD(this._attributes.size());
    Iterator var1 = this._attributes.iterator();

    while(var1.hasNext()) {
      StatusUpdate.Attribute temp = (StatusUpdate.Attribute)var1.next();
      this.writeD(temp.id);
      this.writeD(temp.value);
    }

  }

  public boolean hasAttributes() {
    return !this._attributes.isEmpty();
  }

  class Attribute {
    public final int id;
    public final int value;

    Attribute(int id, int value) {
      this.id = id;
      this.value = value;
    }
  }
}
