//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;

public class ExBaseAttributeCancelResult extends L2GameServerPacket {
  private boolean _result;
  private int _objectId;
  private Element _element;

  public ExBaseAttributeCancelResult(boolean result, ItemInstance item, Element element) {
    this._result = result;
    this._objectId = item.getObjectId();
    this._element = element;
  }

  protected void writeImpl() {
    this.writeEx(117);
    this.writeD(this._result);
    this.writeD(this._objectId);
    this.writeD(this._element.getId());
  }
}
