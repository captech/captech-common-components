package eu.captech.digitalization.commons.basic.xml;

import eu.captech.digitalization.commons.basic.BasicCommonsTest;
import eu.captech.digitalization.commons.basic.api.IXsdXmlValidator;
import eu.captech.digitalization.commons.basic.exception.XmlException;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class XmlSchemaParserTest extends BasicCommonsTest {
    private static final String APACHE_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String PATH_MAPPINGS_XSD = "pathMappings.xsd";
    private static final String SECURITY_RISK_XSD = "securityRisk.xsd";
    private static final String PATH_MAPPINGS_XML = "pathMappings.xml";
    private static final String XML_STRING = "xml";
    private static final String MAPPING_TAG = "mapping";
    private static final String EMPTY_STRING = "";
    private IXsdXmlValidator iXsdXmlValidator;
    private Path xsd;
    private Path secRiskXsd;
    private Path xml;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        super.preMethodSetup();
        logger = LoggerFactory.getLogger(this.getClass());
        iXsdXmlValidator = new XsdXmlValidatorImpl();
        xsd = Paths.get(testResources.toString(), XML_STRING, PATH_MAPPINGS_XSD);
        secRiskXsd = Paths.get(testResources.toString(), XML_STRING, SECURITY_RISK_XSD);
        xml = Paths.get(testResources.toString(), XML_STRING, PATH_MAPPINGS_XML);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testValidateAndTrimByDomRemovesEmptyNodes() throws Exception {
        // Given
        String xml = EMPTY_STRING +
                "<group>\n" +
                "    <parent1>\n" +
                "        <parent1child1>I am not empty</parent1child1>\n" +
                "        <parent1child2>  </parent1child2>\n" +
                "        <parent1child3> Me too not empty </parent1child3>\n" +
                "    </parent1>\n" +
                "    <parent2>\n" +
                "        <parent2child1>  </parent2child1>\n" +
                "        <parent2child2></parent2child2>\n" +
                "        <parent2child3>   </parent2child3>\n" +
                "    </parent2>\n" +
                "</group>";
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        XmlContainer xmlContainer = xmlSchemaParser.validateAndTrimByDom(xml.getBytes(), false);

        // Then
        Document document = xmlContainer.getDocument();
        assertThat(document.getElementsByTagName("parent1").getLength(), is(1));
        assertThat(document.getElementsByTagName("parent1child1").getLength(), is(1));
        assertThat(document.getElementsByTagName("parent1child2").getLength(), is(0));
        assertThat(document.getElementsByTagName("parent1child3").getLength(), is(1));

        assertThat(document.getElementsByTagName("parent2").getLength(), is(0));
        assertThat(document.getElementsByTagName("parent2child1").getLength(), is(0));
        assertThat(document.getElementsByTagName("parent2child2").getLength(), is(0));
        assertThat(document.getElementsByTagName("parent2child3").getLength(), is(0));
    }

    @Test
    public void testValidateXmlWithSchemaValidation() throws Exception {
        // Given
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(readAllBytes(xml), true);

        // Then
        assertTrue(xmlContainer.getXmlErrorHandler().isValidated());
    }

    @Test
    public void testValidateInvalidXmlWithSchemaValidation() throws Exception {
        // Given
        String xmlFailingSchema = EMPTY_STRING +
                "<mappings>\n" +
                "    <mapping>\n" +
                "        <id>203</id>\n" +
                "    </mapping>\n" +
                "</mappings>";
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // Expect
        expectedException.expect(XmlException.class);
        expectedException.expectMessage("Error validating xml");


        // When
        xmlSchemaParser.validateByDom(xmlFailingSchema.getBytes(), true);
    }

    @Test
    public void testValidateInvalidXmlWithoutSchemaValidation() throws Exception {
        // Given
        String xmlFailingSchema = EMPTY_STRING +
                "<mappings>\n" +
                "    <mapping>\n" +
                "        <id>203</id>\n" +
                "    </mapping>\n" +
                "</mappings>";
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(xmlFailingSchema.getBytes(), false);

        // Then
        assertTrue(xmlContainer.getXmlErrorHandler().isValidated());
    }

    @Test
    public void testValidateDOMSourceWithSchemaValidation() throws Exception {
        // Given
        Document document = createDocument(readAllBytes(xml), false);
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        //When
        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(new DOMSource(document), false);

        // Then
        assertTrue(xmlContainer.getXmlErrorHandler().isValidated());
    }

    @Test
    public void testValidateInvalidDOMSourceWithSchemaValidation() throws Exception {
        // Given
        String xmlFailingSchema = EMPTY_STRING +
                "<mappings>\n" +
                "    <mapping>\n" +
                "        <id>203</id>\n" +
                "    </mapping>\n" +
                "</mappings>";
        Document document = createDocument(xmlFailingSchema.getBytes(), false);
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // Expect
        expectedException.expect(XmlException.class);
        expectedException.expectMessage("Error validating xml");


        // When
        xmlSchemaParser.validateByDom(new DOMSource(document), true);
    }

    @Test
    public void testValidateInvalidDOMSourceWithoutSchemaValidation() throws Exception {
        // Given
        String xmlFailingSchema = EMPTY_STRING +
                "<mappings>\n" +
                "    <mapping>\n" +
                "        <id>203</id>\n" +
                "    </mapping>\n" +
                "</mappings>";
        Document document = createDocument(xmlFailingSchema.getBytes(), false);
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(new DOMSource(document), false);

        // Then
        assertFalse(xmlContainer.getXmlErrorHandler().isValidated());
    }

    @Test
    public void testXmlToString() throws Exception {
        // Given
        String xml = EMPTY_STRING +
                "<mappings>\n" +
                "    <mapping>\n" +
                "        <id>203</id>\n" +
                "    </mapping>\n" +
                "</mappings>";
        Document document = createDocument(xml.getBytes(), false);
        Node mappings = document.getElementsByTagName("mappings").item(0);
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        String result = xmlSchemaParser.xmlToString(mappings);

        // Then
        assertTrue(XMLUnit.compareXML(xml, result).identical());
    }

    @Test
    public void testXmlInContainerToString() throws Exception {
        // Given
        String xml = EMPTY_STRING +
                "<mappings>\n" +
                "    <mapping>\n" +
                "        <id>203</id>\n" +
                "    </mapping>\n" +
                "</mappings>";
        Document document = createDocument(xml.getBytes(), false);
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        String result = xmlSchemaParser.xmlToString(new XmlContainer(document, null, null));

        // Then
        assertTrue(XMLUnit.compareXML(xml, result).identical());
    }

    @Test
    public void testTrimDOMElement() throws Exception {

    }

    @Test
    public void testFetchDomTextElementByTag() throws Exception {
        // Given
        String xml = EMPTY_STRING +
                "<mappings>\n" +
                "    <mapping>\n" +
                "        <id>203</id>\n" +
                "    </mapping>\n" +
                "    <mapping>\n" +
                "        <id>503</id>\n" +
                "    </mapping>\n" +
                "</mappings>";
        Document document = createDocument(xml.getBytes(), false);

        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        String result = xmlSchemaParser.fetchDomTextElementByTag(document.getDocumentElement(), "id");

        // Then
        assertThat(result, is("203"));
    }

    @Test
    public void testGetListByTag() throws Exception {
        startingTestMethod(Thread.currentThread().getStackTrace()[1].getMethodName());
        XmlSchemaParser schemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);
        logger.info("Schema loaded: " + xsd);
        XmlContainer xmlContainer = schemaParser.validateByDom(readAllBytes(xml), true);
        assertTrue(xmlContainer.getXmlErrorHandler().isValidated());
        logger.info("XML validated: " + xml);
        NodeList mappingList = schemaParser.getListByTag(xmlContainer, MAPPING_TAG);
        assertTrue("Wrong number of document nodes for tag " + MAPPING_TAG, mappingList.getLength() == 5);
        Element mapping;
        List<Node> subList;
        String[] tags = {"serviceType", "id", "put"};
        int j;
        for (int i = 0; i < mappingList.getLength(); i++) {
            j = 0;
            mapping = ((Element) mappingList.item(i));
            subList = schemaParser.getChildElementsList(mapping);
            assertTrue("Wrong number of sub-list nodes for tag " + MAPPING_TAG, subList.size() == 3);
            for (Node subTag : subList) {
                if (subTag instanceof Element) {
                    assertTrue(((Element) subTag).getTagName().endsWith(tags[j++]));
                }
            }
        }
        logger.info("XML and sub-lists number and positions of elements are confirmed.");
        finishingTestMethod(Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Test
    public void testSearchForNode() throws Exception {
        // Given
        String xml = EMPTY_STRING +
                "<invoices>\n" +
                "    <invoice>\n" +
                "        <supplier>\n" +
                "            <name>abc</name>\n" +
                "            <orgNumber>12345</orgNumber>\n" +
                "        </supplier>\n" +
                "        <buyer>\n" +
                "            <name>xyz</name>\n" +
                "            <orgNumber>67890</orgNumber>\n" +
                "        </buyer>\n" +
                "    </invoice>\n" +
                "</invoices>";
        Document document = createDocument(xml.getBytes(), false);

        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        Element element = xmlSchemaParser.searchForNode(document.getDocumentElement(), "invoice", "buyer", "name");

        // Then
        assertNotNull(element);
        assertThat(element.getTextContent(), is("xyz"));
    }

    @Test
    public void testSearchForNodeWhenNoMatchFound() throws Exception {
        // Given
        String xml = EMPTY_STRING +
                "<invoices>\n" +
                "    <invoice>\n" +
                "        <supplier>\n" +
                "            <name>abc</name>\n" +
                "            <orgNumber>12345</orgNumber>\n" +
                "        </supplier>\n" +
                "        <buyer>\n" +
                "            <name>xyz</name>\n" +
                "            <orgNumber>67890</orgNumber>\n" +
                "        </buyer>\n" +
                "    </invoice>\n" +
                "</invoices>";
        Document document = createDocument(xml.getBytes(), false);

        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // Expect
        expectedException.expect(XmlException.class);
        expectedException.expectMessage("Not able to find tag");

        // When
        xmlSchemaParser.searchForNode(document.getDocumentElement(), "invoice", "buyer", "name", "firstName");
    }

    @Test
    public void testSearchForNodeFromMultipleMatches() throws Exception {
        // Given
        String xml = EMPTY_STRING +
                "<invoices>\n" +
                "    <invoice>\n" +
                "        <supplier>\n" +
                "            <name>abc</name>\n" +
                "            <orgNumber>12345</orgNumber>\n" +
                "        </supplier>\n" +
                "        <supplier>\n" +
                "            <name>xyz</name>\n" +
                "            <orgNumber>67890</orgNumber>\n" +
                "        </supplier>\n" +
                "    </invoice>\n" +
                "</invoices>";
        Document document = createDocument(xml.getBytes(), false);

        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(xsd), iXsdXmlValidator);

        // When
        Element element = xmlSchemaParser.searchForNode(document.getDocumentElement(), "invoice", "supplier", "name");

        // Then
        assertNotNull(element);
        assertThat(element.getTextContent(), is("abc"));
    }

    @Test
    public void readPotentiallyDangerousXml() throws Exception {
        String xml = getXmlString("&file;", true);
        Document document = createDocument(xml.getBytes(), false);
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(secRiskXsd), iXsdXmlValidator);
        List<String> lines = Files.readAllLines(Paths.get(testResources.toString(), "pass.properties"), Charset.forName("UTF-8"));
        String ext = EMPTY_STRING;
        for (String s : lines) {
            ext += s + '\n';
        }
        Assert.assertEquals(xmlSchemaParser.xmlToString(document).trim().replace("\n", "").replace("\r", ""),
                getXmlString(ext, false).trim().replace("\n", "").replace("\r", ""));
    }

    @Test (expected = SAXParseException.class)
    public void rejectPotentiallyDangerousXml() throws IOException, SAXException, ParserConfigurationException{
        String xml = getXmlString("&file;", true);
        Document document = createDocument(xml.getBytes(), true);
        Assert.assertNull(document);
    }

    @Test (expected = XmlException.class)
    public void readPotentiallyDangerousXmlFromParser() throws Exception {
        String xml = getXmlString("&file;", true);
        XmlSchemaParser xmlSchemaParser = new XmlSchemaParser(readAllBytes(secRiskXsd), iXsdXmlValidator);
        Document document = xmlSchemaParser.validateAndTrimByDom(xml.getBytes(), true).getDocument();
        Assert.assertNull(document);
    }

    private String getXmlString(String fileRef, boolean isUpdateProfile){
        return "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
               ((isUpdateProfile) ? "<!DOCTYPE updateProfile [<!ENTITY file SYSTEM \"" + testResources.toString() + "/pass.properties\">]>\n" : EMPTY_STRING) +
                     "<updateProfile>\n" +
                     "    <firstname>Joe</firstname>\n" +
                     "    <lastname>" + fileRef + "</lastname>\n" +
                     "</updateProfile>";
    }

    private Document createDocument(byte[] content, boolean rejectAccessToExternalDTD) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        if (rejectAccessToExternalDTD) {
            domFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, EMPTY_STRING);
            domFactory.setFeature(APACHE_DISALLOW_DOCTYPE_DECL, true);
        }
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(content));
    }
}