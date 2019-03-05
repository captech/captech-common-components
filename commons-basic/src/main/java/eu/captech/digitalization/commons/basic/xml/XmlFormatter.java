package eu.captech.digitalization.commons.basic.xml;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Set;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "8/28/12",
        creationTime = "4:41 PM",
        lastModified = "8/28/12"
)
public enum XmlFormatter {
    formatter;
    public final HashMap<String, String> transformerStaticProperties = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(XmlFormatter.class);
    private static final String APACHE_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

    private XmlFormatter() {
        transformerStaticProperties.put(OutputKeys.INDENT, "yes");
        transformerStaticProperties.put(OutputKeys.METHOD, "xml");
        transformerStaticProperties.put("encoding", "UTF-8");
        transformerStaticProperties.put("{http://xml.apache.org/xslt}indent-amount", "3");
    }

    public String format(String input) {
        try {
            Transformer transformer = getTransformer(transformerStaticProperties);
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(parseXmlFile(input));
            transformer.transform(source, result);
            return result.getWriter().toString();
        }
        catch (TransformerException e) {
            logger.warn("Not able to parse document: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setFeature(APACHE_DISALLOW_DOCTYPE_DECL, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            logger.warn("Not able to parse document: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Transformer getTransformer(@Nullable HashMap<String, String> transformerProperties) throws TransformerConfigurationException {
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        if (transformerProperties == null || transformerProperties.isEmpty()) {
            transformerProperties = transformerStaticProperties;
        }
        Set<String> set = transformerProperties.keySet();
        for (String s : set) {
            tr.setOutputProperty(s, transformerProperties.get(s));
        }
        return tr;
    }
}
