package l2.commons.data.xml.helpers;

import l2.commons.data.xml.AbstractParser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerImpl implements ErrorHandler {
    private AbstractParser<?> _parser;

    public ErrorHandlerImpl(AbstractParser<?> parser) {
        this._parser = parser;
    }

    public void warning(SAXParseException exception) throws SAXException {
        this._parser.warn("File: " + this._parser.getCurrentFileName() + ":" + exception.getLineNumber() + " warning: " + exception.getMessage());
    }

    public void error(SAXParseException exception) throws SAXException {
        this._parser.error("File: " + this._parser.getCurrentFileName() + ":" + exception.getLineNumber() + " error: " + exception.getMessage());
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        this._parser.error("File: " + this._parser.getCurrentFileName() + ":" + exception.getLineNumber() + " fatal: " + exception.getMessage());
    }
}
