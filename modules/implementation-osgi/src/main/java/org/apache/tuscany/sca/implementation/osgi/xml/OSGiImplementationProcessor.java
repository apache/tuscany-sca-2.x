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

import java.net.URI;
import java.net.URL;
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
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescription;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptions;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptionsFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
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
    private static final String BUNDLE_COMPONENT_TYPE = "OSGI-INF/sca/bundle.componentType";
    private static final String COMPONENT_TYPE_HEADER = "SCA-ComponentType";

    private AssemblyFactory assemblyFactory;
    private ServiceDescriptionsFactory serviceDescriptionsFactory;
    private OSGiImplementationFactory osgiImplementationFactory;
    private JavaInterfaceFactory javaInterfaceFactory;
    
    private ExtensionPointRegistry registry;
    private StAXArtifactProcessor artifactProcessor;

    protected OSGiImplementationProcessor(FactoryExtensionPoint modelFactories) {
        this.serviceDescriptionsFactory = modelFactories.getFactory(ServiceDescriptionsFactory.class);
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.osgiImplementationFactory = modelFactories.getFactory(OSGiImplementationFactory.class);
        this.javaInterfaceFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
    }

    public OSGiImplementationProcessor(ExtensionPointRegistry registry, StAXArtifactProcessor processor) {
        this(registry.getExtensionPoint(FactoryExtensionPoint.class));
        this.artifactProcessor = processor;
        this.registry = registry;
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

    public OSGiImplementation read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
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

    public void resolve(OSGiImplementation impl, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {

        if (impl == null || !impl.isUnresolved())
            return;
        Monitor monitor = context.getMonitor();
        impl.setUnresolved(false);

        BundleContext bundleContext = OSGiImplementationActivator.getBundleContext();
        if (bundleContext == null) {
            // FIXME: What if the OSGi is not started
            return;
        }
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
            error(monitor, "CouldNotLocateOSGiBundle", impl, impl.getBundleSymbolicName());
            //throw new ContributionResolveException("Could not locate OSGi bundle " +
            //impl.getBundleSymbolicName());
            return;
        }

        try {
            if (introspect(impl, resolver, context, bundle)) {
                return;
            }
        } catch (ContributionReadException e) {
            throw new ContributionResolveException(e);
        }

        // The bundle may be different from the current contribution
        ComponentType componentType = assemblyFactory.createComponentType();
        // Try to find a bundle.componentType for the target bundle
        componentType.setURI("OSGI-INF/sca/" + bundle.getSymbolicName() + "/bundle.componentType");
        componentType.setUnresolved(true);
        componentType = resolver.resolveModel(ComponentType.class, componentType, context);
        if (componentType.isUnresolved()) {
            // Create a new instance to prevent it being treated as reentry
            // See org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver.resolveModel(Class<T>, T)
            componentType = assemblyFactory.createComponentType();
            // Try a generic one
            componentType.setURI(BUNDLE_COMPONENT_TYPE);
            componentType = resolver.resolveModel(ComponentType.class, componentType, context);
        }
        if (componentType.isUnresolved()) {
            // Try to derive it from the service descriptions
            if (!deriveFromServiceDescriptions(impl, resolver, context)) {
                error(monitor, "MissingComponentTypeFile", impl, componentType.getURI());
            }
            //throw new ContributionResolveException("missing .componentType side file " + ctURI);
            return;
        } else {
            mergeFromComponentType(impl, componentType, resolver, context);
        }
    }

    private boolean deriveFromServiceDescriptions(OSGiImplementation impl, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        // FIXME: How to find the RFC 119 service descriptions in the contribution and
        // derive the SCA component type from them?
        ServiceDescriptions descriptions = serviceDescriptionsFactory.createServiceDescriptions();
        descriptions = resolver.resolveModel(ServiceDescriptions.class, descriptions, context);
        if (descriptions != null && !descriptions.isEmpty()) {
            ComponentType ct = assemblyFactory.createComponentType();
            int index = 0;
            for (ServiceDescription ds : descriptions) {
                for (String i : ds.getInterfaces()) {
                    Class<?> cls = getJavaClass(resolver, i, context);
                    JavaInterface javaInterface;
                    try {
                        javaInterface = javaInterfaceFactory.createJavaInterface(cls);
                    } catch (InvalidInterfaceException e) {
                        throw new ContributionResolveException(e);
                    }
                    Reference reference = assemblyFactory.createReference();
                    JavaInterfaceContract contract = javaInterfaceFactory.createJavaInterfaceContract();
                    contract.setInterface(javaInterface);
                    reference.setInterfaceContract(contract);
                    String refName = (String)ds.getProperties().get(OSGiProperty.SCA_REFERENCE);
                    if (refName == null) {
                        refName = "ref" + (index++);
                    }
                    reference.setName(refName);
                    reference.setUnresolved(false);
                    ct.getReferences().add(reference);
                }
            }
            mergeFromComponentType(impl, ct, resolver, context);
            return true;
        }
        return false;
    }

    private void mergeFromComponentType(OSGiImplementation impl, ComponentType componentType, ModelResolver resolver, ProcessorContext context) {
        List<Service> services = componentType.getServices();
        for (Service service : services) {
            Interface interfaze = service.getInterfaceContract().getInterface();
            if (interfaze instanceof JavaInterface) {
                JavaInterface javaInterface = (JavaInterface)interfaze;
                if (javaInterface.getJavaClass() == null) {
                    javaInterface.setJavaClass(getJavaClass(resolver, javaInterface.getName(), context));
                }
                if (service.getInterfaceContract().getCallbackInterface() instanceof JavaInterface) {
                    JavaInterface callbackInterface =
                        (JavaInterface)service.getInterfaceContract().getCallbackInterface();
                    if (callbackInterface.getJavaClass() == null) {
                        callbackInterface.setJavaClass(getJavaClass(resolver, callbackInterface.getName(), context));
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
                    javaInterface.setJavaClass(getJavaClass(resolver, javaInterface.getName(), context));
                }
                impl.getReferences().add(reference);
            } else
                impl.getReferences().add(reference);
        }

        List<Property> properties = componentType.getProperties();
        for (Property property : properties) {
            impl.getProperties().add(property);
        }
    }

    private Class<?> getJavaClass(ModelResolver resolver, String className, ProcessorContext context) {
        ClassReference ref = new ClassReference(className);
        ref = resolver.resolveModel(ClassReference.class, ref, context);
        return ref.getJavaClass();
    }

    public void write(OSGiImplementation model, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException,
        XMLStreamException {
        String ns = IMPLEMENTATION_OSGI.getNamespaceURI();
        writer.writeStartElement(ns, IMPLEMENTATION_OSGI.getLocalPart());
        writer.writeAttribute(BUNDLE_SYMBOLICNAME, model.getBundleSymbolicName());
        if (model.getBundleVersion() != null) {
            writer.writeAttribute(BUNDLE_VERSION, model.getBundleVersion());
        }
        writer.writeEndElement();
    }

    private boolean introspect(OSGiImplementation implementation, ModelResolver resolver, ProcessorContext context, Bundle bundle)
        throws ContributionReadException, ContributionResolveException {
        String componentTypeFile = (String)bundle.getHeaders().get(COMPONENT_TYPE_HEADER);
        if (componentTypeFile == null) {
            componentTypeFile = BUNDLE_COMPONENT_TYPE;
        }
        URL url = bundle.getEntry(componentTypeFile);
        if (url != null) {
            URLArtifactProcessorExtensionPoint processors =
                registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            URLArtifactProcessor<ComponentType> processor = processors.getProcessor(ComponentType.class);
            ComponentType componentType = processor.read(null, URI.create(BUNDLE_COMPONENT_TYPE), url, context);
            artifactProcessor.resolve(componentType, resolver, context);
            mergeFromComponentType(implementation, componentType, resolver, context);
            return true;
        }
        return false;
    }

}
