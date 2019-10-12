//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio;

public abstract class ReceivablePacket<T> extends AbstractPacket<T> implements Runnable {
    public ReceivablePacket() {
    }

    protected int getAvaliableBytes() {
        return this.getByteBuffer().remaining();
    }

    protected void readB(byte[] dst) {
        this.getByteBuffer().get(dst);
    }

    protected void readB(byte[] dst, int offset, int len) {
        this.getByteBuffer().get(dst, offset, len);
    }

    protected int readC() {
        return this.getByteBuffer().get() & 255;
    }

    protected int readH() {
        return this.getByteBuffer().getShort() & '\uffff';
    }

    protected int readD() {
        return this.getByteBuffer().getInt();
    }

    protected long readUD() {
        return (long)this.getByteBuffer().getInt() & 4294967295L;
    }

    protected long readQ() {
        return this.getByteBuffer().getLong();
    }

    protected double readF() {
        return this.getByteBuffer().getDouble();
    }

    protected String readS() {
        StringBuilder sb = new StringBuilder();

        char ch;
        while((ch = this.getByteBuffer().getChar()) != 0) {
            sb.append(ch);
        }

        return sb.toString();
    }

    protected abstract boolean read();
}
