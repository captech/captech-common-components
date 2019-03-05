package eu.captech.digitalization.commons.basic.xml;

import eu.captech.digitalization.commons.basic.BasicCommonsTest;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.XmlException;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "11/30/12",
        creationTime = "9:49 AM",
        lastModified = "11/30/12"
)
public class XsdXmlValidatorImplTest extends BasicCommonsTest {
    private static final String XML_NAME = "pathMappings.xml";
    private static final String XSD_NAME = "pathMappings.xsd";
    private static Path TEST_RESOURCES = Paths.get("src", "test", "resources", "xml");
    private XmlSchemaParser xmlSchemaParser;
    private eu.captech.digitalization.commons.basic.api.IXsdXmlValidator IXsdXmlValidator = new XsdXmlValidatorImpl();

    @Before
    public void setUp()
            throws Exception {
        super.preMethodSetup();
        logger = getLoggerFor(this.getClass());
        logger.info("****************************************************************");
        logger.info("Starting preMethodSetup for " + this.getClass().getSimpleName());
        logger.info("****************************************************************");
    }

    @After
    public void postMethodSetup()
            throws Exception {
        logger.info("****************************************************************");
        logger.info("Finishing postMethodSetup for " + this.getClass().getSimpleName());
        logger.info("****************************************************************");
        super.tearDown();
    }

    @Test
    public void testXmlFile() throws IOException, XmlException {
        xmlSchemaParser = new XmlSchemaParser(Files.readAllBytes(Paths.get(TEST_RESOURCES.toString(), XSD_NAME)), IXsdXmlValidator);
        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(Files.readAllBytes(Paths.get(TEST_RESOURCES.toString(), XML_NAME)), true);
        if (logger.isInfoEnabled()) {
            logger.info(xmlContainer.getXmlErrorHandler().toString());
            logger.info(xmlContainer.toString());
            logger.info(xmlSchemaParser.xmlToString(xmlContainer.getDocument()));
        }
    }

    @Test
    public void testDOMSourceXmlFile() throws IOException, XmlException {
        xmlSchemaParser = new XmlSchemaParser(Files.readAllBytes(Paths.get(TEST_RESOURCES.toString(), XSD_NAME)), IXsdXmlValidator);
        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(Files.readAllBytes(Paths.get(TEST_RESOURCES.toString(), XML_NAME)), false);
        logger.info(xmlSchemaParser.xmlToString(xmlContainer.getDocument()));
        xmlContainer = xmlSchemaParser.validateByDom(new DOMSource(xmlContainer.getDocument()), true);
        if (logger.isInfoEnabled()) {
            logger.info(xmlContainer.getXmlErrorHandler().toString());
            logger.info(xmlContainer.toString());
            logger.info(xmlSchemaParser.xmlToString(xmlContainer.getDocument()));
        }
    }

    @Test
    public void testXmlFileAsResource() throws IOException, XmlException {
        xmlSchemaParser = new XmlSchemaParser(IOUtils.toByteArray(this.getClass().getResourceAsStream("/xml/pathMappings.xsd")), IXsdXmlValidator);
        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(IOUtils.toByteArray(this.getClass().getResourceAsStream("/xml/pathMappings.xml")), true);
        if (logger.isInfoEnabled()) {
            logger.info(xmlContainer.getXmlErrorHandler().toString());
            logger.info(xmlContainer.toString());
            logger.info(xmlSchemaParser.xmlToString(xmlContainer.getDocument()));
        }
    }


//    @Test
//    public void testTransferMappingsDefs() throws IOException, XmlException, ParameterException{
//        xmlSchemaParser = new XmlSchemaParser(IOUtils.toByteArray(this.getClass().getResourceAsStream("/xml/pathMappings.xsd")), IXsdXmlValidator);
//        XmlContainer xmlContainer = xmlSchemaParser.validateByDom(IOUtils.toByteArray(this.getClass().getResourceAsStream("/xml/pathMappings.xml")), true);
//        if(logger.isInfoEnabled()) {
//            logger.info(xmlContainer.getXmlErrorHandler().toString());
//            logger.info(xmlContainer.toString());
//            logger.info(xmlSchemaParser.xmlToString(xmlContainer.getDocument()));
//        }
//        Map<String, TransferMappingDefs> defMap = TransferMappingDefs.setupTransferMappingDefinitions(xmlSchemaParser, xmlContainer);
//        Set<String> keySet = defMap.keySet();
//        for(String s : keySet) {
//            if(logger.isInfoEnabled()) {
//                logger.info(s + ":\t" + defMap.get(s));
//            }
//        }
//    }
}
