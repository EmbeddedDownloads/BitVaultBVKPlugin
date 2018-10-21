package org.bitvault.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.bitvault.plugin.action.Utils.Constants;
import org.bitvault.plugin.action.Utils.CopySecurityJarsInJDK;
import org.bitvault.plugin.action.Utils.Prefrences;
import org.bitvault.plugin.action.Utils.StringConstants;
import org.bitvault.plugin.action.View.ConversionDialog;
import org.bitvault.plugin.action.View.RestartDialog;
import org.bitvault.plugin.action.interfaces.EventCallback;

import javax.swing.*;


/**
 * Main class from where conversion starts
 */
public class DPKConversionAction extends AnAction implements Constants,StringConstants, EventCallback {

    private ConversionDialog customDialog;
    private AnActionEvent anActionEvent;

    @Override
    public void actionPerformed(final AnActionEvent anActionEvent) {

        this.anActionEvent = anActionEvent;


//        JOptionPane.showMessageDialog(null,Prefrences.getInstance().getValue(Constants.JDK_PATH).equalsIgnoreCase(System.getProperty("java.home") + LIB));

        // Used to CopySecurityJarsInJDK

        if (!Prefrences.getInstance().getValue(Constants.JDK_PATH).equalsIgnoreCase(System.getProperty("java.home") + LIB)) {
            final CopySecurityJarsInJDK copySecurityJarsInJDK = new CopySecurityJarsInJDK();
            if (copySecurityJarsInJDK.getFilePathsAndCopyZipFiles()) {
                final RestartDialog customDialog = new RestartDialog(false);
                customDialog.setTitle(TITLE_RESTART);
                customDialog.setSize(400, 50);
                customDialog.setResizable(true);
                customDialog.show();
            }
        } else {

            customDialog = new ConversionDialog(false, this);
            customDialog.setTitle(TITLE);
            customDialog.setSize(700, 200);
            customDialog.setResizable(true);
            customDialog.show();
        }
    }

    /**
     * Method to open a file chooser and get selected file
     *
     * @param anActionEvent
     * @param extension
     * @return selected file (must be an apk)
     */
    private VirtualFile setFileChooser(final AnActionEvent anActionEvent, final String extension) {

        final Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            return null;
        }

        final FileChooserDescriptor mFileChooserDescriptor = new FileChooserDescriptor(true, false, true, true, true, false);

        // Adding filter for file chooser
        final Condition<VirtualFile> mFilter = virtualFile -> virtualFile != null && virtualFile.getExtension() != null && virtualFile.getExtension().trim().equalsIgnoreCase(extension);

        mFileChooserDescriptor.withFileFilter(mFilter);
        mFileChooserDescriptor.withTreeRootVisible(false);


        return FileChooser.chooseFile(mFileChooserDescriptor, project, null);

    }


    @Override
    public void selectAPKFile() {

        // File Chooser
        final VirtualFile sourceFile = setFileChooser(anActionEvent, APK);

        if (customDialog != null) {
            customDialog.setAPKPath(sourceFile);
        }
    }

    @Override
    public void selectPublicKeyFile() {
        // File Chooser
        final VirtualFile sourceFile = setFileChooser(anActionEvent, BITVAULT);

        if (customDialog != null) {
            customDialog.setPublicKeyPath(sourceFile);
        }
    }


}
