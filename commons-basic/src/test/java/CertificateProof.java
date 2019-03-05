//import javax.net.ssl.HttpsURLConnection;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.cert.Certificate;
//
//public class CertificateProof{
//    private static final String CLIENT_KEY_STORE_JKS = "dfECompletionClientKeyStore.jks";
//    private static final String CLIENT_CERT_ALIAS = "dfecompletionclient";
//    private static final String CER_PASSWORD = "LP2275w";
//    private static final String COMPLETION_URL = "https://online-test.bbs.no/archive-completion/datacapture";
//    private static final String USAGE_JAVA_CERTIFICATE_PROOF_KEY_STORE_PATH_KEY_STORE_PASSWORD_KEY_STORE_ALIAS =
//            "Usage: java CertificateProof <key_store_path> <key_store_password> <key_store_alias> <url>";
//    private static final String RUNNING_JAVA_CERTIFICATE_PROOF = "Running: java CertificateProof ";
//    private static final String EMPTY_STRING = " ";
//    private static final String UNABLE_TO_START_CLASS = "Unable to start class. ";
//    private final String urlString;
//
//    // main
//    public static void main(String[] args){
//          new CertificateProof(args);
//    }
//
//    public CertificateProof(String[] args){
//        CompletionCertificate completionCertificate;
//        if (args.length == 0) {
//            System.out.println(RUNNING_JAVA_CERTIFICATE_PROOF + CLIENT_KEY_STORE_JKS + EMPTY_STRING + CER_PASSWORD +
//                               EMPTY_STRING + CLIENT_CERT_ALIAS + EMPTY_STRING + COMPLETION_URL);
//            completionCertificate = new CompletionCertificate(Paths.get(CLIENT_KEY_STORE_JKS), CLIENT_CERT_ALIAS, CER_PASSWORD);
//            this.urlString = COMPLETION_URL;
//        }
//        else if (args.length == 4) {
//            System.out.println(RUNNING_JAVA_CERTIFICATE_PROOF + Paths.get(args[0]).toString() + EMPTY_STRING + args[1] +
//                               EMPTY_STRING + args[2] + EMPTY_STRING + args[3]);
//            completionCertificate = new CompletionCertificate(Paths.get(args[0]), args[2], args[1]);
//            this.urlString = args[3];
//        }
//        else {
//            System.err.println(USAGE_JAVA_CERTIFICATE_PROOF_KEY_STORE_PATH_KEY_STORE_PASSWORD_KEY_STORE_ALIAS);
//            throw new UnsupportedOperationException(UNABLE_TO_START_CLASS +
//                                                    USAGE_JAVA_CERTIFICATE_PROOF_KEY_STORE_PATH_KEY_STORE_PASSWORD_KEY_STORE_ALIAS);
//        }
//        setupSSL(completionCertificate);
//        testConnect();
//    }
//
//    private void testConnect() {
//        URL url;
//        try {
//
//            url = new URL(urlString);
//            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
//
//            //dumpl all cert info
//            printHttpsCert(con);
//
//            //dump all the content
//            printContent(con);
//
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void printHttpsCert(HttpsURLConnection con){
//
//        if(con!=null){
//
//            try {
//
//                System.out.println("Response Code : " + con.getResponseCode());
//                System.out.println("Cipher Suite : " + con.getCipherSuite());
//                System.out.println("\n");
//
//                Certificate[] certs = con.getServerCertificates();
//                for(Certificate cert : certs){
//                    System.out.println("Cert Type : " + cert.getType());
//                    System.out.println("Cert Hash Code : " + cert.hashCode());
//                    System.out.println("Cert Public Key Algorithm : "
//                                       + cert.getPublicKey().getAlgorithm());
//                    System.out.println("Cert Public Key Format : "
//                                       + cert.getPublicKey().getFormat());
//                    System.out.println("\n");
//                }
//
//            }
//            catch (IOException e){
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//    private void printContent(HttpsURLConnection con){
//        if(con!=null){
//
//            try {
//
//                System.out.println("****** Content of the URL ********");
//                BufferedReader br =
//                        new BufferedReader(
//                                new InputStreamReader(con.getInputStream()));
//
//                String input;
//
//                while ((input = br.readLine()) != null){
//                    System.out.println(input);
//                }
//                br.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//
//    private static void setupSSL(CompletionCertificate certificate) {
//        System.out.println("---> " + certificate.getPasswordString());
//        System.setProperty("javax.net.debug", "ssl");
//        System.setProperty("javax.net.ssl.keyStoreType", "jks");
//        System.setProperty("javax.net.ssl.keyStorePassword", (certificate.getPasswordString()));
//        System.setProperty("javax.net.ssl.keyStore", certificate.getKeyStorePath().toString());
////        System.setProperty("javax.net.ssl.trustStorePassword", (certificate.getPasswordString()));
////        System.setProperty("javax.net.ssl.trustStoreType", "jks");
////        System.setProperty("javax.net.ssl.trustStore", certificate.getKeyStorePath().toString());
//    }
//
//    class CompletionCertificate {
//        private final Path keyStorePath;
//        private final String certificateAlias;
//        private final String password;
//
//        CompletionCertificate(Path keyStorePath, String certificateAlias, String password){
//            this.keyStorePath = keyStorePath;
//            this.certificateAlias = certificateAlias;
//            this.password = password;
//        }
//
//        public final String getPasswordString() {
//            return password;
//        }
//
//        public final String getCertificateAlias() {
//            return certificateAlias;
//        }
//
//        public final Path getKeyStorePath() {
//            return keyStorePath;
//        }
//
//    }
//}
