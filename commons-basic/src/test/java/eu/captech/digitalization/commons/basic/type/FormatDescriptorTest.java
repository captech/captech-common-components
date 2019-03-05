package eu.captech.digitalization.commons.basic.type;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.APPLICATION_PDF_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.APPLICATION_POSTSCRIPT_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.APPLICATION_PSD_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.APPLICATION_SVG_XML_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.APPLICATION_ZIP_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.IMAGE_GIF_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.IMAGE_JPEG_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.IMAGE_PNG_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.IMAGE_TIFF_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.TEXT_CSV_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.TEXT_HTML_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.TEXT_PLAIN_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.TEXT_XML_TYPE;
import static eu.captech.digitalization.commons.basic.type.FileType.AI;
import static eu.captech.digitalization.commons.basic.type.FileType.CSV;
import static eu.captech.digitalization.commons.basic.type.FileType.EPS;
import static eu.captech.digitalization.commons.basic.type.FileType.GIF;
import static eu.captech.digitalization.commons.basic.type.FileType.HTML;
import static eu.captech.digitalization.commons.basic.type.FileType.JPG;
import static eu.captech.digitalization.commons.basic.type.FileType.PDF;
import static eu.captech.digitalization.commons.basic.type.FileType.PNG;
import static eu.captech.digitalization.commons.basic.type.FileType.PSD;
import static eu.captech.digitalization.commons.basic.type.FileType.SVG;
import static eu.captech.digitalization.commons.basic.type.FileType.TIF;
import static eu.captech.digitalization.commons.basic.type.FileType.TXT;
import static eu.captech.digitalization.commons.basic.type.FileType.XML;
import static eu.captech.digitalization.commons.basic.type.FileType.ZIP;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.ai;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.csv;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.e2b;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.eps;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.gif;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.html;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.jpg;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.pdf;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.png;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.psd;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.svg;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.tif;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.txt;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.xml;
import static eu.captech.digitalization.commons.basic.type.FormatDescriptor.zip;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class FormatDescriptorTest {

    @Test
    public void testFormatDescriptorText() {
        assertTrue(txt.hasSuffix(".txt"));
        assertTrue(txt.getId().equals("txt"));
        assertTrue(equalList(asList(".txt", ".text"), (txt.getSuffixes())));
        assertTrue(txt.getFileType().equals(TXT));
        assertTrue(txt.getContentType().equals(TEXT_PLAIN_TYPE));
    }

    @Test
    public void testFormatDescriptorXml() {
        assertTrue(xml.hasSuffix(".xml"));
        assertTrue(xml.getId().equals("xml"));
        assertTrue(equalList(asList(".xml", ".xsd"), (xml.getSuffixes())));
        assertTrue(xml.getFileType().equals(XML));
        assertTrue(xml.getContentType().equals(TEXT_XML_TYPE));
    }

    @Test
    public void testFormatDescriptorHtml() {
        assertTrue(html.hasSuffix(".html"));
        assertTrue(html.getId().equals("html"));
        assertTrue(equalList(asList(".htm", ".html"), (html.getSuffixes())));
        assertTrue(html.getFileType().equals(HTML));
        assertTrue(html.getContentType().equals(TEXT_HTML_TYPE));
    }

    @Test
    public void testFormatDescriptorTiff() {
        assertTrue(tif.hasSuffix(".tiff"));
        assertTrue(tif.getId().equals("tif"));
        assertTrue(equalList(asList(".tif", ".tiff"), (tif.getSuffixes())));
        assertTrue(tif.getFileType().equals(TIF));
        assertTrue(tif.getContentType().equals(IMAGE_TIFF_TYPE));
    }

    @Test
    public void testFormatDescriptorPng() {
        assertTrue(png.hasSuffix(".png"));
        assertTrue(png.getId().equals("png"));
        assertTrue(equalList(asList(".png"), (png.getSuffixes())));
        assertTrue(png.getFileType().equals(PNG));
        assertTrue(png.getContentType().equals(IMAGE_PNG_TYPE));
    }

    @Test
    public void testFormatDescriptorGif() {
        assertTrue(gif.hasSuffix(".gif"));
        assertTrue(gif.getId().equals("gif"));
        assertTrue(equalList(asList(".gif"), (gif.getSuffixes())));
        assertTrue(gif.getFileType().equals(GIF));
        assertTrue(gif.getContentType().equals(IMAGE_GIF_TYPE));
    }

    @Test
    public void testFormatDescriptorJpg() {
        assertTrue(jpg.hasSuffix(".jpg"));
        assertTrue(jpg.getId().equals("jpg"));
        assertTrue(equalList(asList(".jpg", ".jpeg"), (jpg.getSuffixes())));
        assertTrue(jpg.getFileType().equals(JPG));
        assertTrue(jpg.getContentType().equals(IMAGE_JPEG_TYPE));
    }

    @Test
    public void testFormatDescriptorSvg() {
        assertTrue(svg.hasSuffix(".svg"));
        assertTrue(svg.getId().equals("svg"));
        assertTrue(equalList(asList(".svg"), (svg.getSuffixes())));
        assertTrue(svg.getFileType().equals(SVG));
        assertTrue(svg.getContentType().equals(APPLICATION_SVG_XML_TYPE));
    }

    @Test
    public void testFormatDescriptorPsd() {
        assertTrue(psd.hasSuffix(".psd"));
        assertTrue(psd.getId().equals("psd"));
        assertTrue(equalList(asList(".psd"), (psd.getSuffixes())));
        assertTrue(psd.getFileType().equals(PSD));
        assertTrue(psd.getContentType().equals(APPLICATION_PSD_TYPE));
    }

    @Test
    public void testFormatDescriptorPdf() {
        assertTrue(pdf.hasSuffix(".pdf"));
        assertTrue(pdf.getId().equals("pdf"));
        assertTrue(equalList(asList(".pdf", ".ai"), (pdf.getSuffixes())));
        assertTrue(pdf.getFileType().equals(PDF));
        assertTrue(pdf.getContentType().equals(APPLICATION_PDF_TYPE));
    }

    @Test
    public void testFormatDescriptorEps() {
        assertTrue(eps.hasSuffix(".eps"));
        assertTrue(eps.getId().equals("eps"));
        assertTrue(equalList(asList(".eps"), (eps.getSuffixes())));
        assertTrue(eps.getFileType().equals(EPS));
        assertTrue(eps.getContentType().equals(APPLICATION_POSTSCRIPT_TYPE));
    }

    @Test
    public void testFormatDescriptorAi() {
        assertTrue(ai.hasSuffix(".ai"));
        assertTrue(ai.getId().equals("ai"));
        assertTrue(equalList(asList(".ai"), (ai.getSuffixes())));
        assertTrue(ai.getFileType().equals(AI));
        assertTrue(ai.getContentType().equals(APPLICATION_POSTSCRIPT_TYPE));
    }

    @Test
    public void testFormatDescriptorZip() {
        assertTrue(zip.hasSuffix(".zip"));
        assertTrue(zip.getId().equals("zip"));
        assertTrue(equalList(asList(".zip"), (zip.getSuffixes())));
        assertTrue(zip.getFileType().equals(ZIP));
        assertTrue(zip.getContentType().equals(APPLICATION_ZIP_TYPE));
    }

    @Test
    public void testFormatDescriptorCsv() {
        assertTrue(csv.hasSuffix(".csv"));
        assertTrue(csv.getId().equals("csv"));
        assertTrue(equalList(asList(".csv"), (csv.getSuffixes())));
        assertTrue(csv.getFileType().equals(CSV));
        assertTrue(csv.getContentType().equals(TEXT_CSV_TYPE));
    }

    @Test
    public void testFormatDescriptorE2b() {
        assertTrue(e2b.hasSuffix(".e2b"));
        assertTrue(e2b.getId().equals("e2b"));
        assertTrue(equalList(asList(".e2b", ".e2breceipt"), (e2b.getSuffixes())));
        assertTrue(e2b.getFileType().equals(XML));
        assertTrue(e2b.getContentType().equals(TEXT_XML_TYPE));
    }

    private boolean equalList(List<String> expected, List<String> actual) {
        return new HashSet<>(expected).equals(new HashSet<>(actual));
    }

}