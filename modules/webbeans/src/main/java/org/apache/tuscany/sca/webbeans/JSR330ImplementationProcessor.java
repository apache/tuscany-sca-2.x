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
package org.apache.tuscany.sca.webbeans;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Implements a StAX artifact processor for Web implementations.
 */
public class JSR330ImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<JSR330Implementation> {
    
    private AssemblyFactory assemblyFactory;
    
    public JSR330ImplementationProcessor(ExtensionPointRegistry extensionPoints) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
    }

    public QName getArtifactType() {
        return JSR330Implementation.TYPE;
    }

    public Class<JSR330Implementation> getModelType() {
        return JSR330Implementation.class;
    }

    public JSR330Implementation read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        
        JSR330Implementation implementation = new JSR330Implementation();
        implementation.setUnresolved(true);

        String location = getString(reader, "location");
        if (location == null) {
            Monitor monitor = context.getMonitor();
            Problem problem = monitor.createProblem(this.getClass().getName(),
                                      "webbeans-validation-messages",
                                      Severity.ERROR,
                                      reader,
                                      "LocationAttributeMissing");
            monitor.problem(problem);
        }

        implementation.setLocation(location);

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && JSR330Implementation.TYPE.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void write(JSR330Implementation implementation, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        
        // Write <implementation.jsr330>
        writeStart(writer, JSR330Implementation.TYPE.getNamespaceURI(), JSR330Implementation.TYPE.getLocalPart(),
                   new XAttr("location", implementation.getLocation()));
        
        writeEnd(writer);
    }

    public void resolve(JSR330Implementation implementation, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        
        // Resolve the component type
        String uri = implementation.getURI();
        if (uri != null) {
            ComponentType componentType = assemblyFactory.createComponentType();
            componentType.setURI("jsr330.componentType");
            componentType = resolver.resolveModel(ComponentType.class, componentType, context);
            if (!componentType.isUnresolved()) {
                
                // Initialize the implementation's services, references and properties
                implementation.getServices().addAll(componentType.getServices());
                implementation.getReferences().addAll(componentType.getReferences());
                implementation.getProperties().addAll(componentType.getProperties());
            }
        }
        implementation.setUnresolved(false);
    }

}
