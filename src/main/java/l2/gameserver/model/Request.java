//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.model;

import l2.commons.collections.MultiValueSet;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Request extends MultiValueSet<String> {
  private static final long serialVersionUID = 1L;
  private static final Logger _log = LoggerFactory.getLogger(Request.class);
  private static final AtomicInteger _nextId = new AtomicInteger();
  private final int _id;
  private Request.L2RequestType _type;
  private HardReference<Player> _requestor;
  private HardReference<Player> _reciever;
  private boolean _isRequestorConfirmed;
  private boolean _isRecieverConfirmed;
  private boolean _isCancelled;
  private boolean _isDone;
  private long _timeout;
  private Future<?> _timeoutTask;

  public Request(Request.L2RequestType type, Player requestor, Player reciever) {
    this._id = _nextId.incrementAndGet();
    this._requestor = requestor.getRef();
    this._reciever = reciever.getRef();
    this._type = type;
    requestor.setRequest(this);
    reciever.setRequest(this);
  }

  public Request setTimeout(long timeout) {
    this._timeout = timeout > 0L ? System.currentTimeMillis() + timeout : 0L;
    this._timeoutTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
      public void runImpl() throws Exception {
        Request.this.timeout();
      }
    }, timeout);
    return this;
  }

  public int getId() {
    return this._id;
  }

  public void cancel() {
    this._isCancelled = true;
    if (this._timeoutTask != null) {
      this._timeoutTask.cancel(false);
    }

    this._timeoutTask = null;
    Player player = this.getRequestor();
    if (player != null && player.getRequest() == this) {
      player.setRequest(null);
    }

    player = this.getReciever();
    if (player != null && player.getRequest() == this) {
      player.setRequest(null);
    }

  }

  public void done() {
    this._isDone = true;
    if (this._timeoutTask != null) {
      this._timeoutTask.cancel(false);
    }

    this._timeoutTask = null;
    Player player = this.getRequestor();
    if (player != null && player.getRequest() == this) {
      player.setRequest(null);
    }

    player = this.getReciever();
    if (player != null && player.getRequest() == this) {
      player.setRequest(null);
    }

  }

  public void timeout() {
    Player player = this.getReciever();
    if (player != null && player.getRequest() == this) {
      player.sendPacket(Msg.TIME_EXPIRED);
    }

    this.cancel();
  }

  public Player getOtherPlayer(Player player) {
    if (player == this.getRequestor()) {
      return this.getReciever();
    } else {
      return player == this.getReciever() ? this.getRequestor() : null;
    }
  }

  public Player getRequestor() {
    return this._requestor.get();
  }

  public Player getReciever() {
    return this._reciever.get();
  }

  public boolean isInProgress() {
    if (this._isCancelled) {
      return false;
    } else if (this._isDone) {
      return false;
    } else if (this._timeout == 0L) {
      return true;
    } else {
      return this._timeout > System.currentTimeMillis();
    }
  }

  public boolean isTypeOf(Request.L2RequestType type) {
    return this._type == type;
  }

  public void confirm(Player player) {
    if (player == this.getRequestor()) {
      this._isRequestorConfirmed = true;
    } else if (player == this.getReciever()) {
      this._isRecieverConfirmed = true;
    }

  }

  public boolean isConfirmed(Player player) {
    if (player == this.getRequestor()) {
      return this._isRequestorConfirmed;
    } else {
      return player == this.getReciever() && this._isRecieverConfirmed;
    }
  }

  public enum L2RequestType {
    CUSTOM,
    PARTY,
    PARTY_ROOM,
    CLAN,
    ALLY,
    TRADE,
    TRADE_REQUEST,
    FRIEND,
    CHANNEL,
    DUEL;

    L2RequestType() {
    }
  }
}
