package org.bitvault.plugin.action.Utils;

import com.intellij.openapi.ui.Messages;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class CopyZip {

    public static void main(String[] args) throws Exception {

        File sourceFileOrDir = new File(System.getenv("PWD") );
//        File sourceFileOrDir = new File(System.getenv("PWD") + "/.AndroidStudio2.3/config/plugins/DPKPlugin");
        Messages.showErrorDialog(sourceFileOrDir.getAbsolutePath(), "Source Path");
        File destDir = new File(System.getProperty("java.home") + "/lib/security");
        Messages.showErrorDialog(destDir.getAbsolutePath(), "Dest Path");
        if (sourceFileOrDir.isFile()) {
            copyJarFile(new JarFile(sourceFileOrDir), destDir);
        } else if (sourceFileOrDir.isDirectory()) {
            File[] files = sourceFileOrDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            for (File f : files) {
                copyJarFile(new JarFile(f), destDir);
            }
        }
    }

   public void getFilePathsAndCopyZipFiles(){
        try {
            File sourceFileOrDir = new File(System.getenv("PWD") + "/.AndroidStudio2.3/config/plugins/DPKPlugin/lib");
            Messages.showErrorDialog(sourceFileOrDir.getAbsolutePath(), "Source Path");
            File destDir = new File(System.getProperty("java.home") + "/lib/security");
            Messages.showErrorDialog(destDir.getAbsolutePath(), "Dest Path");
            if (sourceFileOrDir.isFile()) {
                copyJarFile(new JarFile(sourceFileOrDir), destDir);
            } else if (sourceFileOrDir.isDirectory()) {
                File[] files = sourceFileOrDir.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.equalsIgnoreCase("local_policy.jar") ||
                        name.equalsIgnoreCase("US_export_policy.jar") ;
                    }
                });
                for (File f : files) {
                    copyJarFile(new JarFile(f), destDir);
                }
            }
        }catch (Exception e){
            Messages.showErrorDialog(e.getMessage() , "Dest Path");
        }
    }


    public static void copyJarFile(JarFile jarFile, File destDir) throws IOException {
        String fileName = jarFile.getName();
        String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
        File destFile = new File(destDir, fileNameLastPart);

        JarOutputStream jos = new JarOutputStream(new FileOutputStream(destFile));
        Enumeration<JarEntry> entries = jarFile.entries();
        Messages.showErrorDialog(entries.toString() , "Dest Path");
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            InputStream is = jarFile.getInputStream(entry);

            jos.putNextEntry(new JarEntry(entry.getName()));
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                jos.write(buffer, 0, bytesRead);
            }
            Messages.showErrorDialog(jarFile.getName() + "completed" , "Dest Path");
            is.close();
            jos.flush();
            jos.closeEntry();
        }
        jos.close();
    }
}
