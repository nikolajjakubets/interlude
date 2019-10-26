//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.taskmanager.actionrunner;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ActionWrapper extends RunnableImpl {
  private static final Logger _log = LoggerFactory.getLogger(ActionWrapper.class);
  private final String _name;
  private Future<?> _scheduledFuture;

  public ActionWrapper(String name) {
    this._name = name;
  }

  public void schedule(long time) {
    this._scheduledFuture = ThreadPoolManager.getInstance().schedule(this, time);
  }

  public void cancel() {
    if (this._scheduledFuture != null) {
      this._scheduledFuture.cancel(true);
      this._scheduledFuture = null;
    }

  }

  public abstract void runImpl0() throws Exception;

  public void runImpl() {
    try {
      this.runImpl0();
    } catch (Exception var5) {
      _log.info("ActionWrapper: Exception: " + var5 + "; name: " + this._name, var5);
    } finally {
      ActionRunner.getInstance().remove(this._name, this);
      this._scheduledFuture = null;
    }

  }

  public String getName() {
    return this._name;
  }
}
