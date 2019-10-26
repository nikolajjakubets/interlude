//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.util.concurrent.Future;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.commons.threading.SteppingRunnableQueueManager;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.AccountBonusDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

public class LazyPrecisionTaskManager extends SteppingRunnableQueueManager {
  private static final LazyPrecisionTaskManager _instance = new LazyPrecisionTaskManager();

  public static final LazyPrecisionTaskManager getInstance() {
    return _instance;
  }

  private LazyPrecisionTaskManager() {
    super(1000L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        LazyPrecisionTaskManager.this.purge();
      }
    }, 60000L, 60000L);
  }

  public Future<?> addPCCafePointsTask(final Player player) {
    long delay = (long)Config.ALT_PCBANG_POINTS_DELAY * 60000L;
    return this.scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        if (!player.isInOfflineMode() && player.getLevel() >= Config.ALT_PCBANG_POINTS_MIN_LVL) {
          player.addPcBangPoints(Config.ALT_PCBANG_POINTS_BONUS, Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0.0D && Rnd.chance(Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE));
        }
      }
    }, delay, delay);
  }

  public Future<?> startBonusExpirationTask(Player player) {
    final HardReference<Player> playerRef = player.getRef();
    long delay = player.getBonus().getBonusExpire() * 1000L - System.currentTimeMillis();
    return this.schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        Player player = (Player)playerRef.get();
        if (player != null) {
          player.getBonus().reset();
          if (player.getParty() != null) {
            player.getParty().recalculatePartyData();
          }

          String msg = (new CustomMessage("scripts.services.RateBonus.LuckEnded", player, new Object[0])).toString();
          player.sendPacket(new ExShowScreenMessage(msg, 10000, ScreenMessageAlign.TOP_CENTER, true));
          player.sendMessage(msg);
          AccountBonusDAO.getInstance().delete(player.getAccountName());
        }
      }
    }, delay);
  }

  public Future<?> addNpcAnimationTask(final NpcInstance npc) {
    return this.scheduleAtFixedRate(new RunnableImpl() {
      public void runImpl() throws Exception {
        if (npc.isVisible() && !npc.isActionsDisabled() && !npc.isMoving() && !npc.isInCombat()) {
          npc.onRandomAnimation();
        }

      }
    }, 1000L, (long)Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION) * 1000L);
  }
}
