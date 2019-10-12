package l2.commons.data.xml;

import java.io.File;
import java.io.FileInputStream;

public abstract class AbstractFileParser<H extends AbstractHolder> extends AbstractParser<H> {
    protected AbstractFileParser(H holder) {
        super(holder);
    }

    public abstract File getXMLFile();

    public abstract String getDTDFileName();

    protected final void parse() {
        File file = this.getXMLFile();
        if (!file.exists()) {
            this.warn("file " + file.getAbsolutePath() + " not exists");
        } else {
            File dtd = new File(file.getParent(), this.getDTDFileName());
            if (!dtd.exists()) {
                this.info("DTD file: " + dtd.getName() + " not exists.");
            } else {
                this.initDTD(dtd);

                try {
                    this.parseDocument(new FileInputStream(file), file.getName());
                } catch (Exception var4) {
                    this.warn("Exception: " + var4, var4);
                }

            }
        }
    }
}
