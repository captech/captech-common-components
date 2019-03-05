package eu.captech.digitalization.commons.basic.xml;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/23/12",
        creationTime = "2:36 PM",
        lastModified = "1/23/12"
)
public class XmlContainer {
    private Document document;
    private XmlErrorHandler xmlErrorHandler;
    private DocumentBuilder documentBuilder;
    private String fileName;

    public XmlContainer(Document document, XmlErrorHandler xmlErrorHandler, @Nullable DocumentBuilder documentBuilder) {
        this.document = document;
        this.xmlErrorHandler = xmlErrorHandler;
        this.documentBuilder = documentBuilder;
    }

    public Document getDocument() {
        return document;
    }

    public XmlErrorHandler getXmlErrorHandler() {
        return xmlErrorHandler;
    }

    public DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setXmlErrorHandler(XmlErrorHandler xmlErrorHandler) {
        this.xmlErrorHandler = xmlErrorHandler;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "XmlContainer" + "{document=" + document + ", xmlErrorHandler=" + xmlErrorHandler +
               ", documentBuilder=" + documentBuilder + ", fileName='" + fileName + '\'' + '}';
    }
}
