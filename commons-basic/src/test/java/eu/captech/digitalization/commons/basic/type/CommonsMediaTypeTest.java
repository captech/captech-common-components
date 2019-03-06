package eu.captech.digitalization.commons.basic.type;

import eu.captech.digitalization.commons.basic.BasicCommonsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.IMAGE_JPEG_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.IMAGE_TIFF_TYPE;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.getCommonsMediaTypeBasedOnSubType;
import static eu.captech.digitalization.commons.basic.type.CommonsMediaType.hasSubType;
import static java.net.InetAddress.getLocalHost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CommonsMediaTypeTest extends BasicCommonsTest {

    public static final String KEY_1 = "key1";
    public static final String KEY_2 = "key2";
    public static final String VALUE_2 = "value2";
    public static final String VALUE_1 = "value1";
    public static final String TIFF_SUFFIX = ".tiff";
    public static final String TIFF_STRING = "tiff";
    public static final String QWERTY_STRING = "qwerty";
    public static final String IMAGE_STRING = "image";
    private static final String OS_VERSION = "os.version";
    private static final String OS_NAME = "os.name";

    @Before
    public void setUp() throws Exception{
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        logger = LoggerFactory.getLogger(getClass());
    }

    @After
    public void tearDown() {
        super.postMethodSetup();
    }

    @Test
    public void testGetCommonsMediaTypeBasedOnSubType() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        logger.info("Comparing to Types...");
        assertEquals(getCommonsMediaTypeBasedOnSubType(TIFF_STRING), IMAGE_TIFF_TYPE);
        assertNotEquals(getCommonsMediaTypeBasedOnSubType(TIFF_STRING), IMAGE_JPEG_TYPE);
        logger.info("Sub Type exists...");
        assertTrue(hasSubType(TIFF_STRING));
        assertFalse(hasSubType(QWERTY_STRING));
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test
    public void testEquals() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        //noinspection ObjectEqualsNull
        assertFalse(IMAGE_TIFF_TYPE.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(IMAGE_TIFF_TYPE.equals(QWERTY_STRING));
        CommonsMediaType type = new CommonsMediaType(IMAGE_STRING, TIFF_STRING, null);
        assertEquals(IMAGE_TIFF_TYPE, type);
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test
    public void testIsWildcardTypeSubTypeWithParameters() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        logger.info("Creating a wild card media type and sub-type with parameters...");
        Map<String, String> map = new HashMap<>();
        map.put(KEY_1, VALUE_1);
        map.put(KEY_2, VALUE_2);
        CommonsMediaType type = new CommonsMediaType(null, null, map);
        assertTrue(type.isWildcardType());
        assertTrue(type.isWildcardSubtype());
        logger.info("Retrieving parameters...");
        map = type.getParameters();
        assertEquals(VALUE_1, map.get(KEY_1));
        assertEquals(VALUE_2, map.get(KEY_2));
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");

    }

    @Test
    public void testGetSuffix() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        assertEquals(TIFF_SUFFIX, IMAGE_TIFF_TYPE.getSuffix());
        assertEquals(-877210160, IMAGE_TIFF_TYPE.hashCode());
        assertEquals(IMAGE_TIFF_TYPE.toString(), "image/tiff");
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");

    }

    @Test
    public void testIsCompatible() throws Exception{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        logger.info("Creating two media type and sub-type without parameters...");
        CommonsMediaType type1 = new CommonsMediaType("testable", "obj1", null);
        CommonsMediaType type2 = new CommonsMediaType("testable", "obj2", null);
        CommonsMediaType type3 = new CommonsMediaType("testable", "obj1", null);
        CommonsMediaType type4 = new CommonsMediaType(null, "obj1", null);
        CommonsMediaType type5 = new CommonsMediaType("testable", null, null);
        CommonsMediaType type6 = new CommonsMediaType("testable_A", null, null);
        assertFalse(type1.isCompatible(null));
        assertFalse(type1.isCompatible(type2));
        assertTrue(type1.isCompatible(type3));
        assertTrue(type2.isCompatible(type4));
        assertFalse(type2.isCompatible(type6));
        assertTrue(type4.isCompatible(type6));
        assertTrue(type1.isCompatible(type4));
        assertTrue(type1.isCompatible(type5));
        assertTrue(type2.isCompatible(type5));
        assertTrue(type3.isCompatible(type5));
        assertTrue(type5.isCompatible(type3));
        assertTrue(type4.isCompatible(type5));
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }

    @Test
    public void systemProperties() throws UnknownHostException{
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Starting method.");
        logger.info(OS_VERSION + ": " + System.getProperty(OS_VERSION));
        logger.info(OS_NAME + ": " + System.getProperty(OS_NAME));
        logger.info("HOST: " + getLocalHost().getHostName());
        logger.info("****** " + Thread.currentThread().getStackTrace()[1].getMethodName() + " :: Method done.");
    }
}