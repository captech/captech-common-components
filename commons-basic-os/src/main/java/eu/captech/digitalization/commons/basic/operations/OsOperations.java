package eu.captech.digitalization.commons.basic.operations;

import eu.captech.digitalization.commons.basic.exception.XmlException;
import eu.captech.digitalization.commons.basic.xml.XmlContainer;
import eu.captech.digitalization.commons.basic.xml.XmlSchemaParser;
import eu.captech.digitalization.commons.basic.xml.XsdXmlValidatorImpl;
import eu.captech.digitalization.commons.basic.api.IOperations;
import eu.captech.digitalization.commons.basic.exception.ExecutionException;
import eu.captech.digitalization.commons.basic.exceptions.OperationRuntimeException;
import eu.captech.digitalization.commons.basic.operations.sniff.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;

import org.hyperic.sigar.cmd.Shell;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;

import static eu.captech.digitalization.commons.basic.operations.sniff.JvmSniffer.jvmSniffer;
import static eu.captech.digitalization.commons.basic.operations.sniff.MemSniffer.*;

public class OsOperations implements IOperations {
    private static final Logger logger = LoggerFactory.getLogger(OsOperations.class);
    private static final String OS_PATTERN_FORM = "os";
    private static final String INFORMATION_XSD = "/xml/OsInformation.xsd";
    private static final int FACTOR_1 = 1;
    private static final String HTTP_WWW_W3_ORG_2001_XMLSCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XMLNS_XSI = "xmlns:xsi";
    private String initialParameter = OPERATIONS_PROTOCOL_OS;
    private String protocol = OPERATIONS_PROTOCOL_OS;
    private XmlSchemaParser xmlSchemaParser;
    private SnifferUtils snifferUtils = SnifferUtils.getInstance();

    public OsOperations() {
        parseXsd();
    }

    public OsOperations(String initialParameter) {
        this.initialParameter = initialParameter;
        parseXsd();
    }

    @Override
    public String getOperationsInfo(@Nullable String parameter) {
        StringBuilder stringBuilder = new StringBuilder();
        if (parameter.equals(MemSniffer.ALL_MEMORY_TYPE)) {
            try {
                getByMemoryType(MEM_MEMORY_TYPE, stringBuilder);
            }
            catch (ExecutionException e) {
                logger.warn("Unable to extract Memory information for " + MEM_MEMORY_TYPE + ": " +
                        e.getMessage(), e);
            }
            try {
                getByMemoryType(MemSniffer.SWAP_MEMORY_TYPE, stringBuilder);
            }
            catch (ExecutionException e) {
                logger.warn("Unable to extract Memory information for " + SWAP_MEMORY_TYPE + ": " +
                        e.getMessage(), e);
            }
            try {
                getByMemoryType(MemSniffer.JVM_MEMORY_TYPE, stringBuilder);
            }
            catch (ExecutionException e) {
                logger.warn("Unable to extract Memory information for " + JVM_MEMORY_TYPE + ": " +
                        e.getMessage(), e);
            }
        }
        else if (parameter.endsWith(MemSniffer.MEMORY_TYPE_STRING)) {
            try {
                getByMemoryType(parameter, stringBuilder);
            }
            catch (ExecutionException e) {
                throw new OperationRuntimeException("Unable to extract Memory information for " + parameter + ": " +
                        e.getMessage(), e);
            }
        }
        else if (parameter.equals(JvmSniffer.JVM_PID)) {
            stringBuilder.append(JvmSniffer.JVM_PID).append(SystemUtils.LINE_SEPARATOR);
            try {
                stringBuilder.append("pid:").append(jvmSniffer.getPid()).append(SystemUtils.LINE_SEPARATOR);
            }
            catch (ExecutionException e) {
                throw new OperationRuntimeException("Unable to extract PID information for " + parameter + ": " +
                        e.getMessage(), e);
            }
        }
        else if (parameter.equals(CpuSniffer.CPU_PID_PERCENTAGE)) {
        	
			try {
				Map<String, Number> cpuDetails = snifferUtils.getJvmCpuTime();
				stringBuilder.append(CpuSniffer.CPU_PID_PERCENTAGE).append(":")
					.append(cpuDetails.get(CpuSniffer.CPU_PID_PERCENTAGE)).append(SystemUtils.LINE_SEPARATOR);
			} catch (ExecutionException e) {
				throw new OperationRuntimeException("Unable to extract CPU information for " + parameter + ": " +
                        e.getMessage(), e);
			}
			
        }
        else {
        	throw new UnsupportedOperationException(parameter + " is not supported");
        }
        return stringBuilder.toString();
    }

    private StringBuilder getByMemoryType(String memoryType, StringBuilder stringBuilder) throws ExecutionException {
        Map<String, Number> map = snifferUtils.getByMemoryType(memoryType);
        stringBuilder.append(memoryType).append(SystemUtils.LINE_SEPARATOR);
        int factor = FACTOR_1;
        for (String s : map.keySet()) {
            Double value = (Double) map.get(s);
            boolean isPercentage = false;
            if (s.endsWith("Percentage")) {
                isPercentage = true;
            }
            stringBuilder.append(s).append(':').append(getDoubleValue(value, isPercentage, factor)).append(SystemUtils.LINE_SEPARATOR);
            if (logger.isInfoEnabled()) {
                logger.info("Memory '" + memoryType + "' :: " + s + ": " + getDoubleValue(value, isPercentage, factor) +
                        ((isPercentage) ? " %." : " KB."));
            }
        }
        return stringBuilder;
    }

    private double getDoubleValue(Number value, boolean percentage, int factor) {
        BigDecimal bd = null;
        if (value instanceof Double) {
            bd = new BigDecimal((percentage) ? (Double) value : ((Double) value / factor));
        }
        else if (value instanceof Integer) {
            bd = new BigDecimal((percentage) ? (Integer) value : ((Integer) value / factor));
        }
        else if (value instanceof Long) {
            bd = new BigDecimal((percentage) ? (Long) value : ((Long) value / factor));
        }
        if (bd != null) {
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
//            System.err.println("out: " + bd.doubleValue());
            return bd.doubleValue();
        }
        return -1;
    }

    //    @Override
    public String getOperations(@Nullable String parameter) {
        Shell shell = new Shell();
        Document document = createDocument();
        document = getFileSystemInfo(shell, document);
        document = CpuSniffer.cpuSniffer.appendCpuInfo(document, (Element) document.getFirstChild(), shell);
        document = MemSniffer.memSniffer.appendMemInfo(document, (Element) document.getFirstChild(), shell);
        try {
//            System.err.println(xmlSchemaParser.xmlToString(document));
            XmlContainer xmlContainer = xmlSchemaParser.validateByDom((xmlSchemaParser.xmlToString(document)).getBytes("UTF-8"), true);
            return xmlSchemaParser.xmlToString(xmlContainer.getDocument());
        }
        catch (XmlException | UnsupportedEncodingException e) {
            throw new OperationRuntimeException("Error generating XML for DF Information: " + e.getMessage(), e);
        }
    }

    private Document getFileSystemInfo(Shell shell, Document document) {
        document = DfSniffer.dfSniffer.appendDf(document, (Element) document.getFirstChild(), shell);
        try {
            Element documentElement = document.getDocumentElement();
            Element dfInfo = xmlSchemaParser.searchForNode(documentElement, "dfInfo");
            Element systemError = null;
            try {
                systemError = xmlSchemaParser.searchForNode(dfInfo, "fileSystems", "systemError");
            }
            catch (XmlException ignore) {
            }
            if (systemError != null) {
                Element status = xmlSchemaParser.searchForNode(dfInfo, "status");
                status.setTextContent("99");

            }
            else {
                Element error = (Element) dfInfo.getElementsByTagName("error").item(0);
                if (error != null) {
                    Element status = xmlSchemaParser.searchForNode(dfInfo, "status");
                    status.setTextContent("50");
                }
            }
        }
        catch (XmlException e) {
            throw new OperationRuntimeException("Error extracting XML for DF Information: " + e.getMessage(), e);
        }
        return document;
    }

    @Override
    public String getInitialParameter() {
        return this.initialParameter;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public boolean isConnected(String parameter) {
        return true;
    }

    @Override
    public String checkProtocolPatterns(String evaluate) {
        return OS_PATTERN_FORM;
    }

    @Override
    public String getOperationPatternForm() {
        return OS_PATTERN_FORM;
    }

    @Override
    public int compareTo(IOperations o) {
        return getInitialParameter().compareTo(o.getInitialParameter());
    }

    private Document createDocument() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new OperationRuntimeException("Unable to create XML.", e);
        }
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement("osInformation");
        root.setAttribute(XMLNS_XSI, HTTP_WWW_W3_ORG_2001_XMLSCHEMA_INSTANCE);
        doc.appendChild(root);
        return doc;
    }

    private void parseXsd() {
        try {
            xmlSchemaParser = new XmlSchemaParser(IOUtils.toByteArray(this.getClass().getResourceAsStream(INFORMATION_XSD)), new XsdXmlValidatorImpl());
        }
        catch (Exception e) {
            throw new OperationRuntimeException("Unable to parse XSD file for OS Operations: " + e.getMessage(), e);
        }
        printOperation();
    }

    private void printOperation() {
        if (logger.isInfoEnabled()) {
            logger.info("Operation: " + this.getClass().getSimpleName() + "\tInitialParameter: " + initialParameter);
        }
    }
}
