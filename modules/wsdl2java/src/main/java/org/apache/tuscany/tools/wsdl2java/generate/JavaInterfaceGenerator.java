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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
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
import org.apache.axis2.util.FileWriter;
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

import com.sun.tools.xjc.api.XJC;

public class JavaInterfaceGenerator {

    private List codegenExtensions = new ArrayList();
    private List<CodeGenConfiguration> codegenConfigurations= new LinkedList<CodeGenConfiguration>();
    private String outputLocation;
    


    public JavaInterfaceGenerator(String uri, String ports[], String outputLocation, String packageName,
                                  Map<QName, SDODataBindingTypeMappingEntry> typeMapping) throws CodeGenerationException {
        this.outputLocation = outputLocation;
        
        Definition definition;
        try {
            definition = readWSDL(uri);
        } catch (WSDLException e) {
            throw new CodeGenerationException(e);
        }
        
        HashSet<String> interestedPorts = ports == null ? null : new HashSet<String>(Arrays.asList(ports));
        
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
                    //use JAXWS/JAXB NS->package default algorithm, not the SDO/EMF one
                    packageName = XJC.getDefaultPackageName(definition.getTargetNamespace());
                }
                //
                // Use WSDL4J object to generate exception classes
                //
                generateFaults(packageName, portType, typeMapping);
                JavaTypeMapper typeMapper = new JavaTypeMapper();
                for (Map.Entry<QName, SDODataBindingTypeMappingEntry> e : typeMapping.entrySet()) {
                    typeMapper.addTypeMappingObject(e.getKey(), e.getValue());
                    // Added for generation of exceptions from faults
                    typeMapper.addTypeMappingName(e.getKey(), e.getValue().getClassName());
                }


                AxisService axisService;
                WSDL11ToAxisServiceBuilder builder;
                try {
                    //
                    // Added since at a newer level of Axis2, this doesn't work 
                    //  without the setCodegen(true)
                    //
                    builder = new WSDL11ToAxisServiceBuilder(definition, serviceQname, port.getName());
                    builder.setCodegen(true);
                    axisService = builder.populateService();
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
                // This lines up with the sync/async variable from the XSL template
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
                // CodeGenExtension
                Object[] pair = (Object[])codegenExtensions.get(i);

                CodeGenExtension cge = (CodeGenExtension)pair[0];
                CodeGenConfiguration cgf = (CodeGenConfiguration)pair[1];

                cge.engage(cgf);
            }

            for (CodeGenConfiguration codegenConfiguration : codegenConfigurations) {
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
     * 
     * @param uri
     * @return
     * @throws WSDLException
     */
    private Definition readWSDL(String uri) throws WSDLException {

        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);

        File file = new File(uri);
        String baseURI;

        if (uri.startsWith("http://")) {
            baseURI = uri;
        } else {
            if (file.getParentFile() == null) {
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
            throw new WSDLException(WSDLException.PARSER_ERROR, "Parser Configuration Error", e);
        } catch (SAXException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR, "Parser SAX Error", e);

        } catch (IOException e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error", e);
        }

        return reader.readWSDL(baseURI, doc);
    }
    
    private void generateFaults(String packageName, PortType portType, Map<QName, SDODataBindingTypeMappingEntry> typeMapping) 
        throws CodeGenerationException{
        
        for (Object o: portType.getOperations()) {
            Operation op = (Operation)o;
            Map messageMap = op.getFaults();
            Iterator iter = messageMap.values().iterator();
            while (iter.hasNext()) {
                Fault fault = (Fault)iter.next();
                Message faultMsg = fault.getMessage();
                Iterator iter2 = faultMsg.getParts().values().iterator();
                Part faultMsgPart = (Part)iter2.next();
                // TODO - if other parts throw exc
                QName faultMsgQName = faultMsg.getQName();
                QName faultMsgPartElementQName = faultMsgPart.getElementName();
                String faultClassName = typeMapping.get(faultMsgPartElementQName).getClassName();                
                writeException(packageName, faultMsgQName, faultClassName, faultMsgPartElementQName);
            }
        }
    }
    
    private void writeException(String packageName, QName faultMsgQName, String faultClassName, QName faultMsgPartElementQName) 
        throws CodeGenerationException{        
        
        try {
            String faultWrapperClassName = 
                WSDL2JavaGenerator.normalizeClassName(faultMsgQName.getLocalPart());
            
            File outputDir = new File(this.outputLocation);
            
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File outputFile = FileWriter.createClassFile(outputDir,
                    packageName, faultWrapperClassName, ".java");

            FileOutputStream fileStream = new FileOutputStream(outputFile);       
            PrintStream stream = new PrintStream(fileStream); 

            System.out.println(">>  Generating Java exception class " + packageName + "." + faultWrapperClassName);

            stream.println();
            stream.println("package " + packageName + ";");
            stream.println();
            stream.println("import javax.xml.namespace.QName; ");
            stream.println();
            stream.println("public class " + faultWrapperClassName  + " extends Exception {");
            stream.println();
            stream.println("    private " + faultClassName + " fault;");
            stream.println();
            stream.println("    public " + faultWrapperClassName + "(String message, " + faultClassName + " fault, Throwable cause) {");
            stream.println("        super(message, cause);");
            stream.println("        this.fault = fault;");
            stream.println("    }");
            stream.println();
            stream.println("    public static QName FAULT_ELEMENT = new QName(\"" + faultMsgPartElementQName.getNamespaceURI() + 
                    "\",\"" + faultMsgPartElementQName.getLocalPart() + "\");");
            stream.println();
            stream.println("    public " + faultClassName + " getFaultInfo() {");
            stream.println("        return this.fault;");
            stream.println("    }");
            stream.println("}");
            stream.println();
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }
}
