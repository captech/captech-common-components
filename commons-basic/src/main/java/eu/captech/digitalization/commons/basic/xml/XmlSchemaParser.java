package eu.captech.digitalization.commons.basic.xml;

import eu.captech.digitalization.commons.basic.api.IXsdXmlValidator;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.XmlException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/23/12",
        creationTime = "2:18 PM",
        lastModified = "1/23/12"
)
public class XmlSchemaParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlSchemaParser.class);
    private DocumentBuilderFactory documentDomBuilder;
    private DocumentBuilderFactory documentValidatorDomBuilder;
    private final Map<String, String> transformerStaticProperties = new HashMap<>();
    private IXsdXmlValidator iXsdXmlValidator;
    private final Schema schema;

    public XmlSchemaParser(byte[] schemaSource, IXsdXmlValidator iXsdXmlValidator) throws XmlException {
        this.iXsdXmlValidator = iXsdXmlValidator;
        documentValidatorDomBuilder = this.iXsdXmlValidator.getDOMDocumentBuilderFactory(schemaSource, true);
        documentDomBuilder = this.iXsdXmlValidator.getDOMDocumentBuilderFactory(schemaSource, false);
        transformerStaticProperties.put(OutputKeys.INDENT, "yes");
        transformerStaticProperties.put(OutputKeys.METHOD, "xml");
        transformerStaticProperties.put("encoding", "UTF-8");
        transformerStaticProperties.put("{http://xml.apache.org/xslt}indent-amount", "3");
        schema = getSchema(schemaSource);
    }

    /**
     * Validates/trims an XML. Defined for DOM and Crimson implementations
     *
     * @param inputXml the XML to validate/trim as a <code>byte</code> array
     * @param validate If <code>true</code>, activates the DocumentBuilder Object validation field. Then, the document
     *                 will be validated twice: before and after triming the XML. If <code>false</code>, the validation
     *                 field is not activated and no validation is performed waht so ever.
     * @return A XmlContainer Object containing the information about XML validation. It will return always a valid XML
     * when 'validate' is set to <code>false</code>.
     * @throws XmlException Signals that ...
     */
    public XmlContainer validateAndTrimByDom(byte[] inputXml, boolean validate)
            throws XmlException {
        XmlContainer xmlContainer;
        Document document;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to start new validation/trimming...");
        }
        xmlContainer = getXmlContainer(new ByteArrayInputStream(inputXml), validate);
        if (validate) {
            if (!xmlContainer.getXmlErrorHandler().isValidated()) {
                throw new XmlException("Error validating xml: " + xmlContainer.toString());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("1rst. validation passed!");
            }
        }
        document = xmlContainer.getDocument();
        trimDOMElement(null, document.getDocumentElement(), "");
        if (validate) {
            xmlContainer = validateDocument(xmlContainer);
            if (!xmlContainer.getXmlErrorHandler().isValidated()) {
                throw new XmlException("Error validating xml: " + xmlContainer.toString());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("2nd. validation passed!");
            }
        }
        return xmlContainer;
    }

    /**
     * Validates/trims an XML. Defined for DOM and Crimson implementations
     *
     * @param inputXml the XML to validate/trim as a <code>byte</code> array
     * @param validate If <code>true</code>, activates the DocumentBuilder Object validation field. Then, the document
     *                 will be validated twice: before and after triming the XML. If <code>false</code>, the validation
     *                 field is not activated and no validation is performed waht so ever.
     * @return A XmlContainer Object containing the information about XML validation. It will return always a valid XML
     * when 'validate' is set to <code>false</code>.
     * @throws XmlException Signals that ...
     */
    public XmlContainer validateByDom(byte[] inputXml, boolean validate)
            throws XmlException {
        XmlContainer xmlContainer;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to start new validation/trimming...");
        }
        xmlContainer = getXmlContainer(new ByteArrayInputStream(inputXml), validate);
        if (validate) {
            if (!xmlContainer.getXmlErrorHandler().isValidated()) {
                throw new XmlException("Error validating xml: " + xmlContainer.toString());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("XML Validation passed!");
            }
        }
        return xmlContainer;
    }
    public XmlContainer validateByDom(InputStream inputStream, boolean validate)
            throws XmlException {
        XmlContainer xmlContainer;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to start new validation/trimming...");
        }
        xmlContainer = getXmlContainer(inputStream, validate);
        if (validate) {
            if (!xmlContainer.getXmlErrorHandler().isValidated()) {
                throw new XmlException("Error validating xml: " + xmlContainer.toString());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("XML Validation passed!");
            }
        }
        return xmlContainer;
    }

    public XmlContainer validateByDom(DOMSource domSource, boolean validate)
            throws XmlException {
        XmlContainer xmlContainer;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("About to start new validation/trimming...");
        }
        xmlContainer = validateDocument(domSource);
        if (validate) {
            if (!xmlContainer.getXmlErrorHandler().isValidated()) {
                throw new XmlException("Error validating xml: " + xmlContainer.toString());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("XML Validation passed!");
            }
        }
        return xmlContainer;
    }

    public String xmlToString(XmlContainer xmlContainer) throws XmlException {
        return xmlToString(xmlContainer.getDocument().getDocumentElement());
    }

    public String xmlToString(Node node) throws XmlException {
        Source source = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        Transformer transformer = getTransformer(null);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new XmlException("Error streaming the XML node. " + e.getMessage(), e);
        }
        return stringWriter.getBuffer().toString();
    }

    private Transformer getTransformer(@Nullable Map<String, String> transformerProperties) throws XmlException {
        Transformer tr = getTransformer();
        Map<String, String> properties = transformerProperties == null || transformerProperties.isEmpty() ? transformerStaticProperties : transformerProperties;

        Set<String> set = properties.keySet();
        for (String s : set) {
            tr.setOutputProperty(s, properties.get(s));
        }
        return tr;
    }

    private Transformer getTransformer() throws XmlException {
        try {
            return TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new XmlException("Error acquiring a XML Transformer object. " + e.getMessage(), e);
        }
    }

    private XmlContainer validateDocument(DOMSource domSource) throws XmlException {
        XmlContainer xmlContainer = new XmlContainer((Document) domSource.getNode(), new XmlErrorHandler(), null);
        Validator validator = schema.newValidator();
        validator.setErrorHandler(xmlContainer.getXmlErrorHandler());
        // validate the DOM tree
        try {
            validator.validate(new DOMSource(xmlContainer.getDocument()));
        } catch (SAXException | IOException e) {
            throw new XmlException("Error validating XML. " + e.getMessage(), e);
        }
        return xmlContainer;
    }

    private XmlContainer validateDocument(XmlContainer xmlContainer) throws XmlException {
        xmlContainer.setXmlErrorHandler(new XmlErrorHandler());
        DocumentBuilder documentBuilder = xmlContainer.getDocumentBuilder();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(xmlContainer.getXmlErrorHandler());
        // validate the DOM tree
        try {
            validator.validate(new DOMSource(xmlContainer.getDocument()));
        } catch (SAXException | IOException e) {
            throw new XmlException("Error validating XML. " + e.getMessage(), e);
        }
        if (documentBuilder != null) {
            documentBuilder.reset();
        }
        return xmlContainer;
    }

    // TODO: private method?
    public XmlContainer getXmlContainer(InputStream inputStream, boolean validate)
            throws XmlException {
        DocumentBuilder documentBuilder = getDocumentBuilder(validate);
        XmlErrorHandler dfErrorHandler = new XmlErrorHandler();
        documentBuilder.setErrorHandler(dfErrorHandler);
        Document document;
        try {
            document = documentBuilder.parse(inputStream);
        } catch (SAXException | IOException e) {
            throw new XmlException("Error parsing XML. " + e.getMessage(), e);
        } finally {
            documentBuilder.reset();
        }
        return new XmlContainer(document, dfErrorHandler, documentBuilder);
    }

    // TODO: private method?
    public DocumentBuilder getDocumentBuilder(boolean validate) throws XmlException {
        if (validate) {
            return iXsdXmlValidator.getDOMDocumentBuilder(documentValidatorDomBuilder);
        } else {
            return iXsdXmlValidator.getDOMDocumentBuilder(documentDomBuilder);
        }
    }

    private Schema getSchema(byte[] schemaSource) throws XmlException {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(new ByteArrayInputStream(schemaSource));
        Schema schema;
        try {
            schema = factory.newSchema(schemaFile);
        } catch (SAXException e) {
            throw new XmlException("Error adquiring the schema object. " + e.getMessage(), e);
        }
        return schema;
    }

    // TODO: private method?
    public void trimDOMElement(@Nullable Element parent, Element element, String tab) {
        // loop through child nodes
        Node child;
        if (parent != null && LOGGER.isDebugEnabled()) {
            LOGGER.debug(tab + "The parent: " + parent.getLocalName());
        }
        Node next = element.getFirstChild();
        if (next == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(tab + "The element is " + element.getLocalName() + " and there are not children");
            }
            if (parent != null) {
                parent.removeChild(element);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(tab + "Removing empty element " + element.getLocalName());
                }
            }
            return;
        }
        int i = 1;
        while ((child = next) != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(tab + "Child " + (i++) + " under " + element.getLocalName() + (child.getLocalName() != null ? (" : " + child.getLocalName()) : ""));
            }
            // set next before we change anything
            next = child.getNextSibling();
            // handle child by node type
            if (child.getNodeType() == Node.TEXT_NODE) {
                // trim whitespace from content text
                String trimmed = child.getNodeValue().trim();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(tab + "Text Node: '" + trimmed + "'");
                }
                if (trimmed.length() == 0) {
                    // delete child if nothing but whitespace
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(tab + "\tRemoving text element '" + trimmed + "'");
                    }
                    element.removeChild(child);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(tab + "Text child '" + trimmed + "' not removed");
                    }
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(tab + "Element Node: '" + child.getLocalName() + "'");
                }
                // handle child elements with recursive call
                trimDOMElement(element, (Element) child, tab + "\t");
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(tab + "Out from Element " + element.getLocalName());
        }
        if (parent != null && !element.hasChildNodes()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(tab + "Removing Element " + element.getLocalName());
            }
            parent.removeChild(element);
        }
    }

    public String fetchDomTextElementByTag(Element element, String tag) {
        Element tagElement = (Element) element.getElementsByTagName(tag).item(0);
        if (tagElement != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Element: " + tagElement.getLocalName());
            }
            Object obj = tagElement.getFirstChild();
            Text text;
            if (obj != null) {
                if (obj instanceof Text) {
                    text = (Text) obj;
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("[" + tag + "] text is " + text);
                    }
                    if (text != null) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("[" + tag + "] text is '" + text.getData() + '\'');
                        }
                        return text.getData();
                    }
                } else {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Node " + ((Node) obj).getLocalName() + " is not a Text Node.");
                    }
                }
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Element " + tagElement.getLocalName() + " has not children.");
                }
            }
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Element " + tag + " is not present.");
            }
        }
        return null;
    }

    public NodeList getListByTag(Element element, String tag) {
        return element.getElementsByTagName(tag);
    }

    public NodeList getListByTag(XmlContainer xmlContainer, String tag) {
        return getListByTag(xmlContainer.getDocument().getDocumentElement(), tag);
    }

    public List<Node> getChildElementsList(Element element) {
        List<Node> list = new LinkedList<>();
        Node subTag;
        NodeList subList = element.getChildNodes();
        for (int j = 0; j < subList.getLength(); j++) {
            subTag = subList.item(j);
            if (subTag instanceof Element) {
                list.add(subTag);
            }
        }
        return list;
    }

    public Element searchForNode(Element parent, String... tagPath) throws XmlException {
        for (int i = 0; i < tagPath.length; i++) {
            parent = (Element) getListByTag(parent, tagPath[i]).item(0);
            if (parent == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Not able to find tag <" + tagPath[i] + ((i > 0) ? "> under parent <" + tagPath[i - 1] + ">." : ">."));
                }
                throw new XmlException("Not able to find tag <" + tagPath[i] + ((i > 0) ? "> under parent <" + tagPath[i - 1] + ">." : ">."));
            }
        }
        return parent;
    }
}
