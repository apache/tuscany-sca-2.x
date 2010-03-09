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

package org.apache.tuscany.sca.contribution.java.impl;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.java.JavaImportExportFactory;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Artifact Processor for Java Imports
 * 
 * @version $Rev$ $Date$
 */
public class JavaImportProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<JavaImport> {
    private static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    
    private static final QName IMPORT_JAVA = new QName(SCA11_NS, "import.java");

    private static final String PACKAGE = "package";
    private static final String LOCATION = "location";
    
    private final JavaImportExportFactory factory;
    
    public JavaImportProcessor(FactoryExtensionPoint modelFactories) {
        super();
        this.factory = modelFactories.getFactory(JavaImportExportFactory.class);
    }
    
    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
     private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
    	 if (monitor != null) {
	        Problem problem = monitor.createProblem(this.getClass().getName(), "contribution-java-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	 }
     }
     
    public QName getArtifactType() {
        return IMPORT_JAVA;
    }
    
    public Class<JavaImport> getModelType() {
        return JavaImport.class;
    }

    /**
     * Process <import.java package="" location=""/>
     */
    public JavaImport read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException {
        JavaImport javaImport = this.factory.createJavaImport();
        QName element = null;
        
        try {
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT:
                        element = reader.getName();
    
                        // Read <import.java>
                        if (IMPORT_JAVA.equals(element)) {
                            String packageName = reader.getAttributeValue(null, PACKAGE);
                            if (packageName == null) {
                            	error(context.getMonitor(), "AttributePackageMissing", reader);
                                //throw new ContributionReadException("Attribute 'package' is missing");
                            } else
                            	javaImport.setPackage(packageName);
                            
                            String location = getURIString(reader, LOCATION);                        
                            javaImport.setLocation(location);
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (IMPORT_JAVA.equals(reader.getName())) {
                            return javaImport;
                        }
                        break;        
                }
                
                // Read the next element
                if (reader.hasNext()) {
                    reader.next();
                }
            }
        }
        catch (XMLStreamException e) {
            ContributionReadException ex = new ContributionReadException(e);
            error(context.getMonitor(), "XMLStreamException", reader, ex);
        }
        
        return javaImport;
    }

    public void write(JavaImport javaImport, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        
        // Write <import.java>
        writer.writeStartElement(IMPORT_JAVA.getNamespaceURI(), IMPORT_JAVA.getLocalPart());
        
        if (javaImport.getPackage() != null) {
            writer.writeAttribute(PACKAGE, javaImport.getPackage());
        }
        if (javaImport.getLocation() != null) {
            writer.writeAttribute(LOCATION, javaImport.getLocation());
        }
        
        writer.writeEndElement();
    }


    public void resolve(JavaImport model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        
    }
}
