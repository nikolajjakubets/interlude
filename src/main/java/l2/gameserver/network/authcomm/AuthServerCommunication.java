//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.gameserver.network.authcomm;

import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.network.authcomm.gs2as.AuthRequest;
import l2.gameserver.network.l2.GameClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class AuthServerCommunication extends Thread {
  private static final AuthServerCommunication instance = new AuthServerCommunication();
  private final Map<String, GameClient> waitingClients = new HashMap<>();
  private final Map<String, GameClient> authedClients = new HashMap<>();
  private final Lock readLock;
  private final Lock writeLock;
  private final ByteBuffer readBuffer;
  private final ByteBuffer writeBuffer;
  private final Queue<SendablePacket> sendQueue;
  private final Lock sendLock;
  private final AtomicBoolean isPengingWrite;
  private SelectionKey key;
  private Selector selector;
  private boolean shutdown;
  private boolean restart;

  public static AuthServerCommunication getInstance() {
    return instance;
  }

  private AuthServerCommunication() {
    ReadWriteLock lock = new ReentrantReadWriteLock();
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
    this.readBuffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
    this.writeBuffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
    this.sendQueue = new ArrayDeque<>();
    this.sendLock = new ReentrantLock();
    this.isPengingWrite = new AtomicBoolean();

    try {
      this.selector = Selector.open();
    } catch (IOException e) {
      log.error("init: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    }

  }

  private void connect() throws IOException {
    log.info("Connecting to authserver on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(false);
    this.key = channel.register(this.selector, 8);
    channel.connect(new InetSocketAddress(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT));
  }

  public void sendPacket(SendablePacket packet) {
    if (!this.isShutdown()) {
      this.sendLock.lock();

      boolean wakeUp;
      label50: {
        try {
          this.sendQueue.add(packet);
          wakeUp = this.enableWriteInterest();
          break label50;
        } catch (CancelledKeyException e) {
          log.error("sendPacket: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
        } finally {
          this.sendLock.unlock();
        }

        return;
      }

      if (wakeUp) {
        this.selector.wakeup();
      }

    }
  }

  private boolean disableWriteInterest() throws CancelledKeyException {
    if (this.isPengingWrite.compareAndSet(true, false)) {
      this.key.interestOps(this.key.interestOps() & -5);
      return true;
    } else {
      return false;
    }
  }

  private boolean enableWriteInterest() throws CancelledKeyException {
    if (!this.isPengingWrite.getAndSet(true)) {
      this.key.interestOps(this.key.interestOps() | 4);
      return true;
    } else {
      return false;
    }
  }

  protected ByteBuffer getReadBuffer() {
    return this.readBuffer;
  }

  protected ByteBuffer getWriteBuffer() {
    return this.writeBuffer;
  }

  public void run() {
    while(!this.shutdown) {
      this.restart = false;

      try {
        Set keys;
        Iterator iterator;
        SelectionKey key;
        int opts;
        label80:
        while(!this.isShutdown()) {
          this.connect();
          long elapsed = System.currentTimeMillis();
          int selected = this.selector.select(5000L);
          elapsed = System.currentTimeMillis() - elapsed;
          if (selected == 0 && elapsed < 5000L) {

            for (SelectionKey selectionKey : this.selector.keys()) {
              key = selectionKey;
              if (key.isValid()) {
                SocketChannel channel = (SocketChannel) key.channel();
                if (channel != null && (key.interestOps() & 8) != 0) {
                  this.connect(key);
                  break label80;
                }
              }
            }
          } else {
            keys = this.selector.selectedKeys();
            if (keys.isEmpty()) {
              throw new IOException("Connection timeout.");
            }

            iterator = keys.iterator();

            try {
              while(iterator.hasNext()) {
                key = (SelectionKey)iterator.next();
                iterator.remove();
                opts = key.readyOps();
                if (opts == 8) {
                  this.connect(key);
                  break label80;
                }
              }
            } catch (CancelledKeyException e) {
              log.error("run: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
              break;
            }
          }
        }

        while(!this.isShutdown()) {
          this.selector.select();
          keys = this.selector.selectedKeys();
          iterator = keys.iterator();

          try {
            while(iterator.hasNext()) {
              key = (SelectionKey)iterator.next();
              iterator.remove();
              opts = key.readyOps();
              switch(opts) {
                case 1:
                  this.read(key);
                case 2:
                case 3:
                default:
                  break;
                case 4:
                  this.write(key);
                  break;
                case 5:
                  this.write(key);
                  this.read(key);
              }
            }
          } catch (CancelledKeyException e) {
            log.error("run: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
            break;
          }
        }
      } catch (IOException var13) {
        log.error("AuthServer I/O error: " + var13.getMessage());
      }

      this.close();

      try {
        Thread.sleep(5000L);
      } catch (InterruptedException e) {
        log.error("run: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
      }
    }

  }

  private void read(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel)key.channel();
    ByteBuffer buf = this.getReadBuffer();
    int count = channel.read(buf);
    if (count == -1) {
      throw new IOException("End of stream.");
    } else if (count != 0) {
      buf.flip();

      while(this.tryReadPacket(key, buf)) {
      }

    }
  }

  private boolean tryReadPacket(SelectionKey key, ByteBuffer buf) throws IOException {
    int pos = buf.position();
    if (buf.remaining() > 2) {
      int size = buf.getShort() & '\uffff';
      if (size <= 2) {
        throw new IOException("Incorrect packet size: <= 2");
      }

      size -= 2;
      if (size <= buf.remaining()) {
        int limit = buf.limit();
        buf.limit(pos + size + 2);
        ReceivablePacket rp = PacketHandler.handlePacket(buf);
        if (rp != null && rp.read()) {
          ThreadPoolManager.getInstance().execute(rp);
        }

        buf.limit(limit);
        buf.position(pos + size + 2);
        if (!buf.hasRemaining()) {
          buf.clear();
          return false;
        }

        return true;
      }

      buf.position(pos);
    }

    buf.compact();
    return false;
  }

  private void write(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel)key.channel();
    ByteBuffer buf = this.getWriteBuffer();
    this.sendLock.lock();

    boolean done;
    try {
      int var5 = 0;

      SendablePacket sp;
      while (var5++ < 64 && (sp = this.sendQueue.poll()) != null) {
        int headerPos = buf.position();
        buf.position(headerPos + 2);
        sp.write();
        int dataSize = buf.position() - headerPos - 2;
        if (dataSize == 0) {
          buf.position(headerPos);
        } else {
          buf.position(headerPos);
          buf.putShort((short)(dataSize + 2));
          buf.position(headerPos + dataSize + 2);
        }
      }

      done = this.sendQueue.isEmpty();
      if (done) {
        this.disableWriteInterest();
      }
    } finally {
      this.sendLock.unlock();
    }

    buf.flip();
    channel.write(buf);
    if (buf.remaining() > 0) {
      buf.compact();
      done = false;
    } else {
      buf.clear();
    }

    if (!done && this.enableWriteInterest()) {
      this.selector.wakeup();
    }

  }

  private void connect(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel)key.channel();
    channel.finishConnect();
    key.interestOps(key.interestOps() & -9);
    key.interestOps(key.interestOps() | 1);
    this.sendPacket(new AuthRequest());
  }

  private void close() {
    this.restart = !this.shutdown;
    this.sendLock.lock();

    try {
      this.sendQueue.clear();
    } finally {
      this.sendLock.unlock();
    }

    this.readBuffer.clear();
    this.writeBuffer.clear();
    this.isPengingWrite.set(false);

    try {
      if (this.key != null) {
        this.key.channel().close();
        this.key.cancel();
      }
    } catch (IOException e) {
      log.error("connect: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
    }

    this.writeLock.lock();

    try {
      this.waitingClients.clear();
    } finally {
      this.writeLock.unlock();
    }

  }

  public void shutdown() {
    this.shutdown = true;
    this.selector.wakeup();
  }

  public boolean isShutdown() {
    return this.shutdown || this.restart;
  }

  public void restart() {
    this.restart = true;
    this.selector.wakeup();
  }

  public GameClient addWaitingClient(GameClient client) {
    this.writeLock.lock();

    GameClient var2;
    try {
      var2 = this.waitingClients.put(client.getLogin(), client);
    } finally {
      this.writeLock.unlock();
    }

    return var2;
  }

  public GameClient removeWaitingClient(String account) {
    this.writeLock.lock();

    GameClient var2;
    try {
      var2 = this.waitingClients.remove(account);
    } finally {
      this.writeLock.unlock();
    }

    return var2;
  }

  public GameClient addAuthedClient(GameClient client) {
    this.writeLock.lock();

    GameClient var2;
    try {
      var2 = this.authedClients.put(client.getLogin(), client);
    } finally {
      this.writeLock.unlock();
    }

    return var2;
  }

  public GameClient removeAuthedClient(String login) {
    this.writeLock.lock();

    GameClient var2;
    try {
      var2 = this.authedClients.remove(login);
    } finally {
      this.writeLock.unlock();
    }

    return var2;
  }

  public GameClient getAuthedClient(String login) {
    this.readLock.lock();

    GameClient var2;
    try {
      var2 = this.authedClients.get(login);
    } finally {
      this.readLock.unlock();
    }

    return var2;
  }

  public GameClient removeClient(GameClient client) {
    this.writeLock.lock();

    GameClient var2;
    try {
      if (client.isAuthed()) {
        var2 = this.authedClients.remove(client.getLogin());
        return var2;
      }

      var2 = this.waitingClients.remove(client.getSessionKey());
    } finally {
      this.writeLock.unlock();
    }

    return var2;
  }

  public String[] getAccounts() {
    this.readLock.lock();

    String[] var1;
    try {
      var1 = this.authedClients.keySet().toArray(new String[0]);
    } finally {
      this.readLock.unlock();
    }

    return var1;
  }
}
