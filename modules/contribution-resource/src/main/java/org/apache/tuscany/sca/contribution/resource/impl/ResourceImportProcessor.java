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

package org.apache.tuscany.sca.contribution.resource.impl;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resource.ResourceImport;
import org.apache.tuscany.sca.contribution.resource.ResourceImportExportFactory;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.monitor.impl.ProblemImpl;

/**
 * Artifact processor for Namespace import
 * 
 * @version $Rev$ $Date$
 */
public class ResourceImportProcessor  implements StAXArtifactProcessor<ResourceImport> {
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    
    private static final QName IMPORT_RESOURCE = new QName(SCA10_NS, "import.resource");

    private static final String URI = "uri";
    private static final String LOCATION = "location";
    
    private final ResourceImportExportFactory factory;
    private final Monitor monitor;
    
    public ResourceImportProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.factory = modelFactories.getFactory(ResourceImportExportFactory.class);
        this.monitor = monitor;
    }
    
    /**
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
     private void error(String message, Object model, Object... messageParameters) {
    	 if (monitor != null) {
	        Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-resource-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	 }
     }
     
     /**
      * Report a exception.
      * 
      * @param problems
      * @param message
      * @param model
      */
     private void error(String message, Object model, Exception ex) {
         if (monitor != null) {
             Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-resource-validation-messages", Severity.ERROR, model, message, ex);
             monitor.problem(problem);
         }
     }
    
    public QName getArtifactType() {
        return IMPORT_RESOURCE;
    }
    
    public Class<ResourceImport> getModelType() {
        return ResourceImport.class;
    }

    /**
     * Process <import.resource uri="" location=""/>
     */
    public ResourceImport read(XMLStreamReader reader) throws ContributionReadException {
    	ResourceImport resourceImport = this.factory.createResourceImport();
        QName element;
        
        try {
            while (reader.hasNext()) {
                int event = reader.getEventType();
                switch (event) {
                    case START_ELEMENT:
                        element = reader.getName();
    
                        // Read <import>
                        if (IMPORT_RESOURCE.equals(element)) {
                            String uri = reader.getAttributeValue(null, URI);
                            if (uri == null) {
                            	error("AttributeURIMissing", reader);
                                //throw new ContributionReadException("Attribute 'uri' is missing");
                            } else
                                resourceImport.setURI(uri);
    
                            String location = reader.getAttributeValue(null, LOCATION);
                            if (location != null) {
                                resourceImport.setLocation(location);
                            }
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (IMPORT_RESOURCE.equals(reader.getName())) {
                            return resourceImport;
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
            error("XMLStreamException", reader, ex);
        }
        
        return resourceImport;
    }

    public void write(ResourceImport resourceImport, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        // Write <import>
        writer.writeStartElement(IMPORT_RESOURCE.getNamespaceURI(), IMPORT_RESOURCE.getLocalPart());
        
        if (resourceImport.getURI() != null) {
            writer.writeAttribute(URI, resourceImport.getURI());
        }
        if (resourceImport.getLocation() != null) {
            writer.writeAttribute(LOCATION, resourceImport.getLocation());
        }
        
        writer.writeEndElement();
    }


    public void resolve(ResourceImport model, ModelResolver resolver) throws ContributionResolveException {
    }
}
