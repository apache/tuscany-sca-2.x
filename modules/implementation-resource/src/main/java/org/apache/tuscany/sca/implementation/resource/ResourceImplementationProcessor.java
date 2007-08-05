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
package org.apache.tuscany.sca.implementation.resource;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;


/**
 * Implements a STAX artifact processor for resource implementations.
 */
public class ResourceImplementationProcessor implements StAXArtifactProcessor<ResourceImplementation> {
    private static final QName IMPLEMENTATION_RESOURCE = new QName("http://www.osoa.org/xmlns/sca/1.0", "implementation.resource");
    
    private ContributionFactory contributionFactory;
    private JavaInterfaceIntrospector introspector;
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    
    public ResourceImplementationProcessor(ModelFactoryExtensionPoint modelFactories) {
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        introspector = new ExtensibleJavaInterfaceIntrospector(javaFactory, new DefaultJavaInterfaceIntrospectorExtensionPoint());        
    }

    public QName getArtifactType() {
        // Returns the qname of the XML element processed by this processor
        return IMPLEMENTATION_RESOURCE;
    }

    public Class<ResourceImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return ResourceImplementation.class;
    }

    public ResourceImplementation read(XMLStreamReader reader) throws ContributionReadException {
        assert IMPLEMENTATION_RESOURCE.equals(reader.getName());
        
        // Read an <implementation.resource> element
        try {
            // Read the directory attribute. This is where the sample
            // CRUD implementation will persist resources.
            String location = reader.getAttributeValue(null, "location");

            // Create an initialize the resource implementationmodel
            ResourceImplementation implementation = new ResourceImplementation(assemblyFactory, javaFactory, introspector);
            implementation.setLocation(location);
            implementation.setUnresolved(true);
            
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && IMPLEMENTATION_RESOURCE.equals(reader.getName())) {
                    break;
                }
            }
            
            return implementation;
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    public void resolve(ResourceImplementation impl, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the resource directory location
        DeployedArtifact artifact = contributionFactory.createDeployedArtifact();
        artifact.setURI(impl.getLocation());
        DeployedArtifact resolved = resolver.resolveModel(DeployedArtifact.class, artifact);
        if (resolved.getLocation() != null) {
            try {
                impl.setLocationURL(new URL(resolved.getLocation()));
                impl.setUnresolved(false);
            } catch (IOException e) {
                throw new ContributionResolveException(e);
            }
        }
    }

    public void write(ResourceImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
}
