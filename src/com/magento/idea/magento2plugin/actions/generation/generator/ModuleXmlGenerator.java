/*
 * Copyright © Magento, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
package com.magento.idea.magento2plugin.actions.generation.generator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.magento.idea.magento2plugin.actions.generation.data.ModuleXmlData;
import com.magento.idea.magento2plugin.actions.generation.generator.data.ModuleDirectoriesData;
import com.magento.idea.magento2plugin.actions.generation.generator.util.DirectoryGenerator;
import com.magento.idea.magento2plugin.actions.generation.generator.util.FileFromTemplateGenerator;
import com.magento.idea.magento2plugin.magento.files.ModuleXml;
import com.magento.idea.magento2plugin.magento.packages.Package;
import org.jetbrains.annotations.NotNull;
import java.util.Properties;

public class ModuleXmlGenerator extends FileGenerator {

    private final ModuleXmlData moduleXmlData;
    private final FileFromTemplateGenerator fileFromTemplateGenerator;
    private final DirectoryGenerator directoryGenerator;

    public ModuleXmlGenerator(@NotNull ModuleXmlData moduleXmlData, Project project) {
        super(project);
        this.moduleXmlData = moduleXmlData;
        this.fileFromTemplateGenerator = FileFromTemplateGenerator.getInstance(project);
        this.directoryGenerator = DirectoryGenerator.getInstance();
    }

    @Override
    public PsiFile generate(String actionName) {
        if (moduleXmlData.getCreateModuleDirs()) {
            ModuleDirectoriesData moduleDirectoriesData = directoryGenerator.createOrFindModuleDirectories(moduleXmlData.getPackageName(), moduleXmlData.getModuleName(), moduleXmlData.getBaseDir());
            return fileFromTemplateGenerator.generate(ModuleXml.getInstance(), getAttributes(), moduleDirectoriesData.getModuleEtcDirectory(), actionName);
        }
        PsiDirectory etcDirectory = directoryGenerator.findOrCreateSubdirectory(moduleXmlData.getBaseDir(), Package.MODULE_BASE_AREA_DIR);
        return fileFromTemplateGenerator.generate(ModuleXml.getInstance(), getAttributes(), etcDirectory, actionName);
    }

    protected void fillAttributes(Properties attributes) {
        attributes.setProperty("PACKAGE", moduleXmlData.getPackageName());
        attributes.setProperty("MODULE_NAME", moduleXmlData.getModuleName());
    }
}
