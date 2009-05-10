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

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.AutomaticBinding;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Base class for Builder implementations 
 *
 * @version $Rev$ $Date$
 */
public abstract class BaseBuilderImpl implements CompositeBuilder {
    protected static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200903";
    protected static final String BINDING_SCA = "binding.sca";
    protected static final QName BINDING_SCA_QNAME = new QName(SCA11_NS, BINDING_SCA);

    protected AssemblyFactory assemblyFactory;
    protected SCABindingFactory scaBindingFactory;
    protected InterfaceContractMapper interfaceContractMapper;
    protected DocumentBuilderFactory documentBuilderFactory;
    protected TransformerFactory transformerFactory;


    protected BaseBuilderImpl(AssemblyFactory assemblyFactory,
                              SCABindingFactory scaBindingFactory,
                              DocumentBuilderFactory documentBuilderFactory,
                              TransformerFactory transformerFactory,
                              InterfaceContractMapper interfaceContractMapper) {
        this.assemblyFactory = assemblyFactory;
        this.scaBindingFactory = scaBindingFactory;
        this.documentBuilderFactory = documentBuilderFactory;
        this.transformerFactory = transformerFactory;
        this.interfaceContractMapper = interfaceContractMapper;
    }
    
    /**
     * Report a warning.
     * 
     * @param monitor
     * @param problems
     * @param message
     * @param model
     */
    protected void warning(Monitor monitor, String message, Object model, String... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }
    
    /**
     * Report a error.
     * 
     * @param monitor
     * @param problems
     * @param message
     * @param model
     */
    protected void error(Monitor monitor, String message, Object model, String... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }  
    
    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
     */
    protected void error(Monitor monitor, String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem = null;
            problem = monitor.createProblem(this.getClass().getName(), "assembly-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }
    }   
       
    /**
     * Index components inside a composite
     * 
     * @param composite
     * @param componentServices

     */
    protected void indexComponents(Composite composite,
                                 Map<String, Component> components) {
        for (Component component : composite.getComponents()) {
            // Index components by name
            components.put(component.getName(), component);
        }    
    }
    
    /**
     * Index services inside a composite
     * 
     * @param composite
     * @param componentServices
     */
    protected void indexServices(Composite composite,
                                 Map<String, ComponentService> componentServices) {

        for (Component component : composite.getComponents()) {
            
            ComponentService nonCallbackService = null;
            int nonCallbackServiceCount = 0;
            
            for (ComponentService componentService : component.getServices()) {                 
                // Index component services by component name / service name
                String uri = component.getName() + '/' + componentService.getName();
                componentServices.put(uri, componentService);
                
                // count how many non-callback there are
                if (!componentService.isCallback()) {                            
                    
                    if (nonCallbackServiceCount == 0) {
                        nonCallbackService = componentService;
                    }
                    nonCallbackServiceCount++;
                }
            }
            if (nonCallbackServiceCount == 1) {
                // If we have a single non callback service, index it by
                // component name as well
                componentServices.put(component.getName(), nonCallbackService);
            }
        }    
    }    
    

    /**
     * Index components, services and references inside a composite.
     * @param composite
     * @param components
     * @param componentServices
     * @param componentReferences
     */
    protected void indexComponentsServicesAndReferences(Composite composite,
                                                        Map<String, Component> components,
                                                        Map<String, ComponentService> componentServices,
                                                        Map<String, ComponentReference> componentReferences) {

        for (Component component : composite.getComponents()) {
            
            // Index components by name
            components.put(component.getName(), component);
            
            ComponentService nonCallbackService = null;
            int nonCallbackServices = 0;
            for (ComponentService componentService : component.getServices()) {
                                  
                // Index component services by component name / service name
                String uri = component.getName() + '/' + componentService.getName();
                componentServices.put(uri, componentService);
                
                // TODO - EPR - $promoted$ no longer used but it doesn't do any harm here
                boolean promotedService = false;
                if (componentService.getName() != null && componentService.getName().indexOf("$promoted$") > -1) {
                    promotedService = true;
                }
                
                // count how many non-callback, non-promoted services there are
                // if there is only one the component name also acts as the service name
                if ((!componentService.isCallback()) && (!promotedService)) {                            
                    
                    // Check how many non callback non-promoted services we have
                    if (nonCallbackServices == 0) {
                        nonCallbackService = componentService;
                    }
                    nonCallbackServices++;
                }

            }
            
            if (nonCallbackServices == 1) {
                // If we have a single non callback service, index it by
                // component name as well
                componentServices.put(component.getName(), nonCallbackService);
            }
            
            // Index references by component name / reference name
            for (ComponentReference componentReference : component.getReferences()) {
                String uri = component.getName() + '/' + componentReference.getName();
                componentReferences.put(uri, componentReference);
            }
        }
    }
    
    protected void indexComponentPropertiesServicesAndReferences(
            Component component,
            Map<String, ComponentService> componentServices,
            Map<String, ComponentReference> componentReferences,
            Map<String, ComponentProperty> componentProperties, Monitor monitor) {
        for (ComponentService componentService : component.getServices()) {
            if (componentServices.containsKey(componentService.getName())) {
                warning(monitor, "DuplicateComponentServiceName", component,
                        component.getName(), componentService.getName());
            } else {
                componentServices.put(componentService.getName(),
                        componentService);
            }
        }
        for (ComponentReference componentReference : component.getReferences()) {
            if (componentReferences.containsKey(componentReference.getName())) {
                warning(monitor, "DuplicateComponentReferenceName", component,
                        component.getName(), componentReference.getName());
            } else {
                componentReferences.put(componentReference.getName(),
                        componentReference);
            }
        }
        for (ComponentProperty componentProperty : component.getProperties()) {
            if (componentProperties.containsKey(componentProperty.getName())) {
                warning(monitor, "DuplicateComponentPropertyName", component,
                        component.getName(), componentProperty.getName());
            } else {
                componentProperties.put(componentProperty.getName(),
                        componentProperty);
            }
        }

    }

    protected void indexImplementationPropertiesServicesAndReferences(
            Component component, Map<String, Service> services,
            Map<String, Reference> references,
            Map<String, Property> properties, Monitor monitor) {
        // First check that the component has a resolved implementation
        Implementation implementation = component.getImplementation();
        if (implementation == null) {
            // A component must have an implementation
            error(monitor, "NoComponentImplementation", component, component.getName());

        } else if (implementation.isUnresolved()) {

            // The implementation must be fully resolved
            error(monitor, "UnresolvedComponentImplementation", component,
                    component.getName(), implementation.getURI());

        } else {

            // Index properties, services and references, also check for
            // duplicates
            for (Property property : implementation.getProperties()) {
                if (properties.containsKey(property.getName())) {
                    warning(monitor, "DuplicateImplementationPropertyName",
                            component, component.getName(), property.getName());
                } else {
                    properties.put(property.getName(), property);
                }
            }
            for (Service service : implementation.getServices()) {
                if (services.containsKey(service.getName())) {
                    warning(monitor, "DuplicateImplementationServiceName",
                            component, component.getName(), service.getName());
                } else {
                    services.put(service.getName(), service);
                }
            }
            for (Reference reference : implementation.getReferences()) {
                if (references.containsKey(reference.getName())) {
                    warning(monitor, "DuplicateImplementationReferenceName",
                            component, component.getName(), reference.getName());
                } else {
                    references.put(reference.getName(), reference);
                }
            }
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
    protected void reconcileProperties(Component component,
                                     Map<String, Property> properties,
                                     Map<String, ComponentProperty> componentProperties,
                                     Monitor monitor) {

        // Connect component properties to their properties
        for (ComponentProperty componentProperty : component.getProperties()) {
            Property property = properties.get(componentProperty.getName());
            if (property != null) {
                componentProperty.setProperty(property);
            } else {
                warning(monitor, "PropertyNotFound", component, component.getName(), componentProperty.getName());
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
                    warning(monitor, "PropertyMustSupplyIncompatible", component, component.getName(), componentProperty.getName());
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
                    warning(monitor, "PropertyMustSupplyNull", component, component.getName(), componentProperty.getName());
                }

                // Check that a a component property does not override the
                // many attribute
                if (!property.isMany() && componentProperty.isMany()) {

                    warning(monitor, "PropertyOverrideManyAttribute", component, component.getName(), componentProperty.getName());
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
                    warning(monitor, "NoTypeForComponentProperty", component, component.getName(), componentProperty.getName());
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
     * @param monitor
     */
    protected void reconcileReferences(Component component,
                                     Map<String, Reference> references,
                                     Map<String, ComponentReference> componentReferences,
                                     Monitor monitor) {

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
                    error(monitor, "ReferenceNotFound", component, component.getName(), componentReference.getName());
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
                        warning(monitor, "ReferenceIncompatibleMultiplicity", component, component.getName(), componentReference.getName());
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
                            warning(monitor, "ReferenceIncompatibleComponentInterface", component, component.getName(), componentReference.getName());
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
     * @param monitor
     */
    protected void reconcileServices(Component component,
                                   Map<String, Service> services,
                                   Map<String, ComponentService> componentServices,
                                   Monitor monitor) {

        // Connect each component service to the corresponding service
        for (ComponentService componentService : component.getServices()) {
            if (componentService.getService() != null || componentService.isCallback()) {
                continue;
            }
            Service service = services.get(componentService.getName());
            if (service != null) {
                componentService.setService(service);
            } else {
                warning(monitor, "ServiceNotFoundForComponentService", component, component.getName(), componentService.getName());
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
                            warning(monitor, "ServiceIncompatibleComponentInterface", component, component.getName(), componentService.getName());
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
    
    protected SCABinding createSCABinding(Definitions definitions) {
        SCABinding scaBinding = scaBindingFactory.createSCABinding();
        
        // mark the bindings that are added automatically so that they can 
        // be disregarded for overriding purposes
        if (scaBinding instanceof AutomaticBinding){
            ((AutomaticBinding)scaBinding).setIsAutomatic(true);
        }
        
        if ( definitions != null ) {
            for ( ExtensionType attachPointType : definitions.getBindingTypes() ) {
                if ( attachPointType.getType().equals(BINDING_SCA_QNAME)) {
                    ((PolicySubject)scaBinding).setType(attachPointType);
                }
            }
        }
        
        return scaBinding;
    }    
}
