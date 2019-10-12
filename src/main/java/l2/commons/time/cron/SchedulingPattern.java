//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package l2.commons.time.cron;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import l2.commons.util.Rnd;

public class SchedulingPattern implements NextTime {
    private static final int MINUTE_MIN_VALUE = 0;
    private static final int MINUTE_MAX_VALUE = 59;
    private static final int HOUR_MIN_VALUE = 0;
    private static final int HOUR_MAX_VALUE = 23;
    private static final int DAY_OF_MONTH_MIN_VALUE = 1;
    private static final int DAY_OF_MONTH_MAX_VALUE = 31;
    private static final int MONTH_MIN_VALUE = 1;
    private static final int MONTH_MAX_VALUE = 12;
    private static final int DAY_OF_WEEK_MIN_VALUE = 0;
    private static final int DAY_OF_WEEK_MAX_VALUE = 7;
    private static final SchedulingPattern.ValueParser MINUTE_VALUE_PARSER = new SchedulingPattern.MinuteValueParser();
    private static final SchedulingPattern.ValueParser HOUR_VALUE_PARSER = new SchedulingPattern.HourValueParser();
    private static final SchedulingPattern.ValueParser DAY_OF_MONTH_VALUE_PARSER = new SchedulingPattern.DayOfMonthValueParser();
    private static final SchedulingPattern.ValueParser MONTH_VALUE_PARSER = new SchedulingPattern.MonthValueParser();
    private static final SchedulingPattern.ValueParser DAY_OF_WEEK_VALUE_PARSER = new SchedulingPattern.DayOfWeekValueParser();
    private String asString;
    protected List<SchedulingPattern.ValueMatcher> minuteMatchers = new ArrayList();
    protected List<SchedulingPattern.ValueMatcher> hourMatchers = new ArrayList();
    protected List<SchedulingPattern.ValueMatcher> dayOfMonthMatchers = new ArrayList();
    protected List<SchedulingPattern.ValueMatcher> monthMatchers = new ArrayList();
    protected List<SchedulingPattern.ValueMatcher> dayOfWeekMatchers = new ArrayList();
    protected int matcherSize = 0;
    protected Map<Integer, Integer> hourAdder = new TreeMap();
    protected Map<Integer, Integer> hourAdderRnd = new TreeMap();
    protected Map<Integer, Integer> dayOfYearAdder = new TreeMap();
    protected Map<Integer, Integer> minuteAdderRnd = new TreeMap();
    protected Map<Integer, Integer> weekOfYearAdder = new TreeMap();

    public static boolean validate(String schedulingPattern) {
        try {
            new SchedulingPattern(schedulingPattern);
            return true;
        } catch (SchedulingPattern.InvalidPatternException var2) {
            return false;
        }
    }

    public SchedulingPattern(String pattern) throws SchedulingPattern.InvalidPatternException {
        this.asString = pattern;
        StringTokenizer st1 = new StringTokenizer(pattern, "|");
        if (st1.countTokens() < 1) {
            throw new SchedulingPattern.InvalidPatternException("invalid pattern: \"" + pattern + "\"");
        } else {
            for(; st1.hasMoreTokens(); ++this.matcherSize) {
                String localPattern = st1.nextToken();
                StringTokenizer st2 = new StringTokenizer(localPattern, " \t");
                int tokCnt = st2.countTokens();
                if (tokCnt < 5 || tokCnt > 6) {
                    throw new SchedulingPattern.InvalidPatternException("invalid pattern: \"" + localPattern + "\"");
                }

                String weekOfYearAdderText;
                String[] dayOfMonthPatternParts;
                int i;
                try {
                    weekOfYearAdderText = st2.nextToken();
                    dayOfMonthPatternParts = weekOfYearAdderText.split(":");
                    if (dayOfMonthPatternParts.length > 1) {
                        for(i = 0; i < dayOfMonthPatternParts.length - 1; ++i) {
                            if (dayOfMonthPatternParts[i].length() > 1) {
                                if (!dayOfMonthPatternParts[i].startsWith("~")) {
                                    throw new SchedulingPattern.InvalidPatternException("Unknown hour modifier \"" + dayOfMonthPatternParts[i] + "\"");
                                }

                                this.minuteAdderRnd.put(this.matcherSize, Integer.parseInt(dayOfMonthPatternParts[i].substring(1)));
                            }
                        }

                        weekOfYearAdderText = dayOfMonthPatternParts[dayOfMonthPatternParts.length - 1];
                    }

                    this.minuteMatchers.add(this.buildValueMatcher(weekOfYearAdderText, MINUTE_VALUE_PARSER));
                } catch (Exception var14) {
                    throw new SchedulingPattern.InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing minutes field: " + var14.getMessage() + ".");
                }

                try {
                    weekOfYearAdderText = st2.nextToken();
                    dayOfMonthPatternParts = weekOfYearAdderText.split(":");
                    if (dayOfMonthPatternParts.length > 1) {
                        for(i = 0; i < dayOfMonthPatternParts.length - 1; ++i) {
                            if (dayOfMonthPatternParts[i].length() > 1) {
                                if (dayOfMonthPatternParts[i].startsWith("+")) {
                                    this.hourAdder.put(this.matcherSize, Integer.parseInt(dayOfMonthPatternParts[i].substring(1)));
                                } else {
                                    if (!dayOfMonthPatternParts[i].startsWith("~")) {
                                        throw new SchedulingPattern.InvalidPatternException("Unknown hour modifier \"" + dayOfMonthPatternParts[i] + "\"");
                                    }

                                    this.hourAdderRnd.put(this.matcherSize, Integer.parseInt(dayOfMonthPatternParts[i].substring(1)));
                                }
                            }
                        }

                        weekOfYearAdderText = dayOfMonthPatternParts[dayOfMonthPatternParts.length - 1];
                    }

                    this.hourMatchers.add(this.buildValueMatcher(weekOfYearAdderText, HOUR_VALUE_PARSER));
                } catch (Exception var13) {
                    throw new SchedulingPattern.InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing hours field: " + var13.getMessage() + ".");
                }

                try {
                    weekOfYearAdderText = st2.nextToken();
                    dayOfMonthPatternParts = weekOfYearAdderText.split(":");
                    if (dayOfMonthPatternParts.length > 1) {
                        for(i = 0; i < dayOfMonthPatternParts.length - 1; ++i) {
                            if (dayOfMonthPatternParts[i].length() > 1) {
                                if (!dayOfMonthPatternParts[i].startsWith("+")) {
                                    throw new SchedulingPattern.InvalidPatternException("Unknown day modifier \"" + dayOfMonthPatternParts[i] + "\"");
                                }

                                this.dayOfYearAdder.put(this.matcherSize, Integer.parseInt(dayOfMonthPatternParts[i].substring(1)));
                            }
                        }

                        weekOfYearAdderText = dayOfMonthPatternParts[dayOfMonthPatternParts.length - 1];
                    }

                    this.dayOfMonthMatchers.add(this.buildValueMatcher(weekOfYearAdderText, DAY_OF_MONTH_VALUE_PARSER));
                } catch (Exception var12) {
                    throw new SchedulingPattern.InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing days of month field: " + var12.getMessage() + ".");
                }

                try {
                    this.monthMatchers.add(this.buildValueMatcher(st2.nextToken(), MONTH_VALUE_PARSER));
                } catch (Exception var11) {
                    throw new SchedulingPattern.InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing months field: " + var11.getMessage() + ".");
                }

                try {
                    this.dayOfWeekMatchers.add(this.buildValueMatcher(st2.nextToken(), DAY_OF_WEEK_VALUE_PARSER));
                } catch (Exception var10) {
                    throw new SchedulingPattern.InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing days of week field: " + var10.getMessage() + ".");
                }

                if (st2.hasMoreTokens()) {
                    try {
                        weekOfYearAdderText = st2.nextToken();
                        if (weekOfYearAdderText.charAt(0) != '+') {
                            throw new SchedulingPattern.InvalidPatternException("Unknown week of year addition in pattern \"" + localPattern + "\".");
                        }

                        weekOfYearAdderText = weekOfYearAdderText.substring(1);
                        this.weekOfYearAdder.put(this.matcherSize, Integer.parseInt(weekOfYearAdderText));
                    } catch (Exception var9) {
                        throw new SchedulingPattern.InvalidPatternException("invalid pattern \"" + localPattern + "\". Error parsing week of year addition: " + var9 + ".");
                    }
                }
            }

        }
    }

    private SchedulingPattern.ValueMatcher buildValueMatcher(String str, SchedulingPattern.ValueParser parser) throws Exception {
        if (str.length() == 1 && str.equals("*")) {
            return new SchedulingPattern.AlwaysTrueValueMatcher();
        } else {
            List<Integer> values = new ArrayList();
            StringTokenizer st = new StringTokenizer(str, ",");

            while(st.hasMoreTokens()) {
                String element = st.nextToken();

                List local;
                try {
                    local = this.parseListElement(element, parser);
                } catch (Exception var9) {
                    throw new Exception("invalid field \"" + str + "\", invalid element \"" + element + "\", " + var9.getMessage());
                }

                Iterator i = local.iterator();

                while(i.hasNext()) {
                    Integer value = (Integer)i.next();
                    if (!values.contains(value)) {
                        values.add(value);
                    }
                }
            }

            if (values.size() == 0) {
                throw new Exception("invalid field \"" + str + "\"");
            } else if (parser == DAY_OF_MONTH_VALUE_PARSER) {
                return new SchedulingPattern.DayOfMonthValueMatcher(values);
            } else {
                return new SchedulingPattern.IntArrayValueMatcher(values);
            }
        }
    }

    private List<Integer> parseListElement(String str, SchedulingPattern.ValueParser parser) throws Exception {
        StringTokenizer st = new StringTokenizer(str, "/");
        int size = st.countTokens();
        if (size >= 1 && size <= 2) {
            List values;
            try {
                values = this.parseRange(st.nextToken(), parser);
            } catch (Exception var11) {
                throw new Exception("invalid range, " + var11.getMessage());
            }

            if (size != 2) {
                return values;
            } else {
                String dStr = st.nextToken();

                int div;
                try {
                    div = Integer.parseInt(dStr);
                } catch (NumberFormatException var10) {
                    throw new Exception("invalid divisor \"" + dStr + "\"");
                }

                if (div < 1) {
                    throw new Exception("non positive divisor \"" + div + "\"");
                } else {
                    List<Integer> values2 = new ArrayList();

                    for(int i = 0; i < values.size(); i += div) {
                        values2.add((Integer) values.get(i));
                    }

                    return values2;
                }
            }
        } else {
            throw new Exception("syntax error");
        }
    }

    private List<Integer> parseRange(String str, SchedulingPattern.ValueParser parser) throws Exception {
        int size;
        int v1;
        if (str.equals("*")) {
            int min = parser.getMinValue();
            size = parser.getMaxValue();
            List<Integer> values = new ArrayList();

            for(v1 = min; v1 <= size; ++v1) {
                values.add(new Integer(v1));
            }

            return values;
        } else {
            StringTokenizer st = new StringTokenizer(str, "-");
            size = st.countTokens();
            if (size >= 1 && size <= 2) {
                String v1Str = st.nextToken();

                try {
                    v1 = parser.parse(v1Str);
                } catch (Exception var14) {
                    throw new Exception("invalid value \"" + v1Str + "\", " + var14.getMessage());
                }

                if (size == 1) {
                    List<Integer> values = new ArrayList();
                    values.add(new Integer(v1));
                    return values;
                } else {
                    String v2Str = st.nextToken();

                    int v2;
                    try {
                        v2 = parser.parse(v2Str);
                    } catch (Exception var13) {
                        throw new Exception("invalid value \"" + v2Str + "\", " + var13.getMessage());
                    }

                    List<Integer> values = new ArrayList();
                    int min;
                    if (v1 < v2) {
                        for(min = v1; min <= v2; ++min) {
                            values.add(new Integer(min));
                        }
                    } else if (v1 > v2) {
                        min = parser.getMinValue();
                        int max = parser.getMaxValue();

                        int i;
                        for(i = v1; i <= max; ++i) {
                            values.add(new Integer(i));
                        }

                        for(i = min; i <= v2; ++i) {
                            values.add(new Integer(i));
                        }
                    } else {
                        values.add(new Integer(v1));
                    }

                    return values;
                }
            } else {
                throw new Exception("syntax error");
            }
        }
    }

    public boolean match(TimeZone timezone, long millis) {
        GregorianCalendar gc = new GregorianCalendar(timezone);
        gc.setTimeInMillis(millis);
        gc.set(13, 0);
        gc.set(14, 0);

        for(int i = 0; i < this.matcherSize; ++i) {
            if (this.weekOfYearAdder.containsKey(i)) {
                gc.add(3, -(Integer)this.weekOfYearAdder.get(i));
            }

            if (this.dayOfYearAdder.containsKey(i)) {
                gc.add(6, -(Integer)this.dayOfYearAdder.get(i));
            }

            if (this.hourAdder.containsKey(i)) {
                gc.add(10, -(Integer)this.hourAdder.get(i));
            }

            boolean var10000;
            label45: {
                int minute = gc.get(12);
                int hour = gc.get(11);
                int dayOfMonth = gc.get(5);
                int month = gc.get(2) + 1;
                int dayOfWeek = gc.get(7) - 1;
                int year = gc.get(1);
                SchedulingPattern.ValueMatcher minuteMatcher = (SchedulingPattern.ValueMatcher)this.minuteMatchers.get(i);
                SchedulingPattern.ValueMatcher hourMatcher = (SchedulingPattern.ValueMatcher)this.hourMatchers.get(i);
                SchedulingPattern.ValueMatcher dayOfMonthMatcher = (SchedulingPattern.ValueMatcher)this.dayOfMonthMatchers.get(i);
                SchedulingPattern.ValueMatcher monthMatcher = (SchedulingPattern.ValueMatcher)this.monthMatchers.get(i);
                SchedulingPattern.ValueMatcher dayOfWeekMatcher = (SchedulingPattern.ValueMatcher)this.dayOfWeekMatchers.get(i);
                if (minuteMatcher.match(minute) && hourMatcher.match(hour)) {
                    label42: {
                        if (dayOfMonthMatcher instanceof SchedulingPattern.DayOfMonthValueMatcher) {
                            if (!((SchedulingPattern.DayOfMonthValueMatcher)dayOfMonthMatcher).match(dayOfMonth, month, gc.isLeapYear(year))) {
                                break label42;
                            }
                        } else if (!dayOfMonthMatcher.match(dayOfMonth)) {
                            break label42;
                        }

                        if (monthMatcher.match(month) && dayOfWeekMatcher.match(dayOfWeek)) {
                            var10000 = true;
                            break label45;
                        }
                    }
                }

                var10000 = false;
            }

            boolean eval = var10000;
            if (eval) {
                return true;
            }
        }

        return false;
    }

    public boolean match(long millis) {
        return this.match(TimeZone.getDefault(), millis);
    }

    public long next(TimeZone timezone, long millis) {
        long result = -1L;

        label112:
        for(int i = 0; i < this.matcherSize; ++i) {
            long next = -1L;
            GregorianCalendar gc = new GregorianCalendar(timezone);
            gc.setTimeInMillis(millis);
            gc.set(13, 0);
            gc.set(14, 0);
            if (this.weekOfYearAdder.containsKey(i)) {
                gc.add(3, (Integer)this.weekOfYearAdder.get(i));
            }

            if (this.dayOfYearAdder.containsKey(i)) {
                gc.add(6, (Integer)this.dayOfYearAdder.get(i));
            }

            if (this.hourAdder.containsKey(i)) {
                gc.add(10, (Integer)this.hourAdder.get(i));
            }

            SchedulingPattern.ValueMatcher minuteMatcher = (SchedulingPattern.ValueMatcher)this.minuteMatchers.get(i);
            SchedulingPattern.ValueMatcher hourMatcher = (SchedulingPattern.ValueMatcher)this.hourMatchers.get(i);
            SchedulingPattern.ValueMatcher dayOfMonthMatcher = (SchedulingPattern.ValueMatcher)this.dayOfMonthMatchers.get(i);
            SchedulingPattern.ValueMatcher monthMatcher = (SchedulingPattern.ValueMatcher)this.monthMatchers.get(i);
            SchedulingPattern.ValueMatcher dayOfWeekMatcher = (SchedulingPattern.ValueMatcher)this.dayOfWeekMatchers.get(i);

            while(true) {
                int year = gc.get(1);
                boolean isLeapYear = gc.isLeapYear(year);

                for(int month = gc.get(2) + 1; month <= 12; ++month) {
                    if (monthMatcher.match(month)) {
                        gc.set(2, month - 1);
                        int maxDayOfMonth = SchedulingPattern.DayOfMonthValueMatcher.getLastDayOfMonth(month, isLeapYear);

                        for(int dayOfMonth = gc.get(5); dayOfMonth <= maxDayOfMonth; ++dayOfMonth) {
                            label117: {
                                if (dayOfMonthMatcher instanceof SchedulingPattern.DayOfMonthValueMatcher) {
                                    if (!((SchedulingPattern.DayOfMonthValueMatcher)dayOfMonthMatcher).match(dayOfMonth, month, isLeapYear)) {
                                        break label117;
                                    }
                                } else if (!dayOfMonthMatcher.match(dayOfMonth)) {
                                    break label117;
                                }

                                gc.set(5, dayOfMonth);
                                int dayOfWeek = gc.get(7) - 1;
                                if (dayOfWeekMatcher.match(dayOfWeek)) {
                                    for(int hour = gc.get(11); hour <= 23; ++hour) {
                                        if (hourMatcher.match(hour)) {
                                            gc.set(11, hour);

                                            for(int minute = gc.get(12); minute <= 59; ++minute) {
                                                if (minuteMatcher.match(minute)) {
                                                    gc.set(12, minute);
                                                    long next0 = gc.getTimeInMillis();
                                                    if (next0 >= millis) {
                                                        if (next == -1L || next0 < next) {
                                                            next = next0;
                                                            if (this.hourAdderRnd.containsKey(i)) {
                                                                next = next0 + (long)(Rnd.get((Integer)this.hourAdderRnd.get(i)) * 60 * 60) * 1000L;
                                                            }

                                                            if (this.minuteAdderRnd.containsKey(i)) {
                                                                next += (long)(Rnd.get((Integer)this.minuteAdderRnd.get(i)) * 60) * 1000L;
                                                            }
                                                        }

                                                        if (next > millis && (result == -1L || next < result)) {
                                                            result = next;
                                                        }
                                                        continue label112;
                                                    }
                                                }
                                            }
                                        }

                                        gc.set(12, 0);
                                    }
                                }
                            }

                            gc.set(11, 0);
                            gc.set(12, 0);
                        }
                    }

                    gc.set(5, 1);
                    gc.set(11, 0);
                    gc.set(12, 0);
                }

                gc.set(2, 0);
                gc.set(11, 0);
                gc.set(12, 0);
                gc.roll(1, true);
            }
        }

        return result;
    }

    public long next(long millis) {
        return this.next(TimeZone.getDefault(), millis);
    }

    public String toString() {
        return this.asString;
    }

    private static int parseAlias(String value, String[] aliases, int offset) throws Exception {
        for(int i = 0; i < aliases.length; ++i) {
            if (aliases[i].equalsIgnoreCase(value)) {
                return offset + i;
            }
        }

        throw new Exception("invalid alias \"" + value + "\"");
    }

    private static class DayOfMonthValueMatcher extends SchedulingPattern.IntArrayValueMatcher {
        private static final int[] lastDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        public DayOfMonthValueMatcher(List<Integer> integers) {
            super(integers);
        }

        public boolean match(int value, int month, boolean isLeapYear) {
            return super.match(value) || value > 27 && this.match(32) && isLastDayOfMonth(value, month, isLeapYear);
        }

        public static int getLastDayOfMonth(int month, boolean isLeapYear) {
            return isLeapYear && month == 2 ? 29 : lastDays[month - 1];
        }

        public static boolean isLastDayOfMonth(int value, int month, boolean isLeapYear) {
            return value == getLastDayOfMonth(month, isLeapYear);
        }
    }

    private static class IntArrayValueMatcher implements SchedulingPattern.ValueMatcher {
        private int[] values;

        public IntArrayValueMatcher(List<Integer> integers) {
            int size = integers.size();
            this.values = new int[size];

            for(int i = 0; i < size; ++i) {
                try {
                    this.values[i] = (Integer)integers.get(i);
                } catch (Exception var5) {
                    throw new IllegalArgumentException(var5.getMessage());
                }
            }

        }

        public boolean match(int value) {
            for(int i = 0; i < this.values.length; ++i) {
                if (this.values[i] == value) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class AlwaysTrueValueMatcher implements SchedulingPattern.ValueMatcher {
        private AlwaysTrueValueMatcher() {
        }

        public boolean match(int value) {
            return true;
        }
    }

    private interface ValueMatcher {
        boolean match(int var1);
    }

    private static class DayOfWeekValueParser extends SchedulingPattern.SimpleValueParser {
        private static String[] ALIASES = new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

        public DayOfWeekValueParser() {
            super(0, 7);
        }

        public int parse(String value) throws Exception {
            try {
                return super.parse(value) % 7;
            } catch (Exception var3) {
                return SchedulingPattern.parseAlias(value, ALIASES, 0);
            }
        }
    }

    private static class MonthValueParser extends SchedulingPattern.SimpleValueParser {
        private static String[] ALIASES = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

        public MonthValueParser() {
            super(1, 12);
        }

        public int parse(String value) throws Exception {
            try {
                return super.parse(value);
            } catch (Exception var3) {
                return SchedulingPattern.parseAlias(value, ALIASES, 1);
            }
        }
    }

    private static class DayOfMonthValueParser extends SchedulingPattern.SimpleValueParser {
        public DayOfMonthValueParser() {
            super(1, 31);
        }

        public int parse(String value) throws Exception {
            return value.equalsIgnoreCase("L") ? 32 : super.parse(value);
        }
    }

    private static class HourValueParser extends SchedulingPattern.SimpleValueParser {
        public HourValueParser() {
            super(0, 23);
        }
    }

    private static class MinuteValueParser extends SchedulingPattern.SimpleValueParser {
        public MinuteValueParser() {
            super(0, 59);
        }
    }

    private static class SimpleValueParser implements SchedulingPattern.ValueParser {
        protected int minValue;
        protected int maxValue;

        public SimpleValueParser(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public int parse(String value) throws Exception {
            int i;
            try {
                i = Integer.parseInt(value);
            } catch (NumberFormatException var4) {
                throw new Exception("invalid integer value");
            }

            if (i >= this.minValue && i <= this.maxValue) {
                return i;
            } else {
                throw new Exception("value out of range");
            }
        }

        public int getMinValue() {
            return this.minValue;
        }

        public int getMaxValue() {
            return this.maxValue;
        }
    }

    private interface ValueParser {
        int parse(String var1) throws Exception;

        int getMinValue();

        int getMaxValue();
    }

    public class InvalidPatternException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        InvalidPatternException() {
        }

        InvalidPatternException(String message) {
            super(message);
        }
    }
}
