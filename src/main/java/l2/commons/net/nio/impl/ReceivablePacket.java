//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class ReceivablePacket<T extends MMOClient> extends l2.commons.net.nio.ReceivablePacket<T> {
    protected T _client;
    protected ByteBuffer _buf;

    public ReceivablePacket() {
    }

    protected void setByteBuffer(ByteBuffer buf) {
        this._buf = buf;
    }

    protected ByteBuffer getByteBuffer() {
        return this._buf;
    }

    protected void setClient(T client) {
        this._client = client;
    }

    public T getClient() {
        return this._client;
    }

    protected abstract boolean read();
}
