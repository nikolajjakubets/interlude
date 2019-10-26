//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.util.concurrent.ScheduledFuture;
import l2.gameserver.taskmanager.TaskManager.ExecutedTask;

public abstract class Task {
  public Task() {
  }

  public abstract void init();

  public ScheduledFuture<?> launchSpecial(ExecutedTask instance) {
    return null;
  }

  public abstract String getName();

  public abstract void onTimeElapsed(ExecutedTask var1);

  public void onDestroy() {
  }
}
