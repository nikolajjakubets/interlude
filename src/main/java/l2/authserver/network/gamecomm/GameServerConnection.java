package l2.authserver.network.gamecomm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.authserver.Config;
import l2.authserver.ThreadPoolManager;
import l2.authserver.network.gamecomm.as2gs.PingRequest;
import l2.commons.threading.RunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerConnection {
    private static final Logger _log = LoggerFactory.getLogger(GameServerConnection.class);
    final ByteBuffer readBuffer;
    final Queue<SendablePacket> sendQueue;
    final Lock sendLock;
    final AtomicBoolean isPengingWrite;
    private final Selector selector;
    private final SelectionKey key;
    private GameServer gameServer;
    private Future<?> _pingTask;
    private int _pingRetry;

    public GameServerConnection(SelectionKey key) {
        this.readBuffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
        this.sendQueue = new ArrayDeque();
        this.sendLock = new ReentrantLock();
        this.isPengingWrite = new AtomicBoolean();
        this.key = key;
        this.selector = key.selector();
    }

    public void sendPacket(SendablePacket packet) {
        this.sendLock.lock();

        boolean wakeUp;
        label44: {
            try {
                this.sendQueue.add(packet);
                wakeUp = this.enableWriteInterest();
                break label44;
            } catch (CancelledKeyException var7) {
            } finally {
                this.sendLock.unlock();
            }

            return;
        }

        if (wakeUp) {
            this.selector.wakeup();
        }

    }

    protected boolean disableWriteInterest() throws CancelledKeyException {
        if (this.isPengingWrite.compareAndSet(true, false)) {
            this.key.interestOps(this.key.interestOps() & -5);
            return true;
        } else {
            return false;
        }
    }

    protected boolean enableWriteInterest() throws CancelledKeyException {
        if (!this.isPengingWrite.getAndSet(true)) {
            this.key.interestOps(this.key.interestOps() | 4);
            return true;
        } else {
            return false;
        }
    }

    public void closeNow() {
        this.key.interestOps(8);
        this.selector.wakeup();
    }

    public void onDisconnection() {
        try {
            this.stopPingTask();
            this.readBuffer.clear();
            this.sendLock.lock();

            try {
                this.sendQueue.clear();
            } finally {
                this.sendLock.unlock();
            }

            this.isPengingWrite.set(false);
            if (this.gameServer != null && this.gameServer.isAuthed()) {
                _log.info("Connection with gameserver " + this.gameServer.getId() + " [" + this.gameServer.getName() + "] lost.");
                _log.info("Setting gameserver down. All proxies will be down as well.");
                this.gameServer.setDown();
            }

            this.gameServer = null;
        } catch (Exception var5) {
            _log.error("", var5);
        }

    }

    ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    GameServer getGameServer() {
        return this.gameServer;
    }

    void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public String getIpAddress() {
        return ((SocketChannel)this.key.channel()).socket().getInetAddress().getHostAddress();
    }

    public void onPingResponse() {
        this._pingRetry = 0;
    }

    public void startPingTask() {
        if (Config.GAME_SERVER_PING_DELAY != 0L) {
            this._pingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new GameServerConnection.PingTask(), Config.GAME_SERVER_PING_DELAY, Config.GAME_SERVER_PING_DELAY);
        }
    }

    public void stopPingTask() {
        if (this._pingTask != null) {
            this._pingTask.cancel(false);
            this._pingTask = null;
        }

    }

    private class PingTask extends RunnableImpl {
        private PingTask() {
        }

        public void runImpl() {
            if (Config.GAME_SERVER_PING_RETRY > 0 && GameServerConnection.this._pingRetry > Config.GAME_SERVER_PING_RETRY) {
                _log.warn("Gameserver " + GameServerConnection.this.gameServer.getId() + " [" + GameServerConnection.this.gameServer.getName() + "] : ping timeout!");
                GameServerConnection.this.closeNow();
            } else {
                GameServerConnection.this._pingRetry++;
                GameServerConnection.this.sendPacket(new PingRequest());
            }
        }
    }
}
