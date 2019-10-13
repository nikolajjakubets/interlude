//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.AggroList.HateInfo;
import l2.gameserver.model.GameObjectTasks.DeleteTask;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class RaidBossInstance extends MonsterInstance {
  private static final long serialVersionUID = 1L;
  private ScheduledFuture<?> minionMaintainTask;
  private static final int MINION_UNSPAWN_INTERVAL = 5000;

  public RaidBossInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  public boolean isRaid() {
    return true;
  }

  protected int getMinionUnspawnInterval() {
    return 5000;
  }

  protected int getKilledInterval(MinionInstance minion) {
    return Config.MINIONS_RESPAWN_INTERVAL;
  }

  public void notifyMinionDied(MinionInstance minion) {
    this.minionMaintainTask = ThreadPoolManager.getInstance().schedule(new RaidBossInstance.MaintainKilledMinion(minion), (long)this.getKilledInterval(minion));
    super.notifyMinionDied(minion);
  }

  protected void onDeath(Creature killer) {
    if (this.minionMaintainTask != null) {
      this.minionMaintainTask.cancel(false);
      this.minionMaintainTask = null;
    }

    int points = this.getTemplate().rewardRp;
    if (points > 0) {
      this.calcRaidPointsReward(points);
    }

    if (this instanceof ReflectionBossInstance) {
      super.onDeath(killer);
    } else {
      if (killer.isPlayable()) {
        Player player = killer.getPlayer();
        if (!player.isInParty()) {
          if (player.isNoble()) {
            HeroController.getInstance().addHeroDiary(player.getObjectId(), 1, this.getNpcId());
          }

          player.sendPacket(Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
        } else {
          Iterator var4 = player.getParty().getPartyMembers().iterator();

          while(var4.hasNext()) {
            Player member = (Player)var4.next();
            if (member.isNoble()) {
              HeroController.getInstance().addHeroDiary(member.getObjectId(), 1, this.getNpcId());
            }
          }

          player.getParty().broadCast(new IStaticPacket[]{Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL});
        }

        Quest q = QuestManager.getQuest(508);
        if (q != null) {
          String qn = q.getName();
          if (player.getClan() != null && player.getClan().getLeader().isOnline() && player.getClan().getLeader().getPlayer().getQuestState(qn) != null) {
            QuestState st = player.getClan().getLeader().getPlayer().getQuestState(qn);
            st.getQuest().onKill(this, st);
          }
        }
      }

      if (this.getMinionList().hasAliveMinions()) {
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
          public void runImpl() throws Exception {
            if (RaidBossInstance.this.isDead()) {
              RaidBossInstance.this.getMinionList().unspawnMinions();
            }

          }
        }, (long)this.getMinionUnspawnInterval());
      }

      int boxId = 0;
      switch(this.getNpcId()) {
        case 25035:
          boxId = 31027;
          break;
        case 25054:
          boxId = 31028;
          break;
        case 25126:
          boxId = 31029;
          break;
        case 25220:
          boxId = 31030;
      }

      if (boxId != 0) {
        NpcTemplate boxTemplate = NpcHolder.getInstance().getTemplate(boxId);
        if (boxTemplate != null) {
          NpcInstance box = new NpcInstance(IdFactory.getInstance().getNextId(), boxTemplate);
          box.spawnMe(this.getLoc());
          box.setSpawnedLoc(this.getLoc());
          ThreadPoolManager.getInstance().schedule(new DeleteTask(box), 60000L);
        }
      }

      super.onDeath(killer);
    }
  }

  private void calcRaidPointsReward(int totalPoints) {
    Map<Object, Object[]> participants = new HashMap();
    double totalHp = (double)this.getMaxHp();

    Iterator var5;
    HateInfo ai;
    Object[] info;
    for(var5 = this.getAggroList().getPlayableMap().values().iterator(); var5.hasNext(); info[1] = (Long)info[1] + (long)ai.damage) {
      ai = (HateInfo)var5.next();
      Player player = ai.attacker.getPlayer();
      Object key = player.getParty() != null ? (player.getParty().getCommandChannel() != null ? player.getParty().getCommandChannel() : player.getParty()) : player.getPlayer();
      info = (Object[])participants.get(key);
      if (info == null) {
        info = new Object[]{new HashSet(), new Long(0L)};
        participants.put(key, info);
      }

      Iterator var10;
      Player p;
      if (key instanceof CommandChannel) {
        var10 = ((CommandChannel)key).iterator();

        while(var10.hasNext()) {
          p = (Player)var10.next();
          if (p.isInRangeZ(this, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
            ((Set)info[0]).add(p);
          }
        }
      } else if (key instanceof Party) {
        var10 = ((Party)key).getPartyMembers().iterator();

        while(var10.hasNext()) {
          p = (Player)var10.next();
          if (p.isInRangeZ(this, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
            ((Set)info[0]).add(p);
          }
        }
      } else {
        ((Set)info[0]).add(player);
      }
    }

    var5 = participants.values().iterator();

    while(var5.hasNext()) {
      Object[] groupInfo = (Object[])var5.next();
      Set<Player> players = (HashSet)groupInfo[0];
      int perPlayer = (int)Math.round((double)((long)totalPoints * (Long)groupInfo[1]) / (totalHp * (double)players.size()));
      Iterator var16 = players.iterator();

      while(var16.hasNext()) {
        Player player = (Player)var16.next();
        int playerReward = (int)Math.round((double)perPlayer * Experience.penaltyModifier((long)this.calculateLevelDiffForDrop(player.getLevel()), 9.0D));
        if (playerReward != 0) {
          player.sendPacket((new SystemMessage(1725)).addNumber(playerReward));
          RaidBossSpawnManager.getInstance().addPoints(player.getObjectId(), this.getNpcId(), playerReward);
        }
      }
    }

    RaidBossSpawnManager.getInstance().updatePointsDb();
    RaidBossSpawnManager.getInstance().calculateRanking();
  }

  protected void onDecay() {
    super.onDecay();
    RaidBossSpawnManager.getInstance().onBossDespawned(this);
  }

  protected void onSpawn() {
    super.onSpawn();
    this.addSkill(SkillTable.getInstance().getInfo(4045, 1));
    RaidBossSpawnManager.getInstance().onBossSpawned(this);
  }

  public boolean isFearImmune() {
    return true;
  }

  public boolean isParalyzeImmune() {
    return true;
  }

  public boolean isLethalImmune() {
    return true;
  }

  public boolean hasRandomWalk() {
    return false;
  }

  public boolean canChampion() {
    return false;
  }

  private class MaintainKilledMinion extends RunnableImpl {
    private final MinionInstance minion;

    public MaintainKilledMinion(MinionInstance minion) {
      this.minion = minion;
    }

    public void runImpl() throws Exception {
      if (!RaidBossInstance.this.isDead()) {
        this.minion.refreshID();
        RaidBossInstance.this.spawnMinion(this.minion);
      }

    }
  }
}
