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

package org.apache.tuscany.sca.osgi.remoteserviceadmin.impl;

import static org.apache.tuscany.sca.assembly.Base.SCA11_TUSCANY_NS;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SCA_BINDINGS;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SERVICE_EXPORTED_INTENTS;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SERVICE_EXPORTED_INTENTS_EXTRA;
import static org.apache.tuscany.sca.implementation.osgi.OSGiProperty.SERVICE_EXPORTED_INTERFACES;
import static org.apache.tuscany.sca.osgi.remoteserviceadmin.impl.OSGiHelper.getStringArray;
import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.framework.Constants.SERVICE_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.apache.tuscany.sca.implementation.osgi.SCAConfig;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.osgi.service.discovery.impl.LocalDiscoveryService;
import org.apache.tuscany.sca.osgi.service.discovery.impl.LocalDiscoveryService.ExtenderConfiguration;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.oasisopen.sca.ServiceRuntimeException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Introspect an OSGi Service to create an SCA composite that contains a single component with
 * implementation.osgi
 */
public class EndpointIntrospector {
    // private BundleContext context;
    private AssemblyFactory assemblyFactory;
    private ContributionFactory contributionFactory;
    private OSGiImplementationFactory implementationFactory;
    private PolicyFactory policyFactory;
    // private ExtensionPointRegistry registry;
    private FactoryExtensionPoint factories;
    private ModelResolverExtensionPoint modelResolvers;
    // private StAXArtifactProcessor<Composite> compositeProcessor;
    private JavaInterfaceFactory javaInterfaceFactory;
    // private Deployer deployer;
    private ServiceTracker discoveryTracker;

    /**
     * @param intentName
     * @return
     */
    private static QName getQName(String intentName) {
        QName qname;
        if (intentName.startsWith("{")) {
            int i = intentName.indexOf('}');
            if (i != -1) {
                qname = new QName(intentName.substring(1, i), intentName.substring(i + 1));
            } else {
                throw new IllegalArgumentException("Invalid intent: " + intentName);
            }
        } else {
            // Default to SCA namespace
            qname = new QName(Base.SCA11_NS, intentName);
        }
        return qname;
    }

    /**
     * @param context TODO
     * @param registry
     */
    public EndpointIntrospector(BundleContext context, ExtensionPointRegistry registry, ServiceTracker discoveryTracker) {
        super();
        // this.context = context;
        this.discoveryTracker = discoveryTracker;
        // this.registry = registry;
        this.factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.modelResolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);
//        this.compositeProcessor =
//            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class).getProcessor(Composite.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.contributionFactory = factories.getFactory(ContributionFactory.class);
        this.policyFactory = factories.getFactory(PolicyFactory.class);
        this.implementationFactory = factories.getFactory(OSGiImplementationFactory.class);
        this.javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
        // this.deployer = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(Deployer.class);
    }

    private Intent getIntent(String intent) {
        QName name = getQName(intent);
        Intent i = policyFactory.createIntent();
        i.setName(name);
        return i;
    }

    private List<Intent> getIntents(String[] intents) {
        if (intents == null || intents.length == 0) {
            return Collections.emptyList();
        }
        List<Intent> intentList = new ArrayList<Intent>();
        for (String i : intents) {
            Intent intent = getIntent(i);
            if (intent != null) {
                intentList.add(intent);
            }
        }
        return intentList;
    }

    /**
     * Any property in the map overrides the service reference properties, regardless of
     * case. That is, if the map contains a key then it will override any case variant
     * of this key in the Service Reference.<p>
     * If the map contains the objectClass or service. id property key in any case
     * variant, then these properties must not override the Service Reference’s value. This 
     * implies that the map can provide the service.exported. interfaces, property allowing 
     * the Topology Manager to export any registered service, also services not specifically 
     * marked to be exported.
     * @param reference
     * @param props
     * @return
     */
    private Map<String, Object> getProperties(ServiceReference reference, Map<String, Object> props) {
        String[] names = reference.getPropertyKeys();
        Map<String, Object> properties = new HashMap<String, Object>();
        if (names != null) {
            for (String name : names) {
                properties.put(name, reference.getProperty(name));
            }
        }
        if (props != null) {
            // Create a map of names (key = lowcase name, value = name)
            Map<String, String> nameMap = new HashMap<String, String>();
            if (names != null) {
                for (String name : names) {
                    nameMap.put(name.toLowerCase(), name);
                }
            }
            for (Map.Entry<String, Object> p : props.entrySet()) {
                if (Constants.OBJECTCLASS.equalsIgnoreCase(p.getKey())) {
                    throw new IllegalArgumentException(Constants.OBJECTCLASS + " property cannot be overridden.");
                } else if (Constants.SERVICE_ID.equalsIgnoreCase(p.getKey())) {
                    throw new IllegalArgumentException(Constants.SERVICE_ID + " property cannot be overridden.");
                }
                String key = nameMap.get(p.getKey().toLowerCase());
                if (key != null) {
                    properties.put(key, p.getValue());
                } else {
                    properties.put(p.getKey(), p.getValue());
                }
            }
        }
        return properties;
    }

    /**
     * Parse the Stringp[] to support values that are separated by comma
     * @param interfaces
     * @return
     */
    private String[] parse(String[] interfaces) {
        if (interfaces == null) {
            return null;
        }
        List<String> names = new ArrayList<String>();
        for (String i : interfaces) {
            String[] parts = i.split(",");
            for (String p : parts) {
                names.add(p.trim());
            }
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * Introspect a local OSGi Service represented by the ServiceReference to create 
     * an SCA service with the required intents and bindings 
     * @param reference The service reference for a local OSGi service 
     * @param props Addiontal properties
     * @return An SCA contribution with a deployable composite for the SCA service
     * @throws Exception
     */
    public Contribution introspect(ServiceReference reference, Map<String, Object> props) throws Exception {
        Bundle bundle = reference.getBundle();
        Map<String, Object> properties = getProperties(reference, props);
        Long sid = (Long)reference.getProperty(SERVICE_ID);

        String[] requiredIntents = getStringArray(properties.get(SERVICE_EXPORTED_INTENTS));
        List<Intent> intents = getIntents(requiredIntents);
        String[] requiredIntentsExtra = getStringArray(properties.get(SERVICE_EXPORTED_INTENTS_EXTRA));
        List<Intent> extraIntents = getIntents(requiredIntentsExtra);
        Set<Intent> allIntents = new HashSet<Intent>(intents);
        allIntents.addAll(extraIntents);

        String[] bindingNames = getStringArray(properties.get(SCA_BINDINGS));
        Collection<Binding> bindings = loadBindings(bindingNames);

        String[] remoteInterfaces = getStringArray(reference.getProperty(SERVICE_EXPORTED_INTERFACES));
        if (remoteInterfaces == null || remoteInterfaces.length > 0 && "*".equals(remoteInterfaces[0])) {
            remoteInterfaces = getStringArray(reference.getProperty(OBJECTCLASS));
        } else {
            remoteInterfaces = parse(remoteInterfaces);
            String[] objectClasses = getStringArray(reference.getProperty(OBJECTCLASS));
            Set<String> objectClassSet = new HashSet<String>(Arrays.asList(objectClasses));
            if (!objectClassSet.containsAll(Arrays.asList(remoteInterfaces))) {
                throw new IllegalArgumentException(
                                                   "The exported interfaces are not a subset of the types" + " listed in the objectClass service property from the Service Reference");
            }
        }

        Contribution contribution = generateContribution(bundle, sid, remoteInterfaces, bindings, allIntents);
        return contribution;
    }

    /**
     * Generate a contribution that contains the composite for the exported service
     * @param bundle The OSGi bundle
     * @param sid The service id
     * @param remoteInterfaces 
     * @param bindings
     * @param allIntents
     * @return
     * @throws ClassNotFoundException
     * @throws InvalidInterfaceException
     */
    private Contribution generateContribution(Bundle bundle,
                                              Long sid,
                                              String[] remoteInterfaces,
                                              Collection<Binding> bindings,
                                              Set<Intent> allIntents) throws ClassNotFoundException,
        InvalidInterfaceException {
        String id = "osgi.service." + UUID.randomUUID();
        Composite composite = assemblyFactory.createComposite();
        composite.setName(new QName(SCA11_TUSCANY_NS, id));

        Component component = assemblyFactory.createComponent();
        component.setName(id);

        composite.getComponents().add(component);

        OSGiImplementation implementation = implementationFactory.createOSGiImplementation();

        implementation.setBundle(bundle);
        component.setImplementation(implementation);
        implementation.setUnresolved(false);

        OSGiProperty serviceID = implementationFactory.createOSGiProperty();
        serviceID.setName(SERVICE_ID);
        // The service.id is Long
        serviceID.setValue(String.valueOf(sid));

        for (String intf : remoteInterfaces) {
            Service service = assemblyFactory.createService();
            JavaInterfaceContract interfaceContract = createJavaInterfaceContract(bundle, intf);
            String name = intf.substring(intf.lastIndexOf('.') + 1);
            service.setName(name);
            service.setInterfaceContract(interfaceContract);

            service.getExtensions().add(serviceID);

            implementation.getServices().add(service);

            ComponentService componentService = assemblyFactory.createComponentService();
            componentService.setName(service.getName());
            component.getServices().add(componentService);
            componentService.setService(service);
        }

        for (ComponentService componentService : component.getServices()) {
            componentService.getRequiredIntents().addAll(allIntents);
            componentService.getBindings().addAll(bindings);
        }

        // FIXME: Should we scan the owning bundle to create the SCA contribution?
        Contribution contribution = createContribution(bundle, id, composite);
        return contribution;
    }

    private Contribution createContribution(Bundle bundle, String id, Composite composite) {
        Contribution contribution = contributionFactory.createContribution();
        contribution.setClassLoader(OSGiHelper.createBundleClassLoader(bundle));
        contribution.setURI("urn:" + id);
        contribution.setLocation(bundle.getEntry("/").toString());
        contribution.getDeployables().add(composite);
        ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, factories);
        contribution.setModelResolver(modelResolver);
        // compositeProcessor.resolve(composite, modelResolver, new ProcessorContext(registry));
        contribution.setUnresolved(true);
        return contribution;
    }

    /**
     * @param bundle
     * @param endpoint
     * @return
     * @throws Exception
     */
    public Contribution introspect(Bundle bundle, EndpointDescription endpoint) throws Exception {
        Collection<Binding> bindings = Collections.emptyList();
        Collection<String> interfaces = Collections.emptyList();
        Collection<Intent> intents = Collections.emptyList();
        Endpoint ep = (Endpoint)endpoint.getProperties().get(Endpoint.class.getName());
        if (ep != null) {
            bindings = Collections.singletonList(ep.getBinding());
            interfaces = Collections.singletonList(((JavaInterface)ep.getComponentServiceInterfaceContract().getInterface()).getName());
            intents = ep.getRequiredIntents();
        } else {
            Map<String, Object> properties = endpoint.getProperties();
            interfaces = endpoint.getInterfaces();
            String[] requiredIntents = getStringArray(properties.get(SERVICE_EXPORTED_INTENTS));
            intents = getIntents(requiredIntents);

            String[] bindingNames = getStringArray(properties.get(SCA_BINDINGS));
            bindings = loadBindings(bindingNames);
        }

        Contribution contribution = generateContribution(bundle, interfaces, bindings, intents);
        return contribution;
    }

    private Contribution generateContribution(Bundle bundle,
                                              Collection<String> remoteInterfaces,
                                              Collection<Binding> bindings,
                                              Collection<Intent> intents) throws ClassNotFoundException,
        InvalidInterfaceException, ContributionResolveException {
        String id = "osgi.reference." + UUID.randomUUID();
        Composite composite = assemblyFactory.createComposite();
        composite.setName(new QName(Base.SCA11_TUSCANY_NS, id));

        Component component = assemblyFactory.createComponent();
        component.setName(id);
        // component.setAutowire(Boolean.TRUE);

        composite.getComponents().add(component);

        OSGiImplementation implementation = implementationFactory.createOSGiImplementation();

        implementation.setBundle(bundle);
        component.setImplementation(implementation);
        implementation.setUnresolved(false);

        int count = 0;
        for (String intf : remoteInterfaces) {
            Reference reference = assemblyFactory.createReference();
            JavaInterfaceContract interfaceContract = createJavaInterfaceContract(bundle, intf);

            reference.setName("ref" + (count++));
            reference.setInterfaceContract(interfaceContract);

            implementation.getReferences().add(reference);

            ComponentReference componentReference = assemblyFactory.createComponentReference();
            componentReference.setName(reference.getName());
            component.getReferences().add(componentReference);
            componentReference.setReference(reference);
            componentReference.setWiredByImpl(true);
        }

        for (ComponentReference componentReference : component.getReferences()) {
            componentReference.getRequiredIntents().addAll(intents);
            componentReference.getBindings().addAll(bindings);
        }

        Contribution contribution = createContribution(bundle, id, composite);
        return contribution;
    }

    private JavaInterfaceContract createJavaInterfaceContract(Bundle bundle, String intf)
        throws ClassNotFoundException, InvalidInterfaceException {
        JavaInterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
        Class<?> interfaceClass = bundle.loadClass(intf);
        JavaInterface javaInterface = javaInterfaceFactory.createJavaInterface();
        // [rfeng] For OSGi, the interfaces should be marked as remotable
        javaInterface.setRemotable(true);
        // [rfeng] We need to mark the interface to be remotable before the createJavaInterface() is called 
        javaInterfaceFactory.createJavaInterface(javaInterface, interfaceClass);
        interfaceContract.setInterface(javaInterface);
        if (javaInterface.getCallbackClass() != null) {
            JavaInterface callbackInterface = javaInterfaceFactory.createJavaInterface(javaInterface.getCallbackClass());
            callbackInterface.setRemotable(true);
            interfaceContract.setCallbackInterface(callbackInterface);
        }
        return interfaceContract;
    }

    private Collection<Binding> loadBindings(String[] qnames) throws IOException, ContributionReadException,
        XMLStreamException {
        if (qnames == null || qnames.length == 0) {
            return Collections.emptyList();
        }
        QName[] bindingNames = new QName[qnames.length];
        int index = 0;
        for (String name : qnames) {
            bindingNames[index++] = getQName(name);
        }

        LocalDiscoveryService discoveryService = (LocalDiscoveryService)discoveryTracker.getService();

        Map<QName, Binding> bindingMap = new HashMap<QName, Binding>();
        if (discoveryService != null) {
            for (ExtenderConfiguration config : discoveryService.getConfigurations()) {
                for (SCAConfig sc : config.getSCAConfigs()) {
                    for (QName bindingName : bindingNames) {
                        if (sc.getTargetNamespace().equals(bindingName.getNamespaceURI())) {
                            for (Binding binding : sc.getBindings()) {
                                if (bindingName.getLocalPart().equals(binding.getName())) {
                                    bindingMap.put(bindingName, binding);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (QName bindingName : bindingNames) {
            if (!bindingMap.containsKey(bindingName)) {
                throw new ServiceRuntimeException("Binding cannot be resolved: " + bindingName);
            }
        }
        return bindingMap.values();
    }

}
