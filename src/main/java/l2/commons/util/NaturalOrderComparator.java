//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.util;

import java.io.File;
import java.util.Comparator;

public class NaturalOrderComparator implements Comparator {
    public static final Comparator<String> STRING_COMPARATOR = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return NaturalOrderComparator.compare0(o1, o2);
        }
    };
    public static final Comparator<File> FILE_NAME_COMPARATOR = new Comparator<File>() {
        public int compare(File o1, File o2) {
            return NaturalOrderComparator.compare0(o1.getName(), o2.getName());
        }
    };
    public static final Comparator<File> ABSOLUTE_FILE_NAME_COMPARATOR = new Comparator<File>() {
        public int compare(File o1, File o2) {
            return NaturalOrderComparator.compare0(o1.getAbsolutePath(), o2.getAbsolutePath());
        }
    };

    public NaturalOrderComparator() {
    }

    private static int compareRight(String a, String b) {
        int bias = 0;
        int ia = 0;
        int ib = 0;

        while(true) {
            char ca = charAt(a, ia);
            char cb = charAt(b, ib);
            if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
                return bias;
            }

            if (!Character.isDigit(ca)) {
                return -1;
            }

            if (!Character.isDigit(cb)) {
                return 1;
            }

            if (ca < cb) {
                if (bias == 0) {
                    bias = -1;
                }
            } else if (ca > cb) {
                if (bias == 0) {
                    bias = 1;
                }
            } else if (ca == 0 && cb == 0) {
                return bias;
            }

            ++ia;
            ++ib;
        }
    }

    public int compare(Object o1, Object o2) {
        String a = o1.toString();
        String b = o2.toString();
        return compare0(a, b);
    }

    private static int compare0(String a, String b) {
        int ia = 0;
        int ib = 0;
        int nza = false;
        boolean var5 = false;

        while(true) {
            int nzb = 0;
            int nza = 0;
            char ca = charAt(a, ia);

            char cb;
            for(cb = charAt(b, ib); Character.isSpaceChar(ca) || ca == '0'; ca = charAt(a, ia)) {
                if (ca == '0') {
                    ++nza;
                } else {
                    nza = 0;
                }

                ++ia;
            }

            while(Character.isSpaceChar(cb) || cb == '0') {
                if (cb == '0') {
                    ++nzb;
                } else {
                    nzb = 0;
                }

                ++ib;
                cb = charAt(b, ib);
            }

            int result;
            if (Character.isDigit(ca) && Character.isDigit(cb) && (result = compareRight(a.substring(ia), b.substring(ib))) != 0) {
                return result;
            }

            if (ca == 0 && cb == 0) {
                return nza - nzb;
            }

            if (ca < cb) {
                return -1;
            }

            if (ca > cb) {
                return 1;
            }

            ++ia;
            ++ib;
        }
    }

    private static char charAt(String s, int i) {
        return i >= s.length() ? '\u0000' : s.charAt(i);
    }
}
