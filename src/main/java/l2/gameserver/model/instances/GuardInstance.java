//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.npc.NpcTemplate;

public class GuardInstance extends NpcInstance {
  public GuardInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public boolean isAutoAttackable(Creature attacker) {
    return attacker.isMonster() && ((MonsterInstance)attacker).isAggressive() || attacker.isPlayable() && attacker.getKarma() > 0;
  }

  public String getHtmlPath(int npcId, int val, Player player) {
    String pom;
    if (val == 0) {
      pom = "" + npcId;
    } else {
      pom = npcId + "-" + val;
    }

    return "guard/" + pom + ".htm";
  }

  public boolean isInvul() {
    return false;
  }

  public boolean isFearImmune() {
    return true;
  }

  public boolean isParalyzeImmune() {
    return true;
  }

  protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
    this.getAggroList().addDamageHate(attacker, (int)damage, 0);
    super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
  }
}
