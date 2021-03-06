/*
 * Copyright © Magento, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
package com.magento.idea.magento2plugin.actions.generation.dialog.validator;

import com.magento.idea.magento2plugin.actions.generation.dialog.NewModuleDialog;
import com.magento.idea.magento2plugin.bundles.ValidatorBundle;
import com.magento.idea.magento2plugin.util.RegExUtil;
import javax.swing.*;

public class NewModuleDialogValidator {
    private static NewModuleDialogValidator INSTANCE = null;
    private ValidatorBundle validatorBundle;
    private NewModuleDialog dialog;

    public static NewModuleDialogValidator getInstance(NewModuleDialog dialog) {
        if (null == INSTANCE) {
            INSTANCE = new NewModuleDialogValidator();
        }
        INSTANCE.dialog = dialog;
        return INSTANCE;
    }

    public NewModuleDialogValidator() {
        this.validatorBundle = new ValidatorBundle();
    }

    public boolean validate()
    {
        String errorTitle = "Error";
        String packageName = dialog.getPackageName();
        if (packageName.length() == 0) {
            String errorMessage = validatorBundle.message("validator.notEmpty", "Package Name");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        if (!packageName.matches(RegExUtil.ALPHANUMERIC)) {
            String errorMessage = validatorBundle.message("validator.alphaNumericCharacters", "Package Name");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        if (!Character.isUpperCase(packageName.charAt(0)) && !Character.isDigit(packageName.charAt(0))) {
            String errorMessage = validatorBundle.message("validator.startWithNumberOrCapitalLetter", "Package Name");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        String moduleName = dialog.getModuleName();
        if (moduleName.length() == 0) {
            String errorMessage = validatorBundle.message("validator.notEmpty", "Module Name");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        if (!moduleName.matches(RegExUtil.ALPHANUMERIC)) {
            String errorMessage = validatorBundle.message("validator.alphaNumericCharacters", "Module Name");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        if (!Character.isUpperCase(moduleName.charAt(0)) && !Character.isDigit(moduleName.charAt(0))) {
            String errorMessage = validatorBundle.message("validator.startWithNumberOrCapitalLetter", "Module Name");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        if (dialog.getModuleVersion().length() == 0) {
            String errorMessage = validatorBundle.message("validator.notEmpty", "Module Version");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        if (dialog.getModuleDescription().length() == 0) {
            String errorMessage = validatorBundle.message("validator.notEmpty", "Module Description");
            JOptionPane.showMessageDialog(null, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        return true;
    }
}
