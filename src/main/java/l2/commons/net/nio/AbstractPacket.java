//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio;

import java.nio.ByteBuffer;

public abstract class AbstractPacket<T> {
    public AbstractPacket() {
    }

    protected abstract ByteBuffer getByteBuffer();

    public abstract T getClient();
}
