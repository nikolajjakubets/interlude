//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StrTable {
    private final Map<Integer, Map<String, String>> rows;
    private final Map<String, Integer> columns;
    private final List<String> titles;

    public StrTable(String title) {
        this.rows = new HashMap<>();
        this.columns = new LinkedHashMap();
        this.titles = new ArrayList<>();
        if (title != null) {
            this.titles.add(title);
        }

    }

    public StrTable() {
        this((String)null);
    }

    public StrTable set(int rowIndex, String colName, boolean val) {
        return this.set(rowIndex, colName, Boolean.toString(val));
    }

    public StrTable set(int rowIndex, String colName, byte val) {
        return this.set(rowIndex, colName, Byte.toString(val));
    }

    public StrTable set(int rowIndex, String colName, char val) {
        return this.set(rowIndex, colName, String.valueOf(val));
    }

    public StrTable set(int rowIndex, String colName, short val) {
        return this.set(rowIndex, colName, Short.toString(val));
    }

    public StrTable set(int rowIndex, String colName, int val) {
        return this.set(rowIndex, colName, Integer.toString(val));
    }

    public StrTable set(int rowIndex, String colName, long val) {
        return this.set(rowIndex, colName, Long.toString(val));
    }

    public StrTable set(int rowIndex, String colName, float val) {
        return this.set(rowIndex, colName, Float.toString(val));
    }

    public StrTable set(int rowIndex, String colName, double val) {
        return this.set(rowIndex, colName, Double.toString(val));
    }

    public StrTable set(int rowIndex, String colName, Object val) {
        return this.set(rowIndex, colName, String.valueOf(val));
    }

    public StrTable set(int rowIndex, String colName, String val) {
        Object row;
        if (this.rows.containsKey(rowIndex)) {
            row = (Map)this.rows.get(rowIndex);
        } else {
            row = new HashMap<>();
            //TODO: i add cast
            this.rows.put(rowIndex, (Map<String, String>) row);
        }

        ((Map)row).put(colName, val);
        int columnSize;
        if (!this.columns.containsKey(colName)) {
            columnSize = Math.max(colName.length(), val.length());
        } else if ((Integer)this.columns.get(colName) >= (columnSize = val.length())) {
            return this;
        }

        this.columns.put(colName, columnSize);
        return this;
    }

    public StrTable addTitle(String s) {
        this.titles.add(s);
        return this;
    }

    private static StringBuilder right(StringBuilder result, String s, int sz) {
        result.append(s);
        if ((sz -= s.length()) > 0) {
            for(int i = 0; i < sz; ++i) {
                result.append(" ");
            }
        }

        return result;
    }

    private static StringBuilder center(StringBuilder result, String s, int sz) {
        int offset = result.length();
        result.append(s);

        int i;
        while((i = sz - (result.length() - offset)) > 0) {
            result.append(" ");
            if (i > 1) {
                result.insert(offset, " ");
            }
        }

        return result;
    }

    private static StringBuilder repeat(StringBuilder result, String s, int sz) {
        for(int i = 0; i < sz; ++i) {
            result.append(s);
        }

        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if (this.columns.isEmpty()) {
            return result.toString();
        } else {
            StringBuilder header = new StringBuilder("|");
            StringBuilder line = new StringBuilder("|");
            Iterator var4 = this.columns.keySet().iterator();

            String title;
            while(var4.hasNext()) {
                title = (String)var4.next();
                center(header, title, (Integer)this.columns.get(title) + 2).append("|");
                repeat(line, "-", (Integer)this.columns.get(title) + 2).append("|");
            }

            if (!this.titles.isEmpty()) {
                result.append(" ");
                repeat(result, "-", header.length() - 2).append(" ").append("\n");
                var4 = this.titles.iterator();

                while(var4.hasNext()) {
                    title = (String)var4.next();
                    result.append("| ");
                    right(result, title, header.length() - 3).append("|").append("\n");
                }
            }

            result.append(" ");
            repeat(result, "-", header.length() - 2).append(" ").append("\n");
            result.append(header).append("\n");
            result.append(line).append("\n");
            var4 = this.rows.values().iterator();

            while(var4.hasNext()) {
                Map<String, String> row = (Map)var4.next();
                result.append("|");
                Iterator var6 = this.columns.keySet().iterator();

                while(var6.hasNext()) {
                    String c = (String)var6.next();
                    center(result, row.containsKey(c) ? (String)row.get(c) : "-", (Integer)this.columns.get(c) + 2).append("|");
                }

                result.append("\n");
            }

            result.append(" ");
            repeat(result, "-", header.length() - 2).append(" ").append("\n");
            return result.toString();
        }
    }
}
