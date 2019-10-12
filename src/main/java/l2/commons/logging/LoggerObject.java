package l2.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggerObject {
    protected final Logger _log = LoggerFactory.getLogger(this.getClass());

    public LoggerObject() {
    }

    public void error(String st, Exception e) {
        this._log.error(this.getClass().getSimpleName() + ": " + st, e);
    }

    public void error(String st) {
        this._log.error(this.getClass().getSimpleName() + ": " + st);
    }

    public void warn(String st, Exception e) {
        this._log.warn(this.getClass().getSimpleName() + ": " + st, e);
    }

    public void warn(String st) {
        this._log.warn(this.getClass().getSimpleName() + ": " + st);
    }

    public void info(String st, Exception e) {
        this._log.info(this.getClass().getSimpleName() + ": " + st, e);
    }

    public void info(String st) {
        this._log.info(this.getClass().getSimpleName() + ": " + st);
    }
}
