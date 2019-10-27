//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.taskmanager.tasks.RecommendationUpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TaskManager {
  private static final Logger _log = LoggerFactory.getLogger(TaskManager.class);
  private static TaskManager _instance;
  static final String[] SQL_STATEMENTS = new String[]{"SELECT id,task,type,last_activation,param1,param2,param3 FROM global_tasks", "UPDATE global_tasks SET last_activation=? WHERE id=?", "SELECT id FROM global_tasks WHERE task=?", "INSERT INTO global_tasks (task,type,last_activation,param1,param2,param3) VALUES(?,?,?,?,?,?)"};
  private final Map<String, Task> _tasks = new ConcurrentHashMap();
  final List<TaskManager.ExecutedTask> _currentTasks = new ArrayList<>();

  public static TaskManager getInstance() {
    if (_instance == null) {
      _instance = new TaskManager();
    }

    return _instance;
  }

  public TaskManager() {
    this.init();
    this.startAllTasks();
  }

  public void init() {
    this.registerTask(new RecommendationUpdateTask());
  }

  public void registerTask(Task task) {
    String name = task.getName();
    if (!this._tasks.containsKey(name)) {
      this._tasks.put(name, task);
      task.init();
    }

  }

  private void startAllTasks() {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(SQL_STATEMENTS[0]);
      rset = statement.executeQuery();

      while(rset.next()) {
        Task task = (Task)this._tasks.get(rset.getString("task"));
        if (task != null) {
          TaskTypes type = TaskTypes.valueOf(rset.getString("type"));
          if (type != TaskTypes.TYPE_NONE) {
            TaskManager.ExecutedTask current = new TaskManager.ExecutedTask(task, type, rset);
            if (this.launchTask(current)) {
              this._currentTasks.add(current);
            }
          }
        }
      }
    } catch (Exception var10) {
      _log.error("error while loading Global Task table " + var10);
      _log.error("", var10);
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

  }

  private boolean launchTask(TaskManager.ExecutedTask task) {
    ThreadPoolManager scheduler = ThreadPoolManager.getInstance();
    TaskTypes type = task.getType();
    if (type == TaskTypes.TYPE_STARTUP) {
      task.run();
      return false;
    } else {
      long interval;
      if (type == TaskTypes.TYPE_SHEDULED) {
        interval = Long.parseLong(task.getParams()[0]);
        task._scheduled = scheduler.schedule(task, interval);
        return true;
      } else if (type == TaskTypes.TYPE_FIXED_SHEDULED) {
//        interval = Long.parseLong(task.getParams()[0]);
        interval = Long.parseLong(task.getParams()[1]);
        task._scheduled = scheduler.scheduleAtFixedRate(task, interval, interval);
        return true;
      } else {
        if (type == TaskTypes.TYPE_TIME) {
          try {
            Date desired = DateFormat.getInstance().parse(task.getParams()[0]);
            long diff = desired.getTime() - System.currentTimeMillis();
            if (diff >= 0L) {
              task._scheduled = scheduler.schedule(task, diff);
              return true;
            }

            _log.info("Task " + task.getId() + " is obsoleted.");
          } catch (Exception e) {
            _log.error("launchTask: eMessage={}, eClause={}", e.getMessage(), e.getClass());
          }
        } else if (type == TaskTypes.TYPE_SPECIAL) {
          ScheduledFuture<?> result = task.getTask().launchSpecial(task);
          if (result != null) {
            task._scheduled = result;
            return true;
          }
        } else if (type == TaskTypes.TYPE_GLOBAL_TASK) {
          interval = Long.parseLong(task.getParams()[0]) * 86400000L;
          String[] hour = task.getParams()[1].split(":");
          if (hour.length != 3) {
            _log.warn("Task " + task.getId() + " has incorrect parameters");
            return false;
          }

          Calendar check = Calendar.getInstance();
          check.setTimeInMillis(task.getLastActivation() + interval);
          Calendar min = Calendar.getInstance();

          try {
            min.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour[0]));
            min.set(Calendar.MINUTE, Integer.parseInt(hour[1]));
            min.set(Calendar.SECOND, Integer.parseInt(hour[2]));
          } catch (Exception var11) {
            _log.warn("Bad parameter on task " + task.getId() + ": " + var11.getMessage());
            return false;
          }

          long delay = min.getTimeInMillis() - System.currentTimeMillis();
          if (check.after(min) || delay < 0L) {
            delay += interval;
          }

          task._scheduled = scheduler.scheduleAtFixedRate(task, delay, interval);
          return true;
        }

        return false;
      }
    }
  }

  public static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3) {
    return addUniqueTask(task, type, param1, param2, param3, 0L);
  }

  public static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation) {
    Connection con = null;
    PreparedStatement statement = null;
    ResultSet rset = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(SQL_STATEMENTS[2]);
      statement.setString(1, task);
      rset = statement.executeQuery();
      boolean exists = rset.next();
      DbUtils.close(statement, rset);
      if (!exists) {
        statement = con.prepareStatement(SQL_STATEMENTS[3]);
        statement.setString(1, task);
        statement.setString(2, type.toString());
        statement.setLong(3, lastActivation / 1000L);
        statement.setString(4, param1);
        statement.setString(5, param2);
        statement.setString(6, param3);
        statement.execute();
      }

      boolean var11 = true;
      return var11;
    } catch (SQLException var15) {
      _log.warn("cannot add the unique task: " + var15.getMessage());
    } finally {
      DbUtils.closeQuietly(con, statement, rset);
    }

    return false;
  }

  public static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3) {
    return addTask(task, type, param1, param2, param3, 0L);
  }

  public static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation) {
    Connection con = null;
    PreparedStatement statement = null;

    try {
      con = DatabaseFactory.getInstance().getConnection();
      statement = con.prepareStatement(SQL_STATEMENTS[3]);
      statement.setString(1, task);
      statement.setString(2, type.toString());
      statement.setLong(3, lastActivation / 1000L);
      statement.setString(4, param1);
      statement.setString(5, param2);
      statement.setString(6, param3);
      statement.execute();
      boolean var9 = true;
      return var9;
    } catch (SQLException var13) {
      _log.warn("cannot add the task:\t" + var13.getMessage());
    } finally {
      DbUtils.closeQuietly(con, statement);
    }

    return false;
  }

  public class ExecutedTask extends RunnableImpl {
    int _id;
    long _lastActivation;
    Task _task;
    TaskTypes _type;
    String[] _params;
    ScheduledFuture<?> _scheduled;

    public ExecutedTask(Task task, TaskTypes type, ResultSet rset) throws SQLException {
      this._task = task;
      this._type = type;
      this._id = rset.getInt("id");
      this._lastActivation = rset.getLong("last_activation") * 1000L;
      this._params = new String[]{rset.getString("param1"), rset.getString("param2"), rset.getString("param3")};
    }

    public void runImpl() throws Exception {
      this._task.onTimeElapsed(this);
      this._lastActivation = System.currentTimeMillis();
      Connection con = null;
      PreparedStatement statement = null;

      try {
        con = DatabaseFactory.getInstance().getConnection();
        statement = con.prepareStatement(TaskManager.SQL_STATEMENTS[1]);
        statement.setLong(1, this._lastActivation / 1000L);
        statement.setInt(2, this._id);
        statement.executeUpdate();
      } catch (SQLException var7) {
        _log.warn("cannot updated the Global Task " + this._id + ": " + var7.getMessage());
      } finally {
        DbUtils.closeQuietly(con, statement);
      }

      if (this._type == TaskTypes.TYPE_SHEDULED || this._type == TaskTypes.TYPE_TIME) {
        this.stopTask();
      }

    }

    public boolean equals(Object object) {
      return this._id == ((TaskManager.ExecutedTask)object)._id;
    }

    public Task getTask() {
      return this._task;
    }

    public TaskTypes getType() {
      return this._type;
    }

    public int getId() {
      return this._id;
    }

    public String[] getParams() {
      return this._params;
    }

    public long getLastActivation() {
      return this._lastActivation;
    }

    public void stopTask() {
      this._task.onDestroy();
      if (this._scheduled != null) {
        this._scheduled.cancel(false);
      }

      TaskManager.this._currentTasks.remove(this);
    }
  }
}
