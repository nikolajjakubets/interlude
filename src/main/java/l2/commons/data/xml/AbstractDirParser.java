package l2.commons.data.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

public abstract class AbstractDirParser<H extends AbstractHolder> extends AbstractParser<H> {
    protected AbstractDirParser(H holder) {
        super(holder);
    }

    public abstract File getXMLDir();

    public abstract boolean isIgnored(File var1);

    public abstract String getDTDFileName();

    protected final void parse() {
        File dir = this.getXMLDir();
        if (!dir.exists()) {
            this.warn("Dir " + dir.getAbsolutePath() + " not exists");
        } else {
            File dtd = new File(dir, this.getDTDFileName());
            if (!dtd.exists()) {
                this.info("DTD file: " + dtd.getName() + " not exists.");
            } else {
                this.initDTD(dtd);

                try {
                    Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());
                    Iterator var4 = files.iterator();

                    while(var4.hasNext()) {
                        File f = (File)var4.next();
                        if (!f.isHidden() && !this.isIgnored(f)) {
                            try {
                                this.parseDocument(new FileInputStream(f), f.getName());
                            } catch (Exception var7) {
                                this.info("Exception: " + var7 + " in file: " + f.getName(), var7);
                            }
                        }
                    }
                } catch (Exception var8) {
                    this.warn("Exception: " + var8, var8);
                }

            }
        }
    }
}
