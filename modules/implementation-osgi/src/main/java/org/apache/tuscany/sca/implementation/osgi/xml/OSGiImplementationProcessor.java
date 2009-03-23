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
package org.apache.tuscany.sca.implementation.osgi.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.apache.tuscany.sca.implementation.osgi.OSGiImplementation.BUNDLE_SYMBOLICNAME;
import static org.apache.tuscany.sca.implementation.osgi.OSGiImplementation.BUNDLE_VERSION;
import static org.apache.tuscany.sca.implementation.osgi.OSGiImplementation.IMPLEMENTATION_OSGI;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptionsFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * 
 * Process an <implementation.osgi/> element in a component definition. An instance of
 * OSGiImplementation is created.
 * Also associates the component type file with the implementation.
 *
 * @version $Rev$ $Date$
 */
public class OSGiImplementationProcessor implements StAXArtifactProcessor<OSGiImplementation> {
    private AssemblyFactory assemblyFactory;
    private ServiceDescriptionsFactory serviceDescriptionsFactory;
    private OSGiImplementationFactory osgiImplementationFactory;
    private Monitor monitor;

    public OSGiImplementationProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.monitor = monitor;
        this.serviceDescriptionsFactory = modelFactories.getFactory(ServiceDescriptionsFactory.class);
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.osgiImplementationFactory = modelFactories.getFactory(OSGiImplementationFactory.class);
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
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "impl-osgi-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    public QName getArtifactType() {
        return IMPLEMENTATION_OSGI;
    }

    public Class<OSGiImplementation> getModelType() {
        return OSGiImplementation.class;
    }

    public OSGiImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        assert IMPLEMENTATION_OSGI.equals(reader.getName());

        String bundleSymbolicName = reader.getAttributeValue(null, BUNDLE_SYMBOLICNAME);
        String bundleVersion = reader.getAttributeValue(null, BUNDLE_VERSION);

        OSGiImplementation implementation = osgiImplementationFactory.createOSGiImplementation();
        implementation.setBundleSymbolicName(bundleSymbolicName);
        implementation.setBundleVersion(bundleVersion);

        implementation.setUnresolved(true);

        // Skip to the end of <implementation.osgi>
        while (reader.hasNext()) {
            int next = reader.next();
            switch (next) {
                case START_ELEMENT:
                    break;
                case END_ELEMENT:
                    if (IMPLEMENTATION_OSGI.equals(reader.getName())) {
                        return implementation;
                    }
                    break;
            }
        }
        return implementation;
    }

    public void resolve(OSGiImplementation impl, ModelResolver resolver) throws ContributionResolveException {

        if (impl == null || !impl.isUnresolved())
            return;

        impl.setUnresolved(false);

        BundleContext bundleContext = OSGiImplementationActivator.getBundleContext();
        Bundle bundle = null;
        for (Bundle b : bundleContext.getBundles()) {
            String sn = b.getSymbolicName();
            String ver = (String)b.getHeaders().get(Constants.BUNDLE_VERSION);
            if (!impl.getBundleSymbolicName().equals(sn)) {
                continue;
            }
            Version v1 = Version.parseVersion(ver);
            Version v2 = Version.parseVersion(impl.getBundleVersion());
            if (v1.equals(v2)) {
                bundle = b;
                break;
            }
        }
        if (bundle != null) {
            impl.setBundle(bundle);
        } else {
            error("CouldNotLocateOSGiBundle", impl, impl.getBundleSymbolicName());
            //throw new ContributionResolveException("Could not locate OSGi bundle " + 
            //impl.getBundleSymbolicName());
            return;
        }

        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setURI("OSGI-INF/sca/bundle.componentType");
        componentType.setUnresolved(true);
        componentType = resolver.resolveModel(ComponentType.class, componentType);
        if (componentType.isUnresolved()) {
            error("MissingComponentTypeFile", impl, componentType.getURI());
            //throw new ContributionResolveException("missing .componentType side file " + ctURI);
            return;
        } else {
            mergeFromComponentType(impl, componentType, resolver);
        }

        // FIXME: How to find the RFC 119 service descriptions in the contribution and 
        // derive the SCA component type from them?
    }

    private void mergeFromComponentType(OSGiImplementation impl, ComponentType componentType, ModelResolver resolver) {
        List<Service> services = componentType.getServices();
        for (Service service : services) {
            Interface interfaze = service.getInterfaceContract().getInterface();
            if (interfaze instanceof JavaInterface) {
                JavaInterface javaInterface = (JavaInterface)interfaze;
                if (javaInterface.getJavaClass() == null) {
                    javaInterface.setJavaClass(getJavaClass(resolver, javaInterface.getName()));
                }
                if (service.getInterfaceContract().getCallbackInterface() instanceof JavaInterface) {
                    JavaInterface callbackInterface =
                        (JavaInterface)service.getInterfaceContract().getCallbackInterface();
                    if (callbackInterface.getJavaClass() == null) {
                        callbackInterface.setJavaClass(getJavaClass(resolver, callbackInterface.getName()));
                    }
                }

                impl.getServices().add(service);
            }
        }

        List<Reference> references = componentType.getReferences();
        for (Reference reference : references) {
            Interface interfaze = reference.getInterfaceContract().getInterface();
            if (interfaze instanceof JavaInterface) {
                JavaInterface javaInterface = (JavaInterface)interfaze;
                if (javaInterface.getJavaClass() == null) {
                    javaInterface.setJavaClass(getJavaClass(resolver, javaInterface.getName()));
                }
                impl.getReferences().add(reference);
            } else
                impl.getReferences().add(reference);
        }

        List<Property> properties = componentType.getProperties();
        for (Property property : properties) {
            impl.getProperties().add(property);
        }
        impl.setConstrainingType(componentType.getConstrainingType());
    }

    private Class<?> getJavaClass(ModelResolver resolver, String className) {
        ClassReference ref = new ClassReference(className);
        ref = resolver.resolveModel(ClassReference.class, ref);
        return ref.getJavaClass();
    }

    public void write(OSGiImplementation model, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        String ns = IMPLEMENTATION_OSGI.getNamespaceURI();
        writer.writeStartElement(ns, IMPLEMENTATION_OSGI.getLocalPart());
        writer.writeAttribute(BUNDLE_SYMBOLICNAME, model.getBundleSymbolicName());
        if (model.getBundleVersion() != null) {
            writer.writeAttribute(BUNDLE_VERSION, model.getBundleVersion());
        }
        writer.writeEndElement();
    }

}
