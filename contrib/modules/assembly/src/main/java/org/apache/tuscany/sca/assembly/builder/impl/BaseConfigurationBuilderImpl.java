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

package org.apache.tuscany.sca.assembly.builder.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.AutomaticBinding;
import org.apache.tuscany.sca.assembly.builder.ComponentPreProcessor;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.IntentAttachPointType;

/**
 * Base class for Builder implementations that handles configuration.
 *
 * @version $Rev$ $Date$
 */
public abstract class BaseConfigurationBuilderImpl {
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    private static final String BINDING_SCA = "binding.sca";
    private static final QName BINDING_SCA_QNAME = new QName(SCA10_NS, BINDING_SCA);

    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private Monitor monitor;
    private InterfaceContractMapper interfaceContractMapper;
    private SCADefinitions policyDefinitions;
    private DocumentBuilderFactory documentBuilderFactory;
    private TransformerFactory transformerFactory;

    protected BaseConfigurationBuilderImpl(AssemblyFactory assemblyFactory,
                                             SCABindingFactory scaBindingFactory,
                                             DocumentBuilderFactory documentBuilderFactory,
                                             TransformerFactory transformerFactory,
                                             InterfaceContractMapper interfaceContractMapper,
                                             SCADefinitions policyDefinitions,
                                             Monitor monitor) {
        this.assemblyFactory = assemblyFactory;
        this.scaBindingFactory = scaBindingFactory;
        this.documentBuilderFactory = documentBuilderFactory;
        this.transformerFactory = transformerFactory;
        this.interfaceContractMapper = interfaceContractMapper;
        this.policyDefinitions = policyDefinitions;
        this.monitor = monitor;
    }

    /**
     * Configure components in the composite.
     * 
     * @param composite
     * @param problems
     */
    protected void configureComponents(Composite composite) throws CompositeBuilderException {
        configureComponents(composite, null);
        configureSourcedProperties(composite, null);
        //configureBindingURIs(composite, null, null);
    }

    /**
     * Configure components in the composite.
     * 
     * @param composite
     * @param uri
     * @param problems
     */
    private void configureComponents(Composite composite, String uri) {
        String parentURI = uri;

        // Process nested composites recursively
        for (Component component : composite.getComponents()) {

            // Initialize component URI
            String componentURI;
            if (parentURI == null) {
                componentURI = component.getName();
            } else {
                componentURI = URI.create(parentURI + '/').resolve(component.getName()).toString();
            }
            component.setURI(componentURI);

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // Process nested composite
                configureComponents((Composite)implementation, componentURI);
            }
        }

        // Initialize service bindings
        List<Service> compositeServices = composite.getServices();
        for (Service service : compositeServices) {
            // Set default binding names 
            
            // Create default SCA binding
            if (service.getBindings().isEmpty()) {
                SCABinding scaBinding = createSCABinding();
                service.getBindings().add(scaBinding);
            }
/*
            // Initialize binding names and URIs
            for (Binding binding : service.getBindings()) {
                
                // Binding name defaults to the service name
                if (binding.getName() == null) {
                    binding.setName(service.getName());
                }
            }
            
            if (service.getCallback() != null) {
                for (Binding binding : service.getCallback().getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(service.getName());
                    }
                }
            }
*/
        }

        // Initialize reference bindings
        for (Reference reference : composite.getReferences()) {
            // Create default SCA binding
            if (reference.getBindings().isEmpty()) {
                SCABinding scaBinding = createSCABinding();
                reference.getBindings().add(scaBinding);
            }
/*
            // Set binding names
            for (Binding binding : reference.getBindings()) {
                if (binding.getName() == null) {
                    binding.setName(reference.getName());
                }
            }

            if (reference.getCallback() != null) {
                for (Binding binding : reference.getCallback().getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(reference.getName());
                    }
                }
            }
*/
        }

        // Initialize all component services and references
        Map<String, Component> components = new HashMap<String, Component>();
        for (Component component : composite.getComponents()) {

            // Index all components and check for duplicates
            if (components.containsKey(component.getName())) {
                error("DuplicateComponentName", component, composite.getName().toString(), component.getName());
            } else {
                components.put(component.getName(), component);
            }

            // Propagate the autowire flag from the composite to components
            if (component.getAutowire() == null) {
                component.setAutowire(composite.getAutowire());
            }

            if (component.getImplementation() instanceof ComponentPreProcessor) {
                ((ComponentPreProcessor)component.getImplementation()).preProcess(component);
            }

            // Index properties, services and references
            Map<String, Service> services = new HashMap<String, Service>();
            Map<String, Reference> references = new HashMap<String, Reference>();
            Map<String, Property> properties = new HashMap<String, Property>();
            indexImplementationPropertiesServicesAndReferences(component,
                                                               services,
                                                               references,
                                                               properties);

            // Index component services, references and properties
            // Also check for duplicates
            Map<String, ComponentService> componentServices =
                new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences =
                new HashMap<String, ComponentReference>();
            Map<String, ComponentProperty> componentProperties =
                new HashMap<String, ComponentProperty>();
            indexComponentPropertiesServicesAndReferences(component,
                                                          componentServices,
                                                          componentReferences,
                                                          componentProperties);

            // Reconcile component services/references/properties and
            // implementation services/references and create component
            // services/references/properties for the services/references
            // declared by the implementation
            reconcileServices(component, services, componentServices);
            reconcileReferences(component, references, componentReferences);
            reconcileProperties(component, properties, componentProperties);

            // Configure or create callback services for component's references
            // with callbacks
            configureCallbackServices(component, componentServices);

            // Configure or create callback references for component's services
            // with callbacks
            configureCallbackReferences(component, componentReferences);

            // Create self references to the component's services
//            if (!(component.getImplementation() instanceof Composite)) {
//                createSelfReferences(component);
//            }

            // Initialize service bindings
            for (ComponentService componentService : component.getServices()) {

                // Create default SCA binding
                if (componentService.getBindings().isEmpty()) {
                    SCABinding scaBinding = createSCABinding();
                    componentService.getBindings().add(scaBinding);
                }
/*
                // Set binding names
                for (Binding binding : componentService.getBindings()) {
                    
                    // Binding name defaults to the service name
                    if (binding.getName() == null) {
                        binding.setName(componentService.getName());
                    }
                }
                if (componentService.getCallback() != null) {
                    for (Binding binding : componentService.getCallback().getBindings()) {
                        if (binding.getName() == null) {
                            binding.setName(componentService.getName());
                        }
                    }
                }
*/
            }

            // Initialize reference bindings
            for (ComponentReference componentReference : component.getReferences()) {

                // Create default SCA binding
                if (componentReference.getBindings().isEmpty()) {
                    SCABinding scaBinding = createSCABinding();
                    componentReference.getBindings().add(scaBinding);
                }
/*
                // Set binding names
                for (Binding binding : componentReference.getBindings()) {
                    if (binding.getName() == null) {
                        binding.setName(componentReference.getName());
                    }
                }
                if (componentReference.getCallback() != null) {
                    for (Binding binding : componentReference.getCallback().getBindings()) {
                        if (binding.getName() == null) {
                            binding.setName(componentReference.getName());
                        }
                    }
                }
*/
            }
        }
    }

    /**
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void warning(String message, Object model, String... messageParameters) {
        if (monitor != null) {
            Problem problem = new ProblemImpl(this.getClass().getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }
    
    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, String... messageParameters) {
        if (monitor != null) {
            Problem problem = new ProblemImpl(this.getClass().getName(), "assembly-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    /**
     * Reconcile component properties and the properties defined by the
     * component type.
     * 
     * @param component
     * @param properties
     * @param componentProperties
     * @param problems
     */
    private void reconcileProperties(Component component,
                                     Map<String, Property> properties,
                                     Map<String, ComponentProperty> componentProperties) {

        // Connect component properties to their properties
        for (ComponentProperty componentProperty : component.getProperties()) {
            Property property = properties.get(componentProperty.getName());
            if (property != null) {
                componentProperty.setProperty(property);
            } else {
                warning("PropertyNotFound", component, component.getName(), componentProperty.getName());
            }
        }

        // Create component properties for all properties
        if (component.getImplementation() != null) {
            for (Property property : component.getImplementation().getProperties()) {
                if (!componentProperties.containsKey(property.getName())) {
                    ComponentProperty componentProperty = assemblyFactory.createComponentProperty();
                    componentProperty.setName(property.getName());
                    componentProperty.setMany(property.isMany());
                    componentProperty.setXSDElement(property.getXSDElement());
                    componentProperty.setXSDType(property.getXSDType());
                    componentProperty.setProperty(property);
                    component.getProperties().add(componentProperty);
                }
            }
        }

        // Reconcile component properties and their properties
        for (ComponentProperty componentProperty : component.getProperties()) {
            Property property = componentProperty.getProperty();
            if (property != null) {

                // Check that a component property does not override the
                // mustSupply attribute
                if (!property.isMustSupply() && componentProperty.isMustSupply()) {
                    warning("PropertyMustSupplyIncompatible", component, component.getName(), componentProperty.getName());
                }

                // Default to the mustSupply attribute specified on the property
                if (!componentProperty.isMustSupply())
                    componentProperty.setMustSupply(property.isMustSupply());

                // Default to the value specified on the property
                if (componentProperty.getValue() == null) {
                    componentProperty.setValue(property.getValue());
                }
                
                // Override the property value for the composite
                if(component.getImplementation() instanceof Composite) {
                    property.setValue(componentProperty.getValue());
                }

                // Check that a value is supplied
                if (componentProperty.getValue() == null && property.isMustSupply()) {
                    warning("PropertyMustSupplyNull", component, component.getName(), componentProperty.getName());
                }

                // Check that a a component property does not override the
                // many attribute
                if (!property.isMany() && componentProperty.isMany()) {

                    warning("PropertyOverrideManyAttribute", component, component.getName(), componentProperty.getName());
                }

                // Default to the many attribute defined on the property
                componentProperty.setMany(property.isMany());

                // Default to the type and element defined on the property
                if (componentProperty.getXSDType() == null) {
                    componentProperty.setXSDType(property.getXSDType());
                }
                if (componentProperty.getXSDElement() == null) {
                    componentProperty.setXSDElement(property.getXSDElement());
                }

                // Check that a type or element are specified
                if (componentProperty.getXSDElement() == null && componentProperty.getXSDType() == null) {
                    warning("NoTypeForComponentProperty", component, component.getName(), componentProperty.getName());
                }
            }
        }
    }

    /**
     * Reconcile component references with the references defined on the
     * component type.
     * 
     * @param component
     * @param references
     * @param componentReferences
     * @param problems
     */
    private void reconcileReferences(Component component,
                                     Map<String, Reference> references,
                                     Map<String, ComponentReference> componentReferences) {

        // Connect each component reference to the corresponding reference
        for (ComponentReference componentReference : component.getReferences()) {
            if (componentReference.getReference() != null || componentReference.isCallback()) {
                continue;
            }
            Reference reference = references.get(componentReference.getName());
            if (reference != null) {
                componentReference.setReference(reference);
            } else {
                if (!componentReference.getName().startsWith("$self$.")) {
                    error("ReferenceNotFound", component, component.getName(), componentReference.getName());
                }
            }
        }

        // Create a component reference for each reference
        if (component.getImplementation() != null) {
            for (Reference reference : component.getImplementation().getReferences()) {
                if (!componentReferences.containsKey(reference.getName())) {
                    ComponentReference componentReference =
                        assemblyFactory.createComponentReference();
                    componentReference.setIsCallback(reference.isCallback());
                    componentReference.setName(reference.getName());
                    componentReference.setReference(reference);
                    component.getReferences().add(componentReference);
                }
            }
        }

        // Reconcile each component reference with its reference
        for (ComponentReference componentReference : component.getReferences()) {
            Reference reference = componentReference.getReference();
            if (reference != null) {
                // Reconcile multiplicity
                if (componentReference.getMultiplicity() != null) {
                    if (!ReferenceConfigurationUtil.isValidMultiplicityOverride(reference.getMultiplicity(),
                                                                   componentReference
                                                                       .getMultiplicity())) {
                        warning("ReferenceIncompatibleMultiplicity", component, component.getName(), componentReference.getName());
                    }
                } else {
                    componentReference.setMultiplicity(reference.getMultiplicity());
                }

                // Reconcile interface
                InterfaceContract interfaceContract = reference.getInterfaceContract();
                if (componentReference.getInterfaceContract() != null) {
                    if (interfaceContract != null && !componentReference.getInterfaceContract().equals(reference
                        .getInterfaceContract())) {
                        if (!interfaceContractMapper.isCompatible(componentReference.getInterfaceContract(),
                                                                  interfaceContract)) {
                            warning("ReferenceIncompatibleComponentInterface", component, component.getName(), componentReference.getName());
                        }
                    }
                } else {
                    componentReference.setInterfaceContract(interfaceContract);
                }

                // Reconcile bindings 
                if (componentReference.getBindings().isEmpty()) {
                    componentReference.getBindings().addAll(reference.getBindings());
                }
                
                // Reconcile callback bindings
                if (componentReference.getCallback() == null) {
                    componentReference.setCallback(reference.getCallback());
                    if (componentReference.getCallback() == null) {
                        // Create an empty callback to avoid null check
                        componentReference.setCallback(assemblyFactory.createCallback());
                    }

                } else if (componentReference.getCallback().getBindings().isEmpty() && reference
                    .getCallback() != null) {
                    componentReference.getCallback().getBindings().addAll(reference.getCallback()
                        .getBindings());
                }
                
                // Propagate autowire setting from the component
                if (componentReference.getAutowire() == null) {
                    componentReference.setAutowire(component.getAutowire());
                }

                // Reconcile targets
                if (componentReference.getTargets().isEmpty()) {
                    componentReference.getTargets().addAll(reference.getTargets());
                }
            }
        }
    }

    /**
     * Reconcile component services and services defined on the component type.
     * 
     * @param component
     * @param services
     * @param componentServices
     * @param problems
     */
    private void reconcileServices(Component component,
                                   Map<String, Service> services,
                                   Map<String, ComponentService> componentServices) {

        // Connect each component service to the corresponding service
        for (ComponentService componentService : component.getServices()) {
            if (componentService.getService() != null || componentService.isCallback()) {
                continue;
            }
            Service service = services.get(componentService.getName());
            if (service != null) {
                componentService.setService(service);
            } else {
                warning("ServiceNotFoundForComponentService", component, component.getName(), componentService.getName());
            }
        }

        // Create a component service for each service
        if (component.getImplementation() != null) {
            for (Service service : component.getImplementation().getServices()) {
                if (!componentServices.containsKey(service.getName())) {
                    ComponentService componentService = assemblyFactory.createComponentService();
                    componentService.setIsCallback(service.isCallback());
                    String name = service.getName();
                    componentService.setName(name);
                    componentService.setService(service);
                    component.getServices().add(componentService);
                    componentServices.put(name, componentService);
                }
            }
        }

        //Reconcile each component service with its service
        for (ComponentService componentService : component.getServices()) {
            Service service = componentService.getService();
            if (service != null) {
                // Reconcile interface
                InterfaceContract interfaceContract = service.getInterfaceContract();
                if (componentService.getInterfaceContract() != null) {
                    if (interfaceContract != null && !componentService.getInterfaceContract().equals(interfaceContract)) {
                        if (!interfaceContractMapper.isCompatible(componentService.getInterfaceContract(),
                                                                  interfaceContract)) {
                            warning("ServiceIncompatibleComponentInterface", component, component.getName(), componentService.getName());
                        }
                    }
                } else {
                    componentService.setInterfaceContract(interfaceContract);
                }

                // Reconcile bindings
                if (componentService.getBindings().isEmpty()) {
                    componentService.getBindings().addAll(service.getBindings());
                }
                
                // Reconcile callback bindings
                if (componentService.getCallback() == null) {
                    componentService.setCallback(service.getCallback());
                    if (componentService.getCallback() == null) {
                        // Create an empty callback to avoid null check
                        componentService.setCallback(assemblyFactory.createCallback());
                    }
                } else if (componentService.getCallback().getBindings().isEmpty() && service
                    .getCallback() != null) {
                    componentService.getCallback().getBindings().addAll(service.getCallback()
                        .getBindings());
                }
            }
        }
    }

    private void indexComponentPropertiesServicesAndReferences(Component component,
                                                               Map<String, ComponentService> componentServices,
                                                               Map<String, ComponentReference> componentReferences,
                                                               Map<String, ComponentProperty> componentProperties) {
        for (ComponentService componentService : component.getServices()) {
            if (componentServices.containsKey(componentService.getName())) {
                warning("DuplicateComponentServiceName", component, component.getName(), componentService.getName());
            } else {
                componentServices.put(componentService.getName(), componentService);
            }
        }
        for (ComponentReference componentReference : component.getReferences()) {
            if (componentReferences.containsKey(componentReference.getName())) {
                warning("DuplicateComponentReferenceName", component, component.getName(), componentReference.getName());
            } else {
                componentReferences.put(componentReference.getName(), componentReference);
            }
        }
        for (ComponentProperty componentProperty : component.getProperties()) {
            if (componentProperties.containsKey(componentProperty.getName())) {
                warning("DuplicateComponentPropertyName", component, component.getName(), componentProperty.getName());
            } else {
                componentProperties.put(componentProperty.getName(), componentProperty);
            }
        }

    }

    private void indexImplementationPropertiesServicesAndReferences(Component component,
                                                                    Map<String, Service> services,
                                                                    Map<String, Reference> references,
                                                                    Map<String, Property> properties) {
        // First check that the component has a resolved implementation
        Implementation implementation = component.getImplementation();
        if (implementation == null) {
            // A component must have an implementation
            warning("NoComponentImplementation", component, component.getName());

        } else if (implementation.isUnresolved()) {

            // The implementation must be fully resolved
            warning("UnresolvedComponentImplementation", component, component.getName(), implementation.getURI());

        } else {

            // Index properties, services and references, also check for
            // duplicates
            for (Property property : implementation.getProperties()) {
                if (properties.containsKey(property.getName())) {
                    warning("DuplicateImplementationPropertyName", component, component.getName(), property.getName());
                } else {
                    properties.put(property.getName(), property);
                }
            }
            for (Service service : implementation.getServices()) {
                if (services.containsKey(service.getName())) {
                    warning("DuplicateImplementationServiceName", component, component.getName(), service.getName());
                } else {
                    services.put(service.getName(), service);
                }
            }
            for (Reference reference : implementation.getReferences()) {
                if (references.containsKey(reference.getName())) {
                    warning("DuplicateImplementationReferenceName", component, component.getName(), reference.getName());
                } else {
                    references.put(reference.getName(), reference);
                }
            }
        }

    }

    /**
     * For all the references with callbacks, create a corresponding callback
     * service.
     * 
     * @param component
     */
    private void configureCallbackServices(Component component,
                                           Map<String, ComponentService> componentServices) {
        for (ComponentReference reference : component.getReferences()) {
            if (reference.getInterfaceContract() != null && // can be null in
                                                            // unit tests
            reference.getInterfaceContract().getCallbackInterface() != null) {
                ComponentService service =
                    componentServices.get(reference.getName());
                if (service == null) {
                    service = createCallbackService(component, reference);
                }
                if (reference.getCallback() != null) {
                    if (service.getBindings().isEmpty()) {
                        service.getBindings().addAll(reference.getCallback().getBindings());
                    }
                }
                reference.setCallbackService(service);
            }
        }
    }

    /**
     * Create a callback service for a component reference
     * 
     * @param component
     * @param reference
     */
    private ComponentService createCallbackService(Component component, ComponentReference reference) {
        ComponentService componentService = assemblyFactory.createComponentService();
        componentService.setIsCallback(true);
        componentService.setName(reference.getName());
        try {
            InterfaceContract contract =
                (InterfaceContract)reference.getInterfaceContract().clone();
            contract.setInterface(contract.getCallbackInterface());
            contract.setCallbackInterface(null);
            componentService.setInterfaceContract(contract);
        } catch (CloneNotSupportedException e) {
            // will not happen
        }
        Reference implReference = reference.getReference();
        if (implReference != null) {
            Service implService = assemblyFactory.createService();
            implService.setName(implReference.getName());
            try {
                InterfaceContract implContract =
                    (InterfaceContract)implReference.getInterfaceContract().clone();
                implContract.setInterface(implContract.getCallbackInterface());
                implContract.setCallbackInterface(null);
                implService.setInterfaceContract(implContract);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            componentService.setService(implService);
        }
        component.getServices().add(componentService);
        return componentService;
    }

    /**
     * For all the services with callbacks, create a corresponding callback
     * reference.
     * 
     * @param component
     */
    private void configureCallbackReferences(Component component,
                                             Map<String, ComponentReference> componentReferences) {
        for (ComponentService service : component.getServices()) {
            if (service.getInterfaceContract() != null && // can be null in
                                                            // unit tests
            service.getInterfaceContract().getCallbackInterface() != null) {
                ComponentReference reference =
                    componentReferences.get(service.getName());
                if (reference == null) {
                    reference = createCallbackReference(component, service);
                }
                if (service.getCallback() != null) {
                    if (reference.getBindings().isEmpty()) {
                        reference.getBindings().addAll(service.getCallback().getBindings());
                    }
                }
                service.setCallbackReference(reference);
            }
        }
    }

    /**
     * Create a callback reference for a component service
     * 
     * @param component
     * @param service
     */
    private ComponentReference createCallbackReference(Component component, ComponentService service) {
        ComponentReference componentReference = assemblyFactory.createComponentReference();
        componentReference.setIsCallback(true);
        componentReference.setName(service.getName());
        try {
            InterfaceContract contract = (InterfaceContract)service.getInterfaceContract().clone();
            contract.setInterface(contract.getCallbackInterface());
            contract.setCallbackInterface(null);
            componentReference.setInterfaceContract(contract);
        } catch (CloneNotSupportedException e) {
            // will not happen
        }
        Service implService = service.getService();
        if (implService != null) {
            Reference implReference = assemblyFactory.createReference();
            implReference.setName(implService.getName());
            try {
                InterfaceContract implContract =
                    (InterfaceContract)implService.getInterfaceContract().clone();
                implContract.setInterface(implContract.getCallbackInterface());
                implContract.setCallbackInterface(null);
                implReference.setInterfaceContract(implContract);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            componentReference.setReference(implReference);
        }
        component.getReferences().add(componentReference);
        return componentReference;
    }

    /**
     * @param composite
     */
    private void configureSourcedProperties(Composite composite, List<ComponentProperty> propertySettings) {
        // Resolve properties
        Map<String, Property> compositeProperties = new HashMap<String, Property>();
        ComponentProperty componentProperty = null;
        for (Property p : composite.getProperties()) {
            componentProperty = getComponentPropertyByName(p.getName(), propertySettings);
            if (componentProperty != null) {
                compositeProperties.put(p.getName(), componentProperty);
            } else {
                compositeProperties.put(p.getName(), p);
            }
        }
    
        for (Component component : composite.getComponents()) {
            try {
                PropertyConfigurationUtil.sourceComponentProperties(compositeProperties, component,
                                                                    documentBuilderFactory, transformerFactory);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Implementation impl = component.getImplementation();
            if (impl instanceof Composite) {
                configureSourcedProperties((Composite)impl, component.getProperties());
            }
        }
    }

    private ComponentProperty getComponentPropertyByName(String propertyName, List<ComponentProperty> properties) {
        if (properties != null) {
            for (ComponentProperty aProperty : properties) {
                if (aProperty.getName().equals(propertyName)) {
                    return aProperty;
                }
            }
        }
        return null;
    }
    
    private SCABinding createSCABinding() {
        SCABinding scaBinding = scaBindingFactory.createSCABinding();
        
        // mark the bindings that are added automatically so that they can 
        // be disregarded for overriding purposes
        if (scaBinding instanceof AutomaticBinding){
            ((AutomaticBinding)scaBinding).setIsAutomatic(true);
        }
        
        if ( policyDefinitions != null ) {
            for ( IntentAttachPointType attachPointType : policyDefinitions.getBindingTypes() ) {
                if ( attachPointType.getName().equals(BINDING_SCA_QNAME)) {
                    ((IntentAttachPoint)scaBinding).setType(attachPointType);
                }
            }
        }
        
        return scaBinding;
    }

    /**
     * Called by CompositeBindingURIBuilderImpl
     *  
     * @param composite the composite to be configured
     */
    protected void configureBindingURIsAndNames(Composite composite) throws CompositeBuilderException {
        configureBindingURIs(composite, null, null);
        configureBindingNames(composite);
    }

    /**
     * Fully resolve the binding URIs based on available information. This includes information
     * from the ".composite" files, from resources associated with the binding, e.g. WSDL files, 
     * from any associated policies and from the default information for each binding type.
     *  
     * @param composite the composite to be configured
     * @param defaultBindings list of default binding configurations
     */
    protected void configureBindingURIs(Composite composite, List<Binding> defaultBindings) throws CompositeBuilderException {
        configureBindingURIs(composite, null, defaultBindings);
    }
       
     /**
      * Fully resolve the binding URIs based on available information. This includes information
      * from the ".composite" files, from resources associated with the binding, e.g. WSDL files, 
      * from any associated policies and from the default information for each binding type.
      * 
      * NOTE: This method repeats some of the processing performed by the configureComponents()
      *       method above.  The duplication is needed because NodeConfigurationServiceImpl
      *       calls this method without previously calling configureComponents().  In the
      *       normal builder sequence used by CompositeBuilderImpl, both of these methods
      *       are called.
      *
      * TODO: Share the URL calculation algorithm with the configureComponents() method above
      *       although keeping the configureComponents() methods signature as is because when
      *       a composite is actually build in a node the node default information is currently
      *       available
      *  
      * @param composite the composite to be configured
      * @param uri the path to the composite provided through any nested composite component implementations
      * @param defaultBindings list of default binding configurations
      */
    private void configureBindingURIs(Composite composite, String uri, List<Binding> defaultBindings) throws CompositeBuilderException {
        
        String parentComponentURI = uri;
        
        // Process nested composites recursively
        for (Component component : composite.getComponents()) {

            // Initialize component URI
            String componentURI;
            if (parentComponentURI == null) {
                componentURI = component.getName();
            } else {
                componentURI = URI.create(parentComponentURI + '/').resolve(component.getName()).toString();
            }
            component.setURI(componentURI);

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // Process nested composite
                configureBindingURIs((Composite)implementation, componentURI, defaultBindings);
            }
        }  
        
        // Initialize composite service binding URIs
        List<Service> compositeServices = composite.getServices();
        for (Service service : compositeServices) {
            // Set default binding names 
            
            // Create default SCA binding
            if (service.getBindings().isEmpty()) {
                SCABinding scaBinding = createSCABinding();
                service.getBindings().add(scaBinding);
            }
    
            // Initialize binding names and URIs
            for (Binding binding : service.getBindings()) {  
                constructBindingName(service, binding);
                constructBindingURI(parentComponentURI, composite, service, binding, defaultBindings);
            }
        }
        
        // Initialize component service binding URIs
        for (Component component : composite.getComponents()) {
            
            // Index properties, services and references
            Map<String, Service> services = new HashMap<String, Service>();
            Map<String, Reference> references = new HashMap<String, Reference>();
            Map<String, Property> properties = new HashMap<String, Property>();
            indexImplementationPropertiesServicesAndReferences(component,
                                                               services,
                                                               references,
                                                               properties);

            // Index component services, references and properties
            // Also check for duplicates
            Map<String, ComponentService> componentServices =
                new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences =
                new HashMap<String, ComponentReference>();
            Map<String, ComponentProperty> componentProperties =
                new HashMap<String, ComponentProperty>();
            indexComponentPropertiesServicesAndReferences(component,
                                                          componentServices,
                                                          componentReferences,
                                                          componentProperties);

            // Reconcile component services/references/properties and
            // implementation services/references and create component
            // services/references/properties for the services/references
            // declared by the implementation
            reconcileServices(component, services, componentServices);
            reconcileReferences(component, references, componentReferences);
            reconcileProperties(component, properties, componentProperties);
            
            for (ComponentService service : component.getServices()) {
    
                // Create default SCA binding
                if (service.getBindings().isEmpty()) {
                    SCABinding scaBinding = createSCABinding();
                    service.getBindings().add(scaBinding);
                }
    
                // Initialize binding names and URIs
                for (Binding binding : service.getBindings()) {
                    
                    constructBindingName(service, binding);
                    constructBindingURI(component, service, binding, defaultBindings);
                }
            } 
        }
    }

    /**
     * Add default names for callback bindings and reference bindings.  Needs to be
     * separate from configureBindingURIs() because configureBindingURIs() is called
     * by NodeConfigurationServiceImpl as well as by CompositeBuilderImpl.
     */
    private void configureBindingNames(Composite composite) {
        
        // Process nested composites recursively
        for (Component component : composite.getComponents()) {

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {

                // Process nested composite
                configureBindingNames((Composite)implementation);
            }
        }  
        
        // Initialize composite service callback binding names
        for (Service service : composite.getServices()) {

            if (service.getCallback() != null) {
                for (Binding binding : service.getCallback().getBindings()) {
                    constructBindingName(service, binding);
                }
            }
        }
        
        // Initialize composite reference binding names
        for (Reference reference : composite.getReferences()) {

            for (Binding binding : reference.getBindings()) {  
                constructBindingName(reference, binding);
            }

            if (reference.getCallback() != null) {
                for (Binding binding : reference.getCallback().getBindings()) {
                    constructBindingName(reference, binding);
                }
            }
        }
        
        // Initialize component service and reference binding names
        for (Component component : composite.getComponents()) {

            // Initialize component service callback binding names
            for (ComponentService service : component.getServices()) {

                if (service.getCallback() != null) {
                    for (Binding binding : service.getCallback().getBindings()) {
                        constructBindingName(service, binding);
                    }
                }
            } 
        
            // Initialize component reference binding names
            for (ComponentReference reference : component.getReferences()) {

                // Initialize binding names
                for (Binding binding : reference.getBindings()) {  
                    constructBindingName(reference, binding);
                }

                if (reference.getCallback() != null) {
                    for (Binding binding : reference.getCallback().getBindings()) {
                        constructBindingName(reference, binding);
                    }
                }
            }
        }
    }
    
    /**
     * If a binding name is not provided by the user, construct it based on the service
     * or reference name
     * 
     * @param contract the service or reference
     * @param binding
     */
    private void constructBindingName(Contract contract, Binding binding) {
        
        // set the default binding name if one is required        
        // if there is no name on the binding then set it to the service or reference name 
        if (binding.getName() == null){
            binding.setName(contract.getName());
        }
            
        // Check that multiple bindings do not have the same name
        for (Binding otherBinding : contract.getBindings()) {
            if (otherBinding == binding) {
                // Skip the current binding
                continue;
            }
            if (binding.getClass() != otherBinding.getClass()) {
                // Look for a binding of the same type
                continue;
            }
            if (binding.getName().equals(otherBinding.getName())) {
                warning(contract instanceof Service ? "MultipleBindingsForService" : "MultipleBindingsForReference",
                        binding, contract.getName(), binding.getName());
            }
        }
    }

    /**
     * URI construction for composite bindings based on Assembly Specification section 1.7.2, This method
     * assumes that the component URI part of the binding URI is formed from the part to the 
     * composite in question and just calls the generic constructBindingURI method with this 
     * information
     * 
     * @param parentComponentURI
     * @param composite
     * @param service
     * @param binding
     * @param defaultBindings
     */
    private void constructBindingURI(String parentComponentURI, Composite composite, Service service, Binding binding, List<Binding> defaultBindings) 
    throws CompositeBuilderException{
        // This is a composite service so there is no component to provide a component URI
        // The path to this composite (through nested composites) is used.
        boolean includeBindingName = composite.getServices().size() != 1;
        constructBindingURI(parentComponentURI, service, binding, includeBindingName, defaultBindings);
    }

     /**
      * URI construction for component bindings based on Assembly Specification section 1.7.2. This method
      * calculates the component URI part based on component information before calling the generic
      * constructBindingURI method
      *
      * @param component the component that holds the service
      * @param service the service that holds the binding
      * @param binding the binding for which the URI is being constructed
      * @param defaultBindings the list of default binding configurations
      */
    private void constructBindingURI(Component component, Service service, Binding binding, List<Binding> defaultBindings)
        throws CompositeBuilderException{
        boolean includeBindingName = component.getServices().size() != 1;
        constructBindingURI(component.getURI(), service, binding, includeBindingName, defaultBindings);
    }
            
    /**
     * Generic URI construction for bindings based on Assembly Specification section 1.7.2
     * 
     * @param componentURIString the string version of the URI part that comes from the component name
     * @param service the service in question
     * @param binding the binding for which the URI is being constructed
     * @param includeBindingName when set true the serviceBindingURI part should be used
     * @param defaultBindings the list of default binding configurations
     * @throws CompositeBuilderException
     */
    private void constructBindingURI(String componentURIString, Service service, Binding binding, boolean includeBindingName, List<Binding> defaultBindings) 
        throws CompositeBuilderException{
        
        try {
            // calculate the service binding URI
            URI bindingURI;
            if (binding.getURI() != null){
                bindingURI = new URI(binding.getURI());

                // if the user has provided an absolute binding URI then use it
                if (bindingURI.isAbsolute()){
                    binding.setURI(bindingURI.toString());
                    return;
                }
            } else {
                bindingURI = null;
            }
            
            // Get the service binding name
            URI bindingName;
            if (binding.getName() != null) {
                bindingName = new URI(binding.getName());
            } else {
                bindingName = new URI("");
            }
            
            // calculate the component URI  
            URI componentURI;
            if (componentURIString != null) {
                componentURI = new URI(addSlashToPath(componentURIString));
            } else {
                componentURI = null;
            }
            
            // if the user has provided an absolute component URI then use it
            if (componentURI != null && componentURI.isAbsolute()){
                binding.setURI(constructBindingURI(null, componentURI, bindingURI, includeBindingName, bindingName));
                return;
            }         
            
            // calculate the base URI
            
            // get the protocol for this binding/URI
/* some code that allows binding specific code to run. Being discussed on ML            
            BindingURICalculator uriCalculator = bindingURICalcualtorExtensionPoint.getBindingURICalculator(binding);
            
            if  (uriCalculator != null){
                String protocol = uriCalculator.getProtocol(binding);
                
                // find the default binding with the right protocol
                Binding defaultBinding = nodeInfo.getBindingDefault(binding, protocol);
                
                if (defaultBinding != null){
                    baseURI = new URI(defaultBinding.getURI());
                } else {
                    baseURI = null;
                }
                
            } else {
                baseURI = null;
            }
*/
            // as a simpler alternative to the above commented out code. 
            URI baseURI = null;
            if (defaultBindings != null) {
                for (Binding defaultBinding : defaultBindings){
                    if (binding.getClass() == defaultBinding.getClass()){
                        baseURI = new URI(addSlashToPath(defaultBinding.getURI()));
                        break;
                    }
                }
            }
            
            binding.setURI(constructBindingURI(baseURI, componentURI, bindingURI, includeBindingName, bindingName));
        } catch (URISyntaxException ex) {
            error("URLSyntaxException", binding, componentURIString, service.getName(), binding.getName());
        }      
    }
    
    /**
     * Use to ensure that URI paths end in "/" as here we want to maintain the
     * last path element of an base URI when other URI are resolved against it. This is
     * not the default behaviour of URI resolution as defined in RFC 2369
     * 
     * @param path the path string to which the "/" is to be added
     * @return the resulting path with a "/" added if it not already there
     */
    private static String addSlashToPath(String path){
        if (path.endsWith("/") || path.endsWith("#")){
            return path;
        } else {
            return path + "/";
        }
    }
    
    /**
     * Concatenate binding URI parts together based on Assembly Specification section 1.7.2
     * 
     * @param baseURI the base of the binding URI
     * @param componentURI the middle part of the binding URI derived from the component name
     * @param bindingURI the end part of the binding URI
     * @param includeBindingName when set true the binding name part should be used
     * @param bindingName the binding name
     * @return the resulting URI as a string
     */
    private static String constructBindingURI(URI baseURI, URI componentURI, URI bindingURI, boolean includeBindingName, URI bindingName){        
        String uriString;
        
        if (baseURI == null) {
            if (componentURI == null){
                if (bindingURI != null ) {
                    uriString = bindingURI.toString();
                } else {
                    uriString = bindingName.toString();
                }
            } else {
                if (bindingURI != null ) {
                    uriString = componentURI.resolve(bindingURI).toString();
                } else {
                    if (includeBindingName) {
                        uriString = componentURI.resolve(bindingName).toString();
                    } else {
                        uriString = componentURI.toString();
                    }
                }
            }
        } else {
            if (componentURI == null) {
                if (bindingURI != null ) {
                    uriString = basedURI(baseURI, bindingURI).toString();
                } else {
                    if (includeBindingName) {
                        uriString = basedURI(baseURI, bindingName).toString();
                    } else {
                        uriString = baseURI.toString();
                    }
                }
            } else {
                if (bindingURI != null ) {
                    uriString = basedURI(baseURI, componentURI.resolve(bindingURI)).toString();
                } else {
                    if (includeBindingName) {
                        uriString = basedURI(baseURI, componentURI.resolve(bindingName)).toString();
                    } else {
                        uriString = basedURI(baseURI, componentURI).toString();
                    }
                }
            }
        }
        
        // tidy up by removing any trailing "/"
        if (uriString.endsWith("/")){
            uriString = uriString.substring(0, uriString.length()-1);   
        }
        
        URI uri = URI.create(uriString);
        if (!uri.isAbsolute()) {
            uri = URI.create("/").resolve(uri);
        }
        return uri.toString();
    }

    /**
     * Combine a URI with a base URI.
     * 
     * @param baseURI
     * @param uri
     * @return
     */
    private static URI basedURI(URI baseURI, URI uri) {
        if (uri.getScheme() != null) {
            return uri;
        }
        String str = uri.toString();
        if (str.startsWith("/")) {
            str = str.substring(1);
        }
        return URI.create(baseURI.toString() + str).normalize();
    }

}
