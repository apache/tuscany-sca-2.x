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

package org.apache.tuscany.sca.implementation.script.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.script.ScriptImplementation;
import org.apache.tuscany.sca.implementation.script.ScriptImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * ScriptImplementationProcessor
 *
 * @version $Rev$ $Date$
 */
public class ScriptImplementationProcessor implements StAXArtifactProcessor<ScriptImplementation> {
    private static final QName IMPLEMENTATION_SCRIPT = ScriptImplementation.TYPE;

    private AssemblyFactory assemblyFactory;
    private ContributionFactory contributionFactory;
    private ScriptImplementationFactory scriptImplementationFactory;
    
    public ScriptImplementationProcessor(ExtensionPointRegistry registry) {
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        scriptImplementationFactory = modelFactories.getFactory(ScriptImplementationFactory.class);
    }
    
    public QName getArtifactType() {
        return IMPLEMENTATION_SCRIPT;
    }
    
    public Class<ScriptImplementation> getModelType() {
        return ScriptImplementation.class;
    }
    
    public ScriptImplementation read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        
        // Read an <implementation.script> element

        // Create and initialize the script implementation model
        ScriptImplementation implementation = scriptImplementationFactory.createScriptImplementation();
        implementation.setUnresolved(true);

        // Read the script attribute
        String script = reader.getAttributeValue(null, "script");
        if (script != null) {                
            implementation.setScript(script);
        }

        // Read the language attribute
        String language = reader.getAttributeValue(null, "language");
        if (language != null) {                
            implementation.setLanguage(language);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_SCRIPT.equals(reader.getName())) {
                break;
            }
        }

        return implementation;
    }
    
    public void resolve(ScriptImplementation implementation, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        
        // Resolve the script location
        if (implementation.getScript() != null) {
            Artifact artifact = contributionFactory.createArtifact();
            artifact.setURI(implementation.getScript());
            Artifact resolved = resolver.resolveModel(Artifact.class, artifact, context);
            if (resolved.getLocation() != null) {
                implementation.setLocation(resolved.getLocation());
            } else {
                error(context.getMonitor(), "CouldNotResolveScript", resolver, implementation.getScript());
            }
            
            // Resolve the componentType and add the declared properties, references
            // and services to the implementation
            String componentTypeURI = implementation.getScript();
            componentTypeURI = componentTypeURI.substring(0, componentTypeURI.lastIndexOf('.'));
            componentTypeURI += ".componentType";
            ComponentType componentType = assemblyFactory.createComponentType();
            componentType.setUnresolved(true);
            componentType.setURI(componentTypeURI);
            componentType = resolver.resolveModel(ComponentType.class, componentType, context);
            if (!componentType.isUnresolved()) {
                implementation.getProperties().addAll(componentType.getProperties());
                implementation.getReferences().addAll(componentType.getReferences());
                implementation.getServices().addAll(componentType.getServices());
            }
        }
        
        implementation.setUnresolved(false);
    }
    
    public void write(ScriptImplementation implementation, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        
        // Write <implementation.script>
        writer.setPrefix("script", IMPLEMENTATION_SCRIPT.getNamespaceURI());
        writer.writeStartElement(IMPLEMENTATION_SCRIPT.getNamespaceURI(), IMPLEMENTATION_SCRIPT.getLocalPart());
        writer.writeNamespace("script", IMPLEMENTATION_SCRIPT.getNamespaceURI());
        
        if (implementation.getScript() != null) {
            writer.writeAttribute("script", implementation.getScript());
        }
        if (implementation.getLanguage() != null) {
            writer.writeAttribute("language", implementation.getLanguage());
        }
        
        writer.writeEndElement();
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
            Problem problem = monitor.createProblem(this.getClass().getName(), "impl-script-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }    

}
