/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.WSDL2AxisServiceBuilder;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.codegen.extension.CodeGenExtension;
import org.apache.axis2.wsdl.codegen.extension.DefaultDatabindingExtension;
import org.apache.axis2.wsdl.codegen.extension.PackageFinder;
import org.apache.axis2.wsdl.codegen.extension.WSDLValidatorExtension;
import org.apache.axis2.wsdl.databinding.JavaTypeMapper;
import org.apache.tuscany.model.util.XMLNameUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class JavaInterfaceGenerator {

    private List codegenExtensions = new ArrayList();
    private CodeGenConfiguration codegenConfiguration;

    public JavaInterfaceGenerator(String uri, String outputLocation, String packageName,
                                  Map<QName, SDODataBindingTypeMappingEntry> typeMapping) throws CodeGenerationException {
        
        Definition definition;
        try {
            definition = readInTheWSDLFile(uri);
        } catch (WSDLException e) {
            throw new CodeGenerationException(e);
        }
        
        Service service=(Service)definition.getServices().values().iterator().next();
        QName serviceQname = service.getQName();
        Port port = (Port)service.getPorts().values().iterator().next();

        if (packageName == null) {
            packageName = XMLNameUtil.getPackageNameFromNamespace(definition.getTargetNamespace());
        }

        JavaTypeMapper typeMapper = new JavaTypeMapper();
        for (Map.Entry<QName, SDODataBindingTypeMappingEntry> e : typeMapping.entrySet()) {
            typeMapper.addTypeMappingObject(e.getKey(), e.getValue());
        }

        AxisService axisService;
        try {
            axisService = new WSDL2AxisServiceBuilder(definition, serviceQname, port.getName()).populateService();
        } catch (AxisFault e) {
            throw new CodeGenerationException(e);
        }

        codegenConfiguration= new CodeGenConfiguration(Collections.EMPTY_MAP);
        codegenConfiguration.setAxisService(axisService);
        codegenConfiguration.setAdvancedCodeGenEnabled(false);
        codegenConfiguration.setAsyncOn(false);
        codegenConfiguration.setDatabindingType("sdo");
        codegenConfiguration.setGenerateAll(true);
        codegenConfiguration.setGenerateDeployementDescriptor(false);
        codegenConfiguration.setOutputLanguage("java");
        codegenConfiguration.setOutputLocation(new File(outputLocation));
        codegenConfiguration.setPackageName(packageName);
        codegenConfiguration.setPackClasses(false);
        codegenConfiguration.setPolicyMap(Collections.EMPTY_MAP);
        codegenConfiguration.setPortName(port.getName());
        codegenConfiguration.setServerSide(false);
        codegenConfiguration.setServiceName(service.getQName().getLocalPart());
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

            JavaInterfaceEmitter emitter = new JavaInterfaceEmitter();
            emitter.setCodeGenConfiguration(codegenConfiguration);
            emitter.setMapper(codegenConfiguration.getTypeMapper());

            emitter.writeInterface(false);

        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

    /**
     * Read the WSDL file
     * @param uri
     * @return
     * @throws WSDLException
     */
    private Definition readInTheWSDLFile(String uri) throws WSDLException {

        WSDLReader reader =
                WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);

        File file = new File(uri);
        String baseURI;

        if (uri.startsWith("http://")){
            baseURI = uri;
        } else{
            if(file.getParentFile() == null){
               try {
                    baseURI = new File(".").getCanonicalFile().toURI().toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                baseURI = file.getParentFile().toURI().toString();
            }
        }


        Document doc;
        try {
            doc = XMLUtils.newDocument(uri);
        } catch (ParserConfigurationException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Configuration Error",
                    e);
        } catch (SAXException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser SAX Error",
                    e);

        } catch (IOException e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error", e);
        }

        return reader.readWSDL(baseURI, doc);
    }

}
