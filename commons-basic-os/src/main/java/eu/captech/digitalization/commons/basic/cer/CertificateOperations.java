package eu.captech.digitalization.commons.basic.cer;

import eu.captech.digitalization.commons.basic.api.IOperations;
import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.CertificateException;
import eu.captech.digitalization.commons.basic.exceptions.CertificateRuntimeException;
import eu.captech.digitalization.commons.basic.exceptions.OperationRuntimeException;
import eu.captech.digitalization.commons.basic.files.cer.AbstractCertificateOperations;
import eu.captech.digitalization.commons.basic.files.cer.KeyStoreProvider;
import eu.captech.digitalization.commons.basic.files.cer.KeyStoreType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import static eu.captech.digitalization.commons.basic.files.cer.KeyStoreProvider.SUN;
import static eu.captech.digitalization.commons.basic.files.cer.KeyStoreType.JKS;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "8/8/14",
        creationTime = "1:25 PM",
        lastModified = "8/8/14"
)
public class CertificateOperations extends AbstractCertificateOperations implements IOperations{

    private static final Logger logger = LoggerFactory.getLogger(CertificateOperations.class);
    public static final String KEY_STORE_OPTION = "-keystore";
    public static final String STORE_TYPE_OPTION = "-storetype";
    public static final String PROVIDER_NAME_OPTION = "-providerName";
    public static final String STORE_PASS_OPTION = "-storepass";
    public static final String CER_ALIAS_OPTION = "-alias";
    private static final String KEYTOOL_PATTERN_FORM = OPERATIONS_PROTOCOL_KEYTOOL + SPACE_STRING + KEY_STORE_OPTION +
                                                       SPACE_STRING + "<keystore_path>" + SPACE_STRING + STORE_TYPE_OPTION +
                                                       SPACE_STRING + "<store_type>" + SPACE_STRING + PROVIDER_NAME_OPTION +
                                                       SPACE_STRING + "<provider_name>" + SPACE_STRING + STORE_PASS_OPTION +
                                                       SPACE_STRING + "<store_password>" + SPACE_STRING + CER_ALIAS_OPTION +
                                                       SPACE_STRING + "<cer_alias>";
    private static final int KEYTOOL_PATTERN_LENGTH = 11;
    private final static Pattern KEY_STORE_PATTERN = Pattern.compile(".+");
    private final static Pattern STORE_TYPE_PATTERN = Pattern.compile(JKS.name());
    private final static Pattern PROVIDER_NAME_PATTERN = Pattern.compile(SUN.name());
    private final static Pattern STORE_PASS_PATTERN = Pattern.compile(".+");
    private final static Pattern CER_ALIAS_PATTERN = Pattern.compile("[\\w]+");
    private final static HashMap<String, Pattern> patternOptions = new HashMap<>();

    static {
        patternOptions.put(KEY_STORE_OPTION, KEY_STORE_PATTERN);
        patternOptions.put(STORE_TYPE_OPTION, STORE_TYPE_PATTERN);
        patternOptions.put(PROVIDER_NAME_OPTION, PROVIDER_NAME_PATTERN);
        patternOptions.put(STORE_PASS_OPTION, STORE_PASS_PATTERN);
        patternOptions.put(CER_ALIAS_OPTION, CER_ALIAS_PATTERN);
    }

    public static String checkProtocolPatternsCorrectness(String evaluate) {
        return (new CertificateOperations()).checkProtocolPatterns(evaluate);
    }

    private String initialParameter = OPERATIONS_PROTOCOL_KEYTOOL;
    private String protocol = OPERATIONS_PROTOCOL_KEYTOOL;
    private Path keyStore;
    private String storePass;
    private String cerAlias;
    private KeyStoreType storeType;
    private KeyStoreProvider storeProvider;

    private CertificateOperations() {
        printOperation();
    }

    public CertificateOperations(String initialParameter) {
        this.initialParameter = initialParameter;
        String errorMessage;
        if (!(errorMessage = checkProtocolPatterns(this.initialParameter)).isEmpty()) {
            throw new OperationRuntimeException("Unable to parse Keytool constructing parameter. Passed parameter is '" +
                    initialParameter + "'. Error Message: " + errorMessage);
        }
        String[] parts = initialParameter.split(SPLIT_STRING);
        int i = 0;
        if (parts[i].equals(OPERATIONS_PROTOCOL_KEYTOOL)) {
            this.protocol = OPERATIONS_PROTOCOL_KEYTOOL;
            i++;
            try {
                setupOptions(parts, i);
                i += 2;
                setupOptions(parts, i);
                i += 2;
                setupOptions(parts, i);
                i += 2;
                setupOptions(parts, i);
                i += 2;
                setupOptions(parts, i);
            }
            catch (Exception e) {
                throw new OperationRuntimeException("Unable to parse Keytool constructing parameter. Passed parameter is '" +
                        initialParameter + "'. The parameter should have following entries: " + KEYTOOL_PATTERN_FORM + ". Possible Error: " + e.getMessage(), e);
            }
        }
        else {
            throw new OperationRuntimeException("Unable to parse Keytool constructing parameter. Passed parameter is '" +
                    initialParameter + "'. The parameter should have following entries: " + KEYTOOL_PATTERN_FORM);
        }
        printOperation();
    }

    private void setupOptions(String[] parts, int i) throws CertificateException {
        String option = parts[i];
        String p;
        switch (option) {
            case KEY_STORE_OPTION:
                Path pf = Paths.get(parts[i + 1]);
                if (Files.exists(pf) && Files.isReadable(pf)) {
                    keyStore = pf;
                }
                else {
                    throw new CertificateException("Error defining mandatory Key Store Path. The file doesn't exists or is not readable: " +
                            pf.toAbsolutePath());
                }
                break;
            case STORE_TYPE_OPTION:
                p = parts[i + 1];
                KeyStoreType keyStoreType;
                if ((keyStoreType = KeyStoreType.valueOf(p)) != null) {
                    storeType = keyStoreType;
                }
                else {
                    throw new CertificateException("Error defining mandatory Key Store Type: " + p);
                }
                break;
            case PROVIDER_NAME_OPTION:
                p = parts[i + 1];
                KeyStoreProvider keyStoreProvider ;
                if ((keyStoreProvider = KeyStoreProvider.valueOf(p)) != null) {
                    storeProvider = keyStoreProvider;
                }
                else {
                    throw new CertificateException("Error defining mandatory Key Store Provider: " + p);
                }
                break;
            case STORE_PASS_OPTION:
                storePass = parts[i + 1];
                break;
            case CER_ALIAS_OPTION:
                cerAlias = parts[i + 1];
                break;
            default:
                throw new CertificateException("Error defining mandatory options. Option not recognized: " + option);
        }
    }

    @Override
    public String getInitialParameter() {
        return initialParameter;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public final boolean certificateIsValid(@Nullable String warningDays) {
         return isConnected(warningDays);
    }

    @Override
    public final boolean isConnected(@Nullable String warningDays) {
        X509Certificate x509Certificate;
        try {
            x509Certificate = getX509Certificate(storeType, storeProvider);
        }
        catch (java.security.cert.CertificateException | IOException | NoSuchProviderException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new CertificateRuntimeException("Unable to open certificate at " + keyStore + ": " + e.getMessage(), e);
        }
        try {
            return isDateValid(x509Certificate, new Date(), Integer.parseInt(warningDays));
        }
        catch (NumberFormatException e) {
            throw new CertificateRuntimeException("Error defining mandatory warning days. Warning days is not a numeric character: " + warningDays, e);
        }
        catch(CertificateException e) {
            throw new CertificateRuntimeException("Certificate Validation Error: " + e.getMessage(), e);
        }
    }

    @Override
    public final String checkProtocolPatterns(String evaluate) {
        if (logger.isInfoEnabled()) {
            logger.info("Starting method " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - evaluate: " + evaluate);
        }
        if (evaluate.startsWith(OPERATIONS_PROTOCOL_KEYTOOL)) {
            if (evaluate.contains(KEY_STORE_OPTION)) {
                if (evaluate.contains(STORE_TYPE_OPTION)) {
                    if (evaluate.contains(PROVIDER_NAME_OPTION)) {
                        if (evaluate.contains(STORE_PASS_OPTION)) {
                            if (evaluate.contains(CER_ALIAS_OPTION)) {
                                    int i = 1;
                                    String[] sp = evaluate.split(SPLIT_STRING);
                                    if (sp.length != KEYTOOL_PATTERN_LENGTH) {
                                        return "Not valid Keytool pattern. Valid Keytool pattern has the form " + KEYTOOL_PATTERN_FORM;
                                    }
                                    int nextInt1;
                                    int nextInt2;
                                    if (checkOption(sp[(nextInt1 = getNextValidValue(i, sp))], sp[(nextInt2 = getNextValidValue(nextInt1 + 1, sp))])) {
                                        if (checkOption(sp[(nextInt1 = getNextValidValue(nextInt2 + 1, sp))], sp[(nextInt2 = getNextValidValue(nextInt1 + 1, sp))])) {
                                            if (checkOption(sp[(nextInt1 = getNextValidValue(nextInt2 + 1, sp))], sp[(nextInt2 = getNextValidValue(nextInt1 + 1, sp))])) {
                                                if (checkOption(sp[(nextInt1 = getNextValidValue(nextInt2 + 1, sp))], sp[(nextInt2 = getNextValidValue(nextInt1 + 1, sp))])) {
                                                    if (checkOption(sp[(nextInt1 = getNextValidValue(nextInt2 + 1, sp))], sp[(nextInt2 = getNextValidValue(nextInt1 + 1, sp))])) {
                                                        return EMPTY_STRING;
                                                    }
                                                    else {
                                                        return "Invalid option (" + sp[nextInt1] + "): '" + sp[nextInt2] + "'. Regex for the option is " + patternOptions.get(sp[nextInt1]);
                                                    }
                                                }
                                                else {
                                                    return "Invalid option (" + sp[nextInt1] + "): '" + sp[nextInt2] + "'. Regex for the option is " + patternOptions.get(sp[nextInt1]);
                                                }
                                            }
                                            else {
                                                return "Invalid option (" + sp[nextInt1] + "): '" + sp[nextInt2] + "'. Regex for the option is " + patternOptions.get(sp[nextInt1]);
                                            }
                                        }
                                        else {
                                            return "Invalid option (" + sp[nextInt1] + "): '" + sp[nextInt2] + "'. Regex for the option is " + patternOptions.get(sp[nextInt1]);
                                        }
                                    }
                                    else {
                                        return "Invalid option (" + sp[nextInt1] + "): '" + sp[nextInt2] + "'. Regex for the option is " + patternOptions.get(sp[nextInt1]);
                                    }
//                                }
//                                else {
//                                    return "Keytool pattern must contain a '" + WARNING_DAYS_OPTION + "' keyword followed by the option (warning days before the certificate expires): " + KEYTOOL_PATTERN_FORM;
//                                }
                            }
                            else {
                                return "Keytool pattern must contain a '" + CER_ALIAS_OPTION + "' keyword followed by the option (the certificate alias): " + KEYTOOL_PATTERN_FORM;
                            }
                        }
                        else {
                            return "Keytool pattern must contain a '" + STORE_PASS_OPTION + "' keyword followed by the option (key store password): " + KEYTOOL_PATTERN_FORM;
                        }
                    }
                    else {
                        return "Keytool pattern must contain a '" + PROVIDER_NAME_OPTION + "' keyword followed by the option (key store provider name): " + KEYTOOL_PATTERN_FORM;
                    }
                }
                else {
                    return "Keytool pattern must contain a '" + STORE_TYPE_OPTION + "' keyword followed by the option (key store type): " + KEYTOOL_PATTERN_FORM;
                }
            }
            else {
                return "Keytool pattern must contain a '" + KEY_STORE_OPTION + "' keyword followed by the option (path of the key store file): " + KEYTOOL_PATTERN_FORM;
            }

        }
        else {
            return "Keytool pattern must contain the keyword '" + OPERATIONS_PROTOCOL_KEYTOOL + "' at the start of the command: " + KEYTOOL_PATTERN_FORM;
        }
    }

    @Override
    public final String getOperationPatternForm() {
        return KEYTOOL_PATTERN_FORM;
    }

    @Override
    public final String getOperationsInfo(@Nullable String parameter) {
        return null;
    }

    @Override
    public final int compareTo(@NotNull IOperations iOperations) {
        return getInitialParameter().compareTo(iOperations.getInitialParameter());
    }

    private boolean checkOption(String key, String value) {
        return patternOptions.get(key).matcher(value).matches();
    }

    protected final void printOperation() {
        if (logger.isInfoEnabled()) {
            logger.info("Operation: " + this.getClass().getSimpleName() + "\tInitialParameter: " + initialParameter);
        }
    }

    @Override
    public final char[] getPassword() {
        return storePass.toCharArray();
    }

    @Override
    public final String getCertificateAlias() {
        return cerAlias;
    }

    @Override
    public final Path getKeyStorePath() {
        return keyStore;
    }
}
