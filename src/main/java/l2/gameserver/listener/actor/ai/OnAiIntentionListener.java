//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.listener.actor.ai;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.listener.AiListener;
import l2.gameserver.model.Creature;

public interface OnAiIntentionListener extends AiListener {
  void onAiIntention(Creature var1, CtrlIntention var2, Object var3, Object var4);
}
