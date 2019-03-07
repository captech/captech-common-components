package eu.captech.digitalization.commons.basic.cer;

import eu.captech.digitalization.commons.basic.BasicOsCommonsTest;
import eu.captech.digitalization.commons.basic.api.IOperations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.captech.digitalization.commons.basic.api.IOperations.EMPTY_STRING;
import static eu.captech.digitalization.commons.basic.api.IOperations.OPERATIONS_PROTOCOL_KEYTOOL;
import static eu.captech.digitalization.commons.basic.cer.CertificateOperations.CER_ALIAS_OPTION;
import static eu.captech.digitalization.commons.basic.cer.CertificateOperations.KEY_STORE_OPTION;
import static eu.captech.digitalization.commons.basic.cer.CertificateOperations.PROVIDER_NAME_OPTION;
import static eu.captech.digitalization.commons.basic.cer.CertificateOperations.STORE_PASS_OPTION;
import static eu.captech.digitalization.commons.basic.cer.CertificateOperations.STORE_TYPE_OPTION;
import static eu.captech.digitalization.commons.basic.files.cer.KeyStoreProvider.SUN;
import static eu.captech.digitalization.commons.basic.files.cer.KeyStoreType.JKS;

public class CertificateOperationsTest extends BasicOsCommonsTest {
    private static final String DF_TELLER_CLIENT_KEY_STORE_JKS = "dfTellerClientKeyStore.jks";
    private static final String DFTELLERCLIENT_CERT_ALIAS = "dftellerclient";
    private static final String CER_PASSWORD = "LP2275w";
    private static final String CER_JKS = "cer/jks";
    private String initialParameter;
    private IOperations operations;

    @Before
    public void setUp() throws Exception {
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
        Path keyStore = Paths.get(testResources.toString(), CER_JKS, DF_TELLER_CLIENT_KEY_STORE_JKS);
        initialParameter = OPERATIONS_PROTOCOL_KEYTOOL + SPACE_CHAR + KEY_STORE_OPTION +
                SPACE_CHAR + keyStore.toString() + SPACE_CHAR + STORE_TYPE_OPTION + SPACE_CHAR + JKS +
                SPACE_CHAR + PROVIDER_NAME_OPTION + SPACE_CHAR + SUN + SPACE_CHAR + STORE_PASS_OPTION +
                SPACE_CHAR + CER_PASSWORD + SPACE_CHAR + CER_ALIAS_OPTION + SPACE_CHAR + DFTELLERCLIENT_CERT_ALIAS;
        operations = new CertificateOperations(initialParameter);
        if (logger.isInfoEnabled()) {
            logger.info("SftpOperations created: " + operations.toString());
        }
    }

    @Test
    public void certificateIsValid() {
        logger.info("\t***** \tStarting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        Assert.assertTrue("The certificate should be valid (or we are close to the end of this certificate validity)",
                ((CertificateOperations)operations).certificateIsValid("30"));
        logger.info("\t***** \tEnding method " + Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Test
    public void validateCertificate() {
        logger.info("\t***** \tStarting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        checkInitialParameter();
        Assert.assertTrue("The certificate should be valid (or we are close to the end of this certificate validity)",
                          operations.isConnected("30"));
        logger.info("\t***** \tEnding method " + Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Test
    public void checkInitialParameter() {
        logger.info("\t***** \tStarting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        logger.info("Initial Parameter: " + operations.getInitialParameter());
        String result = operations.checkProtocolPatterns(operations.getInitialParameter());
        Assert.assertEquals("Result should be an empty string but was: " + result, result, EMPTY_STRING);
        logger.info("\t***** \tEnding method " + Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @Test
    public void checkStaticInitialParameter() {
        logger.info("\t***** \tStarting method " + Thread.currentThread().getStackTrace()[1].getMethodName());
        logger.info("Initial Parameter: " + initialParameter);
        String result = CertificateOperations.checkProtocolPatternsCorrectness(initialParameter);
        Assert.assertEquals("Result should be an empty string but was: " + result, result, EMPTY_STRING);
        logger.info("\t***** \tEnding method " + Thread.currentThread().getStackTrace()[1].getMethodName());
    }

    @After
    public void tearDown () {
        super.postMethodSetup();
    }

}