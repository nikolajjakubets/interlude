//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.commons.lang3.tuple.Pair;

public class RandomUtils {
    public static final Comparator<Pair<?, Double>> DOUBLE_GROUP_COMPARATOR = new Comparator<Pair<?, Double>>() {
        public int compare(Pair<?, Double> o1, Pair<?, Double> o2) {
            double v = o1.getRight() - o2.getRight();
            return Double.compare(v, 0.0D);
        }
    };

    public RandomUtils() {
    }

    public static <G> G pickRandomSortedGroup(Collection<Pair<G, Double>> sortedGroups, double total) {
        double r = total * Rnd.get();
        double share = 0.0D;
        Iterator var7 = sortedGroups.iterator();

        Pair group;
        do {
            if (!var7.hasNext()) {
                return null;
            }

            group = (Pair)var7.next();
            share += (Double)group.getRight();
        } while(r > share);

        //TODO: i add cast
        return (G) group.getLeft();
    }
}
