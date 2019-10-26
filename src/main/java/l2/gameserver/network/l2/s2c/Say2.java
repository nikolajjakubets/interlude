//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SysString;
import l2.gameserver.network.l2.components.SystemMsg;

public class Say2 extends NpcStringContainer {
  private ChatType _type;
  private SysString _sysString;
  private SystemMsg _systemMsg;
  private int _objectId;
  private String _charName;

  public Say2(int objectId, ChatType type, SysString st, SystemMsg sm) {
    super(NpcString.NONE, new String[0]);
    this._objectId = objectId;
    this._type = type;
    this._sysString = st;
    this._systemMsg = sm;
  }

  public Say2(int objectId, ChatType type, String charName, String text) {
    this(objectId, type, charName, NpcString.NONE, text);
  }

  public Say2(int objectId, ChatType type, String charName, NpcString npcString, String... params) {
    super(npcString, params);
    this._objectId = objectId;
    this._type = type;
    this._charName = charName;
  }

  protected final void writeImpl() {
    this.writeC(74);
    this.writeD(this._objectId);
    this.writeD(this._type.ordinal());
    switch(this._type) {
      case SYSTEM_MESSAGE:
        this.writeD(this._sysString.getId());
        this.writeD(this._systemMsg.getId());
        break;
      default:
        this.writeS(this._charName);
        this.writeElements();
    }

  }
}
