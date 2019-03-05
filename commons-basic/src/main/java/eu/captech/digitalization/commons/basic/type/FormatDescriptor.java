package eu.captech.digitalization.commons.basic.type;

import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.util.Arrays;
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

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "9/20/12",
        creationTime = "3:43 PM",
        lastModified = "9/20/12"
)
public enum FormatDescriptor {
    txt(new String[]{".txt", ".text"}, TEXT_PLAIN_TYPE, TXT),
    xml(new String[]{".xml", ".xsd"}, TEXT_XML_TYPE, XML),
    html(new String[]{".html", ".htm"}, TEXT_HTML_TYPE, HTML),
    tif(new String[]{".tif", ".tiff"}, IMAGE_TIFF_TYPE, TIF),
    png(new String[]{".png"}, IMAGE_PNG_TYPE, PNG),
    gif(new String[]{".gif"}, IMAGE_GIF_TYPE, GIF),
    jpg(new String[]{".jpg", ".jpeg"}, IMAGE_JPEG_TYPE, JPG),
    svg(new String[]{".svg"}, APPLICATION_SVG_XML_TYPE, SVG),
    psd(new String[]{".psd"}, APPLICATION_PSD_TYPE, PSD),
    pdf(new String[]{".pdf", ".ai"}, APPLICATION_PDF_TYPE, PDF),
    eps(new String[]{".eps"}, APPLICATION_POSTSCRIPT_TYPE, EPS),
    ai(new String[]{".ai"}, APPLICATION_POSTSCRIPT_TYPE, AI),
    zip(new String[]{".zip"}, APPLICATION_ZIP_TYPE, ZIP),
    csv(new String[]{".csv"}, TEXT_CSV_TYPE, CSV),
    e2b(new String[]{".e2breceipt", ".e2b"}, TEXT_XML_TYPE, XML);
    private final String id;
    private final String[] suffixes;
    private final FileType fileType;
    private final CommonsMediaType contentType;

    private FormatDescriptor(String[] suffixes, CommonsMediaType contentType, FileType fileType) {
        this.id = this.name();
        this.suffixes = suffixes;
        this.fileType = fileType;
        this.contentType = contentType;
    }

    public boolean hasSuffix(String suffix) {
        for (String s : getSuffixes()) {
            if (s.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public List<String> getSuffixes() {
        return Arrays.asList(suffixes);
    }

    public FileType getFileType() {
        return fileType;
    }

    public CommonsMediaType getContentType() {
        return contentType;
    }
}
