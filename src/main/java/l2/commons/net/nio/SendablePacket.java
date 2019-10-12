//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio;

public abstract class SendablePacket<T> extends AbstractPacket<T> {
    public SendablePacket() {
    }

    protected void writeC(int data) {
        this.getByteBuffer().put((byte)data);
    }

    protected void writeF(double value) {
        this.getByteBuffer().putDouble(value);
    }

    protected void writeH(int value) {
        this.getByteBuffer().putShort((short)value);
    }

    protected void writeD(int value) {
        this.getByteBuffer().putInt(value);
    }

    protected void writeQ(long value) {
        this.getByteBuffer().putLong(value);
    }

    protected void writeB(byte[] data) {
        this.getByteBuffer().put(data);
    }

    protected void writeS(CharSequence charSequence) {
        if (charSequence != null) {
            int length = charSequence.length();

            for(int i = 0; i < length; ++i) {
                this.getByteBuffer().putChar(charSequence.charAt(i));
            }
        }

        this.getByteBuffer().putChar('\u0000');
    }

    protected abstract boolean write();
}
