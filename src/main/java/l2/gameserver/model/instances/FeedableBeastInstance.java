//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model.instances;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntObjectHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedableBeastInstance extends MonsterInstance {
  private static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);
  private static int GOLDEN_SPICE = 0;
  private static int CRYSTAL_SPICE = 1;
  private static int SKILL_GOLDEN_SPICE = 2188;
  private static int SKILL_CRYSTAL_SPICE = 2189;
  public static final TIntObjectHashMap<FeedableBeastInstance.growthInfo> growthCapableMobs = new TIntObjectHashMap();
  public static final TIntArrayList tamedBeasts = new TIntArrayList();
  public static final TIntArrayList feedableBeasts = new TIntArrayList();
  public static Map<Integer, Integer> feedInfo;

  private boolean isGoldenSpice(int skillId) {
    return skillId == 2188;
  }

  private boolean isCrystalSpice(int skillId) {
    return skillId == 2189;
  }

  private int getFoodSpice(int skillId) {
    return this.isGoldenSpice(skillId) ? 6643 : 6644;
  }

  public int getItemIdBySkillId(int skillId) {
    int itemId = false;
    short itemId;
    switch(skillId) {
      case 2188:
        itemId = 6643;
        break;
      case 2189:
        itemId = 6644;
        break;
      default:
        itemId = 0;
    }

    return itemId;
  }

  public FeedableBeastInstance(int objectId, NpcTemplate template) {
    super(objectId, template);
  }

  private void spawnNext(Player player, int growthLevel, int food) {
    int npcId = this.getNpcId();
    int nextNpcId = false;
    int nextNpcId = ((FeedableBeastInstance.growthInfo)growthCapableMobs.get(npcId)).spice[food][Rnd.get(((FeedableBeastInstance.growthInfo)growthCapableMobs.get(npcId)).spice[food].length)];
    feedInfo.remove(this.getObjectId());
    if (((FeedableBeastInstance.growthInfo)growthCapableMobs.get(npcId)).growth_level == 0) {
      this.onDecay();
    } else {
      this.deleteMe();
    }

    if (tamedBeasts.contains(nextNpcId)) {
      if (player.getTrainedBeast() != null) {
        player.getTrainedBeast().doDespawn();
      }

      NpcTemplate template = NpcHolder.getInstance().getTemplate(nextNpcId);
      TamedBeastInstance nextNpc = new TamedBeastInstance(IdFactory.getInstance().getNextId(), template);
      Location loc = player.getLoc();
      loc.x += Rnd.get(-50, 50);
      loc.y += Rnd.get(-50, 50);
      nextNpc.spawnMe(loc);
      nextNpc.setTameType(player);
      nextNpc.setFoodType(this.getFoodSpice(food == GOLDEN_SPICE ? SKILL_GOLDEN_SPICE : SKILL_CRYSTAL_SPICE));
      nextNpc.setRunning();
      nextNpc.setOwner(player);
      QuestState st = player.getQuestState("_020_BringUpWithLove");
      if (st != null && !st.isCompleted() && Rnd.chance(5) && st.getQuestItemsCount(7185) == 0L) {
        st.giveItems(7185, 1L);
        st.setCond(2);
      }

      st = player.getQuestState("_655_AGrandPlanForTamingWildBeasts");
      if (st != null && !st.isCompleted() && st.getCond() == 1 && st.getQuestItemsCount(8084) < 10L) {
        st.giveItems(8084, 1L);
      }
    } else {
      MonsterInstance nextNpc = this.spawn(nextNpcId, this.getX(), this.getY(), this.getZ());
      feedInfo.put(nextNpc.getObjectId(), player.getObjectId());
      player.setObjectTarget(nextNpc);
      ThreadPoolManager.getInstance().schedule(new FeedableBeastInstance.AggrPlayer(nextNpc, player), 3000L);
    }

  }

  protected void onDeath(Creature killer) {
    feedInfo.remove(this.getObjectId());
    super.onDeath(killer);
  }

  public MonsterInstance spawn(int npcId, int x, int y, int z) {
    try {
      MonsterInstance monster = (MonsterInstance)NpcHolder.getInstance().getTemplate(npcId).getInstanceConstructor().newInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(npcId));
      monster.setSpawnedLoc(new Location(x, y, z));
      monster.spawnMe(monster.getSpawnedLoc());
      return monster;
    } catch (Exception var6) {
      _log.error("Could not spawn Npc " + npcId, var6);
      return null;
    }
  }

  public void onSkillUse(Player player, int skillId) {
    int npcId = this.getNpcId();
    if (feedableBeasts.contains(npcId)) {
      if (!this.isGoldenSpice(skillId) || !this.isCrystalSpice(skillId)) {
        int food = this.isGoldenSpice(skillId) ? 0 : 1;
        int objectId = this.getObjectId();
        this.broadcastPacket(new L2GameServerPacket[]{new SocialAction(objectId, 2)});
        if (growthCapableMobs.containsKey(npcId)) {
          if (((FeedableBeastInstance.growthInfo)growthCapableMobs.get(npcId)).spice[food].length == 0) {
            return;
          }

          int growthLevel = ((FeedableBeastInstance.growthInfo)growthCapableMobs.get(npcId)).growth_level;
          if (growthLevel > 0 && feedInfo.get(objectId) != null && (Integer)feedInfo.get(objectId) != player.getObjectId()) {
            return;
          }

          if (Rnd.chance(((FeedableBeastInstance.growthInfo)growthCapableMobs.get(npcId)).growth_chance)) {
            this.spawnNext(player, growthLevel, food);
          }
        } else if (Rnd.chance(60)) {
          this.dropItem(player, this.getItemIdBySkillId(skillId), 1L);
        }

      }
    }
  }

  static {
    growthCapableMobs.put(21451, new FeedableBeastInstance.growthInfo(0, new int[][]{{21452, 21453, 21454, 21455}, {21456, 21457, 21458, 21459}}, 100));
    growthCapableMobs.put(21452, new FeedableBeastInstance.growthInfo(1, new int[][]{{21460, 21462}, new int[0]}, 40));
    growthCapableMobs.put(21453, new FeedableBeastInstance.growthInfo(1, new int[][]{{21461, 21463}, new int[0]}, 40));
    growthCapableMobs.put(21454, new FeedableBeastInstance.growthInfo(1, new int[][]{{21460, 21462}, new int[0]}, 40));
    growthCapableMobs.put(21455, new FeedableBeastInstance.growthInfo(1, new int[][]{{21461, 21463}, new int[0]}, 40));
    growthCapableMobs.put(21456, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21464, 21466}}, 40));
    growthCapableMobs.put(21457, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21465, 21467}}, 40));
    growthCapableMobs.put(21458, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21464, 21466}}, 40));
    growthCapableMobs.put(21459, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21465, 21467}}, 40));
    growthCapableMobs.put(21460, new FeedableBeastInstance.growthInfo(2, new int[][]{{21468, 16017}, new int[0]}, 25));
    growthCapableMobs.put(21461, new FeedableBeastInstance.growthInfo(2, new int[][]{{21469, 16018}, new int[0]}, 25));
    growthCapableMobs.put(21462, new FeedableBeastInstance.growthInfo(2, new int[][]{{21468, 16017}, new int[0]}, 25));
    growthCapableMobs.put(21463, new FeedableBeastInstance.growthInfo(2, new int[][]{{21469, 16018}, new int[0]}, 25));
    growthCapableMobs.put(21464, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21468, 16017}}, 25));
    growthCapableMobs.put(21465, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21469, 16018}}, 25));
    growthCapableMobs.put(21466, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21468, 16017}}, 25));
    growthCapableMobs.put(21467, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21469, 16018}}, 25));
    growthCapableMobs.put(21470, new FeedableBeastInstance.growthInfo(0, new int[][]{{21472, 21474, 21471, 21473}, {21475, 21476, 21477, 21478}}, 100));
    growthCapableMobs.put(21471, new FeedableBeastInstance.growthInfo(1, new int[][]{{21479, 21481}, new int[0]}, 40));
    growthCapableMobs.put(21472, new FeedableBeastInstance.growthInfo(1, new int[][]{{21480, 21482}, new int[0]}, 40));
    growthCapableMobs.put(21473, new FeedableBeastInstance.growthInfo(1, new int[][]{{21479, 21481}, new int[0]}, 40));
    growthCapableMobs.put(21474, new FeedableBeastInstance.growthInfo(1, new int[][]{{21480, 21482}, new int[0]}, 40));
    growthCapableMobs.put(21475, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21483, 21485}}, 40));
    growthCapableMobs.put(21476, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21484, 21486}}, 40));
    growthCapableMobs.put(21477, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21483, 21485}}, 40));
    growthCapableMobs.put(21478, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21484, 21486}}, 40));
    growthCapableMobs.put(21479, new FeedableBeastInstance.growthInfo(2, new int[][]{{21487, 16014}, new int[0]}, 25));
    growthCapableMobs.put(21480, new FeedableBeastInstance.growthInfo(2, new int[][]{{21488, 16013}, new int[0]}, 25));
    growthCapableMobs.put(21481, new FeedableBeastInstance.growthInfo(2, new int[][]{{21487, 16014}, new int[0]}, 25));
    growthCapableMobs.put(21482, new FeedableBeastInstance.growthInfo(2, new int[][]{{21488, 16013}, new int[0]}, 25));
    growthCapableMobs.put(21483, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21487, 16014}}, 25));
    growthCapableMobs.put(21484, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21488, 16013}}, 25));
    growthCapableMobs.put(21485, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21487, 16014}}, 25));
    growthCapableMobs.put(21486, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21488, 16013}}, 25));
    growthCapableMobs.put(21489, new FeedableBeastInstance.growthInfo(0, new int[][]{{21491, 21493, 21490, 21492}, {21495, 21497, 21494, 21496}}, 100));
    growthCapableMobs.put(21490, new FeedableBeastInstance.growthInfo(1, new int[][]{{21498, 21500}, new int[0]}, 40));
    growthCapableMobs.put(21491, new FeedableBeastInstance.growthInfo(1, new int[][]{{21499, 21501}, new int[0]}, 40));
    growthCapableMobs.put(21492, new FeedableBeastInstance.growthInfo(1, new int[][]{{21498, 21500}, new int[0]}, 40));
    growthCapableMobs.put(21493, new FeedableBeastInstance.growthInfo(1, new int[][]{{21499, 21501}, new int[0]}, 40));
    growthCapableMobs.put(21494, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21502, 21504}}, 40));
    growthCapableMobs.put(21495, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21503, 21505}}, 40));
    growthCapableMobs.put(21496, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21502, 21504}}, 40));
    growthCapableMobs.put(21497, new FeedableBeastInstance.growthInfo(1, new int[][]{new int[0], {21503, 21505}}, 40));
    growthCapableMobs.put(21498, new FeedableBeastInstance.growthInfo(2, new int[][]{{21506, 16015}, new int[0]}, 25));
    growthCapableMobs.put(21499, new FeedableBeastInstance.growthInfo(2, new int[][]{{21507, 16016}, new int[0]}, 25));
    growthCapableMobs.put(21500, new FeedableBeastInstance.growthInfo(2, new int[][]{{21506, 16015}, new int[0]}, 25));
    growthCapableMobs.put(21501, new FeedableBeastInstance.growthInfo(2, new int[][]{{21507, 16015}, new int[0]}, 25));
    growthCapableMobs.put(21502, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21506, 16015}}, 25));
    growthCapableMobs.put(21503, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21507, 16016}}, 25));
    growthCapableMobs.put(21504, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21506, 16015}}, 25));
    growthCapableMobs.put(21505, new FeedableBeastInstance.growthInfo(2, new int[][]{new int[0], {21507, 16016}}, 25));

    Integer i;
    for(i = 16013; i <= 16018; i = i + 1) {
      tamedBeasts.add(i);
    }

    for(i = 16013; i <= 16019; i = i + 1) {
      feedableBeasts.add(i);
    }

    for(i = 21451; i <= 21507; i = i + 1) {
      feedableBeasts.add(i);
    }

    for(i = 21824; i <= 21829; i = i + 1) {
      feedableBeasts.add(i);
    }

    feedInfo = new ConcurrentHashMap();
  }

  public static class AggrPlayer extends RunnableImpl {
    private NpcInstance _actor;
    private Player _killer;

    public AggrPlayer(NpcInstance actor, Player killer) {
      this._actor = actor;
      this._killer = killer;
    }

    public void runImpl() throws Exception {
      this._actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this._killer, 1000);
    }
  }

  private static class growthInfo {
    public int growth_level;
    public int growth_chance;
    public int[][] spice;

    public growthInfo(int level, int[][] sp, int chance) {
      this.growth_level = level;
      this.spice = sp;
      this.growth_chance = chance;
    }
  }
}
