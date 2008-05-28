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
package org.apache.tuscany.sca.implementation.resource.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.resource.ResourceImplementation;
import org.apache.tuscany.sca.implementation.resource.ResourceImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;


/**
 * Implements a StAX artifact processor for resource implementations.
 *
 * @version $Rev$ $Date$
 */
public class ResourceImplementationProcessor implements StAXArtifactProcessor<ResourceImplementation> {
    private static final QName IMPLEMENTATION_RESOURCE = new QName(Constants.SCA10_TUSCANY_NS, "implementation.resource");
    //private static final String MSG_LOCATION_MISSING = "Reading implementation.resource - location attribute missing";
    
    private ContributionFactory contributionFactory;
    private ResourceImplementationFactory implementationFactory;
    private Monitor monitor;
    
    public ResourceImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        implementationFactory = modelFactories.getFactory(ResourceImplementationFactory.class);
        this.monitor = monitor;
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
	        Problem problem = new ProblemImpl(this.getClass().getName(), "impl-resource-validation-messages", Severity.ERROR, model, message, ex);
	        monitor.problem(problem);
    	 }
    }

    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = new ProblemImpl(this.getClass().getName(), "impl-resource-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }    

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_RESOURCE;
    }

    public Class<ResourceImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return ResourceImplementation.class;
    }

    public ResourceImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        
        // Read an <implementation.resource> element
        
        // Create and initialize the resource implementation model
        ResourceImplementation implementation = null;        

        // Read the location attribute specifying the location of the resources
        String location = reader.getAttributeValue(null, "location");

        if (location != null) {
            implementation = implementationFactory.createResourceImplementation();
            implementation.setLocation(location);
            implementation.setUnresolved(true);
        } else {
            error("LocationAttributeMissing", reader);
            //throw new ContributionReadException(MSG_LOCATION_MISSING);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_RESOURCE.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(ResourceImplementation implementation, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the resource directory location
        Artifact artifact = contributionFactory.createArtifact();
        artifact.setURI(implementation.getLocation());
        Artifact resolved = resolver.resolveModel(Artifact.class, artifact);
        if (resolved.getLocation() != null) {
            try {
                implementation.setLocationURL(new URL(resolved.getLocation()));
                implementation.setUnresolved(false);
            } catch (IOException e) {
            	ContributionResolveException ce = new ContributionResolveException(e);
            	error("ContributionResolveException", resolver, ce);
                //throw ce;
            }
        }
    }

    public void write(ResourceImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        // Write <implementation.resource>
        writer.writeStartElement(IMPLEMENTATION_RESOURCE.getNamespaceURI(), IMPLEMENTATION_RESOURCE.getLocalPart());
        
        if (implementation.getLocation() != null) {
            writer.writeAttribute("location", implementation.getLocation());
        }
        
        writer.writeEndElement();
    }
}
