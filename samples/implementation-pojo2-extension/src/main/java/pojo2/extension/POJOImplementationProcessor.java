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
package pojo2.extension;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;

/**
 * Implements a STAX based artifact processor for POJO implementations.
 * 
 * The artifact processor is responsible for processing <implementation.pojo>
 * elements in SCA assembly XML composite files and populating the POJO
 * implementation model, resolving its references to other artifacts in the SCA
 * contribution, and optionally write the model back to SCA assembly XML. 
 */
public class POJOImplementationProcessor implements StAXArtifactProcessor<POJOImplementation> {
    private static final QName IMPLEMENTATION_POJO = new QName("http://pojo", "implementation.pojo");
    
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    
    public POJOImplementationProcessor(ModelFactoryExtensionPoint modelFactories) {
        
        // Get the assembly and Java interface factories as we'll need them to
        // create model objects 
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
    }

    public QName getArtifactType() {
        // Returns the qname of the XML element processed by this processor
        return IMPLEMENTATION_POJO;
    }

    public Class<POJOImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return POJOImplementation.class;
    }

    public POJOImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
    
        // Read an <implementation.pojo> element
        
        // Read the POJO class attribute.
        String className = reader.getAttributeValue(null, "class");

        // Create the POJO implementation model
        POJOImplementation implementation = new POJOImplementation();
        implementation.setPOJOName(className);
        
        // Mark the POJO model unresolved to track the fact that it's not
        // completely initialized, its class is not loaded yet and services
        // and references not initialized either
        implementation.setUnresolved(true);
        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_POJO.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(POJOImplementation implementation, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the POJO implementation
        
        // First resolve its class
        ClassReference classReference = new ClassReference(implementation.getPOJOName());
        classReference = resolver.resolveModel(ClassReference.class, classReference);
        Class pojoClass = classReference.getJavaClass();
        if (pojoClass == null) {
            throw new ContributionResolveException("Class could not be resolved: " + implementation.getPOJOName());
        }
        implementation.setPOJOClass(pojoClass);
        
        // Check to see if we have a .componentType file describing the POJO class
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        componentType.setURI(implementation.getURI() + ".componentType");
        componentType = resolver.resolveModel(ComponentType.class, componentType);
        if (!componentType.isUnresolved()) {
            
            // We have a component type description, merge it into the POJO model
            implementation.getServices().addAll(componentType.getServices());
            implementation.getReferences().addAll(componentType.getReferences());
            implementation.getProperties().addAll(componentType.getProperties());
            
        } else {
            
            // We have no component type description, simply introspect the POJO and
            // create a single Service for it
            Service service = assemblyFactory.createService();
            service.setName(pojoClass.getSimpleName());
            JavaInterface javaInterface;
            try {
                javaInterface = javaFactory.createJavaInterface(pojoClass);
            } catch (InvalidInterfaceException e) {
                throw new ContributionResolveException(e);
            }
            JavaInterfaceContract interfaceContract = javaFactory.createJavaInterfaceContract();
            interfaceContract.setInterface(javaInterface);
            service.setInterfaceContract(interfaceContract);
            implementation.getServices().add(service);
        }
        
        // Mark the implementation resolved now
        implementation.setUnresolved(false);
    }

    public void write(POJOImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
}
