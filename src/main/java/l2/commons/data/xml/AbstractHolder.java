package l2.commons.data.xml;

import l2.commons.logging.LoggerObject;

public abstract class AbstractHolder extends LoggerObject {
    public AbstractHolder() {
    }

    public void log() {
        this.info(String.format("loaded %d%s(s) count.", this.size(), formatOut(this.getClass().getSimpleName().replace("Holder", "")).toLowerCase()));
    }

    protected void process() {
    }

    public abstract int size();

    public abstract void clear();

    private static String formatOut(String st) {
        char[] chars = st.toCharArray();
        StringBuffer buf = new StringBuffer(chars.length);
        char[] var3 = chars;
        int var4 = chars.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char ch = var3[var5];
            if (Character.isUpperCase(ch)) {
                buf.append(" ");
            }

            buf.append(Character.toLowerCase(ch));
        }

        return buf.toString();
    }
}
