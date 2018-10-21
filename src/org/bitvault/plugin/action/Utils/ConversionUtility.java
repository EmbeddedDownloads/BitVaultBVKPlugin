package org.bitvault.plugin.action.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Class responsible for the conversion of APK Into DPK
 */
public class ConversionUtility {

    /**
     * Method is used to create and save DPK at the target path
     * from encrypted byte array
     *
     * @param targetPath
     * @param encryptedFile
     */
    public static void createAndSaveDPK(final String targetPath, final byte[] encryptedFile) {
        try {
            final File fileTarget = new File(targetPath);

            if(!fileTarget.exists()) {
                fileTarget.createNewFile();
            }
            FileUtils.writeByteArrayToFile(fileTarget, encryptedFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }
}
