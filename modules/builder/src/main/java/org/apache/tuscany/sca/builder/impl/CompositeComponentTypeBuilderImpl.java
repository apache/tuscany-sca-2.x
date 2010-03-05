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
package org.apache.tuscany.sca.builder.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * @version $Rev$ $Date$
 */

// TODO - really implementation.composite component type builder - CompositeComponentTypeBuilder?

public class CompositeComponentTypeBuilderImpl {
    private static final Logger logger = Logger.getLogger(CompositeComponentTypeBuilderImpl.class.getName());

    protected static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";
    protected static final String BINDING_SCA = "binding.sca";
    protected static final QName BINDING_SCA_QNAME = new QName(SCA11_NS, BINDING_SCA);

    private ComponentBuilderImpl componentBuilder;
    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private InterfaceContractMapper interfaceContractMapper;
    private BuilderExtensionPoint builders;

    public CompositeComponentTypeBuilderImpl(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);

        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);

        interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
        builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
    }

    public void setComponentBuilder(ComponentBuilderImpl componentBuilder) {
        this.componentBuilder = componentBuilder;
    }

    /**
     * Calculate the component type for the provided implementation
     * 
     * @param implementation
     * @return component type
     */
    public void createComponentType(Component outerComponent, Composite composite, BuilderContext context) {

        Monitor monitor = context.getMonitor();
        monitor.pushContext("Composite: " + composite.getName().toString());
        
        try {
            // first make sure that each child component has been properly configured based
            // on its own component type
            for (Component component : composite.getComponents()) {
    
                // Check for duplicate component names
                if (component != composite.getComponent(component.getName())) {
                    Monitor.error(monitor, 
                                  this, 
                                  Messages.ASSEMBLY_VALIDATION, 
                                  "DuplicateComponentName", 
                                  composite.getName().toString(), 
                                  component.getName());
                }
    
                // do any work we need to do before we configure the component
                // Anything that needs to be pushed down the promotion
                // hierarchy must be done before we configure the component
    
                // Push down the autowire flag from the composite to components
                if (component.getAutowire() == null) {
                    component.setAutowire(composite.getAutowire());
                }
    
                // configure the component from its component type
                componentBuilder.configureComponentFromComponentType(outerComponent, composite, component, context);
            }
    
            // create the composite component type based on the promoted artifacts
            // from the components that it contains
    
            // index all the components, services and references in the
            // component type so that they are easy to find
            Map<String, Component> components = new HashMap<String, Component>();
            Map<String, ComponentService> componentServices = new HashMap<String, ComponentService>();
            Map<String, ComponentReference> componentReferences = new HashMap<String, ComponentReference>();
            indexComponentsServicesAndReferences(composite, components, componentServices, componentReferences);
    
            // services
            calculateServices(composite, components, componentServices, context);
    
            // references
            calculateReferences(composite, components, componentReferences, context);
    
            // properties
            // Properties on the composite component are unaffected by properties 
            // on child components. Instead child component properties might take their
            // values from composite properties. Hence there is nothing to do here.
            //calculateProperties(composite, components);
        
        } finally {
            monitor.popContext();
        } 
    }

    /**
     * Index components, services and references inside a composite.
     * 
     * @param composite
     * @param components
     * @param componentServices
     * @param componentReferences
     */
    private void indexComponentsServicesAndReferences(Composite composite,
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

                // count how many non-callback services there are
                // if there is only one the component name also acts as the service name
                if (!componentService.isForCallback()) {

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

    /**
     * Connect the services in the component type to the component services that
     * they promote
     * 
     * @param componentType
     * @param component
     */
    private void calculateServices(ComponentType componentType,
                                   Map<String, Component> components,
                                   Map<String, ComponentService> componentServices,
                                   BuilderContext context) {
        
        Monitor monitor = context.getMonitor();

        // Connect this component type's services to the 
        // services from child components which it promotes
        connectPromotedServices(componentType, components, componentServices, monitor);

        // look at each component type service in turn and 
        // calculate its configuration based on OASIS rules
        for (Service service : componentType.getServices()) {
            CompositeService compositeService = (CompositeService)service;
            ComponentService promotedComponentService = compositeService.getPromotedService();

            // promote interface contracts
            calculatePromotedServiceInterfaceContract(compositeService, promotedComponentService, monitor);

            // promote bindings
            calculatePromotedBindings(compositeService, promotedComponentService);

            componentBuilder.policyBuilder.configure(compositeService, context);
        }
    }

    /**
     * Connect the references in the component type to the component references that
     * they promote
     * 
     * @param componentType
     * @param component
     */
    private void calculateReferences(ComponentType componentType,
                                     Map<String, Component> components,
                                     Map<String, ComponentReference> componentReferences, 
                                     BuilderContext context) {
        Monitor monitor = context.getMonitor();
        // Connect this component type's references to the 
        // references from child components which it promotes
        connectPromotedReferences(componentType, components, componentReferences, monitor);

        // look at each component type reference in turn and 
        // calculate its configuration based on OASIS rules
        for (Reference reference : componentType.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences = compositeReference.getPromotedReferences();

            for (ComponentReference promotedComponentReference : promotedReferences) {
                
                // promote multiplicity
                reconcileReferenceMultiplicity(componentType, compositeReference, promotedComponentReference, monitor);

                // promote interface contracts
                calculatePromotedReferenceInterfaceContract(compositeReference, promotedComponentReference, monitor);

                // promote bindings
                // Don't need to promote reference bindings as any lower level binding will
                // already be targeting the correct service without need for promotion
                //calculatePromotedBindings(compositeReference, promotedComponentReference);
            }
            
            componentBuilder.policyBuilder.configure(compositeReference, context);
        }
    }

    /**
     * Connect the services in the component type to the component services that
     * they promote
     * 
     * @param componentType
     * @param component
     */
    private void connectPromotedServices(ComponentType componentType,
                                         Map<String, Component> components,
                                         Map<String, ComponentService> componentServices,
                                         Monitor monitor) {

        for (Service service : componentType.getServices()) {
            // Connect composite (component type) services to the component services 
            // that they promote 
            CompositeService compositeService = (CompositeService)service;
            ComponentService componentService = compositeService.getPromotedService();
            if (componentService != null && componentService.isUnresolved()) {
                // get the name of the promoted component/service
                String promotedComponentName = compositeService.getPromotedComponent().getName();
                String promotedServiceName;
                if (componentService.getName() != null) {
                    if (compositeService.isForCallback()) {
                        // For callbacks the name already has the form "componentName/servicename"
                        promotedServiceName = componentService.getName();
                    } else {
                        promotedServiceName = promotedComponentName + '/' + componentService.getName();
                    }
                } else {
                    promotedServiceName = promotedComponentName;
                }

                // find the promoted service
                ComponentService promotedService = componentServices.get(promotedServiceName);

                if (promotedService != null) {

                    // Point to the resolved component
                    Component promotedComponent = components.get(promotedComponentName);
                    compositeService.setPromotedComponent(promotedComponent);

                    // Point to the resolved component service
                    compositeService.setPromotedService(promotedService);
                } else {
                    Monitor.error(monitor,
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "PromotedServiceNotFound",
                                  ((Composite)componentType).getName().toString(),
                                  promotedServiceName);
                }
            }
        }
    }

    /**
     * Connect the references in the component type to the component references that
     * they promote
     * 
     * @param componentType
     * @param component
     */
    private void connectPromotedReferences(ComponentType componentType,
                                           Map<String, Component> components,
                                           Map<String, ComponentReference> componentReferences,
                                           Monitor monitor) {

        // Connect composite (component type) references to the component references that they promote
        for (Reference reference : componentType.getReferences()) {
            CompositeReference compositeReference = (CompositeReference)reference;
            List<ComponentReference> promotedReferences = compositeReference.getPromotedReferences();
            for (int i = 0, n = promotedReferences.size(); i < n; i++) {
                ComponentReference componentReference = promotedReferences.get(i);
                if (componentReference.isUnresolved()) {
                    String componentReferenceName = componentReference.getName();
                    componentReference = componentReferences.get(componentReferenceName);
                    if (componentReference != null) {
                        // Set the promoted component
                        Component promotedComponent = compositeReference.getPromotedComponents().get(i);
                        promotedComponent = components.get(promotedComponent.getName());
                        compositeReference.getPromotedComponents().set(i, promotedComponent);

                        componentReference.setPromoted(true);

                        // Point to the resolved component reference
                        promotedReferences.set(i, componentReference);
                    } else {
                        Monitor.error(monitor,
                                      this,
                                      Messages.ASSEMBLY_VALIDATION,
                                      "PromotedReferenceNotFound",
                                      ((Composite)componentType).getName().toString(),
                                      componentReferenceName);
                    }
                }
            }
        }
    }

    /**
     * Create a default SCA binding in the case that no binding
     * is specified by the user
     * 
     * @param contract
     * @param definitions
     */
    protected void createSCABinding(Contract contract, Definitions definitions) {

        SCABinding scaBinding = scaBindingFactory.createSCABinding();
        scaBinding.setName(contract.getName());

        if (definitions != null) {
            for (ExtensionType attachPointType : definitions.getBindingTypes()) {
                if (attachPointType.getType().equals(BINDING_SCA_QNAME)) {
                    ((PolicySubject)scaBinding).setExtensionType(attachPointType);
                }
            }
        }

        contract.getBindings().add(scaBinding);
        contract.setOverridingBindings(false);
    }

    /**
     * The following methods implement rules that the OASIS specification defined explicitly
     * to control how configuration from a component type is inherited by a component
     */

    /**
     * Interface contract from higher in the implementation hierarchy takes precedence. 
     * When it comes to checking compatibility the top level service interface is a 
     * subset of the promoted service interface so treat the top level interface as
     * the source
     * 
     * @param topContract the top contract 
     * @param bottomContract the bottom contract
     */
    private void calculatePromotedServiceInterfaceContract(Service topContract, Service bottomContract, Monitor monitor) {
        // Use the interface contract from the bottom level contract if
        // none is specified on the top level contract
        InterfaceContract topInterfaceContract = topContract.getInterfaceContract();
        InterfaceContract bottomInterfaceContract = bottomContract.getInterfaceContract();

        if (topInterfaceContract == null) {
            topContract.setInterfaceContract(bottomInterfaceContract);
        } else if (bottomInterfaceContract != null) {
            // Check that the top and bottom interface contracts are compatible
            boolean isCompatible = true;
            String incompatibilityReason = "";
            try{
                isCompatible = interfaceContractMapper.checkCompatibility(topInterfaceContract, bottomInterfaceContract, Compatibility.SUBSET, false, false);
            } catch (IncompatibleInterfaceContractException ex){
                isCompatible = false;
                incompatibilityReason = ex.getMessage();
            }
            if (!isCompatible) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ServiceInterfaceNotSubSet",
                              topContract.getName(),
                              incompatibilityReason);
            }
        }
    }
    
    /**
     * Interface contract from higher in the implementation hierarchy takes precedence. 
     * When it comes to checking compatibility the top level reference interface is a 
     * superset of the promoted reference interface so treat the promoted
     * (bottom) interface as the source
     * 
     * @param topContract the top contract 
     * @param bottomContract the bottom contract
     */    
    private void calculatePromotedReferenceInterfaceContract(Reference topContract, Reference bottomContract, Monitor monitor) {
        // Use the interface contract from the bottom level contract if
        // none is specified on the top level contract
        InterfaceContract topInterfaceContract = topContract.getInterfaceContract();
        InterfaceContract bottomInterfaceContract = bottomContract.getInterfaceContract();

        if (topInterfaceContract == null) {
            topContract.setInterfaceContract(bottomInterfaceContract);
        } else if (bottomInterfaceContract != null) {
            // Check that the top and bottom interface contracts are compatible
            boolean isCompatible = true;
            String incompatibilityReason = "";
            try{
                isCompatible = interfaceContractMapper.checkCompatibility(bottomInterfaceContract, topInterfaceContract, Compatibility.SUBSET, false, false);
            } catch (IncompatibleInterfaceContractException ex){
                isCompatible = false;
                incompatibilityReason = ex.getMessage();
            }
            if (!isCompatible) {
                Monitor.error(monitor,
                              this,
                              Messages.ASSEMBLY_VALIDATION,
                              "ReferenceInterfaceNotSubSet",
                              topContract.getName(),
                              incompatibilityReason);
            }
        }
    }    

    /**
     * Bindings from higher in the implementation hierarchy take precedence
     * 
     * @param compositeService
     * @param promotedComponentService
     */
    private void calculatePromotedBindings(CompositeService compositeService, ComponentService promotedComponentService) {
        // forward bindings
        if (compositeService.getBindings().isEmpty()) {
            for (Binding binding : promotedComponentService.getBindings()) {
                try {
                    compositeService.getBindings().add((Binding)binding.clone());
                } catch (CloneNotSupportedException ex) {
                    // this binding can't be used in the promoted service
                }
            }
        }

        if (compositeService.getBindings().isEmpty()) {
            createSCABinding(compositeService, null);
        }

        // callback bindings
        if (promotedComponentService.getCallback() != null) {
            if (compositeService.getCallback() != null) {
                compositeService.getCallback().getBindings().clear();
            } else {
                compositeService.setCallback(assemblyFactory.createCallback());
            }

            for (Binding binding : promotedComponentService.getCallback().getBindings()) {
                try {
                    compositeService.getCallback().getBindings().add((Binding)binding.clone());
                } catch (CloneNotSupportedException ex) {
                    // this binding can't be used in the promoted service
                }
            }
        }
    }
    
    private void reconcileReferenceMultiplicity(ComponentType componentType,
                                                Reference compositeReference, 
                                                Reference promotedComponentReference,
                                                Monitor monitor) {
        if (compositeReference.getMultiplicity() != null) {
            if (!isValidMultiplicityOverride(promotedComponentReference.getTargets().size() > 0,
                                             promotedComponentReference.getMultiplicity(), 
                                             compositeReference.getMultiplicity())) {
                Monitor.error(monitor, 
                              this, 
                              Messages.ASSEMBLY_VALIDATION,
                              "CompositeReferenceIncompatibleMultiplicity", 
                              componentType.getURI(), 
                              compositeReference.getName(),
                              promotedComponentReference.getName());
            }
        } else {
            compositeReference.setMultiplicity(promotedComponentReference.getMultiplicity());
        }
    }    
    
    private boolean isValidMultiplicityOverride(boolean componentRefHasTarget,
                                                Multiplicity componentRefMul, 
                                                Multiplicity compositeRefMul) {
        if ((componentRefMul != null) && 
            (compositeRefMul != null) &&
             componentRefMul != compositeRefMul) {
            if (componentRefHasTarget){
                switch (componentRefMul) {
                    case ZERO_ONE:
                        return compositeRefMul == Multiplicity.ZERO_ONE || 
                               compositeRefMul == Multiplicity.ONE_ONE;
                    case ONE_ONE:
                        return compositeRefMul == Multiplicity.ZERO_ONE || 
                               compositeRefMul == Multiplicity.ONE_ONE;                      
                    case ZERO_N:
                        return true;
                    case ONE_N:
                        return true;
                    default:
                        return false;
                }
            } else {
                switch (componentRefMul) {
                    case ZERO_ONE:
                        return compositeRefMul == Multiplicity.ONE_ONE;
                    case ONE_ONE:
                        return compositeRefMul == Multiplicity.ONE_ONE;                      
                    case ZERO_N:
                        return true;
                    case ONE_N:
                        return compositeRefMul == Multiplicity.ONE_ONE || 
                               compositeRefMul == Multiplicity.ONE_N;

                    default:
                        return false;
                }
            }
        } else {
            return true;
        }
    }    

} //end class
