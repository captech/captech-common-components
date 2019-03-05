package eu.captech.digitalization.commons.basic.xml;

import eu.captech.digitalization.commons.basic.api.IXsdXmlValidator;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "11/30/12",
        creationTime = "9:41 AM",
        lastModified = "11/30/12"
)
public class XsdXmlValidatorImpl implements IXsdXmlValidator {
    private static final Logger logger = LoggerFactory.getLogger(XsdXmlValidatorImpl.class);
    private static final String APACHE_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String JAVAX_XML_PARSERS_DOCUMENT_BUILDER_FACTORY = "javax.xml.parsers.DocumentBuilderFactory";
    private static final String COM_SUN_ORG_APACHE_XERCES_INTERNAL_JAXP_DOCUMENT_BUILDER_FACTORY_IMPL = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
    private static final String EMPTY_STRING = "";


    @Override
    public boolean validate(byte[] schemaSource, InputStream xml) throws XmlException {
        // parse an XML document into a DOM tree
        DocumentBuilder documentBuilder;
        XmlErrorHandler dfErrorHandler = new XmlErrorHandler();
        documentBuilder = getDOMDocumentBuilder(getDOMDocumentBuilderFactory(schemaSource, true));
        try {
            documentBuilder.setErrorHandler(dfErrorHandler);
            documentBuilder.parse(xml);
            documentBuilder.reset();
            return dfErrorHandler.isValidated();
        }
        catch (SAXException | IOException e) {
            throw new XmlException("Error validating the XML " + e.getMessage(), e);
        }
    }

    @Override
    public DocumentBuilder getDOMDocumentBuilder(DocumentBuilderFactory documentBuilderFactory) throws XmlException {
        // parse an XML document into a DOM tree
        try {
            return documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new XmlException("Error validating the XML " + e.getMessage(), e);
        }
    }

    @Override
    public Schema getSchema(byte[] schemaSource) throws SAXException {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(new ByteArrayInputStream(schemaSource));
        return factory.newSchema(schemaFile);
    }

    @Override
    public XmlErrorHandler parse(DocumentBuilder documentBuilder, InputStream xml) throws XmlException {
        XmlErrorHandler dfErrorHandler = new XmlErrorHandler();
        documentBuilder.setErrorHandler(dfErrorHandler);
        try {
            documentBuilder.parse(xml);
            return dfErrorHandler;
        }
        catch (SAXException | IOException e) {
            throw new XmlException("Error validating the XML " + e.getMessage(), e);
        }
    }

    @Override
    public XmlErrorHandler confirmByDOM(byte[] schemaSource, InputStream xml) throws XmlException {
        System.setProperty(JAVAX_XML_PARSERS_DOCUMENT_BUILDER_FACTORY, COM_SUN_ORG_APACHE_XERCES_INTERNAL_JAXP_DOCUMENT_BUILDER_FACTORY_IMPL);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, EMPTY_STRING);
        factory.setNamespaceAware(true);
        try {
            factory.setFeature(APACHE_DISALLOW_DOCTYPE_DECL, true);
            factory.setSchema(getSchema(schemaSource));
        }
        catch (IllegalArgumentException | SAXException | ParserConfigurationException e) {
            // Happens if the documentDomBuilder does not support JAXP 1.2
            throw new XmlException("Error validating the XML " + e.getMessage(), e);
        }
        return getXmlErrorHandler(xml, factory);
    }

    private XmlErrorHandler getXmlErrorHandler(InputStream xml, DocumentBuilderFactory factory) throws XmlException{// parse an XML document into a DOM tree
        DocumentBuilder documentBuilder;
        XmlErrorHandler dfErrorHandler = new XmlErrorHandler();
        try {
            documentBuilder = getDOMDocumentBuilder(factory);
            documentBuilder.setErrorHandler(dfErrorHandler);
            documentBuilder.parse(xml);
            return dfErrorHandler;
        }
        catch (SAXException | IOException e) {
            throw new XmlException("Error validating the XML " + e.getMessage(), e);
        }
    }

    @Override
    public DocumentBuilderFactory getDOMDocumentBuilderFactory(byte[] schemaSource, boolean activateValidation) throws XmlException {
        System.setProperty(JAVAX_XML_PARSERS_DOCUMENT_BUILDER_FACTORY, COM_SUN_ORG_APACHE_XERCES_INTERNAL_JAXP_DOCUMENT_BUILDER_FACTORY_IMPL);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, EMPTY_STRING);
        if (logger.isDebugEnabled()) {
            logger.debug("System DocumentBuilderFactory: " + System.getProperty(JAVAX_XML_PARSERS_DOCUMENT_BUILDER_FACTORY));
        }
        factory.setNamespaceAware(activateValidation);
        try {
            if (activateValidation) {
                factory.setFeature(APACHE_DISALLOW_DOCTYPE_DECL, true);
                factory.setSchema(getSchema(schemaSource));
            }
        }
        catch (IllegalArgumentException | SAXException | ParserConfigurationException e) {
            // Happens if the documentDomBuilder does not support JAXP 1.2
            throw new XmlException("Error validating the XML " + e.getMessage(), e);
        }
        return factory;
    }
}
