package org.bitvault.plugin.action.Utils;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class used to get information from the apk
 */

public class APKparser implements Constants{

    public static boolean getAPKDetail(final String filePath) {
        try {

           return checkWhetherSignedOrUnsigned(filePath);

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method used to check whether apk is signed or unsigned
     * @param filePath
     * @return
     */
    private static boolean checkWhetherSignedOrUnsigned(final String filePath) {

        final Enumeration entries;
        byte[] readBuffer = null;

        readBuffer = new byte[8192];

        try {
            final JarFile jarFile = new JarFile(filePath);

            entries = jarFile.entries();

            if (entries.hasMoreElements()) {

                final JarEntry jarEntry = (JarEntry) entries.nextElement();

                return loadCertificates(jarFile, jarEntry, readBuffer);

            }

            jarFile.close();

        } catch (final IOException | RuntimeException e) {
            System.err.println("Exception reading " + filePath + "\n" + e);
        }
        return false;
    }


    /**
     * Method used to load certificates
     * @param jarFile
     * @param jarEntry
     * @param readBuffer
     * @return
     */
    private static boolean loadCertificates(final JarFile jarFile, final JarEntry jarEntry, final byte[] readBuffer) {

        try {

            final InputStream inputStream = jarFile.getInputStream(jarEntry);

            while (inputStream.read(readBuffer, 0, readBuffer.length) != -1) {

            }

            return isSignedAPK(jarEntry);

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Method to decide whether apk is signed or not on the basis of CN Name
     * @param jarEntry
     * @return
     */
    private static boolean isSignedAPK(final JarEntry jarEntry) {
        try {
            String cnName = "";

            if (jarEntry != null && jarEntry.getCertificates() != null && jarEntry.getCertificates().length > 0) {

                final X509Certificate certificate = (X509Certificate) jarEntry.getCertificates()[0];
                final X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();

                if (x500name.getRDNs(BCStyle.CN) != null && x500name.getRDNs(BCStyle.CN).length > 0) {
                    final RDN rdn = x500name.getRDNs(BCStyle.CN)[0];
                    cnName = IETFUtils.valueToString(rdn.getFirst().getValue());
                }

                return !cnName.equalsIgnoreCase(ANDROID_DEBUG);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false ;
    }

}