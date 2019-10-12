//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.net.utils;

import java.util.ArrayList;
import java.util.Iterator;

public final class NetList extends ArrayList<Net> {
    private static final long serialVersionUID = 4266033257195615387L;

    public NetList() {
    }

    public boolean isInRange(String address) {
        Iterator var2 = this.iterator();

        Net net;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            net = (Net)var2.next();
        } while(!net.isInRange(address));

        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator itr = this.iterator();

        while(itr.hasNext()) {
            sb.append(itr.next());
            if (itr.hasNext()) {
                sb.append(',');
            }
        }

        return sb.toString();
    }
}
