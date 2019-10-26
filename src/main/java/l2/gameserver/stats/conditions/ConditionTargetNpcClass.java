//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.stats.conditions;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.stats.Env;

public class ConditionTargetNpcClass extends Condition {
  private final Class<NpcInstance> _npcClass;

  public ConditionTargetNpcClass(String name) {
    Class classType = null;

    try {
      classType = Class.forName("l2.gameserver.model.instances." + name + "Instance");
    } catch (ClassNotFoundException var4) {
      classType = (Class)Scripts.getInstance().getClasses().get("npc.model." + name + "Instance");
    }

    if (classType == null) {
      throw new IllegalArgumentException("Not found type class for type: " + name + ".");
    } else {
      this._npcClass = classType;
    }
  }

  protected boolean testImpl(Env env) {
    return env.target != null && env.target.getClass() == this._npcClass;
  }
}
