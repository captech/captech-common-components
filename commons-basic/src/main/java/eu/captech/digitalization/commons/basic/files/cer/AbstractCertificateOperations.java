package eu.captech.digitalization.commons.basic.files.cer;

import eu.captech.digitalization.commons.basic.doc.Preamble;
import eu.captech.digitalization.commons.basic.exception.CertificateException;
import eu.captech.digitalization.commons.basic.utils.io.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;
import java.util.Date;

import static eu.captech.digitalization.commons.basic.files.cer.KeyStoreProvider.SUN;
import static eu.captech.digitalization.commons.basic.files.cer.KeyStoreType.JKS;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "8/8/14",
        creationTime = "11:32 AM",
        lastModified = "8/8/14"
)
public abstract class AbstractCertificateOperations {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCertificateOperations.class);

    public abstract char[] getPassword();
    public abstract String getCertificateAlias();
    public abstract Path getKeyStorePath();

    public final X509Certificate getX509Certificate()
            throws IOException, NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException,
                   java.security.cert.CertificateException{
        return getX509Certificate(JKS, SUN);
    }

    public final X509Certificate getX509Certificate(KeyStoreType keyStoreType, KeyStoreProvider keyStoreProvider)
            throws IOException, NoSuchProviderException, KeyStoreException, NoSuchAlgorithmException,
                   java.security.cert.CertificateException{
        FileInputStream inputStream = new FileInputStream(getKeyStorePath().toFile());
        KeyStore keyStore = KeyStore.getInstance(keyStoreType.name(), keyStoreProvider.name());
        keyStore.load(inputStream, getPassword());
        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(getCertificateAlias());
        logger.debug("*** Certificate valid until: " + x509Certificate.getNotAfter());
        return x509Certificate;
    }

    public final boolean isDateValidToday(X509Certificate x509Certificate){
        Date noAfter = getValidityDate(x509Certificate);
        Time cerTime = new Time(noAfter);
        Time dueTime = new Time(new Date());
        logger.debug("************ Certificate valid until: " + cerTime.getDate());
        logger.debug("************ The day today: " + dueTime.getDate());
        return cerTime.isAfter(dueTime);
    }

    protected boolean isDateValidToday(X509Certificate x509Certificate, Date date){
        Date noAfter = getValidityDate(x509Certificate);
        Time cerTime = new Time(noAfter);
        Time dueTime = new Time(date);
        logger.info("************ Certificate valid until: " + cerTime.getDate());
        logger.debug("************ The day today: " + dueTime.getDate());
        return cerTime.isAfter(dueTime);
    }

    public final boolean isDateValid(X509Certificate x509Certificate, Date dueDate, int daysBeforeWarning) throws CertificateException{
        Date noAfter = getValidityDate(x509Certificate);
        Time cerTime = new Time(noAfter);
        Time dueTime = new Time(dueDate);
        dueTime.setDay(dueTime.addDays(daysBeforeWarning));
        logger.info("Certificate valid until: " + cerTime.getDate());
        logger.info("The day today plus " + daysBeforeWarning + " days (warning buffer): " + dueTime.getDate());
        boolean certificateIsValid = cerTime.isAfter(dueTime);
        logger.info("Certificate is valid: " + certificateIsValid);
        if (!certificateIsValid) {
            if (!isDateValidToday(x509Certificate)) {
                throw new CertificateException("The certificate has expired: " + cerTime);
            }
        }
        return certificateIsValid;
    }

    public final Date getValidityDate(X509Certificate x509Certificate) {
        return x509Certificate.getNotAfter();
    }

}
