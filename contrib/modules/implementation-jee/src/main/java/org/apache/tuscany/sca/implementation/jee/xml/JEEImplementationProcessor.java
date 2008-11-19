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
package org.apache.tuscany.sca.implementation.jee.xml;

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
import org.apache.tuscany.sca.implementation.jee.JEEImplementation;
import org.apache.tuscany.sca.implementation.jee.JEEImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * Implements a StAX artifact processor for JEE implementations.
 * 
 * @version $Rev$ $Date$
 */
public class JEEImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<JEEImplementation> {
    private static final QName IMPLEMENTATION_JEE = new QName(Constants.SCA10_NS, "implementation.jee");
    
    private AssemblyFactory assemblyFactory;
    private JEEImplementationFactory implementationFactory;
    private Monitor monitor;
    
    public JEEImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.implementationFactory = modelFactories.getFactory(JEEImplementationFactory.class);
        this.monitor = monitor;
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_JEE;
    }

    public Class<JEEImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return JEEImplementation.class;
    }

    public JEEImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        
        // Read an <implementation.jee> element
        JEEImplementation implementation = implementationFactory.createJEEImplementation();
        implementation.setUnresolved(true);

        // Read the archive attribute
        String archive = getString(reader, "archive");
        if (archive != null) {
            implementation.setArchive(archive);

            // Set the URI of the component type 
            implementation.setURI(archive);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_JEE.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(JEEImplementation implementation, ModelResolver resolver) throws ContributionResolveException {
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

    public void write(JEEImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        // Write <implementation.jee>
        writeStart(writer, IMPLEMENTATION_JEE.getNamespaceURI(), IMPLEMENTATION_JEE.getLocalPart(),
                   new XAttr("archive", implementation.getArchive()));
        
        writeEnd(writer);
    }
}
