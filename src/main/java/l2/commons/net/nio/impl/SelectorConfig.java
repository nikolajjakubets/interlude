//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.nio.impl;

import java.nio.ByteOrder;

public class SelectorConfig {
    public int READ_BUFFER_SIZE = 65536;
    public int WRITE_BUFFER_SIZE = 131072;
    public int MAX_SEND_PER_PASS = 32;
    public long SLEEP_TIME = 10L;
    public long INTEREST_DELAY = 30L;
    public int HEADER_SIZE = 2;
    public int PACKET_SIZE = 32768;
    public int HELPER_BUFFER_COUNT = 64;
    public ByteOrder BYTE_ORDER;

    public SelectorConfig() {
        this.BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    }
}