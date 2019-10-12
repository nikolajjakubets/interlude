package l2.commons.data.xml;

import java.io.File;
import java.io.InputStream;
import l2.commons.data.xml.helpers.ErrorHandlerImpl;
import l2.commons.data.xml.helpers.SimpleDTDEntityResolver;
import l2.commons.logging.LoggerObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public abstract class AbstractParser<H extends AbstractHolder> extends LoggerObject {
    protected final H _holder;
    protected String _currentFile;
    protected SAXReader _reader;

    protected AbstractParser(H holder) {
        this._holder = holder;
        this._reader = new SAXReader();
        this._reader.setValidation(false);
        this._reader.setErrorHandler(new ErrorHandlerImpl(this));
    }

    protected void initDTD(File f) {
        this._reader.setEntityResolver(new SimpleDTDEntityResolver(f));
    }

    protected void parseDocument(InputStream f, String name) throws Exception {
        this._currentFile = name;
        Document document = this._reader.read(f);
        this.readData(document.getRootElement());
    }

    protected abstract void readData(Element var1) throws Exception;

    protected abstract void parse();

    protected H getHolder() {
        return this._holder;
    }

    public String getCurrentFileName() {
        return this._currentFile;
    }

    public void load() {
        this.parse();
        this._holder.process();
        this._holder.log();
    }

    public void reload() {
        this.info("reload start...");
        this._holder.clear();
        this.load();
    }
}
