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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.ComponentPreProcessor;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderTmp;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * @version $Rev$ $Date$
 */
public class ComponentBuilderImpl {
    private static final Logger logger = Logger.getLogger(ComponentBuilderImpl.class.getName());
    
    protected static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200903";
    protected static final String BINDING_SCA = "binding.sca";
    protected static final QName BINDING_SCA_QNAME = new QName(SCA11_NS, BINDING_SCA);

    private ComponentTypeBuilderImpl componentTypeBuilder;
    private Monitor monitor;
    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private InterfaceContractMapper interfaceContractMapper;
        
    public ComponentBuilderImpl(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();

        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        
        interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);
    }    
    
    public void setComponentTypeBuilder(ComponentTypeBuilderImpl componentTypeBuilder){
        this.componentTypeBuilder = componentTypeBuilder;
    }

    /**
     * 
     * @param component
     */
    public void configureComponentFromComponentType(Component component){
        
        // do any required pre-processing on the implementation
        // what does this do?
        if (component.getImplementation() instanceof ComponentPreProcessor) {
            ((ComponentPreProcessor)component.getImplementation()).preProcess(component);
        }
        
        // create the component type for this component 
        // taking any nested composites into account
        createComponentType(component);
        
        // services
        configureServices(component);
        
        // references
        //configureReferences(component);
        
        // properties
        //configureProperties(component);
               
    }
       
    /**
     * 
     * @param component
     */
    private void createComponentType(Component component){
        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            componentTypeBuilder.createComponentType((Composite)implementation);
        }
    }
    
    /**
     *
     * @param component
     */
    private void configureServices(Component component){
        
        // If the component type has services that are not described in this
        // component then create services for this component
        addServicesFromComponentType(component);
        
        // Connect this component's services to the 
        // services from its component type
        connectServicesToComponentType(component);
        
        // look at each component service in turn and calculate its 
        // configuration based on OASIS rules
        for (ComponentService componentService : component.getServices()) {
            Service componentTypeService = componentService.getService();

            // interface contracts
            calculateInterfaceContract(componentService,
                                       componentTypeService);
            
            // bindings
            calculateBindings(componentService,
                              componentTypeService);
            
            
            // add callback reference model objects
            createCallbackReference(component,
                                    componentService);
            
            
            // intents - done in CompositePolicyBuilder
            //           discuss with RF
            //calculateIntents(componentService,
            //                 componentTypeService);

            // policy sets - done in CompositePolicyBuilder
            //               discuss with RF
            // calculatePolicySets(componentService,
            //                     componentTypeService);

        }
    }
    
    private void addServicesFromComponentType(Component component){
        
        // Create a component service for each service
        if (component.getImplementation() != null) {
            for (Service service : component.getImplementation().getServices()) {
                ComponentService componentService = 
                    (ComponentService)component.getService(service.getName());
                
                // if the component doesn't have a service with the same name as the 
                // component type service then create one
                if (componentService == null) {
                    componentService = assemblyFactory.createComponentService();
                    componentService.setForCallback(service.isForCallback());
                    String name = service.getName();
                    componentService.setName(name);
                    component.getServices().add(componentService);
                }
            }
        }
    }    
    
    private void connectServicesToComponentType(Component component){
        
        // Connect each component service to the corresponding component type service
        for (ComponentService componentService : component.getServices()) {
            if (componentService.getService() != null || componentService.isForCallback()) {
                continue;
            }
            
            Service service = component.getImplementation().getService(componentService.getName());

            if (service != null) {
                componentService.setService(service);
            } else {
                Monitor.error(monitor, 
                              this, 
                              "assembly-validation-messages", 
                              "ServiceNotFoundForComponentService", 
                              component.getName(),
                              componentService.getName());
            }
        }
    }
       
    /**
     * OASIS RULE: Interface contract from higher in the hierarchy takes precedence
     * 
     * @param componentService the top service 
     * @param componentTypeService the bottom service
     */
    private void calculateInterfaceContract(Service componentService,
                                            Service componentTypeService){
        // Use the interface contract from the higher level service (1) if
        // none is specified on the lower level service (2)
        InterfaceContract componentServiceInterfaceContract = componentService.getInterfaceContract();
        InterfaceContract componentTypeServiceInterfaceContract = componentTypeService.getInterfaceContract();
        
        if (componentServiceInterfaceContract == null) {
            componentService.setInterfaceContract(componentTypeServiceInterfaceContract);
        } else if (componentTypeServiceInterfaceContract != null) {
            // Check that the two interface contracts are compatible
            boolean isCompatible =
                interfaceContractMapper.isCompatible(componentServiceInterfaceContract,
                                                     componentTypeServiceInterfaceContract);
            if (!isCompatible) {
                Monitor.error(monitor, 
                              this, 
                              "assembly-validation-messages", 
                              "ServiceInterfaceNotSubSet", 
                              componentService.getName());
            }
        }         
    }  
    
    /**
     * OASIS RULE: Bindings from higher in the hierarchy take precedence
     * 
     * @param componentService the top service 
     * @param componentTypeService the bottom service
     */    
    private void calculateBindings(Service componentService,
                                   Service componentTypeService){
        // forward bindings
        if (componentService.getBindings().isEmpty()) {
            componentService.getBindings().addAll(componentTypeService.getBindings());
        }
        
        if (componentService.getBindings().isEmpty()) {
            createSCABinding(componentService, null);
        }

        // callback bindings
        if (componentService.getCallback() == null) {
            componentService.setCallback(componentTypeService.getCallback());
            if (componentService.getCallback() == null) {
                // Create an empty callback to avoid null check
                componentService.setCallback(assemblyFactory.createCallback());
            }
        } else if (componentService.getCallback().getBindings().isEmpty() && componentTypeService.getCallback() != null) {
            componentService.getCallback().getBindings().addAll(componentTypeService.getCallback().getBindings());
        }
        
    }
    
    /**
     * Create a callback reference for a component service
     * 
     * @param component
     * @param service
     */
    private void createCallbackReference(Component component, ComponentService service) {
        
        // if the service has a callback interface create a reference
        // to represent the callback 
        if (service.getInterfaceContract() != null && // can be null in unit tests
            service.getInterfaceContract().getCallbackInterface() != null) {
            
            ComponentReference callbackReference = assemblyFactory.createComponentReference();
            callbackReference.setForCallback(true);
            callbackReference.setName(service.getName());
            try {
                InterfaceContract contract = (InterfaceContract)service.getInterfaceContract().clone();
                contract.setInterface(contract.getCallbackInterface());
                contract.setCallbackInterface(null);
                callbackReference.setInterfaceContract(contract);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
            Service implService = service.getService();
            if (implService != null) {
    
                // If the implementation service is a CompositeService, ensure that the Reference that is 
                // created is a CompositeReference, otherwise create a Reference
                Reference implReference;
                if (implService instanceof CompositeService) {
                    CompositeReference implCompReference = assemblyFactory.createCompositeReference();
                    // Set the promoted component from the promoted component of the composite service
                    implCompReference.getPromotedComponents().add(((CompositeService)implService).getPromotedComponent());
                    // Set the promoted service
                    ComponentReference promotedReference = assemblyFactory.createComponentReference();
                    String promotedRefName =
                        ((CompositeService)implService).getPromotedComponent().getName() + "/"
                            + ((CompositeService)implService).getPromotedService().getName();
                    promotedReference.setName(promotedRefName);
                    promotedReference.setUnresolved(true);
                    implCompReference.getPromotedReferences().add(promotedReference);
                    implReference = implCompReference;
                    // Add the composite reference to the composite implementation artifact
                    Implementation implementation = component.getImplementation();
                    if (implementation != null && implementation instanceof Composite) {
                        ((Composite)implementation).getReferences().add(implCompReference);
                    } // end if
                } else {
                    implReference = assemblyFactory.createReference();
                } // end if
                //
    
                implReference.setName(implService.getName());
                try {
                    InterfaceContract implContract = (InterfaceContract)implService.getInterfaceContract().clone();
                    implContract.setInterface(implContract.getCallbackInterface());
                    implContract.setCallbackInterface(null);
                    implReference.setInterfaceContract(implContract);
                } catch (CloneNotSupportedException e) {
                    // will not happen
                }
                callbackReference.setReference(implReference);
            }
            component.getReferences().add(callbackReference);
            
            // Set the bindings of the callback reference
            if (callbackReference.getBindings().isEmpty()) {
                // If there are specific callback bindings set, use them
                if (service.getCallback() != null) {
                    callbackReference.getBindings().addAll(service.getCallback().getBindings());
                } else {
                    // otherwise use the bindings on the forward service
                    callbackReference.getBindings().addAll(service.getBindings());
                } // end if
            } // end if
            service.setCallbackReference(callbackReference);            
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

} //end class
