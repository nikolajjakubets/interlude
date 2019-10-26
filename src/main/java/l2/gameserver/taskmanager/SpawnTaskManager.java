//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Util;

public class SpawnTaskManager {
  private SpawnTaskManager.SpawnTask[] _spawnTasks = new SpawnTaskManager.SpawnTask[500];
  private int _spawnTasksSize = 0;
  private final Object spawnTasks_lock = new Object();
  private static SpawnTaskManager _instance;

  public SpawnTaskManager() {
    ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnTaskManager.SpawnScheduler(), 2000L, 2000L);
  }

  public static SpawnTaskManager getInstance() {
    if (_instance == null) {
      _instance = new SpawnTaskManager();
    }

    return _instance;
  }

  public void addSpawnTask(NpcInstance actor, long interval) {
    this.removeObject(actor);
    this.addObject(new SpawnTaskManager.SpawnTask(actor, System.currentTimeMillis() + interval));
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("============= SpawnTask Manager Report ============\n\r");
    sb.append("Tasks count: ").append(this._spawnTasksSize).append("\n\r");
    sb.append("Tasks dump:\n\r");
    long current = System.currentTimeMillis();
    SpawnTaskManager.SpawnTask[] var4 = this._spawnTasks;
    int var5 = var4.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      SpawnTaskManager.SpawnTask container = var4[var6];
      if (container != null) {
        sb.append("Class/Name: ").append(container.getClass().getSimpleName()).append('/').append(container.getActor());
        sb.append(" spawn timer: ").append(Util.formatTime((int)((container.endtime - current) / 1000L))).append("\n\r");
      }
    }

    return sb.toString();
  }

  private void addObject(SpawnTaskManager.SpawnTask decay) {
    synchronized(this.spawnTasks_lock) {
      if (this._spawnTasksSize >= this._spawnTasks.length) {
        SpawnTaskManager.SpawnTask[] temp = new SpawnTaskManager.SpawnTask[this._spawnTasks.length * 2];

        for(int i = 0; i < this._spawnTasksSize; ++i) {
          temp[i] = this._spawnTasks[i];
        }

        this._spawnTasks = temp;
      }

      this._spawnTasks[this._spawnTasksSize] = decay;
      ++this._spawnTasksSize;
    }
  }

  public void removeObject(NpcInstance actor) {
    synchronized(this.spawnTasks_lock) {
      if (this._spawnTasksSize > 1) {
        int k = -1;

        for(int i = 0; i < this._spawnTasksSize; ++i) {
          if (this._spawnTasks[i].getActor() == actor) {
            k = i;
          }
        }

        if (k > -1) {
          this._spawnTasks[k] = this._spawnTasks[this._spawnTasksSize - 1];
          this._spawnTasks[this._spawnTasksSize - 1] = null;
          --this._spawnTasksSize;
        }
      } else if (this._spawnTasksSize == 1 && this._spawnTasks[0].getActor() == actor) {
        this._spawnTasks[0] = null;
        this._spawnTasksSize = 0;
      }

    }
  }

  private class SpawnTask {
    private final HardReference<NpcInstance> _npcRef;
    public long endtime;

    SpawnTask(NpcInstance cha, long delay) {
      this._npcRef = cha.getRef();
      this.endtime = delay;
    }

    public NpcInstance getActor() {
      return (NpcInstance)this._npcRef.get();
    }
  }

  public class SpawnScheduler extends RunnableImpl {
    public SpawnScheduler() {
    }

    public void runImpl() throws Exception {
      if (SpawnTaskManager.this._spawnTasksSize > 0) {
        try {
          List<NpcInstance> works = new ArrayList();
          synchronized(SpawnTaskManager.this.spawnTasks_lock) {
            long current = System.currentTimeMillis();
            int size = SpawnTaskManager.this._spawnTasksSize;
            int i = size - 1;

            while(true) {
              if (i < 0) {
                break;
              }

              try {
                SpawnTaskManager.SpawnTask container = SpawnTaskManager.this._spawnTasks[i];
                if (container != null && container.endtime > 0L && current > container.endtime) {
                  NpcInstance actor = container.getActor();
                  if (actor != null && actor.getSpawn() != null) {
                    works.add(actor);
                  }

                  container.endtime = -1L;
                }

                if (container == null || container.getActor() == null || container.endtime < 0L) {
                  if (i == SpawnTaskManager.this._spawnTasksSize - 1) {
                    SpawnTaskManager.this._spawnTasks[i] = null;
                  } else {
                    SpawnTaskManager.this._spawnTasks[i] = SpawnTaskManager.this._spawnTasks[SpawnTaskManager.this._spawnTasksSize - 1];
                    SpawnTaskManager.this._spawnTasks[SpawnTaskManager.this._spawnTasksSize - 1] = null;
                  }

                  if (SpawnTaskManager.this._spawnTasksSize > 0) {
                    SpawnTaskManager.this._spawnTasksSize--;
                  }
                }
              } catch (Exception var10) {
                _log.error("", var10);
              }

              --i;
            }
          }

          Iterator var2 = works.iterator();

          while(var2.hasNext()) {
            NpcInstance work = (NpcInstance)var2.next();
            Spawner spawn = work.getSpawn();
            if (spawn != null) {
              spawn.decreaseScheduledCount();
              if (spawn.isDoRespawn()) {
                spawn.respawnNpc(work);
              }
            }
          }
        } catch (Exception var12) {
          _log.error("", var12);
        }
      }

    }
  }
}
