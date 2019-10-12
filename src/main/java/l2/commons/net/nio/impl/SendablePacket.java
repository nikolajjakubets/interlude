//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class SendablePacket<T extends MMOClient> extends l2.commons.net.nio.SendablePacket<T> {
    public SendablePacket() {
    }

    protected ByteBuffer getByteBuffer() {
        return ((SelectorThread)Thread.currentThread()).getWriteBuffer();
    }

    //TODO: i add cast
    public T getClient() {
        return (T) ((SelectorThread)Thread.currentThread()).getWriteClient();
    }

    protected abstract boolean write();
}
