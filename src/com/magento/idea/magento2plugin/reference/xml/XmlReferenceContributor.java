/**
 * Copyright © Magento, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
package com.magento.idea.magento2plugin.reference.xml;

import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlTokenType;
import com.magento.idea.magento2plugin.magento.files.MftfActionGroup;
import com.magento.idea.magento2plugin.magento.files.MftfTest;
import com.magento.idea.magento2plugin.reference.provider.*;
import com.magento.idea.magento2plugin.reference.provider.mftf.*;
import com.magento.idea.magento2plugin.util.RegExUtil;
import org.jetbrains.annotations.NotNull;
import static com.intellij.patterns.XmlPatterns.*;

public class XmlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {

        // <someXmlTag someAttribute="Some\Php\ClassName[::CONST|$property|method()]" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withValue(string().matches(RegExUtil.XmlRegex.CLASS_ELEMENT)),
            new CompositeReferenceProvider(
                new PhpClassReferenceProvider(),
                new PhpClassMemberReferenceProvider()
            )
        );

        // <someXmlTag>Some\Php\ClassName[::CONST|$property|method()]</someXmlTag>
        registrar.registerReferenceProvider(
            XmlPatterns.psiElement(XmlTokenType.XML_DATA_CHARACTERS)
                .withText(string().matches(RegExUtil.XmlRegex.CLASS_ELEMENT)),
            new CompositeReferenceProvider(
                new PhpClassReferenceProvider(),
                new PhpClassMemberReferenceProvider()
            )
        );

        // virtual type references
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("type")).inFile(xmlFile().withName(string().endsWith("di.xml"))
            ),
            new VirtualTypeReferenceProvider()
        );
        registrar.registerReferenceProvider(
            XmlPatterns.psiElement(XmlTokenType.XML_DATA_CHARACTERS).withParent(
                XmlPatterns.xmlText().withParent(
                    XmlPatterns.xmlTag().withChild(
                        XmlPatterns.xmlAttribute().withName("xsi:type")
                    )
                )
            ).inFile(xmlFile().withName(string().endsWith("di.xml"))),
            new VirtualTypeReferenceProvider()
        );

        // arguments
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("name").withParent(
                    XmlPatterns.xmlTag().withName("argument").withParent(
                        XmlPatterns.xmlTag().withName("arguments")
                    )
                )
            ).inFile(xmlFile().withName(string().endsWith("di.xml"))),
            new PhpConstructorArgumentReferenceProvider()
        );

        // <service method="methodName"/>
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("method").withParent(
                    XmlPatterns.xmlTag().withName("service")
                )
            ).inFile(xmlFile().withName(string().endsWith("webapi.xml"))),
            new PhpServiceMethodReferenceProvider()
        );

        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("name").withParent(
                    XmlPatterns.xmlTag().withName("referenceContainer")
                )
            ),
            new LayoutContainerReferenceProvider()
        );

        registrar.registerReferenceProvider(
            XmlPatterns.or(
                XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute().withName("name")
                    .withParent(XmlPatterns.xmlTag().withName("referenceBlock"))),
                XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute()
                        .withName(string().oneOf("before", "after"))
                    .withParent(XmlPatterns.xmlTag().withName("block"))),
                XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute()
                        .withName(string().oneOf("before", "after", "destination", "element"))
                    .withParent(XmlPatterns.xmlTag().withName("move"))),
                XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute().withName("name")
                    .withParent(XmlPatterns.xmlTag().withName("remove")))
            ),
            new LayoutBlockReferenceProvider()
        );

        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("handle").withParent(
                    XmlPatterns.xmlTag().withName("update")
                )
            ),
            new LayoutUpdateReferenceProvider()
        );

        // <event name="reference" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(
                XmlPatterns.xmlAttribute().withName("name").withParent(
                    XmlPatterns.xmlTag().withName("event")
                )
            ),
            new EventNameReferenceProvider()
        );

        // <someXmlTag someAttribute="Module_Name[.*]" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withValue(string().matches(".*[A-Z][a-zA-Z0-9]+_[A-Z][a-zA-Z0-9]+.*")),
            new CompositeReferenceProvider(
                new ModuleNameReferenceProvider()
            )
        );

        // <someXmlTag>Module_Name[.*]</someXmlTag>
        registrar.registerReferenceProvider(
            XmlPatterns.psiElement(XmlTokenType.XML_DATA_CHARACTERS)
                    .withText(string().matches(".*[A-Z][a-zA-Z0-9]+_[A-Z][a-zA-Z0-9]+.*")),
            new CompositeReferenceProvider(
                new ModuleNameReferenceProvider()
            )
        );

        // <someXmlTag someAttribute="path/to/some-of-file.some-ext" />
        registrar.registerReferenceProvider(
                XmlPatterns.xmlAttributeValue().withValue(string().matches(".*\\W([\\w-]+/)*[\\w\\.-]+.*")),
                new CompositeReferenceProvider(
                        new FilePathReferenceProvider()
                )
        );

        // <someXmlTag>path/to/some-of-file.some-ext</someXmlTag>
        registrar.registerReferenceProvider(
                XmlPatterns.psiElement(XmlTokenType.XML_DATA_CHARACTERS)
                        .withText(string().matches(".*\\W([\\w-]+/)*[\\w\\.-]+.*")),
                new CompositeReferenceProvider(
                        new FilePathReferenceProvider()
                )
        );

        // <someXmlTag userInput="{{someValue}}" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withValue(
                string().matches(RegExUtil.Magento.MFTF_CURLY_BRACES)
            ).withParent(XmlPatterns.xmlAttribute().withName(
                MftfActionGroup.USER_INPUT_TAG
            )),
            new CompositeReferenceProvider(
                new DataReferenceProvider()
            )
        );

        // <createData entity="SimpleProduct" />
        // <updateData entity="SimpleProduct" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute()
                .withName(MftfActionGroup.ENTITY_ATTRIBUTE)
                .withParent(XmlPatterns.xmlTag().withName(
                    string().oneOf(MftfActionGroup.CREATE_DATA_TAG, MftfActionGroup.UPDATE_DATA_TAG)
            ))),
            new CompositeReferenceProvider(
                new DataReferenceProvider()
            )
        );

        // <actionGroup ref="someActionGroup" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute().withName("ref")
                .withParent(XmlPatterns.xmlTag().withName("actionGroup"))),
            new CompositeReferenceProvider(
                new ActionGroupReferenceProvider()
            )
        );

        // <actionGroup extends="parentActionGroup" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute().withName("extends")
                .withParent(XmlPatterns.xmlTag().withName("actionGroup"))),
            new CompositeReferenceProvider(
                new ActionGroupReferenceProvider()
            )
        );

        // <entity name="B" extends="A" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute().withName("extends")
                .withParent(XmlPatterns.xmlTag().withName("entity"))),
            new CompositeReferenceProvider(
                new DataReferenceProvider()
            )
        );

        // <test name="B" extends="A" />
        registrar.registerReferenceProvider(
            XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute()
                .withName(MftfTest.EXTENDS_ATTRIBUTE)
                .withParent(XmlPatterns.xmlTag().withName(MftfTest.TEST_TAG)
                    .withParent(XmlPatterns.xmlTag().withName(MftfTest.ROOT_TAG)
                    )
                )
            ),
            new CompositeReferenceProvider(
                new TestNameReferenceProvider()
            )
        );

        // <someXmlTag component="requireJsMappingKey" />
        registrar.registerReferenceProvider(
                XmlPatterns.xmlAttributeValue().withParent(
                        XmlPatterns.xmlAttribute().withName("component")
                ),
                new RequireJsPreferenceReferenceProvider()
        );

        // <item name="component">requireJsMappingKey</item>
        registrar.registerReferenceProvider(
                XmlPatterns.psiElement(XmlTokenType.XML_DATA_CHARACTERS).withParent(
                        XmlPatterns.xmlText().withParent(
                                XmlPatterns.xmlTag().withName("item").withChild(
                                        XmlPatterns.xmlAttribute().withValue(string().matches("component"))
                                ).withChild(
                                        XmlPatterns.xmlAttribute().withName("name")
                                )
                        )
                ),
                new RequireJsPreferenceReferenceProvider()
        );

        registerReferenceForDifferentNesting(registrar);
    }

    private void registerReferenceForDifferentNesting(@NotNull PsiReferenceRegistrar registrar) {
        int i = 1;
        int maxNesting = 10;
        while( i < maxNesting) {

            // <someXmlTag url="{{someValue}}" /> in MFTF Tests and ActionGroups
            registrar.registerReferenceProvider(
                XmlPatterns.xmlAttributeValue().withValue(
                    string().matches(RegExUtil.Magento.MFTF_CURLY_BRACES)
                ).withParent(XmlPatterns.xmlAttribute().withName(
                    MftfActionGroup.URL_ATTRIBUTE
                ).withParent(XmlPatterns.xmlTag().withSuperParent(i, XmlPatterns.xmlTag().withName(
                    string().oneOf(MftfActionGroup.ROOT_TAG, MftfTest.TEST_TAG)
                )))),
                new CompositeReferenceProvider(
                    new PageReferenceProvider()
                )
            );

            // <someXmlTag selector="{{someValue}}" /> in MFTF Tests and ActionGroups
            registrar.registerReferenceProvider(
                XmlPatterns.xmlAttributeValue().withValue(
                    string().matches(RegExUtil.Magento.MFTF_CURLY_BRACES)
                ).withParent(XmlPatterns.xmlAttribute().withName(
                    MftfActionGroup.SELECTOR_ATTRIBUTE
                ).withParent(XmlPatterns.xmlTag().withSuperParent(i, XmlPatterns.xmlTag().withName(
                    string().oneOf(MftfActionGroup.ROOT_TAG, MftfTest.TEST_TAG)
                )))),
                new CompositeReferenceProvider(
                    new SectionReferenceProvider()
                )
            );

            i++;
        }
    }
}
