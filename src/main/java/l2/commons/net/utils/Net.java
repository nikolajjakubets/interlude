//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.utils;

public class Net {
    private final int address;
    private final int netmask;

    public Net(int net, int mask) {
        this.address = net;
        this.netmask = mask;
    }

    public int address() {
        return this.address;
    }

    public int netmask() {
        return this.netmask;
    }

    public boolean isInRange(int address) {
        return (address & this.netmask) == this.address;
    }

    public boolean isInRange(String address) {
        return this.isInRange(parseAddress(address));
    }

    public static Net valueOf(String s) {
        int address = 0;
        int netmask = 0;
        String[] mask = s.trim().split("\\b\\/\\b");
        if (mask.length >= 1 && mask.length <= 2) {
            if (mask.length == 1) {
                String[] octets = mask[0].split("\\.");
                if (octets.length < 1 || octets.length > 4) {
                    throw new IllegalArgumentException("For input string: \"" + s + "\"");
                }

                for(int i = 1; i <= octets.length; ++i) {
                    if (!octets[i - 1].equals("*")) {
                        address |= Integer.parseInt(octets[i - 1]) << 32 - i * 8;
                        netmask |= 255 << 32 - i * 8;
                    }
                }
            } else {
                address = parseAddress(mask[0]);
                netmask = parseNetmask(mask[1]);
            }

            return new Net(address, netmask);
        } else {
            throw new IllegalArgumentException("For input string: \"" + s + "\"");
        }
    }

    public static int parseAddress(String s) throws IllegalArgumentException {
        int ip = 0;
        String[] octets = s.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("For input string: \"" + s + "\"");
        } else {
            for(int i = 1; i <= octets.length; ++i) {
                ip |= Integer.parseInt(octets[i - 1]) << 32 - i * 8;
            }

            return ip;
        }
    }

    public static int parseNetmask(String s) throws IllegalArgumentException {
        int mask = 0;
        String[] octets = s.split("\\.");
        int bitmask;
        if (octets.length == 1) {
            bitmask = Integer.parseInt(octets[0]);
            if (bitmask < 0 || bitmask > 32) {
                throw new IllegalArgumentException("For input string: \"" + s + "\"");
            }

            mask = -1 << 32 - bitmask;
        } else {
            for(bitmask = 1; bitmask <= octets.length; ++bitmask) {
                mask |= Integer.parseInt(octets[bitmask - 1]) << 32 - bitmask * 8;
            }
        }

        return mask;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (!(o instanceof Net)) {
            return false;
        } else {
            return ((Net)o).address() == this.address && ((Net)o).netmask() == this.netmask;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.address >>> 24).append(".");
        sb.append(this.address << 8 >>> 24).append(".");
        sb.append(this.address << 16 >>> 24).append(".");
        sb.append(this.address << 24 >>> 24);
        sb.append("/");
        sb.append(this.netmask >>> 24).append(".");
        sb.append(this.netmask << 8 >>> 24).append(".");
        sb.append(this.netmask << 16 >>> 24).append(".");
        sb.append(this.netmask << 24 >>> 24);
        return sb.toString();
    }
}
