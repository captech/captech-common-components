package eu.captech.digitalization.commons.basic.type;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;

public class FileTypeTest {

    @Test
    public void testFileTypes() throws Exception {
        List<String> expectedFileTypes = asList("TIF", "TIFF", "PDF", "JPEG", "JPG", "JP2", "PNG", "BMP", "PNM", "GIF", "ZIP", "XML", "CSV", "TXT", "TEMP", "EPS", "SVG", "PSD", "AI", "HTML", "TYPENOTFOUND");
        List<String> actualFileTypes = new ArrayList<>();
        for (FileType fileType : FileType.values()) {
            actualFileTypes.add(fileType.name());
        }

        Assert.assertTrue(new HashSet<>(expectedFileTypes).equals(new HashSet<>(actualFileTypes)));
    }
}