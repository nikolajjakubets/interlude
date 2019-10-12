//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.threading;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final Logger _log = LoggerFactory.getLogger(LoggingRejectedExecutionHandler.class);

    public LoggingRejectedExecutionHandler() {
    }

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            _log.error(r + " from " + executor, new RejectedExecutionException());
        }
    }
}
