//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MMOConnection<T extends MMOClient> {
    private final SelectorThread<T> _selectorThread;
    private final SelectionKey _selectionKey;
    private final Socket _socket;
    private final WritableByteChannel _writableByteChannel;
    private final ReadableByteChannel _readableByteChannel;
    private final Queue<SendablePacket<T>> _sendQueue;
    private final Queue<ReceivablePacket<T>> _recvQueue;
    private T _client;
    private ByteBuffer _readBuffer;
    private ByteBuffer _primaryWriteBuffer;
    private ByteBuffer _secondaryWriteBuffer;
    private boolean _pendingClose;
    private long _pendingCloseTime;
    private boolean _closed;
    private long _pendingWriteTime;
    private AtomicBoolean _isPengingWrite = new AtomicBoolean();

    public MMOConnection(SelectorThread<T> selectorThread, Socket socket, SelectionKey key) {
        this._selectorThread = selectorThread;
        this._selectionKey = key;
        this._socket = socket;
        this._writableByteChannel = socket.getChannel();
        this._readableByteChannel = socket.getChannel();
        this._sendQueue = new ArrayDeque();
        this._recvQueue = new MMOExecutableQueue(selectorThread.getExecutor());
    }

    protected void setClient(T client) {
        this._client = client;
    }

    public T getClient() {
        return this._client;
    }

    public void recvPacket(ReceivablePacket<T> rp) {
        if (rp != null) {
            if (!this.isClosed()) {
                this._recvQueue.add(rp);
            }
        }
    }

    public void sendPacket(SendablePacket<T> sp) {
        if (sp != null) {
            synchronized(this) {
                if (this.isClosed()) {
                    return;
                }

                this._sendQueue.add(sp);
            }

            this.scheduleWriteInterest();
        }
    }

    public void sendPacket(SendablePacket<T>... args) {
        if (args != null && args.length != 0) {
            synchronized(this) {
                if (this.isClosed()) {
                    return;
                }

                SendablePacket[] var3 = args;
                int var4 = args.length;
                int var5 = 0;

                while(true) {
                    if (var5 >= var4) {
                        break;
                    }

                    SendablePacket<T> sp = var3[var5];
                    if (sp != null) {
                        this._sendQueue.add(sp);
                    }

                    ++var5;
                }
            }

            this.scheduleWriteInterest();
        }
    }

    public void sendPackets(List<? extends SendablePacket<T>> args) {
        if (args != null && !args.isEmpty()) {
            synchronized(this) {
                if (this.isClosed()) {
                    return;
                }

                int i = 0;

                while(true) {
                    if (i >= args.size()) {
                        break;
                    }

                    SendablePacket sp;
                    if ((sp = (SendablePacket)args.get(i)) != null) {
                        this._sendQueue.add(sp);
                    }

                    ++i;
                }
            }

            this.scheduleWriteInterest();
        }
    }

    protected SelectionKey getSelectionKey() {
        return this._selectionKey;
    }

    protected void disableReadInterest() {
        try {
            this._selectionKey.interestOps(this._selectionKey.interestOps() & -2);
        } catch (CancelledKeyException var2) {
        }

    }

    protected void scheduleWriteInterest() {
        try {
            if (this._isPengingWrite.compareAndSet(false, true)) {
                this._pendingWriteTime = System.currentTimeMillis();
            }
        } catch (CancelledKeyException var2) {
        }

    }

    protected void disableWriteInterest() {
        try {
            if (this._isPengingWrite.compareAndSet(true, false)) {
                this._selectionKey.interestOps(this._selectionKey.interestOps() & -5);
            }
        } catch (CancelledKeyException var2) {
        }

    }

    protected void enableWriteInterest() {
        if (this._isPengingWrite.compareAndSet(true, false)) {
            this._selectionKey.interestOps(this._selectionKey.interestOps() | 4);
        }

    }

    protected boolean isPendingWrite() {
        return this._isPengingWrite.get();
    }

    public long getPendingWriteTime() {
        return this._pendingWriteTime;
    }

    public Socket getSocket() {
        return this._socket;
    }

    public WritableByteChannel getWritableChannel() {
        return this._writableByteChannel;
    }

    public ReadableByteChannel getReadableByteChannel() {
        return this._readableByteChannel;
    }

    protected Queue<SendablePacket<T>> getSendQueue() {
        return this._sendQueue;
    }

    protected Queue<ReceivablePacket<T>> getRecvQueue() {
        return this._recvQueue;
    }

    protected void createWriteBuffer(ByteBuffer buf) {
        if (this._primaryWriteBuffer == null) {
            this._primaryWriteBuffer = this._selectorThread.getPooledBuffer();
            this._primaryWriteBuffer.put(buf);
        } else {
            ByteBuffer temp = this._selectorThread.getPooledBuffer();
            temp.put(buf);
            int remaining = temp.remaining();
            this._primaryWriteBuffer.flip();
            int limit = this._primaryWriteBuffer.limit();
            if (remaining >= this._primaryWriteBuffer.remaining()) {
                temp.put(this._primaryWriteBuffer);
                this._selectorThread.recycleBuffer(this._primaryWriteBuffer);
                this._primaryWriteBuffer = temp;
            } else {
                this._primaryWriteBuffer.limit(remaining);
                temp.put(this._primaryWriteBuffer);
                this._primaryWriteBuffer.limit(limit);
                this._primaryWriteBuffer.compact();
                this._secondaryWriteBuffer = this._primaryWriteBuffer;
                this._primaryWriteBuffer = temp;
            }
        }

    }

    protected boolean hasPendingWriteBuffer() {
        return this._primaryWriteBuffer != null;
    }

    protected void movePendingWriteBufferTo(ByteBuffer dest) {
        this._primaryWriteBuffer.flip();
        dest.put(this._primaryWriteBuffer);
        this._selectorThread.recycleBuffer(this._primaryWriteBuffer);
        this._primaryWriteBuffer = this._secondaryWriteBuffer;
        this._secondaryWriteBuffer = null;
    }

    protected void setReadBuffer(ByteBuffer buf) {
        this._readBuffer = buf;
    }

    public ByteBuffer getReadBuffer() {
        return this._readBuffer;
    }

    public boolean isClosed() {
        return this._pendingClose || this._closed;
    }

    public boolean isPengingClose() {
        return this._pendingClose;
    }

    public long getPendingCloseTime() {
        return this._pendingCloseTime;
    }

    protected void close() throws IOException {
        this._closed = true;
        this._socket.close();
    }

    protected void closeNow() {
        synchronized(this) {
            if (this.isClosed()) {
                return;
            }

            this._sendQueue.clear();
            this._pendingClose = true;
            this._pendingCloseTime = System.currentTimeMillis();
        }

        this.disableReadInterest();
        this.disableWriteInterest();
    }

    public void close(SendablePacket<T> sp) {
        synchronized(this) {
            if (this.isClosed()) {
                return;
            }

            this._sendQueue.clear();
            this.sendPacket(sp);
            this._pendingClose = true;
            this._pendingCloseTime = System.currentTimeMillis();
        }

        this.disableReadInterest();
    }

    protected void closeLater() {
        synchronized(this) {
            if (!this.isClosed()) {
                this._pendingClose = true;
                this._pendingCloseTime = System.currentTimeMillis();
            }
        }
    }

    protected void releaseBuffers() {
        if (this._primaryWriteBuffer != null) {
            this._selectorThread.recycleBuffer(this._primaryWriteBuffer);
            this._primaryWriteBuffer = null;
            if (this._secondaryWriteBuffer != null) {
                this._selectorThread.recycleBuffer(this._secondaryWriteBuffer);
                this._secondaryWriteBuffer = null;
            }
        }

        if (this._readBuffer != null) {
            this._selectorThread.recycleBuffer(this._readBuffer);
            this._readBuffer = null;
        }

    }

    protected void clearQueues() {
        this._sendQueue.clear();
        this._recvQueue.clear();
    }

    protected void onDisconnection() {
        this.getClient().onDisconnection();
    }

    protected void onForcedDisconnection() {
        this.getClient().onForcedDisconnection();
    }

    public String toString() {
        return "MMOConnection: selector=" + this._selectorThread + "; client=" + this.getClient();
    }
}
