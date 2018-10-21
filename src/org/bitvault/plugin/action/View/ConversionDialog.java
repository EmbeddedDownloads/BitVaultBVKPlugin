package org.bitvault.plugin.action.View;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import org.bitvault.plugin.action.Utils.*;
import org.bitvault.plugin.action.interfaces.EventCallback;
import org.bitvault.plugin.action.security.EncryptDecryptData;
import org.bitvault.plugin.action.security.SecurityConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.bitvault.plugin.action.Utils.ConversionUtility.createAndSaveDPK;

/**
 * Class used to show custom ui
 */
public class ConversionDialog extends DialogWrapper implements Constants,StringConstants {

    private final EventCallback mEventCallback;
    private JTextField apkActualPathTextArea;
    private JRadioButton radioButtonRelease;
    private JRadioButton radioButtonDebug;
    private JTextField publicKeyActualPathTextArea;
    private JButton selectPublicKeyButton;
    private String sourceFilepath = "";
    private VirtualFile sourceFile;
    private String publicKey = SecurityConstants.BTC_PUBLIC_KEY;


    public ConversionDialog(final boolean canBeParent, final EventCallback mEventCallback) {
        super(canBeParent);
        this.mEventCallback = mEventCallback;
        init();
    }


    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "2000";
    }

    @Override
    protected boolean isCenterStrictedToPreferredSize() {
        return true;
    }

    @NotNull
    @Override
    protected Action[] createActions() {

        final Action[] actions = new Action[2];
        try {

            final Action[] previousAction = super.createActions();

            for (int i = 0; i < 2; i++) {
                actions[i] = previousAction[i];
                if (i == 0) {
                    actions[i].putValue(KEY_NAME, GENERATE);
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return actions;
    }


    @Override
    protected void doOKAction() {
        try {
            if (this.sourceFile != null) {

                if (checkForValidations()) {
                    final byte[] encryptedSourceFile = new EncryptDecryptData().encryptData(sourceFile.getPath(), publicKey);
                    final String targetPath = FilesUtility.getTargetPath(sourceFile);
                    createAndSaveDPK(targetPath, encryptedSourceFile);
                    final int code = Messages.showDialog(DPK_FILE_GENERATED + " " + targetPath, FILE_GENERATED_SUCCESS, new String[]{"OK"}, -1, null);
                    switch (code) {
                        default:
                            dispose();
                            break;
                    }
                }
            } else {
                Messages.showErrorDialog(SELECT_APK_FILE, ERROR);
            }
        } catch (final Exception e) {
            Messages.showErrorDialog("Error is : " + e.toString(), ERROR);
            e.printStackTrace();

            final CopySecurityJarsInJDK copySecurityJarsInJDK = new CopySecurityJarsInJDK();
            if (copySecurityJarsInJDK.getFilePathsAndCopyZipFiles()) {
                final RestartDialog customDialog = new RestartDialog(false);
                customDialog.setTitle(TITLE_RESTART);
                customDialog.setSize(400, 50);
                customDialog.setResizable(true);
                customDialog.show();
            }
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        final JPanel parentPanel = new JPanel();

        showUi(parentPanel);

        return parentPanel;
    }


    /**
     * Method used to show UI for plugin
     * @param parentPanel
     */
    private void showUi(final JPanel parentPanel) {

        // First row

        // Apk path label
        final JLabel apkPathLable = new JLabel();
        apkPathLable.setText(APK_PATH);
        apkPathLable.setForeground(JBColor.BLACK);
        apkPathLable.setFont(new Font("", Font.BOLD, 15));
        apkPathLable.setBounds(20, 20, 100, 25);

        // Text Field
        apkActualPathTextArea = new JTextField();
        apkActualPathTextArea.setBounds(140, 20, 500, 25);

        //Button
        final JButton selectButton = new JButton();
        selectButton.setText(SELECT_BUTTON);
        selectButton.setBounds(645, 18, 30, 30);


        // Second Row

        // Mode label
        final JLabel modeLable = new JLabel();
        modeLable.setText(MODE);
        modeLable.setForeground(JBColor.BLACK);
        modeLable.setFont(new Font("", Font.BOLD, 15));
        modeLable.setBounds(20, 70, 100, 25);

        // radio button
        final ButtonGroup radioGroup = new ButtonGroup();

        radioButtonRelease = new JRadioButton();
        radioButtonRelease.setText(RELEASE_MODE);
        radioButtonRelease.setBounds(470, 70, 100, 25);

        radioButtonDebug = new JRadioButton();
        radioButtonDebug.setText(DEBUG_MODE);
        radioButtonDebug.setBounds(590, 70, 100, 25);

        radioGroup.add(radioButtonDebug);
        radioGroup.add(radioButtonRelease);


        // Third row

        // publicKey label
        final JLabel publicKey = new JLabel();
        publicKey.setText(PUBLIC_KEY);
        publicKey.setForeground(JBColor.BLACK);
        publicKey.setFont(new Font("", Font.BOLD, 15));
        publicKey.setBounds(20, 120, 100, 25);

        // Text Field
        publicKeyActualPathTextArea = new JTextField();
        publicKeyActualPathTextArea.setBounds(140, 120, 500, 25);

        //Button
        selectPublicKeyButton = new JButton();
        selectPublicKeyButton.setText(SELECT_BUTTON);
        selectPublicKeyButton.setBounds(645, 118, 30, 30);

        // adding all components in main frame
        parentPanel.add(apkPathLable);
        parentPanel.add(apkActualPathTextArea);
        parentPanel.add(selectButton);

        parentPanel.add(modeLable);
        parentPanel.add(radioButtonRelease);
        parentPanel.add(radioButtonDebug);

        parentPanel.add(publicKey);
        parentPanel.add(publicKeyActualPathTextArea);
        parentPanel.add(selectPublicKeyButton);

        parentPanel.setSize(700, 200);
        parentPanel.setPreferredSize(new Dimension(700,200));
        parentPanel.setLayout(null);
        parentPanel.setVisible(true);

        publicKeyActualPathTextArea.setEnabled(false);
        selectPublicKeyButton.setEnabled(false);

        selectButton.addActionListener(e -> mEventCallback.selectAPKFile());

        selectPublicKeyButton.addActionListener(e -> mEventCallback.selectPublicKeyFile());


        radioButtonRelease.addChangeListener(e -> {

            if (radioButtonRelease.isSelected()) {
                publicKeyActualPathTextArea.setEnabled(true);
                selectPublicKeyButton.setEnabled(true);
            } else {
                publicKeyActualPathTextArea.setEnabled(false);
                selectPublicKeyButton.setEnabled(false);
            }
        });
    }


    /**
     * Method used to set APK path
     * @param sourceFile -- Selected File
     */
    public void setAPKPath(final VirtualFile sourceFile) {
        this.sourceFile = sourceFile;
        sourceFilepath = sourceFile.getPath();
        apkActualPathTextArea.setText(sourceFilepath);
        checkWhetherApkIsSignedOrUnsigned(sourceFilepath);
    }

    /**
     * Method to used to set Public Key
     * @param sourceFile -- Public key file
     */
    public void setPublicKeyPath(final VirtualFile sourceFile) {
        publicKeyActualPathTextArea.setText(sourceFile.getPath());
        try {
            final BufferedReader in = new BufferedReader(new FileReader(sourceFile.getPath()));
            String str;
            while ((str = in.readLine()) != null)
                publicKey = str;
            in.close();
        } catch (final IOException ignored) {

        }
    }

    /**
     * Method used to check whether selected apk is signed or not
     * @param filePath
     */
    private void checkWhetherApkIsSignedOrUnsigned(final String filePath) {

        sourceFilepath = filePath;
        final APKparser apkParser = new APKparser();

        if (APKparser.getAPKDetail(filePath)) {
            radioButtonRelease.setSelected(true);
            radioButtonRelease.setEnabled(true);
        } else {
            radioButtonRelease.setEnabled(false);
            radioButtonDebug.setSelected(true);
            publicKeyActualPathTextArea.setEnabled(false);
            selectPublicKeyButton.setEnabled(false);
        }
    }


    /**
     * Check for validations
     * @return - true in case all ok, false in case of any error with error message
     */
    private boolean checkForValidations() {


        if (radioButtonRelease.isSelected()) {

            // Validations for APK file

            if (apkActualPathTextArea.getText().equals("")) {
                Messages.showErrorDialog(SELECT_APK_FILE, ERROR);
                return false;
            } else {
                final File selectedFile = new File(apkActualPathTextArea.getText());
                if (!selectedFile.exists()) {
                    Messages.showErrorDialog(FILE_NOT_EXIST, ERROR);
                    return false;
                } else if (!FilesUtility.getFileExtension(selectedFile).equalsIgnoreCase(APK_EXTENSION)) {
                    Messages.showErrorDialog(INVALID_APK_FILE, ERROR);
                    return false;
                }
            }

            // Validations for Key file

            if (publicKeyActualPathTextArea.getText().equals("")) {
                Messages.showErrorDialog(SELECT_BITVAULT_FILE, ERROR);
                return false;
            } else {
                final File selectedFile = new File(publicKeyActualPathTextArea.getText());
                if (!selectedFile.exists()) {
                    Messages.showErrorDialog(FILE_NOT_EXIST, ERROR);
                    return false;
                } else if (!FilesUtility.getFileExtension(selectedFile).equalsIgnoreCase(BITVAULT_EXTENSION)) {
                    Messages.showErrorDialog(INVALID__BITVAULT_FILE, ERROR);
                    return false;
                }
            }


        } else {

            if (apkActualPathTextArea.getText().equals("")) {
                Messages.showErrorDialog(SELECT_APK_FILE, ERROR);
                return false;
            } else {
                final File selectedFile = new File(apkActualPathTextArea.getText());
                if (!selectedFile.exists()) {
                    Messages.showErrorDialog(FILE_NOT_EXIST, ERROR);
                    return false;
                } else if (!FilesUtility.getFileExtension(selectedFile).equalsIgnoreCase(APK_EXTENSION)) {
                    Messages.showErrorDialog(INVALID_APK_FILE, ERROR);
                    return false;
                }

            }
        }
        return true;
    }
}
