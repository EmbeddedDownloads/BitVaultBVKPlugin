package org.bitvault.plugin.action.View;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import org.bitvault.plugin.action.Utils.Constants;
import org.bitvault.plugin.action.Utils.StringConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Class used to show custom ui
 */
public class RestartDialog extends DialogWrapper implements Constants,StringConstants {

    public RestartDialog(final boolean canBeParent) {
        super(canBeParent);
        init();
    }



    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "1500";
    }

    @Override
    protected boolean isCenterStrictedToPreferredSize() {
        return true;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
        dispose();
    }

    @NotNull
    @Override
    protected Action[] createActions() {

//        JOptionPane.showMessageDialog(null,System.getProperty("os.name"));



        final Action[] actions = new Action[2];
        try {
            final Action[] previousAction = super.createActions();

            for (int i = 0; i < 2; i++) {
                actions[i] = previousAction[i];
                if (i == 0) {
                    if(System.getProperty("os.name").equalsIgnoreCase(MAC) ||
                            System.getProperty("os.name").contains("Mac")){

                        actions[i].putValue(KEY_NAME, OK);
                    }else{
                        actions[i].putValue(KEY_NAME, RESTART);
                    }
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
            try {

                final ApplicationEx app = (ApplicationEx) ApplicationManager.getApplication();
                final boolean canRestart = app.isRestartCapable();
                if(canRestart){
                    app.restart(true);
                }

            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        } catch (final Exception e) {
            Messages.showErrorDialog("Error is : " + e.toString(), ERROR);
            e.printStackTrace();
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
     *
     * @param parentPanel
     */
    private void showUi(final JPanel parentPanel) {

        // First row
        // Apk path label
        final JLabel apkPathLable = new JLabel();
        apkPathLable.setText(RESTART_DESCRIPTION);
        apkPathLable.setForeground(JBColor.BLACK);
        apkPathLable.setHorizontalAlignment(SwingConstants.CENTER);
        apkPathLable.setVerticalAlignment(SwingConstants.CENTER);
        apkPathLable.setFont(new Font("", Font.PLAIN, 15));
        apkPathLable.setBounds(20, 20, 380, 25);

        // adding all components in main frame
        parentPanel.add(apkPathLable);

        parentPanel.setSize(400, 50);
        parentPanel.setPreferredSize(new Dimension(400, 50));
        parentPanel.setLayout(null);
        parentPanel.setVisible(true);

    }
}
