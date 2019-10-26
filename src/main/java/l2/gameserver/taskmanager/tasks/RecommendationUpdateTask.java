//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager.tasks;

import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.taskmanager.Task;
import l2.gameserver.taskmanager.TaskManager;
import l2.gameserver.taskmanager.TaskTypes;
import l2.gameserver.taskmanager.TaskManager.ExecutedTask;

public class RecommendationUpdateTask extends Task {
  private static final String NAME = "sp_recommendations";

  public RecommendationUpdateTask() {
  }

  public void init() {
    TaskManager.addUniqueTask("sp_recommendations", TaskTypes.TYPE_GLOBAL_TASK, "1", String.format("%02d:%02d:00", Config.REC_FLUSH_HOUR, Config.REC_FLUSH_MINUTE), "");
  }

  public String getName() {
    return "sp_recommendations";
  }

  public void onTimeElapsed(ExecutedTask task) {
    Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

    while(var2.hasNext()) {
      Player player = (Player)var2.next();
      player.updateRecommends();
      player.broadcastUserInfo(true);
    }

  }
}
