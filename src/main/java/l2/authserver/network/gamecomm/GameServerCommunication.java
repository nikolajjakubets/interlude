package l2.authserver.network.gamecomm;

import l2.authserver.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;

@Slf4j
public class GameServerCommunication extends Thread {
    private static final GameServerCommunication instance = new GameServerCommunication();
    private final ByteBuffer writeBuffer;
    private Selector selector;
    private boolean shutdown;

    public static GameServerCommunication getInstance() {
        return instance;
    }

    private GameServerCommunication() {
        this.writeBuffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
    }

    public void openServerSocket(InetAddress address, int tcpPort) throws IOException {
        this.selector = Selector.open();
        ServerSocketChannel selectable = ServerSocketChannel.open();
        selectable.configureBlocking(false);
        selectable.socket().bind(address == null ? new InetSocketAddress(tcpPort) : new InetSocketAddress(address, tcpPort));
        selectable.register(this.selector, selectable.validOps());
    }

    public void run() {
        SelectionKey key = null;

        while(!this.isShutdown()) {
            try {
                this.selector.select();
                Set<SelectionKey> keys = this.selector.selectedKeys();
                Iterator iterator = keys.iterator();

                while(iterator.hasNext()) {
                    key = (SelectionKey)iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        this.close(key);
                    } else {
                        int opts = key.readyOps();
                        switch(opts) {
                            case 1:
                                this.read(key);
                                break;
                            case 4:
                                this.write(key);
                                break;
                            case 5:
                                this.write(key);
                                this.read(key);
                                break;
                            case 8:
                                this.close(key);
                                break;
                            case 16:
                                this.accept(key);
                        }
                    }
                }
            } catch (ClosedSelectorException e) {
                log.error("run: Selector={} closed!", this.selector);
                return;
            } catch (IOException e) {
                log.error("run: Gameserver I/O error={} " + e.getMessage());
                this.close(key);
            } catch (Exception e) {
              log.error("Exception: eMessage={}, eClass={}, eCause={}", e.getMessage(), e.getClass(), this.getClass().getSimpleName());

            }
        }

    }

    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        SelectionKey clientKey = sc.register(this.selector, 1);
        GameServerConnection conn;
        clientKey.attach(conn = new GameServerConnection(clientKey));
        conn.setGameServer(new GameServer(conn));
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel)key.channel();
        GameServerConnection conn = (GameServerConnection)key.attachment();
        GameServer gs = conn.getGameServer();
        ByteBuffer buf = conn.getReadBuffer();
        int count = channel.read(buf);
        if (count == -1) {
            this.close(key);
        } else if (count != 0) {
            buf.flip();

            while(this.tryReadPacket(key, gs, buf)) {
            }

        }
    }

    protected boolean tryReadPacket(SelectionKey key, GameServer gs, ByteBuffer buf) throws IOException {
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
                ReceivablePacket rp = PacketHandler.handlePacket(gs, buf);
                if (rp != null) {
                    rp.setByteBuffer(buf);
                    rp.setClient(gs);
                    if (rp.read()) {
                        ThreadPoolManager.getInstance().execute(rp);
                    }

                    rp.setByteBuffer(null);
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

    public void write(SelectionKey key) throws IOException {
        GameServerConnection conn = (GameServerConnection)key.attachment();
        GameServer gs = conn.getGameServer();
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buf = this.getWriteBuffer();
        conn.disableWriteInterest();
        Queue<SendablePacket> sendQueue = conn.sendQueue;
        Lock sendLock = conn.sendLock;
        sendLock.lock();

        boolean done;
        try {
            int var9 = 0;

            SendablePacket sp;
            while (var9++ < 64 && (sp = sendQueue.poll()) != null) {
                int headerPos = buf.position();
                buf.position(headerPos + 2);
                sp.setByteBuffer(buf);
                sp.setClient(gs);
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

            done = sendQueue.isEmpty();
            if (done) {
                conn.disableWriteInterest();
            }
        } finally {
            sendLock.unlock();
        }

        buf.flip();
        channel.write(buf);
        if (buf.remaining() > 0) {
            buf.compact();
            done = false;
        } else {
            buf.clear();
        }

        if (!done && conn.enableWriteInterest()) {
            this.selector.wakeup();
        }

    }

    private ByteBuffer getWriteBuffer() {
        return this.writeBuffer;
    }

    public void close(SelectionKey key) {
        if (key != null) {
            try {
                try {
                    GameServerConnection conn = (GameServerConnection)key.attachment();
                    if (conn != null) {
                        conn.onDisconnection();
                    }
                } finally {
                    key.channel().close();
                    key.cancel();
                }
            } catch (IOException e) {
              log.error("close: restore: eMessage={}, eClass={}", e.getMessage(), e.getClass());
            }

        }
    }

    public boolean isShutdown() {
        return this.shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
