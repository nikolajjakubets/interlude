//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public final class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {

  public LoggingRejectedExecutionHandler() {
  }

  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    if (!executor.isShutdown()) {
      log.error(r + " from " + executor, new RejectedExecutionException());
    }
  }
}
