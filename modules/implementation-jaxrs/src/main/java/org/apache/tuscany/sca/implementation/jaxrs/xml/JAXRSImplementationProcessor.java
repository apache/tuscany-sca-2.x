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
package org.apache.tuscany.sca.implementation.jaxrs.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.binding.rest.RESTBindingFactory;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.jaxrs.JAXRSImplementation;
import org.apache.tuscany.sca.implementation.jaxrs.JAXRSImplementationFactory;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Implements a StAX artifact processor for Web implementations.
 */
public class JAXRSImplementationProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<JAXRSImplementation> {
    private static final QName IMPLEMENTATION_JAXRS = JAXRSImplementation.TYPE;

    private AssemblyFactory assemblyFactory;
    private JAXRSImplementationFactory implementationFactory;
    private RESTBindingFactory restBindingFactory;
    private JavaInterfaceFactory javaInterfaceFactory;

    public JAXRSImplementationProcessor(ExtensionPointRegistry extensionPoints) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.implementationFactory = modelFactories.getFactory(JAXRSImplementationFactory.class);
        this.restBindingFactory = modelFactories.getFactory(RESTBindingFactory.class);
        this.javaInterfaceFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_JAXRS;
    }

    public Class<JAXRSImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return JAXRSImplementation.class;
    }

    public JAXRSImplementation read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException,
        XMLStreamException {

        // Read an <implementation.web> element
        JAXRSImplementation implementation = implementationFactory.createJAXRSImplementation();
        implementation.setUnresolved(true);

        String application = reader.getAttributeValue(null, "application");
        if (application != null) {
            implementation.setApplication(application);
        }

        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_JAXRS.equals(reader.getName())) {
                break;
            }
        }

        return implementation;
    }

    public void resolve(JAXRSImplementation implementation, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {

        ClassReference classReference = new ClassReference(implementation.getApplication());
        classReference = resolver.resolveModel(ClassReference.class, classReference, context);
        implementation.setApplicationClass(classReference.getJavaClass());
        implementation.setUnresolved(false);
        
        Application application;
        try {
            application = (Application) implementation.getApplicationClass().newInstance();
        } catch (Exception e) {
            throw new ContributionResolveException(e);
        } 
        
        for(Class<?> rootResourceClass: application.getClasses()) {
            addService(implementation, rootResourceClass);
        }
        for(Object rootResource: application.getSingletons()) {
            addService(implementation, rootResource.getClass());
        }
        
    }

    private void addService(JAXRSImplementation implementation, Class<?> rootResourceClass) {
        Service service = assemblyFactory.createService();
        JavaInterfaceContract contract = javaInterfaceFactory.createJavaInterfaceContract();
        JavaInterface javaInterface;
        try {
            javaInterface = javaInterfaceFactory.createJavaInterface(rootResourceClass);
        } catch (InvalidInterfaceException e) {
            throw new ServiceRuntimeException(e);
        }
        contract.setInterface(javaInterface);
        service.setInterfaceContract(contract);
        RESTBinding binding = restBindingFactory.createRESTBinding();
        // FIXME: The @ApplicationPath is available for JAX-RS 1.1
        // binding.setURI("/");
        Path path = rootResourceClass.getAnnotation(Path.class);
        if (path != null) {
            binding.setURI(path.value());
        }
        service.getBindings().add(binding);
        service.setName(rootResourceClass.getSimpleName());
        implementation.getServices().add(service);
    }

    public void write(JAXRSImplementation implementation, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {

        // Write <implementation.jaxrs>
        writeStart(writer,
                   IMPLEMENTATION_JAXRS.getNamespaceURI(),
                   IMPLEMENTATION_JAXRS.getLocalPart(),
                   new XAttr("application", implementation.getApplication()));

        writeEnd(writer);
    }
}
