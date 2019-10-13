//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.attachment;

import l2.gameserver.model.Player;

public interface PickableAttachment extends ItemAttachment {
  boolean canPickUp(Player var1);

  void pickUp(Player var1);
}
