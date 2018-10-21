package org.bitvault.plugin.action.Utils;

import com.intellij.openapi.vfs.VirtualFile;
import org.bitvault.plugin.action.security.SecurityConstants;

import java.io.File;
import java.util.Objects;

/**
 * Class responsible for all files related operation.
 */
public class FilesUtility implements SecurityConstants, Constants {

    /**
     * get the parent path of the file
     *
     * @param virtualFile Selected File
     * @return parent path of the file
     */
    public static String getFileParent(final VirtualFile virtualFile) {
        if (virtualFile != null) {
            return virtualFile.getParent().getPath();
        }
        return null;
    }

    /**
     * Method to get file name
     *
     * @param virtualFile
     * @return
     */
    public static String getFileName(final VirtualFile virtualFile) {
        if (virtualFile != null) {
            return virtualFile.getName();
        }
        return null;
    }

    /**
     * Method to get the target path
     *
     * @param sourceFile
     * @return
     */
    public static String getTargetPath(final VirtualFile sourceFile) {
        if (Objects.equals(sourceFile.getExtension(), SOURCE_FILE_EXTENSION)) {
            final String fileName = sourceFile.getNameWithoutExtension();
            return sourceFile.getParent().getPath() + File.separator + fileName + "."
                    + DEST_FILE_EXTENSION;
        }
        return null;
    }


    /**
     * Method to get the target path
     *
     * @param sourceFile
     * @return
     */
    public static String getFileExtension(final File sourceFile) {

        try {
            return sourceFile.getPath().substring(sourceFile.getPath().lastIndexOf("."));
        } catch (final Exception e) {
            return "";
        }

    }

}
