package eu.captech.digitalization.commons.basic.xml;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/23/12",
        creationTime = "2:23 PM",
        lastModified = "1/23/12"
)
public class XmlErrorHandler extends DefaultHandler {
    private boolean validated = true;
    private String level;
    private String message = null;
    private String publicId = null;
    private String systemId = null;
    private int lineNumber;
    private int columnNumber;

    public void warning(SAXParseException e) throws SAXException {
        setFields(e, "warning");
    }

    public void error(SAXParseException e) throws SAXException {
        setFields(e, "error");
    }

    public void fatalError(SAXParseException e) throws SAXException {
        setFields(e, "fatal");
    }

    private void setFields(SAXParseException e, String level) {
        this.validated = false;
        this.level = level;
        this.publicId = e.getPublicId();
        this.systemId = e.getSystemId();
        this.lineNumber = e.getLineNumber();
        this.columnNumber = e.getColumnNumber();
        this.message = e.getMessage();
    }

    public boolean isValidated() {
        return validated;
    }

    @Override
    public String toString() {
        return "DFErrorHandler{" +
                "validated=" + validated +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                ", publicId='" + publicId + '\'' +
                ", systemId='" + systemId + '\'' +
                ", lineNumber=" + lineNumber +
                ", columnNumber=" + columnNumber +
                '}';
    }
}
