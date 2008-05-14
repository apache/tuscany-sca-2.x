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
package org.apache.tuscany.sca.implementation.ejb.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.ejb.EJBImplementation;
import org.apache.tuscany.sca.implementation.ejb.EJBImplementationFactory;


/**
 * Implements a StAX artifact processor for EJB implementations.
 *
 * @version $Rev$ $Date$
 */
public class EJBImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<EJBImplementation> {
    private static final QName IMPLEMENTATION_EJB = new QName(Constants.SCA10_NS, "implementation.ejb");
    
    private AssemblyFactory assemblyFactory;
    private EJBImplementationFactory implementationFactory;
    
    public EJBImplementationProcessor(ModelFactoryExtensionPoint modelFactories) {
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        implementationFactory = modelFactories.getFactory(EJBImplementationFactory.class);
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_EJB;
    }

    public Class<EJBImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return EJBImplementation.class;
    }

    public EJBImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        
        // Read an <implementation.ejb> element
        EJBImplementation implementation = implementationFactory.createEJBImplementation();
        implementation.setUnresolved(true);

        // Read the ejb-link attribute
        String ejbLink = getString(reader, "ejb-link");
        if (ejbLink != null) {
            implementation.setEJBLink(ejbLink);
            
            // Set the URI of the component type 
            //implementation.setURI(ejbLink.replace('#', '/'));
            int hashPosition = ejbLink.indexOf('#');
            if ( hashPosition >= 0) {
                implementation.setURI(ejbLink.substring(hashPosition + 1));
            } else {
                implementation.setURI(ejbLink);
            }
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_EJB.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(EJBImplementation implementation, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the component type
        String uri = implementation.getURI();
        if (uri != null) {
            ComponentType componentType = assemblyFactory.createComponentType();
            componentType.setURI(uri + ".componentType");
            componentType = resolver.resolveModel(ComponentType.class, componentType);
            if (!componentType.isUnresolved()) {
                
                // Initialize the implementation's services, references and properties
                implementation.getServices().addAll(componentType.getServices());
                implementation.getReferences().addAll(componentType.getReferences());
                implementation.getProperties().addAll(componentType.getProperties());
            }
        }
        implementation.setUnresolved(false);
    }

    public void write(EJBImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        // Write <implementation.ejb>
        writeStart(writer, IMPLEMENTATION_EJB.getNamespaceURI(), IMPLEMENTATION_EJB.getLocalPart(),
                                 new XAttr("ejb-link", implementation.getEJBLink()));
        
        writeEnd(writer);
    }
}
