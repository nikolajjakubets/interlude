package l2.commons.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LoggerObject {

  public LoggerObject() {
  }

  public void error(String st, Exception e) {
    log.error(this.getClass().getSimpleName() + ": " + st, e);
  }

  public void error(String st) {
    log.error(this.getClass().getSimpleName() + ": " + st);
  }

  public void warn(String st, Exception e) {
    log.warn(this.getClass().getSimpleName() + ": " + st, e);
  }

  public void warn(String st) {
    log.warn(this.getClass().getSimpleName() + ": " + st);
  }

  public void info(String st, Exception e) {
    log.info(this.getClass().getSimpleName() + ": " + st, e);
  }

  public void info(String st) {
    log.info(this.getClass().getSimpleName() + ": " + st);
  }
}
