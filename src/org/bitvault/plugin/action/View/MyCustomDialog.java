package org.bitvault.plugin.action.View;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.bitvault.plugin.action.Utils.APKparser;
import org.bitvault.plugin.action.Utils.Constants;
import org.bitvault.plugin.action.Utils.FilesUtility;
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
import java.awt.event.ComponentAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.bitvault.plugin.action.Utils.ConversionUtility.createAndSaveDPK;

/**
 * Class used to show custom ui
 */
public class MyCustomDialog extends DialogWrapper implements Constants {

    private EventCallback mEventCallback;
    private JTextField apkActualPathTextArea;
    private JRadioButton radioButtonRelease;
    private JRadioButton radioButtonDebug;
    private JTextField publicKeyActualPathTextArea;
    private JButton selectPublicKeyButton;
    private String sourceFilepath = "";
    private VirtualFile sourceFile;
    private String publicKey = SecurityConstants.BTC_PUBLIC_KEY;

    public MyCustomDialog(boolean canBeParent) {
        super(canBeParent);
        init();
    }

    public MyCustomDialog(boolean canBeParent, EventCallback mEventCallback) {
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

        Action[] actions = null;
        try {
            actions = new Action[2];
            Action[] previousAction = super.createActions();

            if (previousAction != null) {
                for (int i = 0; i < 2; i++) {
                    actions[i] = previousAction[i];
                    if (i == 0) {
                        actions[i].putValue(NAME, GENERATE);
                    }
                }
            }
        } catch (Exception e) {
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
                    String targetPath = FilesUtility.getTargetPath(sourceFile);
                    createAndSaveDPK(targetPath, encryptedSourceFile);
                    int code = Messages.showDialog(DPK_FILE_GENERATED + " " + targetPath, FILE_GENERATED_SUCCESS, new String[]{"OK"}, -1, null);
                    switch (code) {
                        default:
                            dispose();
                            break;
                    }
                }
            } else {
                Messages.showErrorDialog(SELECT_APK_FILE, ERROR);
            }
        } catch (Exception e) {
            Messages.showErrorDialog("Error is : " + e.toString(), ERROR);
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        JPanel parentPanel = new JPanel();

        showUi(parentPanel);

        return parentPanel;
    }


    public void showUi(JPanel parentPanel) {

        // First row

        // Apk path label
        JLabel apkPathLable = new JLabel();
        apkPathLable.setText(APK_PATH);
        apkPathLable.setForeground(Color.BLACK);
        apkPathLable.setFont(new Font("", Font.BOLD, 15));
        apkPathLable.setBounds(20, 20, 100, 25);

        // Text Field
        apkActualPathTextArea = new JTextField();
        apkActualPathTextArea.setBounds(140, 20, 500, 25);

        //Button
        JButton selectButton = new JButton();
        selectButton.setText(SELECT_BUTTON);
        selectButton.setBounds(645, 18, 30, 30);


        // Second Row

        // Mode label
        JLabel modeLable = new JLabel();
        modeLable.setText(MODE);
        modeLable.setForeground(Color.BLACK);
        modeLable.setFont(new Font("", Font.BOLD, 15));
        modeLable.setBounds(20, 70, 100, 25);

        // radio button
        ButtonGroup radioGroup = new ButtonGroup();

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
        JLabel publicKey = new JLabel();
        publicKey.setText(PUBLIC_KEY);
        publicKey.setForeground(Color.BLACK);
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

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mEventCallback.selectAPKFile();
            }
        });

        selectPublicKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mEventCallback.selectPublicKeyFile();
            }
        });


        radioButtonRelease.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if (radioButtonRelease.isSelected()) {
                    publicKeyActualPathTextArea.setEnabled(true);
                    selectPublicKeyButton.setEnabled(true);
                } else {
                    publicKeyActualPathTextArea.setEnabled(false);
                    selectPublicKeyButton.setEnabled(false);
                }
            }

        });
    }

    public void setAPKPath(VirtualFile sourceFile) {
        this.sourceFile = sourceFile;
        sourceFilepath = sourceFile.getPath();
        apkActualPathTextArea.setText(sourceFilepath);
        checkWhetherApkIsSignedOrUnsigned(sourceFilepath);
    }

    public void setPublicKeyPath(VirtualFile sourceFile) {
        publicKeyActualPathTextArea.setText(sourceFile.getPath());
        try {
            BufferedReader in = new BufferedReader(new FileReader(sourceFile.getPath()));
            String str;
            while ((str = in.readLine()) != null)
                publicKey = str;
            in.close();
        } catch (IOException e) {

        }
    }

    private void checkWhetherApkIsSignedOrUnsigned(String filePath) {

        sourceFilepath = filePath;
        APKparser apkParser = new APKparser();

        if (apkParser.getAPKDetail(filePath)) {
            radioButtonRelease.setSelected(true);
            radioButtonRelease.setEnabled(true);
        } else {
            radioButtonRelease.setEnabled(false);
            radioButtonDebug.setSelected(true);
            publicKeyActualPathTextArea.setEnabled(false);
            selectPublicKeyButton.setEnabled(false);
        }
    }


    private boolean checkForValidations() {


        if (radioButtonRelease.isSelected()) {

            // Validations for APK file

            if (apkActualPathTextArea.getText().equals("")) {
                Messages.showErrorDialog(SELECT_APK_FILE, ERROR);
                return false;
            } else {
                File selectedFile = new File(apkActualPathTextArea.getText());
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
                File selectedFile = new File(publicKeyActualPathTextArea.getText());
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
                File selectedFile = new File(apkActualPathTextArea.getText());
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
