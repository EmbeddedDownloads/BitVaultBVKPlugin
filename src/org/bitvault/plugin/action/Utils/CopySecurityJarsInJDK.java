package org.bitvault.plugin.action.Utils;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;


public class CopySecurityJarsInJDK implements Constants,StringConstants {

    /**
     * Method used to copy jar file
     *
     * @param jarFile -- jarfiles
     * @param destDir -- Destination path
     * @throws IOException
     */
    private static void copyJarFile(final JarFile jarFile, final File destDir) throws IOException {
        final String fileName = jarFile.getName();
        final String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
        final File destFile = new File(destDir, fileNameLastPart);

        final JarOutputStream jos = new JarOutputStream(new FileOutputStream(destFile));
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final InputStream is = jarFile.getInputStream(entry);

            jos.putNextEntry(new JarEntry(entry.getName()));
            final byte[] buffer = new byte[4096];
            int bytesRead ;
            while ((bytesRead = is.read(buffer)) != -1) {
                jos.write(buffer, 0, bytesRead);
            }
            is.close();
            jos.flush();
            jos.closeEntry();
        }
        jos.close();
    }

    public boolean getFilePathsAndCopyZipFiles() {
        try {
//            JOptionPane.showMessageDialog(null,PathManager.getPluginsPath() + PLUGIN_LIB);
//            JOptionPane.showMessageDialog(null,System.getProperty("java.home") + LIB);
            final File sourceFileOrDir = new File(PathManager.getPluginsPath() + PLUGIN_LIB);
            final File destDir = new File(System.getProperty("java.home") + LIB);
            if (sourceFileOrDir.isFile()) {
                copyJarFile(new JarFile(sourceFileOrDir), destDir);
            } else if (sourceFileOrDir.isDirectory()) {
                final File[] files = sourceFileOrDir.listFiles((dir, name) -> name.equalsIgnoreCase(Constants.LOCAL_POLICY) ||
                        name.equalsIgnoreCase(US_POLICY));
                if(files != null) {
                    for (final File f : files) {
                        copyJarFile(new JarFile(f), destDir);
                    }
                    Prefrences.getInstance().setValue(Constants.JDK_PATH, System.getProperty("java.home") + LIB);
                }

            }

//            JOptionPane.showMessageDialog(null,"Copied");
            return true;
        } catch (final Exception e) {
            Prefrences.getInstance().setValue(Constants.JDK_PATH, "");
            Messages.showErrorDialog(e.getMessage(), ERROR);
            return false;
        }
    }
}
