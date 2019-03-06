package eu.captech.digitalization.commons.basic.files.cer;

import eu.captech.digitalization.commons.basic.BasicCommonsTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbstractCertificateOperationsTest extends BasicCommonsTest {
    private static final String CER_DATE = "06-06-2020";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String CER_JKS = "cer/jks";
    private AbstractCertificateOperations tellerCertificate;
    private AbstractCertificateOperations completionCertificate;


    @Before
    public void setUp() throws Exception {
        logger = getLoggerFor(this.getClass());
        super.preMethodSetup();
        logger = getLoggerFor(this.getClass());
        tellerCertificate = new TellerCertificate();
        completionCertificate  = new CompletionCertificate();
    }

    @Test
    public void getPassword() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            Assert.assertEquals("Password mismatch.", 7, tellerCertificate.getPassword().length);
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void getX509Certificate() throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
        X509Certificate x509Certificate = tellerCertificate.getX509Certificate(KeyStoreType.JKS, KeyStoreProvider.SUN);
        logger.info("--> " + new Date(x509Certificate.getNotAfter().getTime()));
        Assert.assertTrue("The certificate is not valid. Should be valid until (at least): Mon Jun 06 00:00:00 CEST 2016",
                          x509Certificate.getNotAfter().getTime() >= 1465164000000L);
        logger.info("*** Certificate valid until: " + x509Certificate.getNotAfter());
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    public void isDateValid() throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException,
                                     KeyStoreException, IOException, ParseException, eu.captech.digitalization.commons.basic.exception.CertificateException{
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            X509Certificate x509Certificate = tellerCertificate.getX509Certificate();
            Assert.assertTrue("Certificate is obsolete.", tellerCertificate.isDateValid(x509Certificate, new Date(), 0));
            Assert.assertTrue("Certificate is obsolete.", tellerCertificate.isDateValid(x509Certificate, new Date(), 15));
            Assert.assertFalse("Certificate suppose to be obsolete.", tellerCertificate.isDateValid(x509Certificate, DATE_FORMAT.parse(CER_DATE), 15));
            Assert.assertFalse("Certificate suppose to be obsolete.", tellerCertificate.isDateValid(x509Certificate, DATE_FORMAT.parse(CER_DATE), 0));
            Assert.assertTrue("Certificate suppose to be valid.", tellerCertificate.isDateValidToday(x509Certificate));
            Assert.assertFalse("Certificate suppose to be valid.", tellerCertificate.isDateValidToday(x509Certificate,
                    tellerCertificate.getValidityDate(x509Certificate)));
        }
        finally {
            finishingTestMethod(method);
        }
    }

    @Test
    @Ignore("Integration Test")
    public void testConnect() {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            startingTestMethod(method);
            String https_url = "https://online-test.bbs.no/archive-completion/datacapture";
            setupSSL();
            URL url;
            try {

                url = new URL(https_url);
                HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

                //dumpl all cert info
                print_https_cert(con);

                //dump all the content
                print_content(con);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        finally {
            finishingTestMethod(method);
        }
    }


    @Override
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    @After
    public void tearDown() {
        super.postMethodSetup();
    }

    private void setupSSL() {
        System.setProperty("javax.net.debug", "ssl");
        System.setProperty("javax.net.ssl.keyStoreType", "jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "LP2275w");
        System.setProperty("javax.net.ssl.keyStore", completionCertificate.getKeyStorePath().toString());
        System.setProperty("javax.net.ssl.trustStorePassword", "LP2275w");
        System.setProperty("javax.net.ssl.trustStoreType", "jks");
        System.setProperty("javax.net.ssl.trustStore", completionCertificate.getKeyStorePath().toString());
    }

    private void print_https_cert(HttpsURLConnection con){

        if(con!=null){

            try {

                logger.info("Response Code : " + con.getResponseCode());
                logger.info("Cipher Suite : " + con.getCipherSuite());
                logger.info("\n");

                Certificate[] certs = con.getServerCertificates();
                for(Certificate cert : certs){
                    logger.info("Cert Type : " + cert.getType());
                    logger.info("Cert Hash Code : " + cert.hashCode());
                    logger.info("Cert Public Key Algorithm : "
                                       + cert.getPublicKey().getAlgorithm());
                    logger.info("Cert Public Key Format : "
                                       + cert.getPublicKey().getFormat());
                    logger.info("\n");
                }

            }
            catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    private void print_content(HttpsURLConnection con){
        if(con!=null){

            try {

                logger.info("****** Content of the URL ********");
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;

                while ((input = br.readLine()) != null){
                    logger.info(input);
                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    class CompletionCertificate extends  AbstractCertificateOperations{
        private static final String CLIENT_KEY_STORE_JKS = "dfECompletionClientKeyStore.jks";
        private static final String CLIENT_CERT_ALIAS = "dfecompletionclient";
        private static final String CER_PASSWORD = "LP2275w";

        public final char[] getPassword() {
            return CER_PASSWORD.toCharArray();
        }

        @Override
        public final String getCertificateAlias() {
            return CLIENT_CERT_ALIAS;
        }

        @Override
        public final Path getKeyStorePath() {
            return Paths.get(testResources.toString(), CER_JKS, CLIENT_KEY_STORE_JKS);
        }
    }

    class TellerCertificate extends  AbstractCertificateOperations{
        private static final String DF_TELLER_CLIENT_KEY_STORE_JKS = "dfTellerClientKeyStore.jks";
        private static final String DFTELLERCLIENT_CERT_ALIAS = "dftellerclient";
        private static final String CER_PASSWORD = "LP2275w";

        public final char[] getPassword() {
            return CER_PASSWORD.toCharArray();
        }

        @Override
        public final String getCertificateAlias() {
            return DFTELLERCLIENT_CERT_ALIAS;
        }

        @Override
        public final Path getKeyStorePath() {
            return Paths.get(testResources.toString(), CER_JKS, DF_TELLER_CLIENT_KEY_STORE_JKS);
        }
    }
}