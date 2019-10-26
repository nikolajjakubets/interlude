//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;

public class NpcSay extends NpcStringContainer {
  private int _objId;
  private int _type;
  private int _id;

  public NpcSay(NpcInstance npc, ChatType chatType, String text) {
    this(npc, chatType, NpcString.NONE, text);
  }

  public NpcSay(NpcInstance npc, ChatType chatType, NpcString npcString, String... params) {
    super(npcString, params);
    this._objId = npc.getObjectId();
    this._id = npc.getNpcId();
    this._type = chatType.ordinal();
  }

  protected final void writeImpl() {
    this.writeC(2);
    this.writeD(this._objId);
    this.writeD(this._type);
    this.writeD(1000000 + this._id);
    this.writeElements();
  }
}
