//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.items.attachment;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;

public interface FlagItemAttachment extends PickableAttachment {
  void onLogout(Player var1);

  void onDeath(Player var1, Creature var2);

  void onEnterPeace(Player var1);

  boolean canAttack(Player var1);

  boolean canCast(Player var1, Skill var2);
}
