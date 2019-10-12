//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectorThread<T extends MMOClient> extends Thread {
    private static final Logger _log = LoggerFactory.getLogger(SelectorThread.class);
    private final Selector _selector = Selector.open();
    private final IPacketHandler<T> _packetHandler;
    private final IMMOExecutor<T> _executor;
    private final IClientFactory<T> _clientFactory;
    private IAcceptFilter _acceptFilter;
    private boolean _shutdown;
    private final SelectorConfig _sc;
    private final int HELPER_BUFFER_SIZE;
    private ByteBuffer DIRECT_WRITE_BUFFER;
    private final ByteBuffer WRITE_BUFFER;
    private final ByteBuffer READ_BUFFER;
    private T WRITE_CLIENT;
    private final Queue<ByteBuffer> _bufferPool;
    private final List<MMOConnection<T>> _connections;
    private static final List<SelectorThread> ALL_SELECTORS = new ArrayList();
    private static SelectorStats stats = new SelectorStats();
    public static long MAX_CONNECTIONS = 9223372036854775807L;

    public SelectorThread(SelectorConfig sc, IPacketHandler<T> packetHandler, IMMOExecutor<T> executor, IClientFactory<T> clientFactory, IAcceptFilter acceptFilter) throws IOException {
        synchronized(ALL_SELECTORS) {
            ALL_SELECTORS.add(this);
        }

        this._sc = sc;
        this._acceptFilter = acceptFilter;
        this._packetHandler = packetHandler;
        this._clientFactory = clientFactory;
        this._executor = executor;
        this._bufferPool = new ArrayDeque(this._sc.HELPER_BUFFER_COUNT);
        this._connections = new CopyOnWriteArrayList();
        this.DIRECT_WRITE_BUFFER = ByteBuffer.wrap(new byte[this._sc.WRITE_BUFFER_SIZE]).order(this._sc.BYTE_ORDER);
        this.WRITE_BUFFER = ByteBuffer.wrap(new byte[this._sc.WRITE_BUFFER_SIZE]).order(this._sc.BYTE_ORDER);
        this.READ_BUFFER = ByteBuffer.wrap(new byte[this._sc.READ_BUFFER_SIZE]).order(this._sc.BYTE_ORDER);
        this.HELPER_BUFFER_SIZE = Math.max(this._sc.READ_BUFFER_SIZE, this._sc.WRITE_BUFFER_SIZE);

        for(int i = 0; i < this._sc.HELPER_BUFFER_COUNT; ++i) {
            this._bufferPool.add(ByteBuffer.wrap(new byte[this.HELPER_BUFFER_SIZE]).order(this._sc.BYTE_ORDER));
        }

    }

    public void openServerSocket(InetAddress address, int tcpPort) throws IOException {
        ServerSocketChannel selectable = ServerSocketChannel.open();
        selectable.configureBlocking(false);
        selectable.socket().bind(address == null ? new InetSocketAddress(tcpPort) : new InetSocketAddress(address, tcpPort));
        selectable.register(this.getSelector(), selectable.validOps());
        this.setName("SelectorThread:" + selectable.socket().getLocalPort());
    }

    protected ByteBuffer getPooledBuffer() {
        return this._bufferPool.isEmpty() ? ByteBuffer.wrap(new byte[this.HELPER_BUFFER_SIZE]).order(this._sc.BYTE_ORDER) : (ByteBuffer)this._bufferPool.poll();
    }

    protected void recycleBuffer(ByteBuffer buf) {
        if (this._bufferPool.size() < this._sc.HELPER_BUFFER_COUNT) {
            buf.clear();
            this._bufferPool.add(buf);
        }

    }

    protected void freeBuffer(ByteBuffer buf, MMOConnection<T> con) {
        if (buf == this.READ_BUFFER) {
            this.READ_BUFFER.clear();
        } else {
            con.setReadBuffer((ByteBuffer)null);
            this.recycleBuffer(buf);
        }

    }

    public void run() {
        int totalKeys = false;
        Set<SelectionKey> keys = null;
        Iterator<SelectionKey> itr = null;
        Iterator<MMOConnection<T>> conItr = null;
        SelectionKey key = null;
        MMOConnection<T> con = null;
        long currentMillis = 0L;

        while(true) {
            while(true) {
                try {
                    if (this.isShuttingDown()) {
                        this.closeSelectorThread();
                        return;
                    }

                    currentMillis = System.currentTimeMillis();
                    conItr = this._connections.iterator();

                    while(true) {
                        while(conItr.hasNext()) {
                            con = (MMOConnection)conItr.next();
                            if (con.isPengingClose() && (!con.isPendingWrite() || currentMillis - con.getPendingCloseTime() >= 10000L)) {
                                this.closeConnectionImpl(con);
                            } else if (con.isPendingWrite() && currentMillis - con.getPendingWriteTime() >= this._sc.INTEREST_DELAY) {
                                con.enableWriteInterest();
                            }
                        }

                        int totalKeys = this.getSelector().selectNow();
                        if (totalKeys > 0) {
                            keys = this.getSelector().selectedKeys();
                            itr = keys.iterator();

                            while(itr.hasNext()) {
                                key = (SelectionKey)itr.next();
                                itr.remove();
                                if (key.isValid()) {
                                    try {
                                        if (key.isAcceptable()) {
                                            this.acceptConnection(key);
                                        } else if (key.isConnectable()) {
                                            this.finishConnection(key);
                                        } else {
                                            if (key.isReadable()) {
                                                this.readPacket(key);
                                            }

                                            if (key.isValid() && key.isWritable()) {
                                                this.writePacket(key);
                                            }
                                        }
                                    } catch (CancelledKeyException var13) {
                                    }
                                }
                            }
                        }

                        try {
                            Thread.sleep(this._sc.SLEEP_TIME);
                        } catch (InterruptedException var12) {
                        }
                        break;
                    }
                } catch (IOException var14) {
                    _log.error("Error in " + this.getName(), var14);

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var11) {
                    }
                }
            }
        }
    }

    protected void finishConnection(SelectionKey key) {
        try {
            ((SocketChannel)key.channel()).finishConnect();
        } catch (IOException var5) {
            MMOConnection<T> con = (MMOConnection)key.attachment();
            T client = con.getClient();
            client.getConnection().onForcedDisconnection();
            this.closeConnectionImpl(client.getConnection());
        }

    }

    protected void acceptConnection(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();

        SocketChannel sc;
        try {
            while((sc = ssc.accept()) != null) {
                if (this.getAcceptFilter() != null && !this.getAcceptFilter().accept(sc)) {
                    sc.close();
                } else {
                    sc.configureBlocking(false);
                    SelectionKey clientKey = sc.register(this.getSelector(), 1);
                    MMOConnection<T> con = new MMOConnection(this, sc.socket(), clientKey);
                    T client = this.getClientFactory().create(con);
                    client.setConnection(con);
                    con.setClient(client);
                    clientKey.attach(con);
                    this._connections.add(con);
                    stats.increaseOpenedConnections();
                }
            }
        } catch (IOException var7) {
            _log.error("Error in " + this.getName(), var7);
        }

    }

    protected void readPacket(SelectionKey key) {
        MMOConnection<T> con = (MMOConnection)key.attachment();
        if (!con.isClosed()) {
            int result = -2;
            ByteBuffer buf;
            if ((buf = con.getReadBuffer()) == null) {
                buf = this.READ_BUFFER;
            }

            if (buf.position() == buf.limit()) {
                _log.error("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == this.READ_BUFFER) + ". Closing connection.");
                this.closeConnectionImpl(con);
            } else {
                try {
                    result = con.getReadableByteChannel().read(buf);
                } catch (IOException var6) {
                }

                if (result > 0) {
                    buf.flip();
                    stats.increaseIncomingBytes(result);

                    for(int var5 = 0; this.tryReadPacket2(key, con, buf); ++var5) {
                    }
                } else if (result == 0) {
                    this.closeConnectionImpl(con);
                } else if (result == -1) {
                    this.closeConnectionImpl(con);
                } else {
                    con.onForcedDisconnection();
                    this.closeConnectionImpl(con);
                }
            }

            if (buf == this.READ_BUFFER) {
                buf.clear();
            }

        }
    }

    protected boolean tryReadPacket2(SelectionKey key, MMOConnection<T> con, ByteBuffer buf) {
        if (con.isClosed()) {
            return false;
        } else {
            int pos = buf.position();
            if (buf.remaining() > this._sc.HEADER_SIZE) {
                int size = buf.getShort() & '\uffff';
                if (size <= this._sc.HEADER_SIZE || size > this._sc.PACKET_SIZE) {
                    _log.error("Incorrect packet size : " + size + "! Client : " + con.getClient() + ". Closing connection.");
                    this.closeConnectionImpl(con);
                    return false;
                }

                size -= this._sc.HEADER_SIZE;
                if (size <= buf.remaining()) {
                    stats.increaseIncomingPacketsCount();
                    this.parseClientPacket(this.getPacketHandler(), buf, size, con);
                    buf.position(pos + size + this._sc.HEADER_SIZE);
                    if (!buf.hasRemaining()) {
                        this.freeBuffer(buf, con);
                        return false;
                    }

                    return true;
                }

                buf.position(pos);
            }

            if (pos == buf.capacity()) {
                _log.warn("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == this.READ_BUFFER) + ".");
            }

            if (buf == this.READ_BUFFER) {
                this.allocateReadBuffer(con);
            } else {
                buf.compact();
            }

            return false;
        }
    }

    protected void allocateReadBuffer(MMOConnection<T> con) {
        con.setReadBuffer(this.getPooledBuffer().put(this.READ_BUFFER));
        this.READ_BUFFER.clear();
    }

    protected boolean parseClientPacket(IPacketHandler<T> handler, ByteBuffer buf, int dataSize, MMOConnection<T> con) {
        T client = con.getClient();
        int pos = buf.position();
        client.decrypt(buf, dataSize);
        buf.position(pos);
        if (buf.hasRemaining()) {
            int limit = buf.limit();
            buf.limit(pos + dataSize);
            ReceivablePacket<T> rp = handler.handlePacket(buf, client);
            if (rp != null) {
                rp.setByteBuffer(buf);
                rp.setClient(client);
                if (rp.read()) {
                    con.recvPacket(rp);
                }

                rp.setByteBuffer((ByteBuffer)null);
            }

            buf.limit(limit);
        }

        return true;
    }

    protected void writePacket(SelectionKey key) {
        MMOConnection<T> con = (MMOConnection)key.attachment();
        this.prepareWriteBuffer(con);
        this.DIRECT_WRITE_BUFFER.flip();
        int size = this.DIRECT_WRITE_BUFFER.remaining();
        int result = -1;

        try {
            result = con.getWritableChannel().write(this.DIRECT_WRITE_BUFFER);
        } catch (IOException var6) {
        }

        if (result >= 0) {
            stats.increaseOutgoingBytes(result);
            if (result != size) {
                con.createWriteBuffer(this.DIRECT_WRITE_BUFFER);
            }

            if (!con.getSendQueue().isEmpty() || con.hasPendingWriteBuffer()) {
                con.scheduleWriteInterest();
            }
        } else {
            con.onForcedDisconnection();
            this.closeConnectionImpl(con);
        }

    }

    protected T getWriteClient() {
        return this.WRITE_CLIENT;
    }

    protected ByteBuffer getWriteBuffer() {
        return this.WRITE_BUFFER;
    }

    protected void prepareWriteBuffer(MMOConnection<T> con) {
        this.WRITE_CLIENT = con.getClient();
        this.DIRECT_WRITE_BUFFER.clear();
        if (con.hasPendingWriteBuffer()) {
            con.movePendingWriteBufferTo(this.DIRECT_WRITE_BUFFER);
        }

        if (this.DIRECT_WRITE_BUFFER.hasRemaining() && !con.hasPendingWriteBuffer()) {
            Queue<SendablePacket<T>> sendQueue = con.getSendQueue();

            for(int i = 0; i < this._sc.MAX_SEND_PER_PASS; ++i) {
                SendablePacket sp;
                synchronized(con) {
                    if ((sp = (SendablePacket)sendQueue.poll()) == null) {
                        break;
                    }
                }

                try {
                    stats.increaseOutgoingPacketsCount();
                    this.putPacketIntoWriteBuffer(sp, true);
                    this.WRITE_BUFFER.flip();
                    if (this.DIRECT_WRITE_BUFFER.remaining() < this.WRITE_BUFFER.limit()) {
                        con.createWriteBuffer(this.WRITE_BUFFER);
                        break;
                    }

                    this.DIRECT_WRITE_BUFFER.put(this.WRITE_BUFFER);
                } catch (Exception var7) {
                    _log.error("Error in " + this.getName(), var7);
                    break;
                }
            }
        }

        this.WRITE_BUFFER.clear();
        this.WRITE_CLIENT = null;
    }

    protected final void putPacketIntoWriteBuffer(SendablePacket<T> sp, boolean encrypt) {
        this.WRITE_BUFFER.clear();
        int headerPos = this.WRITE_BUFFER.position();
        this.WRITE_BUFFER.position(headerPos + this._sc.HEADER_SIZE);
        sp.write();
        int dataSize = this.WRITE_BUFFER.position() - headerPos - this._sc.HEADER_SIZE;
        if (dataSize == 0) {
            this.WRITE_BUFFER.position(headerPos);
        } else {
            this.WRITE_BUFFER.position(headerPos + this._sc.HEADER_SIZE);
            if (encrypt) {
                this.WRITE_CLIENT.encrypt(this.WRITE_BUFFER, dataSize);
                dataSize = this.WRITE_BUFFER.position() - headerPos - this._sc.HEADER_SIZE;
            }

            this.WRITE_BUFFER.position(headerPos);
            this.WRITE_BUFFER.putShort((short)(this._sc.HEADER_SIZE + dataSize));
            this.WRITE_BUFFER.position(headerPos + this._sc.HEADER_SIZE + dataSize);
        }
    }

    protected SelectorConfig getConfig() {
        return this._sc;
    }

    protected Selector getSelector() {
        return this._selector;
    }

    protected IMMOExecutor<T> getExecutor() {
        return this._executor;
    }

    protected IPacketHandler<T> getPacketHandler() {
        return this._packetHandler;
    }

    protected IClientFactory<T> getClientFactory() {
        return this._clientFactory;
    }

    public void setAcceptFilter(IAcceptFilter acceptFilter) {
        this._acceptFilter = acceptFilter;
    }

    protected IAcceptFilter getAcceptFilter() {
        return this._acceptFilter;
    }

    protected void closeConnectionImpl(MMOConnection<T> con) {
        try {
            con.onDisconnection();
        } finally {
            try {
                con.close();
            } catch (IOException var22) {
            } finally {
                con.releaseBuffers();
                con.clearQueues();
                con.getClient().setConnection((MMOConnection)null);
                con.getSelectionKey().attach((Object)null);
                con.getSelectionKey().cancel();
                this._connections.remove(con);
                stats.decreseOpenedConnections();
            }

        }

    }

    public void shutdown() {
        this._shutdown = true;
    }

    public boolean isShuttingDown() {
        return this._shutdown;
    }

    protected void closeAllChannels() {
        Set<SelectionKey> keys = this.getSelector().keys();
        Iterator var2 = keys.iterator();

        while(var2.hasNext()) {
            SelectionKey key = (SelectionKey)var2.next();

            try {
                key.channel().close();
            } catch (IOException var5) {
            }
        }

    }

    protected void closeSelectorThread() {
        this.closeAllChannels();

        try {
            this.getSelector().close();
        } catch (IOException var2) {
        }

    }

    public static CharSequence getStats() {
        StringBuilder list = new StringBuilder();
        list.append("selectorThreadCount: .... ").append(ALL_SELECTORS.size()).append("\n");
        list.append("=================================================\n");
        list.append("getTotalConnections: .... ").append(stats.getTotalConnections()).append("\n");
        list.append("getCurrentConnections: .. ").append(stats.getCurrentConnections()).append("\n");
        list.append("getMaximumConnections: .. ").append(stats.getMaximumConnections()).append("\n");
        list.append("getIncomingBytesTotal: .. ").append(stats.getIncomingBytesTotal()).append("\n");
        list.append("getOutgoingBytesTotal: .. ").append(stats.getOutgoingBytesTotal()).append("\n");
        list.append("getIncomingPacketsTotal:  ").append(stats.getIncomingPacketsTotal()).append("\n");
        list.append("getOutgoingPacketsTotal:  ").append(stats.getOutgoingPacketsTotal()).append("\n");
        list.append("getMaxBytesPerRead: ..... ").append(stats.getMaxBytesPerRead()).append("\n");
        list.append("getMaxBytesPerWrite: .... ").append(stats.getMaxBytesPerWrite()).append("\n");
        list.append("=================================================\n");
        return list;
    }
}
