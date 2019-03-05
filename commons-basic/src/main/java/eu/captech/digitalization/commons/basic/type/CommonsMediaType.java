package eu.captech.digitalization.commons.basic.type;

import eu.captech.digitalization.commons.basic.doc.Preamble;

import java.util.*;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "9/21/12",
        creationTime = "10:27 AM",
        lastModified = "9/21/12"
)
public class CommonsMediaType {

    private static final char DOT_CHAR = '.';
    private final static Map<String, CommonsMediaType> COMMONS_MEDIA_TYPE_MAP = new HashMap<>();
    public final static CommonsMediaType TEXT_CSV_TYPE = new CommonsMediaType("text", "csv");
    public final static CommonsMediaType IMAGE_TIF_TYPE = new CommonsMediaType("image", "tif");
    public final static CommonsMediaType IMAGE_TIFF_TYPE = new CommonsMediaType("image", "tiff");
    public final static CommonsMediaType IMAGE_JPG_TYPE = new CommonsMediaType("image", "jpg");
    public final static CommonsMediaType IMAGE_JPEG_TYPE = new CommonsMediaType("image", "jpeg");
    public final static CommonsMediaType IMAGE_GIF_TYPE = new CommonsMediaType("image", "gif");
    public final static CommonsMediaType IMAGE_PNG_TYPE = new CommonsMediaType("image", "png");
    public final static CommonsMediaType APPLICATION_PSD_TYPE = new CommonsMediaType("application", "photoshop");
    public final static CommonsMediaType APPLICATION_PDF_TYPE = new CommonsMediaType("application", "pdf");
    public final static CommonsMediaType APPLICATION_POSTSCRIPT_TYPE = new CommonsMediaType("application", "postscript");
    public final static CommonsMediaType APPLICATION_ZIP_TYPE = new CommonsMediaType("application", "zip");
    public final static String TEXT_CSV = "text/csv";
    public final static String IMAGE_TIFF = "image/tiff";
    public final static String IMAGE_JPEG = "image/jpeg";
    public final static String IMAGE_GIF = "image/gif";
    public final static String IMAGE_PNG = "image/png";
    public final static String APPLICATION_PSD = "application/photoshop";
    public final static String APPLICATION_PDF = "application/pdf";
    public final static String APPLICATION_POSTSCRIPT = "application/postscript";
    public final static String APPLICATION_ZIP = "application/zip";

    /**
     * Empty immutable map used for all instances without parameters
     */
    private static final Map<String, String> emptyMap = Collections.emptyMap();

    /**
     * The value of a type or subtype wildcard: "*"
     */
    public static final String MEDIA_TYPE_WILDCARD = "*";

    // Common media type constants
    /**
     * "*&#47;*"
     */
    public final static String WILDCARD = "*/*";
    /**
     * "*&#47;*"
     */
    public final static CommonsMediaType WILDCARD_TYPE = new CommonsMediaType();

    /**
     * "application/xml"
     */
    public final static String APPLICATION_XML = "application/xml";
    /**
     * "application/xml"
     */
    public final static CommonsMediaType APPLICATION_XML_TYPE = new CommonsMediaType("application", "xml");

    /**
     * "application/atom+xml"
     */
    public final static String APPLICATION_ATOM_XML = "application/atom+xml";
    /**
     * "application/atom+xml"
     */
    public final static CommonsMediaType APPLICATION_ATOM_XML_TYPE = new CommonsMediaType("application", "atom+xml");

    /**
     * "application/xhtml+xml"
     */
    public final static String APPLICATION_XHTML_XML = "application/xhtml+xml";
    /**
     * "application/xhtml+xml"
     */
    public final static CommonsMediaType APPLICATION_XHTML_XML_TYPE = new CommonsMediaType("application", "xhtml+xml");

    /**
     * "application/svg+xml"
     */
    public final static String APPLICATION_SVG_XML = "application/svg+xml";
    /**
     * "application/svg+xml"
     */
    public final static CommonsMediaType APPLICATION_SVG_XML_TYPE = new CommonsMediaType("application", "svg+xml");

    /**
     * "application/json"
     */
    public final static String APPLICATION_JSON = "application/json";
    /**
     * "application/json"
     */
    public final static CommonsMediaType APPLICATION_JSON_TYPE = new CommonsMediaType("application", "json");

    /**
     * "application/x-www-form-urlencoded"
     */
    public final static String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    /**
     * "application/x-www-form-urlencoded"
     */
    public final static CommonsMediaType APPLICATION_FORM_URLENCODED_TYPE = new CommonsMediaType("application", "x-www-form-urlencoded");

    /**
     * "multipart/form-data"
     */
    public final static String MULTIPART_FORM_DATA = "multipart/form-data";
    /**
     * "multipart/form-data"
     */
    public final static CommonsMediaType MULTIPART_FORM_DATA_TYPE = new CommonsMediaType("multipart", "form-data");

    /**
     * "application/octet-stream"
     */
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    /**
     * "application/octet-stream"
     */
    public final static CommonsMediaType APPLICATION_OCTET_STREAM_TYPE = new CommonsMediaType("application", "octet-stream");

    /**
     * "text/plain"
     */
    public final static String TEXT_PLAIN = "text/plain";
    /**
     * "text/plain"
     */
    public final static CommonsMediaType TEXT_PLAIN_TYPE = new CommonsMediaType("text", "plain");

    /**
     * "text/xml"
     */
    public final static String TEXT_XML = "text/xml";
    /**
     * "text/xml"
     */
    public final static CommonsMediaType TEXT_XML_TYPE = new CommonsMediaType("text", "xml");

    /**
     * "text/html"
     */
    public final static String TEXT_HTML = "text/html";
    /**
     * "text/html"
     */
    public final static CommonsMediaType TEXT_HTML_TYPE = new CommonsMediaType("text", "html");

    private String type;
    private String subtype;
    private String suffix;
    private Map<String, String> parameters;

    /**
     * Creates a new instance of CommonsMediaType with the supplied type, subtype and
     * parameters.
     *
     * @param type       the primary type, null is equivalent to
     *                   {@link #MEDIA_TYPE_WILDCARD}.
     * @param subtype    the subtype, null is equivalent to
     *                   {@link #MEDIA_TYPE_WILDCARD}.
     * @param parameters a map of media type parameters, null is the same as an
     *                   empty map.
     */
    public CommonsMediaType(String type, String subtype, Map<String, String> parameters) {
        this.type = type == null ? MEDIA_TYPE_WILDCARD : type;
        this.subtype = subtype == null ? MEDIA_TYPE_WILDCARD : subtype;
        if (parameters == null) {
            this.parameters = emptyMap;
        }
        else {
            Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                map.put(e.getKey().toLowerCase(), e.getValue());
            }
            this.parameters = Collections.unmodifiableMap(map);
        }
        COMMONS_MEDIA_TYPE_MAP.put(this.subtype, this);
    }

    /**
     * Creates a new instance of CommonsMediaType with the supplied type and subtype.
     *
     * @param type    the primary type, null is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     * @param subtype the subtype, null is equivalent to
     *                {@link #MEDIA_TYPE_WILDCARD}
     */
    public CommonsMediaType(String type, String subtype) {
        this(type, subtype, emptyMap);
    }

    /**
     * Creates a new instance of CommonsMediaType, both type and subtype are wildcards.
     * Consider using the constant {@link #WILDCARD_TYPE} instead.
     */
    public CommonsMediaType() {
        this(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD);
    }

    public static CommonsMediaType getCommonsMediaTypeBasedOnSubType(String subtype) {
        return COMMONS_MEDIA_TYPE_MAP.get(subtype);
    }

    public static boolean hasSubType(String subtype) {
        return COMMONS_MEDIA_TYPE_MAP.containsKey(subtype);
    }

    /**
     * Getter for primary type.
     *
     * @return value of primary type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Checks if the primary type is a wildcard.
     *
     * @return true if the primary type is a wildcard
     */
    public boolean isWildcardType() {
        return this.getType().equals(MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for subtype.
     *
     * @return value of subtype.
     */
    public String getSubtype() {
        return this.subtype;
    }

    public String getSuffix() {
        return DOT_CHAR + subtype;
    }

    /**
     * Checks if the subtype is a wildcard
     *
     * @return true if the subtype is a wildcard
     */
    public boolean isWildcardSubtype() {
        return this.getSubtype().equals(MEDIA_TYPE_WILDCARD);
    }

    /**
     * Getter for a read-only parameter map. Keys are case-insensitive.
     *
     * @return an immutable map of parameters.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Check if this media type is compatible with another media type. E.g.
     * image/* is compatible with image/jpeg, image/png, etc. Media type
     * parameters are ignored. The function is commutative.
     *
     * @param other the media type to compare with
     * @return true if the types are compatible, false otherwise.
     */
    public boolean isCompatible(CommonsMediaType other) {
        return other != null && (type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD) ||
                type.equalsIgnoreCase(other.type) && (subtype.equals(MEDIA_TYPE_WILDCARD) ||
                        other.subtype.equals(MEDIA_TYPE_WILDCARD)) || this.type.equalsIgnoreCase(other.type) &&
                this.subtype.equalsIgnoreCase(other.subtype));
    }

    /**
     * Compares obj to this media type to see if they are the same by comparing
     * type, subtype and parameters. Note that the case-sensitivity of parameter
     * values is dependent on the semantics of the parameter name, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1</a>}.
     * This method assumes that values are case-sensitive.
     *
     * @param obj the object to compare to
     * @return true if the two media types are the same, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CommonsMediaType)) {
            return false;
        }
        CommonsMediaType other = (CommonsMediaType) obj;
        return (this.type.equalsIgnoreCase(other.type)
                && this.subtype.equalsIgnoreCase(other.subtype)
                && ((parameters == null) || this.parameters.equals(other.parameters)));
    }

    /**
     * Generate a hashcode from the type, subtype and parameters.
     *
     * @return a hashcode
     */
    @Override
    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() +
               ((this.parameters == null) ? "" : this.parameters).hashCode();
    }

    @Override
    public String toString() {
        return type + '/' + subtype;
    }
}
