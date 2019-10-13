//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances.residences.clanhall;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2.gameserver.model.entity.events.objects.CTBTeamObject;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public abstract class CTBBossInstance extends MonsterInstance {
  public static final Skill SKILL = SkillTable.getInstance().getInfo(5456, 1);
  private CTBTeamObject _matchTeamObject;

  public CTBBossInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
    this.setHasChatWindow(false);
  }

  public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
    if (attacker.getLevel() > this.getLevel() + 8 && attacker.getEffectList().getEffectsCountForSkill(SKILL.getId()) == 0) {
      this.doCast(SKILL, attacker, false);
    } else {
      super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }
  }

  public boolean isAttackable(Creature attacker) {
    CTBSiegeClanObject clan = this._matchTeamObject.getSiegeClan();
    if (clan != null && attacker.isPlayable()) {
      Player player = attacker.getPlayer();
      if (player.getClan() == clan.getClan()) {
        return false;
      }
    }

    return true;
  }

  public boolean isAutoAttackable(Creature attacker) {
    return this.isAttackable(attacker);
  }

  public void onDeath(Creature killer) {
    ClanHallTeamBattleEvent event = (ClanHallTeamBattleEvent)this.getEvent(ClanHallTeamBattleEvent.class);
    event.processStep(this._matchTeamObject);
    super.onDeath(killer);
  }

  public String getTitle() {
    CTBSiegeClanObject clan = this._matchTeamObject.getSiegeClan();
    return clan == null ? "" : clan.getClan().getName();
  }

  public void setMatchTeamObject(CTBTeamObject matchTeamObject) {
    this._matchTeamObject = matchTeamObject;
  }
}
