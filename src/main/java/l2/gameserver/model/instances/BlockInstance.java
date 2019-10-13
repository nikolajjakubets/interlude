//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.templates.npc.NpcTemplate;

public class BlockInstance extends NpcInstance {
  private boolean _isRed;

  public BlockInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public boolean isRed() {
    return this._isRed;
  }

  public void setRed(boolean red) {
    this._isRed = red;
    this.broadcastCharInfo();
  }

  public void changeColor() {
    this.setRed(!this._isRed);
  }

  public void showChatWindow(Player player, int val, Object... arg) {
  }

  public boolean isNameAbove() {
    return false;
  }

  public int getFormId() {
    return this._isRed ? 83 : 0;
  }

  public boolean isInvul() {
    return true;
  }
}
