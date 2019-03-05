package eu.captech.digitalization.commons.basic.api;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.XmlException;
import eu.captech.digitalization.commons.basic.xml.XmlErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import java.io.InputStream;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "11/30/12",
        creationTime = "9:38 AM",
        lastModified = "11/30/12"
)
public interface IXsdXmlValidator {
    boolean validate(byte[] schemaSource, InputStream xml) throws XmlException;

    DocumentBuilder getDOMDocumentBuilder(DocumentBuilderFactory documentBuilderFactory) throws XmlException;

    Schema getSchema(byte[] schemaSource) throws SAXException;

    XmlErrorHandler parse(DocumentBuilder documentBuilder, InputStream xml) throws XmlException;

    XmlErrorHandler confirmByDOM(byte[] schemaSource, InputStream xml) throws XmlException;

    DocumentBuilderFactory getDOMDocumentBuilderFactory(byte[] schemaSource, boolean activateValidation) throws XmlException;
}
