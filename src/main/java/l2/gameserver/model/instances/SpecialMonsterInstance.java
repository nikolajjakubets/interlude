//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.Config;
import l2.gameserver.templates.npc.NpcTemplate;

public class SpecialMonsterInstance extends MonsterInstance {
  public SpecialMonsterInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public boolean canChampion() {
    return Config.ALT_CHAMPION_CAN_BE_SPECIAL_MONSTERS;
  }
}
