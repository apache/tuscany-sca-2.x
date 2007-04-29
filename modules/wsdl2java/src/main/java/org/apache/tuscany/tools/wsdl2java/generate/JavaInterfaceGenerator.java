/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.tools.wsdl2java.generate;

import static org.apache.tuscany.tools.wsdl2java.util.XMLNameUtil.getPackageNameFromNamespace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.codegen.extension.CodeGenExtension;
import org.apache.axis2.wsdl.codegen.extension.DefaultDatabindingExtension;
import org.apache.axis2.wsdl.codegen.extension.PackageFinder;
import org.apache.axis2.wsdl.codegen.extension.WSDLValidatorExtension;
import org.apache.axis2.wsdl.databinding.JavaTypeMapper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class JavaInterfaceGenerator {

    private List codegenExtensions = new ArrayList();
    private List<CodeGenConfiguration> codegenConfigurations= new LinkedList<CodeGenConfiguration>();
    
    


    public JavaInterfaceGenerator(String uri, String ports[], String outputLocation, String packageName,
                                  Map<QName, SDODataBindingTypeMappingEntry> typeMapping) throws CodeGenerationException {
        
        Definition definition;
        try {
            definition = readWSDL(uri);
        } catch (WSDLException e) {
            throw new CodeGenerationException(e);
        }
        
        HashSet interestedPorts= ports == null ? null : new HashSet(Arrays.asList(ports));
        
       // Service service=(Service)definition.getServices().values().().next();
        
        HashSet<QName> donePortTypes= new HashSet<QName>();
        
        for (Iterator sIter  = definition.getServices().values().iterator(); sIter.hasNext(); ) {
            Service service = (Service) sIter.next();
            
            QName serviceQname = service.getQName();
             for (Iterator pIter= service.getPorts().values().iterator(); pIter.hasNext(); ) {
                 Port port= (Port) pIter.next();
                if(interestedPorts != null && ! interestedPorts.contains(port.getName())) continue;//not iterested.
                 PortType portType= getPortType(port);
                 if(null == portType) continue; // not connected.
                 QName pQName= portType.getQName();
                 if(donePortTypes.contains(pQName)) continue; //allready did it.
                 donePortTypes.add(pQName);
              
                if (packageName == null) {
                                        
                    packageName = getPackageNameFromNamespace(definition.getTargetNamespace());
                }
                JavaTypeMapper typeMapper = new JavaTypeMapper();
                for (Map.Entry<QName, SDODataBindingTypeMappingEntry> e : typeMapping.entrySet()) {
                    typeMapper.addTypeMappingObject(e.getKey(), e.getValue());
                }
                AxisService axisService;
                try {
                    axisService = new WSDL11ToAxisServiceBuilder(definition, serviceQname, port.getName()).populateService();
                } catch (AxisFault e) {
                    throw new CodeGenerationException(e);
                }
                axisService.setName(port.getBinding().getPortType().getQName().getLocalPart());
                CodeGenConfiguration codegenConfiguration = new CodeGenConfiguration(Collections.EMPTY_MAP);
                codegenConfigurations.add(codegenConfiguration);
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
                addExtension(new WSDLValidatorExtension(), codegenConfiguration);
                addExtension(new PackageFinder(), codegenConfiguration);
                addExtension(new SDODataBindingCodegenExtension(typeMapper), codegenConfiguration);
                addExtension(new DefaultDatabindingExtension(), codegenConfiguration);
            }            
        }        
    }


    private PortType getPortType(Port port) {
       Binding binding = port.getBinding();
       if(null != binding){
          return binding.getPortType();
       }
       return null;
        
    }


    @SuppressWarnings("unchecked")
    private void addExtension(CodeGenExtension ext, CodeGenConfiguration codegenConfiguration) {
        //ext.init(codegenConfiguration);
        codegenExtensions.add(new Object[]{ext, codegenConfiguration});
    }

    public void generate() throws CodeGenerationException {
        try {
            for (int i = 0; i < codegenExtensions.size(); i++) {
                //CodeGenExtension 
                Object[] pair = (Object[])codegenExtensions.get(i);
                
                CodeGenExtension cge= (CodeGenExtension) pair[0];
                CodeGenConfiguration cgf= (CodeGenConfiguration)pair[1];
                
                cge.engage(cgf);
               
            }

            for(CodeGenConfiguration codegenConfiguration : codegenConfigurations){
            JavaInterfaceEmitter emitter = new JavaInterfaceEmitter();
            emitter.setCodeGenConfiguration(codegenConfiguration);
            emitter.setMapper(codegenConfiguration.getTypeMapper());

            emitter.writeInterface(false);
            }

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
    private Definition readWSDL(String uri) throws WSDLException {

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
