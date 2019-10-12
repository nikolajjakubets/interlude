//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicEnumBitFlag<E extends Enum<E>> {
    private final AtomicLong _field = new AtomicLong();

    public AtomicEnumBitFlag() {
    }

    public boolean set(E emask, boolean val) {
        int o = emask.ordinal();
        if (o > 63) {
            throw new IllegalArgumentException("Maxium 64 enum values allowed");
        } else {
            long mask = 1L << o;

            long c;
            long n;
            do {
                c = this._field.get();
                n = val ? c | mask : c & ~mask;
            } while(!this._field.compareAndSet(c, n));

            return (c & mask) != 0L;
        }
    }

    public boolean get(E emask) {
        int o = emask.ordinal();
        if (o > 63) {
            throw new IllegalArgumentException("Maxium 64 enum values allowed");
        } else {
            long mask = 1L << o;
            return (this._field.get() & mask) != 0L;
        }
    }
}
