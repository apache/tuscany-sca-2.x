/**
 *
 *  Copyright 2005 BEA Systems Inc.
 *  Copyright 2005 International Business Machines Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.tools.wsdl2java.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.apache.axis2.i18n.Messages;
import org.apache.axis2.util.FileWriter;
import org.apache.axis2.wsdl.builder.WOMBuilderFactory;
import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.codegen.emitter.JavaEmitter;
import org.apache.axis2.wsdl.codegen.extension.CodeGenExtension;
import org.apache.axis2.wsdl.codegen.extension.DefaultDatabindingExtension;
import org.apache.axis2.wsdl.codegen.extension.PackageFinder;
import org.apache.axis2.wsdl.codegen.extension.WSDLValidatorExtension;
import org.apache.axis2.wsdl.codegen.writer.InterfaceWriter;
import org.apache.axis2.wsdl.databinding.JavaTypeMapper;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.tuscany.model.util.XMLNameUtil;
import org.apache.wsdl.WSDLDescription;
import org.w3c.dom.Document;

public class JavaInterfaceGenerator {

    private List codegenExtensions = new ArrayList();
    private CodeGenConfiguration codegenConfiguration;

    public JavaInterfaceGenerator(String uri, String outputLocation, String packageName,
                                  Map<QName, String> typeMapping) throws CodeGenerationException {
        WSDLDescription wom;
        try {
            wom = WOMBuilderFactory.getBuilder(org.apache.wsdl.WSDLConstants.WSDL_1_1)
                .build(uri).getDescription();
        } catch (WSDLException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.wsdlParsingException"), e);
        }

        if (packageName == null) {
            packageName = XMLNameUtil.getPackageNameFromNamespace(wom.getTargetNameSpace());
        }

        JavaTypeMapper typeMapper = new JavaTypeMapper();
        for (Map.Entry<QName, String> e : typeMapping.entrySet()) {
            typeMapper.addTypeMappingName(e.getKey(), e.getValue());
        }

        codegenConfiguration = new CodeGenConfiguration(wom, new HashMap());
        codegenConfiguration.setAdvancedCodeGenEnabled(false);
        codegenConfiguration.setAsyncOn(false);
        codegenConfiguration.setCodeGenerationStyle(0);
        codegenConfiguration.setDatabindingType("sdo");
        codegenConfiguration.setGenerateAll(true);
        codegenConfiguration.setGenerateDeployementDescriptor(false);
        codegenConfiguration.setOutputLanguage("java");
        codegenConfiguration.setOutputLocation(new File(outputLocation));
        codegenConfiguration.setPackageName(packageName);
        codegenConfiguration.setPackClasses(false);
        codegenConfiguration.setPolicyMap(new HashMap());
        codegenConfiguration.setPortName(null);
        codegenConfiguration.setServerSide(false);
        codegenConfiguration.setServiceName(null);
        codegenConfiguration.setSyncOn(true);
        codegenConfiguration.setTypeMapper(typeMapper);
        codegenConfiguration.setWriteMessageReceiver(false);
        codegenConfiguration.setWriteTestCase(false);

        addExtension(new WSDLValidatorExtension());
        addExtension(new PackageFinder());
        addExtension(new SDODataBindingCodegenExtension(typeMapper));
        addExtension(new DefaultDatabindingExtension());
    }

    @SuppressWarnings("unchecked")
    private void addExtension(CodeGenExtension ext) {
        ext.init(codegenConfiguration);
        codegenExtensions.add(ext);
    }

    public void generate() throws CodeGenerationException {
        try {
            for (int i = 0; i < codegenExtensions.size(); i++) {
                ((CodeGenExtension)codegenExtensions.get(i)).engage();
            }

            class JavaInterfaceEmitter extends JavaEmitter {

                protected void writeInterface(boolean writeDatabinders) throws Exception {
                    Document interfaceModel = createDOMDocumentForInterface(writeDatabinders);
                    if (!configuration.getOutputLocation().exists()) {
                        configuration.getOutputLocation().mkdirs();
                    }
                    InterfaceWriter interfaceWriter = new InterfaceWriter(this.configuration
                        .getOutputLocation(), this.configuration.getOutputLanguage());

                    String packageName = interfaceModel.getDocumentElement().getAttribute("package");
                    String className = interfaceModel.getDocumentElement().getAttribute("name");

                    System.out.println(">>  Generating Java class " + packageName + "." + className);
                    File outputFile = FileWriter.createClassFile(this.configuration.getOutputLocation(),
                                                                 packageName, className, ".java");
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }

                    writeClass(interfaceModel, interfaceWriter);
                }

            }

            JavaInterfaceEmitter emitter = new JavaInterfaceEmitter();
            emitter.setCodeGenConfiguration(codegenConfiguration);
            emitter.setMapper(codegenConfiguration.getTypeMapper());

            emitter.writeInterface(false);

        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

}
